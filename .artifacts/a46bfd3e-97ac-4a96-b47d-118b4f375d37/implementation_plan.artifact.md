# Create New Home Screen XML

The goal is to create a premium E-commerce Home Screen in a new XML file `activity_home.xml`, matching the provided reference design exactly. A separate `HomeActivity` will be created to host this layout.

## Proposed Changes

### [Resources]

#### [NEW] [ic_notification.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/drawable/ic_notification.xml)
- Vector icon for notifications.

#### [NEW] [ic_cart.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/drawable/ic_cart.xml)
- Vector icon for the shopping cart.

#### [NEW] [ic_search.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/drawable/ic_search.xml)
- Vector icon for the search bar.

#### [NEW] [bg_search_bar.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/drawable/bg_search_bar.xml)
- Rounded background for the search bar (Radius 28dp).

#### [NEW] [bg_offer_banner.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/drawable/bg_offer_banner.xml)
- Gradient background (#7C3AED to #9F67FF) with 24dp corner radius.

#### [NEW] [ic_home.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/drawable/ic_home.xml), [ic_category.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/drawable/ic_category.xml), [ic_orders.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/drawable/ic_orders.xml), [ic_profile.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/drawable/ic_profile.xml)
- Vector icons for bottom navigation.

#### [NEW] [item_category.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/layout/item_category.xml)
- Layout for horizontal category RecyclerView.

#### [NEW] [item_product.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/layout/item_product.xml)
- Layout for horizontal top deals RecyclerView.

### [Layouts]

#### [NEW] [activity_home.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/layout/activity_home.xml)
- Root: `NestedScrollView` -> `ConstraintLayout`.
- Top: Welcome text and action icons (Notification, Cart).
- Search Section: Rounded search bar and filter/category button.
- Banner Section: Large gradient card with "Shop Now" button and illustration placeholder.
- Categories Section: Title + "View All" and horizontal `RecyclerView`.
- Top Deals Section: Title + "View All" and horizontal `RecyclerView`.
- Bottom Navigation: Material `BottomNavigationView`.

### [Activities]

#### [NEW] [HomeActivity.java](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/java/com/example/lightshop/HomeActivity.java)
- Basic Activity setup to set `activity_home.xml` as content.

## Verification Plan

### Manual Verification
- Deploy the app (linking from Login to Home for testing).
- Verify the UI matches the reference image:
    - Colors and gradients.
    - Corner radii and spacing.
    - Bottom navigation appearance.
    - RecyclerView item layouts.
