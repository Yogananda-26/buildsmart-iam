package com.buildsmart.analytics.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Map;


@Entity
@Table(name = "reports")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Report {

    @Id
    @Column(name = "report_id", nullable = false, unique = true)
    private String reportId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Scope scope;

    @Column(columnDefinition = "LONGTEXT")
    private String metrics;

    @Column(name = "generated_date", nullable = false)
    private OffsetDateTime generatedDate;

    protected Report() {

    }

    public Report(String reportId, Scope scope, String metrics, OffsetDateTime generatedDate) {
        this.reportId = reportId;
        this.scope = scope;
        this.metrics = metrics;
        this.generatedDate = generatedDate;
    }

    public String getReportId() {
        return reportId;
    }

    public Scope getScope() {
        return scope;
    }

    public String getMetrics() {
        return metrics;
    }

    public OffsetDateTime getGeneratedDate() {
        return generatedDate;
    }
}