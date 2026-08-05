# Implementation Plan - Enable Professional Status Bar

Enable the system status bar across the entire application while maintaining professional styling and edge-to-edge support for Android 8–15.

## Proposed Changes

The primary strategy is to use the modern `EdgeToEdge` API provided by AndroidX to ensure the app handles system bars correctly across all supported Android versions. We will apply this to every Activity and use `WindowInsets` to ensure content does not overlap with the status bar or display cutout.

### [Component Name] Global UI Configuration

#### [MODIFY] [AuthActivity.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/AuthActivity.java)
- Ensure `EdgeToEdge.enable(this)` is correctly implemented.
- Verify `WindowInsets` are applied to the root view to provide top padding for the status bar.

#### [MODIFY] [HomeActivity.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/HomeActivity.java)
- Add `EdgeToEdge.enable(this)` in `onCreate`.
- Implement `ViewCompat.setOnApplyWindowInsetsListener` on the root layout to apply top insets as padding.

#### [MODIFY] [ProfileActivity.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/ProfileActivity.java)
- Add `EdgeToEdge.enable(this)` in `onCreate`.
- Implement `ViewCompat.setOnApplyWindowInsetsListener` on the root layout to apply top insets as padding.

#### [MODIFY] [CategoryActivity.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/CategoryActivity.java)
- Add `EdgeToEdge.enable(this)` in `onCreate`.
- Implement `ViewCompat.setOnApplyWindowInsetsListener` on the root layout to apply top insets as padding.

#### [MODIFY] [MyOrdersActivity.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/MyOrdersActivity.java)
- Add `EdgeToEdge.enable(this)` in `onCreate`.
- Implement `ViewCompat.setOnApplyWindowInsetsListener` on the root layout to apply top insets as padding.

## Verification Plan

### Automated Tests
- Not applicable for UI layout verification of this nature, but will ensure the project compiles successfully after changes.

### Manual Verification
- Deploy the app to an emulator or physical device.
- Verify that the status bar is visible on all five main screens (`Auth`, `Home`, `Profile`, `Category`, `MyOrders`).
- Confirm that status bar elements (Time, Battery, Wi-Fi, etc.) are visible and readable in both light and dark modes.
- Ensure that the top content of each screen (e.g., "Welcome" text, Search bar, etc.) starts below the status bar and does not overlap it.
- Verify that bottom navigation and other bottom-aligned elements still work correctly with system navigation bars (if enabled).
