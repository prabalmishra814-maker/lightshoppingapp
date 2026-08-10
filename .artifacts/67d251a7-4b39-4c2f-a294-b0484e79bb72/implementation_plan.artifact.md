# Recreate Item View / Product Detail UI

This plan outlines the steps to recreate the Product Detail screen exactly as shown in the reference image. The UI will be implemented as a new Fragment within the existing `HomeActivity` architecture.

## User Review Required

> [!IMPORTANT]
> - The UI will be implemented as a new `ProductDetailFragment`.
> - Navigation from `HomeFragment` and `WishlistActivity` (if applicable) will be updated to open this fragment.
> - The fragment will be loaded into the `HomeActivity`'s `fragment_container`, maintaining the existing bottom navigation.

## Proposed Changes

### [Component] UI Resources

#### [MODIFY] [colors.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/values/colors.xml)
- Added professional e-commerce palette: `primary_blue`, `dark_navy`, `secondary_gray`, `success_green`, etc.

#### [NEW] [ic_share.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/drawable/ic_share.xml)
- Vector icon for sharing.

#### [NEW] [ic_play_circle.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/drawable/ic_play_circle.xml)
- Vector icon for the video button.

#### [NEW] [ic_delivery_truck.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/drawable/ic_delivery_truck.xml)
- Vector icon for free delivery.

#### [NEW] [ic_replacement.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/drawable/ic_replacement.xml)
- Vector icon for 7 days replacement.

#### [NEW] [ic_verified_quality.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/drawable/ic_verified_quality.xml)
- Vector icon for 100% original products.

#### [NEW] [bg_rounded_card.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/drawable/bg_rounded_card.xml)
- Background for cards with specific corner radius and border.

### [Component] Models

#### [MODIFY] [ProductModel.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/models/ProductModel.java)
- Added `rating`, `reviewsCount`, and `soldCount` fields with default values to match the reference image if DB data is missing.

### [Component] Layouts

#### [NEW] [fragment_product_detail.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/fragment_product_detail.xml)
- Implements the entire UI structure:
  - Custom Toolbar with "Item View" title and action icons.
  - `NestedScrollView` for the main content.
  - Image section with discount badge and dots.
  - Product info (Title, Rating, Price).
  - Quantity selector and "Add to Cart" button row.
  - Delivery benefits card.
  - Offers card.
  - TabLayout for Details, Reviews, Questions.
  - "About This Item" card.
  - Sticky bottom action bar with "Add to Wishlist" and "Buy Now".

### [Component] Fragments

#### [NEW] [ProductDetailFragment.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/ProductDetailFragment.java)
- Handles UI population from `ProductModel`.
- Quantity increment/decrement logic.
- "Add to Cart" logic (using `SupabaseApiService`).
- "Add to Wishlist" logic.
- "Buy Now" logic (navigates to checkout).
- Tab switching logic (even if content is static for now).

### [Component] Adapters & Activities

#### [MODIFY] [HomeFragment.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/HomeFragment.java)
- Update `ProductAdapter` to include an `onClickListener` on `itemView`.
- Navigate to `ProductDetailFragment` on click, passing the `ProductModel`.

#### [MODIFY] [HomeActivity.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/HomeActivity.java)
- Add a method `openProductDetail(ProductModel product)` to handle the fragment replacement and backstack.

## Verification Plan

### Automated Tests
- Build the project and ensure no compilation errors.
- Verify that `ProductModel` serialization still works.

### Manual Verification
1. Open the app and tap on a product from the Home screen.
2. Verify that the Product Detail screen opens with the correct data.
3. Compare the UI against the reference image for:
   - Header icons and title.
   - Image section (badge, dots, play button).
   - Rating and Price styling.
   - Delivery benefits icons and layout.
   - Offers card appearance.
   - Tabs styling and selection.
   - "About This Item" card.
   - Bottom sticky buttons.
4. Test functionality:
   - Back button returns to Home.
   - Quantity +/- updates the counter.
   - "Add to Cart" updates the cart in the database and shows feedback.
   - "Add to Wishlist" toggles wishlist state.
   - "Buy Now" opens the Checkout activity.
