package se.su.iot.androidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView loadingText = findViewById(R.id.loadingPrompt);

        loadingText.setOnClickListener( (event) -> {
            Intent moveScreenIntent = new Intent(MainActivity.this, PlatformActivity.class);
            MainActivity.this.startActivity(moveScreenIntent);
        } );

    }
}