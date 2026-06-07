# RunForge — Gesamtarchitektur

> *Forge your run.* — Ein Trainingsplan-Management-Tool für runforgen  
> © 2026 RunForge

---

## Überblick

RunForge ist eine Web-Applikation zur Verwaltung von Mitgliedern, Trainingsplänen und Statistiken einer Laufgruppe. Die Architektur folgt dem klassischen dreischichtigen Modell: React-Frontend, Spring Boot Backend und H2-Datenbank (entwicklungsnah, einfach zu PostgreSQL migrierbar).

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
| Datenbank (dev) | H2 In-Memory | — |
| Datenbank (prod) | PostgreSQL | 15+ |

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
│       │   │   ├── RunningPlan.java
│       │   │   └── PlanAssignment.java
│       │   ├── repository/
│       │   │   ├── MemberRepository.java
│       │   │   ├── RunningPlanRepository.java
│       │   │   └── PlanAssignmentRepository.java
│       │   ├── service/
│       │   │   ├── MemberService.java
│       │   │   ├── RunningPlanService.java
│       │   │   └── StatisticsService.java
│       │   └── controller/
│       │       ├── MemberController.java
│       │       ├── RunningPlanController.java
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
│  │  Dashboard   │  │  Mitglieder  │  │ Laufpläne│  │
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
│              DATENBANK  ·  H2 In-Memory (dev)            │
│        members · training_plans · plan_assignments       │
│                  /h2-console (dev only)                  │
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

**RunningPlan** (Trainingsplan)

| Feld | Typ | Beschreibung |
|------|-----|--------------|
| id | Long | Primärschlüssel |
| name | String | Name des Plans |
| description | String | Beschreibung |
| level | Enum | Ziellevel |
| createdDate | LocalDate | Erstellungsdatum |

**PlanAssignment** (Zuweisung)

| Feld | Typ | Beschreibung |
|------|-----|--------------|
| id | Long | Primärschlüssel |
| member | Member | FK → Mitglied |
| RunningPlan | RunningPlan | FK → Plan |
| startDate | LocalDate | Startdatum |
| endDate | LocalDate | Enddatum |
| completed | boolean | Abgeschlossen? |

### Beziehungen

```
Member ──────────────────── PlanAssignment ──────────────────── RunningPlan
  1                               N : 1                               1
  └── Ein Mitglied kann           └── Zuweisung verbindet    ─────────┘
      mehrere Pläne haben             Mitglied und Plan
```

---

## REST API Endpunkte

### Mitglieder  `/ api/members`

| Methode | Pfad | Beschreibung |
|---------|------|--------------|
| GET | `/api/members` | Alle Mitglieder |
| GET | `/api/members/active` | Nur aktive Mitglieder |
| GET | `/api/members/{id}` | Einzelnes Mitglied |
| POST | `/api/members` | Neues Mitglied erstellen |
| PUT | `/api/members/{id}` | Mitglied bearbeiten |
| DELETE | `/api/members/{id}` | Soft Delete (active = false) |

### Laufpläne  `/api/plans`

| Methode | Pfad | Beschreibung |
|---------|------|--------------|
| GET | `/api/plans` | Alle Pläne |
| GET | `/api/plans/{id}` | Einzelner Plan |
| POST | `/api/plans` | Neuen Plan erstellen |
| PUT | `/api/plans/{id}` | Plan bearbeiten |
| DELETE | `/api/plans/{id}` | Plan löschen |

### Statistiken  `/api/stats`

| Methode | Pfad | Beschreibung |
|---------|------|--------------|
| GET | `/api/stats/dashboard` | Aggregierte Dashboard-Daten |

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

### Laufpläne (`/plans`)
- Karten-Grid mit Plan-Details (Dauer, km/Woche, Level)
- Modal-Formular: Erstellen / Bearbeiten
- Löschen per Icon

---

## Konfiguration

### Backend `application.properties`

```properties
spring.application.name=runforge-backend
server.port=8080
spring.application.name=runforge-backend
server.port=8080
spring.datasource.url=jdbc:h2:mem:runforgedb;DB_CLOSE_DELAY=-1
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.defer-datasource-initialization=true
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.sql.init.mode=always
spring.sql.init.data-locations=classpath:data.sql
```

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

```bash
# Backend
cd backend
./mvnw spring-boot:run
# → http://localhost:8080
# → http://localhost:8080/h2-console

# Frontend (neues Terminal)
cd frontend
npm install
npm run dev
# → http://localhost:3000
```

---

## Migration zu PostgreSQL

1. `pom.xml`: H2-Dependency durch PostgreSQL ersetzen:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

2. `application.properties` anpassen:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/runforge
spring.datasource.username=postgres
spring.datasource.password=deinPasswort
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=false
```

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
