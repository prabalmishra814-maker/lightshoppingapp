# Implementation Plan - Fix Settings Screen Scrolling Bug

Address the issue where the top area (status bar/toolbar) becomes black or dark when scrolling the Settings screen. The goal is to maintain a consistent white background throughout the scrolling experience without altering the existing UI design.

## User Review Required

> [!IMPORTANT]
> This fix involves modifying the `CoordinatorLayout` behavior and `AppBarLayout` properties in the `fragment_settings.xml` to ensure the background remains white during all scroll states. It also involves a small adjustment to `StatusBarUtils` to ensure the system bars are handled consistently.

## Proposed Changes

### Settings UI Layout

#### [MODIFY] [fragment_settings.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/fragment_settings.xml)
-   Add `app:statusBarBackground="@color/white"` to the `CoordinatorLayout` to prevent any default dark scrim from appearing in the status bar area.
-   Explicitly set `app:liftOnScroll="false"` on the `AppBarLayout` to prevent color changes when the content scrolls under it.
-   Ensure the `AppBarLayout` and `CoordinatorLayout` both have `android:background="@color/white"`.

### System Bar Utilities

#### [MODIFY] [StatusBarUtils.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/utils/StatusBarUtils.java)
-   Refine the `EdgeToEdge` configuration to ensure the scrims are explicitly white and don't default to dark during transitions.
-   Ensure the `WindowInsetsControllerCompat` settings for light appearance are applied correctly.

## Verification Plan

### Automated Tests
-   Build the project to ensure no resource or compilation errors.

### Manual Verification
1.  Open the app and navigate to the **Settings** screen (via Profile gear icon).
2.  Scroll the list from top to bottom and bottom to top.
3.  **Verify:** The top area (status bar and toolbar) remains purely white throughout the entire scroll.
4.  **Verify:** No black strip, dark background, or flickering appears at the top.
5.  **Verify:** Status bar icons remain dark and visible.
6.  **Verify:** The existing "Settings" title, back button, and search icon remain unchanged in appearance and position.
7.  **Verify:** The bottom navigation remains unchanged.
