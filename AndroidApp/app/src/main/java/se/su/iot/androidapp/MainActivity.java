package se.su.iot.androidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private BeaconManager beaconManager;
    private Region region;
    private boolean switchingMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView loadingText = findViewById(R.id.loadingPrompt);

        getBluetoothPermissions();

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.addRangeNotifier(getRangeNotifier());
        region = new Region("UserRegion", null, null, null);

    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Resuming...");
        beaconManager.startRangingBeacons(region);
        switchingMode = false;
    }

    private void switchPlatformActivity(Platform platform) {
        if (switchingMode) {
            return;
        }
        switchingMode = true;
        beaconManager.stopRangingBeacons(region);
        Intent moveScreenIntent = new Intent(MainActivity.this, PlatformActivity.class);
        moveScreenIntent.putExtra("platform", platform);
        MainActivity.this.startActivity(moveScreenIntent);
    }


    private RangeNotifier getRangeNotifier() {
        return (Collection<Beacon> beacons, Region region) -> {

            for (Beacon beacon : beacons) {

                double distance = beacon.getDistance();
                Identifier identifier = beacon.getIdentifier(0);

                System.out.println("Found identifier: " + identifier.toString());
                System.out.println("At a distance of: " + distance + "m");

                JSONObject json = sendPlatformRequest("http://iot.studentenfix.se/sensor/platform/" + identifier.toString() + "/");
                System.out.println(json);
                try {

                    String location = json.getJSONObject("platform").getString("name");
                    JSONArray sensors = json.getJSONArray("sensors");
                    System.out.println(sensors);

                    Platform platform = new Platform(location);
                    for (int i = 0; i < sensors.length(); i++) {
                        JSONObject instance = sensors.getJSONObject(i);
                        PlatformSensor sensor = new PlatformSensor(instance.getString("uuid"), instance.getDouble("relative_position"));
                        platform.addSensor(sensor);
                    }

                    switchPlatformActivity(platform);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            System.out.println("\n---------\n");

        };
    }

    private void getBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1);
            requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 2);
        }
        else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    private static class GetPlatformTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder().url(params[0]).build();

            JSONObject json = null;

            try {
                Response response = client.newCall(request).execute();
                String result = response.body().string();

                json = new JSONObject(result);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return json;
        }
    }

    private JSONObject sendPlatformRequest(String uri) {

        JSONObject json = null;

        try {
            json = new GetPlatformTask().execute(uri).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return json;

    }

}