# Implementation Plan - Reduce Product Recommendation Size

The user wants to reduce the size of the product recommendation items and their containers in the Product Detail view.

## Proposed Changes

### [Recommendation Item Layout](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/item_product_recommendation.xml)

#### [MODIFY] [item_product_recommendation.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/item_product_recommendation.xml)
- Reduce `MaterialCardView` width from `180dp` to `160dp`.
- Reduce `MaterialCardView` height from `300dp` to `260dp`.
- Change `ConstraintLayout` height from `261dp` to `match_parent`.
- Reduce `ImageView` (product image) height from `140dp` to `120dp`.

### [Product Detail Layout](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/activity_product_detail.xml)

#### [MODIFY] [activity_product_detail.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/activity_product_detail.xml)
- Update `rv_you_may_also_like` height to `275dp`.
- Update `rv_similar_products` height to `275dp`.
- Update `rv_recently_viewed` height to `275dp`.
- Update `rv_more_products` height to `275dp`.

## Verification Plan

### Manual Verification
- Render the `item_product_recommendation` preview to ensure the elements are not cramped.
- Run the app and check the horizontal scroll sections to verify they look proportional.
