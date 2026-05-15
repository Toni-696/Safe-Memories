# SafeMemories - Backend API

# Índice

1. Introducción
2. Objetivo del proyecto
3. Tecnologías utilizadas
4. Librerías implementadas
5. Arquitectura del proyecto
6. Seguridad JWT
7. Configuración principal
8. Entidades principales
9. DTOs utilizados
10. Repositorios
11. Servicios principales
12. Flujo completo de funcionamiento
13. Gestión de archivos multimedia
14. Sistema de permisos
15. Solicitudes de descarga
16. Manejo de errores
17. Validaciones implementadas
18. Endpoints principales
19. Problemas resueltos durante el desarrollo
20. Estado actual del backend
21. Posibles mejoras futuras

---

# 1. Introducción

SafeMemories es una API REST desarrollada con Spring Boot cuyo objetivo es permitir almacenar, organizar, compartir y gestionar imágenes y vídeos de forma privada y segura.

El sistema está pensado para funcionar junto a un frontend desarrollado en React, aunque puede ser consumido desde cualquier cliente compatible con HTTP y JSON.

La aplicación implementa autenticación mediante JWT, control de permisos por usuario y un sistema avanzado de solicitudes de descarga.

---

# 2. Objetivo del proyecto

El objetivo principal del proyecto es permitir que un usuario pueda compartir recuerdos multimedia con otros usuarios sin perder el control sobre las descargas.

La idea principal es:

- Un usuario puede compartir carpetas para visualizar contenido.
- Los usuarios autorizados pueden ver imágenes y vídeos.
- Para descargar archivos, deben solicitar permiso explícitamente.
- El propietario puede aceptar o rechazar cada solicitud.

Esto permite diferenciar claramente:

```text
Visualizar contenido ≠ Descargar contenido
```

---

# 3. Tecnologías utilizadas

## Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA
- Hibernate
- JWT (jjwt)
- Lombok
- Maven

## Base de datos

- MySQL
- MariaDB
- phpMyAdmin

## Frontend conectado

- React
- Vite

---

# 4. Librerías implementadas

# Spring Web

Spring Web se utiliza para crear la API REST y gestionar las peticiones HTTP.

Permite:

- Crear controladores REST
- Gestionar endpoints
- Procesar peticiones y respuestas
- Recibir archivos mediante MultipartFile

Ejemplo:

```java
@RestController
@RequestMapping("/usuarios")
```

También se utilizan anotaciones como:

```java
@GetMapping
@PostMapping
@PutMapping
@DeleteMapping
@RequestBody
@PathVariable
@RequestParam
```

---

# Spring Security

Spring Security se utiliza para proteger la aplicación.

Funciones principales:

- Restringir endpoints
- Validar JWT
- Obtener usuario autenticado
- Configurar filtros de autenticación

Ejemplo:

```java
http
    .csrf(csrf -> csrf.disable())
    .authorizeHttpRequests(auth -> auth
        .requestMatchers("/usuarios/login").permitAll()
        .anyRequest().authenticated()
    );
```

---

# JWT (jjwt)

La librería jjwt se utiliza para generar y validar tokens JWT.

Funciones principales:

- Crear tokens
- Extraer información
- Validar autenticidad
- Controlar expiración

Ejemplo:

```java
Jwts.builder()
```

El email del usuario se almacena como subject del token.

---

# Spring Data JPA

Spring Data JPA permite trabajar con la base de datos usando repositorios y entidades.

Ventajas:

- Menos SQL manual
- Relaciones automáticas
- CRUD automático
- Consultas derivadas

Ejemplo:

```java
public interface UsuarioRepository extends JpaRepository<Usuario, Long>
```

---

# Hibernate

Hibernate es el ORM utilizado por JPA.

Se encarga de:

- Convertir objetos Java en registros SQL
- Gestionar relaciones
- Persistir datos
- Cargar entidades automáticamente

Ejemplo:

```java
@OneToMany
@ManyToOne
@ManyToMany
```

---

# Lombok

Lombok reduce código repetitivo.

Anotaciones utilizadas:

```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
```

Gracias a Lombok no es necesario escribir manualmente:

- getters
- setters
- constructores
- builders

---

# BCryptPasswordEncoder

Se utiliza para cifrar contraseñas.

Importancia:

- Las contraseñas nunca se guardan en texto plano.
- Se almacenan hashes seguros.

Ejemplo:

```java
passwordEncoder.encode(password)
```

---

# MultipartFile

Se utiliza para subir archivos multimedia.

Permite recibir:

- imágenes
- vídeos

Ejemplo:

```java
MultipartFile archivo
```

---

# 5. Arquitectura del proyecto

El backend está organizado mediante arquitectura por capas:

```text
controller
service
repository
entity
dto
security
config
exception
```

---

# Controller

Contiene los endpoints REST.

Responsabilidad:

- recibir peticiones HTTP
- devolver respuestas JSON

Ejemplo:

```java
UsuarioController
ArchivoController
CarpetaController
SolicitudDescargaController
```

---

# Service

Contiene la lógica de negocio.

Responsabilidad:

- validar datos
- aplicar reglas
- controlar permisos
- ejecutar operaciones complejas

Ejemplo:

```java
UsuarioService
ArchivoService
CarpetaService
SolicitudDescargaService
```

---

# Repository

Gestiona acceso a base de datos.

Responsabilidad:

- consultas SQL automáticas
- búsqueda de entidades
- persistencia

Ejemplo:

```java
UsuarioRepository
ArchivoRepository
```

---

# Entity

Representan tablas de base de datos.

Ejemplo:

```java
Usuario
Archivo
Carpeta
```

---

# DTO

Objetos de transferencia de datos.

Se utilizan para:

- no exponer entidades completas
- controlar datos enviados/recibidos

---

# Security

Contiene la lógica JWT y filtros de seguridad.

---

# Config

Configuraciones generales de la aplicación.

---

# Exception

Manejo global de errores.

---

# 6. Seguridad JWT

La aplicación utiliza autenticación basada en JWT.

Funcionamiento:

1. Usuario hace login
2. Backend genera token JWT
3. Frontend guarda token
4. Token se envía en Authorization Bearer
5. Backend valida token en cada petición

---

# JwtService

Clase encargada de gestionar JWT.

## Métodos importantes

### generateToken()

Genera un token JWT para un usuario.

### extractUsername()

Extrae el email del token.

### isTokenValid()

Comprueba si el token es válido.

---

# JwtFilter

Filtro que intercepta todas las peticiones.

Funcionamiento:

1. Lee Authorization
2. Comprueba Bearer Token
3. Extrae JWT
4. Valida token
5. Autentica usuario

---

# JwtAuthenticationEntryPoint

Devuelve respuestas personalizadas cuando un usuario no está autenticado.

Ejemplo:

```json
{
  "error": "No autorizado"
}
```

---

# 7. Configuración principal

# SecurityConfig

Clase encargada de configurar Spring Security.

Responsabilidades:

- Configurar JWT
- Configurar CORS
- Configurar rutas públicas
- Registrar filtros

Endpoints públicos:

```text
/usuarios/login
/usuarios/registro
```

Todos los demás requieren token JWT.

---

# 8. Entidades principales

# Usuario

Representa un usuario registrado.

## Campos

```text
id
nombre
email
password
fechaRegistro
```

## Relaciones

- Un usuario puede tener muchas carpetas
- Un usuario puede tener muchos archivos
- Un usuario puede crear solicitudes
- Un usuario puede recibir permisos

---

# Carpeta

Representa una carpeta de archivos.

## Campos

```text
id
nombre
fechaCreacion
usuario
```

---

# Archivo

Representa un archivo multimedia.

## Campos

```text
id
nombreOriginal
nombreGuardado
ruta
tipo
tamano
usuario
carpeta
```

---

## nombreOriginal

Nombre visible para el usuario.

Ejemplo:

```text
vacaciones.jpg
```

---

## nombreGuardado

Nombre interno seguro generado mediante UUID.

Ejemplo:

```text
550e8400-e29b.jpg
```

Esto evita:

- nombres duplicados
- problemas de seguridad

---

# PermisoCarpeta

Permite visualizar una carpeta compartida.

## Campos

```text
id
carpeta
usuarioAutorizado
```

---

# PermisoDescarga

Permite descargar un archivo concreto.

## Campos

```text
id
archivo
usuarioAutorizado
```

---

# SolicitudDescarga

Representa solicitudes de descarga.

## Campos

```text
id
estado
fechaSolicitud
usuarioSolicitante
usuarioPropietario
archivos
```

---

# Estados posibles

```java
PENDIENTE
ACEPTADA
RECHAZADA
```

---

# Relación ManyToMany

Una solicitud puede contener varios archivos.

```java
@ManyToMany
private List<Archivo> archivos;
```

---

# 9. DTOs utilizados

# LoginRequest

```text
email
password
```

---

# LoginResponse

```text
token
id
nombre
email
```

---

# CarpetaRequest

Se usa para:

- crear carpeta
- renombrar carpeta

---

# ArchivoResponse

Devuelve información pública del archivo.

---

# SolicitudDescargaRequest

Permite solicitar varios archivos.

Ejemplo:

```json
{
  "archivosIds": [1,2,3]
}
```

---

# 10. Repositorios

# UsuarioRepository

Método importante:

```java
findByEmail()
```

---

# CarpetaRepository

Métodos importantes:

```java
findByUsuario()
existsByNombreAndUsuario()
```

---

# ArchivoRepository

Métodos importantes:

```java
findByUsuario()
findByCarpeta()
```

---

# PermisoCarpetaRepository

Métodos importantes:

```java
existsByCarpetaAndUsuarioAutorizado()
findByUsuarioAutorizado()
```

---

# PermisoDescargaRepository

Controla permisos individuales.

---

# SolicitudDescargaRepository

Incluye consultas con:

```java
JOIN FETCH
```

para evitar errores Lazy Loading.

---

# 11. Servicios principales

# UsuarioService

Responsabilidades:

- registro
- login
- actualización de perfil
- cambio de contraseña

## Métodos importantes

### registrarUsuario()

- valida email
- cifra contraseña

### login()

- valida credenciales
- genera JWT

### actualizarPerfil()

Permite cambiar nombre.

### cambiarPassword()

Valida contraseña actual antes de actualizar.

---

# CarpetaService

Responsabilidades:

- crear carpeta
- borrar carpeta
- renombrar carpeta
- compartir carpeta

## Métodos importantes

### crearCarpeta()

Crea carpeta asociada al usuario.

### compartirCarpeta()

Crea un PermisoCarpeta.

### borrarCarpeta()

Desasocia archivos antes de borrar.

---

# ArchivoService

Responsabilidades:

- subir archivos
- visualizar archivos
- descargar archivos
- borrar archivos
- mover archivos
- compartir archivos

---

# Métodos importantes

## subirArchivo()

- valida tipo
- valida tamaño
- genera UUID
- guarda archivo físicamente

---

## obtenerArchivoParaVisualizar()

Controla quién puede ver archivos.

---

## descargarArchivo()

Controla permisos de descarga.

---

## borrarArchivo()

Antes de borrar:

1. elimina permisos
2. elimina relaciones
3. borra archivo físico
4. borra BD

---

## moverArchivo()

Permite mover archivos entre carpetas.

---

# SolicitudDescargaService

Implementa la lógica principal.

---

## crearSolicitud()

Valida:

- carpeta compartida
- propietario
- permisos

Crea solicitud PENDIENTE.

---

## aceptarSolicitud()

- cambia estado
- crea permisos descarga

---

## rechazarSolicitud()

Marca solicitud como RECHAZADA.

---

# 12. Flujo completo de funcionamiento

## Toni

1. crea carpeta
2. sube imágenes
3. comparte carpeta

---

## Ana

1. ve carpeta compartida
2. selecciona imágenes
3. solicita descarga

---

## Toni

1. revisa solicitudes
2. acepta o rechaza

---

## Ana

1. ve estado
2. descarga imágenes aceptadas

---

# 13. Gestión de archivos multimedia

Los archivos se almacenan físicamente en:

```text
/uploads
```

---

# Validaciones implementadas

## Tamaño máximo

```text
10MB
```

---

## Tipos permitidos

- JPG
- PNG
- MP4

---

# 14. Sistema de permisos

# Visualizar

Controlado mediante:

```text
PermisoCarpeta
```

---

# Descargar

Controlado mediante:

```text
PermisoDescarga
```

---

# 15. Solicitudes de descarga

Funcionamiento:

1. Usuario selecciona archivos
2. Envía solicitud
3. Propietario acepta/rechaza
4. Si acepta:
    - se crean permisos descarga

---

# 16. Manejo de errores

Se utiliza:

```java
@RestControllerAdvice
```

---

# Errores controlados

- token inválido
- usuario no autorizado
- archivo demasiado grande
- recursos inexistentes
- permisos insuficientes

---

# 17. Validaciones implementadas

## Usuarios

- email único
- contraseña cifrada

---

## Archivos

- tamaño máximo
- tipos permitidos

---

## Carpetas

- nombres duplicados
- propiedad del usuario

---

# 18. Endpoints principales

# Usuarios

```text
POST /usuarios/registro
POST /usuarios/login
PUT /usuarios/perfil
PUT /usuarios/password
```

---

# Carpetas

```text
POST /carpetas
GET /carpetas/mis-carpetas
PUT /carpetas/{id}
DELETE /carpetas/{id}
POST /carpetas/{id}/compartir
GET /carpetas/compartidas
```

---

# Archivos

```text
POST /archivos/subir
GET /archivos/ver/{id}
GET /archivos/descargar/{id}
DELETE /archivos/{id}
PUT /archivos/{id}
PUT /archivos/{id}/mover
```

---

# Solicitudes

```text
POST /solicitudes-descarga
GET /solicitudes-descarga/recibidas
GET /solicitudes-descarga/enviadas
PUT /solicitudes-descarga/{id}/aceptar
PUT /solicitudes-descarga/{id}/rechazar
```

---

# 19. Problemas resueltos durante el desarrollo

# Lazy Loading Hibernate

Problema:

```text
failed to lazily initialize a collection
```

Solución:

```java
JOIN FETCH
```

---

# Claves foráneas al borrar archivos

Problema:

- permisos seguían apuntando al archivo

Solución:

- borrar relaciones antes

---

# CORS React + Spring

Configurado en SecurityConfig.

---

# 20. Estado actual del backend

Actualmente soporta:

- autenticación JWT
- usuarios
- carpetas
- archivos multimedia
- permisos
- compartir contenido
- solicitudes descarga
- validaciones
- almacenamiento físico seguro

---

# 21. Posibles mejoras futuras

- Docker
- Swagger/OpenAPI
- AWS S3
- Notificaciones
- Roles
- Miniaturas
- Logs de auditoría
- Expiración de permisos
- Compartir enlaces públicos
- Papelera de reciclaje
- Paginación
- Compresión de imágenes

---