# Implementation Plan - Category Screen UI

Create a modern Category screen for the LightShop app following Material Design 3 guidelines. The screen will feature a dual-pane layout with a sidebar for category selection and a main content area for detailed items.

## Proposed Changes

### Resources

#### [MODIFY] [colors.xml](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/values/colors.xml)
- Add `#F3E8FF` (Selected Background), `#7C3AED` (Primary Purple), and other missing colors from the spec.

#### [NEW] Drawables
Create missing vector drawables:
- `ic_kids.xml`
- `ic_sports.xml`
- `ic_car.xml`
- `ic_book.xml`
- `ic_grocery.xml`
- `ic_chevron_right.xml`
- `ic_all_categories.xml`

### Layouts

#### [NEW] [item_sidebar.xml](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/layout/item_sidebar.xml)
- Layout for the left sidebar items (Icon + Text).
- Support for selected state (Purple background/text).

#### [NEW] [item_category_row.xml](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/layout/item_category_row.xml)
- Layout for the right content items (Icon, Title, Item Count, Chevron).
- 72dp height, rounded card style.

#### [NEW] [activity_category.xml](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/layout/activity_category.xml)
- Main layout with:
    - Custom App Bar (Title "Category" + Search icon).
    - ConstraintLayout splitting screen into Sidebar (25%) and Content (75%).
    - BottomNavigationView at the bottom.

### Code

#### [NEW] [Category.kt](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/Category.kt)
- Data class for category items.

#### [NEW] [SidebarAdapter.kt](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/SidebarAdapter.kt)
- Adapter for the sidebar RecyclerView.

#### [NEW] [CategoryAdapter.kt](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/CategoryAdapter.kt)
- Adapter for the right-side list RecyclerView.

#### [NEW] [CategoryActivity.kt](file:///C:/Users/PRABAL%20MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/CategoryActivity.kt)
- Main activity logic to initialize RecyclerViews and handle selection.

## Verification Plan

### Automated Tests
- Build the project to ensure no resource or code errors.

### Manual Verification
- Deploy to an emulator/device.
- Verify sidebar selection changes the UI state.
- Verify responsiveness on different screen sizes (using layout inspector if possible, or just visual check).
- Check color and typography matches the reference image.
