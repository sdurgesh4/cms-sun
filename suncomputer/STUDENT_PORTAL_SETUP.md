# Student portal API setup

This update adds student self-service GET APIs and connects the React student pages to them.

## Endpoints
- `GET /api/student/me` — linked student profile
- `GET /api/student/courses` — active/completed enrollments, course, batch and schedule
- `GET /api/student/attendance?startDate=2026-04-22&endDate=2026-10-22` — attendance summary and history (both dates optional; defaults to the last six months)
- `GET /api/student/fees` — fee balance, installments and payment history per enrollment

All endpoints derive the student from the authenticated username. The frontend does not send a student ID, preventing users from requesting another student's records by changing an ID. Existing security configuration requires authentication and restricts `/api/student/**` to `STUDENT` or `ADMIN` roles.

## Important data requirement
The authenticated user must be linked to a row in `students` through `students.user_id`, and that student must have enrollments for course/batch data to appear. Fees come from `enrollments.final_fee`; paid totals include only payments whose status is `SUCCESS`. Attendance is read from existing `attendances` records.

## Install
1. Back up your backend project.
2. Merge the updated source files into your existing `suncomputer` project. Keep your database credentials and environment-specific `application.properties` settings.
3. Replace the frontend `src` folder with the supplied frontend source ZIP.
4. Run `./mvnw clean test` (Windows: `mvnw.cmd clean test`) and `npm run dev`.
5. Log in as a student and open My Courses, Attendance, and Fees & Payments.

No schema migration is required. A Maven compile could not be run in the generation environment because the Maven wrapper distribution download was unavailable, so validate with your local Maven setup before deploying.
