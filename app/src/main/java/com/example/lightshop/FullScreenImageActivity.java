package com.example.lightshop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.example.lightshop.utils.StatusBarUtils;
import java.util.ArrayList;

public class FullScreenImageActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private ImageButton btnClose;
    private TextView tvCounter;
    private ArrayList<String> images;
    private int startPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.applyWhiteStatusBar(this); // Optional: customize status bar for full screen
        setContentView(R.layout.activity_full_screen_image);

        viewPager = findViewById(R.id.viewPager);
        btnClose = findViewById(R.id.btnClose);
        tvCounter = findViewById(R.id.tvCounter);

        images = getIntent().getStringArrayListExtra("images");
        startPosition = getIntent().getIntExtra("position", 0);

        if (images == null || images.isEmpty()) {
            finish();
            return;
        }

        ImageAdapter adapter = new ImageAdapter(images);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(startPosition, false);

        tvCounter.setText((startPosition + 1) + " / " + images.size());

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tvCounter.setText((position + 1) + " / " + images.size());
            }
        });

        btnClose.setOnClickListener(v -> finish());
    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
        private final ArrayList<String> images;

        ImageAdapter(ArrayList<String> images) {
            this.images = images;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_full_screen_image, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Glide.with(FullScreenImageActivity.this)
                    .load(images.get(position))
                    .placeholder(R.drawable.ic_headphones)
                    .into(holder.photoView);
        }

        @Override
        public int getItemCount() {
            return images.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            PhotoView photoView;

            ViewHolder(View view) {
                super(view);
                photoView = view.findViewById(R.id.photoView);
            }
        }
    }
}
