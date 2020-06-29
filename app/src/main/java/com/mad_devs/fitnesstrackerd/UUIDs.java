package com.mad_devs.fitnesstrackerd;

import java.util.UUID;

/**
 * Created by kshitij.saxena on 17-11-2017.
 */

class UUIDs {

    //Таможенный сервис 3 компонента
    static UUID CUSTOM_SERVICE_FEE1 = UUID.fromString("0000fee1-0000-1000-8000-00805f9b34fb");
    static UUID CUSTOM_SERVICE_AUTH_CHARACTERISTIC = UUID.fromString("00000009-0000-3512-2118-0009af100700");
    static UUID CUSTOM_SERVICE_AUTH_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    //Профиль информации об устройстве
    static UUID DEVICE_INFORMATION_SERVICE = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    static UUID SERIAL_NUMBER = UUID.fromString("00002a25-0000-1000-8000-00805f9b34fb");
    static UUID HARDWARE_REVISION_STRING = UUID.fromString("00002a27-0000-1000-8000-00805f9b34fb");
    static UUID SOFTWARE_REVISION_STRING = UUID.fromString("00002a28-0000-1000-8000-00805f9b34fb");

    //Общий профиль доступа
    static UUID GENERIC_ACCESS_SERVICE = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");
    static UUID DEVICE_NAME_CHARACTERISTIC = UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb");
    static UUID APPEARANCE_CHARACTERISTIC = UUID.fromString("00002a01-0000-1000-8000-00805f9b34fb");
    static UUID PERIPHERAL_PREFERRED_CONNECTION_CHARACTERISTIC = UUID.fromString("00002a04-0000-1000-8000-00805f9b34fb");

    //Общий профиль атрибута
    static UUID GENERIC_ATTRIBUTE_SERVICE = UUID.fromString("00001801-0000-1000-8000-00805f9b34fb");
    static UUID SERVICE_CHANGED_CHARACTERISTIC = UUID.fromString("00002a05-0000-1000-8000-00805f9b34fb");
    static UUID SERVICE_CHANGED_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    //Space left for custom service 00001530

    //Профиль оповещения
    static UUID ALERT_NOTIFICATION_SERVICE = UUID.fromString("00001811-0000-1000-8000-00805f9b34fb");
    static UUID NEW_ALERT_CHARACTERISTIC = UUID.fromString("00002a46-0000-1000-8000-00805f9b34fb");
    static UUID ALERT_NOTIFICATION_CONTROL_POINT = UUID.fromString("00002a44-0000-1000-8000-00805f9b34fb");
    static UUID ALERT_NOTIFICATION_CONTROL_POINT_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    //Профиль немедленного оповещения
    static UUID IMMEDIATE_ALERT_SERVICE = UUID.fromString("00001802-0000-1000-8000-00805f9b34fb");
    static UUID ALERT_LEVEL_CHARACTERISTIC = UUID.fromString("00002a06-0000-1000-8000-00805f9b34fb");

    //Профиль мониторинга сердечного ритма
    static UUID HEART_RATE_SERVICE = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
    static UUID HEART_RATE_MEASUREMENT_CHARACTERISTIC = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
    static UUID HEART_RATE_MEASURMENT_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    static UUID HEART_RATE_CONTROL_POINT_CHARACTERISTIC = UUID.fromString("00002a39-0000-1000-8000-00805f9b34fb");


    /*----------------Строки UUID для операторов switch----------*/
    static final String DEVICE_INFORMATION_SERVICE_STRING = "0000180a-0000-1000-8000-00805f9b34fb";
    static final String CUSTOM_SERVICE_AUTH_CHARACTERISTIC_STRING = "00000009-0000-3512-2118-0009af100700";
    static final String HEART_RATE_MEASUREMENT_CHARACTERISTIC_STRING = "00002a37-0000-1000-8000-00805f9b34fb";
    static final String GENERIC_ACCESS_SERVICE_STRING = "00001800-0000-1000-8000-00805f9b34fb";
    static final String GENERIC_ATTRIBUTE_SERVICE_STRING = "00001801-0000-1000-8000-00805f9b34fb";
    static final String ALERT_NOTIFICATION_SERVICE_STRING = "00001811-0000-1000-8000-00805f9b34fb";
    static final String IMMEDIATE_ALERT_SERVICE_STRING = "00001802-0000-1000-8000-00805f9b34fb";
}
