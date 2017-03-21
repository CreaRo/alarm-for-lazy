package rish.crearo.alarmmanager;

import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.AlarmClock;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Time;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFloat;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    static TextView clock_begin, clock_end;
    static Time beginTime, endTime;

    static int startTime_hour, startTime_min;
    static int stopTime_hour, stopTime_min;
    static int alarmInterval = 5;
    int totalAlarms = 1;

    NumberPicker numberPicker;

    String[] numbers = {"5", "10", "15", "30"};

    Handler handler;

    LinearLayout lines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1976D2")));

        handler = new Handler();

        beginTime = new Time();
        beginTime.hour = 6;
        beginTime.minute = 0;
        endTime = new Time();
        endTime.hour = 7;
        endTime.minute = 0;

        startTime_hour = beginTime.hour;
        startTime_min = beginTime.minute;

        stopTime_hour = endTime.hour;
        stopTime_min = endTime.minute;

        alarmInterval = 5;

        clock_begin = (TextView) findViewById(R.id.clockbegin);
        clock_end = (TextView) findViewById(R.id.clockend);

        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);

        lines = (LinearLayout) findViewById(R.id.linearlayout_numbers);

        numberPicker.setDisplayedValues(numbers);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(numbers.length - 1);
        numberPicker.setWrapSelectorWheel(false);

        setNumberPickerTextColor(numberPicker, Color.WHITE);

        ButtonFloat float_delete = (ButtonFloat) findViewById(R.id.buttonFloat_delete);


        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                alarmInterval = Integer.parseInt(numbers[newVal]);
                generateLines();
            }
        });

        animate(findViewById(R.id.clockbegin));

        findViewById(R.id.buttonFloat_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long stopTime = (stopTime_hour * 60 + stopTime_min);
                long startTime = (startTime_hour * 60 + startTime_min);

                if ((stopTime - startTime) >= 12 * 60) {
                    //do nothing
                    Toast.makeText(getApplicationContext(), "Too Many alarms", Toast.LENGTH_LONG).show();
                } else if ((stopTime - startTime) <= -12 * 60) {
                    stopTime = ((stopTime_hour + 24) * 60 + stopTime_min);
                }

                totalAlarms = (int) ((stopTime - startTime) / alarmInterval);

                if (!clock_begin.getText().toString().contains("Begin") && !clock_end.getText().toString().contains("End")) {
                    stopAnimate(findViewById(R.id.clockbegin));
                    stopAnimate(findViewById(R.id.clockend));
//                    System.out.println("total alarms = " + totalAlarms);
                    for (int count = 0; count <= totalAlarms; count++) {
//                        handler.postDelayed(createRunnable(count), 1000);
                        createAlarm(count);
//                        System.out.println("Setting alarm " + count);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (clock_begin.getText().toString().contains("Begin")) {
                    animate(findViewById(R.id.clockbegin));
                } else if (clock_end.getText().toString().contains("End")) {
                    animate(findViewById(R.id.clockend));
                }
            }
        });

        float_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String clockImpls[] = {
                        "com.android.deskclock.ALARM_ALERT",
                        "com.google.android.deskclock",
                        "com.android.deskclock.ALARM_DISMISS",
                        "com.htc.android.worldclock.WorldClockTabControl",
                        "com.sonyericsson.organizer.Organizer_WorldClock",
                        "com.htc.android.worldclock.AlarmAlert",
                        "com.htc.android.worldclock.TimerAlert",
                        "com.sec.android.app.clockpackage.ClockPackage",
                        "com.motorola.blur.alarmclock.AlarmClock",
                        "com.android.deskclock.ALARM_DONE",
                        "com.android.deskclock.ALARM_SNOOZE",
                        "com.android.deskclock.DeskClock",
                        "com.google.android.deskclock-3.0.0",
                        "com.android.deskclock.AlarmClock",
                        "com.motorola.blur.alarmclock.AlarmAlert",
                        "com.motorola.blur.alarmclock.AlarmClock",
                        "com.motorola.blur.alarmclock.AlarmTimerAlert",
                        "com.android.alarmclock.AlarmClock",
                        "com.android.deskclock.DeskClock",
                        "com.sec.android.app.clockpackage.alarm.AlarmAlert",
                        "com.android.alarmclock.ALARM_ALERT",
                        "com.sec.android.app.clockpackage.ClockPackage",
                        "com.samsung.sec.android.clockpackage.alarm.ALARM_ALERT",
                        "com.htc.android.worldclock.ALARM_ALERT",
                        "com.sonyericsson.alarm.ALARM_ALERT",
                        "zte.com.cn.alarmclock.ALARM_ALERT",
                        "com.motorola.blur.alarmclock.ALARM_ALERT",
                        "com.mobitobi.android.gentlealarm.ALARM_INFO",
                        "com.urbandroid.sleep.alarmclock.ALARM_ALERT",
                        "com.splunchy.android.alarmclock.ALARM_ALERT"
                };

                String packageName = "NULL";
                for (int i = 0; i < clockImpls.length; i++)
                    if (doesPackageExist(clockImpls[i]))
                        packageName = clockImpls[i];

                if (!packageName.equals("NULL")) {
                    Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.setData(Uri.parse("package:" + packageName));
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to Open", Toast.LENGTH_LONG).show();
                }
            }
        });

        clock_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(MainActivity.this, "begin").show();
                stopTime_hour = endTime.hour;
                stopTime_min = endTime.minute;
                clock_begin.setText(new DecimalFormat("00").format(startTime_hour) + ":" + new DecimalFormat("00").format(startTime_min));
                stopAnimate(findViewById(R.id.clockbegin));
                animate(findViewById(R.id.clockend));
            }
        });

        clock_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(MainActivity.this, "end").show();
                stopTime_hour = endTime.hour;
                stopTime_min = endTime.minute;
                clock_end.setText(new DecimalFormat("00").format(stopTime_hour) + ":" + new DecimalFormat("00").format(stopTime_min));
                stopAnimate(findViewById(R.id.clockbegin));
                stopAnimate(findViewById(R.id.clockend));
                generateLines();
            }
        });

        findViewById(R.id.buttonFloat_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AlarmClock.ACTION_SHOW_ALARMS);

                List<ResolveInfo> infos = getPackageManager().queryIntentActivities(i, 0);
                if (infos.size() > 0) {
                    //Then there is application can handle your intent
                    Toast.makeText(getApplicationContext(), "what man = " + infos.size(), Toast.LENGTH_LONG).show();
                } else {
                    //No Application can handle your intent
                }

                for (ResolveInfo resolveInfo : infos) {
                    System.out.println(resolveInfo.activityInfo + " " + " " + resolveInfo.resolvePackageName);
                }

//                startActivity(i);
            }
        });
    }

    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText) child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                } catch (NoSuchFieldException e) {
                } catch (IllegalAccessException e) {
                } catch (IllegalArgumentException e) {
                }
            }
        }
        return false;
    }


    public boolean doesPackageExist(String targetPackage) {
        List<ApplicationInfo> packages;
        PackageManager pm;

        pm = getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals(targetPackage))
                return true;
        }
        return false;
    }

    public void createAlarm(int count) {
        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
        if (count != totalAlarms)
            i.putExtra(AlarmClock.EXTRA_SKIP_UI, true);

        int hour = (startTime_hour * 60 + startTime_min + (alarmInterval * count)) / 60;
        int min = (startTime_hour * 60 + startTime_min + (alarmInterval * count)) % 60;

        if (hour >= 24)
            hour -= 24;

        i.putExtra(AlarmClock.EXTRA_HOUR, hour);
        i.putExtra(AlarmClock.EXTRA_MINUTES, min);

//        System.out.println("Set alarm for " + hour + " : " + min);
        startActivity(i);
    }


    public void animate(View mView) {
        ObjectAnimator animator = ObjectAnimator
                .ofFloat(mView, "translationX", 0, 10, -10, 10, -10, 7, -7, 5, -5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
//                .ofFloat(R.id.main_alarmimg1, "translationX", 0, 6, -6, 6, -6, 6, -6, 6, -6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
                .setDuration(3000);
        animator.setRepeatCount(Animation.INFINITE);
        animator.start();
    }

    public void stopAnimate(View mView) {
        ObjectAnimator animator = ObjectAnimator
                .ofFloat(mView, "translationX", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
                .setDuration(1500);
        animator.setRepeatCount(Animation.INFINITE);
        animator.start();
    }

    public void generateLines() {
        lines.removeAllViews();
        long stopTime = (stopTime_hour * 60 + stopTime_min);
        long startTime = (startTime_hour * 60 + startTime_min);

        if ((stopTime - startTime) >= 12 * 60) {
            //do nothing
        } else if ((stopTime - startTime) <= -12 * 60) {
            stopTime = ((stopTime_hour + 24) * 60 + stopTime_min);
        }
        totalAlarms = (int) (stopTime - startTime) / alarmInterval;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
        params.weight = 1.0f;
        params.gravity = Gravity.CENTER;

//        System.out.println("Total Lines = " + totalAlarms);

        for (int i = 0; i <= totalAlarms; i++) {
//            System.out.println("making alarm line " + i);
            TextView tv = new TextView(getApplicationContext());
            tv.setLayoutParams(params);
            tv.setHeight(48);
            tv.setGravity(Gravity.CENTER);
            tv.setWidth(10);
            tv.setText(" | ");
            tv.setTextColor(Color.parseColor("#FFFFFF"));
            lines.addView(tv);
        }
    }
}