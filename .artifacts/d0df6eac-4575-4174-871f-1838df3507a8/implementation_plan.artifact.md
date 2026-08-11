# Implementation Plan - Restore Lite Basket UI

Restore the home screen UI to match the "Lite Basket" design as shown in the reference image.

## Proposed Changes

### [Resources]

#### [NEW] [ic_logo_basket.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/drawable/ic_logo_basket.xml)
Create a yellow shopping basket icon for the app logo.

#### [MODIFY] [colors.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/values/colors.xml)
Ensure colors like `primary_blue`, `logo_yellow`, and background colors are defined.

#### [MODIFY] [strings.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/values/strings.xml)
Add strings for "Lite Basket", search hint, location text, timer labels, etc.

### [Layouts]

#### [MODIFY] [fragment_home.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/fragment_home.xml)
*   **Top Bar**: Menu, Logo + Text, Notification, Cart.
*   **Search Bar**: Rounded with Search, Camera, and Mic icons.
*   **Location Row**: Icon, Address, Change button.
*   **Offer Banner**: Styled ImageSlider.
*   **Categories**: Horizontal list with circular icons.
*   **Deals of the Day**: Header with timer, horizontal product list.
*   **Best Selling**: Header with View All, horizontal product list.

### [Java]

#### [MODIFY] [HomeFragment.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/HomeFragment.java)
*   Update UI references for the new layout.
*   Implement the countdown timer for "Deals of the Day".
*   Update `setupImageSlider` with relevant banners.
*   Ensure click listeners for search, location, and headers are functional.

## Verification Plan

### Manual Verification
*   Deploy to device/emulator.
*   Visually compare the UI with the provided image.
*   Test horizontal scrolling for categories, deals, and best selling.
*   Check if the timer in "Deals of the Day" is counting down.
*   Verify click actions for search bar, cart, and product items.
