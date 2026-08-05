# Profile Screen Walkthrough

I have recreated the Profile screen to match the reference image exactly, using Material Design 3 and XML layouts.

## Changes Made

### UI Components
* **activity_profile.xml**: Implemented the main layout using `NestedScrollView` and `ConstraintLayout`.
    * Created the "Profile" toolbar with a settings icon.
    * Designed the User Info Card with a circular profile image and light purple background (#F8F5FF).
    * Integrated two RecyclerViews for "My Account" and "More" sections to ensure a clean and scalable list.
    * Added `BottomNavigationView` with correct icons and states.
* **item_profile_menu.xml**: Created a reusable layout for menu items with an icon, title, and chevron arrow, matching the 56dp height and specific dividers.

### Code Implementation
* **ProfileActivity.kt**:
    * Initialized the UI and populated the "My Account" and "More" menus with appropriate data and icons.
    * Set up the Bottom Navigation to highlight the "Profile" tab.
* **ProfileAdapter.kt**: A flexible adapter that handles both standard and special items (like the red Logout button).
* **ProfileMenuModel.kt**: A simple data class to manage menu item properties.

### Resources
* **colors.xml**: Added `profile_card_bg` (#F8F5FF) and `color_logout` (#DC2626).
* **themes.xml**: Added `CircleImage` style for circular images.
* **Vector Drawables**: Created and updated icons for Location, Credit Card, Help, Info, Privacy, Description, Logout, and Settings to ensure they are high-quality and consistent.

## Verification
* Verified that the layout is responsive and matches the spacing, typography, and colors of the reference image.
* Successfully built the project (`gradle build`) to ensure all binding and resource references are correct.
