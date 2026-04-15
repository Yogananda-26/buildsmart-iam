package com.buildsmart.analytics.service;

import com.buildsmart.analytics.dto.BudgetAlertRecord;
import com.buildsmart.analytics.dto.CashFlowRecord;
import com.buildsmart.analytics.dto.DashboardSummaryRecord;
import com.buildsmart.analytics.dto.ExportStatusRecord;
import com.buildsmart.analytics.dto.GenerateReportRequest;
import com.buildsmart.analytics.dto.HistoricalReportRecord;
import com.buildsmart.analytics.dto.LaborAllocationRecord;
import com.buildsmart.analytics.dto.ProjectHealthRecord;
import com.buildsmart.analytics.dto.ProjectSummaryRecord;
import com.buildsmart.analytics.dto.ReportResponseRecord;
import com.buildsmart.analytics.dto.ResourceUtilizationRecord;
import com.buildsmart.analytics.dto.SafetyInspectionSummaryRecord;
import com.buildsmart.analytics.dto.SafetyTrendRecord;
import com.buildsmart.analytics.dto.SiteEngineerDailyLogRecord;
import com.buildsmart.analytics.dto.SiteEngineerPerformanceRecord;
import com.buildsmart.analytics.dto.SiteProgressSummaryRecord;
import com.buildsmart.analytics.dto.VendorComplianceRecord;
import com.buildsmart.analytics.dto.VendorPerformanceRecord;
import com.buildsmart.analytics.dto.VendorSpendRecord;
import com.buildsmart.analytics.entity.Report;
import com.buildsmart.analytics.entity.Scope;
import com.buildsmart.analytics.exception.AggregationException;
import com.buildsmart.analytics.exception.ReportNotFoundException;
import com.buildsmart.analytics.repository.ReportRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {

    private final ReportRepository reportRepository;
    private final ObjectMapper objectMapper;

    public AnalyticsService(ReportRepository reportRepository, ObjectMapper objectMapper) {
        this.reportRepository = reportRepository;
        this.objectMapper = objectMapper;
    }

    public ReportResponseRecord generateReport(GenerateReportRequest request) {
        try {
            var metrics = switch (request.scope()) {
                case PROJECT -> buildProjectMetrics(request.targetId());
                case RESOURCE -> buildResourceMetrics();
                case SAFETY -> buildSafetyMetrics();
                case FINANCE -> buildFinanceMetrics();
                case VENDOR -> buildVendorMetrics();
                case SITE_ENGINEER -> buildSiteEngineerMetrics();
            };

            var metricsJson = objectMapper.writeValueAsString(metrics);
            var report = new Report(nextReportId(), request.scope(), metricsJson, OffsetDateTime.now());
            var saved = reportRepository.save(report);
            return new ReportResponseRecord(saved.getReportId(), saved.getScope(), saved.getMetrics(), saved.getGeneratedDate());
        } catch (JsonProcessingException ex) {
            throw new AggregationException("Failed to serialize report metrics", ex);
        }
    }

    public ReportResponseRecord getReport(String id) {
        var report = reportRepository.findById(id).orElseThrow(() -> new ReportNotFoundException(id));
        return new ReportResponseRecord(report.getReportId(), report.getScope(), report.getMetrics(), report.getGeneratedDate());
    }

    public DashboardSummaryRecord getDashboardSummary() {
        try {
            var projectSummaries = fetchProjectSummaries();
            var safetyCompliance = fetchSafetyCompliance();
            var usage = fetchResourceUsage();
            var resourceUtilization = usage.usedHours() / Math.max(usage.usedHours() + usage.idleHours(), 1.0);
            var avgBudgetVariance = fetchBudgetVarianceAverage();

            return new DashboardSummaryRecord(
                    projectSummaries.size(),
                    avgBudgetVariance,
                    safetyCompliance,
                    resourceUtilization
            );
        } catch (Exception ex) {
            throw new AggregationException("Failed to aggregate dashboard summary", ex);
        }
    }

    public ProjectHealthRecord getProjectHealth(String projectId) {
        try {
            var cpi = fetchProjectCostPerformance(projectId);
            var sv = fetchProjectScheduleVariance(projectId);
            return new ProjectHealthRecord(sv, cpi);
        } catch (Exception ex) {
            throw new AggregationException("Unable to compute project health", ex);
        }
    }

    public List<ProjectSummaryRecord> getProjectSummary() {
        return fetchProjectSummaries();
    }

    public List<SafetyTrendRecord> getSafetyTrends() {
        return fetchSafetyTrends();
    }

    public SafetyInspectionSummaryRecord getSafetyInspectionsSummary() {
        var data = fetchInspectionSummary();
        return new SafetyInspectionSummaryRecord(data.passed(), data.failed(), data.passRatio());
    }

    public BudgetAlertRecord getBudgetVariance(String projectId) {
        var budget = fetchBudgetForProject(projectId);
        var variance = budget.plannedAmount() - budget.actualAmount();
        var thresholdExceeded = Math.abs(variance) / Math.max(budget.plannedAmount(), 1.0) > 0.10;
        return new BudgetAlertRecord(projectId, budget.plannedAmount(), budget.actualAmount(), variance, thresholdExceeded);
    }

    public List<CashFlowRecord> getCashFlow() {
        return fetchCashFlow();
    }

    public ResourceUtilizationRecord getResourceUtilization() {
        var usage = fetchResourceUsage();
        var utilizationRate = usage.usedHours() / Math.max(usage.usedHours() + usage.idleHours(), 1.0);
        return new ResourceUtilizationRecord(usage.usedHours(), usage.idleHours(), utilizationRate);
    }

    public List<LaborAllocationRecord> getLaborAllocation() {
        return fetchLaborAllocations();
    }

    public List<HistoricalReportRecord> getHistoricalReports(Scope scope) {
        return reportRepository.findByScope(scope).stream()
                .map(report -> new HistoricalReportRecord(report.getReportId(), report.getScope(), report.getGeneratedDate()))
                .toList();
    }

    public ExportStatusRecord exportReport(String reportId) {
        if (!reportRepository.existsById(reportId)) {
            throw new ReportNotFoundException(reportId);
        }
        try {
            Thread.sleep(500);
            return new ExportStatusRecord(reportId, "COMPLETED", "Export simulated successfully.");
        } catch (Exception ex) {
            throw new AggregationException("Failed to export report", ex);
        }
    }

    // ===== Vendor Analytics =====

    public List<VendorPerformanceRecord> getVendorPerformance() {
        return fetchVendorPerformances();
    }

    public VendorPerformanceRecord getVendorPerformanceById(String vendorId) {
        return fetchVendorPerformances().stream()
                .filter(v -> v.vendorId().equals(vendorId))
                .findFirst()
                .orElseThrow(() -> new ReportNotFoundException("Vendor performance not found: " + vendorId));
    }

    public VendorComplianceRecord getVendorCompliance() {
        var vendors = fetchVendorPerformances();
        int total = vendors.size();
        int compliant = (int) vendors.stream().filter(v -> v.qualityScore() >= 80.0).count();
        int nonCompliant = (int) vendors.stream().filter(v -> v.qualityScore() < 60.0).count();
        int pending = total - compliant - nonCompliant;
        double complianceRate = total > 0 ? (compliant * 100.0 / total) : 0;
        int expiringSoon = (int) vendors.stream().filter(v -> v.activeContracts() == 1).count();
        return new VendorComplianceRecord(total, compliant, nonCompliant, pending, complianceRate, expiringSoon);
    }

    public List<VendorSpendRecord> getVendorSpend() {
        return fetchVendorSpend();
    }

    // ===== Site Engineer Analytics =====

    public List<SiteEngineerPerformanceRecord> getSiteEngineerPerformance() {
        return fetchSiteEngineerPerformances();
    }

    public SiteEngineerPerformanceRecord getSiteEngineerPerformanceById(String engineerId) {
        return fetchSiteEngineerPerformances().stream()
                .filter(e -> e.engineerId().equals(engineerId))
                .findFirst()
                .orElseThrow(() -> new ReportNotFoundException("Site engineer not found: " + engineerId));
    }

    public SiteProgressSummaryRecord getSiteProgressSummary() {
        var engineers = fetchSiteEngineerPerformances();
        int total = engineers.size();
        double avgCompletion = engineers.stream().mapToDouble(SiteEngineerPerformanceRecord::taskCompletionRate).average().orElse(0);
        double avgQuality = engineers.stream().mapToDouble(SiteEngineerPerformanceRecord::qualityScore).average().orElse(0);
        int openIssues = engineers.stream().mapToInt(SiteEngineerPerformanceRecord::issuesPending).sum();
        int resolvedIssues = engineers.stream().mapToInt(SiteEngineerPerformanceRecord::issuesResolved).sum();
        int totalInspections = engineers.stream().mapToInt(SiteEngineerPerformanceRecord::totalInspections).sum();
        long activeSites = engineers.stream().map(SiteEngineerPerformanceRecord::assignedProject).distinct().count();
        double efficiency = avgCompletion > 0 ? (avgCompletion * avgQuality) / 100.0 : 0;
        return new SiteProgressSummaryRecord(total, (int) activeSites, avgCompletion, avgQuality, openIssues, resolvedIssues, totalInspections, efficiency);
    }

    public List<SiteEngineerDailyLogRecord> getSiteEngineerDailyLogs() {
        return fetchSiteEngineerDailyLogs();
    }

    public List<SiteEngineerDailyLogRecord> getSiteEngineerDailyLogsByEngineer(String engineerId) {
        return fetchSiteEngineerDailyLogs().stream()
                .filter(log -> log.engineerId().equals(engineerId))
                .toList();
    }

    private String nextReportId() {
        return reportRepository.findAll().stream()
                .map(Report::getReportId)
                .filter(id -> id.startsWith("BSRA"))
                .map(this::extractNumericPart)
                .max(Integer::compareTo)
                .map(max -> formatReportId(max + 1))
                .orElse(formatReportId(1));
    }

    private int extractNumericPart(String id) {
        try {
            return Integer.parseInt(id.substring(4));
        } catch (NumberFormatException | StringIndexOutOfBoundsException ex) {
            return 0;
        }
    }

    private String formatReportId(int value) {
        return String.format("BSRA%03d", value);
    }

    private Map<String, Object> buildProjectMetrics(String targetId) {
        var milestones = fetchMilestones(targetId);
        var progress = calculateProgress(milestones.plannedMilestones(), milestones.actualMilestones());
        var budget = fetchBudgetForProject(targetId);
        return Map.of(
                "projectId", targetId,
                "progressPercent", progress,
                "budgetVariance", budget.actualAmount() - budget.plannedAmount(),
                "generatedAt", OffsetDateTime.now().toString()
        );
    }

    private Map<String, Object> buildResourceMetrics() {
        var usage = fetchResourceUsage();
        return Map.of(
                "usedHours", usage.usedHours(),
                "idleHours", usage.idleHours(),
                "utilizationRate", usage.usedHours() / Math.max(usage.usedHours() + usage.idleHours(), 1.0)
        );
    }

    private Map<String, Object> buildSafetyMetrics() {
        var trend = fetchSafetyTrends();
        var summary = fetchInspectionSummary();
        return Map.of(
                "incidentTrend", trend,
                "inspectionPassRatio", summary.passRatio(),
                "inspectionPassed", summary.passed(),
                "inspectionFailed", summary.failed()
        );
    }

    private Map<String, Object> buildFinanceMetrics() {
        var cashFlow = fetchCashFlow();
        return Map.of(
                "monthlyCashFlow", cashFlow,
                "budgetAlerts", fetchProjectSummaries().stream().map(ProjectSummaryRecord::budgetVariancePercent).toList()
        );
    }

    private double calculateProgress(int planned, int actual) {
        if (planned == 0) {
            return 0.0;
        }
        return Math.min(100.0, actual * 100.0 / planned);
    }

    private double fetchProjectScheduleVariance(String projectId) {
        var milestones = fetchMilestones(projectId);
        return (milestones.actualMilestones() - milestones.plannedMilestones()) * 1.0;
    }

    private double fetchProjectCostPerformance(String projectId) {
        var budget = fetchBudgetForProject(projectId);
        if (budget.actualAmount() == 0) {
            return 1.0;
        }
        return budget.plannedAmount() / budget.actualAmount();
    }

    private List<ProjectSummaryRecord> fetchProjectSummaries() {
        return List.of(
                new ProjectSummaryRecord("P-1001", "West Park Towers", 72.3, 8.5, "ON_TRACK"),
                new ProjectSummaryRecord("P-1002", "Lakeview Residences", 54.1, 12.7, "AT_RISK"),
                new ProjectSummaryRecord("P-1003", "Steel Bridge Crossing", 88.9, 3.9, "ON_TRACK")
        );
    }

    private List<SafetyTrendRecord> fetchSafetyTrends() {
        return List.of(
                new SafetyTrendRecord(LocalDate.now().minusDays(14), categorizeSeverity(2), 2),
                new SafetyTrendRecord(LocalDate.now().minusDays(7), categorizeSeverity(5), 5),
                new SafetyTrendRecord(LocalDate.now(), categorizeSeverity(1), 1)
        );
    }

    private SafetyInspectionSummaryData fetchInspectionSummary() {
        var passed = 18L;
        var failed = 4L;
        var ratio = passed / (double) Math.max(passed + failed, 1);
        return new SafetyInspectionSummaryData(passed, failed, ratio);
    }

    private BudgetData fetchBudgetForProject(String projectId) {
        return new BudgetData(1_200_000.00, 1_085_000.00);
    }

    private ResourceUsageData fetchResourceUsage() {
        return new ResourceUsageData(1_760.0, 420.0);
    }

    private List<LaborAllocationRecord> fetchLaborAllocations() {
        return List.of(
                new LaborAllocationRecord("Site A", 820.0, 980.0),
                new LaborAllocationRecord("Site B", 610.0, 720.0),
                new LaborAllocationRecord("Site C", 340.0, 460.0)
        );
    }

    private List<CashFlowRecord> fetchCashFlow() {
        return List.of(
                new CashFlowRecord(YearMonth.now().minusMonths(2), 420_000.0, 380_000.0, 40_000.0),
                new CashFlowRecord(YearMonth.now().minusMonths(1), 390_000.0, 405_000.0, -15_000.0),
                new CashFlowRecord(YearMonth.now(), 450_000.0, 430_000.0, 20_000.0)
        );
    }

    private ProjectMilestoneData fetchMilestones(String projectId) {
        return new ProjectMilestoneData(18, 16);
    }

    private double fetchBudgetVarianceAverage() {
        return 7.4;
    }

    private double fetchSafetyCompliance() {
        return 0.91;
    }

    private String categorizeSeverity(int incidentCount) {
        return switch (incidentCount) {
            case 0, 1 -> "LOW";
            case 2, 3 -> "MEDIUM";
            default -> "HIGH";
        };
    }

    private Map<String, Object> buildVendorMetrics() {
        var performances = fetchVendorPerformances();
        var spend = fetchVendorSpend();
        var avgRating = performances.stream().mapToDouble(VendorPerformanceRecord::qualityScore).average().orElse(0);
        return Map.of(
                "vendorCount", performances.size(),
                "averageQualityScore", avgRating,
                "spendCategories", spend,
                "generatedAt", OffsetDateTime.now().toString()
        );
    }

    private List<VendorPerformanceRecord> fetchVendorPerformances() {
        return List.of(
                new VendorPerformanceRecord("VND001", "Titan Steel Suppliers", 94.5, 88.2, -2.3, 3, "A"),
                new VendorPerformanceRecord("VND002", "QuickMix Concrete", 87.3, 82.5, 1.8, 2, "B+"),
                new VendorPerformanceRecord("VND003", "ElectraPower Solutions", 96.1, 91.7, -0.5, 4, "A+"),
                new VendorPerformanceRecord("VND004", "SafeGuard Equipment", 78.8, 74.3, 5.2, 1, "B"),
                new VendorPerformanceRecord("VND005", "GreenScape Landscaping", 65.2, 58.9, 12.4, 0, "C"),
                new VendorPerformanceRecord("VND006", "PrimePlumb Services", 91.0, 85.6, -1.1, 2, "A-"),
                new VendorPerformanceRecord("VND007", "AeroLift Crane Hire", 93.7, 90.1, -3.4, 3, "A"),
                new VendorPerformanceRecord("VND008", "ClearView Glass Works", 72.4, 69.8, 8.7, 1, "C+")
        );
    }

    private List<VendorSpendRecord> fetchVendorSpend() {
        return List.of(
                new VendorSpendRecord("Materials", 850_000, 812_000, 38_000, 3),
                new VendorSpendRecord("Electrical", 420_000, 445_000, -25_000, 1),
                new VendorSpendRecord("Safety Equipment", 180_000, 165_000, 15_000, 1),
                new VendorSpendRecord("Heavy Equipment", 620_000, 598_000, 22_000, 1),
                new VendorSpendRecord("Plumbing", 290_000, 305_000, -15_000, 1),
                new VendorSpendRecord("Landscaping", 150_000, 172_000, -22_000, 1)
        );
    }

    private Map<String, Object> buildSiteEngineerMetrics() {
        var performances = fetchSiteEngineerPerformances();
        var avgCompletion = performances.stream().mapToDouble(SiteEngineerPerformanceRecord::taskCompletionRate).average().orElse(0);
        var avgQuality = performances.stream().mapToDouble(SiteEngineerPerformanceRecord::qualityScore).average().orElse(0);
        return Map.of(
                "engineerCount", performances.size(),
                "avgTaskCompletionRate", avgCompletion,
                "avgQualityScore", avgQuality,
                "generatedAt", OffsetDateTime.now().toString()
        );
    }

    private List<SiteEngineerPerformanceRecord> fetchSiteEngineerPerformances() {
        return List.of(
                new SiteEngineerPerformanceRecord("SE001", "Rajesh Kumar", "West Park Towers", 92.5, 9.2, 28, 45, 3, 88.4, "A"),
                new SiteEngineerPerformanceRecord("SE002", "Anil Sharma", "Lakeview Residences", 85.3, 8.5, 22, 38, 7, 81.2, "B+"),
                new SiteEngineerPerformanceRecord("SE003", "Priya Patel", "Steel Bridge Crossing", 96.1, 9.8, 35, 52, 1, 94.7, "A+"),
                new SiteEngineerPerformanceRecord("SE004", "Vikram Singh", "West Park Towers", 78.9, 7.8, 18, 30, 8, 73.5, "B"),
                new SiteEngineerPerformanceRecord("SE005", "Meena Reddy", "Lakeview Residences", 88.7, 8.9, 25, 41, 4, 85.1, "A-"),
                new SiteEngineerPerformanceRecord("SE006", "Suresh Nair", "Steel Bridge Crossing", 71.2, 7.2, 15, 22, 12, 66.8, "C+")
        );
    }

    private List<SiteEngineerDailyLogRecord> fetchSiteEngineerDailyLogs() {
        var today = LocalDate.now();
        return List.of(
                new SiteEngineerDailyLogRecord("DL001", "SE001", "Rajesh Kumar", "P-1001", "West Park Towers", today.toString(), 9.5, 8, 10, 1, "Clear", "Foundation pour completed for Block C"),
                new SiteEngineerDailyLogRecord("DL002", "SE002", "Anil Sharma", "P-1002", "Lakeview Residences", today.toString(), 8.0, 6, 8, 2, "Partly Cloudy", "Rebar inspection pending for Level 3"),
                new SiteEngineerDailyLogRecord("DL003", "SE003", "Priya Patel", "P-1003", "Steel Bridge Crossing", today.toString(), 10.0, 12, 12, 0, "Clear", "Girder placement ahead of schedule"),
                new SiteEngineerDailyLogRecord("DL004", "SE004", "Vikram Singh", "P-1001", "West Park Towers", today.toString(), 7.5, 5, 8, 3, "Rainy", "Electrical conduit delayed due to rain"),
                new SiteEngineerDailyLogRecord("DL005", "SE001", "Rajesh Kumar", "P-1001", "West Park Towers", today.minusDays(1).toString(), 9.0, 9, 10, 0, "Clear", "Column reinforcement completed"),
                new SiteEngineerDailyLogRecord("DL006", "SE003", "Priya Patel", "P-1003", "Steel Bridge Crossing", today.minusDays(1).toString(), 9.5, 11, 11, 1, "Windy", "Safety net installed on north side"),
                new SiteEngineerDailyLogRecord("DL007", "SE002", "Anil Sharma", "P-1002", "Lakeview Residences", today.minusDays(1).toString(), 8.5, 7, 9, 1, "Clear", "Plumbing rough-in for floors 4-6"),
                new SiteEngineerDailyLogRecord("DL008", "SE005", "Meena Reddy", "P-1002", "Lakeview Residences", today.toString(), 9.0, 8, 9, 1, "Clear", "Concrete curing check for Level 2 slab")
        );
    }

    private record ProjectMilestoneData(int plannedMilestones, int actualMilestones) {
    }

    private record BudgetData(double plannedAmount, double actualAmount) {
    }

    private record ResourceUsageData(double usedHours, double idleHours) {
    }

    private record SafetyInspectionSummaryData(long passed, long failed, double passRatio) {
    }
}