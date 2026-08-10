# Create Add Address Activity

This plan outlines the steps to create a new activity for adding an address, including the layout and the Java implementation.

## Proposed Changes

### [Component Name]

#### [NEW] [activity_add_address.xml](file:///C:/Users/Harish Computer/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/activity_add_address.xml)
Create the layout for the Add Address activity using Material Design components. It will include fields for:
- Full Name
- Phone Number
- Pin Code
- State
- City
- House No., Building Name
- Road Name, Area, Colony
- Landmark (Optional)
- Address Type (Home/Work radio buttons)
- Save Button

#### [NEW] [AddAddressActivity.java](file:///C:/Users/Harish Computer/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/example/lightshop/AddAddressActivity.java)
Create the Activity class to handle user input and save the address.

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/Harish Computer/AndroidStudioProjects/lightshoppingapp/app/src/main/AndroidManifest.xml)
Register the new `AddAddressActivity` in the manifest.

## Verification Plan

### Automated Tests
- N/A (Manual verification on device)

### Manual Verification
- Deploy the app.
- Trigger the activity (can be done via Intent from another activity or by making it the launcher activity temporarily for testing).
- Fill in the fields and click "Save".
- Verify that the layout looks correct and fields are working.
