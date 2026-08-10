# Implementation Plan - Remove Header Icons from Category Fragment

Remove Search, Wishlist (Like), and Cart icons from the header in the Categories screen as requested.

## Proposed Changes

### [Layout]

#### [MODIFY] [fragment_category.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/fragment_category.xml)
- Remove the `FrameLayout` containing `btnSearch`.
- Remove the `FrameLayout` containing `btnWishlist`.
- Remove the `FrameLayout` containing `btnCart`.
- Remove the spacer `View` that pushes these icons to the right.

### [Java Code]

#### [MODIFY] [CategoryFragment.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/CategoryFragment.java)
- Remove `setupHeaderActions()` call and method definition.
- Remove `updateCartBadge()` call and method definition.

## Verification Plan

### Automated Tests
- Build the project to ensure no compilation errors due to missing view binding references.

### Manual Verification
- Deploy the app to a device/emulator.
- Navigate to the "Categories" tab.
- Verify that only the "Categories" title is visible in the header, and the search, heart, and cart icons are gone.
