package org.altbeacon.toon;

import android.text.TextUtils;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gfy on 2016/3/3.
 */
public abstract class Feature {
    protected Attribute attribute = Attribute.NULL;
    protected boolean enable = true;
    protected String description;
    protected List<String> uuids = new ArrayList<>();
    private HashMap<Feature.Attribute,HashMap<String,BeanCallBack.OnFeatureChangedListener>> listeners = new HashMap<>();

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addListener(Attribute attribute,String uuid,BeanCallBack.OnFeatureChangedListener listener){
        if(listeners.get(attribute) == null){
            HashMap<String,BeanCallBack.OnFeatureChangedListener> attribute_listener = new HashMap<>();
            attribute_listener.put(uuid,listener);
            listeners.put(attribute,attribute_listener);
        }
        else {
            HashMap<String,BeanCallBack.OnFeatureChangedListener> attribute_listener = listeners.get(attribute);
            attribute_listener.put(uuid,listener);
        }
    }

    public void removeListener(Attribute attribute,String uuid){
        if(listeners.get(attribute) != null){
            HashMap<String,BeanCallBack.OnFeatureChangedListener> attribute_listener = listeners.get(attribute);
            if(attribute_listener.get(uuid) != null){
                attribute_listener.remove(uuid);
            }
        }
    }

    public HashMap<String,BeanCallBack.OnFeatureChangedListener> getListener(Attribute attribute){
        return listeners.get(attribute);
    }

    abstract void operate(Collection<Beacon> beacons, Region region);

    public void addUUID(String uuid){
        if(!TextUtils.isEmpty(uuid) && !uuids.contains(uuid)){
            uuids.add(uuid);
        }
    }

    public void removeUUID(String uuid){
        if(!TextUtils.isEmpty(uuid) && uuids.contains(uuid)){
            uuids.remove(uuid);
        }
    }

    public void clearUUID(){
        uuids.clear();
    }

    public void clearListener(){
        listeners.clear();
    }

    public enum Attribute {
        NULL("NULL"),//未设置
        DISTANCE_NOTICE("DISTANCE_NOTICE") ;//防丢失

        private String value;

        public String getValue() {
            return value;
        }

        Attribute(String value){
            this.value = value;
        }

    }

    public static class Params{
        public static final double DISTANCE = 5;
        public static final String DISTANCE_IN = "distance_in";
        public static final String DISTANCE_OUT = "distance_out";
        public static final int REPEAT_SCAN_COUNT = 8;
    }

}
