# Implementation Plan - Standardize Product Card UI

Standardize the size of product cards across the home screen and ensure discount badges are correctly displayed on top of product images.

## Proposed Changes

### [Layouts]

#### [MODIFY] [item_deal.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/item_deal.xml)
- Change `layout_width` to `160dp` for consistency.
- Move `tv_deal_discount` declaration after `iv_deal_product` inside the `RelativeLayout` to ensure it appears in front.
- Add `android:elevation="4dp"` to `tv_deal_discount` as an extra safety measure.
- Set a fixed height or `minLines` for `tv_deal_name` to keep card heights uniform.

#### [MODIFY] [item_product.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/item_product.xml)
- Ensure `layout_width` is `160dp`.
- Increase `elevation` for `tv_discount` to ensure it stays above the `image_container`.
- Set a fixed height or `minLines` for `tv_product_name`.

## Verification Plan

### Automated Tests
- Build the project to ensure XML validity.

### Manual Verification
- Deploy to device/emulator.
- Check "Explore Products" (horizontal list) on the home screen:
    - Verify all cards have the same width.
    - Verify the blue discount badge (e.g., -27%) is visible on top of the product image.
- Check "Best Selling" or other sections using `item_product.xml`:
    - Verify badge visibility and card uniformity.
