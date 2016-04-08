package com.carlospaulino.parrot

import com.android.build.gradle.api.ApplicationVariant
import groovy.xml.MarkupBuilder

import static com.carlospaulino.parrot.ResourcesSupport.ResourceType.STRING
import static groovy.io.FileType.FILES

class ResourcesSupport {
    static final ENCLOSING_TAG = "resources"

    static Map extractResources(List<File> files, ResourceType resourceType = STRING) {

        Map map = [:]
        files.each { file ->
            def parsedXml = new XmlParser().parse(file)
            if (ENCLOSING_TAG.equals(parsedXml.name()) && parsedXml.value() instanceof NodeList) {
                parsedXml.children().each { resource ->
                    if (resourceType.name().equalsIgnoreCase(resource.name())) {
                        map.put(resource.attributes().name, resource.text())
                    }
                }
            }
        }

        return map
    }

    static List<File> getResourcesFiles(ApplicationVariant variant, language = "") {
        def files = []
        variant.sourceSets.each { sourceSet ->
            sourceSet.resDirectories.each { resDir ->

                if (!resDir.exists()) {
                    return
                }

                resDir.eachFileRecurse(FILES) { file ->
                    if (file.exists() && file.absolutePath.matches(".*res/values${language}/.*xml")) {
                        files.add(file)
                    }
                }
            }
        }

        return files;
    }

    static generateTranslatedResourceXml(values) {
        def writer = new StringWriter()
        def markupBuilder = new MarkupBuilder(writer);
        markupBuilder.doubleQuotes = true

        markupBuilder.resources() {
            values.each {
                string(name: it.key, it.value.translatedText)
            }
        }

        return writer.toString()
    }

    static enum ResourceType {
        STRING,
        COLOR,
        DIMEN,
    }

}
