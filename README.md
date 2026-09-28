# RunForge — Gesamtarchitektur

> *Forge your run.* — Ein Trainingsplan-Management-Tool für runforgen  
> © 2026 RunForge

---

## Überblick

RunForge ist eine Web-Applikation zur Verwaltung von Mitgliedern, Trainingsplänen und Statistiken einer Laufgruppe. Die Architektur folgt dem klassischen dreischichtigen Modell: React-Frontend, Spring Boot Backend und Supabase (PostgreSQL) als Datenbank.

---

## Tech Stack

| Schicht | Technologie | Version |
|---------|-------------|---------|
| Frontend | React + TypeScript | 18.x |
| Build Tool | Vite | 5.x |
| State / Caching | TanStack Query | 5.x |
| HTTP Client | Axios | 1.x |
| Charts | Recharts | 2.x |
| Icons | Lucide React | 0.312 |
| Backend | Spring Boot | 3.2 |
| Sprache | Java | 21 |
| ORM | Spring Data JPA / Hibernate | — |
| Validierung | Jakarta Validation | — |
| Datenbank | Supabase (PostgreSQL) | 15+ |
| DB-Name | postgres (Supabase-Standard) | — |

---

## Projektstruktur

```
runforge/
├── README.md
├── backend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/runforge/
│       │   ├── runforgeApplication.java
│       │   ├── model/
│       │   │   ├── Member.java
│       │   │   ├── TrainingPlan.java
│       │   │   └── PlanAssignment.java
│       │   ├── repository/
│       │   │   ├── MemberRepository.java
│       │   │   ├── TrainingPlanRepository.java
│       │   │   └── PlanAssignmentRepository.java
│       │   ├── service/
│       │   │   ├── MemberService.java
│       │   │   ├── TrainingPlanService.java
│       │   │   └── StatisticsService.java
│       │   └── controller/
│       │       ├── MemberController.java
│       │       ├── TrainingPlanController.java
│       │       └── StatisticsController.java
│       └── resources/
│           ├── application.properties
│           └── data.sql
└── frontend/
    ├── index.html
    ├── vite.config.ts
    ├── package.json
    └── src/
        ├── main.tsx
        ├── App.tsx
        ├── App.css
        ├── types/
        │   └── index.ts
        ├── services/
        │   └── api.ts
        └── pages/
            ├── DashboardPage.tsx
            ├── MembersPage.tsx
            └── PlansPage.tsx
```

---

## Schichtenarchitektur

```
┌─────────────────────────────────────────────────────────┐
│              FRONTEND  ·  React + TypeScript             │
│                     Port 3000 · Vite                     │
│  ┌──────────────┐  ┌──────────────┐  ┌───────────────┐  │
│  │  Dashboard   │  │  Mitglieder  │  │ Trainingspläne│  │
│  │  (Charts)    │  │  (CRUD)      │  │ (CRUD)        │  │
│  └──────────────┘  └──────────────┘  └───────────────┘  │
└────────────────────────┬────────────────────────────────┘
                         │ REST API · JSON · HTTP
                         ▼
┌─────────────────────────────────────────────────────────┐
│             BACKEND  ·  Spring Boot 3 / Java 21          │
│                        Port 8080                         │
│  ┌──────────────────────────────────────────────────┐   │
│  │                  Controller Layer                 │   │
│  │  MemberController · PlanController · StatsCtrl   │   │
│  └──────────────────────┬───────────────────────────┘   │
│                         ▼                                │
│  ┌──────────────────────────────────────────────────┐   │
│  │                  Service Layer                    │   │
│  │   Business-Logik · Validierung · Soft Delete      │   │
│  └──────────────────────┬───────────────────────────┘   │
│                         ▼                                │
│  ┌──────────────────────────────────────────────────┐   │
│  │              JPA Repository Layer                 │   │
│  │    Spring Data · CRUD + Custom Queries            │   │
│  └──────────────────────┬───────────────────────────┘   │
└───────────────────────┬─┴────────────────────────────────┘
                        │ JPA / JDBC
                        ▼
┌─────────────────────────────────────────────────────────┐
│              DATENBANK  ·  Supabase (PostgreSQL)          │
│        members · training_plans · plan_assignments       │
│                   DB-Name: postgres                      │
└─────────────────────────────────────────────────────────┘
```

---

## Datenmodell

### Entitäten

**Member** (Mitglied)

| Feld | Typ | Beschreibung |
|------|-----|--------------|
| id | Long | Primärschlüssel |
| firstName | String | Vorname |
| lastName | String | Nachname |
| email | String (unique) | E-Mail-Adresse |
| phone | String | Telefonnummer |
| level | Enum | BEGINNER / INTERMEDIATE / ADVANCED |
| joinedDate | LocalDate | Beitrittsdatum |
| active | boolean | Soft-Delete-Flag |

**TrainingPlan** (Trainingsplan)

| Feld | Typ | Beschreibung |
|------|-----|--------------|
| id | Long | Primärschlüssel |
| name | String | Name des Plans |
| description | String | Beschreibung |
| level | Enum | Ziellevel |
| durationWeeks | int | Laufzeit in Wochen |
| weeklyKm | int | Wochenkilometer |
| createdDate | LocalDate | Erstellungsdatum |

**PlanAssignment** (Zuweisung)

| Feld | Typ | Beschreibung |
|------|-----|--------------|
| id | Long | Primärschlüssel |
| member | Member | FK → Mitglied |
| trainingPlan | TrainingPlan | FK → Plan |
| startDate | LocalDate | Startdatum |
| endDate | LocalDate | Enddatum |
| completed | boolean | Abgeschlossen? |

### Beziehungen

```
Member ──────────────────── PlanAssignment ──────────────────── TrainingPlan
  1                               N : 1                               1
  └── Ein Mitglied kann           └── Zuweisung verbindet    ─────────┘
      mehrere Pläne haben             Mitglied und Plan
```

---

## REST API Endpunkte

### Mitglieder  `/v01/members`

| Methode | Pfad | Beschreibung |
|---------|------|--------------|
| GET | `/v01/members` | Alle Mitglieder |
| GET | `/v01/members/active` | Nur aktive Mitglieder |
| GET | `/v01/members/{id}` | Einzelnes Mitglied |
| POST | `/v01/members` | Neues Mitglied erstellen |
| PUT | `/v01/members/{id}` | Mitglied bearbeiten |
| DELETE | `/v01/members/{id}` | Soft Delete (active = false) |

### Trainingspläne  `/v01/plans`

| Methode | Pfad | Beschreibung |
|---------|------|--------------|
| GET | `/v01/plans` | Alle Pläne |
| GET | `/v01/plans/{id}` | Einzelner Plan |
| POST | `/v01/plans` | Neuen Plan erstellen |
| PUT | `/v01/plans/{id}` | Plan bearbeiten |
| DELETE | `/v01/plans/{id}` | Plan löschen |

### Statistiken  `/v01/stats`

| Methode | Pfad | Beschreibung |
|---------|------|--------------|
| GET | `/v01/stats/dashboard` | Aggregierte Dashboard-Daten |

---

## Frontend-Seiten

### Dashboard (`/`)
- Statistik-Kacheln: Mitglieder gesamt, aktive Mitglieder, Anzahl Pläne, aktive Zuweisungen
- Pie-Chart: Verteilung Mitglieder nach Level (Recharts)
- Balken-Chart: Aktive vs. abgeschlossene Zuweisungen

### Mitglieder (`/members`)
- Tabelle mit allen Mitgliedern inkl. Level-Badge und Status
- Modal-Formular: Erstellen / Bearbeiten
- Soft-Delete über Trash-Icon

### Trainingspläne (`/plans`)
- Karten-Grid mit Plan-Details (Dauer, km/Woche, Level)
- Modal-Formular: Erstellen / Bearbeiten
- Löschen per Icon

---

## Konfiguration

### Backend `application.properties`

```properties
spring.application.name=runforge-backend
server.port=8080

# Lokale Secrets aus backend/.env laden (echte Umgebungsvariablen haben Vorrang)
spring.config.import=optional:file:.env[.properties],optional:file:backend/.env[.properties]

# Supabase (PostgreSQL) - Verbindungsdaten via Umgebungsvariablen
spring.datasource.url=${SUPABASE_DB_URL:jdbc:postgresql://localhost:5432/RunForgeDb}
spring.datasource.username=${SUPABASE_DB_USER:postgres}
spring.datasource.password=${SUPABASE_DB_PASSWORD:}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=${SPRING_JPA_HIBERNATE_DDL_AUTO:update}
spring.jpa.show-sql=true

spring.h2.console.enabled=false
spring.sql.init.mode=${SPRING_SQL_INIT_MODE:never}
```

Die tatsächlichen Werte für `SUPABASE_DB_URL`, `SUPABASE_DB_USER` und `SUPABASE_DB_PASSWORD` werden **nicht** im Code hinterlegt, sondern in `backend/.env` (wird nicht committet) oder als Umgebungsvariablen gesetzt. Eine Vorlage dafür liefert `backend/.env.example`.

### Frontend `vite.config.ts`

```typescript
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api': { target: 'http://localhost:8080', changeOrigin: true }
    }
  }
});
```

---

## Starten

Voraussetzungen: **Java 21** (`java -version` bzw. `JAVA_HOME` prüfen) und eine ausgefüllte `backend/.env` (Vorlage: `backend/.env.example`). Maven muss nicht installiert sein — der Maven Wrapper (`mvnw`) liegt im Backend.

```bash
# Backend
cd backend
./mvnw spring-boot:run      # Windows (cmd): mvnw.cmd spring-boot:run
# → http://localhost:8080

# Frontend (neues Terminal)
cd frontend
npm install
npm run dev
# → http://localhost:3000
```

---

## Datenbank: Supabase (PostgreSQL)

RunForge nutzt Supabase als produktive PostgreSQL-Datenbank (DB-Name: `postgres`, Supabase-Standard). Die JDBC-Treiber-Dependency (`org.postgresql:postgresql`) ist bereits in `backend/pom.xml` enthalten.

**Wichtig:** `SUPABASE_DB_URL` ist eine JDBC-URL (`jdbc:postgresql://...`), **nicht** die Projekt-/API-URL (`https://<ref>.supabase.co`). Den passenden Connection String findest du im Supabase-Dashboard unter **Connect → Session pooler** (funktioniert auch ohne IPv6). Beim Pooler lautet der Benutzer `postgres.<project-ref>`, das Passwort ist das Datenbank-Passwort (nicht der API-Key).

Verbindungsdaten werden ausschließlich über Umgebungsvariablen konfiguriert (siehe Abschnitt „Backend `application.properties`" oben sowie `backend/.env.example`). Es sind keine Zugangsdaten im Code oder in `application.properties` hinterlegt.

---

## Erweiterungen (Roadmap)

| Feature | Priorität | Beschreibung |
|---------|-----------|--------------|
| Authentifizierung | Hoch | Spring Security + JWT |
| Plan-Zuweisung UI | Hoch | Mitglied → Plan direkt im Frontend zuweisen |
| Workout-Tracking | Mittel | Einzelne Trainingseinheiten erfassen |
| E-Mail-Notifications | Mittel | Benachrichtigung bei Plan-Start/-Ende |
| PDF/CSV Export | Niedrig | Exportfunktion für Pläne und Statistiken |
| Docker Compose | Niedrig | Produktionsnahes Setup mit PostgreSQL |

---

*RunForge · © 2026 · Forge your run.*
