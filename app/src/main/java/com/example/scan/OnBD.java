package com.example.scan;


import android.os.AsyncTask;
import android.util.Log;

import com.example.scan.ItemAddress.ReqAddress;
import com.example.scan.ItemAnswerAdd.AnswerAddCycle;
import com.example.scan.ItemAnswerAdd.AnswerAddNoCycle;
import com.example.scan.ItemModel.Model;
import com.example.scan.ItemModel.NoCycleEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.scan.MainActivity.LOG_TAG;

public class OnBD {
    private final String getModel = "http://10.0.2.2:8000/cgi-bin/get_model.py?login_id="; // Файл расписания
    private final String addNoCycleEvent="http://10.0.2.2:8000/cgi-bin/add_no_cycle_events.py";

    public String GetJsonModel(int id){

        String jsonModel="";
        try {
            GetModel sender = new GetModel();
            sender.execute(String.valueOf(id));
            sender.get();
            jsonModel=sender.get();

        } catch (Exception e) {
            Log.d(LOG_TAG,"Exp=" + e);
        }
        return jsonModel;
    }

    public AnswerAddNoCycle AddNoCycleEvent(int id, String address, String start, String end,
                               String name,String color,String rang,int eventId){

        AddNoCycleEvent sender = new AddNoCycleEvent();

        try {
            sender.execute(String.valueOf(id),address,start,end,color,name,rang,String.valueOf(eventId));

            sender.get();
            String jsonAnswer=sender.get();
            Gson gson = new GsonBuilder().create();
            AnswerAddNoCycle req = gson.fromJson(jsonAnswer, AnswerAddNoCycle.class);
            return req;
        } catch (Exception e){

            Log.d(LOG_TAG,"Exp=" + e);

        }

        return null;
    }


    private class GetModel extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                DefaultHttpClient hc = new DefaultHttpClient();
                ResponseHandler<String> res = new BasicResponseHandler();
                HttpGet getMethod = new HttpGet(getModel+params[0]);

                //получаем ответ от сервера
                String response = hc.execute(getMethod, res);

                Log.d(LOG_TAG,new String(response.getBytes("ISO-8859-1")));
                response=new String(response.getBytes("ISO-8859-1"));
                return response;


            } catch (Exception e) {
                Log.d(LOG_TAG,"Exp=" + e);

                Log.d(LOG_TAG,"error=11");
                return "0";
            }
        }
    }

    class AddNoCycleEvent extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                DefaultHttpClient hc = new DefaultHttpClient();
                ResponseHandler<String> res = new BasicResponseHandler();
                HttpPost postMethod = new HttpPost(addNoCycleEvent);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("login_id",params[0]));
                nameValuePairs.add(new BasicNameValuePair("address",new String((params[1]).getBytes(),"ISO-8859-1")));
                nameValuePairs.add(new BasicNameValuePair("date_start",params[2]));
                nameValuePairs.add(new BasicNameValuePair("date_end",params[3]));
                nameValuePairs.add(new BasicNameValuePair("color",params[4]));
                nameValuePairs.add(new BasicNameValuePair("name",new String((params[5]).getBytes(),"ISO-8859-1")));
                nameValuePairs.add(new BasicNameValuePair("rang",new String((params[6]).getBytes(),"ISO-8859-1")));
                nameValuePairs.add(new BasicNameValuePair("event_id",params[7]));
                postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                String response = hc.execute(postMethod, res);


                Log.d(LOG_TAG,response);
                return response;


            } catch (Exception e) {
                Log.d(LOG_TAG,"Exp=" + e);
                Log.d(LOG_TAG,"error=11");
                return "-3";
            }



        }
    }

}
