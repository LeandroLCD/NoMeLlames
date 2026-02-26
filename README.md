# NoMeLlames

Bloqueador de llamadas spam para Android. Bloquea automáticamente llamadas entrantes de prefijos telefónicos configurables usando la API `CallScreeningService` de Android.

## Características

- **Bloqueo por prefijo** - Bloquea llamadas de números que empiecen con prefijos específicos
- **Prefijos colombianos preconfigurados** - Incluye 315, 316, 317, 318 por defecto
- **Historial de llamadas bloqueadas** - Consulta número, prefijo coincidente y fecha/hora
- **Comportamiento configurable** - Oculta llamadas bloqueadas del registro y notificaciones
- **Material You** - Tema dinámico en Android 12+ con soporte para modo oscuro
- **Servicio en segundo plano** - Funciona sin tener la app abierta vía `CallScreeningService`

## Cómo funciona

NoMeLlames se registra como servicio de filtrado de llamadas en Android. Cuando llega una llamada entrante, el sistema la enruta a `SpamCallScreeningService`, que:

1. Normaliza el número de teléfono (elimina el código de país `+57` de Colombia)
2. Verifica si el número comienza con algún prefijo bloqueado
3. Si coincide, rechaza la llamada y opcionalmente la oculta del registro

## Requisitos

- Android 16 (API 36) o superior
- Permisos: `READ_PHONE_STATE`, `READ_CALL_LOG`
- Debe configurarse como app de filtrado de llamadas predeterminada en ajustes del sistema

## Stack tecnológico

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Base de datos:** Room (historial de llamadas bloqueadas)
- **Almacenamiento:** SharedPreferences (prefijos y configuración)
- **Arquitectura:** MVVM con patrón Repository
- **Navegación:** Jetpack Navigation Compose

## Compilación

1. Clona el repositorio:
   ```bash
   git clone https://github.com/hndresfelipe/NoMeLlames.git
   ```

2. Abre el proyecto en Android Studio (Ladybug o superior recomendado).

3. Compila y ejecuta en un dispositivo con API 36+.

## Estructura del proyecto

```
app/src/main/java/.../nomellames/
├── MainActivity.kt              # UI con 4 tabs (Inicio, Prefijos, Historial, Ajustes)
├── SpamCallScreeningService.kt  # Lógica de filtrado de llamadas
└── data/
    ├── AppDatabase.kt           # Singleton de base de datos Room
    ├── BlockedCall.kt           # Entidad Room
    ├── BlockedCallDao.kt        # Objeto de acceso a datos
    └── PrefixRepository.kt     # Wrapper de SharedPreferences
```

## Licencia

Este proyecto está licenciado bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.
