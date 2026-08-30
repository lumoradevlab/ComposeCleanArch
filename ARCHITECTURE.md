# Architecture & Conventions

The point of this document: any developer can read it and add a feature the same
way the last one did. If you change a rule here, change it on purpose.

## Goals
- **Modular** — clean layer boundaries (model / network / query / ui); each core module builds and tests on its own.
- **Testable** — business logic is plain Kotlin, runnable without an emulator.
- **One way to do things** — one result type, one data-fetching mechanism, one theme.

## Module layout

```
:app                  All UI: Hilt entry point, navigation (Routes/NavHost/app shell), hooks/ (one file
                      per area + HookProvider), and ui/<area>/ screens. No feature modules.
:core:model           Pure Kotlin domain data classes. NO Android/Retrofit/serialization deps. Domain
                      logic lives here as testable properties (e.g. Article.displayAuthor).
:core:common          AppResult / AppError, coroutine dispatchers (DI), SessionStore + TokenRefresher
                      contracts, shared utils.
:core:network         Everything network: Retrofit/OkHttp infra + the data layer — Api interfaces and
                      their query-key objects (apis/), repositories (repositories/), wire DTOs +
                      DTO→model mappers (data/<area>/), Hilt wiring (di/).
:core:datastore       Persistence: DataStoreSessionStore (tokens), plus client-state stores
                      (ThemeStore, OnboardingStore).
:core:database        Room infra shared by the optional useCachedQuery tier (converters).
:core:query           Hand-rolled query cache (no Store5): QueryClient/QueryEntry + useQuery/
                      useCachedQuery/useMutation. The data mechanism.
:core:designsystem    theme/ (AppColors, AppTypography, Space/Radius/Stroke tokens, themed()/themedRes())
                      + components/ (the shared, previewable UI vocabulary).
:core:ui              Connected pieces that need query/theme state: QueryContent, useThemeMode +
                      ProvideTheme.
:core:testing         MainDispatcherRule + shared test helpers.
```

**No feature modules.** All UI (hooks + screens) lives in `:app`; all data (APIs, DTOs, repositories,
mappers) lives in `:core:network`; all domain data classes live in `:core:model`. A
`composearch.android.feature` convention plugin exists for the day a screen set grows big enough to
earn its own module — until then, feature modules are overhead.

**Dependency rule:** dependencies point *downward only*, with no cycles.
`app → core:ui/query/network/... → core:common → core:model`.
No module may depend *upward*. In particular `:core:model` is a pure leaf: `:core:network` depends on it,
**never** the reverse — which is exactly why DTO→model mappers live in `:core:network` (they reference a
DTO), not in `:core:model`.

## How data flows (the one mechanism)

```
Composable screen (:app)
  └─ useQuery(ArticleKeys.headlines(...)) { repo.getHeadlines() }  /  useMutation { ... }   ← hook, in :app
        └─ QueryClient / QueryEntry (in :core:query)      ← cache, dedup, TTL, refetch [plain Kotlin]
              └─ Repository → Api (Retrofit), both in :core:network ← Resource call adapter [plain Kotlin]
```

- **Reads** are `useQuery(key) { repo.get...() }` (memory tier) or `useCachedQuery(...)` (Room tier).
- **Writes** are `useMutation { req -> repo.submit(req) }`, with `invalidateKeys` to refetch affected reads.
- **Query keys** are constants in an `object XxxKeys` next to the Api interface (`:core:network/apis`),
  so hooks and invalidation sites share one definition.
- **Hooks** live in `:app/hooks`, one file per area (`ArticleHooks`, `ProfileHooks`...). They reach
  repositories through `HookProvider` (a `hiltViewModel`), so screens never inject repositories.
- The cache/TTL/dedup engine (`QueryClient`/`QueryEntry`) is **not** `@Composable`, so it is unit-testable.
  It retries transient failures only (network/timeout/5xx — never 4xx/429), pauses interval refetch
  unless the lifecycle is STARTED, and evicts unobserved dynamic keys. Mutations retry only transient
  failures too (a rejected submit must never be re-fired).
- Screens render with `QueryContent { data -> ... }` — they never hand-roll loading/error.

## The network layer (what a repository sits on)

- **Resource call adapter:** every endpoint declares `Resource<T>` as its return type, and the adapter
  centralises "parse whatever came back" — a 2xx body, an error body in any shape, malformed JSON, or a
  dropped connection all land in a `Resource` without throwing. There is **no per-call `safeApiCall`.**
- **Error parsing** is shape-agnostic (`ResponseErrorParser`): it walks the common message/error field
  names, pulls out validation maps, digs one level into nested `error` objects, and falls back to raw
  text. When a new backend shape appears, add its key to `MESSAGE_KEYS` — that's the whole maintenance cost.
- **Envelope (optional):** `ApiResponse<T>` models the common `{ code, success, message, result }` wrapper.
  The example API (NewsAPI) has no envelope, so its DTO sits directly in `Resource<…>`. Delete
  `ApiResponse`/`EmptyResultAdapterFactory` if your backend doesn't wrap payloads.
- **Serialization is Gson** (`@SerializedName` on DTOs). Be defensive: one bad field fails the whole
  response, so model everything nullable on the wire and normalise in the mapper.
- **Secrets** come from `local.properties` → `BuildConfig` → `NetworkConfig.apiKey`, attached by
  `ApiKeyInterceptor`. Never commit a key, and never put one in an endpoint signature.
- **Session:** `AuthInterceptor` attaches the bearer token **only to the API host**;
  `TokenAuthenticator` does a single-flight refresh on 401 and clears the session on failure —
  `isLoggedIn` flips and the app shell returns to login on its own (screens don't navigate to login).
- **Hardening:** certificate pinning via `NetworkConfig.certificatePins` (empty = off; pin prod before
  shipping, at least two keys), 429 retry with backoff.

## Where a ViewModel / state holder belongs

Most read-only screens need **no** ViewModel — the query hook is enough.
Add a small state holder only when a screen has real *local* state:
forms, multi-step flows, or state that must survive configuration changes.
That holder holds UI state + validation and calls the same query/mutation layer. It does
**not** become a second way to fetch data.

## Theming & design system

- Colors come from `AppTheme.colors` tokens, typography from `AppTheme.typography`, dimensions from the
  anatomy tokens (`Space`, `Radius`, `Stroke`) — no hardcoded hex or literal sp/dp where a token exists.
- Light/dark is resolved with `themed(light, dark)` for colors and `themedRes(lightRes, darkRes)` for
  drawables — not `isSystemInDarkTheme()` inside components, and not a `dark: Boolean` param.
- Theme mode (system/dark/light) is persisted client state: `ThemeStore` + `useThemeMode()`, resolved
  once in `MainActivity`.
- Component params added for one caller default to the existing behaviour — never fork a shared
  component for a single screen.

## Hard rules (these are the maintainability guardrails)

1. **One result type at the domain/UI boundary:** `AppResult<T>` + `AppError`. `Resource` is allowed only
   as a transport-internal type inside `:core:network` (the call adapter parses each HTTP response into
   it); a repository unwraps it and returns a domain model or throws, and the query engine maps that
   throw to `AppError`. Don't leak `Resource` or a raw `Result` past a repository.
2. **No god screens.** A `@Composable` over ~250 lines must be split into child components. Logic goes to
   a state holder / use case, not the composable.
3. **One Api interface per area.** No single mega-`Api`. Each area gets its own Retrofit interface in
   `:core:network/apis` — a 200+ endpoint god-interface is the anti-pattern.
4. **Domain stays pure.** Nothing in `:core:model` imports Android, Retrofit, Compose, or Gson — it holds
   only domain data classes and their logic. Wire DTOs (Gson `@SerializedName`) **and** their DTO→model
   mappers live on the data side in `:core:network` (`data/<area>/`). A mapper references a DTO, so
   putting it in `:core:model` would force `model → network` and a dependency cycle.
5. **Inject dispatchers** (`@IoDispatcher` etc.), don't reference `Dispatchers.IO` directly — so tests can
   substitute a TestDispatcher.
6. **Test the logic that costs you if it's wrong.** Mappers, validators, domain properties, and the query
   engine get unit tests. Not 100% coverage — the parts with real consequences.
7. **Money, if you add it, is `BigDecimal`** behind a domain `Money` type. Never Double/Float for
   balances, prices, or fees.

## Adding a feature (the recipe)

There are **no feature modules** — a feature is spread across the existing modules by layer:

1. `:core:model`: the pure domain data classes the screen reasons about; put derivable logic on them
   as testable properties.
2. `:core:network` `apis/`: a Retrofit `XxxApi` (its own interface) returning `Resource<...>`,
   plus an `object XxxKeys` with the query-key constants.
3. `:core:network` `data/<area>/`: DTOs (Gson) + the DTO→model mappers. Handle loose typing defensively.
4. `:core:network` `repositories/`: a `XxxRepository` that calls the Api, unwraps `Resource`, returns
   domain models (throws on failure).
5. `:core:network` `di/`: a Hilt module providing the Api from the shared `Retrofit`.
6. `:app` `hooks/`: a `XxxHooks.kt` with `useXxx()` hooks reaching the repository via `HookProvider`
   (add the repository to `HookProvider`); reads with `useQuery`/`useCachedQuery`, writes with
   `useMutation` + `invalidateKeys`.
7. `:app` `ui/<area>/`: small Composables rendered with `QueryContent`; add the route in `Routes.kt` /
   `AppNavHost`. A state holder only if the screen has real local state.
8. `test/`: unit-test the mappers/validators/domain logic.

## Build

- **build-logic** convention plugins (`composearch.android.library` / `.application` / `.compose` /
  `.hilt` / `.feature`, `composearch.jvm.library`) own compileSdk/minSdk/Java/Compose/Hilt config;
  module build files stay minimal.
- **Flavors** (dimension "environment"): `dev` / `staging` / `prod`, each exposing `BASE_URL` via
  BuildConfig — hosts are never hardcoded in code. **Rename the placeholder hosts in
  `build-logic/.../Flavors.kt` for your app.**
- **Quality gates:** detekt (+ ktlint via detekt-formatting, config in `config/detekt/`), Android Lint,
  and dependency-analysis (`buildHealth`) run through the convention plugins. Debug builds add
  LeakCanary; Compose stability reports behind `-PcomposeCompilerReports=true`.
- Versions live in `gradle/libs.versions.toml`.

## Using this as a base for a new app

1. Rename the package: `dev.lumora.composearch` → yours (namespaces in each `build.gradle.kts`,
   `applicationId` in `:app`, the source directories).
2. Rename the convention plugin ids (`composearch.*`) and `rootProject.name`.
3. Replace the flavor hosts in `Flavors.kt` and the `apiKey` wiring if your API authenticates differently.
4. Delete the example feature: `Article*` in `:core:model`, `:core:network` (apis/data/repositories/di),
   `:app/hooks/ArticleHooks.kt`, `:app/ui/articles/`, and their tests. Keep the shape, drop the news.
5. Repaint `:core:designsystem` — `Color.kt` and `Type.kt` are the two files a rebrand touches.
