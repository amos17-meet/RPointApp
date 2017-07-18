package co.rpoint.www.rpoint;

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

    private static final byte PERMISSIONS_FOR_SCAN = 100;

    private static String TAG = "MainActivity";

    private TextView statusMessageTextView;
    private TextView batteryLevelTextView;

    private BACtrackAPI mAPI;
    private String currentFirmware;
    private Button serialNumberButton;
    private Button useCountButton;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.statusMessageTextView = (TextView)this.findViewById(R.id.status_message_text_view_id);
        this.batteryLevelTextView = (TextView)this.findViewById(R.id.battery_level_text_view_id);

        this.setStatus("Disconnected");

        this.serialNumberButton = (Button) findViewById(R.id.get_serial_number_button_id);
        this.useCountButton = (Button) findViewById(R.id.get_use_count_button_id);

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
            setStatus(R.string.TEXT_CONNECTING);
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_FOR_SCAN);
            } else {
                /**
                 * Permission already granted, start scan.
                 */
                mAPI.connectToNearestBreathalyzer();
            }
        }
    }

    public void disconnectClicked(View v) {
        if (mAPI != null) {
            mAPI.disconnect();
        }
    }

    public void getFirmwareVersionClicked(View v) {
        boolean result = false;
        if (mAPI != null) {
            result = mAPI.getFirmwareVersion();
        }
        if (!result)
            Log.e(TAG, "mAPI.getFirmwareVersion() failed");
        else
            Log.d(TAG, "Firmware version requested");
    }

    public void getSerialNumberClicked(View view) {
        boolean result = false;
        if (mAPI != null) {
            result = mAPI.getSerialNumber();
        }
        if (!result)
            Log.e(TAG, "mAPI.getSerialNumber() failed");
        else
            Log.d(TAG, "Serial Number requested");
    }

    public void requestUseCountClicked(View view) {
        boolean result = false;
        if (mAPI != null) {
            result = mAPI.getUseCount();
        }
        if (!result)
            Log.e(TAG, "mAPI.requestUseCount() failed");
        else
            Log.d(TAG, "Use count requested");
    }

    public void requestBatteryVoltageClicked(View view) {
        boolean result = false;
        if (mAPI != null) {
            result = mAPI.getBreathalyzerBatteryVoltage();
        }
        if (!result)
            Log.e(TAG, "mAPI.getBreathalyzerBatteryVoltage() failed");
        else
            Log.d(TAG, "Battery voltage requested");
    }


    public void startBlowProcessClicked(View v) {
        boolean result = false;
        if (mAPI != null) {
            result = mAPI.startCountdown();
        }
        if (!result)
            Log.e(TAG, "mAPI.startCountdown() failed");
        else
            Log.d(TAG, "Blow process start requested");
    }

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

    private void setBatteryStatus(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, message);
                batteryLevelTextView.setText(String.format("\n%s", message));
            }
        });
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
                            setStatus(R.string.TEXT_DISCONNECTED);
                            dialog.cancel();
                        }
                    });

            apiApprovalAlert.create();
            apiApprovalAlert.show();
        }
    }

    private final BACtrackAPICallbacks mCallbacks = new BACtrackAPICallbacks() {

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
            setStatus(R.string.TEXT_CONNECTED);
        }

        @Override
        public void BACtrackDidConnect(String s) {
            setStatus(R.string.TEXT_DISCOVERING_SERVICES);
        }

        @Override
        public void BACtrackDisconnected() {
            setStatus(R.string.TEXT_DISCONNECTED);
            setBatteryStatus("");
            setCurrentFirmware(null);
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
            setStatus(getString(R.string.TEXT_COUNTDOWN) + " " + currentCountdownCount);
        }

        @Override
        public void BACtrackStart() {
            setStatus(R.string.TEXT_BLOW_NOW);
        }

        @Override
        public void BACtrackBlow() {
            setStatus(R.string.TEXT_KEEP_BLOWING);
        }

        @Override
        public void BACtrackAnalyzing() {
            setStatus(R.string.TEXT_ANALYZING);
        }

        @Override
        public void BACtrackResults(float measuredBac) {
            setStatus(getString(R.string.TEXT_FINISHED) + " " + measuredBac);
        }

        @Override
        public void BACtrackFirmwareVersion(String version) {
            setStatus(getString(R.string.TEXT_FIRMWARE_VERSION) + " " + version);
            setCurrentFirmware(version);
        }

        @Override
        public void BACtrackSerial(String serialHex) {
            setStatus(getString(R.string.TEXT_SERIAL_NUMBER) + " " + serialHex);
        }

        @Override
        public void BACtrackUseCount(int useCount) {
            Log.d(TAG, "UseCount: " + useCount);
            setStatus(getString(R.string.TEXT_USE_COUNT) + " " + useCount);
        }

        @Override
        public void BACtrackBatteryVoltage(float voltage) {

        }

        @Override
        public void BACtrackBatteryLevel(int level) {
            setBatteryStatus(getString(R.string.TEXT_BATTERY_LEVEL) + " " + level);

        }

        @Override
        public void BACtrackError(int errorCode) {
            if (errorCode == Errors.ERROR_BLOW_ERROR)
                setStatus(R.string.TEXT_ERR_BLOW_ERROR);
        }
    };


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

