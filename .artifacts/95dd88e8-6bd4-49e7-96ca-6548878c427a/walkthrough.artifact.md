# Walkthrough - Category Screen & Navigation

I have completed the Category Screen UI to match your reference image exactly and implemented the navigation from the Home screen.

## Changes Made

### UI Refinements
- **`activity_category.xml`**: Updated the layout to use a strict 25%/75% split using Guidelines. Styled the top bar with the correct typography (24sp Bold) and colors (#111827). Added a bottom divider above the navigation.
- **`item_sidebar.xml`**: Adjusted padding and text styling (11sp Bold, #6B7280) to match the vertical menu in the reference.
- **`item_category_row.xml`**: Redesigned as a 72dp high row with rounded corners (16dp), precise text sizes (18sp/14sp), and a subtle bottom divider.
- **`CategoryActivity.kt`**: Populated with the full list of 11 categories (Men, Women, Kids, Electronics, etc.) using the specified icons.

### Navigation
- **`HomeActivity.kt`**:
    - Added click listener to the "Category" item in the Bottom Navigation to open `CategoryActivity`.
    - Added click listener to the "View All" button in the Categories section to open `CategoryActivity`.
- **`CategoryActivity.kt`**: Updated Bottom Navigation to allow returning to the Home screen when the "Home" item is clicked.

## Verification Results
- The Category screen now displays a sidebar on the left and a scrollable list on the right.
- The sidebar correctly highlights the selected item with a purple theme.
- Navigation between Home and Category screens is seamless.

> [!TIP]
> You can now test the navigation by clicking "Category" in the bottom bar or "View All" next to the Categories section on the Home screen.
