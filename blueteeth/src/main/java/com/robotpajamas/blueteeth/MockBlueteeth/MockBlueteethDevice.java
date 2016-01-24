package com.robotpajamas.blueteeth.MockBlueteeth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.support.annotation.Nullable;

import com.robotpajamas.blueteeth.BlueteethDevice;
import com.robotpajamas.blueteeth.Callback.OnCharacteristicReadListener;
import com.robotpajamas.blueteeth.Callback.OnCharacteristicWriteListener;
import com.robotpajamas.blueteeth.Callback.OnConnectionChangedListener;
import com.robotpajamas.blueteeth.Callback.OnServicesDiscoveredListener;

/**
 * Created by Bryan Roiled on 2016-01-24.
 */
public class MockBlueteethDevice extends BlueteethDevice {

    private final BluetoothDevice mBluetoothDevice;

    @Nullable
    private BluetoothGatt mBluetoothGatt;
    @Nullable
    private OnConnectionChangedListener mConnectionChangedListener;
    @Nullable
    private OnServicesDiscoveredListener mServicesDiscoveredListener;
    @Nullable
    private OnCharacteristicReadListener mCharacteristicReadListener;
    @Nullable
    private OnCharacteristicWriteListener mCharacteristicWriteListener;

    private final String mName;

    public String getName() {
        return mName;
    }

    private final String mMacAddress;

    public String getMacAddress() {
        return mMacAddress;
    }

    public enum BondState {
        Unknown,
        UnBonded,
        Bonding,
        Bonded;

        public static BondState fromInteger(int x) {
            switch (x) {
                case 10:
                    return UnBonded;
                case 11:
                    return Bonding;
                case 12:
                    return Bonded;
            }
            return Unknown;
        }
    }

    private BondState mBondState;

    public BondState getBondState() {
        return mBondState;
    }

    private boolean mIsConnected;

    public boolean isConnected() {
        return mIsConnected;
    }

    public BluetoothDevice getBluetoothDevice() {
        return mBluetoothDevice;
    }

    MockBlueteethDevice(BluetoothDevice device) {
        super(device);
        mBluetoothDevice = device;
        mName = device.getName();
        mMacAddress = device.getAddress();
    }

}
