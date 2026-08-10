# Implementation Plan - Standardize Brand Color to Primary Blue

Replace all occurrences of `@color/primary` (Purple) with `@color/primary_blue` (Blue) to unify the application's branding.

## Proposed Changes

### [Layout Components]

#### [MODIFY] [activity_auth.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/activity_auth.xml)
- Replace all `@color/primary` with `@color/primary_blue`.
- Change `android:backgroundTint` to `app:backgroundTint` for `MaterialButton` components (`btn_login`, `btn_register`).
- Update `app:boxStrokeColor`, `android:textColor`, and `android:buttonTint` references.

#### [MODIFY] [activity_checkout.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/activity_checkout.xml)
- Update "Coming Soon" text color to `@color/primary_blue`.

#### [MODIFY] [activity_wishlist.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/activity_wishlist.xml)
- Update `btnContinueShopping` to use `app:backgroundTint="@color/primary_blue"`.

#### [MODIFY] [fragment_home.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/fragment_home.xml)
- Update search icon tint, cart badge background, and "View All" text colors to `@color/primary_blue`.

#### [MODIFY] [item_product.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/item_product.xml)
- Update price text color and "Add to Cart" button background to `@color/primary_blue`.

#### [MODIFY] [item_wishlist.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/item_wishlist.xml)
- Update price text color and "Add to Cart" button stroke color to `@color/primary_blue`.

### [Drawable & Color Resources]

#### [MODIFY] [nav_selector.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/color/nav_selector.xml)
- Update active state color to `@color/primary_blue`.

#### [MODIFY] [bg_button_purple.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/drawable/bg_button_purple.xml)
- Update solid color to `@color/primary_blue`.

#### [MODIFY] [bg_edittext.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/drawable/bg_edittext.xml)
- Update focus stroke color to `@color/primary_blue`.

#### [MODIFY] [bg_toggle_selected.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/drawable/bg_toggle_selected.xml)
- Update solid color to `@color/primary_blue`.

#### [MODIFY] [ic_dot_active.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/drawable/ic_dot_active.xml)
- Update solid color to `@color/primary_blue`.

## Verification Plan

### Automated Tests
- Run Gradle build to ensure all layout files are valid.

### Manual Verification
- Walk through the app (Home, Auth, Wishlist, Checkout) to ensure all purple elements have successfully transitioned to blue.
