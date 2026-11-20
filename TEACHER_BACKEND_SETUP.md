# SunComputer Teacher Portal API

This update adds authenticated teacher self-service APIs without adding database tables or migrations.

## Endpoints

All endpoints require a valid JWT bearer token and `TEACHER` role (the current security config also permits `ADMIN`, but teacher-specific data still requires a linked teacher profile).

- `GET /api/teacher/me` — signed-in teacher profile.
- `GET /api/teacher/batches` — only batches assigned to the signed-in teacher.
- `GET /api/teacher/batches/{batchId}/students` — active enrolled students for an assigned batch.
- `GET /api/teacher/batches/{batchId}/attendance?attendanceDate=YYYY-MM-DD` — attendance sheet for an assigned batch and date.
- `POST /api/teacher/attendance/batch` — create/update attendance records for an assigned batch.
- `GET /api/teacher/batches/{batchId}/attendance/dashboard?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD` — batch attendance summary.
- `GET /api/teacher/batches/{batchId}/attendance/report?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD` — student attendance report.
- `GET /api/teacher/batches/{batchId}/attendance/low-attendance?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD&threshold=75` — students below the threshold.

### Bulk attendance body

```json
{
  "batchId": 12,
  "attendanceDate": "2026-10-22",
  "records": [
    { "enrollmentId": 105, "status": "PRESENT", "remarks": "" },
    { "enrollmentId": 106, "status": "ABSENT", "remarks": "Not present" }
  ]
}
```

Statuses: `PRESENT`, `ABSENT`, `LATE`, `LEAVE`. The existing attendance service validates active enrollment, matching batch membership, duplicate enrollment IDs, and disallows future attendance dates.

## Security change

The legacy general-purpose `/api/attendance/**` endpoints are now admin-only. Teachers must use `/api/teacher/**`; the portal service checks the signed-in username against the teacher profile and verifies that the requested batch is assigned to that teacher before returning roster/attendance data or saving attendance. Requests for batches assigned to other teachers return not found.

## Install

Copy the updated `src/main/java` tree into the backend project, replacing matching files, and keep your existing `application.properties`, migrations, and other project configuration. No database migration is needed.

Run from the `suncomputer` project directory:

```bash
./mvnw clean test
```

On Windows PowerShell:

```powershell
.\mvnw.cmd clean test
```

Make sure each teacher login has a matching `teachers` row whose `user_id` points to the logged-in `users.id`, and that batches have their `teacher_id` assigned.
