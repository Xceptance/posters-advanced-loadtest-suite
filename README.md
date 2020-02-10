# Performance Test Suite

## Introduction
The performance test suite is the foundation for further NewStore load tests and it is based on [Xceptance LoadTest](https://www.xceptance.com/xlt/). Several features this test suite delivers:

* Handling of authentication (Authorized*HttpRequest)
* Handling of tracing against LightStep (TraceableHttpRequest)
* Support of multi-tenant load testing
* Internal handling of configuration via YAML files including regions, locales, and sites/tenants
* Replayable randomness
* Handling of scaling tests and collecting results as well as building reports
* Fits into CICD including comparison against previous runs
* It is Java and the JVM, hence nearly everything can be done, as long as the measurement infrastructure is not circumvented, threads are used or expensive operations are run that influence the measurement

### Other Features

* Every test cases is a JUnit tests form the technical point of view, hence you can use Eclipse or any other IDE to compile, run, and debug
* These test can run as normal integration test as part of a classic build process
* XLT measures, scales, and paces the testing

## XLT Documentation
XLT is a load testing tool build by Xceptance on the JVM using Java also as the scripting language. Here are some documentation links:

* [Release Notes](https://lab.xceptance.de/releases/xlt/latest/release-notes/index.html)
* [User Manual](https://lab.xceptance.de/releases/xlt/latest/user-manual/index.html)
* [User Forum](https://ask.xceptance.de/)
* [XLT Introduction](https://training.xceptance.com/xlt/05-what-is-xlt.html#/)
* [Some Other Training Material](https://training.xceptance.com/xlt/#/)

## Basics by Example

### Test Case

### Actions

### Requests

## Important 
### private-data.yaml
The commit does not contain a `private-data.yaml` file but a similarly named template. To enable the LightStep tracing, you have to rename the template file to `private-data.yaml` and fill in the token to be used. If this is not set, the suite will complain with an assertion during startup. Important: This file is not part of the GIT commit by design to avoid checking in sensitive data such as tokens and passwords.

## Sites/Tenants, Regions, and Locale
Before we go into the inner workings, here is the high level business view of one of the big differences to the previous SG suite: It is multi-site by default.

A lot of projects nowadays come with several sites, in several languages per site, and often target regions or markets which have similar properties such as credit cards for instance.

In addition, sometimes the site is similar in language but not in functionality, such as UK and US for instance, where the same search phrases can be used (most likely) but the address format is different.

If you want to do things during runtime based on the site and its properties, you can access the current site context and ask:

```java
Context.get().data.site.id
Context.get().data.site.region
Context.get().data.site.locale
Context.get().data.site.language()
```

In case this is too ambiguous, you can easily extend the `Site` class to directly answer your questions, such as isAPAC() for instance. 

The language is automatically derived from the locale. For instance `en_CA` is `en` aka English and `fr_CA` is `fr` aka French both for Canada.

So the idea is to allow reuse based on region and locale/language while you also define everything for the site itself, if you don't want any reuse. But more later.

### File System
You will find a hierarchy under `data` where the suite is looking for information in order of the fallback (see later). This looks something like that (shortened):

```
config/data/
├── languages
│   ├── default
│   │   ├── firstnames.txt
│   │   ├── lastnames.txt
│   │   └── words.txt
│   ├── en
│   │   ├── localization.yaml
│   │   └── searchterms.txt
│   ├── en_UK
│   │   ├── firstnames.txt
│   │   └── lastnames.txt
│   ├── en_US
│   │   └── stateCodesUS.txt
│   ├── fr_FR
│   │   ├── localization.yaml
│   │   └── searchterms.txt
│   └── zh_CN
│       ├── firstnames.txt
│       ├── lastnames.txt
│       ├── localization.yaml
│       └── searchterms.txt
├── regions
│   ├── APAC
│   ├── default
│   │   ├── flash.yaml
│   │   └── site.yaml
│   ├── Europe
│   └── NorthAmerica
└── sites
    ├── sites.yaml
    ├── China
    │   └── accounts.yaml
    ├── default
    │   ├── creditcards.yaml
    │   └── filters.yaml
    ├── France
    │   └── accounts.yaml
    ├── Italy
    │   └── accounts.yaml
    ├── Japan
    │   └── accounts.yaml
    ├── UK
    │   └── accounts.yaml
    └── US
        ├── accounts.yaml
        └── flash.yaml
```

So we have regions, locales and languages, and sites. Everything except default is free to be defined as needed. This comes from information set in `data/sites/sites.yaml`. Consider this the start point of everything. This file is referenced in the `project.properties` via `general.properties.yaml.global.files` and basically gets the YAML loading started.

You can specify more files there which are loaded as initial defaults before the fallback based handling starts.

But it is important that one file of this contains the site list to use otherwise the site concept is broken. 

```yaml
 # make all sites known upfront       
sites:
    -   id: US
        active: true # in case you want to exclude it from a run 
        locale: en_US # this is the language to be used
        region: NorthAmerica # this is something like country or market
        marketshare: 10
    -   id: UK
        active: false
        locale: en_UK
        region: Europe
        marketshare: 10
```

## Properties
This test suite uses a new concept to deal with properties and test data. Please read the following paragraphs carefully and also keep an eye on everything marked important.

### General
The suite uses in part known properties but tries to replace most of it (might not be completely done yet) by introducing a flexible and easy to read and configure YAML based property extension.

This concept has been setup for two reasons:
* avoid boilerplate and support UTF-8
* permit simple overwrite by site, market, or region

### YAML
The YAML properties file are an extension of the existing XLT concept but are not delivered by XLT(!) but by the test suite. Hence some limitations apply. 

**Advantages**
* YAML supports UTF-8
* YAML supports classic comments with #
* YAML avoids redundancy because you specify things by indention
* YAML allows [references](https://en.wikipedia.org/wiki/YAML#Advanced_components) aka you can reuse pieces
* Less boilerplate compared to JSON and easier editable

**Disadvantages**
* Needs spaces not tabs for indentation, it is a little sensitive here 

*Important:* YAML does not like `TAB` at all. Make sure you modify your editor settings to correctly turn a TAB into spaces. YAML also likes indentions because only that make up the struture of a document. 

More about YAML and some easy rules can be found in the [Wikipedia](https://en.wikipedia.org/wiki/YAML).

### Properties Format in YAML
The test suite transforms the YAML properties under the hood into regular properties that XLT and the suite can handle, so only the storage is YAML, not the runtime behavior. Hence what you know about `Configuration.java` or `XltProperties` still applies.

Here are some examples:

**Plain**
```yaml
general:
    host: host.com
    baseUrl: justanurl
    credentials: # Comment here
        username: storefront # Comment there
        password: foobar
```
This translates into:

```
general.host = host.com
general.host.baseUrl = host.com
general.credentials.username = storefront
general.credentials.password = foobar
```


** Enumerations**

```yaml
creditcards:
    - id: Mastercard
      type: Master Card
    - id: Visa
      type: Visa
```

This translates into:

```
creditcards.0.id = Mastercard
creditcards.0.type = Master Card
creditcards.1.id = Visa
creditcards.1.type = Visa
```

*Important:* YAML handles quotes around values automatically. So if you write `foo="bar"` you will get just `bar` as value. If you need the quotes, you have to do `foo="\"bar\""`. Same if you have a single quote or backslash somewhere in the value. 

### Private Data
There is a `sites/private-data.yaml` file that is listed in `.gitignore` purposely to avoid leaking data into the repo. There is a template `sites/private-data.yaml.properties` that is meant to be the starter to copy your local `sites/private-data.yaml` from. When you have a project, you can commit private-data.yaml because this data is key in that case.

## Data
Data has been setup in a similar way to properties to permit easy reuse as well as well targeted setup per region, site, or locale/language. Good examples are search phrases for English sites

## Fallback
For data as well as properties, we use the same fallback logic and look for the file or the property until we find it. If we cannot find it, we complain.

1. from the site first (e.g. US)
2. if not found from default site,
3. if not found from region,
4. if not found from default region,
5. if not found from locale (e.g. en_US, as specified for the site)
6. if not found from language (e.g. en taken from en_US)
7. if not found from default language
8. if not found, we fail! No file no fun!

Empty file are permitted in case you specified something that does not have data in that case.

## Suite vs. XLT
Because the new normal is the loading of a lot of properties from YAML file to allow easier editing, property reuse as well as get UTF-8 support easily, it does not correspond fully with the XLT concept of properties.

*Important:* Right now, the classic dev.properties overwrite does not work anymore due to the YAML properties being applied last. This also applies to test.properites which is not longer loaded on top of everything during a load test, rather before the YAML pieces, hence it cannot overwrite what is defined in any YAML file.
 
*Important:* Because XLT sees the YAML file as data files and hence does not back this up as part of a load test run. So a recreation based on settings for a run cannot be done from the reports archive anymore.

## Data Concept
The suite tries to employ a stricter concept of data separation as well as data maintenance. Hence we have new means to load data from external sources as well as keep data during runtime.

### Loading
Due to the new data file concept, the standard loading concept of XLT cannot longer be used because it does not know anything about site, regions, and locales. 

The new `DataFileProvider.java` has the responsibility to load files based on the fallback and the context. 

```java
public static Optional<File> dataFileBySite(final Site site, final String fileName);
```

### Access and Storage per User
The Context got a new sub-level to keep the test data separated from configuration as well as make extending it easier. `Context.data` which is an instance of `TestData.java`. This instance is exclusive per running scenario. 

Most data is made public for easier access and less boilerplate. If you need logic to hand out data, you have to add a method for fetching and it should be private again of course. 

For instance you will find the `Site` located here, because this is a per user/scenario state.

If you need something only during development time, make sure you add this to `debugData` and not `data`.

`DebugData` features an internal implementation that carries data, called  `DevelopmentDebugData` where you should put all your data in. DebugData always returns something non-sense or static, while DevelopmentDebugData does the right thing. This automatically handles the `isLoadTest` check for you. See the current implementation for more details.
