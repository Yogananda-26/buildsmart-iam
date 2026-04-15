package com.buildsmart.analytics.repository;

import com.buildsmart.analytics.entity.Report;
import com.buildsmart.analytics.entity.Scope;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for persisting and querying generated reports.
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, String> {

    List<Report> findByScope(Scope scope);

    Optional<Report> findByReportId(String reportId);
}