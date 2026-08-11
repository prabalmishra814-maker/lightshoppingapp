# Fix Home Screen Header

The home screen header in `fragment_home.xml` has several layout issues, including missing constraints and poor positioning of the notification and cart icons.

## User Review Required

> [!IMPORTANT]
> I will be updating the header layout to ensure all elements (Logo, Title, Notification, and Cart) are correctly aligned and visible. The cart badge will be positioned at the top-right of the cart icon.

## Proposed Changes

### [Layout]

#### [MODIFY] [fragment_home.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/fragment_home.xml)
- Add `app:layout_constraintStart_toStartOf="parent"` to `logo_container`.
- Update `iv_home_notification` constraints to properly align with the cart container.
- Use a `FrameLayout` or better `RelativeLayout` for the cart badge to ensure it sits on top of the icon without obscuring it completely.
- Adjust the `logo_container` layout for better vertical alignment.
- Remove `tools:layout_editor_absoluteX`.

## Verification Plan

### Automated Tests
- Build the app to ensure no compilation or layout inflation errors.

### Manual Verification
- Deploy the app and visually inspect the home screen header.
- Confirm that the logo and text "Lite Basket" are on the left.
- Confirm that the notification icon and cart icon (with badge) are on the right.
- Verify that the header remains "sticky" at the top as intended.
