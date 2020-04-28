package com.example.parseproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.provider.MediaStore.Images.Media.getBitmap;

public class UserListActivity extends AppCompatActivity {

    private List<User> users;
    private RecyclerView userRecyclerView;
    private UserListAdapter userListAdapter;
    private static final int SUCCESS_REQUEST_CODE = 1;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        context = this;

        getUsers();
    }

    private void getUsers() {
        users = new ArrayList<>();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo(getString(R.string.username_key), ParseUser.getCurrentUser().getUsername());
        query.addAscendingOrder(getString(R.string.username_key));
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (e == null && parseUsers.size() > 0) {
                    for (ParseUser user : parseUsers) {
                        users.add(new User(user.getUsername()));
                    }
                    setupRecyclerView();
                }
            }
        });
    }

    private void setupRecyclerView() {
        userRecyclerView = findViewById(R.id.users_recycler_view);
        userListAdapter = new UserListAdapter(users, new UserListAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

            }
        });
        userRecyclerView.setAdapter(userListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        userRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration decoration = new DividerItemDecoration(this,
                LinearLayoutManager.VERTICAL);
        userRecyclerView.addItemDecoration(decoration);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.share) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, SUCCESS_REQUEST_CODE);
            } else {
                getPhoto();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void getPhoto() {
        Intent importImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(importImageIntent, SUCCESS_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedImageLocation = data.getData();
        if (requestCode == SUCCESS_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            importImage(selectedImageLocation);
        }
    }

    private void importImage(Uri selectedImageLocation) {
        try {
            Bitmap bitmap = getBitmap(this.getContentResolver(), selectedImageLocation);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] byteArray = outputStream.toByteArray();
            ParseFile file = new ParseFile("image.png", byteArray);
            ParseObject object = new ParseObject(getString(R.string.image_table_key));
            object.put(getString(R.string.image_column_key), file);
            object.put(getString(R.string.username_key), ParseUser.getCurrentUser().getUsername());
            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        ToastUtils.showToast(context, getString(R.string.image_shared));
                    } else {
                        ToastUtils.showToast(context, getString(R.string.image_not_shared));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showToast(context, e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SUCCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        } else {
            Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT)
                    .show();
        }
    }
}