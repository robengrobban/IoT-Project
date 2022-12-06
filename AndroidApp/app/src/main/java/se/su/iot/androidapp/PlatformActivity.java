package se.su.iot.androidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class PlatformActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform);

        Platform platform = (Platform) getIntent().getSerializableExtra("platform");

        TextView textView = findViewById(R.id.textView);

        textView.setText(platform.getName());
        for ( PlatformSensor sensor : platform.getSensors() ) {
            System.out.println(sensor.getUuid() + " " + sensor.getPosition());
        }

    }
}