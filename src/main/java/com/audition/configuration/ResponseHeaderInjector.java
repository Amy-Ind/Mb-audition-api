package com.audition.configuration;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;


@Component
public class ResponseHeaderInjector {

    // TODO Inject openTelemetry trace and span Ids in the response headers.


    public void injectOpenTelemetryHeaders(HttpServletResponse response) {
        // Get current OpenTelemetry context
        Context context = Context.current();

        // Get the current span from context
        Span span = Span.fromContext(context);

        // Only proceed if we have a valid span
        if (span != null && span.getSpanContext().isValid()) {
            // Get trace and span IDs
            String traceId = span.getSpanContext().getTraceId();
            String spanId = span.getSpanContext().getSpanId();

            // Inject into response headers
            response.addHeader("X-Trace-Id", traceId);
            response.addHeader("X-Span-Id", spanId);
        }
    }

}
