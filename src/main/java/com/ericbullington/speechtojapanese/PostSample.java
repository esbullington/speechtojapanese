package com.ericbullington.speechtojapanese;

import android.util.Base64;
import android.util.Log;

import android.os.AsyncTask;
import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.StringBuilder;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

import org.json.JSONObject;

public class PostSample extends AsyncTask<String, Long, String> {

    private static int BUFSIZE = 1024;
    private static String TAG = "PostSample";
    private Context context;
    private Properties p;

    public PostSample(Context context) {
        this.context = context;
    }

    private String readInputStreamToString(HttpURLConnection connection) {
        String result = null;
        StringBuffer sb = new StringBuffer();
        InputStream is = null;

        try {
            is = new BufferedInputStream(connection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            result = sb.toString();
        }
        catch (Exception e) {
            Log.d(TAG, "Error reading InputStream");
            result = null;
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException e) {
                    Log.d(TAG, "Error closing InputStream");
                }
            }
        }

        return result;
    }

    private void writeStream(InputStream inputstream, OutputStream outputstream)
            throws IOException
    {
        byte abyte0[] = new byte[1024];
        int i;
        while((i = inputstream.read(abyte0)) >= 0)
            outputstream.write(abyte0, 0, i);
        inputstream.close();
        outputstream.close();
    }

    protected String doInBackground(String... params) {

        Log.i(TAG, "Requesting speech-to-text and translation...");

        try {

            PropertyReader pReader = new PropertyReader(this.context);
            p = pReader.getProperties("credentials.properties");


            String s = String.format("%s/v1/recognize", p.getProperty("url"));

            File f = new File(params[0]);

            URL url = new URL(s);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            try {
                byte[] basicCredentials = String.format("%s:%s", p.getProperty("username") , p.getProperty("password")).getBytes();
                String encoded = Base64.encodeToString(basicCredentials, Base64.NO_WRAP);
                urlConnection.setRequestProperty("Authorization", "Basic "+encoded);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "audio/l16; rate=44100");
                urlConnection.setRequestProperty("Transfer-Encoding", "identity");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setChunkedStreamingMode(0);

                InputStream in = new BufferedInputStream(new FileInputStream(f));
                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                writeStream(in, out);
                int status = urlConnection.getResponseCode();
                String message = urlConnection.getResponseMessage();

                Log.d(TAG, "Response code: " + status);
                Log.d(TAG, "Response message: " + message);
                switch (status) {
                    case 200:
                    case 201:
                        String result = readInputStreamToString(urlConnection);
                        String responseString =  new JSONObject(result).getJSONArray("results")
                                .getJSONObject(0).getJSONArray("alternatives")
                                .getJSONObject(0).getString("transcript");
                        Log.d(TAG, "response string: " + responseString);
                        String translatedString = HttpRequest.get(p.getProperty("google_url"), true, "key", p.getProperty("google_key"), 'q', responseString, "source", "en", "target", "ja")
                                .body();
                        String finalString = new JSONObject(translatedString).getJSONObject("data").getJSONArray("translations").getJSONObject(0).getString("translatedText");
                        Log.d(TAG, "translated string: " + finalString);
                        return finalString;
                }
            } catch (Exception ex) {
                Log.e(TAG, "exception", ex);
            } finally {
                urlConnection.disconnect();
            }

            return null;

        } catch (MalformedURLException ex) {
            Log.e(TAG, "Request exception", ex);
            return null;
        } catch (IOException ex) {
            Log.e(TAG, "Request exception", ex);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String responseString) {
        if (responseString != null) {
            Log.d(TAG, "onPostExecute string: " + responseString);
        } else {
            Log.d(TAG, "Request failed");
        }
    }

}
