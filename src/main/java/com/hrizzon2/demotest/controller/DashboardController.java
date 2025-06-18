package com.hrizzon2.demotest.controller;

import com.hrizzon2.demotest.dto.AdminDashboardDto;
import com.hrizzon2.demotest.dto.StagiaireDashboardDto;
import com.hrizzon2.demotest.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/dashboard")
    public AdminDashboardDto getAdminDashboard() {
        return dashboardService.getAdminDashboardStats();
    }

    @PreAuthorize("hasRole('STAGIAIRE')")
    @GetMapping("/stagiaire/dashboard/{id}")
    public StagiaireDashboardDto getStagiaireDashboard(@PathVariable Long id) {
        return dashboardService.getStagiaireDashboardStats(id);
    }
}
