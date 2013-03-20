package com.softwaremill.common.test.web.email;

import com.dumbster.smtp.SmtpMessage;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.QuotedPrintableCodec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Class wraps SmtpMessage from mock email server and represents an email message
 * with methods to extract certain parts of it.
 *
 * Available headers might vary, depending on how email was sent.
 *
 * @author maciek
 */
public class EmailMessage {

    private SmtpMessage email;

    public EmailMessage(SmtpMessage email) {
        this.email = email;
    }

    public String getSubject(){
        return email.getHeaderValue("Subject");
    }

    /**
     * @return Email body, decoded from quoted-printable if such encoding set
     */
    public String getMessage(){
        if(getHeaderValue(EmailHeader.CONTENT_TRANSFER_ENCODING).equals("quoted-printable")) {
            return getDecodedMessage();
        } else {
            return getRawMessage();
        }
    }

    /**
     * @return Raw email body, can be encoded
     */
    public String getRawMessage(){
       return email.getBody();
    }

    /**
     * @return Decoded email body, null if decoding failed
     */
    private String getDecodedMessage() {
        String body = removeSoftLineBreaks(email.getBody());
        QuotedPrintableCodec codec = new QuotedPrintableCodec();
        try {
            return codec.decode(body);
        } catch (DecoderException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     *  Removes "soft line breaks" from encoded email.
     *
     *  Email has line length limit to 76 chars - longer lines are split and char '=' is inserted.
     *  When getting mock email body this line break could appear in th middle of one long line.
     *  Also this soft line breaks need to be cleared for QuotedPrintableCodec
     *
     */
    private String removeSoftLineBreaks(String message){
        String[] lines = message.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            int overflows = (line.length() / 76);
            if (overflows > 0) {
                int i = 0;
                for( ; i < (line.length() / 76); i++) {
                    sb.append(line.substring(i*76, (i+1)*76 - 1));
                }
                sb.append(line.substring(i*76));
            } else {
                sb.append(line);
                sb.append("\n");
            }

        }
        return sb.toString();
    }

    public String getToHeader(){
        return email.getHeaderValue("To");
    }

    public String getFromHeader(){
        return email.getHeaderValue("From");
    }

    public String getHeaderValue(EmailHeader header){
        return email.getHeaderValue(header.getValue());
    }

    public String[] getHeaderValues(EmailHeader header){
        return email.getHeaderValues(header.getValue());
    }

    public String getHeaderValue(String headerName){
        return email.getHeaderValue(headerName);
    }

    public String[] getHeaderValues(String headerName){
        return email.getHeaderValues(headerName);
    }

    public Iterator getHeaderNames() {
        return email.getHeaderNames();
    }

    /**
     * Finds all links in email message.
     * Does not validate proper url format - finds any http[s]://text-with-no-space
     *
     * @return List of Strings with found links, or empty list if none found
     */
    public List<String> getLinksInMessage() {
        List<String> links = new ArrayList<String>();
        String msg = getMessage();
        Pattern p = Pattern.compile("\\bhttps?[^\\s]*\\b");
        Matcher m = p.matcher(msg);
        while (m.find()) {
            String link = m.group();
            links.add(link);
        }

        return links;
    }

    /**
     * Finds first link in email message with given part
     *
     * @param urlPart part of url we are looking for, e.g. not changing action part
     * @return Url String with matching link, or null if none found
     */
    public String getLinkLike(String urlPart) {
        for (String link : getLinksInMessage()) {
            if (link.contains(urlPart)) {
                return link;
            }
        }

        return null;
    }

    /**
     * Finds links in email message with given part
     *
     * @param urlPart part of url we are looking for, e.g. not changing action part
     * @return List of Strings with matching links, or empty list if none found
     */
    public List<String> getLinksLike(String urlPart) {
        List<String> links = new ArrayList<String>();
        for (String link : getLinksInMessage()) {
            if (link.contains(urlPart)) {
                links.add(link);
            }
        }

        return links;
    }
}
