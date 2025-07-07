package com.audition.web.advice;


import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.client.HttpClientErrorException;
import org.mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("PMD.BeanMembersShouldSerialize")
class ExceptionControllerAdviceTest {

    @Mock
    private AuditionLogger mockLogger;

    @InjectMocks
    private ExceptionControllerAdvice exceptionHandler;

    private static final Logger SLF4J_LOGGER = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleHttpClientException_shouldReturnProblemDetailWithCorrectStatus() {
        HttpClientErrorException exception = HttpClientErrorException.create(
            HttpStatus.NOT_FOUND,
            "Not Found",
            null,
            null,
            null
        );

        ProblemDetail result = exceptionHandler.handleHttpClientException(exception);

        assertEquals(404, result.getStatus());
        assertEquals("API Error Occurred", result.getTitle());
        assertEquals("404 Not Found", result.getDetail());
    }

    @Test
    void handleSystemException_shouldReturnProblemDetailWithCustomTitle() {
        SystemException ex = new SystemException("system failure", "Custom Title", 503);

        ProblemDetail result = exceptionHandler.handleSystemException(ex);

        assertEquals(503, result.getStatus());
        assertEquals("Custom Title", result.getTitle());
        assertEquals("system failure", result.getDetail());

        verify(mockLogger).logErrorWithException(any(), eq("System exception occurred"), eq(ex));
        verify(mockLogger).logHttpStatusCodeError(any(), anyString(), eq(503));
    }

    @Test
    void handleMainException_shouldReturnProblemDetailAndLog() {
        Exception ex = new IllegalArgumentException("bad input");

        ProblemDetail result = exceptionHandler.handleMainException(ex);

        assertEquals(500, result.getStatus());
        assertEquals("API Error Occurred", result.getTitle());
        assertEquals("bad input", result.getDetail());

        verify(mockLogger).logErrorWithException(any(), eq("Unhandled exception occurred"), eq(ex));
        verify(mockLogger).logHttpStatusCodeError(any(), anyString(), eq(500));
    }

    @Test
    void handleMainException_shouldReturn405ForMethodNotSupported() {
        Exception ex = new HttpRequestMethodNotSupportedException("PUT");

        ProblemDetail result = exceptionHandler.handleMainException(ex);

        assertEquals(405, result.getStatus());
        assertEquals("API Error Occurred", result.getTitle());
        assertEquals("Request method 'PUT' is not supported", result.getDetail());

        verify(mockLogger).logErrorWithException(any(), eq("Unhandled exception occurred"), eq(ex));
        verify(mockLogger).logHttpStatusCodeError(any(), anyString(), eq(405));
    }

    @Test
    void handleSystemException_shouldFallbackTo500IfInvalidStatusCode() {
        SystemException ex = new SystemException("invalid code", "Fallback Title", 9999);

        ProblemDetail result = exceptionHandler.handleSystemException(ex);

        assertEquals(500, result.getStatus());
        assertEquals("Fallback Title", result.getTitle());

        verify(mockLogger).info(any(), contains("could not be mapped to a valid HttpStatus Code"));
    }
}
