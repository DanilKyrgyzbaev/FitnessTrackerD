/*
 * MIT License
 * <p>
 * Copyright (c) 2017 Donato Rimenti
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.mad_devs.fitnesstrackerd.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.Closeable;

/**
 * Класс для обработки соединения Bluetooth.
 */
public class BluetoothController implements Closeable {

    /**
     * Строка тега, используемая для регистрации.
     */
    private static final String TAG = "BluetoothManager";

    /**
     * Интерфейс для служб Bluetooth OS.
     */
    private final BluetoothAdapter bluetooth;

    /**
     * Класс, используемый для обработки связи с ОС о системных событиях Bluetooth.
     */
    private final BroadcastReceiverDelegator broadcastReceiverDelegator;

    /**
     * Деятельность, которая использует этот контроллер.
     */
    private final Activity context;

    /**
     * Используется как простой способ синхронизации между включением Bluetooth и запуском
     * обнаружение устройства.
     */
    private boolean bluetoothDiscoveryScheduled;

    /**
    * Используется как временное поле для текущего ограничивающего устройства. Это поле делает это целое
     * класс не безопасен для потоков.
     */
    private BluetoothDevice boundingDevice;

    /**
     *Создает новый BluetoothController.
     *
     * @param context  деятельность, которая использует этот контроллер.
     * @param listener обратный вызов для обработки событий Bluetooth.
     */
    public BluetoothController(Activity context,BluetoothAdapter adapter, BluetoothDiscoveryDeviceListener listener) {
        this.context = context;
        this.bluetooth = adapter;
        this.broadcastReceiverDelegator = new BroadcastReceiverDelegator(context, listener, this);
    }

    /**
     * Проверяет, включен ли уже Bluetooth на этом устройстве.
     *
     * @return true, если Bluetooth включен, иначе false.
     */
    public boolean isBluetoothEnabled() {
        return bluetooth.isEnabled();
    }

    /**
     * Начинает обнаружение новых устройств Bluetooth поблизости.
     */
    public void startDiscovery() {
        broadcastReceiverDelegator.onDeviceDiscoveryStarted();

        // Эта строка кода очень важна. В Android> = 6.0 вы должны запросить время выполнения
        // разрешение, а также для обнаружения, чтобы получить идентификаторы устройств. Если вы не делаете
        // это открытие не найдет ни одного устройства.
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }

        // Если выполняется другое обнаружение, отмените его перед запуском нового.
        if (bluetooth.isDiscovering()) {
            bluetooth.cancelDiscovery();
        }

        // Пытается начать открытие. Если обнаружение возвращает ложь, это означает, что
        // Bluetooth еще не запущен.
        Log.d(TAG, "Bluetooth starting discovery.");
        if (!bluetooth.startDiscovery()) {
            Toast.makeText(context, "Error while starting device discovery!", Toast.LENGTH_SHORT)
                    .show();
            Log.d(TAG, "StartDiscovery returned false. Maybe Bluetooth isn't on?");

            // Ends the discovery.
            broadcastReceiverDelegator.onDeviceDiscoveryEnd();
        }
    }

    public void turnOnBluetooth() {
        Log.d(TAG, "Enabling Bluetooth.");
        broadcastReceiverDelegator.onBluetoothTurningOn();
        bluetooth.enable();
    }

    public boolean pair(BluetoothDevice device) {

        //Останавливает обнаружение, а затем создает соединение.
        if (bluetooth.isDiscovering()) {
            Log.d(TAG, "Bluetooth cancelling discovery.");
            bluetooth.cancelDiscovery();
        }
        Log.d(TAG, "Bluetooth bonding with device: " + deviceToString(device));
        boolean outcome = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            outcome = device.createBond();
        }
        Log.d(TAG, "Bounding outcome : " + outcome);

        // If the outcome is true, we are bounding with this device.
        if (outcome == true) {
            this.boundingDevice = device;
        }
        return outcome;
    }

    /**
     * Checks if a device is already paired.
     *
     * @param device the device to check.
     * @return true if it is already paired, false otherwise.
     */
    public boolean isAlreadyPaired(BluetoothDevice device) {
        return bluetooth.getBondedDevices().contains(device);
    }

    /**
     * Converts a BluetoothDevice to its String representation.
     *
     * @param device the device to convert to String.
     * @return a String representation of the device.
     */
    public static String deviceToString(BluetoothDevice device) {
        return "[Address: " + device.getAddress() + ", Name: " + device.getName() + "]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        this.broadcastReceiverDelegator.close();
    }

    /**
     * Checks if a deviceDiscovery is currently running.
     *
     * @return true if a deviceDiscovery is currently running, false otherwise.
     */
    public boolean isDiscovering() {
        return bluetooth.isDiscovering();
    }

    /**
     * Cancels a device discovery.
     */
    public void cancelDiscovery() {
        if(bluetooth != null) {
            bluetooth.cancelDiscovery();
            broadcastReceiverDelegator.onDeviceDiscoveryEnd();
        }
    }

    /**
     * Turns on the Bluetooth and executes a device discovery when the Bluetooth has turned on.
     */
    public void turnOnBluetoothAndScheduleDiscovery() {
        this.bluetoothDiscoveryScheduled = true;
        turnOnBluetooth();
    }

    /**
     * Called when the Bluetooth status changed.
     */
    public void onBluetoothStatusChanged() {
        //
        //Делает что-либо, только если обнаружение устройства было запланировано.
        if (bluetoothDiscoveryScheduled) {

            int bluetoothState = bluetooth.getState();
            switch (bluetoothState) {
                case BluetoothAdapter.STATE_ON:
                    // Bluetooth is ON.
                    Log.d(TAG, "Bluetooth succesfully enabled, starting discovery");
                    startDiscovery();
                    //
                    //Сбрасывает флаг, так как это обнаружение было выполнено.
                    bluetoothDiscoveryScheduled = false;
                    break;
                case BluetoothAdapter.STATE_OFF:
                    // Bluetooth is OFF.
                    Log.d(TAG, "Error while turning Bluetooth on.");
                    Toast.makeText(context, "Error while turning Bluetooth on.", Toast.LENGTH_SHORT);
                    //
                    //Сбрасывает флаг, так как это обнаружение было выполнено.
                    bluetoothDiscoveryScheduled = false;
                    break;
                default:
                    //
                    //Bluetooth включается или выключается. Отбой.
                    break;
            }
        }
    }

    /**
     * Возвращает состояние текущего соединения и очищает состояние, если соединение выполнено.
     *
     */
    public int getPairingDeviceStatus() {
        if (this.boundingDevice == null) {
            throw new IllegalStateException("No device currently bounding");
        }
        int bondState = this.boundingDevice.getBondState();
        //
        //Если новое состояние не BOND_BONDING, соединение завершено, очищает состояние.
        if (bondState != BluetoothDevice.BOND_BONDING) {
            this.boundingDevice = null;
        }
        return bondState;
    }

    /**
     *
     * Получает имя текущего устройства сопряжения.
     *
     *
     */
    public String getPairingDeviceName() {
        return getDeviceName(this.boundingDevice);
    }

    /**
     *
     Получает имя устройства. Если имя устройства недоступно, возвращает адрес устройства.
     *
     */
    public static String getDeviceName(BluetoothDevice device) {
        String deviceName = device.getName();
        if (deviceName == null) {
            deviceName = device.getAddress();
        }
        return deviceName;
    }


    /**
     * Возвращает, если в настоящее время выполняется соединение через это приложение.
     */
    public boolean isPairingInProgress() {
        return this.boundingDevice != null;
    }

    /**
     * Получает текущее ограничивающее устройство.
     */
    public BluetoothDevice getBoundingDevice() {
        return boundingDevice;
    }
}
