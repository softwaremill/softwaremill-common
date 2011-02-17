package pl.softwaremill.common.test.web.email;

import com.dumbster.smtp.SmtpMessage;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author maciek
 */
public class EmailMessageTest {

    @Test(dataProvider = "getMessages")
	public void shouldFindLinksInEmail(String message, String... expectedLinks) {
        // Given
        EmailMessage email = new EmailMessage(getMockEmail(message));

		// When
		List<String> links = email.getLinksInMessage();

		// Then
		assertThat(links).isNotNull();
        assertThat(links.size()).isEqualTo(expectedLinks.length);
		assertThat(links).containsExactly(expectedLinks);
	}

    @Test(dataProvider = "getMessagesWithSpecialLink")
	public void shouldFindSpecialLinkInEmail(String message, String urlPart, String expectedLink) {
        // Given
        EmailMessage email = new EmailMessage(getMockEmail(message));

		// When
		String link = email.getLinkLike(urlPart);

		// Then
		assertThat(link).isEqualTo(expectedLink);
	}

    @Test(dataProvider = "getMessagesWithSpecialLinks")
	public void shouldFindSpecialLinksInEmail(String message, String urlPart, String... expectedLinks) {
        // Given
        EmailMessage email = new EmailMessage(getMockEmail(message));

		// When
		List<String> links = email.getLinksLike(urlPart);

		// Then
		assertThat(links).isNotNull();
        assertThat(links.size()).isEqualTo(expectedLinks.length);
		assertThat(links).containsExactly(expectedLinks);
	}

    private final String link1 = "http://www.example.org";
    private final String link2 = "https://www.example.org";
    private final String link3 = "http://www.example.org/data/project-x";
    private final String link4 = "http://www.example.org?id=12";
    private final String link5 = "http://www.example.org?id=12&type=3&cat=9";
    private final String link6 = "http://www.example.org?id=12&type=new-version#top";

    private final String urlPart3 = "data/project-x";
    private final String urlPart6 = "type=new-version";

    private final String urlPart456 = "id=12";


    private final String message1 = "Welcome User,\n Visit our site at {link} for great deals!";
    private final String message2 = "Welcome User,\n To activate your account follow link {link1}. For help see our site at {link2}!";

	@DataProvider
	private  Object[][] getMessagesWithSpecialLink(){
        return new Object[][] {
			// message,	url part, expected link
	        { getSimpleMessage(link1), "mill.pl",  null },
            { getSimpleMessage(link3), urlPart3, link3 },
            { getSimpleMessage(link6), urlPart6, link6 },
            { getAdvancedMessage(link5, link6), urlPart6, link6 }
	    };
    }

    @DataProvider
	private  Object[][] getMessagesWithSpecialLinks(){
        return new Object[][] {
			// message,	url part, expected links
	        { getSimpleMessage(link1), "mill.pl",  new String[] { } },
            { getSimpleMessage(link3), urlPart3, new String[] { link3 } },
            { getSimpleMessage(link6), urlPart456, new String[] { link6 } },
            { getAdvancedMessage(link5, link6), urlPart456, new String[] { link5, link6 } },
            { getAdvancedMessage(link1, link6), urlPart456, new String[] { link6 } }
	    };
    }

    @DataProvider
	private  Object[][] getMessages(){
        return new Object[][] {
			// message,	expected links
	        { getSimpleMessage(link1), new String[] { link1 } },
            { getSimpleMessage(link2), new String[] { link2 } },
            { getSimpleMessage(link3), new String[] { link3 } },
            { getSimpleMessage(link4), new String[] { link4 } },
            { getSimpleMessage(link5), new String[] { link5 } },
            { getSimpleMessage(link6), new String[] { link6 } },
            { getSimpleMessage(""), new String[] { } },
            { getAdvancedMessage(link1, link2), new String[] { link1, link2 } },
            { getAdvancedMessage(link5, link3), new String[] { link5, link3 } },
            { getAdvancedMessage(link6, link4), new String[] { link6, link4 } },
            { getAdvancedMessage("", ""), new String[] { } }
	    };
    }

    private String getSimpleMessage(String link) {
        return message1.replace("{link}", link);
    }

    private String getAdvancedMessage(String link1, String link2) {
        String msg = message2.replace("{link1}", link1);
        msg = msg.replace("{link2}", link2);
        return msg;
    }

    private SmtpMessage getMockEmail(String message){
        SmtpMessage email = mock(SmtpMessage.class);
        when(email.getBody()).thenReturn(message);

        return email;
    }
}
