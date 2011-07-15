# CDI+JSF2 integration utilities

Maven dependency:

    <dependency>
        <groupId>pl.softwaremill.common</groupId>
        <artifactId>softwaremill-faces</artifactId>
        <version>8</version>
    </dependency>

## Current locale holder

Stores the selected locale in a client cookie, for a month. To change the locale, use the
CurrentLocale.setCurrentLocale method.

To enable, add to `faces-config.xml`:

    <application>
        <view-handler>pl.softwaremill.common.faces.i18n.CurrentLocaleViewHandler</view-handler>
    </application>

## Transaction JSF phase listeners

The phase listener starts two transactions: one for postbacks, which lasts until after the invoke application phase,
and a second one for rendering the response.

To enable, add to `faces-config.xml`:

    <lifecycle>
        <phase-listener>pl.softwaremill.common.faces.transaction.TransactionPhaseListener</phase-listener>
    </lifecycle>

## Fields equal validator

A validator for checking that the content of two fields is equal.

    <h:inputSecret id="password" value="#{password}" />

    <h:inputSecret id="confirmPassword" value="#{confirmPassword}">
        <f:validator validatorId="fieldsEqual" />
        <f:attribute name="fieldsEqualCompareTo" value="password" />
        <f:attribute name="fieldsEqualMessageKey" value="passwords don't match" />
    </h:inputSecret>

To enable, add to `faces-config.xml`:

    <validator>
        <validator-id>fieldsEqual</validator-id>
        <validator-class>pl.softwaremill.common.faces.validator.FieldsEqualValidator</validator-class>
    </validator>

## Faces messages

A component for enqueing faces messages, which will survive redirects. Use:

    @Inject
    private FacesMessages facesMessages;

To enable, add to `faces-config.xml`:

    <application>
        <system-event-listener>
            <system-event-class>javax.faces.event.PreRenderViewEvent</system-event-class>
            <system-event-listener-class>pl.softwaremill.common.faces.messages.FacesMessagesListener</system-event-listener-class>
        </system-event-listener>
    </application>

## Redirecting to an error page in case of a missing required view parameter

If there's a required view parameter, which is missing, JSF only gives the possibility to add a faces message. With the
listener, the user will be redirected to an error page, if that's the case, and the message will also be enqueued.
The error page is specified by the navigation component (see 11).

Important! To mark a view parameter as required, specify the requiredMessage attribute. Leave out the required
attribute, as it interferes with `<f:ajax>` (don't ask me why ... ;) ).

To enable, add to `faces-config.xml`:

    <lifecycle>
        <phase-listener>pl.softwaremill.common.faces.navigation.RequiredViewParameterPhaseListener</phase-listener>
    </lifecycle>

## Restricting pages to logged in users only

There must be a bean implementing the LoginBean interface; the bean controls if there's a logged in user.
Any pages, defined in the navigation (see above) by the page builder to require login, will be redirected to the
login page if a user isn't logged in.

To enable, add to `faces-config.xml`:

    <lifecycle>
        <phase-listener>pl.softwaremill.common.faces.security.SecurityPhaseListener</phase-listener>
    </lifecycle>

## Navigation

Extend the `NavBase` to create a "nav" component and define any pages that you use the following way, using the PageBuilder:

    private final Page page1 = new ViewIdPageBuilder("/page1.xhtml").setRequiresLogin(true).b();
    private final Page page1 = new ViewIdPageBuilder("/admin.xhtml").setRequiresLogin(true).setSecurityEL("#{currentUser.isAdmin)").b();
    private final Page login = new ViewIdPageBuilder("/login.xhtml").b();
    ...

And define a getter for each page.

You can then use the component either to return results of action methods or to create links:

    <h:link outcome="#{nav.page1.s}">Page 1</h:link>

    public String someAction() {
        ...
        return nav.getPage1().includeViewParams().redirect().s();
    }

You must define a login page. This works in conjuction with restricting pages to logged in users only.

If you want to add extra security on the page, set the security EL using the page builder. It has to resolve to Boolean.class.
If the expression returns false user will get 403 Forbidden.

## Cross-field validation

Enclose `UIInput` components for cross-field validation in `multiValidator` tag. Attach validator to this tag.

    <html xmlns="http://www.w3.org/1999/xhtml"
          xmlns:h="http://java.sun.com/jsf/html"
          xmlns:v="http://pl.softwaremill.common.faces/components">

      <v:multiValidator id="atLeastOne" validator="#{bean.validateAtLeastOne}">
        <h:selectBooleanCheckbox value="#{debtorBean.check1}"/>
        <h:selectBooleanCheckbox value="#{debtorBean.check2}"/>
      </v:multiValidator>
      <h:message for="atLeastOne" />

Value that goes to validator is `List` of submitted values of every `UIInput` component enclosed in `multiValidator`.
    
    public void validateAtLeastOne(FacesContext context, UIComponent component, Object value) {
        //value is List of two Booleans
        List<Object> values = (List<Object>) value;
        boolean one = (Boolean) values.get(0);
        boolean two = (Boolean) values.get(1);

        if (! (one || two)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "At least one has to be checked", null);
            throw new ValidatorException(msg);
        }
    }

If validation doesn't pass, message appears in `<h:message for="atLeastOne" />`

## Delegating validator

It can be used when you want to specify validator ID base on runtime value (eg. from bean passed to a view). All validators are
resolved during JSF compile time, so you cannot just do it like this: `<f:validator validatorId="#{bean.validatorId}"/>` - it simple
doesn't work. Instead, you can use `DelegatingValidator` to do the work, see an example below:

    <h:inputText value="#{pageBean.userName}" id="userName">
        <f:attribute name="validatorId" value="#{pageBean.validatorId}"/>
        <f:validator validatorId="delegatingValidator"/>
    </h:inputText>

As you can see, you must specify two things, the validator itself and an attribute named `validatorId` with valid validator ID.
The expression `#{pageBean.validatorId}` must return a valid validator ID - the validator must be registered with `faces-config.xml`
or by `@FacesValidator` annotation.

## OnSubmit validator

When calling ajax model updates you sometimes want to skip the validation phase, but update the model, so immediate=true is not an option.

This validator wrapper can help.

All fields need to use f:onSubmitValidator. You can pass validatorId in an attribute (look at Delegating Validator)
and the OnSubmit validator will call that specified validator.

Optionally if you provide

    <f:param name="required" value="true"/>

The validator will check the value to be not empty.

Then on the SUBMIT button you need to add

    <f:param name="performValidation" value="true"/>

This will trigger the validation when clicking this SUBMIT button.

And voila! now the validation will be called only when SUBMIT is clicked, and you can freely perform as many ajax model
updates as you want.

##File upload

For file upload to work, there is jetty `MultipartFilter` necessary, which
retrieves files from multipart request and adds them as request attributes.

Dependency for jetty:

    <dependency>
        <groupId>org.mortbay.jetty</groupId>
        <artifactId>jetty-util</artifactId>
        <version>6.1.16</version>
    </dependency>

It is safe to map filter to "/*" url pattern in web.xml:

    <filter>
        <filter-name>MultipartRequestFilter</filter-name>
        <filter-class>org.mortbay.servlet.MultiPartFilter</filter-class>
        <init-param>
            <param-name>deleteFiles</param-name>
            <param-value>false</param-value>
        </init-param>
        <init-param>
            <param-name>fileOutputBuffer</param-name>
            <param-value>0</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>MultipartRequestFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

Parameters are optional. Parameter values above are default values. Files are stored in
directory set as ServletContext attribute "javax.servlet.context.tempdir". On JBoss it is
<jboss-home>/server/<server>/work

If `deleteFiles` is set to true, files are deleted after request is handled.


The simpliest form with file upload:
 
    <form enctype="multipart/form-data">
      <sml:fileUpload value="#{bean.file}" />
      <h:commandButton action="#{bean.submit}" />
    </form>

It is also necessary to add namespace:

    xmlns:sml="http://pl.softwaremill.common.faces/components"

`file` property of the `bean` is of type java.io.File. `sml:fileUpload` renders to &lt;input type="file"&gt;. After form submit, file is in the bean.