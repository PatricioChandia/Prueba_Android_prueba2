📱 Descripción general

SmartConnect es una aplicación Android educativa desarrollada en Java con Android Studio, orientada a la demostración de intents implícitos y explícitos.
Permite interactuar con funciones comunes del sistema (llamadas, mensajes, ubicación, contactos, galería) y gestionar un perfil básico con datos persistentes.

⚙️ Detalles técnicos

  - Lenguaje: Java ☕
  - IDE: Android Studio
  - Versión de Android mínima: API 24 (Android 7.0 Nougat)
  - Versión objetivo: API 34 (Android 14)
  - Android Gradle Plugin: 8.5.0
  - Versión de la app: v1.0.0


🏗️ Estructura principal del proyecto

| Archivo / Clase       | Descripción                                                                    |
| --------------------- | ------------------------------------------------------------------------------ |
| `LoginActivity.java`  | Pantalla de inicio de sesión con autenticación tradicional y biométrica.       |
| `HomeActivity.java`   | Pantalla principal con botones para acceder a distintas funciones del sistema. |
| `PerfilActivity.java` | Vista editable del perfil de usuario con imagen, nombre, correo y edad.        |
| `activity_home.xml`   | Interfaz con botones de intents implícitos y explícitos.                       |
| `activity_perfil.xml` | Interfaz moderna con campos de texto e imagen de perfil.                       |


✨ Funcionalidades principales
🔐 Inicio de sesión

  - Permite ingresar con correo y contraseña.
  - Incluye autenticación biométrica (huella digital) para acceso rápido.
  - Datos de usuario enviados mediante Intent explícito hacia HomeActivity.

🏠 HomeActivity

Contiene accesos rápidos a funciones del sistema, implementadas con intents implícitos:
🚀 Intents implementados
🟦 Intents implícitos (5)

| Función                | Acción                 | URI / Extra                                           | Descripción                                                      | Pasos de prueba                                                                                          |
| ---------------------- | ---------------------- | ----------------------------------------------------- | ---------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------- |
| 📞 Llamar              | `Intent.ACTION_DIAL`   | `tel:`                                                | Abre el marcador telefónico con un número ingresado manualmente. | 1. Presiona el botón “Llamar”.<br>2. Ingresa el número.<br>3. Se abrirá el marcador con el número listo. |
| 💬 Enviar SMS          | `Intent.ACTION_SENDTO` | `smsto:` + texto predefinido                          | Abre la interfaz de mensajes con texto editable.                 | 1. Presiona “Enviar SMS”.<br>2. Se abrirá la app de Mensajes con texto predefinido.                      |
| 📇 Contactos           | `Intent.ACTION_PICK`   | `ContactsContract.Contacts.CONTENT_URI`               | Permite seleccionar un contacto del teléfono.                    | 1. Pulsa “Contactos”.<br>2. Elige un contacto.<br>3. Se muestra el número o nombre seleccionado.         |
| 📍 Ubicación           | `Intent.ACTION_VIEW`   | `geo:-33.4513,-70.6623?q=Santo+Tomas+Santiago+Centro` | Abre Google Maps en la ubicación de Santo Tomás Santiago Centro. | 1. Toca “Ubicación”.<br>2. Se abre Google Maps mostrando la dirección.                                   |
| 🖼️ Seleccionar imagen | `Intent.ACTION_PICK`   | `MediaStore.Images.Media.EXTERNAL_CONTENT_URI`        | Permite elegir una imagen de la galería para el perfil.          | 1. En Perfil, toca la imagen.<br>2. Selecciona una imagen de galería.<br>3. Se muestra en pantalla.      |



🟩 Intents explícitos (3)

| Origen → Destino                            | Descripción                                 | Pasos de prueba                                                                             |
| ------------------------------------------- | ------------------------------------------- | ------------------------------------------------------------------------------------------- |
| `LoginActivity → HomeActivity`              | Se ejecuta al iniciar sesión correctamente. | 1. Ingresa `estudiante@st.cl` y contraseña `123456`.<br>2. Se abre el Home.                 |
| `LoginActivity → HomeActivity` (biométrico) | Ingreso mediante huella digital.            | 1. Pulsa “Huella”.<br>2. Autentícate con tu dedo registrado.<br>3. Ingresa automáticamente. |
| `HomeActivity → PerfilActivity`             | Abre la vista de perfil del usuario.        | 1. Presiona el botón “Perfil”.<br>2. Se abre el perfil con datos editables.                 |


💾 Persistencia de datos

  - Los datos del perfil (nombre, edad, correo e imagen) se guardan usando SharedPreferences.
  - Al cerrar y reabrir la app, los datos permanecen.



📸 Capturas

![1](https://github.com/user-attachments/assets/8975bfd0-199c-447d-9103-ff794763326a)

![2](https://github.com/user-attachments/assets/9a2b7d78-4c04-42fe-aa4e-55a2c6c48c95)

![3](https://github.com/user-attachments/assets/f5ceb58c-06a6-4f0a-aea3-93d08afa8641)

![4](https://github.com/user-attachments/assets/21506bc4-ed5f-4cad-9a80-2dd700693d03)


Diagrama de Intents

Este diagrama representa el flujo de navegación de tu app, mostrando los Intents explícitos (dentro de la app) y los implícitos (que llaman a apps del sistema).

<img width="1668" height="650" alt="Intents" src="https://github.com/user-attachments/assets/b9edaede-aeec-4cf1-be6f-7d7c082bf06d" />



Diagrama de Flujo de Interacción del Usuario


Este diagrama muestra cómo el usuario navega por la app, los eventos clave (botones, intents, pantallas) y las respuestas del sistema o del dispositivo.

<img width="1098" height="576" alt="Flujo" src="https://github.com/user-attachments/assets/45ef2025-51f5-44ff-9516-c94640d06a93" />





💡 Posibles mejoras futuras

  - Implementar base de datos SQLite o Firebase.
  - Agregar modo oscuro 🌙.
  - Validar campos antes de guardar el perfil.
  - Conexión a servicios web externos.


📱 Descargar aplicación

👉 [Descargar APK desde la última versión] (https://github.com/PatricioChandia/Prueba_Android_prueba2/releases/tag/v1.0.0)


👨‍💻 Autor

  - Desarrollado por: Patricio Chandia Pino (🦆🍉)
  - 📧 pchandia1@alumnos.santotomas.cl
  - 📅 Versión del proyecto: Octubre 2025




