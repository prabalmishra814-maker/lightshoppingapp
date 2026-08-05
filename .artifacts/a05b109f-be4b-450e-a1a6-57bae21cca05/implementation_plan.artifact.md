# Profile Screen Completion Plan

I have already implemented the Profile screen components. This plan covers the final refinements to ensure the screen is fully integrated and matches the pixel-perfect requirements.

## Proposed Changes

### [Manifest]

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/AndroidManifest.xml)
* Register `ProfileActivity`.

### [Layout]

#### [MODIFY] [activity_profile.xml](file:///C:/Users/PRABAL MISHRA/AndroidStudioProjects/lightshop/app/src/main/res/layout/activity_profile.xml)
* Apply 24dp top padding to the toolbar section as per requirements.
* Set user card elevation to 0dp and keep the stroke for a flatter, more modern look if it matches the reference better.

## Verification Plan

### Automated Tests
* Run `gradle build` to ensure the project compiles with the new Activity.

### Manual Verification
* The user can now launch `ProfileActivity` and verify it matches the reference image exactly.
