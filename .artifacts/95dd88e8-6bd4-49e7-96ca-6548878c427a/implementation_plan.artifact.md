# Implementation Plan - Category Screen UI

Create a modern, responsive Category Screen UI in Android using XML, matching the provided reference image and specifications.

## Proposed Changes

### Resources & Assets
- Ensure all required colors are present in `colors.xml`.
- Verify and update vector drawables for all category icons and bottom navigation icons.
- Create/Update `nav_selector.xml` for Bottom Navigation.

### Layouts
#### [MODIFY] [activity_category.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/layout/activity_category.xml)
- Refine the `ConstraintLayout` structure.
- Ensure 25%/75% split for Sidebar and Right Content.
- Style the Top Bar (Title: 24sp Bold, #111827; Search icon).
- Add a top divider for Bottom Navigation.

#### [MODIFY] [item_sidebar.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/layout/item_sidebar.xml)
- Update layout to include an icon and text.
- Ensure proper padding and alignment.
- Add a selection indicator (vertical line) if visible in reference (it's subtle but often present in such designs). *Correction: Looking at the reference, it's just a background color change and text/icon color change for the whole item.*

#### [MODIFY] [item_category_row.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/layout/item_category_row.xml)
- Style as a 72dp height row with rounded card (16dp).
- Match text sizes: Name (Bold, 16sp), Item Count (14sp, #6B7280).
- Add Chevron icon.

### Code Implementation
#### [MODIFY] [CategoryActivity.kt](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/CategoryActivity.kt)
- Update dummy data to match the required list.
- Ensure adapters are correctly set up.

#### [MODIFY] [CategoryAdapter.kt](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/CategoryAdapter.kt)
- Ensure it handles the colorful icons correctly.

#### [MODIFY] [SidebarAdapter.kt](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/SidebarAdapter.kt)
- Refine selection logic and styling (Purple text/icon, Light Purple background).

## Verification Plan
- Build and run the app to check UI against the reference image.
- Verify responsiveness on different screen sizes (using Layout Editor).
- Ensure all icons load correctly.
