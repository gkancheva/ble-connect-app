package com.bluetooth.app.views;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluetooth.app.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BluetoothDeviceRVAdapter extends RecyclerView.Adapter<BluetoothDeviceRVAdapter.BleDeviceViewHolder> {
    private static final String TAG =  BluetoothDeviceRVAdapter.class.getSimpleName();

    private int selectedPosition;
    private final List<BluetoothDevice> devices;
    private final OnDeviceSelectedListener clickListener;

    public BluetoothDeviceRVAdapter(OnDeviceSelectedListener clickListener) {
        this.devices = new ArrayList<>();
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public BleDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.rv_device, parent, false);
        view.setFocusable(true);
        return new BleDeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BleDeviceViewHolder holder, int position) {
        holder.itemView.setSelected(selectedPosition == position);
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public void addDevice(BluetoothDevice device) {
        if (devices.contains(device)) {
            Log.i(getClass().getSimpleName(), "Device is already in the list, device name: " + device.getName());
            return;
        }
        Log.i(getClass().getSimpleName(), "Item inserted: " + device.getName());
        devices.add(device);
        notifyItemInserted(devices.size() - 1);
    }

    public class BleDeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView txtDeviceName;

        public BleDeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDeviceName = (TextView) itemView.findViewById(R.id.ble_device_name);
            itemView.setOnClickListener(this);
        }

        public void bind(int position) {
            BluetoothDevice device = devices.get(position);
            this.txtDeviceName.setText(device.getName());
        }

        @Override
        public void onClick(View v) {
            BluetoothDevice device = devices.get(getBindingAdapterPosition());
            Log.i(TAG,  "Device clicked: " + device.toString());
            notifyItemChanged(selectedPosition);
            selectedPosition = getBindingAdapterPosition();
            notifyItemChanged(selectedPosition);
            clickListener.onDeviceSelected(device);
        }

    }

}
