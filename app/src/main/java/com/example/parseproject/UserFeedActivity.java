package com.example.parseproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class UserFeedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Context context;
    private ImageView emptyImageView;
    private TextView emptyTextView;
    private UserFeedAdapter adapter;
    private List<Bitmap> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);

        context = this;
        recyclerView = findViewById(R.id.user_feed_recycler_view);
        emptyImageView = findViewById(R.id.empty_image_view);
        emptyTextView = findViewById(R.id.empty_text_view);

        setupRecyclerView();
        getPhotosFromChosenUser();
    }

    private void setupRecyclerView() {

        adapter = new UserFeedAdapter(images, new UserFeedAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
//                displayPhotoInDialog();
            }
        });
        recyclerView.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void getPhotosFromChosenUser() {
        ParseQuery<ParseObject> query = new ParseQuery<>(getString(R.string.image_table_key));

        Intent intent = getIntent();
        String chosenUsername = intent.getStringExtra(getString(R.string.username_key));

        setTitle(chosenUsername + getString(R.string.photos));

        query.whereEqualTo(getString(R.string.username_key), chosenUsername);
        query.orderByDescending(getString(R.string.created_at_column_key));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects != null && objects.size() > 0) {
                    for (ParseObject object : objects) {
                        ParseFile file = (ParseFile) object.get(getString(R.string.image_column_key));
                        System.out.println("file name: " + file.getName() + " file URL: " + file.getUrl());
                        if (file != null) {
                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null && data != null) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        images.add(bitmap);
                                        adapter.notifyDataSetChanged();
                                        removeEmptyView();
                                    } else if (e != null) {
                                        ToastUtils.showToast(context, e.getMessage());
                                    }
                                }
                            });
                        }
                    }
                } else if (e != null) {
                    ToastUtils.showToast(context, e.getMessage());
                    showEmptyView();
                }
            }
        });
    }

    private void removeEmptyView() {
        emptyTextView.setVisibility(View.GONE);
        emptyImageView.setVisibility(View.GONE);
    }

    private void showEmptyView() {
        emptyTextView.setVisibility(View.VISIBLE);
        emptyImageView.setVisibility(View.VISIBLE);
    }
}