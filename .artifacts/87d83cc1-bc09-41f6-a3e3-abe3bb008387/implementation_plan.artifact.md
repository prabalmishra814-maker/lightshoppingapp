# Implementation Plan - Restructure Product Details and Remove Tabs

The user wants to remove the "Specs" and "Reviews" tabs and style the "Details" (description) section to match the look of the "Offers" section.

## Proposed Changes

### [app]

#### [MODIFY] [activity_product_detail.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/activity_product_detail.xml)

- Remove the `TabLayout` (`product_tabs`) and its preceding divider.
- Update the "About This Item" `MaterialCardView`:
    - Use `RelativeLayout` as the content container.
    - Add an `ImageView` for an icon (using `ic_description`).
    - Add a `TextView` for the label ("Product Details").
    - Place the `tv_product_description` `TextView` below the label, matching the spacing and layout of the "Offers" section.
    - Match card properties (corner radius, elevation, stroke) with the "Offers" card.

#### [MODIFY] [ProductDetailActivity.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/ProductDetailActivity.java)

- Remove the `addOnTabSelectedListener` for `productTabs` to avoid compilation errors after the view is removed.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:compileDebugJavaWithJavac` to ensure the project builds without errors.

### Manual Verification
- Deploy the app and navigate to the Product Detail screen.
- Verify that the Tabs are gone and the "Product Details" section now looks like the "Offers" section.
