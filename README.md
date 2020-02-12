# Performance Test Suite for Posters Demo Shop

## Introduction
The test suite is the foundation for load tests with [Xceptance LoadTest](https://www.xceptance.com/xlt/). Several features this load test suite demonstrates:

* Every test case is a JUnit test. Use Eclipse or any other IDE to compile, run (as single test user), and debug.
* The tests can run as a normal integration test or as part of a classic build process.
* XLT measures, scales, and paces the testing.
* Internal handling of configuration via YAML files including regions, locales, and sites.
* Handling of test scaling, results collection and report building by XLT.
* Fits your CI/CD pipeline, including comparison against previous runs.
* Replayable randomness of the test scenarios.
* It is Java and the JVM. Nearly everything can be done, as long as the measurement infrastructure is not circumvented, threads are used or expensive operations are run that influence the measurement.
* The test suite is ready to execute against Xceptance' demo webshop 'Posters'.

## XLT Documentation
XLT is a load testing tool build by Xceptance on the JVM using Java also as the scripting language. Here are some documentation links:

* [Release Notes](https://lab.xceptance.de/releases/xlt/latest/release-notes/index.html)
* [User Manual](https://lab.xceptance.de/releases/xlt/latest/user-manual/index.html)
* [User Forum](https://ask.xceptance.de/)
* [XLT Introduction](https://training.xceptance.com/xlt/05-what-is-xlt.html#/)
* [Some Other Training Material](https://training.xceptance.com/xlt/#/)

## Test Suite Overview

...TODO...

## Sites, Regions, and Locale

Nowadays a lot of sites come in several languages and often target different regions or markets. Data will be different for some sites (e.g. addresses) and others might share certain information, e.g. search phrases.
The test suite is multi-site by design even so it is only working on one demo shop. Data can be site, language or region specific and the test scripts will have a way to determine which site they are working on.
If you want to do things during runtime based on the site and its properties, you can access the current site context and ask:

```java
Context.get().data.site.id
Context.get().data.site.region
Context.get().data.site.locale
Context.get().data.site.language()
```

In case this is too ambiguous, you can easily extend the `Site` class to directly answer your questions, such as isAPAC() for instance. 

The language is automatically derived from the locale. For instance `en_CA` is `en` aka English and `fr_CA` is `fr` aka French both for Canada.

So the idea is to allow reuse based on region and locale/language, while you separate everything else that is site specific.

### File System
You will find a hierarchy under `data` where the suite is looking for information in order of the fallback (see later). This can look something like the following hierarchy:

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

There are regions, locales and languages as well as sites. Everything except default is free to be defined as needed. This hierarchy comes from information set in `data/sites/sites.yaml`. Consider it the start point of the site definitions. This file is referenced in the `project.properties` via `general.properties.yaml.global.files` and basically gets the YAML loading started.

You can specify more files there which are loaded as initial defaults before the fallback based handling starts.

But it is important that one file of this contains the site list to use (example below), otherwise the site concept is broken. 

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
The test suite deals with different configurations (properties) and test data. These are explained in the following sections.

### General
The suite uses in part known properties but tries to replace most of it by introducing a flexible and easy to read and configure YAML based property extension.

This concept has been setup for two reasons:
* Avoid boilerplate and support UTF-8
* Permit simple overwrite by site, market, or region

### YAML
The YAML property files are an extension of the existing XLT property concept but are not delivered by XLT but the test suite.

*Important:* YAML does not like `TAB` at all. Make sure you modify your editor settings to correctly turn a TAB into spaces. YAML requires indentions to structure the document.

More about YAML and some easy rules can be found in the [Wikipedia](https://en.wikipedia.org/wiki/YAML).

### Properties Format in YAML
The test suite transforms the YAML properties into regular properties which XLT and the suite can handle, so only the definition is in YAML but not the runtime behavior.

An example:

**Plain**
```yaml
general:
    host: host.com
    baseUrl: ${host}/my-site
    credentials: # Comment here
        username: storefront # Comment there
        password: foobar
```
Which translates into:

```
general.host = host.com
general.host.baseUrl = host.com/my-site
general.credentials.username = storefront
general.credentials.password = foobar
```

### Private Data
There is a `sites/private-data.yaml` which defines access and credentials for the site under test. If required, this file could be excluded from your repository (via .gitignore).
The Posters test suite contains a readily setup `private-data.yaml` file with access details for Xceptance's demo store site 'Posters'.

## Data
Data has been setup in a similar way to properties to permit easy reuse as well as well targeted setup per region, site, or locale/language. Good examples are search phrases for English sites.

## Fallback
Data and property look up use the same fallback logic. The look up follows below steps until the property is found. If not the test suite will complain with an error.

1. Look for item in site first (e.g. US).
2. Look in default site.
3. Look in region.
4. Look in default region.
5. Look in locale (e.g. en_US, as specified for the site).
6. Look in language (e.g. en taken from en_US).
7. Look in default language.
8. If not found, the look up fails.

Empty file are permitted in case you specified something that does not have data in that case.

## Data Concept
The suite tries to employ a strict concept of data separation and data maintenance.

### Loading
The class `DataFileProvider.java` has the responsibility to load files based on earlier described fallback pattern and the site context. 

```java
public static Optional<File> dataFileBySite(final Site site, final String fileName);
```

### Access and Storage per User
The test suite contains the `Context` object which holds (among others) test suite/scenario configuration and data as well as runtime data of the test scenario.
`Context.data` is an instance of `TestData.java`. This instance is exclusive per running scenario.
Most data is made public for easier access and less boilerplate. If you need logic to hand out data, you have to add a methods for fetching the data.
One example contained in the `TestData` (`Context.data`) is the `Site` object.