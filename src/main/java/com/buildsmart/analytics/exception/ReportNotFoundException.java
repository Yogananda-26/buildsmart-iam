package com.buildsmart.analytics.exception;

public class ReportNotFoundException extends RuntimeException {

    public ReportNotFoundException(String reportId) {
        super("Report not found: " + reportId);
    }
}
