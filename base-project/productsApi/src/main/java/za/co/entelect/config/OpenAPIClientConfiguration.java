package za.co.entelect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIClientConfiguration {

    @Bean
    public za.co.entelect.client.login.ApiClient authApiClient() {
        return new za.co.entelect.client.login.ApiClient();
    }

    @Bean
    public za.co.entelect.client.cis.ApiClient cisApiClient() {
        return new za.co.entelect.client.cis.ApiClient();
    }
}