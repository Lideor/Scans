package com.example.scan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CalendarActivity extends Activity {

    Context ctn=this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        Calendar calendar = Calendar.getInstance();
        Intent intent=getIntent();
        int year = intent.getIntExtra("year", calendar.get(Calendar.YEAR));
        int month = intent.getIntExtra("month", calendar.get(Calendar.MONTH))-1;
        int days = intent.getIntExtra("day", calendar.get(Calendar.DAY_OF_MONTH));
        Date current = new GregorianCalendar(year,month,days).getTime();
        calendarView.setDate(current.getTime());
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year,
                                            int month, int dayOfMonth) {
                int mYear = year;
                int mMonth = month;
                int mDay = dayOfMonth;
                Intent intent = new Intent(ctn, MainActivity.class);
                intent.putExtra("year",mYear);
                intent.putExtra("month",mMonth);
                intent.putExtra("day",mDay);
                startActivity(intent);
            }
        });
    }
}