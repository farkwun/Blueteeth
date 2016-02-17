package com.robotpajamas.blueteeth.MockBlueteeth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.database.sqlite.SQLiteFullException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.robotpajamas.blueteeth.BlueteethDevice;
import com.robotpajamas.blueteeth.Callback.OnCharacteristicReadListener;
import com.robotpajamas.blueteeth.Callback.OnCharacteristicWriteListener;
import com.robotpajamas.blueteeth.Callback.OnConnectionChangedListener;
import com.robotpajamas.blueteeth.Callback.OnServicesDiscoveredListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by Bryan Roiled on 2016-01-24.
 */
public class MockBlueteethDevice extends BlueteethDevice {

    private BluetoothDevice mMockBluetoothDevice;

    private boolean ifReadSucceed = true;
    private boolean ifWriteSucceed = true;
    private UUID mReadCharacteristic;


    @Nullable
    private BluetoothGatt mMockBluetoothGatt;
    @Nullable
    private OnConnectionChangedListener mConnectionChangedListener;
    @Nullable
    private OnServicesDiscoveredListener mServicesDiscoveredListener;
    @Nullable
    private OnCharacteristicReadListener mCharacteristicReadListener;
    @Nullable
    private OnCharacteristicWriteListener mCharacteristicWriteListener;

    private String mMockName;

    public String getName() {
        return mMockName;
    }

    private String mMockMacAddress;

    public String getMacAddress() {
        return mMockMacAddress;
    }

//    public enum BondState {
//        Unknown,
//        UnBonded,
//        Bonding,
//        Bonded;
//
//        public static BondState fromInteger(int x) {
//            switch (x) {
//                case 10:
//                    return UnBonded;
//                case 11:
//                    return Bonding;
//                case 12:
//                    return Bonded;
//            }
//            return Unknown;
//        }
//    }
//
//    private BondState mBondState;
//
//    public BondState getBondState() {
//        return mBondState;
//    }

    private boolean mIsConnected;

    private MockGattProfile mBluetoothGatt = null;

    public void enableReadSuccess(boolean enable){
        if (enable){
            ifReadSucceed = true;
        }
        else {
            ifReadSucceed = false;
        }
    }

    public void enableWriteSuccess(boolean enable){
        if (enable){
            ifWriteSucceed = true;
        }
        else {
            ifWriteSucceed = false;
        }
    }

    public void enableGatt(boolean enable){
        if (enable){
            mBluetoothGatt = new MockGattProfile();
        }
        else {
            mBluetoothGatt = null;
        }

    }

    public boolean isConnected() {
        return mIsConnected;
    }

    public BluetoothDevice getBluetoothDevice() {
        return mMockBluetoothDevice;
    }

    MockBlueteethDevice(BluetoothDevice device, String  name, String macAddress) {
        super(device);
        this.mMockName = name;
        this.mMockMacAddress = macAddress;

    }

    public void connect(OnConnectionChangedListener onConnectionChangedListener) {
        // TODO: Passing in a null context seems to work, but what are the consequences?
        // TODO: Should I grab the application context from the BlueteethManager? Seems odd...
    mIsConnected = true;

    }

    public void disconnect(OnConnectionChangedListener onConnectionChangedListener) {
        if (mBluetoothGatt == null) {
            Timber.e("GATT is null");
            return;
        }
        mIsConnected = false;

    }

    public boolean discoverServices(OnServicesDiscoveredListener onServicesDiscoveredListener) {
        if (!mIsConnected || mBluetoothGatt == null) {
            Timber.e("Device is not connected, or GATT is null");
            return false;
        }


        return true;
    }

    public boolean readCharacteristic(@NonNull UUID characteristic, @NonNull UUID service, OnCharacteristicReadListener onCharacteristicReadListener) {
        if (!mIsConnected || mBluetoothGatt == null) {
            Timber.e("Device is not connected, or GATT is null");
            return true;
        }

        MockService gattService = mBluetoothGatt.getService(service);
        if (gattService == null) {
            Timber.e("Service not available - %s", service.toString());
            return true;
        }

        MockCharacteristic gattCharacteristic = gattService.getCharacteristic(characteristic);
        if (gattCharacteristic == null) {
            Timber.e("Characteristic not available - %s", characteristic.toString());
            return true;
        }

        return mBluetoothGatt.readCharacteristic(gattCharacteristic);
    }


    public boolean writeCharacteristic(@NonNull byte[] data, @NonNull UUID characteristic, @NonNull UUID service, OnCharacteristicWriteListener onCharacteristicWriteListener) {
        if (!mIsConnected || mBluetoothGatt == null) {
            Timber.e("Device is not connected, or GATT is null");
            return false;
        }

        MockService gattService = mBluetoothGatt.getService(service);
        if (gattService == null) {
            Timber.e("Service not available - %s", service.toString());
            return false;
        }

        MockCharacteristic gattCharacteristic = gattService.getCharacteristic(characteristic);
        if (gattCharacteristic == null) {
            Timber.e("Characteristic not available - %s", characteristic.toString());
            return false;
        }

        gattCharacteristic.setValue(data);
        return mBluetoothGatt.writeCharacteristic(gattCharacteristic);
    }

    private class MockGattProfile{

        List <MockService> mockServices = new ArrayList<>();
        List <MockCharacteristic> mockCharacteristics = new ArrayList<>();
        public void loadService(MockService mockService){
            mockServices.add(mockService);
        }

        public MockService getService(UUID serviceUUID){
            for (int i = 0; i < mockServices.size(); i++){
                if (mockServices.get(i).uuid == serviceUUID){
                    return mockServices.get(i);
                }
            }
            Timber.e("Unable to find service");
            return null;
        }

        public boolean readCharacteristic(MockCharacteristic gattCharacteristic){
            if (ifReadSucceed){
                mReadCharacteristic = gattCharacteristic.uuid;
                return true;
            }

            return false;

        }

        public boolean writeCharacteristic(MockCharacteristic gattCharacteristic){
            if (ifWriteSucceed){
                mockCharacteristics.add(gattCharacteristic);
                return true;
            }

            return false;

        }
    }

    public void close() {
        if (mBluetoothGatt != null) {
            mIsConnected = false;
            mBluetoothGatt = null;
        }
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }


    private class MockCharacteristic{
        UUID uuid;
        int properties;
        int permissions;
        byte[] value;
        public MockCharacteristic(UUID uuid, int properties, int permissions){
            this.uuid = uuid;
            this.properties = properties;
            this.permissions = permissions;
        }

        public void setValue(byte[] value){
            this.value = value;

        }
    }

    private class MockService{
        UUID uuid;
        int serviceType;
        List<MockCharacteristic> mockCharacteristics = new ArrayList<>();
        public MockService(UUID uuid, int serviceType){
            this.uuid = uuid;
            this.serviceType = serviceType;
        }
        public void loadCharacteristics(MockCharacteristic characteristics){
            mockCharacteristics.add(characteristics);
        }

        public MockCharacteristic getCharacteristic(UUID characteristicUUID){
            for (int i = 0; i < mockCharacteristics.size(); i++){
                if (mockCharacteristics.get(i).uuid == characteristicUUID){
                    return mockCharacteristics.get(i);
                }
            }
            Timber.e("Unable to find characteristic");
            return null;
        }
    }



}
