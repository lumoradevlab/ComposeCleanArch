# ComposeCleanArch

A **Jetpack Compose** base app: a modular, layered Android architecture you can clone and start a
real project from. It fetches TechCrunch headlines from [NewsAPI](https://newsapi.org/) as a working
example, so the architecture is demonstrated end to end rather than described in the abstract.

The headline idea: **screens have no ViewModels.** Server state goes through a hand-rolled query
cache (`useQuery` / `useMutation`, React-Query style), so a read-only screen is a hook call and a
`QueryContent` block — no `uiState` class, no `LaunchedEffect { load() }`, no loading boolean.

---

## Module structure

```
:app                → entry point, navigation, hooks/, and all screens
:core:model         → pure Kotlin domain models (no Android, no Gson)
:core:common        → AppResult/AppError, dispatcher qualifiers, session contracts
:core:network       → Retrofit/OkHttp infra + APIs, DTOs, mappers, repositories
:core:query         → the query cache: QueryClient, useQuery, useMutation
:core:datastore     → DataStore: session tokens, theme, onboarding
:core:database      → shared Room infra for the optional persistent query tier
:core:designsystem  → colour/type/spacing tokens + shared components
:core:ui            → QueryContent, theme hooks (the query-aware UI layer)
:core:testing       → shared test helpers
```

Dependencies point **downward only**: `app → core:ui/query/network → core:common → core:model`.
`:core:model` is a pure leaf, which is why DTO→model mappers live in `:core:network` (a mapper
references a DTO, so the reverse would be a cycle).

There are deliberately **no feature modules** — all UI lives in `:app`, all data in `:core:network`.
A `composearch.android.feature` convention plugin is ready for the day a screen set earns its own
module.

📖 **[ARCHITECTURE.md](ARCHITECTURE.md)** is the real reference: the rules, the data-flow diagram, and
the step-by-step recipe for adding a feature.

---

## What's inside

| Area | Implementation |
|---|---|
| UI | Jetpack Compose, Material 3, type-safe Navigation Compose (`@Serializable` routes) |
| Data fetching | Hand-rolled query cache — `useQuery` / `useCachedQuery` / `useMutation`, with TTL, dedup, invalidation, retry, interval refetch |
| Networking | Retrofit + OkHttp with a `Resource` call adapter — **no per-call `safeApiCall`** |
| Errors | One `AppError` type at the UI boundary; a shape-agnostic parser turns any error body into it |
| DI | Hilt, one small module per area |
| Local storage | DataStore (session, theme, onboarding) + Room infra for the persistent query tier |
| Design system | Semantic colour/type/spacing tokens, light+dark, `themed()` helper |
| Build | `build-logic` convention plugins, version catalog, dev/staging/prod flavors |
| Quality | detekt + ktlint, Android Lint, dependency-analysis (`buildHealth`), LeakCanary in debug |

---

## Getting started

```bash
git clone https://github.com/lumoradevlab/ComposeCleanArch.git
cd ComposeCleanArch
cp local.properties.example local.properties
```

Then edit `local.properties`:

```properties
sdk.dir=/Users/you/Library/Android/sdk
NEWS_API_KEY=your_key_here     # free key: https://newsapi.org/register
```

Open in Android Studio and sync, or build from the terminal:

```bash
./gradlew :app:assembleProdDebug     # or devDebug / stagingDebug
./gradlew test                       # unit tests (no emulator needed)
./gradlew detekt buildHealth         # quality gates
```

The key is read from `local.properties` into `BuildConfig` and attached by an interceptor — it is
never committed, and no endpoint signature carries it.

**Requirements:** `minSdk 24` · `compileSdk 34` · JDK 17+

---

## Using this as a base for your own app

1. Rename the package `dev.lumora.composearch` → yours (module namespaces, `applicationId`, source dirs).
2. Rename the convention plugin ids (`composearch.*`) and `rootProject.name`.
3. Replace the placeholder hosts in `build-logic/.../Flavors.kt`.
4. Delete the example feature — `Article*` across `:core:model` / `:core:network`,
   `:app/hooks/ArticleHooks.kt`, `:app/ui/articles/`, and their tests.
5. Repaint `:core:designsystem` — `Color.kt` and `Type.kt` are the two files a rebrand touches.

---

## Versions

| | Version |
|---|---|
| Kotlin | 2.0.21 |
| Compose BOM | 2024.06.00 |
| Android Gradle Plugin | 8.9.0 |
| Hilt | 2.52 |
| Room | 2.6.1 |
| Gradle | 8.11.1 |

---

## License

Not yet licensed. Feel free to read and learn from it; open an issue if you'd like a license added.
