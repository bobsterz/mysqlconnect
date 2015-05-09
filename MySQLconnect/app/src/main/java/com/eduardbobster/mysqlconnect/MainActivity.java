package com.eduardbobster.mysqlconnect;

/**
 * Activity download records in ListView from MySQL.
 *
 * This activity showing records from database using JSON, PHP.
 *
 * @author Eduard Bobsterz
 * @version 1.0
 */


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    ListView listView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView1 = (ListView)findViewById(R.id.listView);

        //Стандартный тулбар из библиотеки appcompat-v7:21.0.3
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //При запуске активити данный метод обновляет наш listView1
        onClickUpdate();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //обработчик события на вью элементах которые закреплены на тулбаре :D
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update:
                onClickUpdate();
                return true;
            case R.id.action_add:
                Intent intent = new Intent(getApplicationContext(), InsertActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //данный метот получает записи из базы данных для нашего listView1
    //создаеться экземпляр класса где все и реализовано.
    //также реализована проверка через if есть ли доступ к интернету
    public void onClickUpdate() {
        if ( !isOnline() ){
            Toast.makeText(getApplicationContext(),
                    "Check internet connection",Toast.LENGTH_LONG).show();
            return;
        } else {
            ReadData task1 = new ReadData();
            task1.execute(new String[]{"http://bobsterz.co.ua/android/readjson.php"});
        }
    }

    //В данном классе испотльзовано наследие от AsyncTask и задействованы
    //переопределение методов onPreExecute(), doInBackground(), onPostExecute.
    private class ReadData extends AsyncTask<String, Void, Boolean> {
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        //данный метод срабатывает первым и ждет выполнения метода doInBackground().
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Reading Data...");
            dialog.show();
            super.onPreExecute();
        }

        String text = "";
        ArrayList<String> list1;


        //метод получения данных
        @Override
        protected Boolean doInBackground(String... urls) {
            //создаем строку для входного потока
            InputStream is1;
            //подключаемся и читаем данные которые пришли
            for (String url1 : urls) {
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(url1);
                    HttpResponse response = client.execute(post);
                    is1 = response.getEntity().getContent();

                } catch (ClientProtocolException e) {
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    return false;
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    return false;
                }

                //конверитуем наши данные в текст
                BufferedReader reader;
                try {
                    reader = new BufferedReader(new InputStreamReader(is1, "utf-8"), 8);
                    String line = null;
                    while ((line = reader.readLine()) != null){
                        text += line + "\n";
                    }
                    is1.close();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Конвертируем текст в формат JSON в массив list1
                list1 = new ArrayList<String>();
                try {
                    JSONArray json = new JSONArray(text);
                    for (int i = 0; i<json.length(); i++){
                        JSONObject jsonData = json.getJSONObject(i);
                        list1.add(jsonData.getString("st_name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //результат тру
            return true;
        }

        //завершение
        //и если результат тру, создаем ArrayAdapter и записываем данные в listView1.
        @Override
        protected void onPostExecute(Boolean result) {
            if (result == true) {
                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, list1);
                listView1.setAdapter(adapter1);
            } else {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        }
    }

    //проверка подключен ли интернет на устройстве
    protected boolean isOnline() {
        String cs = Context.CONNECTIVITY_SERVICE;
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(cs);
        if (cm.getActiveNetworkInfo() == null) {
            return false;
        } else {
            return true;
        }
    }
}