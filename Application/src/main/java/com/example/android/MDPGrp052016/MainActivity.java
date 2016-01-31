/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


package com.example.android.MDPGrp052016;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.LayoutInflater;

import com.example.android.common.activities.SampleActivityBase;
import com.example.android.common.logger.Log;
import com.example.android.common.logger.LogFragment;
import com.example.android.common.logger.LogWrapper;
import com.example.android.common.logger.MessageOnlyLogFilter;



/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p>
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
public class MainActivity extends SampleActivityBase {

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;
    private Button config_btnsave;
    EditText et_explore;
    EditText et_run;
    EditText et_manual;
    EditText et_refresh;
    EditText et_forward;
    EditText et_reverse;
    EditText et_turn_left;
    EditText et_turn_right;
    EditText et_f1;
    EditText et_f2;
    EditText et_xcoor;
    EditText et_ycoor;
    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;

    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> mConversationArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;
    private LayoutInflater inflater;
    private View dialogView;
    private View dialogView_config;
    private AlertDialog alertDialog;
    private AlertDialog alertDialog_config;
    /**
     * Member object for the chat services
     */
    private BluetoothChatService mChatService = null;
    public static final String TAG = "MainActivity";

    // Whether the Log Fragment is currently shown
    private boolean mLogShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.android.MDPGrp052016.R.layout.activity_main);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
        }
        if (savedInstanceState == null) {
            inflater = this.getLayoutInflater();
            dialogView = inflater.inflate(com.example.android.MDPGrp052016.R.layout.fragment_bluetooth_chat, null);

            Context context = this;

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);

            // set title
            alertDialogBuilder.setTitle("Transmit/Receive Text");


            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
// ...Irrelevant code for customizing the buttons and title


            dialogBuilder.setView(dialogView);
            alertDialog = dialogBuilder.create();
            //FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //BluetoothChatFragment fragment = new BluetoothChatFragment();
            //transaction.replace(R.id.sample_content_fragment, fragment);
            //transaction.commit();



            Context context_config = this;
            AlertDialog.Builder alertDialogBuilder_config = new AlertDialog.Builder(
                    context_config);
            inflater = this.getLayoutInflater();
            dialogView_config = inflater.inflate(com.example.android.MDPGrp052016.R.layout.config, null);
            // set title
            alertDialogBuilder_config.setTitle("Config");


            AlertDialog.Builder dialogBuilder_config = new AlertDialog.Builder(this);
// ...Irrelevant code for customizing the buttons and title

            dialogBuilder_config.setView(dialogView_config);



            alertDialog_config = dialogBuilder_config.create();

        }

        mConversationView = (ListView) dialogView.findViewById(com.example.android.MDPGrp052016.R.id.in);
        mOutEditText = (EditText) dialogView.findViewById(com.example.android.MDPGrp052016.R.id.edit_text_out);
        mSendButton = (Button) dialogView.findViewById(com.example.android.MDPGrp052016.R.id.button_send);

        config_btnsave = (Button) dialogView_config.findViewById(R.id.btnSave);
        et_explore = (EditText) dialogView_config.findViewById(R.id.et_explore);
        et_run = (EditText) dialogView_config.findViewById(R.id.et_run);
        et_manual = (EditText) dialogView_config.findViewById(R.id.et_manual);
        et_refresh = (EditText) dialogView_config.findViewById(R.id.et_refresh);
        et_forward = (EditText) dialogView_config.findViewById(R.id.et_forward);
        et_reverse = (EditText) dialogView_config.findViewById(R.id.et_reverse);
        et_turn_left = (EditText) dialogView_config.findViewById(R.id.et_turnleft);
        et_turn_right= (EditText) dialogView_config.findViewById(R.id.et_turnright);
        et_f1 = (EditText) dialogView_config.findViewById(R.id.et_f1);
        et_f2 = (EditText) dialogView_config.findViewById(R.id.et_f2);
        et_xcoor = (EditText) dialogView_config.findViewById(R.id.et_xcoor);
        et_ycoor = (EditText) dialogView_config.findViewById(R.id.et_ycoor);

        ConfigRepo repo = new ConfigRepo(this);
        Config config = repo.getconfigByname("explore");
        et_explore.setText(config.binding);

        config = repo.getconfigByname("run");
        et_run.setText(config.binding);

        config = repo.getconfigByname("manual");
        et_manual.setText(config.binding);

        config = repo.getconfigByname("refresh");
        et_refresh.setText(config.binding);

        config = repo.getconfigByname("forward");
        et_forward.setText(config.binding);

        config = repo.getconfigByname("reverse");
        et_reverse.setText(config.binding);

        config = repo.getconfigByname("turnleft");
        et_turn_left.setText(config.binding);

        config = repo.getconfigByname("turnright");
        et_turn_right.setText(config.binding);

        config = repo.getconfigByname("f1");
        et_f1.setText(config.binding);

        config = repo.getconfigByname("f2");
        et_f2.setText(config.binding);

        config = repo.getconfigByname("xcoor");
        et_xcoor.setText(config.binding);

        config = repo.getconfigByname("ycoor");
        et_ycoor.setText(config.binding);

        config_btnsave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                    ConfigRepo repo = new ConfigRepo(getApplicationContext());
                    Config[] config = new Config[12];

                    config[0] = new Config();
                    config[0].name = "explore";
                    config[0].binding = et_explore.getText().toString();

                    config[1] = new Config();
                    config[1].name = "run";
                    config[1].binding = et_run.getText().toString();

                    config[2] = new Config();
                    config[2].name = "manual";
                    config[2].binding = et_manual.getText().toString();

                    config[3] = new Config();
                    config[3].name = "refresh";
                    config[3].binding = et_refresh.getText().toString();

                    config[4] = new Config();
                    config[4].name = "forward";
                    config[4].binding = et_forward.getText().toString();

                    config[5] = new Config();
                    config[5].name = "reverse";
                    config[5].binding = et_reverse.getText().toString();

                    config[6] = new Config();
                    config[6].name = "turnleft";
                    config[6].binding = et_turn_left.getText().toString();

                    config[7] = new Config();
                    config[7].name = "turnright";
                    config[7].binding = et_turn_right.getText().toString();

                    config[8] = new Config();
                    config[8].name = "f1";
                    config[8].binding = et_f1.getText().toString();

                    config[9] = new Config();
                    config[9].name = "f2";
                    config[9].binding = et_f2.getText().toString();

                    config[10] = new Config();
                    config[10].name = "xcoor";
                    config[10].binding = et_xcoor.getText().toString();

                    config[11] = new Config();
                    config[11].name = "ycoor";
                    config[11].binding = et_ycoor.getText().toString();
                for (int i = 0; i < config.length; i++) {
                    if (repo.getconfigByname(config[i].name).name == null) {
                        repo.insert(config[i]);
                        } else {
                        repo.update(config[i]);
                    }
                    }
                Toast.makeText(getApplicationContext(), "Config saved", Toast.LENGTH_SHORT).show();

            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.example.android.MDPGrp052016.R.menu.main, menu);
        getMenuInflater().inflate(com.example.android.MDPGrp052016.R.menu.bluetooth_chat, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logToggle = menu.findItem(com.example.android.MDPGrp052016.R.id.menu_toggle_log);
        logToggle.setVisible(findViewById(com.example.android.MDPGrp052016.R.id.sample_output) instanceof ViewAnimator);
        logToggle.setTitle(mLogShown ? com.example.android.MDPGrp052016.R.string.sample_hide_log : com.example.android.MDPGrp052016.R.string.sample_show_log);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case com.example.android.MDPGrp052016.R.id.menu_toggle_log:
                mLogShown = !mLogShown;
                ViewAnimator output = (ViewAnimator) findViewById(com.example.android.MDPGrp052016.R.id.sample_output);
                if (mLogShown) {
                    output.setDisplayedChild(1);
                } else {
                    output.setDisplayedChild(0);
                }
                supportInvalidateOptionsMenu();
                return true;
            case com.example.android.MDPGrp052016.R.id.view_chat:


                alertDialog.show();

                return true;
            case com.example.android.MDPGrp052016.R.id.config:
                ConfigRepo repo = new ConfigRepo(this);
                Config config = repo.getconfigByname("explore");
                et_explore.setText(config.binding);

                config = repo.getconfigByname("run");
                et_run.setText(config.binding);

                config = repo.getconfigByname("manual");
                et_manual.setText(config.binding);

                config = repo.getconfigByname("refresh");
                et_refresh.setText(config.binding);

                config = repo.getconfigByname("forward");
                et_forward.setText(config.binding);

                config = repo.getconfigByname("reverse");
                et_reverse.setText(config.binding);

                config = repo.getconfigByname("turnleft");
                et_turn_left.setText(config.binding);

                config = repo.getconfigByname("turnright");
                et_turn_right.setText(config.binding);

                config = repo.getconfigByname("f1");
                et_f1.setText(config.binding);

                config = repo.getconfigByname("f2");
                et_f2.setText(config.binding);

                config = repo.getconfigByname("xcoor");
                et_xcoor.setText(config.binding);

                config = repo.getconfigByname("ycoor");
                et_ycoor.setText(config.binding);

                alertDialog_config.show();

                return true;
            case com.example.android.MDPGrp052016.R.id.secure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
            case com.example.android.MDPGrp052016.R.id.insecure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            }
            case com.example.android.MDPGrp052016.R.id.discoverable: {
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    /** Create a chain of targets that will receive log data */
    @Override
    public void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(com.example.android.MDPGrp052016.R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());

        Log.i(TAG, "Ready");
    }
    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), com.example.android.MDPGrp052016.R.layout.message);

        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                View view = dialogView;
                if (null != view) {
                    TextView textView = (TextView) view.findViewById(com.example.android.MDPGrp052016.R.id.edit_text_out);
                    String message = textView.getText().toString();
                    sendMessage(message);
                }
            }
        });



        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(getApplicationContext(), mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    /**
     * Makes this device discoverable.
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
// Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(getApplicationContext(), com.example.android.MDPGrp052016.R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
    }

    /**
     * The action listener for the EditText widget, to listen for the return key
     */
    private TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {

        if (null == getApplicationContext()) {
            return;
        }
        final ActionBar actionBar = this.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {

        if (null == getApplicationContext()) {
            return;
        }
        final ActionBar actionBar = this.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(com.example.android.MDPGrp052016.R.string.title_connected_to, mConnectedDeviceName));
                            mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(com.example.android.MDPGrp052016.R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(com.example.android.MDPGrp052016.R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != getApplicationContext()) {
                        Toast.makeText(getApplicationContext(), "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != getApplicationContext()) {
                        Toast.makeText(getApplicationContext(), msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getApplicationContext(), com.example.android.MDPGrp052016.R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                }
        }
    }


    /**
     * Establish connection with other divice
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }
}
