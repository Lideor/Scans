package com.example.scan;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;



public class JsonParse {
    private String LOG_TAG = "mylogs";
    final String FILENAME = "Package";
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
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
    public void importJsonInFile(Context ctn){
        Gson gson = new Gson();

        String jsonString="";

        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    ctn.openFileInput(FILENAME)));

            // читаем содержимое
            while ((jsonString = br.readLine()) != null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(LOG_TAG,"00000000000000000000");
        Log.d(LOG_TAG,jsonString);
        //return true;
    }
}
