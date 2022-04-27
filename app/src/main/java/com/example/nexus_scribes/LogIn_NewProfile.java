package com.example.nexus_scribes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.nexus_scribes.databinding.LoginNewprofileBinding;
import com.example.nexus_scribes.utilities.Constants;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogIn_NewProfile extends AppCompatActivity {

    private LoginNewprofileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Login or Create New Profile");
        binding = LoginNewprofileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners() {
        binding.logInButton.setOnClickListener(view -> {
            if (validLogIn()) {
                logIn();
            }
        });

        binding.newProfileButton.setOnClickListener(view ->
                startActivity(new Intent(LogIn_NewProfile.this,
                        CreateProfile.class)));
    }

    public void logIn() {
        final String myEmail = binding.etLogInEmail.getText().toString();
        final String myPassword = binding.etLogInPassword.getText().toString();

        loading(true);
        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        myAuth.signInWithEmailAndPassword(myEmail, myPassword);
        database.collection(Constants.KEY_COLLECTIONS_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, binding.etLogInEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.etLogInPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null
                            && task.getResult().getDocuments().size() > 0) {
                        showToast("Successful Log In!");
                        Intent intent = new Intent(getApplicationContext(), HomePage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        loading(false);
                        showToast("Log-In Failed: Email and/or Password is incorrect");
                        finish();
                        startActivity(getIntent());
                    }
                });
    }

    private Boolean validLogIn() {
        if (binding.etLogInEmail.getText().toString().trim().isEmpty()) {
            binding.logInEmailError.setVisibility(View.VISIBLE);
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.etLogInEmail.getText().toString()).matches()) {
            binding.logInEmailError.setVisibility(View.VISIBLE);
            return false;
        } else if (binding.etLogInPassword.getText().toString().trim().isEmpty()) {
            binding.logInPasswordError.setVisibility(View.VISIBLE);
            return false;
        } else {
            binding.etLogInEmail.setVisibility(View.GONE);
            binding.etLogInPassword.setVisibility(View.GONE);
            return true;
        }
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.toast_background);
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.parseColor("#FFFFFF"));
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.logInButton.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.logInButton.setVisibility(View.VISIBLE);
        }
    }
}
