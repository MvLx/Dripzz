package com.example.dripz;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dripz.ui.home.HomeFragment;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Tampilkan HomeFragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new HomeFragment())
                    .commit();
        }
    }
}