# Fix "cannot find symbol variable tvCartBadge" in ProductDetailActivity

The compilation error occurs because `tvCartBadge` is referenced in `ProductDetailActivity.java` but is missing from the layout file `activity_product_detail.xml`. This task involves adding the missing UI elements (Cart icon and badge) to the header and setting up the navigation to the Cart screen.

## Proposed Changes

### [Component] UI Layout

#### [NEW] [bg_circle_red.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/drawable/bg_circle_red.xml)
- Create a circular red background for the cart badge.

#### [MODIFY] [activity_product_detail.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/activity_product_detail.xml)
- Add a cart icon (`iv_cart`) and a badge (`tv_cart_badge`) to the header section.
- Use a `FrameLayout` to overlay the badge on the cart icon.
- Position it after the wishlist icon in the horizontal header.

### [Component] Activity Logic

#### [MODIFY] [ProductDetailActivity.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/ProductDetailActivity.java)
- Add a click listener to the newly added cart icon (`iv_cart`) to navigate to `CartActivity`.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:compileDebugJavaWithJavac` to ensure the compilation error is resolved.

### Manual Verification
- Deploy the app to a device.
- Open a product detail screen.
- Verify that the cart icon is visible in the header.
- Add an item to the cart and verify that the badge updates with the correct count.
- Click the cart icon and verify it navigates to the Cart screen.
