package za.co.entelect.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import za.co.entelect.client.login.ApiException;
import za.co.entelect.client.login.api.AuthenticationControllerApi;

@Component
@Data
@Slf4j
public class TokenHolder {

    private static String token = null;

    public static String getToken() {
        za.co.entelect.client.login.ApiClient authApiClient = new za.co.entelect.client.login.ApiClient();
        authApiClient.setUsername("admin@entelect.co.za");
        authApiClient.setPassword("password");
        AuthenticationControllerApi authApi = new AuthenticationControllerApi(authApiClient);

        try {
            token = authApi.token(token);
        } catch (ApiException e) {
            log.warn("Error generating token: {}", e.getMessage());
        }

        return token;
    }

}
