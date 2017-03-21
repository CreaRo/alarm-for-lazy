package rish.crearo.alarmmanager;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.view.Window;
import android.widget.TimePicker;

import java.text.DecimalFormat;

/**
 * Created by rish on 4/7/15.
 */
public class TimePickerDialog extends Dialog {
    String type = "begin";

    public TimePickerDialog(Context context, String type) {
        super(context);
        this.type = type;
    }

    TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_timepicker);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.setCancelable(false);
        timePicker = (TimePicker) findViewById(R.id.dialog_timePicker);
        timePicker.setIs24HourView(true);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if (type.equals("begin")) {
                    MainActivity.beginTime.hour = hourOfDay;
                    MainActivity.beginTime.minute = minute;
                    MainActivity.startTime_hour = MainActivity.beginTime.hour;
                    MainActivity.startTime_min = MainActivity.beginTime.minute;
                    MainActivity.clock_begin.setText(new DecimalFormat("00").format(MainActivity.startTime_hour) + ":" + new DecimalFormat("00").format(MainActivity.startTime_min));
                } else if (type.equals("end")) {
                    MainActivity.endTime.hour = hourOfDay;
                    MainActivity.endTime.minute = minute;
                    MainActivity.stopTime_hour = MainActivity.endTime.hour;
                    MainActivity.stopTime_min = MainActivity.endTime.minute;
                    MainActivity.clock_end.setText(new DecimalFormat("00").format(MainActivity.stopTime_hour) + ":" + new DecimalFormat("00").format(MainActivity.stopTime_min));
                }
            }
        });

        findViewById(R.id.dialog_okay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("begin")) {
                    MainActivity.beginTime.hour = timePicker.getCurrentHour();
                    MainActivity.beginTime.minute = timePicker.getCurrentMinute();
                    MainActivity.startTime_hour = MainActivity.beginTime.hour;
                    MainActivity.startTime_min = MainActivity.beginTime.minute;
                    MainActivity.clock_begin.setText(new DecimalFormat("00").format(MainActivity.startTime_hour) + ":" + new DecimalFormat("00").format(MainActivity.startTime_min));

                } else if (type.equals("end")) {
                    MainActivity.endTime.hour = timePicker.getCurrentHour();
                    MainActivity.endTime.minute = timePicker.getCurrentMinute();
                    MainActivity.stopTime_hour = MainActivity.endTime.hour;
                    MainActivity.stopTime_min = MainActivity.endTime.minute;
                    MainActivity.clock_end.setText(new DecimalFormat("00").format(MainActivity.stopTime_hour) + ":" + new DecimalFormat("00").format(MainActivity.stopTime_min));
                }
                dismiss();
            }
        });

    }
}
