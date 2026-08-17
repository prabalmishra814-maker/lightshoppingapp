# Product Review System Implementation Plan

This plan outlines the steps to add a Ratings and Review system to the Product Detail screen, using Supabase as the backend.

## Proposed Changes

### 1. Backend (Supabase SQL)
We need a new table to store reviews. You can run this in your Supabase SQL Editor:

```sql
CREATE TABLE reviews (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    product_id TEXT NOT NULL,
    user_id UUID REFERENCES auth.users(id),
    user_name TEXT,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Enable RLS (Optional but recommended)
ALTER TABLE reviews ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Anyone can read reviews" ON reviews FOR SELECT USING (true);
CREATE POLICY "Authenticated users can insert reviews" ON reviews FOR INSERT WITH CHECK (auth.uid() = user_id);
```

### 2. Data Model
#### [NEW] [ReviewModel.java](file:///C:/Users/Harish Computer/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/amstudio/lightbasket/models/ReviewModel.java)
A simple Java class to hold review data (id, product_id, user_name, rating, comment, created_at).

### 3. API Integration
#### [MODIFY] [SupabaseApiService.java](file:///C:/Users/Harish Computer/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/amstudio/lightbasket/api/SupabaseApiService.java)
- Add `@GET("rest/v1/reviews")` to fetch reviews for a specific `product_id`.
- Add `@POST("rest/v1/reviews")` to submit a new review.

### 4. User Interface (UI)
#### [MODIFY] [activity_product_detail.xml](file:///C:/Users/Harish Computer/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/activity_product_detail.xml)
- Add a "Ratings & Reviews" header.
- Add a horizontal or vertical `RecyclerView` to show existing reviews.
- Add a "Write a Review" button.

#### [NEW] [item_review.xml](file:///C:/Users/Harish Computer/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/item_review.xml)
A layout for a single review item (User Name, Rating Stars, Date, Comment).

#### [NEW] [dialog_add_review.xml](file:///C:/Users/Harish Computer/AndroidStudioProjects/lightshoppingapp/app/src/main/res/layout/dialog_add_review.xml)
A custom dialog layout with a `RatingBar` and `EditText` for the user to input their review.

### 5. Application Logic
#### [MODIFY] [ProductDetailActivity.java](file:///C:/Users/Harish Computer/AndroidStudioProjects/lightshoppingapp/app/src/main/java/com/amstudio/lightbasket/ProductDetailActivity.java)
- Create `ReviewAdapter`.
- Call API to fetch reviews when the activity loads.
- Implement the "Write a Review" button logic:
    - Check if user is logged in.
    - Show the Add Review Dialog.
    - Send data to Supabase on "Submit".
    - Refresh the review list on success.

## Verification Plan
1. Open a product detail page.
2. Verify that existing reviews (if any) are loaded.
3. Click "Write a Review".
4. Submit a rating and comment.
5. Verify that the new review appears in the list and is saved in the Supabase table.
