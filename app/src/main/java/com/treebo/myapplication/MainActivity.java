package com.treebo.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_get_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetDataTask(MainActivity.this).execute();
            }
        });
    }

    public static class GetDataTask extends AsyncTask<Void, String, String> {

        private WeakReference<Context> mContextWeakReference;

        public GetDataTask(Context context) {
            mContextWeakReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(Void... voids) {
            URL url;
            try {
                url = new URL("https://omd1kwzdxk.execute-api.ap-south-1.amazonaws.com/Prod/getcategorydata");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return e.getMessage();
            }

            HttpsURLConnection urlConnection;
            try {
                urlConnection = (HttpsURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
                return e.getMessage();
            }

            // timeout 0 means no timeout
            urlConnection.setReadTimeout(0);
            urlConnection.setConnectTimeout(0);

            try {
                urlConnection.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
                return e.getMessage();
            }

            try {
                int responseCode = urlConnection.getResponseCode();
                boolean success = responseCode == HttpsURLConnection.HTTP_OK;
                if (success) {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    return readStream(in);
                } else {
                    return "FAILED";
                }
            } catch (IOException e) {
                return e.getMessage();
            } finally {
                urlConnection.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Context context = mContextWeakReference.get();
            if (context != null) {
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        }

        private String readStream(InputStream inputStream) {
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            String line;
            try {
                while ((line = r.readLine()) != null) {
                    total.append(line).append('\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return total.toString();
        }
    }
}
