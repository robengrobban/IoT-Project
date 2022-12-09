package se.su.iot.androidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
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

public class PlatformActivity extends AppCompatActivity {

    private BeaconManager beaconManager;
    private Platform platform;
    private Location platformLocation;

    private TextView topText;

    private PlatformView platformView;
    private TrainView trainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform);

        platform = (Platform) getIntent().getSerializableExtra("platform");
        platformLocation = new Location(platform);

        topText = findViewById(R.id.topText);
        topText.setText(platform.getName());

        platformView = findViewById(R.id.platformView);
        trainView = findViewById(R.id.trainView);

        loadNextTrain();

        beaconManager = BeaconManager.getInstanceForApplication(this);

    }

    private JSONObject getNextTrain() {

        JSONObject json = null;

        try {
            json = new GetNextTrainTask().execute(platform.getName()).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return json;

    }

    private void loadNextTrain() {
        JSONObject nextTrain = getNextTrain();

        try {

            System.out.println(nextTrain);

            JSONObject trainJson = nextTrain.getJSONObject("train");
            JSONArray carriagesJson = nextTrain.getJSONArray("carriages");

            Train train = new Train(trainJson.getInt("id"));

            for ( int i = 0; i < carriagesJson.length(); i++ ) {
                JSONObject instance = carriagesJson.getJSONObject(i);

                Carriage carriage = new Carriage(instance.getInt("id"), instance.getInt("position"));
                train.addCarriage(carriage);

            }

            trainView.setTrain(train);

        } catch (JSONException e) {
            e.printStackTrace();
        }
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


    private static class GetNextTrainTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder().url("http://iot.studentenfix.se/nextTrain/" + params[0] + "/").build();

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

}