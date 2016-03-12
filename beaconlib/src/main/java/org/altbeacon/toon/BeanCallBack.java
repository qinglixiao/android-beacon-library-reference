package org.altbeacon.toon;

/**
 * Created by gfy on 2016/3/4.
 */
public class BeanCallBack {
    public interface OnBeaconObtainListener {
        void onBeaconObtain(ToonBeacon beacon, int state);
    }

    public interface OnFeatureChangedListener {
        void onFeatureChanged(String uuid, String info, Feature.Attribute attribute);
    }

}
