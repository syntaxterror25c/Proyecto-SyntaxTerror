1. **Módulo de Suscripción y Contratación**
   
   Este bloque regula el derecho de acceso basado en el pago y el cupo de actividades.  
   - __Usuario (1) — (\*) Suscripción__: Representa el historial de contratos del cliente. El sistema identifica la suscripción cuya vigencia temporal y estado permitan la operatividad en la fecha actual.  
   - __Plan_Precios (1) — (\*) Suscripción__: Define las condiciones comerciales aplicadas a cada contrato. El atributo `limite_actividades` del plan determina cuántos registros se pueden crear en la tabla de selección.  
   - __Suscripción (1) — (\*) Selección_Actividad__: Constituye la pieza clave para la restricción de acceso. Vincula el contrato mensual con las disciplinas específicas (ej. Yoga, Zumba) que el usuario ha decidido incluir en su cuota.  
   - __Tipo_Actividad (1) — (\*) Selección_Actividad__: Permite validar si una disciplina concreta forma parte del catálogo de elecciones activas del abonado.  

2. **Configuración del Horario Fijo (El "Molde")**

   Define la estructura organizativa de las actividades antes de su ejecución.
   - __Tipo_Actividad (1) — (\*) Actividad_Configurada__: Desglosa una categoría general en horarios específicos. Una disciplina (ej. Pilates) puede tener múltiples configuraciones semanales.  
   - __Sala (1) — (\*) Actividad_Configurada__: Asocia cada horario a un espacio físico, permitiendo la gestión de la capacidad máxima y evitando solapamientos espaciales.  
   - __Profesor (1) — (\*) Actividad_Configurada (Titular)__: Asigna la responsabilidad docente de manera recurrente a un horario fijo preestablecido.  

3. **Gestión de Sesiones (Capa Operativa)**

   Representa la ejecución real del calendario y la gestión de imprevistos diarios.
   - __Actividad_Configurada (1) — (\*) Sesión__: Relación de generación automática. Cada registro de configuración se proyecta en el calendario para crear sesiones con fechas específicas.  
   - __Profesor (0..1) — (\*) Sesión (Sustituto)__: Permite la gestión de incidencias. Se utiliza para asignar un docente distinto al titular en una fecha concreta sin modificar la configuración base.  

4. **Flujo de Reserva y Validación**

   Establece la interacción final entre el usuario y la oferta de clases disponibles.
   - __Usuario (1) — (\*) Reserva__: Documenta la intención de asistencia de una persona a una clase determinada y permite el rastreo de ausencias.  
   - __Sesión (1) — (\*) Reserva__: Centraliza todas las peticiones para una fecha y hora dadas, facilitando el cálculo de plazas libres mediante el contraste con el aforo de la sala.  

---

### Lógica de Verificación para la Reserva

Para que una reserva sea procesada con éxito, el sistema ejecuta de forma automática el siguiente flujo de comprobaciones:

1. __Estado de la Suscripción__: Se verifica que el **Usuario** posea una **Suscripción** en estado "ACTIVA" y cuya fecha de fin sea posterior a la fecha de la sesión.  
2. __Validación de Cupo__: Se comprueba que el **Tipo_Actividad** vinculado a la **Sesión** esté presente en la tabla **Selección_Actividad** asociada a la suscripción del usuario.  
3. __Disponibilidad de Aforo__: Se contrasta que el número de registros en la tabla **Reserva** para esa **Sesión** sea inferior a la capacidad máxima definida en la **Sala**.  
4. __Estado de la Sesión__: Se confirma que el atributo `estado` de la **Sesión** no sea "CANCELADA", permitiendo así el bloqueo de reservas en días festivos.
