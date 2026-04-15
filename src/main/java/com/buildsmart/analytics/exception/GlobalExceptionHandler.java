package com.buildsmart.analytics.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ReportNotFoundException.class)
    public ProblemDetail handleReportNotFound(ReportNotFoundException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Report Not Found");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler({AggregationException.class, ResourceDataUnavailableException.class})
    public ProblemDetail handleAggregationError(RuntimeException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Data Aggregation Error");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(ExternalServiceUnavailableException.class)
    public ProblemDetail handleExternalServiceUnavailable(ExternalServiceUnavailableException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);
        problem.setTitle("External Service Unavailable");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ProblemDetail handleProjectNotFound(ProjectNotFoundException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Project Not Found");
        problem.setDetail(ex.getMessage());
        return problem;
    }
}
