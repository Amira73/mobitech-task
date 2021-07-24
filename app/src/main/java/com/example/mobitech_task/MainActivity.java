package com.example.mobitech_task;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    public static final Integer REQUEST_CODE_FOR_PERMISSION = 0x1;
    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];
    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];
    TextView txt_batteryhealth;
    int deviceHealth;
    String currentBatteryHealth = "Battery Health ";
    int batteryLevel;
    IntentFilter intentfilter;
    Button btn_sensor;
    Button btn_battery;
    Button txt_rec_btn;
    TextView txt_battery_level, txt_immei_val;
    TextView step_sensor_val, light_sensor_val, distance_sensor_val, txt_batt_lvl;
    private SensorManager mSensorManager;
    private SensorManager sensorManager;
    private Sensor mLight, mstep, mdistance;
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level * 100 / (float) scale;
            txt_batt_lvl.setText(String.valueOf(batteryPct) + "%");
        }
    };
    private BroadcastReceiver broadcastreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            deviceHealth = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);

            if (deviceHealth == BatteryManager.BATTERY_HEALTH_COLD) {

                txt_batteryhealth.setText(currentBatteryHealth + " = Cold");
            }

            if (deviceHealth == BatteryManager.BATTERY_HEALTH_DEAD) {

                txt_batteryhealth.setText(currentBatteryHealth + " = Dead");
            }

            if (deviceHealth == BatteryManager.BATTERY_HEALTH_GOOD) {

                txt_batteryhealth.setText(currentBatteryHealth + " = Good");
            }

            if (deviceHealth == BatteryManager.BATTERY_HEALTH_OVERHEAT) {

                txt_batteryhealth.setText(currentBatteryHealth + " = OverHeat");
            }

            if (deviceHealth == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE) {

                txt_batteryhealth.setText(currentBatteryHealth + " = Over voltage");
            }

            if (deviceHealth == BatteryManager.BATTERY_HEALTH_UNKNOWN) {

                txt_batteryhealth.setText(currentBatteryHealth + " = Unknown");
            }
            if (deviceHealth == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE) {

                txt_batteryhealth.setText(currentBatteryHealth + " = Unspecified Failure");
            }
        }

//    public String getIMEI(Activity activity) {
//        TelephonyManager telephonyManager = (TelephonyManager) activity
//                .getSystemService(Context.TELEPHONY_SERVICE);
////        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
////            // TODO: Consider calling
////            //    ActivityCompat#requestPermissions
////            // here to request the missing permissions, and then overriding
////            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
////            //                                          int[] grantResults)
////            // to handle the case where the user grants the permission. See the documentation
////            // for ActivityCompat#requestPermissions for more details.
////            return "";
////        }
//        return telephonyManager.getImei();
//    }

//    public static String getSerialNumber() {
//        String serialNumber;
//
//        try {
//            Class<?> c = Class.forName("android.os.SystemProperties");
//            Method get = c.getMethod("get", String.class);
//
//            serialNumber = (String) get.invoke(c, "gsm.sn1");
//            if (serialNumber.equals(""))
//                serialNumber = (String) get.invoke(c, "ril.serialnumber");
//            if (serialNumber.equals(""))
//                serialNumber = (String) get.invoke(c, "ro.serialno");
//            if (serialNumber.equals(""))
//                serialNumber = (String) get.invoke(c, "sys.serialnumber");
//            if (serialNumber.equals(""))
//                serialNumber = Build.SERIAL;
//
//            // If none of the methods above worked
//            if (serialNumber.equals(""))
//                serialNumber = null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            serialNumber = null;
//        }
//
//        return serialNumber;
//    }


    };

    public static void askForPermission(Activity context, String permission, Integer requestCode) {
        if ((Build.VERSION.SDK_INT >= 23) && (context != null) && (TextUtils.isEmpty(permission) == false)) {
            ActivityCompat.requestPermissions(context, new String[]{permission}, requestCode);
        }
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    public static String getIMEIDeviceId(Context context) {

        String deviceId;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }
            assert mTelephony != null;
            if (mTelephony.getImei() != null) {
                System.out.println("in iF cONDITION");
                deviceId = mTelephony.getImei();
            } else {
                deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                System.out.println("in else cONDITION");
            }
        }
        Log.d("deviceId", deviceId);
        return deviceId;
    }

    @Override
    protected void onStart() {
        super.onStart();
        askForPermission(this, Manifest.permission.READ_PHONE_STATE, REQUEST_CODE_FOR_PERMISSION);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView txt_serial = findViewById(R.id.txt_serial);
        TextView txt_brand_name = findViewById(R.id.txt_brandnamw);
        txt_batteryhealth = findViewById(R.id.txt_batteryhealth);
        btn_battery = findViewById(R.id.battery_btn);
        txt_battery_level = findViewById(R.id.txt_batterylevel);
        btn_sensor = findViewById(R.id.sensor_btn);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mstep = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mdistance = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        Button btn_step_sensor = (Button) findViewById(R.id.sensor_btn_dtep);
        light_sensor_val = findViewById(R.id.light_sensor_value);
        step_sensor_val = findViewById(R.id.step_sensor_value);
        txt_immei_val = findViewById(R.id.txt_imme_val);
        txt_immei_val.setText(getIMEIDeviceId(this));
        txt_batt_lvl = findViewById(R.id.txt_batt_val);
        distance_sensor_val = findViewById(R.id.distance_sensor_value);
        txt_rec_btn= findViewById(R.id.txt_rec_btn);
        txt_rec_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent light_intent = new Intent(getApplicationContext(), txt_recog.class);
                startActivity(light_intent);
            }
        });
        //   txt_batt_lvl.setText();


        // lightsensor lightsensor_obj=new lightsensor();

        // lightsensor_obj.onSensorChanged();
//         btn_step_sensor.setOnClickListener(new View.OnClickListener() {
//             @Override
//             public void onClick(View v) {
//                 Intent step_intent=new Intent(getApplicationContext(),stepActivity2.class);
//                 startActivity(step_intent);
//             }
//         });
//         Button btn_light_sensor=findViewById(R.id.sensor_btn_light);
//         btn_light_sensor.setOnClickListener(new View.OnClickListener() {
//             @Override
//             public void onClick(View v) {
//                 Intent light_intent=new Intent(getApplicationContext(),txt_recog.class);
//                 startActivity(light_intent);
//             }
//         });
//         btn_sensor.setOnClickListener(new View.OnClickListener() {
//             @Override
//             public void onClick(View v) {
//                 Intent intent=new Intent(getApplicationContext(),SensorActivity.class);
//                 startActivity(intent);
//             }
//         });

        String myVersion = android.os.Build.VERSION.RELEASE; // e.g. myVersion := "1.6"
        int sdkVersion = android.os.Build.VERSION.SDK_INT;


        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // Do something for lollipop and above versions
            txt_serial.setText("sdk version=" + getIMEIDeviceId(this));
            txt_brand_name.setText("brand name" + getDeviceName());
        } else {
            // do something for phones running an SDK before lollipop
        }


        intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        btn_battery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MainActivity.this.registerReceiver(broadcastreceiver, intentfilter);

            }
        });


        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));


//
//        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            light_sensor_val.setText(String.valueOf(event.values[0]));
            //  Toast.makeText(this, "sensor changed"+event.values[0], Toast.LENGTH_SHORT).show();
            // TODO
        }
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            step_sensor_val.setText(String.valueOf(event.values[0]));
            //   Toast.makeText(this, "sensor changed" + event.values[0], Toast.LENGTH_SHORT).show();
            // TODO
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading,
                    0, accelerometerReading.length);
            float distance = event.values[0];
            distance_sensor_val.setText(String.valueOf(event.values[0]));
            //    Toast.makeText(this, "sensor changed"+distance, Toast.LENGTH_SHORT).show();
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading,
                    0, magnetometerReading.length);

        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor.getType() == Sensor.TYPE_LIGHT) {
            //    Toast.makeText(this, "accuracy changed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        mSensorManager.registerListener(this, mLight,
                SensorManager.SENSOR_DELAY_FASTEST);


        mSensorManager.registerListener(this, mstep,
                SensorManager.SENSOR_DELAY_FASTEST);


        if (mdistance != null) {
            mSensorManager.registerListener(this, mdistance,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
        Sensor magneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticField != null) {
            mSensorManager.registerListener(this, magneticField,
                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }


        super.onResume();
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(this);
        super.onPause();
    }
}