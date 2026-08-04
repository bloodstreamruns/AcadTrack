# AcadTrack

Aplicación móvil para Android que ayuda a estudiantes universitarios a organizar sus asignaturas y tareas académicas: registrarlas, darles seguimiento, y visualizar su progreso desde un panel central.

## Funcionalidades

- **Login** — inicio de sesión con validación de formato (correo y contraseña de 6+ caracteres).
- **Inicio** — dashboard con contadores (asignaturas, pendientes, atrasadas), un gráfico de dona de tareas pendientes por prioridad, buscador de tareas, y acceso al detalle de cada una (incluye notas y descripción).
- **Asignaturas** — CRUD completo (nombre, código, profesor, semestre), buscador, y barra de progreso de tareas completadas por asignatura. No permite eliminar una asignatura con tareas pendientes.
- **Tareas** — CRUD completo, vinculadas a una asignatura existente, con fecha de entrega, tipo, prioridad y buscador.
- **Perfil** — muestra el correo de la sesión activa y permite cerrar sesión.

Los datos de Asignaturas y Tareas se guardan en la nube (Firebase Firestore) y se sincronizan en tiempo real. La sesión se guarda en el dispositivo (DataStore) y se mantiene iniciada entre aperturas.

## Tecnologías

- **Kotlin** + **Jetpack Compose** + **Material 3**
- **Navigation Compose** — navegación entre pantallas
- **MVVM** + **Repository pattern**
- **Firebase Cloud Firestore** — persistencia remota de Asignaturas y Tareas, en tiempo real
- **DataStore (Preferences)** — persistencia local de la sesión
- **Kotlin Coroutines / Flow / StateFlow**

## Requisitos

- Android Studio (AGP 9.x)
- JDK 11+
- `compileSdk` 37 / `minSdk` 24
- Un proyecto de Firebase propio, con su archivo `google-services.json` colocado en `app/`

## Estructura del proyecto

```
app/src/main/java/com/example/acadtrack_beta/
├── MainActivity.kt
├── AcadTrackApplication.kt
├── data/
│   ├── model/            # Asignatura, Tarea
│   └── repository/       # TareaRepository (Firestore), SesionRepository (DataStore)
└── ui/
    ├── components/        # BarraBusqueda, GraficoDona, BarraProgreso
    ├── util/              # normalizarBusqueda, coincideConBusqueda
    └── screens/
        ├── login/
        ├── home/
        ├── asignaturas/
        ├── tareas/
        └── perfil/
```

## Instrucciones de uso

1. Clona el repositorio y ábrelo en Android Studio.
2. Crea un proyecto en [Firebase Console](https://console.firebase.google.com), registra una app Android con el paquete `com.example.acadtrack_beta`, y descarga tu propio `google-services.json` dentro de `app/`.
3. En Firestore Database, crea la base de datos en **modo de prueba** (el proyecto no usa Firebase Authentication todavía, así que las reglas de producción bloquearían la app).
4. Sincroniza Gradle y ejecuta la app en un emulador o dispositivo.

## Estado del proyecto

Proyecto académico en desarrollo. El login solo valida formato, no autentica contra un backend real; sin autenticación real, las reglas de seguridad de Firestore deben quedar en modo de prueba. Pendiente: autenticación real y pruebas automatizadas.

## Autor

Desarrollado como proyecto de la asignatura de Desarrollo Móvil.
