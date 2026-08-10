# Implementation Plan - Product Recommendation Sections

Add "You May Also Like", "Similar Products", and "Recently Viewed" sections to the Product Detail screen.

## User Review Required

> [!IMPORTANT]
> The existing Product Detail UI will remain unchanged. The new sections will be appended below the current content within the same vertical scroll view.

## Proposed Changes

### [Layouts]

#### [NEW] [item_product_recommendation.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/item_product_recommendation.xml)
- Create a new layout for the recommendation cards following the specific design requirements:
    - Width: 180dp, Height: 300dp.
    - White background, 16dp corner radius.
    - Product image (150dp, FitCenter).
    - Wishlist heart icon (top-right).
    - Product name (max 2 lines, Bold, Dark Navy).
    - Rating badge (Green, e.g., 4.3 ★ (2,345)).
    - Price (Bold), MRP (Strikethrough), Discount (Green).
    - Stock status (Green, "In Stock").

#### [MODIFY] [activity_product_detail.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/activity_product_detail.xml)
- Add the following sections at the bottom of the `LinearLayout` inside the `NestedScrollView`:
    - **You May Also Like**: Horizontal `RecyclerView`.
    - **Similar Products**: Horizontal `RecyclerView`.
    - **Recently Viewed**: Horizontal `RecyclerView`.
    - **More Products**: Another `RecyclerView` or similar section.

### [Logic]

#### [NEW] [RecommendationAdapter.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/RecommendationAdapter.java)
- A new adapter for the recommendation carousels using `item_product_recommendation.xml`.
- Implement click listener to open `ProductDetailActivity` with the selected product.
- Implement Wishlist toggle logic consistent with the existing app.

#### [MODIFY] [ProductDetailActivity.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/ProductDetailActivity.java)
- Initialize the new `RecyclerView`s.
- Fetch recommendation data from Supabase (or use dummy data if needed to fulfill "6-10 products" requirement initially).
- Implement "Recently Viewed" logic by saving the current product to a local list (e.g., in `SessionManager` or a singleton).

## Verification Plan

### Automated Tests
- Build the project to ensure no compilation errors.
- Run the app and navigate to a product detail screen.

### Manual Verification
- Verify that the new sections appear below the existing product details.
- Verify vertical scrolling of the entire page.
- Verify horizontal scrolling of the recommendation carousels.
- Verify that clicking a recommended product reloads the screen with the new product's details.
- Verify that "Add to Cart" and "Wishlist" functionalities work for recommended products.
- Verify that the Cart badge updates correctly.
