package za.co.entelect.services.cis;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import za.co.entelect.client.cis.ApiClient;
import za.co.entelect.client.cis.ApiException;
import za.co.entelect.client.cis.api.CustomerApi;
import za.co.entelect.client.cis.models.AddCustomerDocumentByCustomerId200Response;
import za.co.entelect.client.cis.models.DocumentDto;
import za.co.entelect.pojo.cis.Customer;
import za.co.entelect.pojo.pc.OrderItem;
import za.co.entelect.pojo.pc.Order;
import za.co.entelect.pojo.enums.Status;
import za.co.entelect.services.TokenHolder;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
@Slf4j
public class DocumentService {
    private final SpringTemplateEngine springTemplateEngine;

    private final CustomerApi customerApi;

    @Autowired
    public DocumentService(SpringTemplateEngine springTemplateEngine) {
        this.springTemplateEngine = springTemplateEngine;
        this.customerApi = new CustomerApi(new ApiClient());
        this.customerApi.getApiClient().setBearerToken(TokenHolder.getToken());
    }

    public String htmlToPdf(String html) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            PdfWriter pdfWriter = new PdfWriter(byteArrayOutputStream);
            DefaultFontProvider defaultFontProvider = new DefaultFontProvider();
            ConverterProperties converterProperties = new ConverterProperties();
            converterProperties.setFontProvider(defaultFontProvider);
            HtmlConverter.convertToPdf(html, pdfWriter, converterProperties);

            String contract = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
            log.info(contract);
            byteArrayOutputStream.flush();

            return contract;
        } catch (IOException e) {
            log.error("Could not convert html to pdf: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void generatePdfContract(Order order, Customer customer) {
        String finalHtml;

        for (OrderItem orderItem : order.getOrderItemList()) {
            if (orderItem.getCheckStatus().getStatus().equals(Status.PASSED)) {

                Map<String, Object> contextVariables = new HashMap<>();
                contextVariables.put("orderItem", orderItem);
                contextVariables.put("customer", customer);
                contextVariables.put("startDate", LocalDate.now());

                switch (orderItem.getProduct().getName()) {
                    case "Retail Short Term Insurance":
                    case "Commercial Short Term Insurance":
                        contextVariables.put("endDate", LocalDate.now().plusMonths(6));
                        break;
                    case "Retail Long-Term Insurance":
                    case "Islamic Investment Product":
                    case "VIP Investment Product":
                        contextVariables.put("endDate", LocalDate.now().plusYears(5));
                        break;
                    case "Commercial Long-Term Insurance":
                        contextVariables.put("endDate", LocalDate.now().plusYears(10));
                        break;
                    case "Device Contract":
                        contextVariables.put("endDate", LocalDate.now().plusYears(2));
                        break;
                    case "Short-Term Investment Product":
                        contextVariables.put("endDate", LocalDate.now().plusDays(32));
                        break;
                    default:
                        contextVariables.put("endDate", LocalDate.now());
                        break;
                }

                finalHtml = springTemplateEngine.process(orderItem.getProduct().getName().toLowerCase().replaceAll(" ", "-"), new Context(Locale.getDefault(), contextVariables));
                String contract = htmlToPdf(finalHtml);
                DocumentDto documentDto = new DocumentDto();
                documentDto.setDocument(contract);

                saveContract(customer.getId().intValue(), documentDto);
            }
        }
    }

    private void saveContract(int customerId, DocumentDto document) {
        try {
            AddCustomerDocumentByCustomerId200Response response = customerApi.addCustomerDocumentByCustomerId(customerId, document);
            log.info("Saved customer contact with id {}", response.getDocumentId());
        } catch (ApiException e) {
            log.warn("Customer contract could not be saved: {}", e.getMessage());
            saveContract(customerId, document);
        }
    }

}
