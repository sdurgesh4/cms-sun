package com.inturn.suncomputer.admin.dashboard.controller;

import com.inturn.suncomputer.admin.dashboard.dto.AdminDashboardResponse;
import com.inturn.suncomputer.admin.dashboard.service.AdminDashboardService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;


    @GetMapping
    public ResponseEntity<AdminDashboardResponse>
    getDashboard(

            @AuthenticationPrincipal
            UserDetails userDetails
    ) {

        String username =
                userDetails != null
                        ? userDetails.getUsername()
                        : null;


        return ResponseEntity.ok(

                dashboardService.getDashboard(
                        username
                )
        );
    }
}