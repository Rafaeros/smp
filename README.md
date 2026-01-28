# SMP — Production Monitoring System

**Spring Boot API + TCP Socket Server for IoT Production Monitoring**

This project is a backend system built with **Spring Boot** that acts as both a **REST API** and a **TCP socket server** to receive and process data from IoT devices in real time. It supports user authentication with roles, user-device relationships, and production order tracking. Devices (sensors) send logs over persistent TCP connections, and the server processes them concurrently using dedicated threads.

---

## 🚀 Features

- **REST API with Spring Boot**  
  Exposes endpoints for user management, device assignment, production orders, and logs.

- **TCP Socket Server for IoT Devices**  
  Listens on a defined port for incoming connections from production sensors. Each connection runs in a separate thread for scalability and responsiveness.

- **User Authentication & Authorization**  
  Users can have different roles (e.g., `ADMIN`, `USER`) with controlled access to resources.

- **Device Management**  
  Users can register and manage their own IoT devices.

- **Production Orders**  
  Users can create and update production orders.  
  Assigned sensors will count time and send production logs back to the API.

- **Real-time Monitoring Support**  
  Designed to support dashboards or real-time monitoring screens (frontend not included in this repository).

---

## 🧠 Architecture Overview

1. **Spring Boot Application**
   - Handles HTTP requests (REST API).
   - Manages users, roles, devices, production orders, and logs.
   - Implements persistence using JPA (e.g., with PostgreSQL/MySQL).

2. **TCP Socket Listener**
   - Listens for device connections on a dedicated TCP port.
   - Accepts incoming socket connections from IoT devices.
   - For each device connection, the server spawns a **thread** to handle communication independently.

3. **Threaded Device Handlers**
   - Each handler thread:
     - Reads production data from the connected sensor.
     - Parses messages according to the agreed protocol.
     - Stores or updates logs via API services.

4. **Security**
   - Authentication via Spring Security with roles.
   - JWT or session-based authentication (depending on implementation).

---

## 🛠️ Tech Stack

| Component | Technology |
|---------|------------|
| Language | Java |
| Framework | Spring Boot |
| Security | Spring Security |
| Persistence | JPA / Hibernate |
| TCP Networking | Java Sockets (multithreaded) |
| Build Tool | Maven |
| Database | PostgreSQL / MySQL |

---

## 📦 Getting Started

### 🔁 Clone the Project

```bash
git clone https://github.com/Rafaeros/smp.git
cd smp
```

### 📌 Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL or MySQL

### ⚙️ Configuration

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/smpdb
    username: your_user
    password: your_password

server:
  port: 8080

iot:
  tcp-port: 9000
```

### ▶️ Build & Run

```bash
mvn clean package
java -jar target/smp-0.0.1-SNAPSHOT.jar
```

---

## 📡 IoT Device Communication

- Devices connect via TCP to the configured port.
- Each device maintains a persistent socket connection.
- Messages may be sent in text or JSON format.
- Each connection is handled in its own thread.

Example payload:
```json
{
  "deviceId": "sensor-01",
  "orderId": "PO-123",
  "timestamp": "2026-01-28T12:00:00Z",
  "counter": 42
}
```

---

## 🧑‍💼 Users & Roles

- **ADMIN**: Full system access
- **USER**: Manage own devices and production orders

Example endpoints:

| Method | Endpoint | Description |
|------|----------|------------|
| POST | /auth/login | Authenticate |
| POST | /devices | Register device |
| POST | /orders | Create production order |
| GET | /logs | Retrieve production logs |

---

## 📊 Production Logs

Production logs received from devices include:

- Device identifier
- Production order
- Timestamp
- Counters / status

Logs are linked to:
- Device
- User
- Production order

---

## 📚 Contributing

1. Fork the repository  
2. Create a feature branch  
3. Commit your changes  
4. Open a Pull Request  

---