# Implementation Plan - Final Header Polish & Cut-off Fix

Resolve the issue where header icons (specifically the cart and badge) are being cut off by the screen edge and the header is too close to the status bar.

## Proposed Changes

### [Layouts]

#### [MODIFY] [fragment_category.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/fragment_category.xml)
- **Status Bar Fix**: Add `android:fitsSystemWindows="true"` to the root `ConstraintLayout` to ensure the header starts below the status bar.
- **Icon Padding**: Add `android:paddingEnd="12dp"` to the `topBar` `LinearLayout` to provide a safety margin from the right edge of the screen.
- **Standardize Containers**:
    - Set all icon `FrameLayout` widths to exactly `48dp`.
    - Ensure the cart badge (`tv_cart_badge`) has a sufficient `marginEnd` so it doesn't extend beyond the parent bounds.
- **Title Alignment**: Ensure the "Categories" title has consistent vertical centering within the 56dp bar.

### [Adapters]

#### [MODIFY] [SidebarAdapter.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/SidebarAdapter.java)
- Minor polish: If the first item "Electronic" looks too low, check the top padding/margin of the sidebar list or item.

## Verification Plan

### Manual Verification
- **Screenshot Check**: Take a new screenshot to verify that:
    - The "Cart" badge is fully visible and not touching the screen edge.
    - There is comfortable space between the "Categories" title and the status bar (clock/battery).
    - All icons (Search, Wishlist, Cart) are equally spaced and properly aligned.
- **Interaction**: Verify that all three header icons remain clickable.
