# Softwaremill-common

Most of the modules are usable stand-alone and do not require other modules. Simply include the jar in your
project and you're ready to use it. See the individual module READMEs for mor information.

Jars and sources are available in our Maven repository:

    <repository>
        <id>softwaremill-releases</id>
        <name>SoftwareMill Releases</name>
        <url>http://tools.softwaremill.pl/nexus/content/repositories/releases</url>
    </repository>

# Modules overview

## CDI extensions

* Transaction interceptors
* Stackable security interceptors
* Implementation of assisted inject in Weld (autofactories,
* Object-services (polymorphic extension methods)
* Static bean injection

## CDI+JSF2 integration utilities

* Transaction phase listeners ("open session in view")
* Security phase listeners
* Navigation handlers
* i18n, messaging, validation utilities

## Java utilties

## Selenium+JBoss UI testing utilities

## Test-with-DB framework using Arquillian

---

Licensed under the Apache2 license. [Softwaremill 2010](http://softwaremill.eu/).