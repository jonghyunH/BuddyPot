package com.test.bluet;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.dinuscxj.progressbar.CircleProgressBar;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class MainActivity2 extends Fragment {
    TextView mTvBluetoothStatus;
    TextView mTvReceiveData;
    TextView mTvSendData;
    TextView mTvTimer;

    Button mBtnBluetoothOn;
    Button mBtnBluetoothOff;
    Button mBtnConnect;
    Button mBtnSendData;
    Button mBtnSpeakerOn;
    Button mBtnSpeakerOff;
    Button mBtnFanOn;
    Button mBtnFanOff;
    Button mBtnPair;

    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> mPairedDevices;
    List<String> mListPairedDevices;

    Handler mBluetoothHandler;
    ConnectedBluetoothThread mThreadConnectedBluetooth;
    BluetoothDevice mBluetoothDevice;
    BluetoothSocket mBluetoothSocket;

    public static Context context_main;
    public Float[] ht_data;
    public ArrayList<Entry> entry_1 = new ArrayList<>();
    public ArrayList<Entry> entry_2 = new ArrayList<>();

    final static int BT_REQUEST_ENABLE = 1;
    final static int BT_MESSAGE_READ = 2;
    final static int BT_CONNECTING_STATUS = 3;
    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    int sec = 0;

    private static final class MyProgressFormatter implements CircleProgressBar.ProgressFormatter {
        private static final String DEFAULT_PATTERN = "%d℃";

        @Override
        public CharSequence format(int progress, int max) {
            return String.format(DEFAULT_PATTERN, (int) ((float) progress / (float) max * 50));
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View v = inflater.inflate(R.layout.activity_main2,container,false);

        mTvBluetoothStatus = v.findViewById(R.id.tvBluetoothStatus);
        mTvReceiveData = v.findViewById(R.id.tvReceiveData);
        mTvTimer = v.findViewById(R.id.tvTimer);
        mTvSendData =  v.findViewById(R.id.tvSendData);
        mBtnBluetoothOn = v.findViewById(R.id.btnBluetoothOn);
        mBtnBluetoothOff = v.findViewById(R.id.btnBluetoothOff);
        mBtnConnect = v.findViewById(R.id.btnConnect);
        mBtnSendData = v.findViewById(R.id.btnSendData);

        mBtnSpeakerOn = v.findViewById(R.id.btn_speaker_on);
        mBtnSpeakerOff = v.findViewById(R.id.btn_speaker_off);
        mBtnFanOn = v.findViewById(R.id.btn_fan_on);
        mBtnFanOff = v.findViewById(R.id.btn_fan_off);
        mBtnPair = v.findViewById(R.id.btn_pair);

        context_main = getActivity().getApplicationContext();

        CircleProgressBar mPbar_temp = v.findViewById(R.id.pbar_temp);
        CircleProgressBar mPbar_humid = v.findViewById(R.id.pbar_humid);
        mPbar_temp.setMax(50);
        mPbar_temp.setProgressFormatter(new MyProgressFormatter());

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        mBtnBluetoothOn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothOn();
            }
        });
        mBtnBluetoothOff.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothOff();
            }
        });
        mBtnConnect.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                listPairedDevices();
            }
        });
        mBtnSendData.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mThreadConnectedBluetooth != null) {
                    mThreadConnectedBluetooth.write(mTvSendData.getText().toString());
                    mTvSendData.setText("");
                }
            }
        });
        mBtnSpeakerOn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mThreadConnectedBluetooth!=null){ mThreadConnectedBluetooth.write("speaker_on"); }
            }
        });
        mBtnSpeakerOff.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mThreadConnectedBluetooth!=null){ mThreadConnectedBluetooth.write("speaker_off"); }
            }
        });
        mBtnFanOn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mThreadConnectedBluetooth!=null){ mThreadConnectedBluetooth.write("fan_on"); }
            }
        });
        mBtnFanOff.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mThreadConnectedBluetooth!=null){ mThreadConnectedBluetooth.write("fan_off"); }
            }
        });
        mBtnPair.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mThreadConnectedBluetooth!=null){ mThreadConnectedBluetooth.write("pair"); }
            }
        });


        mBluetoothHandler = new Handler(){

            @RequiresApi(api = Build.VERSION_CODES.N)
            public void handleMessage(android.os.Message msg){
                int h = 0;
                int t = 0;


                if(msg.what == BT_MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if(readMessage.charAt(4) == ',') {
                        String[] data_raw = readMessage.split(",");
                        Float[] data = Arrays.stream(data_raw).map(Float::valueOf).toArray(Float[]::new);
                        ht_data = data;

                        entry_1.add(new Entry(sec, ht_data[1]));
                        entry_2.add(new Entry(sec, ht_data[0]));
                        sec = sec + 5;

                        Fragment MainPage = new MainActivity2();
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("list1", (ArrayList<? extends Parcelable>) entry_1);
                        bundle.putParcelableArrayList("list2", (ArrayList<? extends Parcelable>) entry_2);
                        MainPage.setArguments(bundle);


                        h = Math.round(data[0]);
                        t = Math.round(data[1]);
                        mPbar_humid.setProgress(h);
                        mPbar_temp.setProgress(t);
                        mPbar_temp.invalidate();
                        mPbar_humid.invalidate();


                    }
                    else if(readMessage.trim().equals("c")){
                        Timer_thread thread_1 = new Timer_thread();
                        thread_1.start();
                    }
                    else {
                        mTvReceiveData.setText(readMessage);
                    }

                }
            }
        };

        return inflater.inflate(R.layout.activity_main2, container, false);
    }

    public class Timer_thread extends Thread {

        CountDownTimer countDownTimer = new CountDownTimer(180 * 1000, 5 * 1000) {
            public void onTick(long millisUntilFinished) {
                //반복 실행 구문
                if(mThreadConnectedBluetooth!=null){ mThreadConnectedBluetooth.write("ht"); }
            }
            public void onFinish() {
                //마지막 실행 구문
            }
        };
        public Timer_thread() {

            countDownTimer.start();
        }
        public void cancel() {
            countDownTimer.cancel();
        }

    }

    void bluetoothOn() {
        if(mBluetoothAdapter == null) {
            Toast.makeText(getActivity().getApplicationContext(), "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_LONG).show();
        }
        else {
            if (mBluetoothAdapter.isEnabled()) {
                Toast.makeText(getActivity().getApplicationContext(), "블루투스가 이미 활성화 되어 있습니다.", Toast.LENGTH_LONG).show();
                mTvBluetoothStatus.setText("활성화");
            }
            else {
                Toast.makeText(getActivity().getApplicationContext(), "블루투스가 활성화 되어 있지 않습니다.", Toast.LENGTH_LONG).show();
                Intent intentBluetoothEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intentBluetoothEnable, BT_REQUEST_ENABLE);
            }
        }
    }
    void bluetoothOff() {
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
            Toast.makeText(getActivity().getApplicationContext(), "블루투스가 비활성화 되었습니다.", Toast.LENGTH_SHORT).show();
            mTvBluetoothStatus.setText("비활성화");
        }
        else {
            Toast.makeText(getActivity().getApplicationContext(), "블루투스가 이미 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BT_REQUEST_ENABLE:
                if (resultCode == RESULT_OK) { // 블루투스 활성화를 확인을 클릭하였다면
                    Toast.makeText(getActivity().getApplicationContext(), "블루투스 활성화", Toast.LENGTH_LONG).show();
                    mTvBluetoothStatus.setText("활성화");
                } else if (resultCode == RESULT_CANCELED) { // 블루투스 활성화를 취소를 클릭하였다면
                    Toast.makeText(getActivity().getApplicationContext(), "취소", Toast.LENGTH_LONG).show();
                    mTvBluetoothStatus.setText("비활성화");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    void listPairedDevices() {
        if (mBluetoothAdapter.isEnabled()) {
            mPairedDevices = mBluetoothAdapter.getBondedDevices();

            if (mPairedDevices.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity().getApplicationContext());
                builder.setTitle("장치 선택");

                mListPairedDevices = new ArrayList<String>();
                for (BluetoothDevice device : mPairedDevices) {
                    mListPairedDevices.add(device.getName());
                    //mListPairedDevices.add(device.getName() + "\n" + device.getAddress());
                }
                final CharSequence[] items = mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]);
                mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]);

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        connectSelectedDevice(items[item].toString());
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(getActivity().getApplicationContext(), "블루투스가 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }
    void connectSelectedDevice(String selectedDeviceName) {
        for(BluetoothDevice tempDevice : mPairedDevices) {
            if (selectedDeviceName.equals(tempDevice.getName())) {
                mBluetoothDevice = tempDevice;
                break;
            }
        }
        try {
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(BT_UUID);

            try {
                mBluetoothSocket.connect();
            } catch (IOException e) {
                Toast.makeText(getActivity().getApplicationContext(), "블루투스 커넥트 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
            mThreadConnectedBluetooth = new ConnectedBluetoothThread(mBluetoothSocket);
            mThreadConnectedBluetooth.start();
            mBluetoothHandler.obtainMessage(BT_CONNECTING_STATUS, 1, -1).sendToTarget();
        } catch (IOException e) {
            Toast.makeText(getActivity().getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
        }
    }

    public class ConnectedBluetoothThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedBluetoothThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(getActivity().getApplicationContext(), "소켓 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        SystemClock.sleep(100);
                        bytes = mmInStream.available();
                        bytes = mmInStream.read(buffer, 0, bytes);
                        mBluetoothHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }
        public void write(String str) {
            byte[] bytes = str.getBytes();
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Toast.makeText(getActivity().getApplicationContext(), "데이터 전송 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Toast.makeText(getActivity().getApplicationContext(), "소켓 해제 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }


    }


}