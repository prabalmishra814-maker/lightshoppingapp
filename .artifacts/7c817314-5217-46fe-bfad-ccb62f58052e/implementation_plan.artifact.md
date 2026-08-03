# Supabase Login System Implementation (Java)

This plan outlines the steps to integrate Supabase Authentication into the existing Android app using only Java. Since there is no official Java SDK, we will use Retrofit to interact with the Supabase GoTrue API.

## User Review Required

> [!IMPORTANT]
> You will need to provide your **Supabase URL** and **Anon Key** from your Supabase Project Settings (Settings -> API) to make the login system functional.
> We will use placeholders in the code for now.

## Proposed Changes

### Configuration & Dependencies

#### [MODIFY] [libs.versions.toml](file:///C:/Users/Harish Computer/AndroidStudioProjects/lightshoppingapp/gradle/libs.versions.toml)
Add Retrofit, Gson, and OkHttp dependencies.

#### [MODIFY] [build.gradle.kts](file:///C:/Users/Harish Computer/AndroidStudioProjects/lightshoppingapp/app/build.gradle.kts)
Apply the new dependencies.

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/Harish Computer/AndroidStudioProjects/lightshoppingapp/app/src/main/AndroidManifest.xml)
Add `<uses-permission android:name="android.permission.INTERNET" />`.

---

### Supabase Auth Backend (Java)

#### [NEW] [AuthModels.java](file:///C:/Users/Harish Computer/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/models/AuthModels.java)
Data classes for Supabase responses (User, Session, AuthResponse).

#### [NEW] [SupabaseAuthService.java](file:///C:/Users/Harish Computer/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/api/SupabaseAuthService.java)
Retrofit interface defining Auth endpoints.

#### [NEW] [SupabaseClient.java](file:///C:/Users/Harish Computer/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/api/SupabaseClient.java)
Singleton class to initialize Retrofit and provide the Auth service.

---

### UI Integration

#### [MODIFY] [MainActivity.java](file:///C:/Users/Harish Computer/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/MainActivity.java)
- Implement `handleLogin()` and `handleRegister()` methods.
- Connect UI buttons to the `SupabaseClient`.
- Add basic validation and error handling (Toasts).

## Verification Plan

### Automated Tests
- None planned for this phase.

### Manual Verification
1. Run the app.
2. Enter email and password.
3. Tap "Register" (verify user creation in Supabase dashboard).
4. Tap "Login" (verify navigation to `HomeActivity`).
5. Verify error messages for incorrect credentials.
