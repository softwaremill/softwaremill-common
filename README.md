# SoftwareMill Common project

Most of the modules are usable stand-alone and do not require other modules. Simply include the jar in your
project and you're ready to use it. See the individual module READMEs for mor information.

Jars and sources are available in our Maven repository:

    <repository>
        <id>softwaremill-releases</id>
        <name>SoftwareMill Releases</name>
        <url>http://tools.softwaremill.pl/nexus/content/repositories/releases</url>
    </repository>

# Modules overview

## [CDI extensions](/softwaremill/softwaremill-common/tree/master/softwaremill-cdi/)

* Transaction interceptors
* Stackable security interceptors
* Implementation of assisted inject in Weld (autofactories)
* Object-services (polymorphic extension methods)
* Static bean injection

## [CDI+JSF2 integration utilities](/softwaremill/softwaremill-common/tree/master/softwaremill-faces/)

* Transaction phase listeners ("open session in view")
* Security phase listeners
* Navigation handlers
* i18n, messaging, validation utilities

## [Selenium+JBoss UI testing utilities](/softwaremill/softwaremill-common/tree/master/softwaremill-ui-test/)

## [Test-with-DB framework using Arquillian](/softwaremill/softwaremill-common/tree/master/softwaremill-db-test/)

## [Configuration reader](/softwaremill/softwaremill-common/tree/master/softwaremill-conf/)

Reads key-value configuration files either from JBoss's conf directory (which have priority) or from the classpath. 

## [Amazon AWS Utilities](/softwaremill/softwaremill-common/tree/master/softwaremill-sqs/)

## [Softwaremill Parent](/softwaremill/softwaremill-common/tree/master/softwaremill-parent/)

Our BOM.

## [Testing utilities](/softwaremill/softwaremill-common/tree/master/softwaremill-testing/)

## [Java utilities](/softwaremill/softwaremill-common/tree/master/softwaremill-util/)

---

Licensed under the Apache2 license. [Softwaremill 2010](http://softwaremill.eu/).