package com.ratila.universitybase1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText usernameInput = findViewById(R.id.username);
        EditText passwordInput = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.loginButton);

        // Applying fade-in animation to the views
        applyFadeInAnimation(usernameInput);
        applyFadeInAnimation(passwordInput);
        applyFadeInAnimation(loginButton);

        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (username.equals("admin") && password.equals("1234")) {
                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                finish();
            } else if (username.equals("user") && password.equals("1234")) {
                startActivity(new Intent(LoginActivity.this, UserActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Неверные логин или пароль", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to apply fade-in animation
    private void applyFadeInAnimation(View view) {
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f); // Start from fully transparent to fully opaque
        fadeIn.setDuration(1000); // Duration of the fade-in effect in milliseconds
        fadeIn.setStartOffset(500); // Optional delay before animation starts
        view.startAnimation(fadeIn);
    }
}
