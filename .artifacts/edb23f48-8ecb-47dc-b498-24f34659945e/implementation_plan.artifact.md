# Fix Compilation Error in ProductDetailActivity

The `ProductDetailActivity.java` fails to compile because it references `tvProductDescription` and `productTabs` from the View Binding class, but these IDs are missing in the `activity_product_detail.xml` layout file.

## Proposed Changes

### [Layouts]

#### [MODIFY] [activity_product_detail.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/activity_product_detail.xml)

- Add a `TabLayout` with ID `product_tabs` in the designated Tabs Section.
- Add a `TextView` with ID `tv_product_description` inside the "About This Item Card" section.
- Ensure the "About This Item Card" has a `LinearLayout` container for the description.

## Verification Plan

### Automated Tests
- Run `:app:compileDebugJavaWithJavac` to verify the compilation error is resolved.

### Manual Verification
- Deploy the app to a device/emulator.
- Navigate to the Product Detail screen and verify that the product description and tabs are visible.
