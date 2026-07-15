# AcadTrack

Aplicación móvil para Android que ayuda a estudiantes universitarios a organizar sus asignaturas y tareas académicas.

Permite registrar asignaturas, crear tareas vinculadas a ellas con fecha de entrega, tipo y prioridad, marcarlas como completadas, y consultar un panel de inicio con un resumen de pendientes y tareas atrasadas.

## Funcionalidades

- **Login** — inicio de sesión con validación de formato (correo y contraseña de 6+ caracteres).
- **Inicio** — dashboard con contadores de asignaturas, tareas pendientes y atrasadas, y lista de pendientes con acceso rápido al detalle de cada tarea.
- **Asignaturas** — CRUD completo (nombre, código, profesor, semestre). No permite eliminar una asignatura con tareas pendientes asociadas.
- **Tareas** — CRUD completo, vinculadas a una asignatura existente, con fecha de entrega, tipo (examen, tarea, proyecto, lectura, laboratorio u otro) y prioridad (alta, media, baja).
- **Perfil** — muestra el correo de la sesión activa y permite cerrar sesión.

Los datos de asignaturas y tareas, así como la sesión iniciada, se guardan en el dispositivo y sobreviven a cerrar la app o reiniciar el teléfono.

## Tecnologías

- **Kotlin**
- **Jetpack Compose** + **Material 3** — interfaz declarativa
- **Navigation Compose** — navegación entre pantallas
- **MVVM** + **Repository pattern** — arquitectura
- **Room** — persistencia de Asignaturas y Tareas
- **DataStore (Preferences)** — persistencia de la sesión
- **Kotlin Coroutines / Flow / StateFlow** — manejo de datos asíncronos y reactivos
- **KSP** — procesamiento de anotaciones de Room

## Requisitos

- Android Studio (con soporte para AGP 9.x)
- JDK 11+
- `compileSdk` 37 / `minSdk` 24

## Estructura del proyecto

```
app/src/main/java/com/example/acadtrack_beta/
├── MainActivity.kt
├── AcadTrackApplication.kt
├── data/
│   ├── model/            # Asignatura, Tarea (entidades de Room)
│   ├── local/            # AppDatabase, DAOs, Converters
│   └── repository/       # TareaRepository, SesionRepository
└── ui/screens/
    ├── login/
    ├── home/
    ├── asignaturas/
    ├── tareas/
    └── perfil/
```

## Estado del proyecto

Proyecto académico en desarrollo. El login actualmente solo valida formato, no autentica contra un backend real. Pendiente: filtros y búsqueda de tareas, autenticación real, y pruebas automatizadas, implementación de notificaciones push para basadas en el nivel de prioridad de cada tarea y la cercanía de su fecha de entrega.

## Autor

Desarrollado como proyecto de la asignatura de Programación Móvil por Ashley Merlo y Daniel Sánchez.
