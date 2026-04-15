package com.buildsmart.analytics.client;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class ProjectServiceFallback implements ProjectServiceClient {

    @Override
    public List<ProjectDTO> getAllProjects() {
        // Return empty or cached data when Projects service is down
        return List.of();
    }

    @Override
    public ProjectDTO getProject(String projectId) {
        return new ProjectDTO(projectId, "Unknown", 0, 0, "UNAVAILABLE");
    }

    @Override
    public MilestoneDTO getMilestones(String projectId) {
        return new MilestoneDTO(0, 0);
    }
}