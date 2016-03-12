package org.altbeacon.beaconreference;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.altbeacon.toon.ToonBeacon;

import java.util.ArrayList;

/**
 * Created by gfy on 2016/2/28.
 */
public class LeDeviceListAdapter extends BaseAdapter {
    // Adapter for holding devices found through scanning.

    private ArrayList<ToonBeacon> mLeDevices;
    private LayoutInflater mInflator;
    private Activity mContext;

    public LeDeviceListAdapter(Activity c) {
        super();
        mContext = c;
        mLeDevices = new ArrayList<ToonBeacon>();
        mInflator = mContext.getLayoutInflater();
    }

    public void addDevice(ToonBeacon device) {
        if(device==null)
            return;

        for(int i=0;i<mLeDevices.size();i++){
            String btAddress = mLeDevices.get(i).getBluetoothAddress();
            if(btAddress.equals(device.getBluetoothAddress())){
                mLeDevices.add(i+1, device);
                mLeDevices.remove(i);
                return;
            }
        }
        mLeDevices.add(device);

    }

    public ToonBeacon getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clear() {
        mLeDevices.clear();
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = mInflator.inflate(R.layout.listitem_device, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
            viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
            viewHolder.deviceUUID= (TextView)view.findViewById(R.id.device_beacon_uuid);
            viewHolder.deviceMajor_Minor=(TextView)view.findViewById(R.id.device_major_minor);
            viewHolder.devicetxPower_RSSI=(TextView)view.findViewById(R.id.device_txPower_rssi);
            viewHolder.devicetxAll = (TextView)view.findViewById(R.id.device_all);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        ToonBeacon device = mLeDevices.get(i);
        final String deviceName = device.getBluetoothName();
        if (deviceName != null && deviceName.length() > 0)
            viewHolder.deviceName.setText(deviceName);
        else
            viewHolder.deviceName.setText(R.string.unknown_device);

        viewHolder.deviceAddress.setText(device.getBluetoothAddress());
        viewHolder.deviceUUID.setText("UUID:"+ device.getId1());
        viewHolder.deviceMajor_Minor.setText("major:"+device.getId2()+",minor:"+device.getId3());
        viewHolder.devicetxPower_RSSI.setText("txPower:"+device.getTxPower()+",rssi:"+device.getRssi());
//        viewHolder.devicetxAll.setText(device.All);
        return view;
    }

    class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceUUID;
        TextView deviceMajor_Minor;
        TextView devicetxPower_RSSI;
        TextView devicetxAll;
    }
}
