# Implementation Plan - Login & Register Screen Fix

This plan addresses the requirement to remove the back button from the Login screen and implement full backend functionality for the Register screen using Supabase.

## User Review Required

> [!IMPORTANT]
> - The loading indicator will be implemented by updating the button text (e.g., "Registering...") and disabling the button to prevent multiple clicks, as no ProgressBar is present in the XML and we must avoid UI/layout changes.
> - The "Remove Back Arrow" requirement will be achieved by setting `btn_back` visibility to `GONE` when on the Login screen. This will cause the content below to shift up, fulfilling the "do not leave any empty space" requirement.

## Proposed Changes

### Auth Component

#### [MODIFY] [AuthActivity.java](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/AuthActivity.java)

- **UI Management**:
    - Hide `btn_back` in `showLogin()` using `View.GONE`.
    - Show `btn_back` in `showRegister()` using `View.VISIBLE`.
    - Set a click listener on `btn_back` to call `showLogin()` when in the Register screen.
- **Registration Logic (`handleRegister`)**:
    - **Validations**:
        - Full Name: Not empty.
        - Email: Not empty and matches a valid email regex.
        - Password: Minimum length (e.g., 6 characters).
        - Confirm Password: Must match Password.
        - Terms Checkbox: Must be checked.
    - **Loading State**:
        - Disable `btn_register`.
        - Change text to "Creating account...".
    - **API Call**:
        - Invoke `SupabaseAuthService#signUp`.
        - Handle `onResponse`:
            - If successful: Show success Toast, switch to Login screen using `showLogin()`, and reset button state.
            - If failure: Parse error using `parseError()` (handles duplicate email, etc.) and show Toast. Reset button state.
        - Handle `onFailure`: Show network error Toast. Reset button state.
- **Navigation**:
    - Override `onBackPressed()`:
        - If `registerContainer` is visible, call `showLogin()`.
        - Otherwise, proceed with default behavior (`finish()`).

## Verification Plan

### Automated Tests
- Build the project to ensure no syntax errors.

### Manual Verification
- **Login Screen**: Verify that no back arrow is visible and the title is at the top.
- **Registration**:
    - Try registering with empty fields -> should show error toast.
    - Try registering with invalid email -> should show error toast.
    - Try registering with mismatching passwords -> should show error toast.
    - Try registering without checking terms -> should show error toast.
    - Perform a valid registration:
        - Verify button text changes and button is disabled.
        - Verify success toast appears.
        - Verify screen switches to Login automatically.
- **Duplicate Email**: Try registering with the same email again -> verify "Email already exists" (or similar) error from Supabase is shown.
- **Back Navigation**:
    - Click `Register` from Login -> back arrow should appear.
    - Click Back Arrow -> should return to Login.
    - Use Android system back gesture from Register -> should return to Login.
    - Use Android system back gesture from Login -> should close the app.
