# SkillSwap

Skill-sharing platform featuring a Spring Boot backend and an Angular frontend. Users list teachable skills, get matched (directly or through a trust network), arrange exchanges, and leave reviews.

## Tech Stack
- **Backend:** Java 21, Spring Boot 3.3, Spring Data JPA, Spring Security, PostgreSQL, Maven
- **Frontend:** Angular, TypeScript, HTML5, CSS3
- **API Docs:** Swagger / OpenAPI
- **Version Control:** Git / GitHub

## Features

- **User accounts** — registration, login, profile management
- **Skills** — users can list skills they offer
- **Matching** — direct matches between users, plus indirect matches and 3-way exchange chains via a trust graph
- **Exchanges** — propose, complete, or cancel a skill exchange between users
- **Reviews** — leave and view reviews per user after an exchange
- **Trust graph** — computes "trust distance" between two users in the network

## Project Structure
```text
skillswap/
├── src/                    # Spring Boot Backend
├── frontend/               # Angular Frontend UI
├── pom.xml                 # Maven Configuration
└── README.md
```

## Getting Started / Local Run

### Prerequisites
- Java 21 & Maven installed
- Node.js (LTS) & Angular CLI installed (`npm install -g @angular/cli`)
- PostgreSQL installed and running locally (or accessible remotely)

### Step 1: Clone the repository
```bash
git clone https://github.com/ArchontiaSam/SkillSwap.git
cd SkillSwap
```

 ### Step 2: Set up the database

Create a PostgreSQL database for the project:

```sql
CREATE DATABASE skillswap;
```

Then open `src/main/resources/application.properties` (or `application.yml`) and set your own connection details:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/skillswap
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
spring.jpa.hibernate.ddl-auto=update
```

> `ddl-auto=update` lets Hibernate create/update the tables automatically on first run — no manual schema needed.

### Step 3: Run the backend (Spring Boot)

Open a terminal in the project root folder:

```bash
mvn spring-boot:run
```

The API will be available at **http://localhost:8080/api**, with Swagger UI at **http://localhost:8080/swagger-ui.html**.

### Step 4: Run the frontend (Angular)

Open a **second terminal**, navigate to the frontend folder, and start the app:

```bash
cd frontend
npm install
ng serve
```

The UI will be available at **http://localhost:4200**.

### Step 5: Open the app

Visit **http://localhost:4200** in your browser. The Angular app is pre-configured to call the backend at `http://localhost:8080/api` (see `frontend/src/app/services/api.service.ts` if you need to point it elsewhere).

> CORS is already configured on every backend controller to accept requests from `localhost:4200`, so no extra setup is needed for local development.
