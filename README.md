# Blog API

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-brightgreen)
![Spring Security](https://img.shields.io/badge/Security-Spring_Security-success)
![JWT](https://img.shields.io/badge/Auth-JWT-yellow)
![MySQL](https://img.shields.io/badge/Database-MySQL-blue)
![H2](https://img.shields.io/badge/Test_DB-H2-informational)
![JUnit5](https://img.shields.io/badge/Tests-JUnit5-red)
![Mockito](https://img.shields.io/badge/Mockito-Enabled-green)
![Swagger](https://img.shields.io/badge/OpenAPI-Swagger-85EA2D)
![Gradle](https://img.shields.io/badge/Build-Gradle-02303A)
![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED)

API REST desarrollada con Spring Boot para la gestión de usuarios, publicaciones, categorías y comentarios.

El proyecto implementa autenticación mediante JWT, autorización basada en roles (USER y ADMIN), validaciones, documentación con Swagger/OpenAPI, pruebas unitarias e integración y ejecución mediante Docker Compose.

## Características

- API REST siguiendo una arquitectura en capas.
- Autenticación mediante JWT.
- Autorización basada en roles.
- Contraseñas cifradas con BCrypt.
- Validación de datos con Jakarta Validation.
- Manejo global de excepciones.
- Documentación automática con Swagger/OpenAPI.
- Persistencia con Spring Data JPA.
- Tests unitarios con Mockito y MockMvc.
- Tests de integración con H2.
- Ejecución mediante Docker Compose.
- Base de datos MySQL.
- Datos de demostración para facilitar las pruebas.

## Tecnologías utilizadas

- Java 21
- Spring Boot 3.5
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- MySQL 9.3
- H2 Database (tests)
- JUnit 5
- Mockito
- MockMvc
- Swagger / OpenAPI
- Gradle
- Docker
- Docker Compose

## Funcionalidades

### Usuarios

- Registro de usuarios.
- Inicio de sesión.
- Contraseñas cifradas con BCrypt.
- Autenticación mediante JWT.

### Posts

- Crear posts.
- Editar posts.
- Eliminar posts.
- Obtener posts paginados.
- Buscar por filtros.

### Comentarios

- Agregar comentarios.
- Listar comentarios.
- Eliminar comentarios.

### Categorías

- Crear categorías.
- Obtener categorías.
- Editar categorías.
- Eliminar categorías.

### Seguridad

- Roles USER y ADMIN.
- Solo el autor puede modificar sus publicaciones.
- El administrador puede modificar cualquier publicación.
- Protección mediante JWT.
- Validación de permisos.

## Arquitectura

```text
                  HTTP Request
                       │
                       ▼
                Spring Controller
                       │
                       ▼
                 Service Layer
                       │
                       ▼
              Spring Data JPA
                       │
                       ▼
                    MySQL
```

## Diagrama

```text
Usuario (1)
│
├─────────────┐
│             │
▼             ▼
Post (N)   Comentario (N)
│
│ N
▼
Categoria (N)
```

## Modelo de datos

Usuario
   - id
   - nombre
   - apellido
   - email
   - password
   - prioridad
   - rol

Post
   - id
   - titulo
   - descripcion
   - usuario
   - categorias

Categoria
   - id
   - nombre

Comentario
   - id
   - comentario
   - usuario
   - post

## Seguridad

Todas las solicitudes protegidas deben incluir el siguiente encabezado:

```http
Authorization: Bearer <JWT>
```
### El flujo de autenticación es:
Registrar usuario
      │
      ▼
POST /auth/register
      │
      ▼
POST /auth/login
      │
      ▼
     JWT
      │
      ▼
Authorization: Bearer <token>
      │
      ▼
Consumir endpoints protegidos

## Documentación

Swagger disponible en
http://localhost:8080/swagger-ui/index.html
## Swagger
![Swagger](images/swaggerBlog.png)

## Cómo ejecutar el proyecto
### Ejecución con Docker
La forma recomendada de ejecutar el proyecto es mediante Docker Compose.

### Requisitos
- Docker
- Docker Compose

No es necesario generar previamente el .jar. El Dockerfile realiza el build de la aplicación durante la construcción de la imagen.

### Clonar
```bash
git clone https://github.com/Gaston11/blog-api.git
cd blog-api
``` 
### Levantar la aplicación
```bash
docker compose up --build
```
### Docker compose levantara:
- MySQL
- Spring Boot API
La api esta disponible en:
```bash
http://localhost:8080
```
La base de datos `cale` se crea automáticamente y Hibernate genera las tablas necesarias al iniciar la aplicación.
Usuario:
```bash
Email: demo@cale.com
Password: demo1234
```
También se crea:
- Una categoría de demostración.
- Un post de demostración.
- Un comentario de demostración.

Esto permite probar la API inmediatamente después de iniciar Docker Compose.

### Verificar los contenedores
```bash
docker compose ps 
```
Los servicios deberían aparecer como activos.
### Ver los logs de la API
```bash
docker compose logs blog-api
```
### Detener la aplicación
```bash
docker compose down
```

### Configuración
Las credenciales y configuraciones locales no se almacenan en el repositorio.

El proyecto incluye un archivo de ejemplo:
```bash
src/main/resources/application.properties.example
```
Este archivo sirve como referencia para crear una configuración local.

El archivo real:
```bash
src/main/resources/application.properties
```
está incluido en ```.gitignore``` y no debe subirse al repositorio si contiene credenciales. Se puede crear para editar de la siguiente forma:
```bash
cp src/main/resources/application.properties.example \
   src/main/resources/application.properties
```

### Variables de entorno
La aplicación permite configurar la conexión a MySQL mediante:
```bash
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
```
Docker Compose proporciona estas variables automáticamente.

## Ejecución local sin Docker
También es posible ejecutar la aplicación directamente mediante Gradle.

### Requisitos
- Java 21
- Gradle Wrapper incluido en el proyecto
Configurar previamente la conexión a MySQL en:
```bash
src/main/resources/application.properties
```
Luego ejecutar:
```bash
./gradlew bootRun
```
### Generar el JAR
```bash
./gradlew bootJar
```
El archivo generado estará en:
```bash
build/libs/
```
## Cobertura de pruebas

### Actualmente el proyecto incluye:

- Tests unitarios con Mockito
- Tests web con MockMvc
- Tests de integración con H2
- JaCoCo para cobertura.

### Se validan:

- Registro
- Login
- JWT
- CRUD de Posts
- CRUD de Categorías
- Comentarios
- Roles
- Permisos
- Validaciones

### Ejecutar tests
```bash
./gradlew test
```
Para generar el reporte de cobertura:
```bash
./gradlew jacocoTestReport
```
El reporte HTML se genera en:
```bash
build/reports/jacoco/test/html/index.html
```

### Endpoints principales

| Método | Endpoint               | Descripción        |
| ------ | ---------------------- | ------------------ |
| POST   | /auth/register         | Registrar usuario  |
| POST   | /auth/login            | Iniciar sesión     |
| GET    | /post                  | Obtener posts      |
| POST   | /post                  | Crear post         |
| PUT    | /post/{id}             | Modificar post     |
| DELETE | /post/{id}             | Eliminar post      |
| POST   | /post/{id}/comentarios | Agregar comentario |
| GET    | /categorias            | Listar categorías  |

### Ejemplo real
### POST /auth/register

```
Request

{
  "nombre":"Gaston",
  "apellido":"Perez",
  "email":"gaston@mail.com",
  "password":"123456",
  "prioridad":1
}

Response

201 Created
```
### POST /auth/login
Request:
```bash
{
  "email": "demo@cale.com",
  "password": "demo1234"
}
```
La respuesta devuelve un JWT que deberá utilizarse para acceder a los endpoints protegidos.

El usuario `demo@cale.com` ya está creado y listo para iniciar sesión.

### Ejemplo de flujo
Registrar usuario
```POST /auth/register```

↓

Login
```POST /auth/login```

↓

Obtener JWT

↓

Consumir la API
```Authorization: Bearer <token>```

### Funcionalidades implementadas
✅ JWT
✅ BCrypt
✅ Roles
✅ Swagger / OpenAPI
✅ Validaciones
✅ Manejo global de excepciones
✅ Tests unitarios
✅ Tests de integración
✅ JaCoCo (Cobertura de pruebas) 
✅ Paginación
✅ Comentarios
✅ Categorías
✅ MySQL
✅ Docker Compose
✅ Datos de demostración
✅ JaCoCo

### Próximas mejoras
- GitHub Actions (CI/CD)
- Logging con SLF4J
- Cache con Spring Cache
- Mejoras en auditoría de usuarios
- Despliegue en la nube

## Authors

- [@Gaston11](https://github.com/Gaston11)
