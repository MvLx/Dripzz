package com.example.dripz.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;



public class ImageDownloader {
    public interface DownloadCallback {
        void onDownloaded(String filePath);
        void onFailed(Exception e);
    }

    public static void download(Context context, String urlStr, String fileName, DownloadCallback callback) {
        new AsyncTask<Void, Void, String>() {
            Exception error = null;
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    File file = new File(context.getFilesDir(), fileName);
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                    fos.close();
                    is.close();
                    conn.disconnect();
                    return file.getAbsolutePath();
                } catch (Exception e) {
                    error = e;
                    return null;
                }
            }
            @Override
            protected void onPostExecute(String path) {
                if (path != null) {
                    callback.onDownloaded(path);
                } else {
                    callback.onFailed(error);
                }
            }
        }.execute();
    }
}