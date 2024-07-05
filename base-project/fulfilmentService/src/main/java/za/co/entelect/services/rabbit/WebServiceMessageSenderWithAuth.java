package za.co.entelect.services.rabbit;

import org.springframework.ws.transport.http.HttpUrlConnectionMessageSender;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Base64;

public class WebServiceMessageSenderWithAuth extends HttpUrlConnectionMessageSender {
    @Override
    protected void prepareConnection(HttpURLConnection connection) throws IOException {
        String credentials = "user:password";
        String encodedAuthorization = Base64.getEncoder().encodeToString(credentials.getBytes());
        connection.setRequestProperty("Authorization", "Basic " + encodedAuthorization);

        super.prepareConnection(connection);
    }
}
