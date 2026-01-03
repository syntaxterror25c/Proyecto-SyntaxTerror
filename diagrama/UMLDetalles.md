# Sistema de Gestión de Gimnasio

## 1. Módulo de Suscripción y Contratación
Este bloque regula el derecho de acceso basado en el pago y el cupo de actividades.
* **Usuario (1) — (*) Suscripción**: Representa el historial de contratos del cliente. El sistema identifica la suscripción cuya vigencia temporal y estado permitan la operatividad en la fecha actual.
* **Plan_Precios (1) — (*) Suscripción**: Define las condiciones comerciales aplicadas a cada contrato. El atributo `limite_actividades` del plan determina cuántos registros se pueden crear en la tabla de selección.
* **Suscripción (1) — (*) Selección_Actividad**: Constituye la pieza clave para la restricción de acceso. Vincula el contrato mensual con las disciplinas específicas (ej. Yoga, Zumba) que el usuario ha decidido incluir en su cuota.
* **Tipo_Actividad (1) — (*) Selección_Actividad**: Permite validar si una disciplina concreta forma parte del catálogo de elecciones activas del abonado.

## 2. Configuración del Horario Fijo (El "Molde")
Define la estructura organizativa de las actividades antes de su ejecución.
* **Tipo_Actividad (1) — (*) Actividad_Configurada**: Desglosa una categoría general en horarios específicos. Una disciplina (ej. Pilates) puede tener múltiples configuraciones semanales.
* **Sala (1) — (*) Actividad_Configurada**: Asocia cada horario a un espacio físico, permitiendo la gestión de la capacidad máxima y evitando solapamientos espaciales.
* **Profesor (1) — (*) Actividad_Configurada (Titular)**: Asigna la responsabilidad docente de manera recurrente a un horario fijo preestablecido.

## 3. Gestión de Sesiones (Capa Operativa)
Representa la ejecución real del calendario y la gestión de imprevistos diarios.
* **Actividad_Configurada (1) — (*) Sesión**: Relación de generación automática. Cada registro de configuración se proyecta en el calendario para crear sesiones con fechas específicas.
* **Profesor (0..1) — (*) Sesión (Sustituto)**: Permite la gestión de incidencias. Se utiliza para asignar un docente distinto al titular en una fecha concreta sin modificar la configuración base.

## 4. Flujo de Reserva y Validación
Establece la interacción final entre el usuario y la oferta de clases disponibles.
* **Usuario (1) — (*) Reserva**: Documenta la intención de asistencia de una persona a una clase determinada y permite el rastreo de ausencias.
* **Sesión (1) — (*) Reserva**: Centraliza todas las peticiones para una fecha y hora dadas, facilitando el cálculo de plazas libres mediante el contraste con el aforo de la sala.

---

## Lógica de Verificación para la Reserva
Para que una reserva sea procesada con éxito, el sistema ejecuta de forma automática el siguiente flujo de comprobaciones:

1. **Estado de la Suscripción**: Se verifica que el **Usuario** posea una **Suscripción** en estado "ACTIVA" y cuya fecha de fin sea posterior a la fecha de la sesión.
2. **Validación de Cupo**: Se comprueba que el **Tipo_Actividad** vinculado a la **Sesión** esté presente en la tabla **Selección_Actividad** asociada a la suscripción del usuario.
3. **Disponibilidad de Aforo**: Se contrasta que el número de registros en la tabla **Reserva** para esa **Sesión** sea inferior a la capacidad máxima definida en la **Sala**.
4. **Estado de la Sesión**: Se confirma que el atributo `estado` de la **Sesión** no sea "CANCELADA", permitiendo así el bloqueo de reservas en días festivos.
   
---

## Gestión de Accesos y Roles

### ¿Cómo funcionará el acceso?
1. **Registro**: Cuando damos de alta a un socio, se le pide su email y le asignamos una contraseña (que se guarda cifrada en `password_hash`).
2. **Login**: El socio introduce su email y contraseña.
3. **Validación**: El sistema busca el email en la tabla `usuarios`. Si lo encuentra y la contraseña coincide, le permite ver sus sesiones y reservar.

### ¿Qué pasa con los Profesores y el Admin?
* **Profesores**: Son solo "nombres y teléfonos" que sirven para rellenar el horario. No necesitan contraseña porque no entrarán al sistema.
* **Admin**: El acceso del administrador se gestionará a nivel de servidor o mediante una herramienta externa (como pgAdmin o un panel de control propio) que no requiere estar mezclado con los datos de los clientes.


