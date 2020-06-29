package com.mad_devs.fitnesstrackerd.bluetooth.view;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mad_devs.fitnesstrackerd.R;
import com.mad_devs.fitnesstrackerd.bluetooth.BluetoothController;
import com.mad_devs.fitnesstrackerd.bluetooth.BluetoothDiscoveryDeviceListener;

import java.util.ArrayList;
import java.util.List;

public class DeviceRecyclerViewAdapter extends RecyclerView.Adapter<DeviceRecyclerViewAdapter.ViewHolder>
        implements BluetoothDiscoveryDeviceListener {

    /**
     * Устройства, показанные в этом {@link RecyclerView}.
     */
    private final List<BluetoothDevice> devices;

    /**
     * Обратный вызов для обработки событий взаимодействия.
     */
    private final ListInteractionListener<BluetoothDevice> listener;

    /**
     * Контроллер для функциональности Bluetooth.
     */
    private BluetoothController bluetooth;

    /**
     * Создает новый DeviceRecyclerViewAdapter.
     *
     * @param listener обработчик для событий взаимодействия.
     */
    public DeviceRecyclerViewAdapter(ListInteractionListener<BluetoothDevice> listener) {
        this.devices = new ArrayList<> ();
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate( R.layout.list_device_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = devices.get(position);
        holder.mImageView.setImageResource(getDeviceIcon(devices.get(position)));
        holder.mDeviceNameView.setText(devices.get(position).getName());
        holder.mDeviceAddressView.setText(devices.get(position).getAddress());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    listener.onItemClick(holder.mItem);
                }
            }
        });
    }

    /**
     * Возвращает значок, показанный слева от устройства внутри списка.
     */
    private int getDeviceIcon(BluetoothDevice device) {
        if (bluetooth.isAlreadyPaired(device)) {
            return R.drawable.ic_bluetooth_connected_black_24;
        } else {
            return R.drawable.ic_bluetooth_black_24;
        }
    }
    @Override
    public int getItemCount() {
        return devices.size();
    }

    @Override
    public void onDeviceDiscovered(BluetoothDevice device) {
        listener.endLoading(true);
        devices.add(device);
        notifyDataSetChanged();
    }

    @Override
    public void onDeviceDiscoveryStarted() {
        cleanView();
        listener.startLoading();
    }

    /**
     * Очищает вид.
     */
    public void cleanView() {
        devices.clear();
        notifyDataSetChanged();
    }

    @Override
    public void setBluetoothController(BluetoothController bluetooth) {
        this.bluetooth = bluetooth;
    }

    @Override
    public void onDeviceDiscoveryEnd() {
        listener.endLoading(false);
    }

    @Override
    public void onBluetoothStatusChanged() {
        // Уведомляет контроллер Bluetooth.
        bluetooth.onBluetoothStatusChanged();
    }

    @Override
    public void onBluetoothTurningOn() {
        listener.startLoading();
    }

    @Override
    public void onDevicePairingEnded() {
        if (bluetooth.isPairingInProgress()) {
            BluetoothDevice device = bluetooth.getBoundingDevice();
            switch (bluetooth.getPairingDeviceStatus()) {
                case BluetoothDevice.BOND_BONDING:
                    // Все еще сопрягаемся, ничего не делаем.
                    break;
                case BluetoothDevice.BOND_BONDED:
                    // Successfully paired.
                    listener.endLoadingWithDialog(false, device);

                    // Updates the icon for this element.
                    notifyDataSetChanged();
                    break;
                case BluetoothDevice.BOND_NONE:
                    // Failed pairing.
                    listener.endLoadingWithDialog(true, device);
                    break;
            }
        }
    }

    /**
     * ViewHolder для устройства Bluetooth.
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * Раздутый вид этого ViewHolder.
         */
        final View mView;

        /**
         * Значок устройства.
         */
        final ImageView mImageView;

        /**
         * Название устройства.
         */
        final TextView mDeviceNameView;

        /**
         * MAC-адрес устройства Bluetooth.
         */
        final TextView mDeviceAddressView;

        /**
         * Предмет этого ViewHolder.
         */
        BluetoothDevice mItem;

        /**
         * Создает новый ViewHolder.
         *
         * @param view раздутый вид этого ViewHolder
         */
        ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.device_icon);
            mDeviceNameView = (TextView) view.findViewById(R.id.device_name);
            mDeviceAddressView = (TextView) view.findViewById(R.id.device_address);
        }
        @Override
        public String toString() {
            return super.toString() + " '" + BluetoothController.deviceToString(mItem) + "'";
        }
    }
}
