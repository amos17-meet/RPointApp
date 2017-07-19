package co.rpoint.www.rpoint;


import android.*;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.w3c.dom.Text;

import BACtrackAPI.API.BACtrackAPI;
import BACtrackAPI.API.BACtrackAPICallbacks;
import BACtrackAPI.Constants.BACTrackDeviceType;
import BACtrackAPI.Exceptions.BluetoothLENotSupportedException;
import BACtrackAPI.Exceptions.BluetoothNotEnabledException;
import BACtrackAPI.Exceptions.LocationServicesNotEnabledException;
import BACtrackAPI.Mobile.Constants.Errors;

import static android.R.attr.apiKey;

public class Connect extends AppCompatActivity {

    private static BACtrackAPI mAPI;
    protected boolean Connected;
    private Context mContext;
    public TextView statusMessageTextView;
    private static String TAG = "MainActivity";
    private static final byte PERMISSIONS_FOR_SCAN = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        statusMessageTextView = (TextView) findViewById(R.id.status_message_text_view_id);
        String apiKey = "176cb3af68604c13bb02b04e376aba";
        try {
            mAPI = new BACtrackAPI(this, mCallbacks, apiKey);
            mContext = this;
        } catch (BluetoothLENotSupportedException e) {
            e.printStackTrace();
            this.setStatus("Bluetooth LE is not supported.");
        } catch (BluetoothNotEnabledException e) {
            e.printStackTrace();
            this.setStatus("Bluetooth is not enabled.");
        } catch (LocationServicesNotEnabledException e) {
            e.printStackTrace();
            this.setStatus("Location is not enabled.");
        }


    }
    public void SetConnected()
    {
        Connected=true;
//        Intent TestPage = new Intent(Connect.this, Test.class);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.test);
                statusMessageTextView=(TextView)findViewById(R.id.test_view);
//stuff that updates ui

            }
        });


    }

    public void startBlowProcessClicked(View v) {
        boolean result = false;
        Log.d(TAG, "Reached blow process.");
        if (mAPI != null) {
            result = mAPI.startCountdown();
        }
        if (!result)
            Log.e(TAG, "mAPI.startCountdown() failed");
        else
            Log.d(TAG, "Blow process start requested");
    }

    private final BACtrackAPICallbacks mCallbacks = new BACtrackAPICallbacks() {
    public boolean Connected = false;

        @Override
        public void BACtrackAPIKeyDeclined(String errorMessage) {
            APIKeyVerificationAlert verify = new APIKeyVerificationAlert();
            verify.execute(errorMessage);
        }

        @Override
        public void BACtrackAPIKeyAuthorized() {

        }

        @Override
        public void BACtrackConnected(BACTrackDeviceType bacTrackDeviceType) {
            setStatus("Connected");
            SetConnected();
        }

        @Override
        public void BACtrackDidConnect(String s) {
            setStatus("Discovering Services");
            SetConnected();
        }

        @Override
        public void BACtrackDisconnected() {
            setStatus("Disconnected");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setContentView(R.layout.activity_connect);
                    statusMessageTextView=(TextView)findViewById(R.id.status_message_text_view_id);
//stuff that updates ui

                }
            });

        }
        @Override
        public void BACtrackConnectionTimeout() {

        }

        @Override
        public void BACtrackFoundBreathalyzer(BluetoothDevice bluetoothDevice) {
            Log.d(TAG, "Found breathalyzer : " + bluetoothDevice.getName());
        }

        @Override
        public void BACtrackCountdown(int currentCountdownCount) {
            setStatus("Countdown" + " " + currentCountdownCount);
        }

        @Override
        public void BACtrackStart() {
            setStatus("Blow now");
        }

        @Override
        public void BACtrackBlow() {
            setStatus("Keep blowing...");
        }

        @Override
        public void BACtrackAnalyzing() {
            setStatus("Analyzing...");
        }

        @Override
        public void BACtrackResults(float measuredBac) {
            setStatus("Results:" + " " + measuredBac);
        }

        @Override
        public void BACtrackFirmwareVersion(String version) {
            setStatus("Firmware:" + " " + version);

        }

        @Override
        public void BACtrackSerial(String serialHex) {
            setStatus("Serial number:" + " " + serialHex);
        }

        @Override
        public void BACtrackUseCount(int useCount) {
            Log.d(TAG, "UseCount: " + useCount);
            setStatus("Use count:" + " " + useCount);
        }

        @Override
        public void BACtrackBatteryVoltage(float voltage) {

        }

        @Override
        public void BACtrackBatteryLevel(int level) {
            //setBatteryStatus(getString(R.string.TEXT_BATTERY_LEVEL) + " " + level);

        }

        @Override
        public void BACtrackError(int errorCode) {
            if (errorCode == Errors.ERROR_BLOW_ERROR)
                setStatus("Error");
                //setStatus(R.string.TEXT_ERR_BLOW_ERROR);
        }
    };
    private void setStatus(int resourceId) {
        this.setStatus(this.getResources().getString(resourceId));
    }

    private void setStatus(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, message);

                statusMessageTextView.setText(String.format("Status:\n%s", message));
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_FOR_SCAN: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /**
                     * Only start scan if permissions granted.
                     */
                    mAPI.connectToNearestBreathalyzer();
                }
            }
        }
    }

    public void connectNearestClicked(View v) {
        if (mAPI != null) {
            setStatus("Connecting");
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(Connect.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(Connect.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Connect.this,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_FOR_SCAN);
            } else {
                /**
                 * Permission already granted, start scan.
                 */

                mAPI.connectToNearestBreathalyzer();
//                if(Connected==true) {
//                    Intent TestPage = new Intent(Connect.this, Test.class);
//                    startActivity(TestPage);
//                }

            }
        }
    }

    private class APIKeyVerificationAlert extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return urls[0];
        }

        @Override
        protected void onPostExecute(String result) {
            AlertDialog.Builder apiApprovalAlert = new AlertDialog.Builder(mContext);
            apiApprovalAlert.setTitle("API Approval Failed");
            apiApprovalAlert.setMessage(result);
            apiApprovalAlert.setPositiveButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mAPI.disconnect();
                            setStatus("Disconnected");
                            dialog.cancel();
                        }
                    });

            apiApprovalAlert.create();
            apiApprovalAlert.show();
        }
    }
}
