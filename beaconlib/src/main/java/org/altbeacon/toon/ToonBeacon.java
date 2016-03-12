package org.altbeacon.toon;

import android.os.Parcel;

import org.altbeacon.beacon.Beacon;

/**
 * Created by gfy on 2016/3/3.
 */
public class ToonBeacon extends Beacon {
    private Feature.Attribute attribute;

    public ToonBeacon(Beacon beacon){
        this.mBluetoothName = beacon.getBluetoothName();
        this.mBluetoothAddress = beacon.getBluetoothAddress();
        this.mIdentifiers = beacon.getIdentifiers();
        this.mBeaconTypeCode = beacon.getBeaconTypeCode();
        this.mDataFields = beacon.getDataFields();
        this.mDistance = beacon.getDistance();
        this.mRssi = beacon.getRssi();
        this.mTxPower = beacon.getTxPower();
    }

    public Feature.Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Feature.Attribute attribute) {
        this.attribute = attribute;
    }

    public static final Creator<ToonBeacon> CREATOR = new Creator() {
        public ToonBeacon createFromParcel(Parcel in) {
            return new ToonBeacon(in);
        }

        public ToonBeacon[] newArray(int size) {
            return new ToonBeacon[size];
        }
    };

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
    }

    protected ToonBeacon(Parcel in) {
        super(in);
    }

    public static class Builder extends Beacon.Builder{

        public ToonBeacon build(){
            return new ToonBeacon(super.build());
        }

    }

}
