
|**RECOPILACIÓN DE INFORMACIÓN PARA EL PROYECTO**|
| :-: |

1) **Definición del alcance del proyecto**

   Desarrollar una solución tecnológica completa (aplicación de escritorio y APP móvil en Android) que permita la gestión automatizada y *self-service* de la ocupación y horarios de cualquier tipo de espacio o recurso (salas, equipos, *parkings*). 

   Inicialmente se desarrollará un proyecto mínimo viable centrado en un gimnasio, para posteriormente escalar según disponibilidad de tiempo.

   Intentamos satisfacer la necesidad del mercado TIC de digitalizar activos físicos, ofrecer movilidad al usuario final (reserva desde dispositivo móvil) y permitir a los administradores la gestión centralizada y la monetización de sus recursos (integración de pasarela de pagos).

2) **Herramientas para gestionar el proyecto**

   La gestión del proyecto se centrará en el uso de GitHub como herramienta principal, bajo una metodología de trabajo ágil y colaborativa.

   Se creará un repositorio en formato web, la cual usaremos no solo para gestionar las versiones del código, si no para definir cómo va evolucionando el proyecto. 

   Este espacio nos servirá para desarrollar tareas, dividirnos de forma eficiente todas las partes del trabajo, y como bitácora para poder compartir paso a paso como ha avanzado la aplicación

   Adicionalmente usaremos en principio:
    - Android Studio/Intellij como IDE
    - Java (API *Backend)*
    - App JavaFX (Frontend) 
    - Kotlin (para la App Móvil)

3) **Información organizativa**

   |**Rol**|**Tarea Principal**|**GitHub**|
   | :- | :- | :- |
   |**Project Manager**|Coordinador del equipo. |Gestión de hitos e *issues*|
   |**Secretario**|Documentación formal del proyecto|Actualización del documento de texto o HTML|
   |**Desarrollo *Frontend***|Interfaces de JavaFX y la App Móvil Android|Rama *frontend*|
   |**Desarrollo *Backend***|Modelaje BBDD, programación API|Rama *backend*|
   |**QA (*Quality Assurance*)**|Encargado de las pruebas (funcionales y no funcionales)|Abre y Cierra *Issues* de tipo *Bug*|
   |**DevOps**|Gestión de GitHub. Define las políticas de ramas y gestiona el despliegue de la API|Supervisa *Pull Requests* y *Master* Branch|

---

|**ESTUDIO DE VIABILIDAD TÉCNICA**|
| :-: |



1) **Requisitos técnicos del proyecto***
  
   **Hardware necesario.**
   
   Ordenadores para desarrollo y dispositivos Android para pruebas móviles. Servidor para alojar BBDD.Se cuenta con que será facilitado por el centro y en caso contrario se suscribirá un servicio en la nube.

   **Software y frameworks.**
   
   Intellij IDEA, Visual Studio Code, SceneBuilder, Lombok, Java, JavaFX, Kotlin, MySQL o PostgreSQL, Android SDK, JSON.

   **Compatibilidad con sistemas existentes.** 
   
   Sistema nuevo. No requiere compatibilidad. Se usará JSON para la API para facilitar integraciones futuras con pasarelas de pago o sistemas ERP.
   En principio sólo se requiere desarrollo escritorio y Android.



2) **Disponibilidad de tecnología**
   
   **¿Existen herramientas maduras para la solución?**
   
   Las herramientas seleccionadas son maduras y estables.

   **¿Hay soporte y actualizaciones garantizadas?**

   Hay una gran cantidad de soporte y de documentación que proviene de grandes empresas y comunidades Open Source.
  
3) **Escalabilidad y rendimiento**

   **¿Podrá crecer el sistema sin perder eficiencia?**
  
   La arquitectura API + BBDD central es muy escalable en caso de requerirse.
  
   **¿Cumple con los tiempos de respuesta esperados?**
  
   No se han definido, pero objetivos habituales son perfectamente alcanzables con esta tecnología. Se estima que cumpla con los estándares de apps similares.

4) **Seguridad y normativas**
   
   **Cumplimiento de RGPD, cifrado de datos, autenticación segura.**
   
   Se requiere protección de los datos personales sensibles en la BBDD y la implementación de políticas de consentimiento explícito en el login y registro.

5) **Riesgos técnicos**
   
   **Obsolescencia tecnológica.** 

   A corto plazo no hay ningún riesgo de obsolescencia en ninguna de las tecnologías elegidas.

   **Dependencia de proveedores externos.** 

   Únicamente del servicio en la nube que usaremos para alojar la BBDD en caso de que el centro no nos facilite un servidor.

   **Falta de personal especializado.**

   Puede llegar a ser un reto el implementar todo lo aprendido de forma eficiente en este proyecto debido a la inexperiencia del equipo. Dificultades para resolver problemas complejos que puedan surgir.

6) **Requisitos funcionales y no funcionales**

   **Listado de requisitos del proyecto**
   
    - **Requisitos funcionales**
      - Gestión de usuarios
        - Registro y edición de usuarios (clientes, monitores y administradores).
        - Inicio de sesión con autenticación segura.
        - Recuperación y cambio de contraseña.
        - Asignación de roles para limitar accesos.
      - Autenticación y autorización por roles
        - Cada tipo de usuario tendrá permisos distintos:
          - Administrador: gestión completa del sistema.
          - Monitor: gestión de clases y horarios.
          - Cliente: ver y gestionar sus reservas.
        - Protección de zonas de la app según el rol.
      - Gestión de reservas de máquinas, espacios y salas
        - Consultar disponibilidad de máquinas y salas por día y hora.
        - Crear, modificar y cancelar reservas.
        - Control de aforo y límites por tipo de recurso.
        - Notificaciones al usuario (confirmación, cancelación, cambios).
      - Búsqueda y filtrado
        - Búsqueda de espacios/salas por nombre, tipo, horario o disponibilidad.
        - Filtrado por categoría (musculación, cardio, etc.).
      - Proceso de reserva atómica (API)
        - Las reservas deben procesarse de forma atómica, evitando duplicidades o solapamientos.
        - El sistema debe bloquear temporalmente el recurso mientras se completa la operación.
        - Respuesta clara del API: éxito, error o reserva no disponible.
      - Gestión de pagos (API)
        - Procesar pagos de cuotas o actividades mediante una pasarela de pago.
        - Generar confirmación del pago y asociarlo a una reserva.
        - Registro histórico de pagos realizados por cada usuario.
      - Gestión de clases y actividades del gimnasio
        - Panel administrador
        - Crear clases (zumba, yoga, cycling, etc.) con horarios y monitor asignado.
        - Permitir al usuario reservar plaza en una clase.
        - Mostrar el número de plazas disponibles y ocupadas.
      - Gestión de máquinas, salas, clases, usuarios y monitores.
        - Visualización de métricas básicas (uso del gimnasio, horas reservadas/día, etc.).
    - **No funcionales**
        - Respuesta < 4 seg
        - Disponibilidad 99%
        - Seguridad https y cifrado
        - Escalabilidad (x reservas/día)
        - Usabilidad (interfaz gráfica)
        - Portabilidad
        - Mantenibilidad (control de versiones Git)
        - Sistema de copias de seguridad

7) **Análisis DAFO**
  
   **Fortalezas y debilidades del proyecto**

   |**Debilidades (D)**|**Amenazas (A)**|
   | :- | :- |
   |Complejidad del *Backend* (lógica para evitar el *overbooking* en tiempo real)|Limitación de tiempo finalización|
   |Pruebas y documentación|Caída del servidor cloud|
   |Integración multiplataforma (complejidad escritorio+Android)|Ciberseguridad y RGPD|
   |Curva de aprendizaje (p.ej. pasarela de pagos)|Cambios en versiones que puedan romper estabilidad|
   |**Fortalezas**|**Oportunidades**|
   |Viabilidad técnica (tecnologías maduras y estables)|Demanda de APPs para *self-service* |
   |Arquitectura sólida  (separación *frontend*, *backend*, API) BBDD escalable|Mercado en crecimiento (nicho sistemas de gestión de recursos)  SGR|
   |Aplicación multiplataforma|Mucha documentación accesible|

---

|**IDENTIFICACIÓN DE FASES Y ESTRUCTURA DEL PROYECTO**|
| :-: |

1) **Inicio**

   **Definición del alcance y objetivos.**

   El objetivo del proyecto es desarrollar una aplicación multiplataforma que sirva para la reserva de espacios o salas para distintos tipos de negocios, aunque de momento se entregará una adaptación para gimnasios.

   El usuario podrá ver qué espacios están libres u ocupados, elegir el espacio a reservar y el intervalo de tiempo. (Además podrá pagar directamente desde la app).

   **Elaboración del acta de constitución del proyecto.**

   Vamos a usar Github para alojar el acta de constitución del proyecto, que quedará también reflejada en la web, puesto que en ella se identifican los integrantes del equipo, los recursos que vamos a utilizar, los plazos del desarrollo y el propósito general del proyecto.

2) **Planificación**

   Cronograma. Secuenciación de actividades. Ordenar las actividades según la lógica del proyecto y las dependencias entre ellas.

   ![cronograma](https://github.com/user-attachments/assets/22a2b6e3-8e35-43df-a6b9-89c724e7e648)

3) **Ejecución**

   **Desarrollo del producto o servicio.**

   Durante la fase de ejecución se llevará a cabo el desarrollo completo del producto, siguiendo la planificación establecida en fases previas. Esto incluye la implementación de la lógica de negocio, el diseño y construcción de la interfaz utilizando JavaFX, la conexión con la base de datos PostgreSQL y la integración progresiva de todas las funcionalidades del sistema.

   El desarrollo se realizará de manera incremental, siguiendo una estructura de entregas periódicas que permitirán evaluar el avance, detectar errores y aplicar correcciones sin comprometer los plazos del proyecto, actualizando el repositorio Github.

   **Coordinación del equipo.**

   La coordinación entre los miembros del equipo será continua durante toda la fase de ejecución. Se emplearán reuniones periódicas para revisar el progreso, redistribuir tareas cuando sea necesario y resolver incidencias técnicas de forma conjunta.

   Al haber 6 roles y ser 3 personas, cada uno desarrollará dos roles durante aproximadamente 3 semanas. Se rotará cada semana 1 de esos roles según madurez de conocimientos en cada momento. El ajuste de días se hará de forma dinámica. 

4) **Control y seguimiento**

   **Gestión de cambios**

   Utilizaremos GitHub para control de versiones, gestión de cambios y tareas. Se establecerá un flujo de trabajo basado en ramas, que incluirá al menos:

    - **Principal**: Contiene la versión estable del proyecto.
    - **Desarrollo**: Funcionalidades nuevas para probar cambios antes de pasarlos a la rama principal.
      - <nombre\_módulo>: Desarrollo de desarrollos específicos.
      - <miembro\_equipo>: Notas y programas personales 
    - **Documentación**
      - Seguimiento proyecto: Logros, dificultades y soluciones
      - Manuales
    - **Extras**: Ideas sobre funcionalidades que mejoren la aplicación

5) **Cierre**

   **Entrega del producto final.**
      - Documentación del proyecto: acta de constitución, plan de proyecto, manuales técnicos, manuales de usuario, etc.
        Al finalizar el proyecto, se hará entrega del producto completo, al igual que de su documentación, manuales de usuario, bitácora etc

      - Evaluación y lecciones aprendidas.
        Se realizará un análisis del desarrollo del proyecto, evaluando:
         
         - El cumplimiento de los objetivos y requisitos definidos.
         - La calidad técnica del producto final.
         - La eficacia del trabajo en equipo y la coordinación de roles.
         - Las dificultades encontradas y las estrategias utilizadas para resolverlas.
         - Las lecciones aprendidas, que servirán para mejorar futuros proyectos y prácticas de desarrollo colaborativo.

      - Presentación del proyecto cuando finalicen los exámenes.

        Al finalizar los exámenes, se presentará la defensa del proyecto, en la que explicaremos los alcances y objetivos de la aplicación, demostraremos su funcionamiento y resumiremos la metodología de trabajo y la documentación elaborada
