package com.example.nexus_scribes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class WelcomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Welcome");
        setContentView(R.layout.welcome_screen);

        Button btn_embark = findViewById(R.id.embarkButton);
        btn_embark.setOnClickListener(view ->
                startActivity(new Intent(WelcomeScreen.this,
                LogIn_NewProfile.class)));
    }
}