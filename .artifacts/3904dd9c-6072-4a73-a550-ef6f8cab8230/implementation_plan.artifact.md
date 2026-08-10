# Implementation Plan - Fix Missing Symbols and Restore Header Icons

The user is encountering a `cannot find symbol variable btnSearch` error in `CategoryFragment.java`. This indicates that the layout `fragment_category.xml` is missing the `btn_search` ID, while the Java code (or the user's version of it) expects it. Research shows a recent plan was to remove these icons, but the build is failing for the user. We will restore these icons to provide a complete shopping experience and fix the build error.

## User Review Required

> [!IMPORTANT]
> This plan will restore the Search, Wishlist, and Cart icons to the Categories screen header. If you intended to keep them removed, please let me know, and I will instead remove the remaining references in the Java code.

## Proposed Changes

### [Layout]

#### [MODIFY] [fragment_category.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/fragment_category.xml)
- Update the `topBar` to include:
    - `ImageView` with ID `btn_search` for search functionality.
    - `ImageView` with ID `btn_wishlist` for wishlist access.
    - `RelativeLayout` containing an `ImageView` and `TextView` (ID `tv_cart_badge`) for the cart.

### [Java Code]

#### [MODIFY] [CategoryFragment.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/CategoryFragment.java)
- Restore `SessionManager` initialization in `onViewCreated`.
- Add `btnSearch`, `btnWishlist`, and `btnCart` click listeners.
- Restore the `updateCartBadge()` method to fetch and display the cart count.
- Add necessary imports (`Map`, `SessionManager`, `Intent`, etc.).

## Verification Plan

### Automated Tests
- Run `./gradlew :app:compileDebugJavaWithJavac` to ensure the project builds without symbol errors.

### Manual Verification
- Deploy the app and navigate to the "Categories" tab.
- Verify that the Search, Heart, and Cart icons are visible in the top bar.
- Verify that clicking the Cart icon opens the Cart screen.
