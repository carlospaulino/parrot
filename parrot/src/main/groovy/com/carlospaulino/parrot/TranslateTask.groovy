package com.carlospaulino.parrot

import com.google.api.services.translate.Translate
import com.google.api.services.translate.TranslateRequestInitializer
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

import static com.carlospaulino.parrot.ResourcesSupport.analyzeResources
import static com.carlospaulino.parrot.ResourcesSupport.generateTranslatedResourceXml
import static com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport
import static com.google.api.client.json.jackson2.JacksonFactory.defaultInstance
import static groovy.json.internal.Charsets.UTF_8

class TranslateTask extends DefaultTask {
    private static final APPLICATION_NAME = "com.carlospaulino.parrot";

    @OutputDirectory
    File outputDir

    @Input
    String sourceLanguage

    @Input
    String destinationLanguage

    @Input
    String flavorName

    @Input
    String buildTypeName

    @Input
    Map resources

    @Input
    Map existingTranslatedResources

    @Input
    String apiKey

    @Input
    long cacheBuster

    @TaskAction
    void beginTranslation() {

        // Cache resources per lang/flavor/buildType
        def cacheFolder = project.file("$project.buildDir/parrot/${destinationLanguage}-${flavorName}${buildTypeName}/")
        def cacheFile = new File(project.file(cacheFolder), "cache.json")
        def cachedStringResources = extractCachedResources(cacheFile)

        def analyzedResources = analyzeResources(resources, cachedStringResources, existingTranslatedResources)

        def changedResources = analyzedResources.changedResources

        def unchangedResources = analyzedResources.unchangedResources

        def mergedResources = translate(changedResources) + unchangedResources

        writeTranslatedResourceXml(generateTranslatedResourceXml(mergedResources), outputDir)

        prepareDestination(cacheFolder)

        cacheResources(cacheFile, mergedResources)
    }

    private Map translate(Map changedResources) {
        def translator = new Translate.Builder(newTrustedTransport(), getDefaultInstance(), null)
                .setApplicationName(APPLICATION_NAME)
                .setGoogleClientRequestInitializer(new TranslateRequestInitializer(apiKey))
                .build();

        def words = []

        changedResources.each {
            words.add(it.value.originalText)
        }

        def translationResult = translator
                .translations()
                .list(words, destinationLanguage)
                .setSource(sourceLanguage)
                .execute()

        changedResources.eachWithIndex { entry, int i ->
            // The api returns the translations in the same order they are provided
            entry.value.translatedText = (translationResult.translations as List)[i].translatedText
        }

        return changedResources
    }


    private static writeTranslatedResourceXml(String xml, File outputDir) {
        new File(outputDir, "translated-strings.xml").write(xml, UTF_8.name())
    }

    private static cacheResources(File target, Map content) {
        target.write(JsonOutput.toJson(content), UTF_8.name())
    }

    private static prepareDestination(File target) {
        if (target.exists()) {
            target.deleteDir()
        }
        target.mkdirs()
    }

    private static Map extractCachedResources(File cacheFile) {
        return (cacheFile.exists() ? new JsonSlurper().parse(cacheFile) : [:]) as Map
    }
}
