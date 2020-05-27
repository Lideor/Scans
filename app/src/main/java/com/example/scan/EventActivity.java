package com.example.scan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scan.ItemAnswerAdd.AnswerAddNoCycle;
import com.example.scan.ItemModel.Model;
import com.example.scan.ItemModel.NoCycleEvent;
import com.example.scan.ItemModel.Time;
import com.example.scan.ItemModel.TimeRange;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.jaredrummler.android.colorpicker.ColorShape;

import java.util.Calendar;

import static com.example.scan.MainActivity.LOG_TAG;

public class EventActivity extends AppCompatActivity implements ColorPickerDialogListener {

    Context cth = this;
    TimePicker time;
    View btmTime;
    DatePicker date;
    View btmDate;
    int showDateFlag=0;
    int showTimeFlag=0;
    EditText address;
    int endStartFlag = 0;
    int dateTimeFlag=0;
    TextView timeStart;
    TextView errorAddress;
    TextView dateStart;
    TextView timeEnd;
    TextView dateEnd;
    Button addButton;
    EditText titleText;
    EditText number;
    Time start = new Time(0,0);
    Time end = new Time(0,0);
    View btnColor;
    int type=-1;
    int id=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity);
        btnColor = (View) findViewById(R.id.color);
        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createColorPickerDialog(1);


            }
        });
        //настройка TODO: календаря и часовв
        time = (TimePicker) findViewById(R.id.timePicker);
        date = (DatePicker) findViewById(R.id.datePicker);
        timeStart = (TextView) findViewById(R.id.time_start);
        timeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDate(1,1);
            }
        });

        dateStart=(TextView) findViewById(R.id.date_start);
        dateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDate(2,1);
            }
        });
        timeEnd = (TextView) findViewById(R.id.time_end);
        timeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDate(1,2);
            }
        });

        dateEnd=(TextView) findViewById(R.id.date_end);
        dateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDate(2,2);
            }
        });



        btmTime=(View) findViewById(R.id.timePickerbutton);
        btmTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                desTime();
                endStartFlag=0;
                dateTimeFlag=0;
            }
        });
        btmDate=(View) findViewById(R.id.datePickerbutton);
        btmDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                desDate();
                endStartFlag=0;
                dateTimeFlag=0;
            }
        });
        date.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if(endStartFlag==1) {
                    start.setYear(year);
                    start.setDay(dayOfMonth);
                    start.setMonth(monthOfYear+1);
                }else {
                    end.setYear(year);
                    end.setDay(dayOfMonth);
                    end.setMonth(monthOfYear+1);
                }

                SetText(dayOfMonth+"-"+(monthOfYear+1)+"-"+year);
            }
        });
        time.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if(endStartFlag==1) {
                    start.setHours(hourOfDay);
                    start.setMinutes(minute);
                }else {
                    end.setHours(hourOfDay);
                    end.setMinutes(minute);
                }
                SetText(hourOfDay+":"+(minute));
            }
        });


        //натсрйока TODO: считаывающий боксов
        errorAddress=(TextView) findViewById(R.id.title_error_address);
        address=(EditText) findViewById(R.id.edit_text);
        address.addTextChangedListener(new TextWatcher(){

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count){
            }

            @Override
            public void afterTextChanged(Editable editable) {

                Log.d(LOG_TAG,editable.toString());
                if(new JsonParse().checkPoint(editable.toString())==-1) errorAddress.setVisibility(View.VISIBLE);
                else errorAddress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
        });

        addButton = (Button) findViewById(R.id.add);
        titleText=(EditText) findViewById(R.id.title_text);
        number=(EditText)findViewById(R.id.number);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO айди заменить на логи id
                ColorDrawable color = (ColorDrawable) btnColor.getBackground();
                AnswerAddNoCycle ans = new OnBD().AddNoCycleEvent(11,address.getText().toString(),
                        start.getStringLong(),end.getStringLong(),titleText.getText().toString(),
                        Integer.toHexString(color.getColor()),number.getText().toString());
                if(ans==null);//неверное введены данные
                else if(ans.getReq()==1){
                    Model model = new JsonParse().importModelToJson(cth);
                    TimeRange range = new TimeRange(start,end);
                    NoCycleEvent event = new NoCycleEvent(Integer.valueOf(number.getText().toString()),range,address.getText().toString(),titleText.getText().toString(),Integer.toHexString(color.getColor()));
                    event.setEventId(ans.getId());
                    model.addNoCycle(event);
                    new JsonParse().exportModelToJson(cth,model);
                    finish();
                }
                Log.d(LOG_TAG,ans+"");
            }
        });

        //TODO апдейт события
        Intent intent=getIntent();
        type = intent.getIntExtra("type", -1);
        id = intent.getIntExtra("id", -1);
        if(id!=-1){
            Model model = new JsonParse().importModelToJson(this);
            NoCycleEvent event = model.getEventNoCycleOnId(id);
            if(event!=null){
                titleText.setText(event.getName());
                number.setText(event.getRang()+"");
                address.setText(event.getAddress());
                if(event.getColor()!=null) btnColor.setBackgroundColor(Integer.parseUnsignedInt(event.getColor(),16));
                dateEnd.setText(event.getTime().getEnd().getStringDate());
                start=event.getTime().getStart();
                end=event.getTime().getEnd();
                dateStart.setText(event.getTime().getStart().getStringDate());
                timeStart.setText(event.getTime().getStart().getStringShort());
                timeEnd.setText(event.getTime().getEnd().getStringShort());

            }
        }

    }

    private void SetText(String text){
        if(dateTimeFlag==1){
            if(endStartFlag==1) timeStart.setText(text);
            if(endStartFlag==2) timeEnd.setText(text);
        }
        if(dateTimeFlag==2){
            if(endStartFlag==1) dateStart.setText(text);
            if(endStartFlag==2) dateEnd.setText(text);
        }
    }

    private void showTimeDate(int typeContent, int typeDate){
        //1 время 2 дата
        if(typeContent==1){
            if(showDateFlag==1) desDate();
            showTime();
        }
        if(typeContent==2){
            if(showTimeFlag==1) desTime();
            showDate();

        }
        endStartFlag=typeDate;
        dateTimeFlag=typeContent;
    }

    private void desDate(){
        RelativeLayout.LayoutParams feedCommentParams = new RelativeLayout.
                LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,0);/// создаем параметры лайаута чтобы его размер зависил от содержимого

        date.setLayoutParams(feedCommentParams);

        showDateFlag=0;
    }

    private void desTime(){
        RelativeLayout.LayoutParams feedCommentParams = new RelativeLayout.
                LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,0);/// создаем параметры лайаута чтобы его размер зависил от содержимого
        time.setLayoutParams(feedCommentParams);
        showTimeFlag=0;
    }

    private void showDate(){
        RelativeLayout.LayoutParams feedCommentParams = new RelativeLayout
                .LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);/// создаем параметры лайаута чтобы его размер зависил от содержимого
        feedCommentParams.setMargins(70,70,0,0);
        date.setLayoutParams(feedCommentParams);
        showDateFlag=1;
    }
    private void showTime(){
        RelativeLayout.LayoutParams feedCommentParams = new RelativeLayout.
                LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);/// создаем параметры лайаута чтобы его размер зависил от содержимого
        time.setLayoutParams(feedCommentParams);
        showTimeFlag=1;
    }
    private void createColorPickerDialog(int id) {
        if (id == 1) {
            ColorPickerDialog.newBuilder()
                    .setColor(Color.RED)
                    .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                    .setAllowCustom(true)
                    .setAllowPresets(true)
                    .setColorShape(ColorShape.SQUARE)
                    .setDialogId(1)
                    .show(this);
        }
// полный список атрибутов класса ColorPickerDialog смотрите ниже
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        View view = (View) findViewById(R.id.color);
        view.setBackgroundColor(color);
    }

    @Override
    public void onDialogDismissed(int dialogId) {
        Toast.makeText(this, "Dialog dismissed", Toast.LENGTH_SHORT).show();
    }

}
