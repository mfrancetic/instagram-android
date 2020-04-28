package com.example.parseproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button signUpLoginButton;
    private TextView switchToSignUpLoginTextView;
    private boolean signUpModeActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupLoginRegisterView();

        // checking how much the user has used the app
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    private void setupLoginRegisterView() {
        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        signUpLoginButton = findViewById(R.id.sign_up_login_button);
        switchToSignUpLoginTextView = findViewById(R.id.switch_to_sign_up_login_text_view);

        if (signUpModeActive) {
            setupSignUpMode();
        } else {
            setupLoginMode();
        }

        signUpLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = getUsername();
                String password = getPassword();

                if (areUsernameAndPasswordFieldsValid(username, password)) {
                    if (signUpModeActive) {
                        signUpUser(username, password);
                    } else {
                        loginUser(username, password);
                    }
                }
            }
        });

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

    private void signUpUser(String username, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    showToast(getString(R.string.sign_up_successful));
                } else {
                    showToast(e.getMessage());
                }
            }
        });
    }

    private void loginUser(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null && user != null) {
                    showToast(getString(R.string.login_successful));
                } else if (e != null) {
                    showToast(e.getMessage());
                }
            }
        });
    }

    private boolean areUsernameAndPasswordFieldsValid(String username, String password) {
        if (username.matches("") || password.matches("")) {
            showToast(getString(R.string.username_password_required));
            return false;
        } else {
            return true;
        }
    }

    private String getUsername() {
        return usernameEditText.getText().toString();
    }

    private String getPassword() {
        return passwordEditText.getText().toString();
    }

    private void showToast(String toastText) {
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
    }

    private void setupSignUpMode(){
        String switchToLogin = getString(R.string.or) + getString(R.string.log_in);
        switchToSignUpLoginTextView.setText(switchToLogin);
        signUpLoginButton.setText(getString(R.string.sign_up));
        signUpModeActive = true;
    }

    private void setupLoginMode(){
        signUpLoginButton.setText(getString(R.string.log_in));
        String switchToSignUp = getString(R.string.or) + getString(R.string.sign_up);
        switchToSignUpLoginTextView.setText(switchToSignUp);
        signUpModeActive = false;
    }
}