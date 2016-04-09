package com.carlospaulino.parrot

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

import static com.carlospaulino.parrot.ResourcesSupport.extractResources
import static com.carlospaulino.parrot.ResourcesSupport.getResourcesFiles
import static com.google.common.base.Strings.isNullOrEmpty
import static java.lang.System.currentTimeMillis

class ParrotPlugin implements Plugin<Project> {
    private static final API_KEY_ENV_VAR = "GOOGLE_TRANSLATE_API_KEY"

    @Override
    void apply(Project project) {
        project.extensions.create("parrot", ParrotPluginExtension)

        if (!project.hasProperty("android")) {
            project.logger.error("Project ${project.name} requires the Android Plugin for Parrot to work")
            return
        }

        project.afterEvaluate {
            def androidExtension = project.property("android") as AppExtension

            androidExtension.applicationVariants.all { variant ->

                def variantName = variant.name.capitalize()
                def taskProperties = project.property("parrot") as ParrotPluginExtension

                if (taskProperties.destinationLanguages.size() == 0) {
                    project.logger.warn("Parrot will not perform any translations unless you specify one or more destination languages")
                    return;
                }

                taskProperties.destinationLanguages.each { language ->

                    def outputDirectory = project.file("$project.buildDir/generated/res/rs/${flavorName}/${buildType.name}/values-${language}/");

                    def task = project.task("translateStringResourcesFor${variantName}To${language.toUpperCase()}", type: TranslateTask) {
                        outputDir = outputDirectory
                        flavorName = variant.flavorName
                        buildTypeName = variant.buildType.name
                        destinationLanguage = language
                        sourceLanguage = taskProperties.sourceLanguage
                        resources = extractResources(getResourcesFiles(variant))
                        existingTranslatedResources = extractResources(getResourcesFiles(variant, "-${language}"))
                        cacheBuster = taskProperties.alwaysTranslate ? currentTimeMillis() : 0
                        apiKey = getApiKey(taskProperties)
                    } as TranslateTask

                    variant.registerResGeneratingTask(task, task.outputDir)
                }
            }
        }
    }


    private static getApiKey(ParrotPluginExtension taskProperties) {
        def apiKeyEnvironmentalVariable = System.getenv(API_KEY_ENV_VAR)
        if (!isNullOrEmpty(apiKeyEnvironmentalVariable)) {
            return apiKeyEnvironmentalVariable;
        }

        return taskProperties.apiKey;
    }
}
