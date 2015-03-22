package com.ericbullington.speechtojapanese;

import android.util.Log;
import android.os.AsyncTask;
import android.content.Context;

import java.io.IOException;
import java.io.File;
import java.lang.System;
import java.lang.StringBuilder;
import java.util.Properties;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

public class PostSample extends AsyncTask<String, Long, String> {

    private Context context;
    private Properties p;

    public PostSample(Context context) {
        this.context = context;
    }

    protected String doInBackground(String... params) {
        try {

            PropertyReader pReader = new PropertyReader(this.context);
            p = pReader.getProperties("credentials.properties");

            String s = String.format("%s/v1/recognize", p.getProperty("url"));

            File f = new File(params[0]);

            /* HttpRequest request =  HttpRequest.get("http://speechtojapanese.mybluemix.net/") */
            HttpRequest request =  HttpRequest.post(s)
                .accept("application/json")
                .trustAllCerts() //Temporarily accept all certificates
                .basic(p.getProperty("username"), p.getProperty("password"));

            request.part("form[body]", "Making a multipart request");
            request.part("form[file]", f);

            return request.body();

        } catch (HttpRequestException exception) {
            Log.e("MyApp", "Request exception", exception);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String responseString) {
        if (responseString != null) {
            Log.d("MyApp", responseString);
        } else {
            Log.d("MyApp", "Request failed");
        }
    }

}
