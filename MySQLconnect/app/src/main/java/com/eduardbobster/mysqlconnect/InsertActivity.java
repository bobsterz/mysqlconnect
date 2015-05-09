package com.eduardbobster.mysqlconnect;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;


public class InsertActivity extends ActionBarActivity {

    EditText etName;
    EditText etPhone;
    EditText etDesc;
    Button btnSendData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        etName = (EditText) findViewById(R.id.editName);
        etPhone = (EditText) findViewById(R.id.editPhone);
        etDesc = (EditText) findViewById(R.id.editDesc);
        btnSendData = (Button) findViewById(R.id.btnSend);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void onClickSend(View v) {
        if ( !isOnline() ){
            Toast.makeText(getApplicationContext(),
                    "Check internet connection",Toast.LENGTH_LONG).show();
            return;
        } else {
            InsertData task1 = new InsertData();
            task1.execute(new String[]{"http://www.bobsterz.co.ua/android/insert.php"});
        }
    }

    private class InsertData extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(InsertActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Sending Data...");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... urls) {

            for (String url1 : urls) {
                try {
                    ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
                    pairs.add(new BasicNameValuePair("txtName", etName.getText().toString()));
                    pairs.add(new BasicNameValuePair("txtTel", etPhone.getText().toString()));
                    pairs.add(new BasicNameValuePair("txtDesc", etDesc.getText().toString()));

                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(url1);
                    post.setEntity(new UrlEncodedFormEntity(pairs, "utf-8"));
                    HttpResponse response = client.execute(post);
                } catch (ClientProtocolException e) {
                    Toast.makeText(InsertActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    return false;
                } catch (IOException e) {
                    Toast.makeText(InsertActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result == true) {
                Toast.makeText(InsertActivity.this, "Insert Success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(InsertActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        }
    }

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