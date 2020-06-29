package com.mad_devs.fitnesstrackerd.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mad_devs.fitnesstrackerd.R;
import com.mad_devs.fitnesstrackerd.bluetooth.BluetoothController;
import com.mad_devs.fitnesstrackerd.bluetooth.view.DeviceRecyclerViewAdapter;
import com.mad_devs.fitnesstrackerd.bluetooth.view.ListInteractionListener;
import com.mad_devs.fitnesstrackerd.bluetooth.view.RecyclerViewProgressEmptySupport;

public class MainActivity extends AppCompatActivity implements ListInteractionListener<BluetoothDevice> {

    /**
     * Строка тега, используемая для регистрации.
     */
    private static final String TAG = "MainActivity";

    /**
     * Контроллер для функциональности Bluetooth.
     */
    private BluetoothController bluetooth;

    /**
     * Кнопка обнаружения Bluetooth.
     */
    private FloatingActionButton fab;

    /**
     * диалоговое окно rogress, отображаемое в процессе сопряжения.
     */
    private ProgressDialog bondingProgressDialog;

    /**
     * Адаптер для вида переработчика.
     */
    private DeviceRecyclerViewAdapter recyclerViewAdapter;

    private RecyclerViewProgressEmptySupport recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SystemClock.sleep(getResources().getInteger(R.integer.splashscreen_duration));
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        // Sets up the RecyclerView.
        this.recyclerViewAdapter = new DeviceRecyclerViewAdapter(this);
        this.recyclerView = (RecyclerViewProgressEmptySupport) findViewById(R.id.list);
        this.recyclerView.setLayoutManager(new LinearLayoutManager (this));

        // Sets the view to show when the dataset is empty. IMPORTANT : this method must be called
        // before recyclerView.setAdapter().
        View emptyView = findViewById(R.id.empty_list);
        this.recyclerView.setEmptyView(emptyView);

        // Sets the view to show during progress.
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.recyclerView.setProgressView(progressBar);

        this.recyclerView.setAdapter(recyclerViewAdapter);

        // [#11] Гарантирует, что Bluetooth доступен на этом устройстве, прежде чем продолжить.
        boolean hasBluetooth = getPackageManager().hasSystemFeature( PackageManager.FEATURE_BLUETOOTH);
        if(!hasBluetooth) {
            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
            dialog.setTitle(getString(R.string.bluetooth_not_available_title));
            dialog.setMessage(getString(R.string.bluetooth_not_available_message));
            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Closes the dialog and terminates the activity.
                            dialog.dismiss();
                            MainActivity.this.finish();
                        }
                    });
            dialog.setCancelable(false);
            dialog.show();
        }

        // Настраивает контроллер Bluetooth.
        this.bluetooth = new BluetoothController(this, BluetoothAdapter.getDefaultAdapter(), recyclerViewAdapter);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                // Если блютус не включен, включается.
                if (!bluetooth.isBluetoothEnabled()) {
                    Snackbar.make(view, R.string.enabling_bluetooth, Snackbar.LENGTH_SHORT).show();
                    bluetooth.turnOnBluetoothAndScheduleDiscovery();
                } else {
                    // Запрещает пользователю спамить кнопку и тем самым сбивать интерфейс пользователя.
                    if (!bluetooth.isDiscovering()) {
                        // Начинает открытие.
                        Snackbar.make(view, R.string.device_discovery_started, Snackbar.LENGTH_SHORT).show();
                        bluetooth.startDiscovery();
                    } else {
                        Snackbar.make(view, R.string.device_discovery_stopped, Snackbar.LENGTH_SHORT).show();
                        bluetooth.cancelDiscovery();
                    }
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Раздувать меню; это добавляет элементы в панель действий, если она присутствует.
        getMenuInflater().inflate(R.menu.current_place_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Обрабатывать щелчки элементов панели действий здесь. Панель действий будет
        // автоматически обрабатывать нажатия на кнопку Home / Up, так долго
        // как вы указали родительскую активность в AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            showAbout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Создает о всплывающем окне.
     */
    private void showAbout() {
        // Раздувать о содержании сообщения
        View messageView = getLayoutInflater().inflate(R.layout.about, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(R.string.app_name);
        builder.setView(messageView);
        builder.create();
        builder.show();
    }

    @Override
    public void onItemClick(BluetoothDevice device) {
        Log.d(TAG, "Item clicked : " + BluetoothController.deviceToString(device));
        if (bluetooth.isAlreadyPaired(device)) {
            Log.d(TAG, "Устройство уже сопряжено!");
            Toast.makeText(this, R.string.device_already_paired, Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "Устройство не спаренный. Сопряжение..");
            boolean outcome = bluetooth.pair(device);

            // Prints a message to the user.
            String deviceName = BluetoothController.getDeviceName(device);
            if (outcome) {
                // The pairing has started, shows a progress dialog.
                Log.d(TAG, "Отображение диалога сопряжения");
                bondingProgressDialog = ProgressDialog.show(this, "", "Сопряжение с устройством " + deviceName + "...", true, false);
            } else {
                Log.d(TAG, "Ошибка при сопряжении с устройством " + deviceName + "!");
                Toast.makeText(this, "Ошибка при сопряжении с устройством " + deviceName + "!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void startLoading() {
        this.recyclerView.startLoading();

        // Изменяет значок кнопки.
        this.fab.setImageResource(R.drawable.ic_baseline_bluetooth_searching_24);
    }

    @Override
    public void endLoading(boolean partialResults) {
        this.recyclerView.endLoading();

        // Если обнаружение завершено, меняется значок кнопки.
        if (!partialResults) {
            fab.setImageResource(R.drawable.ic_bluetooth_white_24);
        }
    }

    @Override
    public void endLoadingWithDialog(boolean error, BluetoothDevice device) {
        if (this.bondingProgressDialog != null) {
            View view = findViewById(R.id.main_content);
            String message;
            String deviceName = BluetoothController.getDeviceName(device);



            // Получает сообщение для печати.
            if (error) {
                message = "Не удалось соединиться с устройством" + deviceName + "!";
            } else {
                message = "Успешно в паре с устройством " + deviceName + "!";
            }

            //Отклоняет диалог прогресса и печатает сообщение для пользователя.
            this.bondingProgressDialog.dismiss();
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();


            this.bondingProgressDialog = null;
        }

    }

    @Override
    protected void onDestroy() {
        bluetooth.close();
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Останавливает открытие.
        if (this.bluetooth != null) {
            this.bluetooth.cancelDiscovery();
        }
        // Очищает вид.
        if (this.recyclerViewAdapter != null) {
            this.recyclerViewAdapter.cleanView();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stoops the discovery.
        if (this.bluetooth != null) {
            this.bluetooth.cancelDiscovery();
        }
    }

}

