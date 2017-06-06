package gdm.darkskyweatherapp.View;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import gdm.darkskyweatherapp.Model.GPSTracker;
import gdm.darkskyweatherapp.Model.WeatherAdapter;
import gdm.darkskyweatherapp.R;
import gdm.darkskyweatherapp.ViewModel.Forecast;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    public RecyclerView recyclerView;
    public WeatherAdapter mAdapter;
    private final static int ALL_PERMISSIONS_RESULT = 101;
    GPSTracker gps;

    public static final String BASE_URL = "https://api.forecast.io/forecast/";
    public static final String API_KEY = "203bf0976335ed98863b556ed9f61f79";

    // Location data
    protected double latitude;
    protected double longitude;
    public AsyncHttpClient client;
    ArrayList<Forecast> weeklyForecast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this, "Latitude: "+latitude+" Longitude:"+longitude,
                Toast.LENGTH_LONG).show();

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

        gps = new GPSTracker(MainActivity.this);

        if (gps.canGetLocation()) {


            double longitude = gps.getLongitude();
            double latitude = gps.getLatitude();

            Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        client = new AsyncHttpClient();
        weeklyForecast = new ArrayList<Forecast>();
         latitude = gps.getLatitude();
        longitude =gps.getLongitude();
        getWeeklyForecast();
    }
    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(Object permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission((String) permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }
                if (permissionsRejected.size() > 0) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale((String) permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(gdm.darkskyweatherapp.View.MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    public ArrayList<Forecast> getWeeklyForecast() {
        // Clear before inserting new data
        weeklyForecast.clear();

        client.get(BASE_URL + API_KEY + "/" + latitude + "," + longitude , null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject root = new JSONObject(response);
                    JSONObject daily = root.getJSONObject("daily");
                    JSONArray forecasts = daily.getJSONArray("data");

                    // Loop through every day
                    for (int i = 0; i < forecasts.length(); i++) {
                        JSONObject dayObject = forecasts.getJSONObject(i);

                        weeklyForecast.add(new Forecast(
                                dayObject.getInt("time"),
                                dayObject.getString("summary"),
                                dayObject.getString("icon"),
                                dayObject.getDouble("temperatureMin"),
                                dayObject.getDouble("temperatureMax")
                        ));
                    }
                    //Data Persistence
                    SharedPreferences appSharedPrefs = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                    Gson gson = new Gson();
                    String weatherlist = gson.toJson(weeklyForecast);
                    prefsEditor.putString("user", weatherlist);
                    prefsEditor.commit();

                    mAdapter =new WeatherAdapter(weeklyForecast,getApplicationContext());
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
                    recyclerView.setAdapter(mAdapter);
                } catch(Exception e) {
                    // TODO: handle
                }


                System.out.println(weeklyForecast.toString());
            }
        });

        return weeklyForecast;
    }

     }
