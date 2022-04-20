package com.example.nexus_scribes;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.nexus_scribes.databinding.HomePageBinding;

public class HomePage extends AppCompatActivity {

    private HomePageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Home Page");
        binding = HomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
