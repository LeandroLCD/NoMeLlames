# Política de Privacidad - NoMeLlames

**Última actualización:** 27 de febrero de 2026

## Resumen

NoMeLlames es un bloqueador de llamadas spam que funciona completamente de forma local en tu dispositivo. No recopila, transmite ni comparte datos personales con terceros.

## Datos que almacena la app

Toda la información se almacena exclusivamente en tu dispositivo:

- **Prefijos bloqueados:** Los prefijos telefónicos que configuras para bloquear, almacenados en SharedPreferences.
- **Historial de llamadas bloqueadas:** Número de teléfono, prefijo coincidente y fecha/hora de cada llamada bloqueada, almacenados en una base de datos local (Room).

## Datos que NO recopila la app

- No envía datos a servidores externos
- No recopila datos personales ni identificadores
- No utiliza servicios de analítica o rastreo
- No comparte información con terceros
- No requiere conexión a internet para funcionar

## Permisos requeridos

- **READ_PHONE_STATE:** Necesario para que el sistema operativo permita el filtrado de llamadas entrantes.
- **READ_CALL_LOG:** Requerido por Android para registrar la app como servicio de filtrado de llamadas (CallScreeningService).

Estos permisos se utilizan exclusivamente para el funcionamiento del bloqueo de llamadas. No se accede al registro de llamadas del usuario para ningún otro propósito.

## Eliminación de datos

Puedes eliminar todos los datos almacenados por la app en cualquier momento:

- **Historial de llamadas bloqueadas:** Desde la pestaña "Historial" en la app, usando el botón de limpiar.
- **Todos los datos:** Desinstalando la app de tu dispositivo.

## Código abierto

NoMeLlames es software de código abierto. Puedes revisar el código fuente en [GitHub](https://github.com/hndresfelipe/NoMeLlames) para verificar estas prácticas de privacidad.

## Contacto

Si tienes preguntas sobre esta política de privacidad, puedes contactarme a través de [GitHub Issues](https://github.com/hndresfelipe/NoMeLlames/issues).

## Cambios

Cualquier cambio a esta política de privacidad se publicará en esta página con la fecha de actualización correspondiente.
