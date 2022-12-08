package se.su.iot.androidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

public class PlatformActivity extends AppCompatActivity {

    private BeaconManager beaconManager;
    private Platform platform;
    private Location platformLocation;

    private TextView topText;

    private PlatformView platformView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform);

        platform = (Platform) getIntent().getSerializableExtra("platform");
        platformLocation = new Location(platform);

        topText = findViewById(R.id.topText);

        topText.setText(platform.getName());

        beaconManager = BeaconManager.getInstanceForApplication(this);

        platformView = findViewById(R.id.platformView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        beaconManager.addRangeNotifier(getRangeNotifier());
    }

    @Override
    protected void onPause() {
        super.onPause();
        beaconManager.removeAllRangeNotifiers();
    }


    private RangeNotifier getRangeNotifier() {
        return (Collection<Beacon> beacons, Region region) -> {

            for (Beacon beacon : beacons) {

                double distance = beacon.getDistance();
                Identifier identifier = beacon.getIdentifier(0);

                platformLocation.updateDistances(identifier.toString(), distance);

            }

            double position = platformLocation.getPositioning();
            double length = platform.getLength();

            System.out.println("Position: " + position);

            platformView.locationChanged(position/length);

        };
    }

}