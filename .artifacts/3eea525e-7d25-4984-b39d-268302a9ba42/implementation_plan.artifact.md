# Implementation Plan - Fix User App Crash (Java Only)

The goal is to resolve all crashes in the application while maintaining a 100% Java codebase and preserving all existing UI and features.

## User Review Required

> [!IMPORTANT]
> - **Kotlin to Java Conversion**: Several activities and models are currently in Kotlin. I will convert them to Java as requested.
> - **Missing Launcher Activity**: `MainActivity` is missing but set as the launcher in `AndroidManifest.xml`. I will set `AuthActivity` as the launcher.
> - **Internet Permission**: The app is missing the `INTERNET` permission, which will cause crashes during Supabase authentication. I will add it.
> - **Supabase Configuration**: `local.properties` is missing Supabase keys. I will add placeholders to prevent build/runtime errors, but you will need to provide valid keys for full functionality.

## Proposed Changes

### Configuration & Manifest

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/AndroidManifest.xml)
- Add `INTERNET` permission.
- Change LAUNCHER activity from `MainActivity` to `AuthActivity`.
- Remove the missing `MainActivity` declaration.

#### [MODIFY] [build.gradle.kts](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/build.gradle.kts)
- Remove Kotlin dependencies and plugins (if any).
- Ensure `viewBinding` is working for Java.

### Kotlin to Java Conversion

#### [NEW] [OrderModel.java](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/OrderModel.java)
- Java equivalent of `OrderModel.kt`.

#### [NEW] [ProfileMenuModel.java](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/ProfileMenuModel.java)
- Java equivalent of `ProfileMenuModel.kt`.

#### [NEW] [OrdersAdapter.java](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/OrdersAdapter.java)
- Java equivalent of `OrdersAdapter.kt`.

#### [NEW] [ProfileAdapter.java](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/ProfileAdapter.java)
- Java equivalent of `ProfileAdapter.kt`.

#### [NEW] [MyOrdersActivity.java](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/MyOrdersActivity.java)
- Java equivalent of `MyOrdersActivity.kt`.

#### [NEW] [ProfileActivity.java](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/ProfileActivity.java)
- Java equivalent of `ProfileActivity.kt`.

#### [DELETE] All `.kt` files in the package.

### Crash Fixes & Stability

#### [MODIFY] [HomeActivity.java](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/HomeActivity.java)
- Replace `Class.forName` with direct class references for `MyOrdersActivity` and `ProfileActivity`.
- Add null checks for `getBackground()` when setting tints.

#### [MODIFY] [CategoryActivity.java](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/CategoryActivity.java)
- Replace `Class.forName` with direct class references.

#### [MODIFY] [AuthActivity.java](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/AuthActivity.java)
- Add null checks for views before accessing them.
- Improve error handling for Google Sign-In and Supabase responses.

## Verification Plan

### Automated Tests
- `gradlew assembleDebug` to ensure successful compilation.
- `gradlew test` to run any existing unit tests.

### Manual Verification
- Deploy the app to a device/emulator.
- Verify the splash/auth screen appears correctly.
- Test navigation between Home, Category, Orders, and Profile.
- Check that no crashes occur during any user interaction.
