package com.robotpajamas.blueteeth.MockBlueteeth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;

/**
 * Created by Bryan Roiled on 2016-01-02.
 */
public class MockBluetoothDevice {
    private String mName;
    private String mAddress;
    private BondState mBondState;
    private BluetoothGattCallback mGattCallback;
    private BluetoothGatt mBluetoothGatt;

    public enum BondState{
        BOND_NONE, BOND_BONDING, BOND_BONDED
    }

    MockBluetoothDevice (String mName, String mAddress, BondState initialBondState){
        this.mName = mName;
        this.mAddress = mAddress;
        this.mBondState = initialBondState;
    }

    public int getBondState(){
        switch(mBondState){
            case BOND_BONDING:
                return 11;
            case BOND_BONDED:
                return 12;

        }
        //Case - if BOND_NONE
        return 10;
    }

    public BluetoothGatt connectGatt(Context context, boolean autoConnect, MockBluetoothGatt mockGatt){
        return mBluetoothGatt;
    }

    public String getName(){
        return mName;
    }

    public String getAddress(){
        return mAddress;
    }
}
