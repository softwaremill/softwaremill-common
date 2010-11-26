Most of the modules are usable stand-alone and do not require other modules. Simply include the jar in your
project and you're ready to use it. See the individual module READMEs for mor information.

Jars and sources are available in our Maven repository:

<repository>
    <id>softwaremill-releases</id>
    <name>SoftwareMill Releases</name>
    <url>http://tools.softwaremill.pl/nexus/content/repositories/releases</url>
</repository>

Modules overview
================

1. CDI extensions

* Transaction interceptors
* Stackable security interceptors
* Implementation of assisted inject in Weld (autofactories,
* Object-services (polymorphic extension methods)
* Static bean injection

2. CDI+JSF2 integration

* Transaction phase listeners ("open session in view")
* Security phase listeners
* Navigation handlers
* i18n, messaging, validation utilities

3. Java utilties

4. Selenium+JBoss UI testing utilities

5. Test-with-DB framework using Arquillian