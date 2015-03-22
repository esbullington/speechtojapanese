package com.ericbullington.speechtojapanese;

import android.util.Log;
import android.os.AsyncTask;
import android.content.Context;

import java.io.IOException;
import java.io.File;
import java.lang.System;
import java.util.Properties;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

public class PostSample extends AsyncTask<String, Long, String> {

    private Context context;
    private Properties p;

    public PostSample(Context context) {
        this.context = context;
    }

    protected String doInBackground(String... urls) {
        try {

            PropertyReader pReader = new PropertyReader(this.context);
            p = pReader.getProperties("credentials.properties");

            HttpRequest request =  HttpRequest.get(p.getProperty("url") + "/v1/models")
                .accept("application/json") 
                .basic(p.getProperty("username"), p.getProperty("password"));

            //Temporarily accept all certificates
            request.trustAllCerts();

            if (request.ok()) {
                System.out.println("Response was: " + request.body());
            }
            return request.body();

        } catch (Exception exception) {
            Log.e("MyApp", "Error opening file", exception);
            return null;
        }
    }

    protected void onPostExecute(String responseString) {
        if (responseString != null)
            Log.d("MyApp", responseString);
        else
            Log.d("MyApp", "Request failed");
    }
}
