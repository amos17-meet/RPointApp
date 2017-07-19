package co.rpoint.www.rpoint;


import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import BACtrackAPI.API.BACtrackAPI;
import BACtrackAPI.API.BACtrackAPICallbacks;
import BACtrackAPI.Constants.BACTrackDeviceType;
import BACtrackAPI.Exceptions.LocationServicesNotEnabledException;
import BACtrackAPI.Mobile.Constants.Errors;
import BACtrackAPI.Exceptions.BluetoothLENotSupportedException;
import BACtrackAPI.Exceptions.BluetoothNotEnabledException;

public class MainActivity extends Activity {






    private TextView batteryLevelTextView;

    private static String TAG = "MainActivity";
    private String currentFirmware;
    private Button serialNumberButton;
    private Button useCountButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser User = mAuth.getCurrentUser();
        if (User == null) {
            // User is not logged in; forward to Login page.
            Intent Login = new Intent(MainActivity.this, Login.class);
            startActivity(Login);
        }
        else {
            Intent Connections = new Intent(MainActivity.this, Connect.class);
            startActivity(Connections);
            // User is logged in and app is authenticated; continue on.
            setContentView(R.layout.activity_main);
            mAuth = FirebaseAuth.getInstance();
            // Following 2 lines are for browsing the web
            // Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
            // startActivity(browserIntent);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference CountRef = database.getReference("Tests/Count");
            CountRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Reached
                    System.out.print("It works...");
                    int Count = Integer.parseInt(dataSnapshot.getValue(String.class));
                    Log.d(TAG, "Value is: " + Count);
                    //FirebaseDatabase.getInstance().getReference("Tests/Count").setValue("15");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });




        }
    }













//    private void setBatteryStatus(final String message) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Log.d(TAG, message);
//                batteryLevelTextView.setText(String.format("\n%s", message));
//            }
//        });
//    }






    public void setCurrentFirmware(@Nullable String currentFirmware) {
        this.currentFirmware = currentFirmware;

        String[] firmwareSplit = new String[0];
        if (currentFirmware != null) {
            firmwareSplit = currentFirmware.split("\\s+");
        }
        if (firmwareSplit.length >= 1
                && Long.valueOf(firmwareSplit[0]) >= Long.valueOf("201510150003")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (serialNumberButton != null) {
                        serialNumberButton.setVisibility(View.VISIBLE);
                    }
                    if (useCountButton != null) {
                        useCountButton.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (serialNumberButton != null) {
                        serialNumberButton.setVisibility(View.GONE);
                    }
                    if (useCountButton != null) {
                        useCountButton.setVisibility(View.GONE);
                    }
                }
            });
        }
    }
}

