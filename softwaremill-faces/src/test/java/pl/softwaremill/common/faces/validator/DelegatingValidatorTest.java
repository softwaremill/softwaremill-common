package pl.softwaremill.common.faces.validator;

import org.testng.annotations.Test;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.fail;

public class DelegatingValidatorTest {

    private static final String VALIDATOR_ID = "mockValidator";

    private Application app;
    private UIComponent component;
    private FacesContext context;

    @Test
    public void shouldCreateValidatorAndNotThrowException() throws Exception {
        // given
        DelegatingValidator validator = new DelegatingValidator();
        MockValidator mockValidator = prepareMocks(false);
        Object value = "123 33 23";

        // when
        try {
            validator.validate(context, component, value);
        } catch (ValidatorException e) {
            fail("Validation should pass!", e);
        }

        // then
        verify(context).getApplication();
        verify(app).createValidator(VALIDATOR_ID);
        assertThat(value).isEqualTo(mockValidator.getValue());
    }

    @Test
    public void shouldCreateValidatorAndThrowException() throws Exception {
        // given
        DelegatingValidator validator = new DelegatingValidator();
        MockValidator mockValidator = prepareMocks(true);
        Object value = "123 33 23";


        // when
        try {
            validator.validate(context, component, value);
            fail("Validation shouldn't pass!");
        } catch (ValidatorException expected) {
        }

        // then
        verify(context).getApplication();
        verify(app).createValidator(VALIDATOR_ID);
        assertThat(value).isEqualTo(mockValidator.getValue());
    }

    private MockValidator prepareMocks(boolean throwException) {
        context = mock(FacesContext.class);
        app = mock(Application.class);
        MockValidator validator = new MockValidator(throwException);
        when(app.createValidator(anyString())).thenReturn(validator);
        when(context.getApplication()).thenReturn(app);

        component = new UIInput();
        component.getAttributes().put("validatorId", VALIDATOR_ID);
        return validator;
    }

}

class MockValidator implements Validator {

    private boolean throwException;
    private Object value;

    public MockValidator(boolean throwException) {
        this.throwException = throwException;
    }

    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object value) throws ValidatorException {
        this.value = value;
        if (throwException) {
            throw new ValidatorException(new FacesMessage("An error occurred!"));
        }
    }

    public Object getValue() {
        return value;
    }
}
