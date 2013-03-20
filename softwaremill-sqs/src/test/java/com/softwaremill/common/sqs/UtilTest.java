package com.softwaremill.common.sqs;

import com.amazonaws.Protocol;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;
import static com.softwaremill.common.sqs.Util.determineProtocol;

/**
 * @author Maciej Bilas
 * @since 15/10/12 16:44
 */
public class UtilTest {

    @Test
    public void shouldDetermineTheProtocolOfAHttpsURL() {
        // Given
        String testUrl = "https://sqs.softwaremill.pl";

        // When and then
        assertThat(determineProtocol(testUrl)).isEqualTo(Protocol.HTTPS);
    }

    @Test
    public void shouldDetermineTheProtocolOfAHttpURL() {
        // Given
        String testUrl = "http://foobar.softwaremill.pl";

        // When and then
        assertThat(determineProtocol(testUrl)).isEqualTo(Protocol.HTTP);
    }

    @Test
    public void shouldDetermineTheProtocolOfAHttpLinkWithANonstandardPort() {
        // Given
        String testUrl = "http://sqs.softwaremill.pl:12345";

        // When and then
        assertThat(determineProtocol(testUrl)).isEqualTo(Protocol.HTTP);
    }

    @Test
    public void shouldDetermineTheProtocolOfAHttpsLinkWithoutTheProtocolSegment() {
        // Given
        String testLink = "localhost:443";

        // When and then
        assertThat(determineProtocol(testLink)).isEqualTo(Protocol.HTTPS);
    }
}
