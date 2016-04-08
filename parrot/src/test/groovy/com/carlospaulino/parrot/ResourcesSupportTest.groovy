package com.carlospaulino.parrot

import org.junit.Test

import static com.carlospaulino.parrot.ResourcesSupport.*
import static org.fest.assertions.Assertions.assertThat
import static org.fest.assertions.MapAssert.entry

class ResourcesSupportTest {
    private static final RESOURCES_PATH = './src/test/resources/'

    @Test
    void testExtractingResourcesFromXml() {
        def variant = [sourceSets: [[resDirectories: [new File(RESOURCES_PATH)]]]]

        List<File> files = getResourcesFiles(variant)

        def resources = extractResources(files)

        assertThat(resources)
                .hasSize(4)
                .includes(entry('secondary', 'Secondary'), entry('app_name', 'My Application'), entry('plugin_name', 'Parrot'), entry('what_is', 'What is my name'))
    }

    @Test
    void testFindingResourceFilesForSpecificLanguage() {
        def variant = [sourceSets: [[resDirectories: [new File(RESOURCES_PATH)]]]]

        def files = getResourcesFiles(variant, "-es")

        assertThat(files)
                .hasSize(1)
                .contains(new File(RESOURCES_PATH, "res/values-es/spanish-override.xml"))
    }

    @Test
    void testFindingResourceFilesForNoSpecificLanguage() {
        def variant = [sourceSets: [[resDirectories: [new File(RESOURCES_PATH)]]]]

        List<File> files = getResourcesFiles(variant)

        assertThat(files)
                .hasSize(2)
                .contains(new File(RESOURCES_PATH, "res/values/strings.xml"), new File(RESOURCES_PATH, "res/values/strings-secondary.xml")
        )
    }

    @Test
    void testAnalyzeResourcesIgnoresAlreadyLocalizedResources() {
        def variant = [sourceSets: [[resDirectories: [new File(RESOURCES_PATH)]]]]

        def existingTranslatedResources = extractResources(getResourcesFiles(variant, "-es"))
        def resources = extractResources(getResourcesFiles(variant))
        def analyzedResources = analyzeResources(resources, [:], existingTranslatedResources)

        def changedResources = analyzedResources.changedResources
        def unchangedResources = analyzedResources.unchangedResources

        assertThat(unchangedResources).hasSize(0) // Because the cache is empty
        assertThat(changedResources)
                .hasSize(3) // app_name is ignored because it's already translated
                .includes(entry('secondary', [originalText: 'Secondary']), entry('plugin_name', [originalText: 'Parrot']), entry('what_is', [originalText: 'What is my name']))
    }

}
