# FreshTrack

FreshTrack is an Android app that helps you stop throwing food away. You enter what you bought and its expiry date, and the app shows what is already expired, what expires today, what is running out soon, and what you should use first. When you finish a product you mark it as **Used** or **Discarded**, and the Statistics screen shows how much of your food ends up in the bin.

Built with Kotlin and Jetpack Compose (Material 3) for the KBTU Android course, Midterm Project Part 1.

## The problem

Food is often forgotten in the fridge or pantry until it is too late. Remembering many different dates in your head does not work. FreshTrack keeps the dates in one place, counts the days for you and puts the most urgent products at the top.

FreshTrack only shows the dates you enter. It does **not** decide whether food is safe to eat.

## Features

1. **Expiry tracking.** Every product gets a status calculated from calendar dates:

   | Status | Rule |
   |---|---|
   | Expired | expiry date is before today |
   | Today | expiry date is today |
   | Soon | 1 to 3 days left |
   | OK | more than 3 days left |

2. **Search, filter and sort.** Search by name, filter by status, sort by expiry date or by name. Empty and "nothing found" states have their own messages.
3. **Add, edit and delete.** A form with validation (name, quantity, date), category chips, quick date buttons and a Material 3 date picker.
4. **Used vs Discarded.** Finished products are not deleted. They stay in history with the outcome you chose. Expired products are never removed automatically.
5. **Undo.** After marking a product Used or Discarded a snackbar offers to undo it.
6. **Statistics.** Number of used and discarded products, waste rate and discarded products by category.
7. **Sample data.** An optional button on the empty Home screen fills the app with example products for a quick demo.

## Screens and navigation

| Screen | Route | How you get there |
|---|---|---|
| Home | `home` | start destination, bottom bar |
| Products | `products` | bottom bar, "See all" on Home |
| Statistics | `stats` | bottom bar |
| Add product | `product/add` | "+" button on Home or Products |
| Edit product | `product/edit/{productId}` | tap a product card |

Navigation Compose with a bottom `NavigationBar` for the three main tabs. Add and Edit are separate destinations with a back button. The Edit route receives the product id as a navigation argument.

## Architecture

```
app/src/main/java/com/example/freshtrack/
├── MainActivity.kt
├── model/        Product, Category, ProductStatus
├── domain/       ExpiryRules, ProductFilters, ProductInsights, ProductValidator, todayFlow
├── data/         ProductRepository (in-memory), SampleProducts
└── ui/
    ├── ProductsViewModel.kt
    ├── navigation/   Routes, AppNavGraph
    ├── screens/      Home, Products, ProductForm, Stats
    ├── components/   reusable composables, icons
    └── theme/        Material 3 theme
```

Data flow:

```
ProductRepository (MutableStateFlow<List<Product>>)
        │
        ▼
ProductsViewModel ── combine(products, today, query, filter, sort) ──► StateFlow ui state
        │                                                                  │
        ▼                                                                  ▼
 user events  ◄──────────────  stateless screens (collectAsStateWithLifecycle)
```

- **One ViewModel** (`ProductsViewModel`) is shared by all screens, so a change on one screen is visible on the others.
- **UI state** is exposed as `StateFlow` and collected with `collectAsStateWithLifecycle()`.
- **Screens are stateless.** They receive state and callbacks. Form fields are local UI state.
- **Business logic** (status rules, filtering, statistics, validation) is plain Kotlin in `domain/` and has unit tests.
- **Today's date** comes from a coroutine flow that re-emits after midnight, so statuses stay correct while the app is open.

### Where each course topic is used

| Topic | Where |
|---|---|
| Jetpack Compose, Material 3 | all files in `ui/` |
| Navigation Compose | `ui/navigation/AppNavGraph.kt`, `Routes.kt` |
| Reusable composables, consistent styling | `ui/components/`, `ui/theme/Theme.kt` |
| `remember` | `SnackbarHostState` in `HomeScreen.kt` and `ProductsScreen.kt`, product lookup in `AppNavGraph.kt` |
| `rememberSaveable` | form fields and dialog flags in `ProductFormScreen.kt` |
| State hoisting | `HomeScreen`, `ProductsScreen`, `StatsScreen` receive state and callbacks; search text lives in the ViewModel |
| ViewModel + StateFlow | `ui/ProductsViewModel.kt` |
| Lifecycle-aware collection | `collectAsStateWithLifecycle()` in `AppNavGraph.kt`, `stateIn(..., WhileSubscribed(5000), ...)` in the ViewModel |
| Collections and functions | `ProductFilters`, `ProductInsights` (filter, sortedWith, count, groupingBy) |
| Classes, enums, data classes | `model/Models.kt`, `domain/` |
| Coroutines | `viewModelScope`, `stateIn`, `todayFlow()` with `delay` |
| Configuration changes | ViewModel keeps data, `rememberSaveable` keeps form input after rotation |

## Tech stack

| Part | Version |
|---|---|
| Android Gradle Plugin | 9.4.0 |
| Kotlin | 2.4.10 |
| Gradle wrapper | 9.6.0 |
| Compose BOM | 2026.09.00 |
| Activity Compose | 1.13.0 |
| Lifecycle (ViewModel, runtime Compose) | 2.11.0 |
| Navigation Compose | 2.10.2 |
| compileSdk / targetSdk / minSdk | 37 / 36 / 26 |
| JDK | 17 |

No other libraries. No database or network yet (those come in the second half of the course).

## How to run

1. Install a recent **Android Studio** that supports AGP 9.4 (for example Android Studio Quail 4 2026.1.4 or newer) and install **Android SDK Platform 37** from SDK Manager.
2. Clone the repository and open the project folder with **File → Open**.
3. Wait for Gradle sync to finish. Android Studio downloads the dependencies.
4. Choose an emulator (API 26 or newer) or a connected phone and press **Run**.

Command line (JDK 17 and the Android SDK must be installed):

```
./gradlew assembleDebug
./gradlew test
```

On Windows use `gradlew.bat`.

If Android Studio reports that the project needs a newer Android Gradle Plugin or SDK, update Android Studio or the SDK platform and sync again.

## Tests

Unit tests for the plain Kotlin logic are in `app/src/test`:

- `ExpiryRulesTest`: day counting and status boundaries
- `ProductFiltersTest`: search, status filter and sorting
- `ProductLogicTest`: validation, home summary and statistics

Run with `./gradlew test`.

## Screenshots

Initial state of Home without products
![Home without products](/screenshots/img.png)

## Known limitations

- Data is stored in memory only. It is kept while the app process is alive (including rotation), but it is lost when the app is closed completely. Persistent storage is planned for Part 2.
- The "Soon" threshold is fixed at 3 days.
- There are no notifications or reminders.
- Quantity is a plain number without units.
