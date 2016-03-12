package org.altbeacon.toon;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beaconreference.R;


/**
 * Created by gfy on 2016/3/3.
 */
public class BeaconObtain {
    private static final String TAG = "LX";
    private static final int SCAN_INTERVAL = 6000;
    private static final int RUNNING = 1;
    private static final int STOP = 0;
    private Context context;
    private BeanCallBack.OnBeaconObtainListener listener;
    private BeaconManager beaconManager;
    private BluetoothAdapter bluetoothAdapter;
    private Handler handler;
    private int status = STOP;

    public BeaconObtain(Context context) {
        this.context = context;
        beaconManager = BeaconManager.getInstanceForApplication(context);
        handler = new Handler();
        verifyBluetooth();
    }

    public void registerBeaconObtainListener(BeanCallBack.OnBeaconObtainListener onBeaconObtainListener) {
        listener = onBeaconObtainListener;
    }

    private void verifyBluetooth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                Toast.makeText(context, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            }

            // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
            // BluetoothAdapter through BluetoothManager.
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();

            // Checks if Bluetooth is supported on the device.
            if (bluetoothAdapter == null) {
                Toast.makeText(context, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
                return;
            }

            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
            }
        }
    }

    public void refresh() {
        if (status != RUNNING) {
            handler.postDelayed(new Runnable() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
                @Override
                public void run() {
                    bluetoothAdapter.stopLeScan(leScanCallback);
                    status = STOP;
                }
            }, SCAN_INTERVAL);
            bluetoothAdapter.startLeScan(leScanCallback);
            status = RUNNING;
        }
    }

    private BluetoothAdapter.LeScanCallback leScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    Log.d(TAG, "BluetoothDevice:" + device.getName() + "  " + bluetoothAdapter.getScanMode());
                    final IBeaconClass.iBeacon ibeacon = IBeaconClass.fromScanData(device, rssi, scanRecord);
                    if (ibeacon == null)
                        return;

                    ToonBeacon.Builder builder = new ToonBeacon.Builder();
                    builder.setId1(ibeacon.proximityUuid);
                    builder.setId2(ibeacon.major + "");
                    builder.setId3(ibeacon.minor + "");
                    builder.setBluetoothAddress(ibeacon.bluetoothAddress);
                    builder.setRssi(ibeacon.rssi);
                    builder.setBluetoothName(ibeacon.name);
                    if (listener != null) {
                        listener.onBeaconObtain(builder.build(), 0);
                    }
                }
            };

}
