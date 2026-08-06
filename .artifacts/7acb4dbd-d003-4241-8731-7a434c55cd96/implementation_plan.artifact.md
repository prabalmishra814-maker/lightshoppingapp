# Implementation Plan - Professional White Navigation & Bottom Bar Styling

The goal is to make the entire navigation system (system navigation bar and bottom navigation bar) white and professional, matching the styling of apps like Amazon and Meesho.

## User Review Required

> [!IMPORTANT]
> - The **System Navigation Bar** (bottom buttons/gestures) will be set to white with dark icons.
> - The **Bottom Navigation Bar** will be standardized with a white background, black active items, gray inactive items, and a subtle gray indicator.
> - I will unify the multiple color selectors (`nav_item_tint`, `nav_selector`) into a single professional selector.

## Proposed Changes

### [Component] Colors & Selectors

Update colors and unify selectors for a professional look.

#### [MODIFY] [colors.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/values/colors.xml)
- Add `nav_unselected` (#8A8A8A).
- Add `nav_active_indicator` (#F2F2F2).

#### [MODIFY] [nav_selector.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/color/nav_selector.xml)
- Set active color to `#000000` (black).
- Set inactive color to `#8A8A8A`.

### [Component] Themes

Apply global styles to enforce the white look and professional bottom navigation styling.

#### [MODIFY] [values/themes.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/values/themes.xml)
#### [MODIFY] [values-night/themes.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/values-night/themes.xml)
- Set `android:navigationBarColor` to `@color/white`.
- Set `android:windowLightNavigationBar` to `true`.
- Create a global style for `BottomNavigationView`:
    - `app:itemIconTint="@color/nav_selector"`
    - `app:itemTextColor="@color/nav_selector"`
    - `app:itemActiveIndicatorStyle` pointing to a new style with `@color/nav_active_indicator`.
    - `android:background="@color/white"`

### [Component] Utilities

#### [MODIFY] [StatusBarUtils.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/utils/StatusBarUtils.java)
- Update to handle the navigation bar explicitly in `applySystemBarStyles` (renamed from `applyWhiteStatusBar`).
- Ensure dark icons are applied to the navigation bar using `WindowInsetsControllerCompat`.

#### [MODIFY] [NavigationHelper.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/NavigationHelper.java)
- Clean up redundant padding logic as `StatusBarUtils` handles root-level padding.

### [Component] Layouts

Standardize the `BottomNavigationView` instances.

#### [MODIFY] all activity layouts
- Ensure `BottomNavigationView` uses `match_parent` width and a consistent height (e.g., `80dp`).
- Remove inline color/tint attributes to let the theme handle them.

## Verification Plan

### Automated Tests
- Build the project: `gradlew assembleDebug`

### Manual Verification
- Verify System Navigation Bar (gestures/buttons) is white with dark icons.
- Verify Bottom Navigation active item is Black with a light gray pill indicator.
- Verify Bottom Navigation inactive items are Gray (#8A8A8A).
- Verify consistency across Home, Category, Orders, and Profile screens.
