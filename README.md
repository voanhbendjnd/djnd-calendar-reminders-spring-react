# FAP Calendar Reminders

A production-ready web application for students to import their FAP timetable and receive daily timetable reminder emails.

## Architecture Overview

The application uses a manual timetable import workflow:
1. User obtains timetable HTML from FAP
2. User uploads/pastes HTML into our application
3. Backend parses the FAP timetable HTML using Jsoup
4. Timetable entries are stored in PostgreSQL
5. Daily email notifications are sent at 05:00 Asia/Ho_Chi_Minh

**Important**: The application does NOT automatically authenticate to FAP. Users manually provide timetable HTML.

## 1. Backend Folder Structure

```
backend/
├── src/main/java/com/faptimetable/backend/
│   ├── BackendApplication.java
│   ├── config/
│   ├── controller/          # REST APIs (Timetable, Auth, InternalJobs)
│   ├── domain/entity/       # JPA Entities (User, TimetableEntry, UserNotificationSetting)
│   ├── dto/                 # Data Transfer Objects
│   ├── exception/           # Global Exception Handlers
│   ├── parser/              # Jsoup HTML parsing logic
│   ├── repository/          # Spring Data JPA Repositories
│   ├── scheduler/           # Spring @Scheduled cron jobs
│   ├── security/            # JWT, UserDetails, SecurityConfig
│   └── service/             # Business Logic (Import, Timetable, Notifications)
└── src/main/resources/
    ├── application.yml
    ├── application-local.yml
    └── application-prod.yml
```

## 2. Frontend Folder Structure

```
frontend/
├── index.html
├── package.json
├── tailwind.config.js
├── postcss.config.js
├── src/
│   ├── App.tsx              # Router configuration
│   ├── main.tsx
│   ├── index.css            # Tailwind directives
│   ├── components/
│   │   └── Layout.tsx       # Main dashboard layout with Sidebar
│   ├── pages/
│   │   ├── Login.tsx
│   │   ├── Register.tsx
│   │   ├── Dashboard.tsx    # Today's classes
│   │   ├── Timetable.tsx    # Weekly view
│   │   ├── ImportTimetable.tsx # HTML Import UI
│   │   └── Settings.tsx
│   └── services/
│       └── api.ts           # Axios instance with JWT interceptor
```

## 3. Database Schema

The PostgreSQL database uses the following core entities:

- **users**: `id` (UUID), `email` (Unique), `password`, `enabled`, `created_at`, `updated_at`
- **timetable_entries**: `id` (UUID), `user_id`, `date`, `slot`, `subject_code`, `class_name`, `start_time`, `end_time`, `mode`, `room`, `lecturer`, `meeting_url`
  - *Constraints*: Unique constraint on `(user_id, date, slot, subject_code, class_name)`. Indexed by `user_id` and `date`.
- **user_notification_settings**: `id` (UUID), `user_id` (Unique), `enabled`, `notification_time`, `timezone`

## 4. API Documentation

- **Auth**
  - `POST /api/auth/register`: `{ "email": "...", "password": "..." }` -> `{ "token": "...", "email": "..."}`
  - `POST /api/auth/login`: `{ "email": "...", "password": "..." }` -> `{ "token": "...", "email": "..."}`
- **Timetables (Requires JWT Bearer)**
  - `POST /api/timetables/import`: Body is Raw HTML string.
  - `POST /api/timetables/import/json`: Body is `TimetableEntryDTO` list.
  - `GET /api/timetables/week?date=YYYY-MM-DD`: Get weekly classes.
  - `GET /api/timetables/day?date=YYYY-MM-DD`: Get today's classes.
  - `DELETE /api/timetables/week?date=YYYY-MM-DD`: Delete week.
- **Internal / Cron (Protected)**
  - `POST /api/internal/jobs/send-daily-timetable`: Manually trigger email sending.
    - Requires `X-Internal-Job-Secret` header with `INTERNAL_JOB_SECRET` value.

## 5. Local Setup Instructions

### Prerequisites
- JDK 21
- Node.js 20+
- PostgreSQL server
- Maven (included as mvnw wrapper)

### Backend
1. Create a local PostgreSQL database named `faptimetable`.
2. Navigate to `/backend`.
3. Copy `backend/.env.example` to `backend/.env` and fill in your values.
4. Run `.\mvnw.cmd spring-boot:run`.

### Frontend
1. Navigate to `/frontend`.
2. Run `npm install`.
3. Copy `frontend/.env.example` to `frontend/.env` and set `VITE_API_URL=http://localhost:8080/api`.
4. Run `npm run dev`.

## 6. Environment Variables

**Backend (`.env` or Render environment)**
```env
SPRING_PROFILES_ACTIVE=local  # or 'prod' for production
DATABASE_URL=jdbc:postgresql://localhost:5432/faptimetable
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_password
JWT_SECRET=your_jwt_secret_at_least_32_characters_long
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password
FRONTEND_URL=http://localhost:5173
INTERNAL_JOB_SECRET=your_internal_job_secret_here
```

**Frontend (`.env` or Render environment)**
```env
VITE_API_URL=http://localhost:8080/api
```

## 7. Production Deployment on Render

### Option 1: Using Render Blueprint (Recommended)

The repository includes `render.yaml` for automated deployment.

1. Push your code to GitHub/GitLab.
2. Go to Render Dashboard and click "New +".
3. Select "Blueprint" and connect your repository.
4. Render will detect `render.yaml` and create:
   - PostgreSQL database
   - Backend web service (Docker)
   - Frontend static site

**Manual configuration required in Render:**
- `MAIL_HOST`, `MAIL_USERNAME`, `MAIL_PASSWORD` (SMTP credentials)
- `FRONTEND_URL` (your deployed frontend URL)
- `INTERNAL_JOB_SECRET` (generate a strong secret)

### Option 2: Manual Deployment

#### Database (PostgreSQL)
1. Go to Render Dashboard.
2. Create **New PostgreSQL**.
3. Choose a name (e.g. `fap-timetable-db`) and create.
4. Copy the "Internal Database URL".

#### Backend (Web Service - Docker)
1. Create **New Web Service**.
2. Connect your Git repository.
3. Root Directory: `backend`
4. Runtime: `Docker`
5. Dockerfile Path: `Dockerfile`
6. Add Environment Variables:
   - `SPRING_PROFILES_ACTIVE=prod`
   - `PORT=8080`
   - `DATABASE_URL` (from PostgreSQL)
   - `DATABASE_USERNAME` (from PostgreSQL)
   - `DATABASE_PASSWORD` (from PostgreSQL)
   - `JWT_SECRET` (generate a strong secret)
   - `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`
   - `FRONTEND_URL` (your frontend URL)
   - `INTERNAL_JOB_SECRET` (generate a strong secret)

#### Frontend (Static Site)
1. Create **New Static Site**.
2. Connect your Git repository.
3. Root Directory: `frontend`
4. Build Command: `npm install && npm run build`
5. Publish Directory: `dist`
6. Add Environment Variable: `VITE_API_URL` pointing to the deployed Backend URL.
7. Add a Redirect/Rewrite rule for React Router:
   - Source: `/*`
   - Destination: `/index.html`
   - Action: `Rewrite`

## 8. Daily Scheduler

The application has a Spring @Scheduled job configured to run at 05:00 Asia/Ho_Chi_Minh.

**Important**: Render Free Web Services may sleep. To ensure reliable execution:

1. The internal job endpoint can be triggered externally:
   ```bash
   curl -X POST https://your-backend.onrender.com/api/internal/jobs/send-daily-timetable \
     -H "X-Internal-Job-Secret: your_secret"
   ```

2. Consider using:
   - Render Cron Jobs (paid tier)
   - External cron service (e.g., EasyCron, cron-job.org)
   - Render's auto-wake feature

## 9. Test Instructions

### Backend Tests
1. Navigate to `backend` directory.
2. Run `.\mvnw.cmd test -Dtest=FapTimetableParserTest`
3. The parser test validates FAP HTML parsing logic.

### Build Verification
```bash
# Backend
cd backend
.\mvnw.cmd clean package -DskipTests

# Frontend
cd frontend
npm install
npm run build
```

### Docker Build (Optional)
```bash
cd backend
docker build -t fap-timetable-backend .
```

## 10. Security Notes

- JWT tokens are used for authentication
- Internal job endpoint is protected with `INTERNAL_JOB_SECRET`
- CORS is configured to allow only specified origins
- Database credentials are never hardcoded
- The application does NOT store FAP session cookies
- Timetable HTML is parsed safely using Jsoup
