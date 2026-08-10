# Fix Quantity Counter in CartActivity

The goal is to fix the quantity counter logic in `CartActivity` and `CartAdapter` to ensure it updates correctly, handles item removal when quantity reaches zero, and provides a smoother user experience via optimistic updates.

## User Review Required

> [!IMPORTANT]
> The quantity update logic will now automatically remove an item from the cart if its quantity is reduced to zero. Previously, it simply did nothing.

## Proposed Changes

### [Component] Cart Logic and UI

#### [MODIFY] [CartActivity.java](file:///C:/Users/Harish Computer/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/CartActivity.java)
- Update `onQuantityChanged` to:
    - Use correct column name `product_id` for database filters (fixing potential case-sensitivity issues).
    - Implement **Optimistic UI Updates**: change the quantity in the list and update the total price immediately.
    - Handle removal when quantity reaches 0 by calling `onRemoveItem`.
    - Add a rollback mechanism if the API call fails.
- Ensure `CartManager` and the local `cartItems` list stay perfectly in sync.

#### [MODIFY] [CartAdapter.java](file:///C:/Users/Harish Computer/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/CartAdapter.java)
- Ensure the quantity display is always synced with the data model.
- Clean up price display to avoid potential double currency symbols (e.g., "₹₹").

#### [MODIFY] [utils/CartManager.java](file:///C:/Users/Harish Computer/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/utils/CartManager.java)
- Add a method to update quantity by product ID to ensure consistency across different list instances.

## Verification Plan

### Automated Tests
- Build the project to ensure no syntax errors.

### Manual Verification
1.  Open the Cart Activity.
2.  Click the "+" button on an item: The quantity should increase immediately, and the "Price Details" at the bottom should update instantly.
3.  Click the "-" button: The quantity should decrease, and totals should update.
4.  Decrease an item's quantity to 1, then click "-" again: The item should be removed from the cart.
5.  Verify that the total amount, discount, and savings message are all updated correctly after each change.
