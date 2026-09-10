package com.inturn.suncomputer.admin.dashboard.dto;

public record DashboardSummaryResponse(

        long totalStudents,
        long activeStudents,

        long totalTeachers,
        long activeTeachers,

        long totalCourses,
        long activeCourses,

        long totalBatches,
        long activeBatches,

        long totalEnrollments,
        long activeEnrollments

) {
}