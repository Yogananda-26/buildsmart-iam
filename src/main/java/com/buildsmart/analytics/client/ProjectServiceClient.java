package com.buildsmart.analytics.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "project-manager-service", fallback = ProjectServiceFallback.class)
public interface ProjectServiceClient {

    @GetMapping("/api/projects")
    List<ProjectDTO> getAllProjects();

    @GetMapping("/api/projects/{projectId}")
    ProjectDTO getProject(@PathVariable String projectId);

    @GetMapping("/api/projects/{projectId}/milestones")
    MilestoneDTO getMilestones(@PathVariable String projectId);
}