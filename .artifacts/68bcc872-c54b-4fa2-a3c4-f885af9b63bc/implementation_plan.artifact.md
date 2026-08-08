# Implementation Plan - Bypass Login and Navigate to Home

The user wants to be redirected to the main part of the app (HomeActivity) when they click the login button in AuthActivity.

## Proposed Changes

### [Component: Authentication]

#### [MODIFY] [AuthActivity.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/AuthActivity.java)
- Update the `handleLogin` method to immediately save a dummy session and navigate to `HomeActivity`, effectively bypassing the real authentication process for development/testing purposes.

## Verification Plan

### Manual Verification
- Deploy the app to a device/emulator.
- Navigate to the Login screen.
- Click the "Login" button.
- Verify that the app navigates to the Home screen (HomeActivity).
- Restart the app and verify that it skips the login screen (due to `MainActivity` checking the session).
