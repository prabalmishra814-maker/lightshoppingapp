# Implementation Plan - Single Professional Bottom Navigation

Implement a unified, robust Bottom Navigation system across the main app screens (Home, Category, Orders, Profile) with proper layout handling for all devices.

## User Review Required

> [!IMPORTANT]
> - All main activities will have their `launchMode` set to `singleTop` in `AndroidManifest.xml` to prevent multiple instances and state loss.
> - A new utility class `NavigationHelper` will be created to centralize navigation logic and ensure consistent behavior.
> - Layout IDs for the Bottom Navigation will be standardized to `bottom_nav` across all files.

## Proposed Changes

### [Component] Navigation Architecture

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/AndroidManifest.xml)
- Set `android:launchMode="singleTop"` for `HomeActivity`, `CategoryActivity`, `MyOrdersActivity`, and `ProfileActivity`.

#### [NEW] [NavigationHelper.java](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/NavigationHelper.java)
- Create a helper class to:
    - Handle `OnItemSelectedListener` with `Intent.FLAG_ACTIVITY_REORDER_TO_FRONT`.
    - Apply `WindowInsets` to the Bottom Navigation to fix clipping/overlap issues.
    - Ensure the correct item is selected based on the current Activity.

### [Component] Layout Standardization

#### [MODIFY] [activity_home.xml](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/layout/activity_home.xml)
- Standardize `bottom_nav` height to `wrap_content`.
- Ensure constraints are correct for Edge-to-Edge.

#### [MODIFY] [activity_category.xml](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/layout/activity_category.xml)
- Rename `bottomNavigation` to `bottom_nav`.
- Standardize height to `wrap_content`.

#### [MODIFY] [activity_my_orders.xml](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/layout/activity_my_orders.xml)
- Standardize height to `wrap_content`.

#### [MODIFY] [activity_profile.xml](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/layout/activity_profile.xml)
- Rename `bottomNavigation` to `bottom_nav`.
- Standardize height to `wrap_content`.

### [Component] Activity Logic

#### [MODIFY] [HomeActivity.java](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/HomeActivity.java)
- Replace inline navigation logic with `NavigationHelper.setupBottomNavigation(this)`.

#### [MODIFY] [CategoryActivity.java](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/CategoryActivity.java)
- Replace inline navigation logic with `NavigationHelper.setupBottomNavigation(this)`.

#### [MODIFY] [MyOrdersActivity.java](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/MyOrdersActivity.java)
- Replace inline navigation logic with `NavigationHelper.setupBottomNavigation(this)`.

#### [MODIFY] [ProfileActivity.java](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/ProfileActivity.java)
- Replace inline navigation logic with `NavigationHelper.setupBottomNavigation(this)`.

## Verification Plan

### Automated Tests
- Build project to ensure no ID mismatches.

### Manual Verification
- **Navigation Flow**: Verify switching between all four tabs does not restart activities (check logs or screen transition).
- **Clipping**: Test on a device with a gesture-based navigation bar (long bar) and a 3-button navigation bar to ensure the Bottom Nav is always fully visible and correctly padded.
- **State Preservation**: Scroll down in Home, go to Category, then back to Home. The scroll position should be preserved.
- **Back Stack**: Pressing the system back button should behave predictably (e.g., return to Home if not already there, then close).
