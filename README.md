# 🏥 Clinic Management System

A **RESTful backend API** for managing clinic operations — patients, doctors, appointments, and prescriptions — built with **Spring Boot 3** and secured with **JWT Authentication**.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.x |
| Security | Spring Security + JWT |
| Database | MySQL + Spring Data JPA (Hibernate 6) |
| Documentation | OpenAPI 3 / Swagger UI |
| Build Tool | Maven |

---

## ✨ Key Features

- 🔐 **JWT Authentication** — Stateless login/register, token-based security
- 👥 **Role-based Access Control** — `ADMIN`, `DOCTOR`, `PATIENT` with `@PreAuthorize`
- 📅 **Appointment System** — Book, cancel, reschedule with conflict detection
- 📋 **Prescription Management** — Doctor creates prescriptions after completing appointments
- 🔍 **Search & Pagination** — Dynamic search by keyword, phone; paginated results
- ⚠️ **Global Exception Handling** — Centralized error response for all HTTP status codes
- 📄 **Swagger UI** — Full API documentation with examples

---

## 📁 Project Structure

```
src/main/java/vn/huy/clinic/
├── auth/               # Login, Register controllers
├── config/             # Security, JWT, CORS, OpenAPI config
├── controller/         # REST Controllers (Patient, Doctor, Appointment, Metadata)
├── dto/                # Request/Response DTOs
├── exception/          # GlobalExceptionHandler + custom exceptions
├── model/              # JPA Entities
├── repository/         # Spring Data JPA Repositories
├── service/            # Business logic (interface + impl)
└── token/              # JWT token management
```

---

## 🔌 API Endpoints

### 🔑 Auth
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/auth/register` | Đăng ký tài khoản |
| POST | `/api/v1/auth/login` | Đăng nhập, nhận JWT token |

### 👤 Patient
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/v1/patient` | ✅ | Danh sách bệnh nhân (phân trang) |
| GET | `/api/v1/patient/me` | PATIENT | Thông tin bệnh nhân đang login |
| GET | `/api/v1/patient/{id}` | ✅ | Chi tiết bệnh nhân |
| GET | `/api/v1/patient/search` | ✅ | Tìm kiếm theo tên/SĐT |
| PUT | `/api/v1/patient/{id}` | ✅ | Cập nhật thông tin |
| DELETE | `/api/v1/patient/{id}` | ✅ | Xóa bệnh nhân |
| POST | `/api/v1/patient/create-appointment` | PATIENT | Đặt lịch hẹn |

### 🩺 Doctor
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/v1/doctors` | ✅ | Tìm kiếm bác sĩ (phân trang) |
| GET | `/api/v1/doctors/{id}` | ✅ | Chi tiết bác sĩ |
| GET | `/api/v1/doctors/{id}/availability` | ✅ | Khung giờ còn trống |
| GET | `/api/v1/doctors/me` | DOCTOR | Thông tin bác sĩ đang login |
| POST | `/api/v1/doctors` | ADMIN | Tạo hồ sơ bác sĩ |
| PUT | `/api/v1/doctors` | DOCTOR | Cập nhật thông tin |
| PUT | `/api/v1/doctors/{id}/complete` | DOCTOR | Hoàn thành cuộc hẹn + kê đơn |

### 📅 Appointment
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/v1/appointments/{id}` | ✅ | Chi tiết cuộc hẹn |
| GET | `/api/v1/appointments/mine` | PATIENT | Bệnh nhân xem lịch của mình |
| PUT | `/api/v1/appointments/{id}/cancel` | PATIENT | Hủy lịch hẹn |
| PUT | `/api/v1/appointments/{id}/updateDatePatient` | PATIENT | Đổi lịch hẹn |
| GET | `/api/v1/appointments/doctor/schedule` | DOCTOR | Bác sĩ xem lịch của mình |

---

## 🚀 Getting Started

### Prerequisites
- JDK 21+
- Maven 3.9+
- MySQL 8.0+

### 1. Clone & Configure

```bash
git clone https://github.com/<your-username>/clinic.git
cd clinic
```

Cập nhật `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/clinic_db
spring.datasource.username=root
spring.datasource.password=your_password
jwt.secret=your_jwt_secret_key
```

### 2. Build & Run

```bash
# Chạy development
mvn spring-boot:run

# Hoặc build jar rồi chạy
mvn clean package -DskipTests
java -jar target/clinic-0.0.1-SNAPSHOT.jar
```

### 3. API Documentation

Sau khi chạy, truy cập Swagger UI:
```
http://localhost:8080/swagger-ui/index.html
```

---

## 🔒 Security Design

- **JWT Stateless** — không lưu session phía server
- **Password Encryption** — BCrypt hashing
- **CORS** — Cho phép `localhost:3000` và `localhost:9090`
- **Method Security** — `@PreAuthorize` phân quyền từng endpoint

---

## ⚠️ Error Response Format

```json
{
  "timestamp": "2026-02-27T08:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Bác sĩ đã kín lịch vào khung giờ này",
  "path": "/api/v1/patient/create-appointment"
}
```

Validation errors:
```json
{
  "status": 400,
  "error": "Bad Request",
  "validationErrors": {
    "email": ["must not be blank", "invalid email format"],
    "phone": ["must be 10 digits"]
  }
}
```
