# CDI+JSF2 integration utilities

Maven dependency:

    <dependency>
        <groupId>pl.softwaremill.common</groupId>
        <artifactId>softwaremill-faces</artifactId>
        <version>3</version>
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
