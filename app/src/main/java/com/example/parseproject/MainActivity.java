package com.example.parseproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener {

    private ConstraintLayout signupLoginLayout;
    private ImageView logoImageView;
    private EditText usernameEditText;
    private EditText password1EditText;
    private EditText password2EditText;
    private Button signUpLoginButton;
    private TextView switchToSignUpLoginTextView;
    private boolean signUpModeActive = true;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        setupLoginRegisterView();

        // checking how much the user has used the app
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    private void setupLoginRegisterView() {
        signupLoginLayout = findViewById(R.id.login_signup_layout);
        logoImageView = findViewById(R.id.logo_image_view);
        usernameEditText = findViewById(R.id.username_edit_text);
        password1EditText = findViewById(R.id.password1_edit_text);
        password2EditText = findViewById(R.id.password2_edit_text);
        signUpLoginButton = findViewById(R.id.sign_up_login_button);
        switchToSignUpLoginTextView = findViewById(R.id.switch_to_sign_up_login_text_view);

        if (signUpModeActive) {
            setupSignUpMode();
        } else {
            setupLoginMode();
        }

        password1EditText.setOnKeyListener(this);
        password2EditText.setOnKeyListener(this);
        logoImageView.setOnClickListener(this);
        signupLoginLayout.setOnClickListener(this);

        checkIfUserLoggedIn();

        signUpLoginButtonClick();
        signUpLoginTextViewClick();
    }

    private void checkIfUserLoggedIn() {
        if (ParseUser.getCurrentUser() != null) {
            goToUserListActivity();
        }
    }

    private void signUpLoginTextViewClick() {
        switchToSignUpLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signUpModeActive) {
                    setupLoginMode();
                } else {
                    setupSignUpMode();
                }
            }
        });
    }

    private void signUpLoginButtonClick() {
        signUpLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = getUsername();
                String password = getPassword1();

                if (signUpModeActive) {
                    if (areUsernamePassword1Password2FieldsValid(username, password, getPassword2())) {
                        signUpUser(username, password);
                    }
                } else if (areUsernameAndPasswordFieldsValid(username, password)) {
                    loginUser(username, password);
                }
            }
        });
    }

    private void signUpUser(String username, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ToastUtils.showToast(context, getString(R.string.sign_up_successful));
                    goToUserListActivity();
                } else {
                    ToastUtils.showToast(context, e.getMessage());
                }
            }
        });
    }

    private void loginUser(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null && user != null) {
                    ToastUtils.showToast(context, getString(R.string.login_successful));
                    goToUserListActivity();
                } else if (e != null) {
                    ToastUtils.showToast(context, e.getMessage());
                }
            }
        });
    }

    private boolean areUsernameAndPasswordFieldsValid(String username, String password) {
        if (username.matches("") || password.matches("")) {
            ToastUtils.showToast(context, getString(R.string.username_password_required));
            return false;
        } else {
            return true;
        }
    }

    private boolean areUsernamePassword1Password2FieldsValid(String username, String password,
                                                             String password2) {
        if (username.matches("") || password.matches("") || password2.matches("")) {
            ToastUtils.showToast(context, getString(R.string.username_password_required));
            return false;
        } else if (!password.matches(password2)) {
            ToastUtils.showToast(context, getString(R.string.passwords_do_not_match));
            return false;
        } else if(username.length() < 4 || password.length() < 4) {
            ToastUtils.showToast(context, getString(R.string.username_or_password_too_short));
            return false;
        } else if(username.contains(" ") || password.contains(" ")) {
            ToastUtils.showToast(context, getString(R.string.no_spaces_allowed));
            return false;
        } else {
            return true;
        }
    }

    private String getUsername() {
        return usernameEditText.getText().toString();
    }

    private String getPassword1() {
        return password1EditText.getText().toString();
    }

    private String getPassword2() {
        return password2EditText.getText().toString();
    }

    private void setupSignUpMode() {
        switchToSignUpLoginTextView.setText(getString(R.string.account_log_in));
        signUpLoginButton.setText(getString(R.string.sign_up));
        password2EditText.setVisibility(View.VISIBLE);
        signUpModeActive = true;
    }

    private void setupLoginMode() {
        signUpLoginButton.setText(getString(R.string.log_in));
        password2EditText.setVisibility(View.GONE);
        switchToSignUpLoginTextView.setText(getString(R.string.no_account_yet_sign_up));
        signUpModeActive = false;
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            signUpLoginButtonClick();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_signup_layout || v.getId() == R.id.logo_image_view) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager != null && getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    private void goToUserListActivity() {
        clearEditTextFields();
        Intent intent = new Intent(this, UserListActivity.class);
        startActivity(intent);
    }

    private void clearEditTextFields() {
        usernameEditText.setText("");
        password1EditText.setText("");
        password2EditText.setText("");
    }
}