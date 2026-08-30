# ComposeCleanArch

A **Jetpack Compose + Clean Architecture** starter for Android, split into four Gradle modules.
It fetches TechCrunch headlines from [NewsAPI](https://newsapi.org/) as a working example, so the
architecture is demonstrated end to end rather than described in the abstract.

> ⚠️ **Work in progress.** This project is being updated — dependencies are pinned to older versions
> (see [Status](#status)). Read it as a structural reference, not a drop-in template.

---

## Module structure

```
:app           → entry point, navigation host, Hilt application
:presentation  → Compose screens, ViewModels, UI state
:domain        → models, repository interfaces, use cases  (pure Kotlin logic)
:data          → Retrofit, Room, DataStore, repository implementations
```

Dependencies point **inward**: `:presentation` and `:data` know about `:domain`, but `:domain`
knows about neither. Swapping the network layer doesn't touch a use case.

---

## What's inside

| Area | Implementation |
|---|---|
| UI | Jetpack Compose, Material, bottom-nav + Navigation Compose |
| Architecture | Clean Architecture, MVVM, use cases per feature |
| DI | Hilt — with its own module per layer (`AppModule`, `NetworkModule`, `DatabaseModule`, `RepositoryModule`, `UseCaseModule`) |
| Networking | Retrofit + OkHttp, `ConnectivityInterceptor` for offline detection |
| Paging | Paging 3 via a custom `ArticlePagingSource` |
| Local storage | Room (`AppDatabase`, `ArticleDao`, type converters) + DataStore preferences |
| State | `DataState` / `ResponseWrapper` wrappers, per-screen state classes |
| Build | `buildSrc` module centralizing versions, dependencies, and build types |

**Screens:** an onboarding/welcome flow, a paginated article list, and a location screen —
each with its own navigation graph.

---

## Getting started

```bash
git clone https://github.com/lumoradevlab/ComposeCleanArch.git
```

Open in Android Studio and let Gradle sync.

**Add a NewsAPI key.** Get a free one at [newsapi.org](https://newsapi.org/register), then set it
in `data/src/main/java/dev/roshana/data/network/api/ArticlePagingSource.kt`:

```kotlin
apiKey = "YOUR_API_KEY"
```

> 🔑 The key currently committed in this repo is public and will be rotated — supply your own.
> Moving it into `local.properties` / `BuildConfig` is on the list below.

**Requirements:** `minSdk 21` · `targetSdk 33` · JDK 11

---

## Status

Version pins as of this README:

| | Version |
|---|---|
| Kotlin | 1.5.31 |
| Compose | 1.1.0-beta01 |
| Gradle Plugin | 7.2.2 |
| Hilt | 2.42 |
| Room | 2.4.2 |

### Planned

- [ ] Upgrade Kotlin, Compose, and AGP to current stable
- [ ] Move the API key out of source into `local.properties` → `BuildConfig`
- [ ] Migrate `buildSrc` to a Gradle version catalog (`libs.versions.toml`)
- [ ] Replace the placeholder unit/instrumented tests with real coverage
- [ ] Material 3

---

## License

Not yet licensed. Feel free to read and learn from it; open an issue if you'd like a license added.
