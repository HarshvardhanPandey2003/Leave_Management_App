# 🚀 HR Management Suite

A comprehensive **HR Management System** built using Spring Boot, Next.js, PostgreSQL, and Docker.  
Supports employee management, leave tracking, role-based access control (RBAC), JWT authentication, unit testing, and CI/CD pipelines using GitHub Actions. Easily deployable with Docker Compose on AWS EC2.

---

## 📋 Table of Contents

- [✨ Features](#-features)
- [🛠️ Tech Stack](#️-tech-stack)
- [📐 Architecture](#-architecture)
- [⚙️ Prerequisites](#️-prerequisites)
- [💻 Local Setup](#-local-setup)
  - [1. Clone Repository](#1-clone-repository)
  - [2. Backend Setup](#2-backend-setup)
  - [3. Frontend Setup](#3-frontend-setup)
  - [4. Run with Docker Compose](#4-run-with-docker-compose)
- [✅ Running Tests](#-running-tests)
- [🚢 CI/CD](#-cicd)
- [☁️ Deployment](#-deployment)
- [📖 API Reference](#-api-reference)
- [🤝 Contributing](#-contributing)
- [📄 License](#-license)

---

## ✨ Features

- 📋 **Employee Management** and **Leave Tracking** modules
- 🔒 **JWT-based Authentication** for secure access
- 👥 **Role-Based Access Control** (Admin & Employee)
- 🧪 **Unit Testing** for services & controllers using JUnit 5 & Mockito
- 📦 **Dockerized** deployment with Docker Compose
- 🤖 **CI/CD** pipeline using GitHub Actions
- ☁️ **Deployed** on AWS EC2 instance

---

## 🛠️ Tech Stack

| Layer            | Technology                 |
| ---------------- | -------------------------- |
| Backend          | Java 17, Spring Boot       |
| Frontend         | Next.js, TypeScript, React |
| Database         | PostgreSQL                 |
| Testing          | JUnit 5, Mockito           |
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

````

---

## ⚙️ Prerequisites

- [Git](https://git-scm.com/)
- [Docker & Docker Compose](https://docs.docker.com/compose/)
- [Node.js (LTS)](https://nodejs.org/) & npm or yarn
- [Java 21 JDK](https://adoptium.net/)
- AWS account for EC2 hosting

---

## 💻 Local Setup

### 1. Clone Repository

```bash
git clone https://github.com/HarshvardhanPandey2003/leave-management-system.git
cd leave-management-system
````

### 2. Backend Setup

```bash
cd leave-management
```

* Configure environment variables in `application.yml` or export them:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/leave_db
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=yourpassword
export JWT_SECRET=YourJWTSecretKey
```

* Build & run the backend:

```bash
./mvnw clean package
java -jar target/leave-management-0.0.1-SNAPSHOT.jar
```

### 3. Frontend Setup

```bash
cd frontend
npm install
```

* Configure `.env.local`:

```
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

* Start the development server:

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

### Backend:

```bash
cd leave-management
./mvnw test
```

### Frontend:

```bash
cd frontend
npm test
```

---

## 🚢 CI/CD

GitHub Actions is used to:

1. **Build & Test** backend and frontend on each pull request
2. **Publish** Docker images to Docker Hub
3. **Deploy** to AWS EC2 via SSH and Docker Compose

See `.github/workflows/ci-cd.yml` for full details.

---

## ☁️ Deployment

1. Provision an EC2 instance (Ubuntu)
2. Install Docker & Docker Compose
3. Clone this repository on the instance
4. Set environment variables (via `.env` or export)
5. Run:

```bash
docker-compose up -d --build
```

6. Make sure the EC2 security group allows ports **80**, **443**, **3000**, and **8080**

---

## 📖 API Reference

| Endpoint             | Method | Description                        |
| -------------------- | ------ | ---------------------------------- |
| `/api/auth/login`    | POST   | Authenticate & retrieve JWT        |
| `/api/auth/register` | POST   | Register new user (Admin/Employee) |
| `/api/leaves`        | GET    | List leaves (RBAC-aware)           |
| `/api/leaves`        | POST   | Apply for leave                    |
| `/api/leaves/{id}`   | PUT    | Approve/Reject (Admin only)        |
| `/api/users/me`      | GET    | Get current user profile           |

For complete API documentation, refer to `documentation.txt` or visit `/swagger-ui.html`.

---

## 🤝 Contributing

1. Fork the repo
2. Create a feature branch: `git checkout -b feat/YourFeature`
3. Commit and push: `git push origin feat/YourFeature`
4. Open a Pull Request — reviews & 🎉!

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
Feel free to use, modify, and distribute!

```

Let me know if you want to auto-generate a `documentation.txt` file or Swagger/OpenAPI spec too!
```
