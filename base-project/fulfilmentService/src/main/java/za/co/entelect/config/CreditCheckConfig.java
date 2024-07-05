package za.co.entelect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import za.co.entelect.services.CreditService;

@Configuration
public class CreditCheckConfig {

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("za.co.entelect.client.creditcheck");
        return marshaller;
    }

    @Bean
    public CreditService creditCheckService(Jaxb2Marshaller marshaller) {
        CreditService client = new CreditService();
        client.setDefaultUri("http://localhost:8083/CreditCheck");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }

}
