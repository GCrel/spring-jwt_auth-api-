# 👤 User Management API

API REST para registro, login y gestión de usuarios con roles y seguridad JWT.

## 🔐 Funcionalidades
- Registro de usuario (`/auth/register`)
- Login y obtención de JWT (`/auth/login`)
- CRUD de usuarios con control de acceso (solo admin)

## 🛡️ Seguridad
- Spring Security + JWT
- Cifrado de contraseñas con BCrypt
- Control de roles: ADMIN y USER

## 🚀 Tecnologías
- Java 17
- Spring Boot
- Spring Security
- JWT
- JPA + PostgreSQL
- Swagger (documentación de la API)

## ▶️ Cómo correr

```bash
./mvnw spring-boot:run -pl adapter
```