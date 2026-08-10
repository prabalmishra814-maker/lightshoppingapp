# Recreate Item View / Product Detail UI

This plan outlines the steps to recreate the Product Detail screen exactly as described in the requirements, matching the professional Indian e-commerce style (Amazon/Flipkart inspired).

## User Review Required

> [!IMPORTANT]
> The UI will be implemented as a new `ProductDetailFragment` to maintain consistency with the existing fragment-based architecture in `HomeActivity`.
> Existing Cart and Wishlist systems will be integrated to ensure dynamic updates (e.g., cart badge count).

## Proposed Changes

### Assets & Resources

#### [MODIFY] [colors.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/values/colors.xml)
*   Ensure all specified colors (#1469E8, #101828, #667085, #F2F4F7, #0AAE55) are present and correctly named. (Already verified as present).

#### [MODIFY] [styles.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/values/styles.xml)
*   Add custom styles for tabs and text if necessary.

---

### Layouts

#### [NEW] [fragment_product_detail.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/fragment_product_detail.xml)
*   Implement a vertically scrollable `NestedScrollView`.
*   **Header**: Custom toolbar with back arrow, title, share, wishlist, and cart (with badge).
*   **Product Image**: Large rounded container with discount badge, video button, and carousel dots.
*   **Product Info**: Title, ratings, price, lowest price pill, and stock status.
*   **Actions**: Quantity selector row and "Add to Cart" button.
*   **Benefits Card**: Horizontal card for delivery, replacement, and original products.
*   **Offers Card**: Rounded card for bank/wallet offers.
*   **Tabs**: TabLayout for Product Details, Reviews, and Questions.
*   **About Card**: Feature-rich "About This Item" section with icons.
*   **Sticky Bottom Bar**: "Add to Wishlist" and "Buy Now" buttons.

---

### Components & Logic

#### [NEW] [ProductDetailFragment.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/ProductDetailFragment.java)
*   Use View Binding.
*   Populate UI using `ProductModel` passed via arguments.
*   Implement quantity increase/decrease logic.
*   Handle "Add to Cart" and "Add to Wishlist" using `SupabaseClient`.
*   Handle tab switching.
*   Implement "Buy Now" navigation to `CheckoutActivity`.

#### [MODIFY] [HomeFragment.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/HomeFragment.java)
*   Update `ProductAdapter` to include an `itemView` click listener that opens `ProductDetailFragment`.

#### [MODIFY] [HomeActivity.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/HomeActivity.java)
*   Add a helper method `openProductDetail(ProductModel product)` to handle the fragment transaction with backstack support.

---

### Integration

#### [MODIFY] [WishlistActivity.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/WishlistActivity.java)
*   Update `onItemClick` to open the `ProductDetailFragment` (might require starting `HomeActivity` with specific intent or handling in `WishlistActivity`).

## Verification Plan

### Automated Tests
*   `gradle build` to ensure no syntax errors.

### Manual Verification
1.  Launch the app and tap on a product (e.g., boAt Airdopes) from the Home screen.
2.  Verify the Product Detail UI against the specifications:
    *   Scroll performance.
    *   Sticky bottom bar visibility.
    *   Quantity selector functionality (+ and -).
    *   "Add to Cart" updates the cart count.
    *   "Wishlist" toggles the heart icon state.
    *   Tabs switch content correctly.
3.  Check layout responsiveness on different device widths in Android Studio.
