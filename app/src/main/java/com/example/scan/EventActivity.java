package com.example.scan;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.scan.ItemAnswerAdd.AnswerAddNoCycle;
import com.example.scan.ItemModel.Cluster;
import com.example.scan.ItemModel.Model;
import com.example.scan.ItemModel.NoCycleEvent;
import com.example.scan.ItemModel.Time;
import com.example.scan.ItemModel.TimeRange;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.jaredrummler.android.colorpicker.ColorShape;

import java.lang.ref.SoftReference;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


import static com.example.scan.MainActivity.LOG_TAG;

public class EventActivity extends AppCompatActivity implements ColorPickerDialogListener {

    Context cth = this;

    TimePicker time;
    TimePicker timeCycle;

    DatePicker date;
    DatePicker dateCycle;

    int showDateCycleFlag = 0;
    int showTimeFlagCycle = 0;

    int endStartFlagCycle = 0;
    int dateTimeFlagCycle = 0;

    int showDateFlag = 0;
    int showTimeFlag = 0;
    EditText address;
    int endStartFlag = 0;
    int dateTimeFlag = 0;


    TextView dateStart;
    TextView dateEnd;

    TextView timeStart;
    TextView timeEnd;
    TextView errorAddress;

    TextView cluster;

    TextView params;
    TextView dateCycleTextBox;
    TextView timeStartCycle;
    TextView timeEndCycle;


    Button addButton;
    Button addButtonCycle;

    EditText titleText;
    EditText number;
    int checkSwitch = 0;
    Time start = new Time(0, 0);
    Time end = new Time(0, 0);

    Time dayCycle = new Time(0, 0);
    Time startCycle = new Time(0, 0);
    Time endCycle = new Time(0, 0);

    View btnColor;
    View btmTime;
    View btmDate;

    View btmTimeCycle;
    View btmDateCycle;

    Switch cycle;
    Toolbar tool;
    Model model;

    int type = -1;
    int selectCluster;
    int id = -1;

    RelativeLayout noCycle;
    RelativeLayout cycleLayout;

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

        noCycle = (RelativeLayout) findViewById(R.id.no_cycle);
        cycleLayout = (RelativeLayout) findViewById(R.id.cycle);
        cycleLayout.setVisibility(View.INVISIBLE);
        //настройка TODO: календаря и часовв
        model = new JsonParse().importModelToJson(cth);
        tool = (Toolbar) findViewById(R.id.toolbar);
        params = (TextView) findViewById(R.id.params);
        if (getCheckSwitch() == 0) tool.setTitle("Не циклическое событие");
        if (getCheckSwitch() == 1) {
            tool.setTitle(model.getNameCluster().get(0));

            params.setText("Параметры:" + model.getClusterArray(0).getAddress()
                    + " Ранг - " + model.getClusterArray(0).getRang());
        }
        setSupportActionBar(tool);
        tool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCheckSwitch() == 1) {
                    PopupMenu menu = new PopupMenu(cth, v);
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            Log.d(LOG_TAG, item.toString());
                            int f = model.getIdClusterOnName(item.toString());
                            if (f != -1) {
                                tool.setTitle(item.toString());
                                selectCluster = f;
                                Cluster itemModel = model.getCluster(f);
                                params.setText("Параметры:" + itemModel.getAddress()
                                        + " Ранг - " + itemModel.getRang());
                            }

                            return false;

                        }
                    });
                    for (String i : model.getNameCluster()) menu.getMenu().add(i);
                    menu.show();
                }
            }

        });
        cycle = (Switch) findViewById(R.id.switch_cycle);
        cycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchClick();
            }

        });
        //TODO НЕ циклические собыйтия
        {
            //настройка TODO: календаря и часовв
            time = (TimePicker) findViewById(R.id.timePicker);
            date = (DatePicker) findViewById(R.id.datePicker);

            //TODO сами активные боксы
            timeStart = (TextView) findViewById(R.id.time_start);
            timeStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTimeDate(1, 1);
                }
            });

            dateStart = (TextView) findViewById(R.id.date_start);
            dateStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTimeDate(2, 1);
                }
            });
            timeEnd = (TextView) findViewById(R.id.time_end);
            timeEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTimeDate(1, 2);
                }
            });

            dateEnd = (TextView) findViewById(R.id.date_end);
            dateEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTimeDate(2, 2);
                }
            });


            btmTime = (View) findViewById(R.id.timePickerbutton);
            btmTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    desTime();
                    endStartFlag = 0;
                    dateTimeFlag = 0;
                }
            });
            btmDate = (View) findViewById(R.id.datePickerbutton);
            btmDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    desDate();
                    endStartFlag = 0;
                    dateTimeFlag = 0;
                }
            });
            //TODO где пользователь тыкается
            date.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    if (endStartFlag == 1) {
                        start.setYear(year);
                        start.setDay(dayOfMonth);
                        start.setMonth(monthOfYear + 1);
                    } else {
                        end.setYear(year);
                        end.setDay(dayOfMonth);
                        end.setMonth(monthOfYear + 1);
                    }

                    SetText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                }
            });
            time.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    if (endStartFlag == 1) {
                        start.setHours(hourOfDay);
                        start.setMinutes(minute);
                    } else {
                        end.setHours(hourOfDay);
                        end.setMinutes(minute);
                    }
                    SetText(hourOfDay + ":" + (minute));
                }
            });


            //натсрйока TODO: считаывающий боксов
            errorAddress = (TextView) findViewById(R.id.title_error_address);
            address = (EditText) findViewById(R.id.edit_text);
            address.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable editable) {

                    Log.d(LOG_TAG, editable.toString());
                    if (new JsonParse().checkPoint(editable.toString()) == -1)
                        errorAddress.setVisibility(View.VISIBLE);
                    else errorAddress.setVisibility(View.INVISIBLE);
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
            });

            addButton = (Button) findViewById(R.id.add);
            titleText = (EditText) findViewById(R.id.title_text);
            number = (EditText) findViewById(R.id.number);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO айди заменить на логи id
                    ColorDrawable color = (ColorDrawable) btnColor.getBackground();
                    AnswerAddNoCycle ans = new OnBD().AddNoCycleEvent(11,
                            address.getText().toString(),
                            start.getStringLong(),
                            end.getStringLong(),
                            titleText.getText().toString(),
                            Integer.toHexString(color.getColor()),
                            number.getText().toString(),
                            id);
                    if (ans == null) ;//неверное введены данные
                    else if (ans.getReq() == 1) {
                        if (ans.getType() == 2) {
                            Model model = new JsonParse().importModelToJson(cth);
                            TimeRange range = new TimeRange(start, end);

                            NoCycleEvent event = new NoCycleEvent(
                                    Integer.valueOf(number.getText().toString()),
                                    range, address.getText().toString(),
                                    titleText.getText().toString(),
                                    Integer.toHexString(color.getColor()));


                            event.setEventId(ans.getId());
                            model.addNoCycle(event);
                            new JsonParse().exportModelToJson(cth, model);
                            finish();
                        }
                    } else if (ans.getReq() == 2)
                        if (ans.getType() == 2) {
                            Model model = new JsonParse().importModelToJson(cth);

                            SoftReference<NoCycleEvent> rect = new SoftReference<NoCycleEvent>(model.getEventNoCycleOnId(id));
                            //   NoCycleEvent event=model.getEventNoCycleOnId(ans.getId())
                            rect.get().setColor(Integer.toHexString(color.getColor()));
                            rect.get().setAddress(address.getText().toString());
                            rect.get().setName(titleText.getText().toString());
                            TimeRange range = new TimeRange(start, end);
                            rect.get().setTime(range);
                            rect.get().setRang(Integer.parseInt(number.getText().toString()));
                            NoCycleEvent event = model.getEventNoCycleOnId(id);
                            new JsonParse().exportModelToJson(cth, model);

                            finish();
                        }

                    Log.d(LOG_TAG, ans + "");
                }
            });

            //TODO апдейт события
            Intent intent = getIntent();
            type = intent.getIntExtra("type", -1);
            id = intent.getIntExtra("id", -1);
            if (id != -1) {
                Model model = new JsonParse().importModelToJson(this);
                NoCycleEvent event = model.getEventNoCycleOnId(id);
                if (event != null) {
                    titleText.setText(event.getName());
                    number.setText(event.getRang() + "");
                    address.setText(event.getAddress());
                    if (event.getColor() != null)
                        btnColor.setBackgroundColor(Integer.parseUnsignedInt(event.getColor(), 16));
                    dateEnd.setText(event.getTime().getEnd().getStringDate());
                    start = event.getTime().getStart();
                    end = event.getTime().getEnd();
                    dateStart.setText(event.getTime().getStart().getStringDate());
                    timeStart.setText(event.getTime().getStart().getStringShort());
                    timeEnd.setText(event.getTime().getEnd().getStringShort());

                }
            }


        }

        //TODO ЦИКЛические собыйтия

        dateCycle = (DatePicker) findViewById(R.id.date_picker_cycle);
        dateCycleTextBox = (TextView) findViewById(R.id.date_cycle);
        dateCycleTextBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDateCycle(2, 1);
            }
        });
        dateCycle.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Time time = model.getStartModel();
                Date date = new Date(year, monthOfYear, dayOfMonth);
                Date startDate = new Date(time.getYear(), time.getMonth(), time.getDay());
                Calendar f = new GregorianCalendar(time.getYear(), time.getMonth(), time.getDay());
                Calendar c = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                Log.d(LOG_TAG, f.toString() + " " + c.toString());

                int t = Math.abs(f.get(Calendar.WEEK_OF_YEAR) % 2 - c.get(Calendar.WEEK_OF_YEAR) % 2);
                int g = 0;
                if (c.get(Calendar.DAY_OF_WEEK) > f.DAY_OF_WEEK)
                    g = Math.abs(f.get(Calendar.DAY_OF_WEEK) - c.get(Calendar.DAY_OF_WEEK));
                else g = Math.abs(7 - f.get(Calendar.DAY_OF_WEEK)+ c.get(Calendar.DAY_OF_WEEK));
                g-=4;
                dayCycle.setDay(g);
                dayCycle.setWeek(t);
                g+=1;
                t+=1;
                dateCycleTextBox.setText(t + "-" + g);
            }
        });

        timeCycle=(TimePicker) findViewById(R.id.time_picker_cycle);
        timeCycle.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if (endStartFlagCycle == 1) {
                    startCycle.setHours(hourOfDay);
                    startCycle.setMinutes(minute);
                    timeStartCycle.setText(hourOfDay + ":" + (minute));
                } else {
                    endCycle.setHours(hourOfDay);
                    endCycle.setMinutes(minute);
                    timeEndCycle.setText(hourOfDay + ":" + (minute));

                }

            }
        });

        btmTimeCycle=(View) findViewById(R.id.time_picker_button_cycle);
        btmTimeCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                desTimeCycle();
                endStartFlagCycle=0;
                dateTimeFlagCycle=0;
            }
        });

        btmDateCycle=(View) findViewById(R.id.date_picker_button_cycle);
        btmDateCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               desDateCycle();
               endStartFlagCycle=0;
               dateTimeFlagCycle=0;
            }
        });


        timeStartCycle = (TextView) findViewById(R.id.time_cycle_start);
        timeStartCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDateCycle(1, 1);
            }
        });
        timeEndCycle = (TextView) findViewById(R.id.time_cycle_end);
        timeEndCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDateCycle(1, 2);
            }
        });



    }

    private void switchClick() {
        if (checkSwitch == 1) {
            tool.setTitle("Не циклическое событие");
            checkSwitch = 0;

            noCycle.setVisibility(View.VISIBLE);
            cycleLayout.setVisibility(View.INVISIBLE);
        } else if (checkSwitch == 0) {
            tool.setTitle(model.getNameCluster().get(0));
            ;
            checkSwitch = 1;
            params.setText("Параметры:" + model.getClusterArray(0).getAddress()
                    + " ранг - " + model.getClusterArray(0).getRang());
            noCycle.setVisibility(View.INVISIBLE);
            cycleLayout.setVisibility(View.VISIBLE);
        }
        setSupportActionBar(tool);
    }

    private int getCheckSwitch() {
        return checkSwitch;
    }

    private void SetText(String text) {
        if (dateTimeFlag == 1) {
            if (endStartFlag == 1) timeStart.setText(text);
            if (endStartFlag == 2) timeEnd.setText(text);
        }
        if (dateTimeFlag == 2) {
            if (endStartFlag == 1) dateStart.setText(text);
            if (endStartFlag == 2) dateEnd.setText(text);
        }
    }


    //TODO ЦИКЛ


    private void showTimeDateCycle(int typeContent, int typeDate) {
        //1 время 2 дата
        if (typeContent == 1) {
            if (showDateCycleFlag == 1) desDateCycle();
            showTimeCycle();
        }
        if (typeContent == 2) {
            if (showTimeFlagCycle == 1) desTimeCycle();
            showDateCycle();

        }
        endStartFlagCycle = typeDate;
        dateTimeFlagCycle = typeContent;

    }

     private void showDateCycle() {
        RelativeLayout.LayoutParams feedCommentParams = new RelativeLayout
                .LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);/// создаем параметры лайаута чтобы его размер зависил от содержимого
        feedCommentParams.setMargins(70, 70, 0, 0);
        dateCycle.setLayoutParams(feedCommentParams);
        showDateCycleFlag = 1;
    }

    private void desDateCycle() {
        RelativeLayout.LayoutParams feedCommentParams = new RelativeLayout.
                LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 0);/// создаем параметры лайаута чтобы его размер зависил от содержимого

        dateCycle.setLayoutParams(feedCommentParams);

        showDateCycleFlag = 0;
    }
    private void showTimeCycle() {
        RelativeLayout.LayoutParams feedCommentParams = new RelativeLayout.
                LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);/// создаем параметры лайаута чтобы его размер зависил от содержимого
        timeCycle.setLayoutParams(feedCommentParams);
        showTimeFlagCycle = 1;
    }

    private void desTimeCycle() {
        RelativeLayout.LayoutParams feedCommentParams = new RelativeLayout.
                LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 0);/// создаем параметры лайаута чтобы его размер зависил от содержимого
        timeCycle.setLayoutParams(feedCommentParams);
        showTimeFlagCycle = 0;
    }

    //TODO НЕ цикд

    private void showTimeDate(int typeContent, int typeDate) {
        //1 время 2 дата
        if (typeContent == 1) {
            if (showDateFlag == 1) desDate();
            showTime();
        }
        if (typeContent == 2) {
            if (showTimeFlag == 1) desTime();
            showDate();

        }
        endStartFlag = typeDate;
        dateTimeFlag = typeContent;
    }

    private void desDate() {
        RelativeLayout.LayoutParams feedCommentParams = new RelativeLayout.
                LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 0);/// создаем параметры лайаута чтобы его размер зависил от содержимого

        date.setLayoutParams(feedCommentParams);

        showDateFlag = 0;
    }

    private void desTime() {
        RelativeLayout.LayoutParams feedCommentParams = new RelativeLayout.
                LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 0);/// создаем параметры лайаута чтобы его размер зависил от содержимого
        time.setLayoutParams(feedCommentParams);
        showTimeFlag = 0;
    }

    private void showDate() {
        RelativeLayout.LayoutParams feedCommentParams = new RelativeLayout
                .LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);/// создаем параметры лайаута чтобы его размер зависил от содержимого
        feedCommentParams.setMargins(70, 70, 0, 0);
        date.setLayoutParams(feedCommentParams);
        showDateFlag = 1;
    }

    private void showTime() {
        RelativeLayout.LayoutParams feedCommentParams = new RelativeLayout.
                LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);/// создаем параметры лайаута чтобы его размер зависил от содержимого
        time.setLayoutParams(feedCommentParams);
        showTimeFlag = 1;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cluster_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.d(LOG_TAG, id + "");
        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

}
