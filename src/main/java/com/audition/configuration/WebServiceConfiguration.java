package com.audition.configuration;

import com.audition.common.logging.AuditionLogger;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebServiceConfiguration implements WebMvcConfigurer {

    private static final String YEAR_MONTH_DAY_PATTERN = "yyyy-MM-dd";
    private static final Logger logger = LoggerFactory.getLogger(WebServiceConfiguration.class);

    @SuppressWarnings("PMD")
    private final AuditionLogger auditionLogger = new AuditionLogger();

    @Bean
    public ObjectMapper objectMapper() {
        // TODO configure Jackson Object mapper that -Done
        //  1. allows for date format as yyyy-MM-dd
        //  2. Does not fail on unknown properties
        //  3. maps to camelCase
        //  4. Does not include null values or empty values
        //  5. does not write datas as timestamps.
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat(YEAR_MONTH_DAY_PATTERN,  Locale.getDefault()));
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return mapper;
    }

    @Bean
    public RestTemplate restTemplate(ObjectMapper objectMapper) {
        final RestTemplate restTemplate = new RestTemplate(
            new BufferingClientHttpRequestFactory(createClientFactory()));
        // TODO use object mapper -Done
        // Configure ObjectMapper usage
        restTemplate.getMessageConverters().removeIf(c ->
            c instanceof MappingJackson2HttpMessageConverter
        );
        restTemplate.getMessageConverters().add(
            new MappingJackson2HttpMessageConverter(objectMapper)
        );
        // TODO create a logging interceptor that logs request/response for rest template calls. -Done

        restTemplate.setInterceptors(Collections.singletonList(loggingInterceptor()));

        return restTemplate;
    }

    private SimpleClientHttpRequestFactory createClientFactory() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        return requestFactory;
    }

    // Logging interceptor implementation
    private ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            // Log request
            auditionLogger.info(logger, "Request URI: {}", request.getURI());
            auditionLogger.info(logger, "Request Method: {}", request.getMethod());
            auditionLogger.info(logger, "Request Headers: {}", request.getHeaders());
            auditionLogger.info(logger, "Request Body: {}", new String(body, StandardCharsets.UTF_8));

            ClientHttpResponse response = execution.execute(request, body);

            // Read response body
            InputStream responseStream = response.getBody();
            String responseBody = responseStream != null
                ? new String(responseStream.readAllBytes(), StandardCharsets.UTF_8)
                : "[no body]";

            auditionLogger.info(logger, "Response Status: {}", response.getStatusCode());
            auditionLogger.info(logger, "Response Headers: {}", response.getHeaders());
            auditionLogger.info(logger, "Response Body: {}", responseBody);
            return response;
        };
    }

}
