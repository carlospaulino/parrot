Parrot
======

Parrot is a gradle plugin that allows your app to speak multiple languages, without the hassle of working with multiple resources, using the translation editor or ordering an expensive translation from AS/IDEA

It leverages the Google Translate API in order to translate your string resources into all the languages specified in the configuration.

Installation
------------

Get a Google Cloud account and get your Translate Api Key.

`
https://cloud.google.com/translate/
`

Add the following to your `build.gradle`:

```gradle
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.carlospaulino:parrot:0.1.0'
    }
}

// Apply below the Android Application Plugin (com.android.application)
apply plugin: 'com.carlospaulino.parrot'
```

Parrot configuration must be done in the `parrot` closure:

```gradle
parrot {

    // Default Google Translate Api Key.
    // You can also set the environmental variable GOOGLE_TRANSLATE_API_KEY with the key
    apiKey = "MY_API_KEY"

    // Language Parrot is translation from
    sourceLanguage = "en"

    // Languages Parrot is translating to
    destinationLanguages = [ "es", "fr", "de" ]

    // Ignore cache and always perform translations
    alwaysTranslate = false
}
```

ProTips
-------

If you think the translation that was auto generated is not accurate enough you can just override it, by setting the resources in the correct language folder. For example if you are translating to Spanish just create the file: `values-es/strings.xml` and add the resource. Parrot will skip that resource and not perform the translation.
```
<resources>
    <string name="app_name">Mi Aplicación es la mejor</string>
</resources>
````

You can also view the translated resources before they are merged by looking in your build folder.
`{appModule}/build/generated/res/rs/{flavor}/{buildType}/values-{lang}/translated-strings.xml`

Finally
-------
Keep in mind that you have to comply with Google's Attribution Requirements. More here: https://cloud.google.com/translate/v2/attribution


License
--------

    Copyright 2016 Carlos Paulino

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.