package com.robotpajamas.blueteeth.MockBlueteeth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.robotpajamas.blueteeth.BlueteethDevice;
import com.robotpajamas.blueteeth.BlueteethManager;
import com.robotpajamas.blueteeth.Callback.OnScanCompletedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Bryan Roiled on 2016-01-10.
 */
public class MockBlueteethManager extends BlueteethManager {

    private boolean MOCK_BLUETOOTH_SERVICE = true;
    private boolean MOCK_BLUETOOTH_ADAPTER = true;
    private boolean MOCK_BLUETOOTH_ADAPTER_ENABLE = true;
    private Handler mHandler = new Handler();
    private HashMap<String, MockBlueteethDevice> mMockScannedPeripherals = new HashMap<>();
    //private List<MockBlueteethDevice> mMockScannedPeripherals = new ArrayList<>();
    private BluetoothDevice emptyDevice = null;


    private boolean mMockIsScanning;

    static volatile MockBlueteethManager singleton = null;

    public void loadMockScanCallback(String name, String macAddress){

        MockBlueteethDevice newDevice = new MockBlueteethDevice(emptyDevice, name, macAddress);

        mMockScannedPeripherals.put(macAddress, newDevice);


    }

    public void systemServiceEnable(boolean enable){
        if (enable){
            MOCK_BLUETOOTH_SERVICE = true;
        }
        else{
            MOCK_BLUETOOTH_SERVICE = false;
        }

    }

    public void BLEAdapterExists(boolean exists){
        if (exists){
            MOCK_BLUETOOTH_ADAPTER = true;
        }
        else{
            MOCK_BLUETOOTH_ADAPTER = false;
        }

    }

    public void BLEAdapterEnable(boolean enable){
        if (enable){
            MOCK_BLUETOOTH_ADAPTER_ENABLE = true;
        }
        else{
            MOCK_BLUETOOTH_ADAPTER_ENABLE = false;
        }

    }


    @Override
    public List<MockBlueteethDevice> getPeripherals() {
        List<MockBlueteethDevice> arrayListOfDevices =
                new ArrayList<>(mMockScannedPeripherals.values());
        return arrayListOfDevices;
    }

    public BlueteethDevice getPeripheral(@NonNull String macAddress) {
        if (!mMockScannedPeripherals.containsKey(macAddress)) {
            throw new IllegalArgumentException("MacAddress is null or ill-formed");
        }
        return mMockScannedPeripherals.get(macAddress);



    }

    @Nullable
    private OnScanCompletedListener mMockScanCompletedCallback;

    public static MockBlueteethManager with (Context context){
        if (singleton == null) {
            synchronized (MockBlueteethManager.class) {
                if (singleton == null) {
                    singleton = new MockBuilder(context).build();
                }
            }
        }
        return singleton;
    }

    public enum LogLevel {
        None,
        Debug;

        public boolean log() {
            return this != None;
        }
    }

    private LogLevel mLogLevel = LogLevel.None;

    protected MockBlueteethManager(Context applicationContext) {
        super(applicationContext);

        Context context = applicationContext.getApplicationContext();

        Timber.d("Initializing BluetoothManager");
//        BluetoothManager bleManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (!MOCK_BLUETOOTH_SERVICE) {
            Timber.e("Unable to initialize BluetoothManager.");
            throw new RuntimeException();
        }

        Timber.d("Initializing BLEAdapter");
        if (!MOCK_BLUETOOTH_ADAPTER) {
            Timber.e("Unable to obtain a BluetoothAdapter.");
            throw new RuntimeException();
        }

        if (!MOCK_BLUETOOTH_ADAPTER_ENABLE) {
            Timber.e("Bluetooth is not enabled.");
            throw new RuntimeException();
        }

    }

    public void scanForPeripherals(int scanTimeoutMillis, OnScanCompletedListener onScanCompletedCallback, boolean successful) {
        Timber.d("scanForPeripheralsWithTimeout");

        if (successful){
            scanForPeripherals();
            Timber.d("Scanned!");
            mHandler.postDelayed(this::stopScanForPeripherals, scanTimeoutMillis);

        }else{
            Timber.d("I did not scan.");
        }




    }

    public void scanForPeripherals() {
        Timber.d("scanForPeripherals");
        clearPeripherals();
        mMockIsScanning = true;
    }

    private void clearPeripherals() {
        // TODO: Need to be a bit clever about how these are handled
        // TODO: If this is the last reference, close it, otherwise don't?
//        for (BlueteethDevice blueteethDevice : mMockScannedPeripherals) {
//            blueteethDevice.close();
//        }

        mMockScannedPeripherals.clear();
    }

    public void stopScanForPeripherals() {
        Timber.d("stopScanForPeripherals");
        mMockIsScanning = false;

    }

//SEEMS UNNECESSARY?
//    private BluetoothAdapter.LeScanCallback mMockBLEScanCallback =
//            (device, rssi, scanRecord) -> mMockScannedPeripherals.add(new MockBlueteethDevice(device));


    public static MockBlueteethManager getInstance() {
        return singleton;
    }

    static class MockBuilder {
        private final Context mContext;

        /**
         * Start building a new {@link BlueteethManager} instance.
         */
        public MockBuilder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null.");
            }
            mContext = context.getApplicationContext();
        }

        /**
         * Create the {@link BlueteethManager} instance.
         */
        public MockBlueteethManager build() {
            Context context = mContext;
            return new MockBlueteethManager(context);
        }
    }
}