package org.altbeacon.toon;

import android.text.TextUtils;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by gfy on 2016/3/4.
 */
public class SafeDistanceFeature extends Feature {
    private static final String TAG = "LX";
    private static SafeDistanceFeature instance;
    private HashMap<String, Integer> record = new HashMap<>();

    public static SafeDistanceFeature getInstance() {
        if (instance == null) {
            synchronized (SafeDistanceFeature.class) {
                if (instance == null) {
                    instance = new SafeDistanceFeature();
                    instance.attribute = Attribute.DISTANCE_NOTICE;
                }
            }
        }
        return instance;
    }

    @Override
    void operate(Collection<Beacon> beacons, Region region) {
        if (enable) {
            HashMap<String, BeanCallBack.OnFeatureChangedListener> listeners = getListener(attribute);
            if (listeners == null)
                return;

            if (beacons.size() == 0) {
                for (int i = 0; i < uuids.size(); i++) {
                    String uuid = uuids.get(i);
                    if (getDeviceRepeat(uuid) > Params.REPEAT_SCAN_COUNT) {
                        if (listeners.containsKey(uuid)) {
                            BeanCallBack.OnFeatureChangedListener listener = listeners.get(uuid);
                            listener.onFeatureChanged(uuid, Params.DISTANCE_OUT, attribute);
                        }
                    }
                }
                return;
            }

            Iterator<Beacon> iterator = beacons.iterator();
            while (iterator.hasNext()) {
                Beacon beacon = iterator.next();
                String uuid = beacon.getId1().toString();
                if (SafeDistanceFeature.this.uuids.contains(uuid)) {
                    if (listeners.containsKey(uuid)) {
                        BeanCallBack.OnFeatureChangedListener listener = listeners.get(uuid);
                        if (beacon.getDistance() > Params.DISTANCE) {
                            listener.onFeatureChanged(uuid, Params.DISTANCE_OUT, attribute);
                        } else {
                            resetCount(uuid);
                            listener.onFeatureChanged(uuid, Params.DISTANCE_IN, attribute);
                        }
                    }
                }
            }
        }
    }

    private int getDeviceRepeat(String uuid) {
        if (!TextUtils.isEmpty(uuid)) {
            if (record.containsKey(uuid)) {
                if (record.get(uuid) < Params.REPEAT_SCAN_COUNT) {
                    record.put(uuid, record.get(uuid) + 1);
                }
            } else
                record.put(uuid, 1);
        }
        Log.d(TAG,"设备:"+uuid+"  repeat:"+record.get(uuid));
        return record.get(uuid);
    }

    private void resetCount(String uuid) {
        if (!TextUtils.isEmpty(uuid) && record.containsKey(uuid)) {
            Log.d(TAG,"设备:"+uuid+"  重置计数：0");
            record.put(uuid, 0);
        }
    }

}
