# Pruebas API Gestión de Usuarios

Nota: Se utilizó el terminal de Nushell con el comando `http` para probar el API.

---

## Registrar Usuario

### Registrar usuario con rol ADMIN

```sh
http post -t application/json http://localhost:8080/auth/register {
"username": "gcrel",
"email": "gcrel@email.com",
"password": "pass2",
"role": "ADMIN"
}
```

Respuesta:

```json
{
  "id": "ac3964bf-c4f9-45a5-ace8-0e0a24bbfb64",
  "username": "gcrel",
  "email": "gcrel@email.com",
  "password": "$2a$10$NJVHuFCjCLRtRL3oFpTNGuMWdeNwzEfRCYhxpVYrWmSyfxFs6YuJe",
  "role": "ADMIN"
}
```

---

### Registrar usuario con rol USER

```sh
http post -t application/json http://localhost:8080/auth/register {
"username": "user1",
"email": "user1@email.com",
"password": "pass3",
"role": "USER"
}
```

Respuesta:

```json
{
  "id": "265078ed-5404-40b2-8a6f-ef867e2e5f8c",
  "username": "user1",
  "email": "user1@email.com",
  "password": "$2a$10$OXcxciFmbRvwKqfbvDFauuAcqS1/z3Xen.vgAkG.PGALfiRkwUpzu",
  "role": "USER"
}
```

---

## Login User

### ADMIN Login

```sh
http post -t application/json http://localhost:8080/auth/login {
"email": "gcrel@email.com",
"password": "pass2"
}
```

Respuesta (JWT Token):

```
eyJhbGciOiJIUzM4NCJ9.eyJqdGkiOiJhYzM5NjRiZi1jNGY5LTQ1YTUtYWNlOC0wZTBhMjRiYmZiNjQiLCJ1c2VySWQiOiJhYzM5NjRiZi1jNGY5LTQ1YTUtYWNlOC0wZTBhMjRiYmZiNjQiLCJ1c2VybmFtZSI6ImdjcmVsIiwiZW1haWwiOiJnY3JlbEBlbWFpbC5jb20iLCJyb2xlIjoiQURNSU4iLCJzdWIiOiJnY3JlbEBlbWFpbC5jb20iLCJpYXQiOjE3NDg4MDQ5NzYsImV4cCI6MTc0ODgxMTAyNH0.u0v2BhqArKtSVy6qBmwfKBhfyw6g8RPnTTkMtkULN274OUqKY1KxqrX3XPy9Ebpo
```

### USER Login

```sh
http post -t application/json http://localhost:8080/auth/login {
"email": "user1@email.com",
"password": "pass3"
}
```

Respuesta (JWT Token):

```
eyJhbGciOiJIUzM4NCJ9.eyJqdGkiOiIyNjUwNzhlZC01NDA0LTQwYjItOGE2Zi1lZjg2N2UyZTVmOGMiLCJ1c2VySWQiOiIyNjUwNzhlZC01NDA0LTQwYjItOGE2Zi1lZjg2N2UyZTVmOGMiLCJ1c2VybmFtZSI6InVzZXIxIiwiZW1haWwiOiJ1c2VyMUBlbWFpbC5jb20iLCJyb2xlIjoiVVNFUiIsInN1YiI6InVzZXIxQGVtYWlsLmNvbSIsImlhdCI6MTc0ODgwNTA0MCwiZXhwIjoxNzQ4ODExMDg4fQ.3WRbbVbQwMdp1G_p-cgI45X0U4QM0y4OS7J4kzxmSQTD_Cg1Uk27VUJQvpd5NMfK
```

---

## Obtener Usuarios

### Obtener todos los usuarios (con token ADMIN)

```sh
http get http://localhost:8080/users -H [Authorization $"Bearer ($tokenAdmin)"] | to json
```

Respuesta:

```json
[
  {
    "id": "ac3964bf-c4f9-45a5-ace8-0e0a24bbfb64",
    "username": "gcrel",
    "email": "gcrel@email.com",
    "password": "$2a$10$NJVHuFCjCLRtRL3oFpTNGuMWdeNwzEfRCYhxpVYrWmSyfxFs6YuJe",
    "role": "ADMIN"
  },
  {
    "id": "265078ed-5404-40b2-8a6f-ef867e2e5f8c",
    "username": "user1",
    "email": "user1@email.com",
    "password": "$2a$10$OXcxciFmbRvwKqfbvDFauuAcqS1/z3Xen.vgAkG.PGALfiRkwUpzu",
    "role": "USER"
  }
]
```

### Obtener un usuario por ID (token ADMIN)

```sh
http get http://localhost:8080/users/ac3964bf-c4f9-45a5-ace8-0e0a24bbfb64 -H [Authorization $"Bearer ($tokenAdmin)"] | to json
```

Respuesta:

```json
{
  "id": "ac3964bf-c4f9-45a5-ace8-0e0a24bbfb64",
  "username": "gcrel",
  "email": "gcrel@email.com",
  "password": "$2a$10$NJVHuFCjCLRtRL3oFpTNGuMWdeNwzEfRCYhxpVYrWmSyfxFs6YuJe",
  "role": "ADMIN"
}
```

---

## Modificar Usuario

### Modificar usuario con rol USER (se actualiza y devuelve token)

```sh
http put -t application/json http://localhost:8080/users/265078ed-5404-40b2-8a6f-ef867e2e5f8c -H [Authorization $"Bearer ($tokenUser)"] {
"username": "CambioGC",
"email": "cbmGC@email.com",
"password": "123_pass"
}
```

Respuesta:

```json
{
  "user": {
    "id": "265078ed-5404-40b2-8a6f-ef867e2e5f8c",
    "username": "CambioGC",
    "email": "cbmGC@email.com",
    "password": "$2a$10$dM7By49stGyEWBbfia1C.Oy5MJwa/VmabwmQwdw9j2Fdaoqp4GJau",
    "role": "USER"
  },
  "jwtToken": "eyJhbGciOiJIUzM4NCJ9.eyJqdGkiOiIyNjUwNzhlZC01NDA0LTQwYjItOGE2Zi1lZjg2N2UyZTVmOGMiLCJ1c2VySWQiOiIyNjUwNzhlZC01NDA0LTQwYjItOGE2Zi1lZjg2N2UyZTVmOGMiLCJ1c2VybmFtZSI6IkNhbWJpb0dDIiwiZW1haWwiOiJjYm1HQ0BlbWFpbC5jb20iLCJyb2xlIjoiVVNFUiIsInN1YiI6ImNibUdDQGVtYWlsLmNvbSIsImlhdCI6MTc0ODgwNzUyMSwiZXhwIjoxNzQ4ODEzNTY5fQ.Ay8bwjkKH-NdS6V7AhHcwfAFkQVsX55CWkG9w9mkJRGZp2SpB7blR_BU-qI4m3K4"
}
```

---

## Delete User

### Eliminar usuario por ID (token ADMIN)

```sh
http delete http://localhost:8080/users/2f869ba8-69be-4dcd-9247-7779c5513f49 -H [Authorization $"Bearer ($tokenAdmin)"] | to json
```

Respuesta:

```json
{
  "id": "2f869ba8-69be-4dcd-9247-7779c5513f49",
  "username": "user2",
  "email": "user2@email.com",
  "password": "$2a$10$i0ukfPXzfrDFkDWwXCxY3OVjv9vkuR2nrTqw8cRm6g/XZm3sxYO7y",
  "role": "USER"
}
```

---

## Errores Probados

### Modificación usuario sin token o con token USER no autorizado (403)

```sh
http put -t application/json http://localhost:8080/users/265078ed-5404-40b2-8a6f-ef867e2e5f8c -H [Authorization $"Bearer ($tokenUser)"] {
"username": "CambioGC",
"email": "cbmGC@email.com",
"password": "123_pass"
}
```

Respuesta:

```
Access forbidden (403) to "http://localhost:8080/users/265078ed-5404-40b2-8a6f-ef867e2e5f8c"
```

---

### Intento modificar sin token

```sh
http put -t application/json http://localhost:8080/users/265078ed-5404-40b2-8a6f-ef867e2e5f8c {
"username": "CambioGC",
"email": "cbmGC@email.com",
"password": "123_pass"
}
```

Respuesta:

```
Access forbidden (403) to "http://localhost:8080/users/265078ed-5404-40b2-8a6f-ef867e2e5f8c"
```
