package com.audition.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SystemExceptionTest {

    @Test
    void testDefaultConstructor() {
        SystemException ex = new SystemException();
        assertNull(ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    void testMessageConstructor() {
        SystemException ex = new SystemException("Some error");
        assertEquals("Some error", ex.getMessage());
        assertEquals(SystemException.DEFAULT_TITLE, ex.getTitle());
        assertNull(ex.getStatusCode());
    }

    @Test
    void testMessageAndErrorCodeConstructor() {
        SystemException ex = new SystemException("Error occurred", 400);
        assertEquals("Error occurred", ex.getMessage());
        assertEquals(SystemException.DEFAULT_TITLE, ex.getTitle());
        assertEquals(400, ex.getStatusCode());
    }

    @Test
    void testMessageAndThrowableConstructor() {
        Throwable cause = new RuntimeException("Cause");
        SystemException ex = new SystemException("Wrapped error", cause);
        assertEquals("Wrapped error", ex.getMessage());
        assertEquals(cause, ex.getCause());
        assertEquals(SystemException.DEFAULT_TITLE, ex.getTitle());
    }

    @Test
    void testDetailTitleAndStatusConstructor() {
        SystemException ex = new SystemException("Detail here", "Custom Title", 404);
        assertEquals("Detail here", ex.getMessage());
        assertEquals("Custom Title", ex.getTitle());
        assertEquals("Detail here", ex.getDetail());
        assertEquals(404, ex.getStatusCode());
    }

    @Test
    void testDetailTitleAndThrowableConstructor() {
        final String badRequestStr = "Bad Input";
        Throwable cause = new IllegalArgumentException(badRequestStr);
        SystemException ex = new SystemException(badRequestStr, "Validation Failed", cause);
        assertEquals(badRequestStr, ex.getMessage());
        assertEquals("Validation Failed", ex.getTitle());
        assertEquals(badRequestStr, ex.getDetail());
        assertEquals(500, ex.getStatusCode());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void testDetailErrorCodeAndThrowableConstructor() {
        Throwable cause = new NullPointerException("Null!");
        SystemException ex = new SystemException("Something null", 502, cause);
        assertEquals("Something null", ex.getMessage());
        assertEquals(502, ex.getStatusCode());
        assertEquals(SystemException.DEFAULT_TITLE, ex.getTitle());
        assertEquals("Something null", ex.getDetail());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void testFullConstructor() {
        Throwable cause = new Exception("Root cause");
        SystemException ex = new SystemException("Deep error", "Database Error", 503, cause);
        assertEquals("Deep error", ex.getMessage());
        assertEquals("Database Error", ex.getTitle());
        assertEquals(503, ex.getStatusCode());
        assertEquals("Deep error", ex.getDetail());
        assertEquals(cause, ex.getCause());
    }
}