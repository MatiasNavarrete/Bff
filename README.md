# 🐾 Mascotas Bien - Plataforma de Reporte de Mascotas

Plataforma web orientada a la comunidad para reportar mascotas perdidas y encontradas. Este proyecto está construido bajo una **Arquitectura de Microservicios** utilizando Java/Spring Boot en el backend y React/Vite en el frontend, garantizando alta cohesión, bajo acoplamiento y resiliencia ante fallos.

## 🏗️ Arquitectura del Sistema

El sistema se compone de múltiples piezas independientes que se comunican entre sí:

* **Frontend (React + Vite):** Interfaz de usuario interactiva que consume los datos a través del BFF.
* **MS-Pet-BFF (Backend For Frontend):** Orquestador central. No posee base de datos propia. Recibe las peticiones del frontend y utiliza **Spring Cloud OpenFeign** para comunicarse con los microservicios internos. Implementa **Resilience4j (Circuit Breaker)** para proteger la aplicación si algún microservicio interno falla.
* **MS-Propietario:** Microservicio encargado de gestionar los datos de los usuarios/dueños. Utiliza base de datos **H2** en memoria para despliegue rápido.
* **MS-Mascotas:** Microservicio encargado de gestionar los reportes de mascotas. Persiste sus datos en una base de datos relacional **MySQL**.

## 🚀 Tecnologías y Herramientas

**Frontend:**
* React 18 + Vite
* React Router DOM (Navegación)
* Axios (Cliente HTTP)
* Vitest + React Testing Library (Testing con 100% de Cobertura)

**Backend:**
* Java 21 + Spring Boot 3.x
* Spring Data JPA & Hibernate
* Spring Cloud OpenFeign
* Resilience4j (Circuit Breaker)
* Swagger / OpenAPI 3 (Documentación de endpoints)
* JUnit 5 + Mockito + JaCoCo (Testing con 100% de Cobertura)

## 📋 Requisitos Previos

Para ejecutar este proyecto en un entorno local, necesitas tener instalado:
* [Java Development Kit (JDK) 21](https://adoptium.net/)
* [Node.js](https://nodejs.org/) (Versión 18 o superior)
* [XAMPP](https://www.apachefriends.org/) (Para el motor de MySQL)
* Maven (Opcional, los microservicios incluyen el wrapper `mvnw`)

## 🛠️ Instrucciones de Instalación y Ejecución

### 1. Base de Datos (MySQL)
1. Abre el panel de control de XAMPP.
2. Inicia los módulos de **Apache** y **MySQL**.
3. *Nota: La base de datos `sanos_y_salvos_db` se creará automáticamente gracias a la configuración de Hibernate en el microservicio.*

### 2. Levantar el Backend (Microservicios)
Es importante levantar primero los microservicios base y al final el orquestador (BFF). Desde tu IDE (IntelliJ IDEA, Eclipse, etc.), ejecuta las clases principales (`@SpringBootApplication`) en este orden:
1.  Levantar `ms-propietario` (Puerto 8080).
2.  Levantar `ms-mascotas` (Puerto 8081).
3.  Levantar `ms-pet-bff` (Puerto 8082).

Puedes revisar la documentación Swagger de cada microservicio ingresando a:
* `http://localhost:<PUERTO>/swagger-ui/index.html`

### 3. Levantar el Frontend
Abre una terminal en la carpeta raíz del frontend y ejecuta:
```bash
# Instalar dependencias
npm install

# Levantar servidor de desarrollo
npm run dev
