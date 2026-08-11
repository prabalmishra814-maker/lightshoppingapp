# Fix "cannot find symbol variable tvCartBadge" in ProductDetailActivity

The compilation error occurs because `tvCartBadge` is referenced in `ProductDetailActivity.java` but is missing from the `activity_product_detail.xml` layout file. This view is intended to show the number of items in the user's cart.

## Proposed Changes

### [Component Name] UI Layout

#### [MODIFY] [activity_product_detail.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/res/layout/activity_product_detail.xml)
- Add a cart icon (`iv_cart`) and a badge (`tv_cart_badge`) to the header section.
- Use a `RelativeLayout` or `FrameLayout` to overlay the badge on the cart icon.
- Ensure the ID `tv_cart_badge` matches what is expected by `ProductDetailActivity.java` (which uses `binding.tvCartBadge`).

### [Component Name] Logic

#### [MODIFY] [ProductDetailActivity.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/ProductDetailActivity.java)
- Add a click listener to `iv_cart` (the new cart icon) to navigate to `CartActivity`. This improves the user experience by allowing them to view their cart directly from the product details page.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:compileDebugJavaWithJavac` to verify that the "cannot find symbol" error is resolved.

### Manual Verification
- Deploy the app to a device/emulator.
- Open the Product Detail screen.
- Verify that the cart icon appears in the header.
- Add an item to the cart and verify that the badge updates correctly.
- Click the cart icon and verify it navigates to the Cart screen.
