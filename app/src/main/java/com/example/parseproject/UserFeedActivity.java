package com.example.parseproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class UserFeedActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);

        context = this;
        linearLayout = findViewById(R.id.user_feed_linear_layout);

        getPhotosFromChosenUser();
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
                        System.out.println(file.getName() + file.getUrl());
                        if (file != null) {
                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null && data != null) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        setImage(bitmap);
                                    }
                                }
                            });
                        }
                    }
                } else if (e != null) {
                    ToastUtils.showToast(context, e.getMessage());
                }
            }
        });
    }

    private void setImage(Bitmap bitmap) {
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        imageView.setImageBitmap(bitmap);
        linearLayout.addView(imageView);
    }
}