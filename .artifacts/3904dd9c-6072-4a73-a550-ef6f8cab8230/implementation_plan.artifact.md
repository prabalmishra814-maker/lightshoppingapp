# Implementation Plan - Professional E-commerce Categories Screen

Fix the existing `CategoryFragment` to create a complete, professional Categories screen with a two-column sidebar/grid layout, real project assets, and proper data population.

## User Review Required

> [!IMPORTANT]
> The Categories screen will be populated with a local list of 14+ categories and their respective subcategories to ensure a professional look, as requested. I will use the existing drawable assets found in your project.

## Proposed Changes

### [Layouts]

#### [MODIFY] [fragment_category.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/fragment_category.xml)
- Update the `topBar` to include Search, Wishlist, and Cart icons.
- Ensure the main body `LinearLayout` uses `0dp` height and `weight="1"` to fill the screen correctly.
- Add `tv_section_label` (e.g., "ALL ELECTRONICS") at the top of the right content area.
- Ensure both `rvSidebar` and `rvCategories` have `match_parent` height and correct weights (0.27 and 0.73).

#### [MODIFY] [item_sidebar.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/item_sidebar.xml)
- Adjust height to be more compact (e.g., `85dp`) to fit more categories.
- Ensure the blue selection indicator is properly positioned on the left.
- Style the category name text and image container to match the requested design.

#### [MODIFY] [item_subcategory_grid.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/item_subcategory_grid.xml)
- Ensure the subcategory image is center-aligned and styled with a rounded background.
- Adjust text styling for professional look.

### [Java Code]

#### [MODIFY] [CategoryFragment.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/CategoryFragment.java)
- Implement a `loadProfessionalData()` method to populate the categories list with 14+ items using local resource IDs.
- Create a mapping for subcategories for each category.
- Fix `setupSidebar()` and `setupSubCategories()` to handle selection and data updates correctly.
- Implement click listeners for header icons (Search, Wishlist, Cart).
- Ensure `updateSectionLabel()` correctly updates the "ALL [CATEGORY]" title.

#### [MODIFY] [SidebarAdapter.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/SidebarAdapter.java)
- Update `onBindViewHolder` to support both URL-based and resource-based images.
- Refine selection styling (blue text, blue indicator, light blue background).

#### [MODIFY] [CategoryAdapter.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/CategoryAdapter.java)
- Update `onBindViewHolder` to support both URL-based and resource-based images for subcategories.
- Improve grid item styling.

## Verification Plan

### Automated Tests
- Build the project to ensure no compilation errors.
- Verify `viewBinding` references are all correct.

### Manual Verification
- Deploy to emulator/device.
- Navigate to Categories tab.
- Verify 14+ categories in the left sidebar with real icons.
- Verify 2-column grid in the right content.
- Verify scrolling in both areas is independent.
- Verify tapping a category updates the right-side grid and title.
- Verify header icons are present and clickable.
