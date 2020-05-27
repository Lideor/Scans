package com.example.scan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scan.ItemModel.CycleEvent;
import com.example.scan.ItemModel.Event;
import com.example.scan.ItemModel.Model;
import com.example.scan.ItemModel.NoCycleEvent;
import com.example.scan.ItemModel.Time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.scan.MainActivity.LOG_TAG;

public class RVAdapterDay  extends RecyclerView.Adapter<RVAdapterDay.CardViewHolder> {

    private List<Event> list = new ArrayList<Event>();
    private Model model;
    private Time day;
    private int id;
    private Context ctn;//контекст обьекта который вызвал адаптер

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        CardViewHolder(CardView cv) {
            super(cv);
            cardView = cv;
        }
    }

    RVAdapterDay(Model model, Time day,int id, Context ctn) {
        this.model = model;
        this.day = day;
        this.ctn = ctn;
        this.list = model.getEventInDays(day);
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())

                .inflate(R.layout.card_lesson, parent, false);

        return new CardViewHolder(cv);

    }
    @Override
    public void onBindViewHolder(CardViewHolder cardViewHolder, final int position) {

        CardView cardView = cardViewHolder.cardView;
        RelativeLayout all = (RelativeLayout) cardView.findViewById(R.id.all);// контейнер всех элементов

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctn, EventActivity.class);
                intent.putExtra("type", list.get(position).getType());
                intent.putExtra("id", list.get(position).getEventId());
                ctn.startActivity(intent);


            }

        });

        RelativeLayout main = (RelativeLayout) all.findViewById(R.id.main);// контейнер всех элементов

        RelativeLayout time = (RelativeLayout) main.findViewById(R.id.time);// контейнер времени

        TextView start = (TextView) time.findViewById(R.id.start);
        start.setText(list.get(position).getTime().getStart().getStringShort());//устанавливаем время начала занятия
        TextView end = (TextView) time.findViewById(R.id.end);
        end.setText(list.get(position).getTime().getEnd().getStringShort());//устанавливаем время конца занятия
      //  Log.d(LOG_TAG,"тип обьекта:"+list.get(position).getClass().getName().equals(new CycleEvent().getClass().getName()));
        if(list.get(position).getType()==0){
            NoCycleEvent event=(NoCycleEvent) list.get(position);
            if(event.getColor()!=null)time.setBackgroundColor(Integer.parseUnsignedInt(event.getColor(),16));
        }
        else{
            CycleEvent event=(CycleEvent) list.get(position);
            Log.d(LOG_TAG,"тип клаастера:"+event.getClusterId());
            time.setBackgroundColor(Integer.parseUnsignedInt(model.getCluster(event.getClusterId()).getColor(),16));
        }
        RelativeLayout day = (RelativeLayout) main.findViewById(R.id.day);// контейнер всех элементов
        TextView title = (TextView) day.findViewById(R.id.title);
        if(list.get(position).getName()==null) {
            title.setText(list.get(position).getAddress());
            if(list.get(position).getAddress().length()>16) title.setTextSize(20);
        }


        else {
            title.setText(list.get(position).getName());
            TextView type = (TextView) day.findViewById(R.id.type);
            type.setText(list.get(position).getAddress());
        }
        RelativeLayout rang = (RelativeLayout) main.findViewById(R.id.rang);// контейнер времени
        TextView rang_text = (TextView) rang.findViewById(R.id.rang_text);
        Log.d(LOG_TAG,list.get(position).getRang()+"");
        rang_text.setText(list.get(position).getRang()+"");

        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.BL_TR,
                new int[] { 0xff220000+0x00200000*list.get(position).getRang(), 0xff220000+0x00200000*list.get(position).getRang()});
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        drawable.setCornerRadius(40);
        rang.setBackground(drawable);


    }


    @Override

    public int getItemCount() {
        return list.size();
    }


}

