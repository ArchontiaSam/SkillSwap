# SkillSwap

Full-stack application featuring a **Spring Boot** backend and an **Angular** frontend. Designed for skill-sharing and collaboration.

## Tech Stack
- **Backend:** Java, Spring Boot, REST APIs, Maven
- **Frontend:** Angular, TypeScript, HTML5, CSS3
- **Version Control:** Git / GitHub

## Project Structure
```text
skillswap/
├── src/                    # Spring Boot Backend
├── frontend/               # Angular Frontend UI
├── pom.xml                 # Maven Configuration
└── README.md
Getting Started / Local Run
Prerequisites
Java 17+ & Maven installed

Node.js (LTS) & Angular CLI installed (npm install -g @angular/cli)

Step 1: Clone the repository
Bash
git clone https://github.com/ArchontiaSam/SkillSwap.git
cd SkillSwap
Step 2: Run Backend (Spring Boot)
Open Terminal 1 in the root folder (C:/skillswap):

Bash
mvn spring-boot:run
(API will be available at http://localhost:8080)

Step 3: Run Frontend (Angular)
Open Terminal 2, navigate to the frontend folder, and start the app:

Bash
cd frontend
npm install
ng serve
(UI will be available at http://localhost:4200)

Open your browser at http://localhost:4200.