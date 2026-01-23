# VerdeGo
Full-Stack E-commerce con arquitectura desacoplada. Frontend reactivo (Vite + Bootstrap) y Backend RESTful (Java JAX-RS + MySQL). Incluye carrito dinámico (Kg/Ud), sistema de fidelización y pagos con PayPal.
# 🌿 VerdeGo - Fresh Market E-commerce

> **VerdeGo** es una plataforma de comercio electrónico moderna diseñada para la venta de productos frescos. Destaca por una arquitectura **SPA (Single Page Application)** totalmente desacoplada, donde un cliente ligero consume una **API RESTful** robusta.

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![JAX-RS](https://img.shields.io/badge/JAX--RS-Jersey-red?style=for-the-badge)](https://eclipse-ee4j.github.io/jersey/)
[![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Vite](https://img.shields.io/badge/Vite-B73BFE?style=for-the-badge&logo=vite&logoColor=white)](https://vitejs.dev/)
[![Bootstrap](https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white)](https://getbootstrap.com/)
[![PayPal](https://img.shields.io/badge/PayPal-00457C?style=for-the-badge&logo=paypal&logoColor=white)](https://developer.paypal.com/)

---

## 🏗️ Arquitectura del Sistema

El proyecto sigue un patrón de diseño **Cliente-Servidor desacoplado**:

### 🔌 Backend (Servidor)
* **Tecnología:** Java EE sobre Apache Tomcat.
* **API REST:** Implementada con **JAX-RS (Jersey)**. Expone endpoints JSON puros.
* **Persistencia:** Patrón **DAO** con **JDBC** para control total de las consultas SQL.
* **Seguridad:**
    * Hashing de contraseñas.
    * Filtrado de datos sensibles (password) en las respuestas JSON mediante limpieza de objetos.
    * Manejo de **Polimorfismo** en usuarios (Clientes vs Empleados) usando `Gson TypeAdapters`.

### 💻 Frontend (Cliente)
* **Tecnología:** JavaScript Vanilla moderno (ES6 Modules) empaquetado con **Vite**.
* **UI/UX:** **Bootstrap 5** con personalización CSS. Uso extensivo de *Modals* y *Offcanvas* para una experiencia fluida sin recargas.
* **Estado:** Gestión de sesión y carrito mediante `localStorage` y manipulación dinámica del DOM.

---

## ✨ Características Clave

### 🛒 Carrito Inteligente (Kg vs Unidades)
El sistema detecta automáticamente el tipo de venta del producto:
* **Por Unidad:** El selector suma de 1 en 1.
* **Por Peso (Kg):** El selector suma en intervalos de **0.25 kg**, permitiendo compras de precisión (ej: 1.75 kg de manzanas).

### 💳 Checkout & Pagos
* Integración completa con la **API de PayPal**.
* Gestión de direcciones de envío (CRUD completo) dentro del mismo flujo de compra.
* Validación de stock y cálculo de totales en tiempo real.

### 🎁 Sistema de Fidelización
* Gamificación integrada: Los usuarios ganan puntos con cada compra.
* Visualización de progreso para canje de cupones de descuento.

---

## 🗄️ Modelo de Datos (BBDD)

La base de datos MySQL implementa **Herencia de Tablas** para la gestión de usuarios, optimizando la integridad referencial:

* `usuarios` (Tabla padre): Credenciales, datos comunes.
* `clientes`: Datos específicos (Puntos, PayPal).
* `empleados`: Datos internos (Fecha alta, observaciones).
* `pedidos` & `detalles`: Histórico inmutable de compras.

---
## 🛠️ Instrucciones de Ejecución

Este proyecto consta de 3 partes:

1. **Base de Datos:**
   - Ejecutar el script `database.sql` en MySQL.
   - Revisar credenciales en `JdbcConnection.java`.

2. **Backend (API):**
   - Importar `VerdeGo_Server` en Eclipse/IntelliJ.
   - Desplegar en servidor Tomcat v9.0+.
   - URL Base: `http://localhost:8080/VerdeGo_Server/`

3. **Frontend (Cliente):**
   - Abrir terminal en `VerdeGo_Client`.
   - Ejecutar: `npm install`
   - Ejecutar: `npm run dev`
