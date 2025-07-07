package com.audition.common.logging;

import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.http.ProblemDetail;

import static org.mockito.Mockito.*;

@SuppressWarnings("PMD.BeanMembersShouldSerialize")
public class AuditionLoggerTest {

    private AuditionLogger auditionLogger;

    @SuppressWarnings("PMD.LoggerIsNotStaticFinal")
    private Logger mockLogger;

    @BeforeEach
    void setUp() {
        auditionLogger = new AuditionLogger();
        mockLogger = mock(Logger.class);
    }

    @Test
    void testInfo() {
        when(mockLogger.isInfoEnabled()).thenReturn(true);
        auditionLogger.info(mockLogger, "Info message");
        verify(mockLogger).info("Info message");
    }

    @Test
    void testInfoWithObject() {
        when(mockLogger.isInfoEnabled()).thenReturn(true);
        auditionLogger.info(mockLogger, "Info with {}", "value");
        verify(mockLogger).info("Info with {}", "value");
    }

    @Test
    void testDebug() {
        when(mockLogger.isDebugEnabled()).thenReturn(true);
        auditionLogger.debug(mockLogger, "Debug message");
        verify(mockLogger).debug("Debug message");
    }

    @Test
    void testWarn() {
        when(mockLogger.isWarnEnabled()).thenReturn(true);
        auditionLogger.warn(mockLogger, "Warn message");
        verify(mockLogger).warn("Warn message");
    }

    @Test
    void testError() {
        when(mockLogger.isErrorEnabled()).thenReturn(true);
        auditionLogger.error(mockLogger, "Error message");
        verify(mockLogger).error("Error message");
    }

    @Test
    void testLogErrorWithException() {
        Exception ex = new RuntimeException("Something went wrong");
        when(mockLogger.isErrorEnabled()).thenReturn(true);
        auditionLogger.logErrorWithException(mockLogger, "Exception occurred", ex);
        verify(mockLogger).error("Exception occurred", ex);
    }

    @Test
    void testLogStandardProblemDetail() {
        ProblemDetail problemDetail = ProblemDetail.forStatus(400);
        problemDetail.setTitle("Bad Request");
        problemDetail.setDetail("Invalid input");
        problemDetail.setType(URI.create("https://example.com/bad-request"));
        problemDetail.setInstance(URI.create("/posts/1"));

        Exception ex = new RuntimeException("bad data");
        when(mockLogger.isErrorEnabled()).thenReturn(true);

        auditionLogger.logStandardProblemDetail(mockLogger, problemDetail, ex);

        verify(mockLogger).error(contains("ProblemDetail: [Status: 400"), eq(ex));
    }

    @Test
    void testLogHttpStatusCodeError() {
        when(mockLogger.isErrorEnabled()).thenReturn(true);
        auditionLogger.logHttpStatusCodeError(mockLogger, "Something failed", 500);

        verify(mockLogger).error(contains("Error occurred [Code: 500] - Something failed"));
    }
}
