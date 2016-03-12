package org.altbeacon.beaconreference;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.toon.BeanCallBack;
import org.altbeacon.toon.Feature;
import org.altbeacon.toon.ToonBeaconDeal;

import java.util.Collection;
import java.util.Iterator;

public class RangingActivity extends Activity implements BeaconConsumer {
    protected static final String TAG = "RangingActivity";
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);

//        toonBeaconManager.getBeaconParsers().clear();
//        toonBeaconManager.getBeaconParsers().add(new BeaconParser().
//                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
//        toonBeaconManager.setForegroundScanPeriod(5285);
//        toonBeaconManager.bind(this);

        ToonBeaconDeal.getInstance(this).addFeature("e2c56db5-dffb-48d2-b060-d0f5a71096e0", Feature.Attribute.DISTANCE_NOTICE,onFeatureChangedListener);
        ToonBeaconDeal.getInstance(this).start();

    }

    private BeanCallBack.OnFeatureChangedListener onFeatureChangedListener = new BeanCallBack.OnFeatureChangedListener() {
        @Override
        public void onFeatureChanged(String uuid, String info, Feature.Attribute attribute) {
            Toast.makeText(RangingActivity.this, uuid + "  "+info, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                logToDisplay("");
                if (beacons.size() > 0) {
                    Iterator<Beacon> beacons_i = beacons.iterator();
                    while (beacons_i.hasNext()) {
                        Beacon beacon = beacons_i.next();
                        logToDisplay(beacon.getBluetoothName() + "\n" + beacon.getId1().toString() + "\n is about " + beacon.getDistance() + " meters away.");

                    }
                }
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
    }


    private void logToDisplay(final String line) {
        runOnUiThread(new Runnable() {
            public void run() {
                TextView editText = (TextView) RangingActivity.this.findViewById(R.id.rangingText);
                if (TextUtils.isEmpty(line)) {
                    editText.setText("");
                } else
                    editText.append(line + "\n");
            }
        });
    }
}
