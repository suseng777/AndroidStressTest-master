/*
 * Copyright(c) 2018 Bob Shen <ayst.shen@foxmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ayst.stresstest.test.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.ayst.stresstest.R;
import com.ayst.stresstest.test.base.BaseTimingTestFragment;
import com.ayst.stresstest.test.base.TestType;
import com.ayst.stresstest.util.BatteryInfoUtils;
import com.ayst.stresstest.util.FileUtils;
import com.ayst.stresstest.view.DincondFontTextView;
import com.github.lzyzsd.circleprogress.ArcProgress;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BatteryTestFragment extends BaseTimingTestFragment {
    @BindView(R.id.tv_capacity)
    DincondFontTextView mCapacityTv;
    @BindView(R.id.tv_temp)
    DincondFontTextView mTempTv;
    @BindView(R.id.tv_voltage)
    DincondFontTextView mvoltageTv;
    @BindView(R.id.tv_voltage_max)
    TextView mMaxVoltageTv;
    @BindView(R.id.pgr_capacity)
    ArcProgress mCapacityPgr;
    @BindView(R.id.pgr_temp)
    ArcProgress mTempPgr;
    @BindView(R.id.pgr_voltage)
    ArcProgress mVoltagePgr;
    @BindView(R.id.container_content)
    LinearLayout mContentContainer;
    @BindView(R.id.container_running)
    LinearLayout mRunningContainer;
    Unbinder unbinder;
    IntentFilter mFilter;
    int disconectCount;
    String dateTime;
    int  temp;
    int voltage;
    int capacity;
    String dataFilePath;
    private Timer dataFileTimer;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        setTitle(R.string.battery_test);
        setType(TestType.TYPE_BATTERY_TEST);

        View contentView = inflater.inflate(R.layout.fragment_battery_test, container, false);
        setContentView(contentView);
        unbinder = ButterKnife.bind(this, contentView);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mMaxVoltageTv.setText(BatteryInfoUtils.getChargeVoltage());
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    protected void updateImpl() {
        super.updateImpl();

        if (isRunning()) {
            mContentContainer.setVisibility(View.INVISIBLE);
            mRunningContainer.setVisibility(View.VISIBLE);
        } else {
            mContentContainer.setVisibility(View.VISIBLE);
            mRunningContainer.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onStartClicked() {
        super.onStartClicked();
    }

    @Override
    public void start() {
        //获取时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        dataFilePath = simpleDateFormat.format(date);
        dateTime = simpleDateFormat.format(date);
        dataFileTimer = new Timer();
        //获取时间
        dataFileTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                try {
                    FileUtils.writeTxtToFile(dateTime+":"+BatteryInfoUtils.getBatterryInfo(),
                            "/sdcard/Battery/", dataFilePath+"_status"+ ".txt");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        },5000,30000);
        mFilter = new IntentFilter();
        mFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mFilter.addAction(Intent.ACTION_BATTERY_LOW);
        mFilter.addAction(Intent.ACTION_BATTERY_OKAY);
        mFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        mFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        getActivity().registerReceiver(mReceiver, mFilter);
        super.start();
    }

    @Override
    public void stop() {
        if (dataFileTimer != null) {
            dataFileTimer.cancel();
            dataFileTimer = null;
        }
        getActivity().unregisterReceiver(mReceiver);
        super.stop();
    }

    @Override
    public boolean isSupport() {
        return true;
    }

    @Override
    protected void handleMsg(Message msg) {
        super.handleMsg(msg);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            StringBuffer sb = new StringBuffer();
            String action = intent.getAction();
            float temperature = 0f;
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                capacity = intent.getIntExtra("level", 0);
                voltage  =   intent.getIntExtra("voltage", 0);
                temp = intent.getIntExtra("temperature", 0);
                temperature = (temp / 10.0f);
                sb.append("\n当前电量: ").append(capacity);
                sb.append("\n当前电压：").append(voltage);
                sb.append("\n当前温度：").append(temp);
                String BatteryStatus = null;
                int batStatus = intent.getIntExtra("status",
                        BatteryManager.BATTERY_STATUS_UNKNOWN);
                switch (batStatus) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        BatteryStatus = "充电状态";
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        BatteryStatus = "放电状态";
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        BatteryStatus = "未充电";
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        BatteryStatus = "充满电";
                        break;
                    case BatteryManager.BATTERY_STATUS_UNKNOWN:
                        BatteryStatus = "未知道状态";
                        break;
                }
                sb.append("\n当前状态：").append(BatteryStatus);
                String BatteryStatus2 = null;
                switch (intent.getIntExtra("plugged",
                        BatteryManager.BATTERY_PLUGGED_AC)) {
                    case BatteryManager.BATTERY_PLUGGED_AC:
                        BatteryStatus2 = "AC充电";
                        break;
                    case BatteryManager.BATTERY_PLUGGED_USB:
                        BatteryStatus2 = "USB充电";
                        break;
                }
                sb.append("\n充电方式：").append(BatteryStatus2);
                String BatteryTemp = null;
                switch (intent.getIntExtra("health",
                        BatteryManager.BATTERY_HEALTH_UNKNOWN)) {
                    case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                        BatteryTemp = "未知错误";
                        break;
                    case BatteryManager.BATTERY_HEALTH_GOOD:
                        BatteryTemp = "状态良好";
                        break;
                    case BatteryManager.BATTERY_HEALTH_DEAD:
                        BatteryTemp = "电池没有电";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                        BatteryTemp = "电池电压过高";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                        BatteryTemp = "电池过热";
                        break;
                }
                sb.append("\n电池状态：").append(BatteryTemp);
            }
            if (Intent.ACTION_POWER_DISCONNECTED.equals(action)) {
                disconectCount++;
                FileUtils.writeTxtToFile(dateTime+": Disconnected","/sdcard/Battery/", dataFilePath+"_status"+ ".txt");
            }
            if (Intent.ACTION_POWER_CONNECTED.equals(action)) {
                FileUtils.writeTxtToFile(dateTime+": Connected","/sdcard/Battery/", dataFilePath+"_status"+ ".txt");
            }

            sb.append("\n断开次数：").append(disconectCount);
            Log.d("BatteryTest",sb.toString());
            mCapacityTv.setText(capacity + "");
            mTempTv.setText(temperature + "");
            mvoltageTv.setText(voltage + "");

            mCapacityPgr.setProgress(100);
            mVoltagePgr.setProgress(100);
            if (capacity <= 10 ) {
                mCapacityPgr.setFinishedStrokeColor(getResources().getColor(R.color.red));
                mVoltagePgr.setFinishedStrokeColor(getResources().getColor(R.color.red));
            } else if (capacity <= 20) {
                mCapacityPgr.setFinishedStrokeColor(getResources().getColor(R.color.orange));
                mVoltagePgr.setFinishedStrokeColor(getResources().getColor(R.color.orange));
            } else {
                mCapacityPgr.setFinishedStrokeColor(getResources().getColor(R.color.GREEN));
                mVoltagePgr.setFinishedStrokeColor(getResources().getColor(R.color.GREEN));
            }

            mTempPgr.setProgress(100);
            if (temperature >= 50 ) {
                Log.e(TAG, ">=50");
                mTempPgr.setFinishedStrokeColor(getResources().getColor(R.color.red));
            } else if (temperature >= 45) {
                Log.e(TAG, ">=45");
                mTempPgr.setFinishedStrokeColor(getResources().getColor(R.color.orange));
            } else {
                Log.e(TAG, "<45");
                mTempPgr.setFinishedStrokeColor(getResources().getColor(R.color.GREEN));
            }
        }
    };
}
