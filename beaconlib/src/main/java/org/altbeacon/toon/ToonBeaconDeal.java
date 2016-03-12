package org.altbeacon.toon;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by gfy on 2016/3/3.
 */
public class ToonBeaconDeal implements BeaconConsumer {
    private static ToonBeaconDeal instance;
    private List<Feature> features = new ArrayList<>();
    private Context context;
    private BeaconManager toonBeaconManager;
    private HashMap<Feature.Attribute, HashMap<String, BeanCallBack.OnFeatureChangedListener>> listeners = new HashMap<>();
    private static BackgroundPowerSaver powerSaver;
    private Handler handler;

    private ToonBeaconDeal(Context context) {
        this.context = context.getApplicationContext();
        listeners = new HashMap<>();
        toonBeaconManager = BeaconManager.getInstanceForApplication(context);
        handler = new Handler();

        //添加ibeacon转码器
        toonBeaconManager.getBeaconParsers().clear();
        toonBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        toonBeaconManager.setForegroundScanPeriod(500);
    }

    public static ToonBeaconDeal getInstance(Context context) {
        if (instance == null) {
            instance = new ToonBeaconDeal(context);
        }
        return instance;
    }

    public static void init(Application application) {
        BeaconManager.getInstanceForApplication(application);
        powerSaver = new BackgroundPowerSaver(application);
        application.registerActivityLifecycleCallbacks(lifecycleCallbacks);
    }

    public void start() {
        toonBeaconManager.bind(this);
    }

    public void stop() {
        toonBeaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        toonBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, final Region region) {
                Log.d("LX", "正在搜索...");
                if (beacons.size() > 0) {
                    String devices = "搜索到设备:";
                    Iterator<Beacon> iterator = beacons.iterator();
                    while (iterator.hasNext()) {
                        Beacon beacon = iterator.next();
                        devices += beacon.getBluetoothName() + "\n";
                    }
                    Log.d("LX", devices);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (Feature feature : features) {
                            feature.operate(beacons, region);
                        }
                    }
                });
            }
        });
        try {
            toonBeaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
    }

    @Override
    public Context getApplicationContext() {
        return context;
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        context.unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return context.bindService(intent, serviceConnection, i);
    }

    public void addFeature(String uuid, Feature.Attribute attribute, BeanCallBack.OnFeatureChangedListener onFeatureChangedListener) {
        Feature feature = null;
        if (attribute == Feature.Attribute.DISTANCE_NOTICE) {
            SafeDistanceFeature.getInstance().addUUID(uuid);
            feature = addFeature(SafeDistanceFeature.getInstance());
        }
        if (onFeatureChangedListener != null && feature != null)
            feature.addListener(Feature.Attribute.DISTANCE_NOTICE, uuid, onFeatureChangedListener);
    }

    public void addFeature(String uuid, Feature.Attribute attribute) {
        if (attribute == Feature.Attribute.DISTANCE_NOTICE) {
            SafeDistanceFeature.getInstance().addUUID(uuid);
            addFeature(SafeDistanceFeature.getInstance());
        }
    }

    public void removeFeature(String uuid, Feature.Attribute attribute) {
        if (!TextUtils.isEmpty(uuid)) {
            Feature temp = null;
            for (Feature feature : features) {
                if (feature.uuids.contains(uuid) && feature.attribute == attribute) {
                    temp = feature;
                    break;
                }
            }
            if (temp != null) {
                temp.removeListener(attribute, uuid);
                removeFeature(temp);
            }
        }
    }

    private Feature addFeature(Feature feature) {
        if (feature != null && !features.contains(feature)) {
            features.add(feature);
        }
        return feature;
    }

    private void removeFeature(Feature feature) {
        if (feature != null && features.contains(feature)) {
            features.remove(feature);
        }
    }

    public void destory() {
        for (Feature feature : features) {
            feature.clearUUID();
            feature.clearListener();
        }
        features.clear();
        features = null;
    }

    private static Application.ActivityLifecycleCallbacks lifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
            powerSaver.onActivityResumed(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {
            powerSaver.onActivityPaused(activity);
        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };


}
