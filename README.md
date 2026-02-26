# Clinic Management System

## Overview
This is a comprehensive Clinic Management System backend application built with **Spring Boot**. It provides APIs for managing patients, doctors, appointments, and other clinic-related operations. The system is designed to streamline clinic workflows and ensure efficient data management.

## Tech Stack
- **Language:** Java 17+
- **Framework:** Spring Boot 3.x
- **Database:** MySQL
- **Security:** Spring Security with JWT Authentication
- **Documentation:** OpenAPI / Swagger UI
- **Build Tool:** Maven

## Key Features

### 1. User Management
- **Authentication:** Secure login and registration using JWT.
- **Roles:** Support for different user roles (e.g., Admin, Doctor, Patient).

### 2. Patient Management
- **CRUD Operations:** Create, Read, Update, and Delete patient records.
- **Search:** Search patients by name or phone number.
- **Profile:** Manage patient personal details and medical history.

### 3. Doctor Management
- **Profiles:** Manage doctor information, specializations, and availability.
- **Schedules:** Manage doctor working hours and appointment slots.

### 4. Appointment System
- **Booking:** Patients can book appointments with doctors.
- **Status Tracking:** Track appointment status (Pending, Confirmed, Completed, Cancelled).
- **Validation:** comprehensive validation to prevent double-booking and ensure data integrity.

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 17 or higher
- Maven 3.x
- MySQL Database

### Installation

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd clinic
   ```

2. **Configure Database:**
   - Update `src/main/resources/application.properties` (or `application.yml`) with your database credentials.
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/clinic_db
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

3. **Build the Application:**
   ```bash
   ./mvnw clean install
   ```

4. **Run the Application:**
   ```bash
   ./mvnw spring-boot:run
   ```

## API Documentation
Once the application is running, you can access the Swagger UI documentation at:
```
http://localhost:8080/swagger-ui/index.html
```
(Note: Port may vary based on configuration)

## Contributing
Contributions are welcome! Please fork the repository and submit a pull request.

## License
This project is licensed under the MIT License.
