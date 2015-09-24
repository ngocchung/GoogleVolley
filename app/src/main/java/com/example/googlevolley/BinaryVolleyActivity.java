package com.example.googlevolley;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class BinaryVolleyActivity extends AppCompatActivity {
    private final Context mContext = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binary_volley);

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        String url = "http://www.youtubeinmp3.com/download/get/?i=3sI2yV5mJ0kQ8CnddqmANZqK8a%2BgVQJ%2Fmg3xwhHTUsJ3Ix%2FDOIH2ysnvxAaf1jrpAM7mKSwIk%2F2%2B7DpIH5wYFQ%3D%3D";

        BaseVolleyRequest volleyRequest = new BaseVolleyRequest(url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                Map<String, String> headers = response.headers;
                String contentDisposition = headers.get("Content-Disposition");
                // String contentType = headers.get("Content-Type");
                String[] temp = contentDisposition.split("filename=");
                String fileName = temp[1].replace("\"", "") + ".mp3";
                InputStream inputStream = new ByteArrayInputStream(response.data);
                createLocalFile(inputStream, fileName);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
            }
        });

        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(volleyRequest);
    }
    private String createLocalFile(InputStream inputStream, String fileName) {
        try {
            String folderName = "MP3VOLLEY";
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, folderName);
            folder.mkdir();
            File file = new File(folder, fileName);
            file.createNewFile();
            FileOutputStream f = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                f.write(buffer, 0, length);
            }
            //f.flush();
            f.close();
            return file.getPath();
        } catch (IOException e) {
            return e.getMessage();
        }
    }
}
