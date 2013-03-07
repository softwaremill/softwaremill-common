package pl.softwaremill.common.testserver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface Responder {

    boolean canRespond(HttpServletRequest request);

    void respond(HttpServletRequest request, HttpServletResponse response) throws IOException;

}
