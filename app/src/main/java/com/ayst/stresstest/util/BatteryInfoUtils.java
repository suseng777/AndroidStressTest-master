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

package com.ayst.stresstest.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BatteryInfoUtils {
    private static final String TAG = "BatteryInfoUtils";
    private static final String deviceName = DeviceUtils.getDeviceName();
    private static final String harewareVersion = DeviceUtils.getHardwareVersion("ro.boot.hardware.revision", "Not define");

    /* CW2015 */
    private static final String GAUGE_CW2015_BATTERY_VOLTAGE_NOW = "/sys/class/power_supply/rk-bat/voltage_now";
    private static final String GAUGE_CW2015_BATTERY_CAPACITY_NOW = "/sys/class/power_supply/rk-bat/capacity";
    private static final String GAUGE_CW2015_BATTERY_TEMP_NOW = "/sys/class/power_supply/rk-bat/temp";

    /* BQ24192 */
    private static final String CHARGER_BQ24190_STATUS = "/sys/class/power_supply/bq24190-charger/status";
    private static final String CHARGER_BQ24190_HEALTH = "/sys/class/power_supply/bq24190-charger/health";
    private static final String CHARGER_BQ24190_CHARGE_VOLTAGE = "/sys/class/power_supply/bq24190-charger/constant_charge_voltage";
    /* SGM41510 */
    private static final String CHARGER_SGM415XX_STATUS = "/sys/class/power_supply/sgm415xx-charger/status";
    private static final String CHARGER_SGM415XX_HEALTH = "/sys/class/power_supply/sgm415xx-charger/health";
    private static final String CHARGER_SGM415XX_CHARGE_VOLTAGE = "/sys/class/power_supply/sgm415xx-charger/constant_charge_voltage";
    /* ETA6963 */
    private static final String CHARGER_ETA696X_STATUS = "/sys/class/power_supply/eta696x-charger/status";
    private static final String CHARGER_ETA696X_HEALTH = "/sys/class/power_supply/eta696x-charger/health";
    private static final String CHARGER_ETA696X_CHARGE_VOLTAGE = "/sys/class/power_supply/eta696x-charger/constant_charge_voltage";
    /* SH366002 */
    private static final String GAUGE_SH366002_BATTERY_STATUS = "/sys/class/power_supply/sh366002-0/status";
    private static final String GAUGE_SH366002_BATTERY_PRESENT = "/sys/class/power_supply/sh366002-0/present";
    private static final String GAUGE_SH366002_BATTERY_VOLTAGE_NOW = "/sys/class/power_supply/sh366002-0/voltage_now";
    private static final String GAUGE_SH366002_BATTERY_CURRENT_NOW = "/sys/class/power_supply/sh366002-0/current_now";
    private static final String GAUGE_SH366002_BATTERY_CAPACITY = "/sys/class/power_supply/sh366002-0/capacity";
    private static final String GAUGE_SH366002_BATTERY_CAPACITY_LEVAL = "/sys/class/power_supply/sh366002-0/capacity_level";
    private static final String GAUGE_SH366002_BATTERY_TEMP = "/sys/class/power_supply/sh366002-0/temp";
    private static final String GAUGE_SH366002_BATTERY_CHARGE_FULL = "/sys/class/power_supply/sh366002-0/charge_full";
    private static final String GAUGE_SH366002_BATTERY_CHARGE_NOW = "/sys/class/power_supply/sh366002-0/charge_now";
    private static final String GAUGE_SH366002_BATTERY_CHARGE_FULL_DESIGN = "/sys/class/power_supply/sh366002-0/charge_full_design";
    private static final String GAUGE_SH366002_BATTERY_CYCLE_COUNT = "/sys/class/power_supply/sh366002-0/cycle_count";
    private static final String GAUGE_SH366002_BATTERY_POWER_AVG = "/sys/class/power_supply/sh366002-0/power_avg";
    private static final String GAUGE_SH366002_BATTERY_HEALTH = "/sys/class/power_supply/sh366002-0/health";

    /* BQ27542 */
    private static final String GAUGE_BQ27542_BATTERY_STATUS = "/sys/class/power_supply/bq27542-0/status";
    private static final String GAUGE_BQ27542_BATTERY_PRESENT = "/sys/class/power_supply/bq27542-0/present";
    private static final String GAUGE_BQ27542_BATTERY_VOLTAGE_NOW = "/sys/class/power_supply/bq27542-0/voltage_now";
    private static final String GAUGE_BQ27542_BATTERY_CURRENT_NOW = "/sys/class/power_supply/bq27542-0/current_now";
    private static final String GAUGE_BQ27542_BATTERY_CAPACITY = "/sys/class/power_supply/bq27542-0/capacity";
    private static final String GAUGE_BQ27542_BATTERY_CAPACITY_LEVAL = "/sys/class/power_supply/bq27542-0/capacity_level";
    private static final String GAUGE_BQ27542_BATTERY_TEMP = "/sys/class/power_supply/bq27542-0/temp";
    private static final String GAUGE_BQ27542_BATTERY_CHARGE_FULL = "/sys/class/power_supply/bq27542-0/charge_full";
    private static final String GAUGE_BQ27542_BATTERY_CHARGE_NOW = "/sys/class/power_supply/bq27542-0/charge_now";
    private static final String GAUGE_BQ27542_BATTERY_CHARGE_FULL_DESIGN = "/sys/class/power_supply/bq27542-0/charge_full_design";
    private static final String GAUGE_BQ27542_BATTERY_CYCLE_COUNT = "/sys/class/power_supply/bq27542-0/cycle_count";
    private static final String GAUGE_BQ27542_BATTERY_POWER_AVG = "/sys/class/power_supply/bq27542-0/power_avg";
    private static final String GAUGE_BQ27542_BATTERY_HEALTH = "/sys/class/power_supply/bq27542-0/health";
    private static final String CPU_TEMP = "sys/devices/virtual/thermal/thermal_zone0/temp";

    /* SC8885 */
    private static final String CHARGER_SC8885_STATUS = "/sys/class/power_supply/sc8885-charger/status";
    private static final String CHARGER_SC8885_HEALTH = "/sys/class/power_supply/sc8885-charger/health";
    private static final String CHARGER_SC8885_CHARGE_VOLTAGE = "/sys/class/power_supply/sc8885-charger/constant_charge_voltage";

    /* SH366003 */
    private static final String GAUGE_SH366003_BATTERY_STATUS = "/sys/class/power_supply/sh366003-0/status";
    private static final String GAUGE_SH366003_BATTERY_PRESENT = "/sys/class/power_supply/sh366003-0/present";
    private static final String GAUGE_SH366003_BATTERY_VOLTAGE_NOW = "/sys/class/power_supply/sh366003-0/voltage_now";
    private static final String GAUGE_SH366003_BATTERY_CURRENT_NOW = "/sys/class/power_supply/sh366003-0/current_now";
    private static final String GAUGE_SH366003_BATTERY_CAPACITY = "/sys/class/power_supply/sh366003-0/capacity";
    private static final String GAUGE_SH366003_BATTERY_CAPACITY_LEVAL = "/sys/class/power_supply/sh366003-0/capacity_level";
    private static final String GAUGE_SH366003_BATTERY_TEMP = "/sys/class/power_supply/sh366003-0/temp";
    private static final String GAUGE_SH366003_BATTERY_CHARGE_FULL = "/sys/class/power_supply/sh366003-0/charge_full";
    private static final String GAUGE_SH366003_BATTERY_CHARGE_NOW = "/sys/class/power_supply/sh366003-0/charge_now";
    private static final String GAUGE_SH366003_BATTERY_CHARGE_FULL_DESIGN = "/sys/class/power_supply/sh366003-0/charge_full_design";
    private static final String GAUGE_SH366003_BATTERY_CYCLE_COUNT = "/sys/class/power_supply/sh366003-0/cycle_count";
    private static final String GAUGE_SH366003_BATTERY_POWER_AVG = "/sys/class/power_supply/sh366003-0/power_avg";
    private static final String GAUGE_SH366003_BATTERY_HEALTH = "/sys/class/power_supply/sh366003-0/health";
    public static String getCpuTemp() {
        String temp = readFromFile(CPU_TEMP);
        if (temp.isEmpty()) {
            return temp;
        } else {
            return Float.toString((Integer.parseInt(temp) / 1000.f));
        }
    }
    public static String getChargeVoltage() {
        String mChargeVoltage = "";
        switch (deviceName) {
            case "blue_lava":
            case "blue_lava_oversea":
            case "lava_me_3":
            case "lava_me_3_factory":
            case "lava_me_3_lh":
            case "lava_me_3_lh_oversea":
            case "lava_me_3_oversea":
            case "lava_me_4_cfs":
            case "lava_me_4_cfs_factory":
            case "lava_me_4_cfs_oversea":
                mChargeVoltage = readFromFile(CHARGER_BQ24190_CHARGE_VOLTAGE);
                break;
            case "lava_me_play":
                if (harewareVersion.equals("lava_me_play_v03b_v04")) {
                    mChargeVoltage = readFromFile(CHARGER_ETA696X_CHARGE_VOLTAGE);
                } else if (harewareVersion.equals("lava_me_play_v05")){
                    mChargeVoltage = readFromFile(CHARGER_SGM415XX_CHARGE_VOLTAGE);
                }
                break;
            case "lava_me_4_sts_36":
                mChargeVoltage = readFromFile(CHARGER_SGM415XX_CHARGE_VOLTAGE);
                break;
            case "lava_one":
            case "lava_one_x":
                mChargeVoltage = readFromFile(CHARGER_SC8885_CHARGE_VOLTAGE);
                break;
            default:
                break;
        }
        if (mChargeVoltage.isEmpty()) {
            return mChargeVoltage;
        } else {
            return Integer.toString(Integer.parseInt(mChargeVoltage) / 1000);
        }
    }
    public static String getBatterryInfo() throws IOException {
        String result = "";
            switch (deviceName) {
                case "blue_lava":
                case "blue_lava_oversea":
                    String bl_charge_status = readFromFile(CHARGER_BQ24190_STATUS);
                    String bl_charge_health = readFromFile(CHARGER_BQ24190_HEALTH);
                    String bl_cw2015_voltage_now = readFromFile(GAUGE_CW2015_BATTERY_VOLTAGE_NOW);
                    String bl_cw2015_capacity_now = readFromFile(GAUGE_CW2015_BATTERY_CAPACITY_NOW);
                    String bl_cw2015_temp = readFromFile(GAUGE_CW2015_BATTERY_TEMP_NOW);
                    String bl_cpu_temp = readFromFile(CPU_TEMP);
                    result = bl_charge_status+","+bl_charge_health+","+bl_cw2015_voltage_now+","+bl_cw2015_capacity_now+","+bl_cw2015_temp+","+bl_cpu_temp;
                    break;
                case "lava_me_3":
                case "lava_me_3_factory":
                case "lava_me_3_lh":
                case "lava_me_3_lh_oversea":
                case "lava_me_3_oversea":
                case "lava_me_4_cfs":
                case "lava_me_4_cfs_factory":
                case "lava_me_4_cfs_oversea":
                    if (harewareVersion.equals("lava_me_3_v14")) {
                        String charge_status = readFromFile(CHARGER_BQ24190_STATUS);
                        String charge_health = readFromFile(CHARGER_BQ24190_HEALTH);
                        String cw2015_voltage_now = readFromFile(GAUGE_CW2015_BATTERY_VOLTAGE_NOW);
                        String cw2015_capacity_now = readFromFile(GAUGE_CW2015_BATTERY_CAPACITY_NOW);
                        String cw2015_temp = readFromFile(GAUGE_CW2015_BATTERY_TEMP_NOW);
                        String cpu_temp = readFromFile(CPU_TEMP);
                        result = charge_status+","+charge_health+","+cw2015_voltage_now+","+cw2015_capacity_now+","+cw2015_temp+","+cpu_temp;
                    } else if (harewareVersion.equals("lava_me_3_v15") || harewareVersion.equals("lava_me_3_v16") || harewareVersion.equals("lava_me_4_cfs_v16")) {
                        String charge_status = readFromFile(CHARGER_BQ24190_STATUS);
                        String charge_health = readFromFile(CHARGER_BQ24190_HEALTH);
                        String bat_status = readFromFile(GAUGE_BQ27542_BATTERY_STATUS);
                        String present = readFromFile(GAUGE_BQ27542_BATTERY_PRESENT);
                        String voltage_now = readFromFile(GAUGE_BQ27542_BATTERY_VOLTAGE_NOW);
                        String current_now = readFromFile(GAUGE_BQ27542_BATTERY_CURRENT_NOW);
                        String capacity = readFromFile(GAUGE_BQ27542_BATTERY_CAPACITY);
                        String capacity_level = readFromFile(GAUGE_BQ27542_BATTERY_CAPACITY_LEVAL);
                        String temp = readFromFile(GAUGE_BQ27542_BATTERY_TEMP);
                        String charge_full = readFromFile(GAUGE_BQ27542_BATTERY_CHARGE_FULL);
                        String charge_now = readFromFile(GAUGE_BQ27542_BATTERY_CHARGE_NOW);
                        String charge_full_design = readFromFile(GAUGE_BQ27542_BATTERY_CHARGE_FULL_DESIGN);
                        String cycle_count = readFromFile(GAUGE_BQ27542_BATTERY_CYCLE_COUNT);
                        String power_avg = readFromFile(GAUGE_BQ27542_BATTERY_POWER_AVG);
                        String bat_health = readFromFile(GAUGE_BQ27542_BATTERY_HEALTH);
                        String cw2015_voltage_now = readFromFile(GAUGE_CW2015_BATTERY_VOLTAGE_NOW);
                        String cw2015_capacity_now = readFromFile(GAUGE_CW2015_BATTERY_CAPACITY_NOW);
                        String cpu_temp = readFromFile(CPU_TEMP);
                        result = charge_status+","+charge_health+","+bat_status+","+present+","+voltage_now+","+ current_now+","+capacity+","+capacity_level+","+temp+","+charge_full+","+charge_now+","+charge_full_design+","+ cycle_count+","+power_avg+","+bat_health+","+cw2015_voltage_now+","+cw2015_capacity_now+","+cpu_temp;
                    }
                    break;
                case "lava_me_play":
                    if (harewareVersion.equals("lava_me_play_v03b_v04")) {
                        String charge_status = readFromFile(CHARGER_ETA696X_STATUS);
                        String charge_health = readFromFile(CHARGER_ETA696X_HEALTH);
                        String bat_status = readFromFile(GAUGE_SH366002_BATTERY_STATUS);
                        String present = readFromFile(GAUGE_SH366002_BATTERY_PRESENT);
                        String voltage_now = readFromFile(GAUGE_SH366002_BATTERY_VOLTAGE_NOW);
                        String current_now = readFromFile(GAUGE_SH366002_BATTERY_CURRENT_NOW);
                        String capacity = readFromFile(GAUGE_SH366002_BATTERY_CAPACITY);
                        String capacity_level = readFromFile(GAUGE_SH366002_BATTERY_CAPACITY_LEVAL);
                        String temp = readFromFile(GAUGE_SH366002_BATTERY_TEMP);
                        String charge_full = readFromFile(GAUGE_SH366002_BATTERY_CHARGE_FULL);
                        String charge_now = readFromFile(GAUGE_SH366002_BATTERY_CHARGE_NOW);
                        String charge_full_design = readFromFile(GAUGE_SH366002_BATTERY_CHARGE_FULL_DESIGN);
                        String cycle_count = readFromFile(GAUGE_SH366002_BATTERY_CYCLE_COUNT);
                        String power_avg = readFromFile(GAUGE_SH366002_BATTERY_POWER_AVG);
                        String bat_health = readFromFile(GAUGE_SH366002_BATTERY_HEALTH);
                        String cw2015_voltage_now = readFromFile(GAUGE_CW2015_BATTERY_VOLTAGE_NOW);
                        String cw2015_capacity_now = readFromFile(GAUGE_CW2015_BATTERY_CAPACITY_NOW);
                        String cpu_temp = readFromFile(CPU_TEMP);
                        result = charge_status+","+charge_health+","+bat_status+","+present+","+voltage_now+","+ current_now+","+capacity+","+capacity_level+","+temp+","+charge_full+","+charge_now+","+charge_full_design+","+ cycle_count+","+power_avg+","+bat_health+","+cw2015_voltage_now+","+cw2015_capacity_now+","+cpu_temp;
                    } else if (harewareVersion.equals("lava_me_play_v05")){
                        String charge_status = readFromFile(CHARGER_SGM415XX_STATUS);
                        String charge_health = readFromFile(CHARGER_SGM415XX_HEALTH);
                        String bat_status = readFromFile(GAUGE_BQ27542_BATTERY_STATUS);
                        String present = readFromFile(GAUGE_BQ27542_BATTERY_PRESENT);
                        String voltage_now = readFromFile(GAUGE_BQ27542_BATTERY_VOLTAGE_NOW);
                        String current_now = readFromFile(GAUGE_BQ27542_BATTERY_CURRENT_NOW);
                        String capacity = readFromFile(GAUGE_BQ27542_BATTERY_CAPACITY);
                        String capacity_level = readFromFile(GAUGE_BQ27542_BATTERY_CAPACITY_LEVAL);
                        String temp = readFromFile(GAUGE_BQ27542_BATTERY_TEMP);
                        String charge_full = readFromFile(GAUGE_BQ27542_BATTERY_CHARGE_FULL);
                        String charge_now = readFromFile(GAUGE_BQ27542_BATTERY_CHARGE_NOW);
                        String charge_full_design = readFromFile(GAUGE_BQ27542_BATTERY_CHARGE_FULL_DESIGN);
                        String cycle_count = readFromFile(GAUGE_BQ27542_BATTERY_CYCLE_COUNT);
                        String power_avg = readFromFile(GAUGE_BQ27542_BATTERY_POWER_AVG);
                        String bat_health = readFromFile(GAUGE_BQ27542_BATTERY_HEALTH);
                        String cw2015_voltage_now = readFromFile(GAUGE_CW2015_BATTERY_VOLTAGE_NOW);
                        String cw2015_capacity_now = readFromFile(GAUGE_CW2015_BATTERY_CAPACITY_NOW);
                        String cpu_temp = readFromFile(CPU_TEMP);
                        result = charge_status+","+charge_health+","+bat_status+","+present+","+voltage_now+","+ current_now+","+capacity+","+capacity_level+","+temp+","+charge_full+","+charge_now+","+charge_full_design+","+ cycle_count+","+power_avg+","+bat_health+","+cw2015_voltage_now+","+cw2015_capacity_now+","+cpu_temp;
                    }
                    break;
                case "lava_me_4_sts_36":
                    String charge_status = readFromFile(CHARGER_SGM415XX_STATUS);
                    String charge_health = readFromFile(CHARGER_SGM415XX_HEALTH);
                    String bat_status = readFromFile(GAUGE_BQ27542_BATTERY_STATUS);
                    String present = readFromFile(GAUGE_BQ27542_BATTERY_PRESENT);
                    String voltage_now = readFromFile(GAUGE_BQ27542_BATTERY_VOLTAGE_NOW);
                    String current_now = readFromFile(GAUGE_BQ27542_BATTERY_CURRENT_NOW);
                    String capacity = readFromFile(GAUGE_BQ27542_BATTERY_CAPACITY);
                    String capacity_level = readFromFile(GAUGE_BQ27542_BATTERY_CAPACITY_LEVAL);
                    String temp = readFromFile(GAUGE_BQ27542_BATTERY_TEMP);
                    String charge_full = readFromFile(GAUGE_BQ27542_BATTERY_CHARGE_FULL);
                    String charge_now = readFromFile(GAUGE_BQ27542_BATTERY_CHARGE_NOW);
                    String charge_full_design = readFromFile(GAUGE_BQ27542_BATTERY_CHARGE_FULL_DESIGN);
                    String cycle_count = readFromFile(GAUGE_BQ27542_BATTERY_CYCLE_COUNT);
                    String power_avg = readFromFile(GAUGE_BQ27542_BATTERY_POWER_AVG);
                    String bat_health = readFromFile(GAUGE_BQ27542_BATTERY_HEALTH);
                    String cw2015_voltage_now = readFromFile(GAUGE_CW2015_BATTERY_VOLTAGE_NOW);
                    String cw2015_capacity_now = readFromFile(GAUGE_CW2015_BATTERY_CAPACITY_NOW);
                    String cpu_temp = readFromFile(CPU_TEMP);
                    result = charge_status+","+charge_health+","+bat_status+","+present+","+voltage_now+","+ current_now+","+capacity+","+capacity_level+","+temp+","+charge_full+","+charge_now+","+charge_full_design+","+ cycle_count+","+power_avg+","+bat_health+","+cw2015_voltage_now+","+cw2015_capacity_now+","+cpu_temp;
                    break;
                case "lava_one":
                case "lava_one_x":
                    String one_charge_status = readFromFile(CHARGER_SC8885_STATUS);
                    String one_charge_health = readFromFile(CHARGER_SC8885_HEALTH);
                    String one_bat_status = readFromFile(GAUGE_SH366003_BATTERY_STATUS);
                    String one_present = readFromFile(GAUGE_SH366003_BATTERY_PRESENT);
                    String one_voltage_now = readFromFile(GAUGE_SH366003_BATTERY_VOLTAGE_NOW);
                    String one_current_now = readFromFile(GAUGE_SH366003_BATTERY_CURRENT_NOW);
                    String one_capacity = readFromFile(GAUGE_SH366003_BATTERY_CAPACITY);
                    String one_capacity_level = readFromFile(GAUGE_SH366003_BATTERY_CAPACITY_LEVAL);
                    String one_temp = readFromFile(GAUGE_SH366003_BATTERY_TEMP);
                    String one_charge_full = readFromFile(GAUGE_SH366003_BATTERY_CHARGE_FULL);
                    String one_charge_now = readFromFile(GAUGE_SH366003_BATTERY_CHARGE_NOW);
                    String one_charge_full_design = readFromFile(GAUGE_SH366003_BATTERY_CHARGE_FULL_DESIGN);
                    String one_cycle_count = readFromFile(GAUGE_SH366003_BATTERY_CYCLE_COUNT);
                    String one_power_avg = readFromFile(GAUGE_SH366003_BATTERY_POWER_AVG);
                    String one_bat_health = readFromFile(GAUGE_SH366003_BATTERY_HEALTH);
                    String one_cpu_temp = readFromFile(CPU_TEMP);
                    result = one_charge_status+","+one_charge_health+","+one_bat_status+","+one_present+","+one_voltage_now+","+ one_current_now+","+one_capacity+","+one_capacity_level+","+one_temp+","+one_charge_full+","+one_charge_now+","+one_charge_full_design+","+ one_cycle_count+","+one_power_avg+","+one_bat_health+","+one_cpu_temp;
                    break;
                default:
                    result = "NULL";
                    break;
            }
        return result;
    }

    private static String readFromFile(String file) {

        String ret = "";

        try {
            InputStream inputStream = new FileInputStream(file);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "Can not read file: " + e.toString());
        }

        return ret;
    }
}
