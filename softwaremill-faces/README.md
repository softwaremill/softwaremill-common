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
