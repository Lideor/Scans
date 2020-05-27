package com.example.scan;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.scan.ItemAddress.ReqAddress;
import com.example.scan.ItemModel.Model;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.example.scan.ItemAddress.ResultAddress;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class JsonParse {

    private String getPointAddress="http://search.maps.sputnik.ru/search/addr?q=";

    private String LOG_TAG = "mylogs";
    private final String FILENAME = "Package.txt"; // имя файла
    private final String JSONFILE = "Model.json";
    //загрухка в файл пакета
    public boolean exportJsonInFile(List<SaveOnePackage> allPackage, Context ctn){
        Gson gson = new Gson();
        String jsonString = gson.toJson(allPackage);
        Log.d(LOG_TAG,jsonString);
        try {
            // отрываем поток для записи

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ctn.openFileOutput(FILENAME, MODE_PRIVATE)));
            // пишем данные
            bw.write(jsonString);
            // закрываем поток
            bw.close();

            Log.d(LOG_TAG, "Файл записан");

        } catch (FileNotFoundException e) {
            Log.d(LOG_TAG,"Exp=" + e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(LOG_TAG,"Exp=" + e);
        }
        return true;
    }

    //выгрузка из файла пакетов
    public String importJsonInFile(Context ctn){
        Gson gson = new Gson();

        String jsonString="";

        try {
            // открываем поток для чтения
            File file = new File(ctn.getFilesDir(),FILENAME);
            FileInputStream fileReader = new FileInputStream(file);

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    ctn.openFileInput(FILENAME)));

            // читаем содержимое
            try {
                StringBuilder sb=  new StringBuilder();
                while ( (jsonString=br.readLine()) != null) {
                    sb.append(jsonString);
                }
                jsonString = sb.toString();
                br.close();
                fileReader.close();
            } catch (IOException e)
            {
                Log.d(LOG_TAG,"Exp=" + e);
            }

        } catch (FileNotFoundException e) {
            Log.d(LOG_TAG,"Exp=" + e);
        }

        Log.d(LOG_TAG,jsonString);
        return jsonString;
        //return true;
    }

    //очистка файла пакетов
    public boolean deleteFile(Context ctn){
        ctn.deleteFile(FILENAME);
        return true;
    }


    //конферт json в модель
    public Model convertJsonModel(String jsonModel){
        Model model=null;
        Gson gson = new GsonBuilder().create();

        try {
            model = gson.fromJson(jsonModel, Model.class);//десерелизуем полученную строку по образу объекта
        } catch (ArithmeticException e) {
            Log.d(LOG_TAG,e.toString());
        }

        return model;
    }

    public Model importModelToJson(Context ctn){
        InputStreamReader streamReader = null;
        FileInputStream fileInputStream = null;
        try{
            fileInputStream = ctn.openFileInput(JSONFILE);
            streamReader = new InputStreamReader(fileInputStream);
            Gson gson = new Gson();
            Model model = gson.fromJson(streamReader, Model.class);
            return  model;
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        finally {
            if (streamReader != null) {
                try {
                    streamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public boolean exportModelToJson(Context ctn, Model model){
        Gson gson = new Gson();
        String jsonString = gson.toJson(model);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = ctn.openFileOutput(JSONFILE,ctn.MODE_PRIVATE);
            fileOutputStream.write(jsonString.getBytes());
            SharedPreferences sPref = ctn.getSharedPreferences("prefs",MODE_PRIVATE);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putInt("model",1);
            ed.apply();
            return true;
        } catch ( Exception e ){
            Log.d(LOG_TAG,e.toString());
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    public int checkPoint(String address){

        Gson gson = new GsonBuilder().create();

        try {
            GetPoint sender = new GetPoint();
            sender.execute(String.valueOf(address));
            sender.get();
            String jsonModel=sender.get();
            ReqAddress req = gson.fromJson(jsonModel, ReqAddress.class);//десерелизуем полученную строку по образу объекта
            if(req.result.getViewport().getTopLat()!=0) return 1;
        } catch (Exception e) {
            Log.d(LOG_TAG,e.toString());
            return -1;
        }

        return -1;
    }

    private class GetPoint extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                DefaultHttpClient hc = new DefaultHttpClient();
                ResponseHandler<String> res = new BasicResponseHandler();
                HttpGet getMethod = new HttpGet(getPointAddress+params[0].replaceAll(" ", "%20"));
                Log.d(LOG_TAG,res.toString());

                //получаем ответ от сервера
                String response = hc.execute(getMethod, res);
               // Log.d(LOG_TAG,new String(response.getBytes("ISO-8859-1")));
                response=new String(response.getBytes("ISO-8859-1"));
                return response;


            } catch (Exception e) {
                Log.d(LOG_TAG,"Exp=" + e);
                Log.d(LOG_TAG,"error=11");
                return "0";
            }
        }
    }


}
