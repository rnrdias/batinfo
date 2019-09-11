package com.app.rnr.batinfo.sensor;

/*
    final String fileSenBatVol = "/sys/class/power_supply/battery/batt_vol";//("/sys/class/power_supply/battery/InstatVolt");//("/sys/devices/platform/battery_meter/FG_g_fg_dbg_bat_volt");
    final String fileSenBatAmp = "/sys/devices/platform/battery_meter/FG_Current";
    //final String senBatAmpMed = "/sys/devices/platform/battery_meter/FG_g_fg_dbg_bat_current";
    final String fileSenBatPorcent = "/sys/class/power_supply/battery/capacity";//"/sys/devices/platform/battery_meter/FG_g_fg_dbg_percentage";
    final String fileSenBatTemp = "/sys/class/power_supply/battery/batt_temp";//("/sys/devices/platform/battery_meter/FG_g_fg_dbg_bat_temp");
    final String fileSenBatSaud = "/sys/class/power_supply/battery/health";

    final String fileSenCarrVolt = "/sys/class/power_supply/battery/ChargerVoltage";
    final String fileSenCarrUsb = "/sys/class/power_supply/usb/online";
    final String fileSenCarrFont = "/sys/class/power_supply/ac/online";
    final String fileSenCarrWire = "/sys/class/power_supply/wireless/online";
*/

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;

/**
 * Created by RNR on 06/01/2018.
 */

public class SensorManager {
    final static SensorManager instance = new SensorManager();
    private Context context;

    private Intent batteryStatus;
    private IntentFilter ifilterBattChanged;
    private BatteryManager mBatteryManager;

    public final int MODE_DISABLE = 0;
    public final int MODE_API = 1;
    public final int MODE_FILE = 2;
    public final int MODE_CONST = 3;

    private int batVoltMode = MODE_API;
    private int batAmpMode = MODE_FILE;
    private int batPorcentMode = MODE_API;
    private int batTempMode = MODE_API;
    private int batSaudMode = MODE_API;
    private int batCapMode = MODE_CONST;

    private int carrVoltMode = MODE_FILE;
    private int carrAmpMode = MODE_DISABLE;
    private int carrUsbMode = MODE_API;
    private int carrFontMode = MODE_API;
    private int carrWirelessMode = MODE_API;

    private float batAmpMed = 400f;
    private float batAmpAlt = 600f;

    public final int BAT_AMP_MED_ERRO = 0;
    public final int BAT_AMP_MED_BAIX = 1;
    public final int BAT_AMP_MED_MED = 2;
    public final int BAT_AMP_MED_ALT = 3;

    private String batCapConst = "2300mA";


    private Sensor batVoltFile = new Sensor("/sys/class/power_supply/battery/batt_vol", "V", 3);
    private SensorRunnable batVoltSr = new SensorRunnable() {
        @Override
        public String getValue() {
            return "" + batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        }
    };
    private Sensor batVoltApi = new Sensor(batVoltSr, "V", 3);


    private Sensor batAmpFile = new Sensor("/sys/devices/platform/battery_meter/FG_Current", "mA", 1);
    private SensorRunnable batAmpSr = new SensorRunnable() {
        @Override
        public String getValue() {
            int amp = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                amp = mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
            } else {
                return "";
            }
            return "" + amp;
        }
    };
    private Sensor batAmpApi = new Sensor(batAmpSr, "mA", 3);


    private Sensor batPorcentFile = new Sensor("/sys/class/power_supply/battery/capacity", "%", -1);
    private SensorRunnable batPorcentSr = new SensorRunnable() {
        @Override
        public String getValue() {
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            return ((int) (level / (float) scale * 100)) + "";
        }
    };
    private Sensor batPorcentApi = new Sensor(batPorcentSr, "%", -1);

    private Sensor batTempFile = new Sensor("/sys/class/power_supply/battery/batt_temp", "C", 1);
    private SensorRunnable batTempSr = new SensorRunnable() {
        @Override
        public String getValue() {
            return batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) + "";
        }
    };
    private Sensor batTempApi = new Sensor(batTempSr, "C", 1);


    private Sensor batSaudFile = new Sensor("/sys/class/power_supply/battery/health", "", -1);
    private SensorRunnable batSaudSr = new SensorRunnable() {
        @Override
        public String getValue() {
            int health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
            // FIXED: Not used yet, Sample needs more fields
            String batteryHealth = "Unknown";
            switch (health) {
                case BatteryManager.BATTERY_HEALTH_DEAD:
                    batteryHealth = "Dead";
                    break;
                case BatteryManager.BATTERY_HEALTH_GOOD:
                    batteryHealth = "Good";
                    break;
                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                    batteryHealth = "Over voltage";
                    break;
                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                    batteryHealth = "Overheat";
                    break;
                case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                    batteryHealth = "Unknown";
                    break;
                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                    batteryHealth = "Unspecified failure";
                    break;
            }
            return batteryHealth;
        }
    };
    private Sensor batSaudApi = new Sensor(batSaudSr, "", -1);

    private Sensor batCapFile = new Sensor("/sys/devices/platform/battery_meter/FG_Current", "mA", -1);
    private SensorRunnable batCapSr = new SensorRunnable() {
        @Override
        public String getValue() {
            int cap = 0;

            Object mPowerProfile;
            double batteryCapacity = 0;
            final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

            try {
                mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                        .getConstructor(Context.class)
                        .newInstance(context);

                batteryCapacity = (double) Class
                        .forName(POWER_PROFILE_CLASS)
                        .getMethod("getBatteryCapacity")
                        .invoke(mPowerProfile);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return (int) batteryCapacity + "";
        }
    };
    private Sensor batCapApi = new Sensor(batCapSr, "mA", -1);


    private Sensor carrVoltFile = new Sensor("/sys/class/power_supply/battery/ChargerVoltage", "V", 3);
    private SensorRunnable carrVoltSr = new SensorRunnable() {
        @Override
        public String getValue() {
            return "";
        }
    };
    private Sensor carrVoltApi = new Sensor(carrVoltSr, "", 1);

    private Sensor carrUsbFile = new Sensor("/sys/class/power_supply/usb/online", "", -1);
    private SensorRunnable carrUsbSr = new SensorRunnable() {
        @Override
        public String getValue() {
            int plugged = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
            if (plugged == BatteryManager.BATTERY_PLUGGED_USB) {
                return "True";
            } else {
                return "False";
            }
        }
    };
    private Sensor carrUsbApi = new Sensor(carrUsbSr, "", -1);

    private Sensor carrFontFile = new Sensor("/sys/class/power_supply/ac/online", "", -1);
    private SensorRunnable carrFontSr = new SensorRunnable() {
        @Override
        public String getValue() {
            int plugged = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
            if (plugged == BatteryManager.BATTERY_PLUGGED_AC) {
                return "True";
            } else {
                return "False";
            }
        }
    };
    private Sensor carrFontApi = new Sensor(carrFontSr, "", -1);

    private Sensor carrWirelessFile = new Sensor("/sys/class/power_supply/wireless/online", "", -1);
    private SensorRunnable carrWirelessSr = new SensorRunnable() {
        @Override
        public String getValue() {
            int plugged = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
            if (plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS) {
                return "True";
            } else {
                return "False";
            }
        }
    };
    private Sensor carrWirelessApi = new Sensor(carrWirelessSr, "", -1);


    private String senBatVol;
    private String senBatAmp;
    private String senBatPorcent;
    private String senBatTemp;
    private String senBatSaud;
    private String senBatCap;

    private String senCarrVolt;
    private String senCarrAmp;
    private String senCarrUsb;
    private String senCarrFont;
    private String senCarrWireless;

    private SensorManager() {
    }

    void init(Context context) {
        this.context = context;
        ifilterBattChanged = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBatteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        }
    }

    public String getSenBatVol() {
        return senBatVol;
    }

    public String getSenBatAmp() {
        return senBatAmp;
    }

    //public float getsenBatAmpMed() { }

    public String getSenBatPorcent() {
        return senBatPorcent;
    }

    public String getSenBatTemp() {
        return senBatTemp;
    }

    public String getSenBatSaud() {
        return senBatSaud;
    }

    public String getSenBatCap() {
        return senBatCap;
    }

    public String getSenCarrVolt() {
        return senCarrVolt;
    }

    public String getSenCarrAmp() {
        return senCarrAmp;
    }

    public String getSenCarrUsb() {
        return senCarrUsb;
    }

    public String getSenCarrFont() {
        return senCarrFont;
    }

    public String getSenCarrWireless() {
        return senCarrWireless;
    }

    public int getBatAmpMed() {
        try {
            String s = getSenBatAmp();
            float amp = Float.parseFloat(s.substring(0, s.length() - 2));

            if (amp < batAmpMed) {
                return BAT_AMP_MED_BAIX;
            } else if (amp < batAmpAlt) {
                return BAT_AMP_MED_MED;
            } else {
                return BAT_AMP_MED_ALT;
            }
        } catch (Exception e) {
            return BAT_AMP_MED_ERRO;
        }
    }

    public String getTempEstim() {
        try {
            float amp = Float.parseFloat(getSenBatAmp().substring(0, getSenBatAmp().length() - 2));
            //if(amp<0)
            //    return "--:-- H";
            float porcet = Float.parseFloat(getSenBatPorcent().substring(0, getSenBatPorcent().length() - 1));
            float cap = Float.parseFloat(getSenBatCap().substring(0, getSenBatCap().length() - 2));


            float atual = (cap / 100) * porcet;
            if (amp < 0) {
                amp*=-1;
                atual = cap - atual;
            }
            float thf = atual / amp;
            int th = (int) thf;
            thf -= th;
            int ts = (int) (60 * thf);

            if (amp < 0) {
                return "+" + String.format("%02d", th) + ":" + String.format("%02d", ts);
            } else {
                return "-" + String.format("%02d", th) + ":" + String.format("%02d", ts);
            }

        } catch (Exception e) {
            return "--:--";
        }
    }

    public int getModeBatVolt() {
        return batVoltMode;
    }

    public int getModeBatAmp() {
        return batAmpMode;
    }

    public int getModeBatPorcent() {
        return batPorcentMode;
    }

    public int getModeBatTemp() {
        return batTempMode;
    }

    public int getModeBatSaud() {
        return batSaudMode;
    }

    public int getModeBatCapac() {
        return batCapMode;
    }

    public int getModeCarrVolt() {
        return carrVoltMode;
    }

    public int getModeCarrAmp() {
        return carrAmpMode;
    }

    public int getModeCarrUSB() {
        return carrUsbMode;
    }

    public int getModeCarrFont() {
        return carrFontMode;
    }

    public int getModeCarrWireless() {
        return carrWirelessMode;
    }


    void refresh() {
        batteryStatus = context.registerReceiver(null, ifilterBattChanged);

        if (batVoltMode == MODE_FILE)
            senBatVol = batVoltFile.getValue();
        else if (batVoltMode == MODE_API) {
            senBatVol = batVoltApi.getValue();
        } else {
            senBatVol = "";
        }

        if (batAmpMode == MODE_FILE)
            senBatAmp = batAmpFile.getValue();
        else if (batAmpMode == MODE_API) {
            senBatAmp = batAmpApi.getValue();
        } else {
            senBatAmp = "";
        }

        if (batPorcentMode == MODE_FILE)
            senBatPorcent = batPorcentFile.getValue();
        else if (batPorcentMode == MODE_API) {
            senBatPorcent = batPorcentApi.getValue();
        } else {
            senBatPorcent = "";
        }

        if (batTempMode == MODE_FILE)
            senBatTemp = batTempFile.getValue();
        else if (batTempMode == MODE_API) {
            senBatTemp = batTempApi.getValue();
        } else {
            senBatTemp = "";
        }

        if (batSaudMode == MODE_FILE)
            senBatSaud = batSaudFile.getValue();
        else if (batSaudMode == MODE_API) {
            senBatSaud = batSaudApi.getValue();
        } else {
            senBatSaud = "";
        }

        if (batCapMode == MODE_FILE) {
            senBatCap = batCapFile.getValue();
        } else if (batCapMode == MODE_API) {
            senBatCap = batCapApi.getValue();
        } else if (batCapMode == MODE_CONST) {
            senBatCap = batCapConst;
        } else {
            senBatCap = "";
        }


        if (carrVoltMode == MODE_FILE)
            senCarrVolt = carrVoltFile.getValue();
        else if (carrVoltMode == MODE_API) {
            senCarrVolt = carrVoltApi.getValue();
        } else {
            senCarrVolt = "";
        }

        if (carrAmpMode == MODE_FILE)
            senCarrAmp = "";
        else if (carrAmpMode == MODE_API) {
            senCarrAmp = "";
        } else {
            senCarrAmp = "";
        }


        if (carrUsbMode == MODE_FILE)
            senCarrUsb = carrUsbFile.getValue();
        else if (carrUsbMode == MODE_API) {
            senCarrUsb = carrUsbApi.getValue();
        } else {
            senCarrUsb = "";
        }

        if (carrFontMode == MODE_FILE)
            senCarrFont = carrFontFile.getValue();
        else if (carrFontMode == MODE_API) {
            senCarrFont = carrFontApi.getValue();
        } else {
            senCarrFont = "";
        }

        if (carrWirelessMode == MODE_FILE)
            senCarrWireless = carrWirelessFile.getValue();
        else if (carrWirelessMode == MODE_API) {
            senCarrWireless = carrWirelessApi.getValue();
        } else {
            senCarrWireless = "";
        }
    }

}
