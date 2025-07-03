# 🚀 Leave Management System

A full‑stack **Leave Management System** built with Spring Boot, Next.js, PostgreSQL, and Docker.
Role‑based access control (RBAC), JWT authentication, unit tests, GitHub Actions CI/CD, and automated Docker‑Compose deployment on AWS EC2.

---

## 📋 Table of Contents

* [✨ Features](#-features)
* [🛠️ Tech Stack](#️-tech-stack)
* [📐 Architecture](#-architecture)
* [⚙️ Prerequisites](#️-prerequisites)
* [💻 Local Setup](#-local-setup)

  * [1. Clone Repository](#1-clone-repository)
  * [2. Backend Setup](#2-backend-setup)
  * [3. Frontend Setup](#3-frontend-setup)
  * [4. Run with Docker Compose](#4-run-with-docker-compose)
* [✅ Running Tests](#-running-tests)
* [🚢 CI/CD](#-cicd)
* [☁️ Deployment](#-deployment)
* [📖 API Reference](#-api-reference)
* [🤝 Contributing](#-contributing)
* [📄 License](#-license)

---

## ✨ Features

* 🔒 **JWT‑based Authentication** for secure login & session management
* 👥 **Role‑Based Access Control for perfoming role specific actions** (Admin & Employee)
* 🧪 **Unit Testing** via JUnit 5 & Mockito
* 📦 **Dockerized** backend & frontend with Docker Compose
* 🤖 **CI/CD** pipeline using GitHub Actions
* ☁️ **Hosted** on AWS EC2

---

## 🛠️ Tech Stack

| Layer            | Technology                 |
| ---------------- | -------------------------- |
| Backend          | Java 17, Spring Boot       |
| Frontend         | Next.js, TypeScript, React |
| Database         | PostgreSQL                 |
| Testing          | JUnit 5, Mockito           |
| CI/CD            | GitHub Actions             |
| Containerization | Docker, Docker Compose     |
| Hosting          | AWS EC2                    |

---

## 📐 Architecture

```
┌─────────────┐       ┌───────────────┐
│  Next.js    │ ←──→  │  Spring Boot  │
│ (frontend)  │       │  REST API     │
└─────────────┘       └───────────────┘
       ▲                    ▲
       │                    │
       ▼                    ▼
┌─────────────┐       ┌─────────────────┐
│ PostgreSQL  │       │ Docker & CI/CD  │
│   (RDS)     │       │ (GitHub Actions)│
└─────────────┘       └─────────────────┘
```

---

## ⚙️ Prerequisites

* [Git](https://git-scm.com/)
* [Docker & Docker Compose](https://docs.docker.com/compose/)
* [Node.js (LTS)](https://nodejs.org/) & npm or yarn
* [Java 17 JDK](https://adoptium.net/)
* AWS account for EC2 hosting

---

## 💻 Local Setup

### 1. Clone Repository

```bash
git clone https://github.com/HarshvardhanPandey2003/leave-management-system.git
cd leave-management-system
```

### 2. Backend Setup

```bash
cd leave-management
```

* Configure environment variables in `application.yml` or export:

  ```bash
  export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/leave_db
  export SPRING_DATASOURCE_USERNAME=postgres
  export SPRING_DATASOURCE_PASSWORD=yourpassword
  export JWT_SECRET=YourJWTSecretKey
  ```
* Build & run:

  ```bash
  ./mvnw clean package
  java -jar target/leave-management-0.0.1-SNAPSHOT.jar
  ```

### 3. Frontend Setup

```bash
cd frontend
npm install      # or yarn install
```

* Configure `.env.local`:

  ```
  NEXT_PUBLIC_API_URL=http://localhost:8080/api
  ```
* Run development server:

  ```bash
  npm run dev
  ```

### 4. Run with Docker Compose

```bash
docker-compose up --build
```

* **Backend** → [http://localhost:8080](http://localhost:8080)
* **Frontend** → [http://localhost:3000](http://localhost:3000)

---

## ✅ Running Tests

* **Backend**:

  ```bash
  cd leave-management
  ./mvnw test
  ```
* **Frontend**:

  ```bash
  cd frontend
  npm test
  ```

---

## 🚢 CI/CD

We use GitHub Actions to:

1. **Build & Test** backend & frontend on every PR
2. **Publish** Docker images to Docker Hub
3. **Deploy** to AWS EC2 via SSH & Docker Compose

See `.github/workflows/ci-cd.yml` for details.

---

## ☁️ Deployment

1. Provision an EC2 (Ubuntu) instance
2. Install Docker & Docker Compose
3. Clone this repo on the instance
4. Set environment variables (via `.env` or export)
5. Run:

   ```bash
   docker-compose up -d --build
   ```
6. Ensure security groups allow ports **80**, **443**, **3000**, **8080**

---

## 📖 API Reference

| Endpoint             | Method | Description                        |
| -------------------- | ------ | ---------------------------------- |
| `/api/auth/login`    | POST   | Authenticate & retrieve JWT        |
| `/api/auth/register` | POST   | Register new user (Admin/Employee) |
| `/api/leaves`        | GET    | List leaves (RBAC‑aware)           |
| `/api/leaves`        | POST   | Apply for leave                    |
| `/api/leaves/{id}`   | PUT    | Approve/Reject (Admin only)        |
| `/api/users/me`      | GET    | Get current user profile           |

For full docs, see `documentation.txt` or Swagger UI at `/swagger-ui.html`.

---

## 🤝 Contributing

1. Fork the repo
2. Create a feature branch: `git checkout -b feat/YourFeature`
3. Commit & push: `git push origin feat/YourFeature`
4. Open a Pull Request — reviews & 🎉!

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
Feel free to use, modify, and distribute!
