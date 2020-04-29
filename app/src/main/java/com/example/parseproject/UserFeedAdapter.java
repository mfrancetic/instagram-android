package com.example.parseproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserFeedAdapter extends RecyclerView.Adapter<UserFeedAdapter.ViewHolder> {

    private List<Bitmap> images;
    private UserFeedAdapter.RecyclerViewClickListener listener;

    public UserFeedAdapter(List<Bitmap> images, UserFeedAdapter.RecyclerViewClickListener listener) {
        this.images = images;
        this.listener = listener;
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

    @NonNull
    @Override
    public UserFeedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View userView = layoutInflater.inflate(R.layout.user_feed_list_item, parent, false);
        GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) userView.getLayoutParams();
        layoutParams.width = parent.getMeasuredWidth() /2;
        layoutParams.height = parent.getMeasuredHeight() / 3;
        userView.setLayoutParams(layoutParams);
        return new UserFeedAdapter.ViewHolder(userView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserFeedAdapter.ViewHolder holder, int position) {
        Bitmap image = images.get(position);
        ImageView userFeedImageView = holder.userFeedImageView;
        userFeedImageView.setImageBitmap(image);
    }

    @Override
    public int getItemCount() {
        if (images != null) {
            return images.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView userFeedImageView;

        public ViewHolder(@NonNull View itemView, UserFeedAdapter.RecyclerViewClickListener clickListener) {
            super(itemView);

            listener = clickListener;
            itemView.setOnClickListener(this);

            userFeedImageView = itemView.findViewById(R.id.user_feed_image_view);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
    }
}