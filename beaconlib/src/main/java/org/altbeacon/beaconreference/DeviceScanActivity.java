package org.altbeacon.beaconreference;

import android.app.ListActivity;
import android.os.Bundle;

import org.altbeacon.toon.BeaconObtain;
import org.altbeacon.toon.BeanCallBack;
import org.altbeacon.toon.Feature;
import org.altbeacon.toon.ToonBeacon;
import org.altbeacon.toon.ToonBeaconDeal;

/**
 * Created by gfy on 2016/2/28.
 */
public class DeviceScanActivity extends ListActivity {
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BeaconObtain beaconObtain;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle(R.string.title_devices);
        beaconObtain = new BeaconObtain(this);
        beaconObtain.registerBeaconObtainListener(beaconObtainListener);
    }

    private BeanCallBack.OnBeaconObtainListener beaconObtainListener = new BeanCallBack.OnBeaconObtainListener() {
        @Override
        public void onBeaconObtain(final ToonBeacon beacon, int scanMode) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.addDevice(beacon);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    private void service(){
        ToonBeaconDeal toonBeaconDeal = ToonBeaconDeal.getInstance(getApplicationContext());
        toonBeaconDeal.addFeature("", Feature.Attribute.DISTANCE_NOTICE, new BeanCallBack.OnFeatureChangedListener() {
            @Override
            public void onFeatureChanged(String uuid, String info, Feature.Attribute attribute) {

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter(this);
        setListAdapter(mLeDeviceListAdapter);
        beaconObtain.refresh();
//        scanLeDevice(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }
}
