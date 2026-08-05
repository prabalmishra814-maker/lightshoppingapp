# Implementation Plan - Profile Screen (Exact UI Match)

Recreate the "Profile" screen as a pixel-perfect match to the reference image using Android XML and Material Design 3.

## User Review Required

> [!IMPORTANT]
> I will use a `RecyclerView` with two different section headers for "My Account" and "More" to ensure consistent spacing and easy management of the menu items. The "Logout" item will have a distinct styling (red text and icon) as per the design.

## Proposed Changes

### Resources

#### [MODIFY] [colors.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/values/colors.xml)
- Add `profile_card_bg` (#F8F5FF).

#### [NEW] Drawables
- `ic_settings.xml`: Gear icon for the toolbar.
- `ic_location.xml`: Pin icon for Address Book.
- `ic_credit_card.xml`: Card icon for Payment Methods.
- `ic_help.xml`: Question mark icon for Help & Support.
- `ic_info.xml`: Info icon for About Us.
- `ic_lock.xml`: Lock icon for Privacy Policy.
- `ic_description.xml`: Paper icon for Terms & Conditions.
- `ic_logout.xml`: Exit icon for Logout.

### Data & Logic

#### [NEW] [ProfileMenuModel.kt](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/ProfileMenuModel.kt)
- Data class for menu items: `title`, `iconRes`, `isLogout` (boolean).

#### [NEW] [ProfileAdapter.kt](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/ProfileAdapter.kt)
- RecyclerView adapter for the menu items.

#### [NEW] [ProfileActivity.kt](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/ProfileActivity.kt)
- Activity logic to set up the menu lists, user info card, and bottom navigation.

### Layouts

#### [NEW] [item_profile_menu.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/layout/item_profile_menu.xml)
- Layout for individual menu rows (icon, title, chevron).

#### [NEW] [activity_profile.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/layout/activity_profile.xml)
- Main screen layout with Toolbar, User Info Card, and Sectioned Menu.

## Verification Plan

### Automated Tests
- Build the project to ensure no compilation errors.

### Manual Verification
- Deploy to an Android device/emulator.
- Verify:
    - Profile title (28sp, Bold) and Settings icon in the toolbar.
    - User Info Card with #F8F5FF background and circular image.
    - Menu items have correct icons and spacing.
    - "Logout" item is displayed in red.
    - Bottom navigation correctly highlights "Profile".
