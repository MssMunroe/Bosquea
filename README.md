# 🌲 BOSQUEA: Exploración y Gestión de Parques Naturales de España

[![Python](https://img.shields.io/badge/Backend-Python_3.x-blue?style=flat-square&logo=python&logoColor=white)](https://www.python.org/)
[![Flask](https://img.shields.io/badge/Framework-Flask-black?style=flat-square&logo=flask&logoColor=white)](https://flask.palletsprojects.com/)
[![Kotlin](https://img.shields.io/badge/Mobile-Kotlin-purple?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![SQLite](https://img.shields.io/badge/Database-SQLite-003B57?style=flat-square&logo=sqlite&logoColor=white)](https://www.sqlite.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square)](https://opensource.org/licenses/MIT)

**Bosquea** es una plataforma integral diseñada para la exploración, gestión y fomento del turismo sostenible en los parques naturales de España. El sistema conecta a los amantes de la naturaleza con los gestores de los parques, permitiendo una comunicación fluida en tiempo real.

---

## 📖 Introducción
Bosquea facilita el acceso a información relevante sobre espacios protegidos, incluyendo rutas, servicios y avistamientos de fauna. Mientras que la **versión web** ofrece una consulta general, la **aplicación móvil** desbloquea la experiencia completa: registro de visitas, gestión de favoritos, comentarios y un sistema de avistamientos en tiempo real que desaparecen al finalizar el día.

---

## 🚀 Funcionalidades Principales

### 👤 Para Usuarios (App Móvil)
*   **Exploración:** Consulta detallada de parques y rutas disponibles.
*   **Gestión Personal:** Marcado de parques como "Favoritos" o "Visitados".
*   **Interacción Social:** Sistema de comentarios y feedback por parque.
*   **Avistamientos en Vivo:** Publicación de fauna vista durante la ruta (datos temporales).
*   **Seguridad:** Autenticación segura mediante **JWT (JSON Web Tokens)**.

### 👔 Para Gestores (Panel de Control)
*   **Gestión de Contenido:** CRUD completo de parques y rutas.
*   **Análisis de Afluencia:** Generación de informes detallados (Excel/XML) sobre popularidad y visitas.
*   **Control de Accesos:** Herramientas para la planificación de recursos y emergencias.

---

## 🛠️ Stack Tecnológico

*   **Backend:** Python con Flask & Flask-Login.
*   **Frontend Mobile:** Kotlin (Desarrollo nativo en Android Studio).
*   **Frontend Web:** HTML5 & CSS3.
*   **Base de Datos:** SQLite (Relacional).
*   **Diseño:** Figma (UX/UI enfocada en la calma y simplicidad).
*   **Herramientas:** Git, GitHub, VS Code y Postman.

---

## 🏗️ Arquitectura del Sistema

El sistema utiliza una arquitectura **Cliente-Servidor** comunicada mediante una API REST:

1.  **Clientes:** App Móvil (Kotlin) y Web (HTML/CSS).
2.  **Servidor:** API Flask que gestiona la lógica de negocio.
3.  **BBDD:** Almacenamiento persistente en SQLite.
4.  **Externos:** Integración con Google Maps API para geolocalización.

---

## 📁 Estructura del Repositorio

```bash
bosquea/
├── 📂 backend/           # API REST, modelos de datos y rutas
│   ├── models/           # Definición de tablas (Usuario, Parque, Ruta...)
│   └── routes/           # Endpoints de la API
├── 📂 frontend/
│   ├── 📱 mobile_app/    # Proyecto Android Studio (Kotlin)
│   └── 🌐 web/           # Landing page y consulta web
├── 📂 database/          # Scripts SQL y archivos de datos iniciales
├── 📂 docs/              # Documentación, diagramas ER y casos de uso
└── 📄 README.md          # Documentación principal
