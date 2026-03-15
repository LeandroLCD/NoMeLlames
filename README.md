<div align="center">

# 📵 PrefixApp

### Bloqueador de llamadas spam para Android basado en prefijos telefónicos

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-purple.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-API%2024+-green.svg?style=flat&logo=android)](https://developer.android.com)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-blue.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Room](https://img.shields.io/badge/Room-Persistencia-orange.svg?style=flat)](https://developer.android.com/training/data-storage/room)
[![Hilt](https://img.shields.io/badge/Hilt-DI-red.svg?style=flat)](https://dagger.dev/hilt/)
[![Version](https://img.shields.io/badge/version-1.3-brightgreen.svg?style=flat)](https://github.com/LeandroLCD/NoMeLlames)

Bloquea automáticamente llamadas entrantes de prefijos telefónicos configurables usando la API nativa `CallScreeningService` de Android. Enfocado en el mercado colombiano con soporte para prefijos `+57`.

> Anteriormente conocido como **NoMeLlames**.

[Características](#-características) •
[Arquitectura](#-arquitectura) •
[Cómo funciona](#-cómo-funciona) •
[Instalación](#-instalación) •
[Estructura](#-estructura-del-proyecto) •
[Contribuir](#-contribuir)

</div>

---

## 📋 Tabla de Contenidos

- [✨ Características](#-características)
- [🏛️ Arquitectura](#-arquitectura)
- [⚙️ Cómo funciona](#-cómo-funciona)
- [📦 Instalación](#-instalación)
- [🗂️ Estructura del proyecto](#-estructura-del-proyecto)
- [🛠️ Stack tecnológico](#-stack-tecnológico)
- [🤝 Contribuir](#-contribuir)

---

## ✨ Características

- 🚫 **Bloqueo por prefijo** — Rechaza llamadas de números que comiencen con prefijos configurados con reglas `BLOCK` / `ALLOW`
- 🇨🇴 **Prefijos colombianos preconfigurados** — Soporte nativo para el código de país `+57`
- 📜 **Historial completo** — Registro de llamadas bloqueadas y permitidas con número, prefijo coincidente y fecha/hora
- ⚙️ **Comportamiento configurable** — Oculta llamadas del registro del sistema y suprime notificaciones
- 🎨 **Material You** — Tema dinámico en Android 12+ con soporte para modo oscuro
- 🔒 **Seguridad biométrica** — Acceso protegido con huella dactilar o patrón
- 🔄 **Servicio en segundo plano** — Funciona sin tener la app abierta mediante `CallScreeningService`
- 🧩 **Arquitectura limpia** — Capas bien definidas: UI, Dominio y Datos

---

## 🏛️ Arquitectura

El proyecto sigue **Clean Architecture** con separación en capas y el patrón **MVVM**:

```
┌─────────────────────────────────────────────┐
│                    UI Layer                  │
│   Compose Screens  ←→  ViewModels (Hilt)    │
├─────────────────────────────────────────────┤
│                 Domain Layer                 │
│   UseCases (IUseCase)  ·  Models  · Repos   │
├─────────────────────────────────────────────┤
│                  Data Layer                  │
│   Room Entities  ·  DAOs  ·  RepoImpls      │
└─────────────────────────────────────────────┘
```

### Flujo de datos

1. `SpamCallScreeningService.onScreenCall()` recibe la llamada entrante del sistema
2. Carga la lista de `PrefixRule` y aplica la lógica **longest prefix wins** (`BLOCK` vs `ALLOW`)
3. Las llamadas bloqueadas se persisten mediante `BlockedCallRepository`, las permitidas mediante `AllowedCallRepository`
4. La UI observa los cambios en tiempo real a través de `Flow<List<T>>` expuestos desde los DAOs

---

## ⚙️ Cómo funciona

NoMeLlames se registra como servicio de filtrado de llamadas predeterminado en Android. Al recibir una llamada:

1. **Normaliza** el número eliminando el código de país `+57`
2. **Evalúa** la lista de prefijos aplicando la regla del prefijo más largo coincidente
3. **Ejecuta** la acción: si es `BLOCK`, rechaza la llamada y opcionalmente la oculta del registro
4. **Registra** el evento en el historial local (Room)

### Patrón UseCase

Cada caso de uso sigue el contrato interfaz + implementación:

```kotlin
// Interfaz
interface IAddPrefixRuleUseCase {
    suspend operator fun invoke(prefix: String, ruleType: PrefixRule.RuleType): Result<Unit>
}

// Implementación
class AddPrefixRuleUseCase @Inject constructor(
    private val prefixRepository: PrefixRepository
) : IAddPrefixRuleUseCase {
    override suspend operator fun invoke(
        prefix: String,
        ruleType: PrefixRule.RuleType
    ): Result<Unit> { ... }
}
```

---

## 📦 Instalación

### Requisitos previos

- Android Studio **Ladybug** o superior
- Dispositivo / emulador con **Android API 24+**
- Configurar la app como **servicio de filtrado de llamadas predeterminado** en los ajustes del sistema

### Permisos requeridos

| Permiso | Uso |
|---|---|
| `READ_PHONE_STATE` | Leer información de la llamada entrante |
| `READ_CALL_LOG` | Acceso al registro de llamadas |
| `BIND_SCREENING_SERVICE` | Vinculación con el sistema de filtrado de llamadas |

### Pasos

1. Clona el repositorio:
   ```bash
   git clone https://github.com/LeandroLCD/NoMeLlames.git
   ```

2. Abre el proyecto en **Android Studio Ladybug** o superior.

3. Compila y ejecuta:
   ```bash
   ./gradlew assembleDebug
   ```

4. Instala en el dispositivo y configura la app como **filtrado de llamadas predeterminado** desde *Ajustes del sistema → Aplicaciones de teléfono*.

---

## 🗂️ Estructura del proyecto

```
prefixapp/
├── app/
│   └── src/main/java/.../
│       ├── MainActivity.kt                  # Actividad principal con navegación Compose (4 tabs)
│       ├── SpamCallScreeningService.kt      # Servicio de filtrado de llamadas
│       ├── data/
│       │   ├── local/
│       │   │   ├── dao/                     # DAOs: PrefixRuleDao, BlockedCallDao, AllowedCallDao…
│       │   │   ├── database/AppDatabase.kt  # Base de datos Room
│       │   │   └── entities/               # Entidades Room con sufijo Entity
│       │   └── repository/                 # Implementaciones de repositorios (extienden BaseRepository)
│       ├── di/                              # Módulos Hilt: DataModule, RepositoryModule, UseCaseModule
│       ├── domain/
│       │   ├── model/                       # Modelos de dominio (PrefixRule, BlockedCall, AppSettings…)
│       │   ├── repositories/               # Contratos de repositorios (interfaces)
│       │   └── useCase/                    # Casos de uso por funcionalidad (prefix, blockedcall, history…)
│       └── ui/
│           ├── home/                        # Pantalla de inicio con estadísticas y toggle del firewall
│           ├── prefix/                      # Gestión de prefijos (alta, baja, reglas BLOCK/ALLOW)
│           ├── history/                     # Historial de llamadas bloqueadas y permitidas
│           ├── settings/                    # Configuración, biométrico y zona de peligro
│           └── theme/                       # Colores, tipografía y tema Material You
└── specialbottombar/                        # Módulo reutilizable de barra de navegación inferior
```

---

## 🛠️ Stack tecnológico

| Componente | Tecnología |
|---|---|
| **Lenguaje** | Kotlin |
| **UI** | Jetpack Compose + Material 3 |
| **Arquitectura** | Clean Architecture + MVVM |
| **Inyección de dependencias** | Hilt |
| **Base de datos** | Room (con migraciones) |
| **Navegación** | Jetpack Navigation Compose |
| **Concurrencia** | Kotlin Coroutines + Flow |
| **Seguridad** | BiometricPrompt API |
| **SDK mínimo** | API 24 (Android 7.0) |
| **SDK objetivo** | API 36 (Android 16) |

---

## 🤝 Contribuir

¡Las contribuciones son bienvenidas! Si deseas mejorar el proyecto:

1. 🍴 Haz un **Fork** del proyecto
2. 🌿 Crea una rama para tu función (`git checkout -b feature/NuevaFuncionalidad`)
3. 💾 Haz commit de tus cambios (`git commit -m 'feat: agregar nueva funcionalidad'`)
4. 📤 Push a la rama (`git push origin feature/NuevaFuncionalidad`)
5. 🔃 Abre un **Pull Request**

---

## 📄 Licencia

Este proyecto está licenciado bajo la **Licencia MIT** — ver el archivo [LICENSE](LICENSE) para más detalles.

---

<div align="center">

**Hecho con ❤️ para la comunidad Android**

⭐ Si te gusta este proyecto, ¡dale una estrella en GitHub!

</div>
