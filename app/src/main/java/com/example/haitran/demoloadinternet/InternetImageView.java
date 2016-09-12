package com.example.haitran.demoloadinternet;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class InternetImageView extends ImageView {

    public InternetImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private static final String TAG = "DownloadImageTask";

    public void setImageAsync(String url) {
        setImageResource(R.drawable.thumbnail_default);
        new DownloadImageTask().execute(url);
    }

    public void setImageRx(final String url) {
        setImageResource(R.drawable.thumbnail_default);
        final rx.Observable<Bitmap> operationObservable = rx.Observable.create(new rx.Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                subscriber.onNext(downloadBitmap(url));
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
            operationObservable.subscribe(new Subscriber<Bitmap>() {
                @Override
                public void onCompleted() {
                    Snackbar.make(getRootView(), "OK", Snackbar.LENGTH_LONG).show();
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Bitmap bitmap) {
                    if (bitmap != null) {
                        InternetImageView.this.setImageBitmap(bitmap);
                    } else {
                        InternetImageView.this.setImageResource(R.drawable.thumbnail_default);
                        Toast.makeText(getContext(), "Null", Toast.LENGTH_LONG).show();
                    }
                }
            });
    }

    public Bitmap downloadBitmap(String url) {
        HttpURLConnection urlConnection = null;
        Bitmap bitmap = null;

        try {
            Log.i(TAG, "Downloading:  " + url);
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode < 0) {
                Log.e(TAG, "Image not found.  Response code = " + responseCode);
            } else {
                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(inputStream, null, options);
                    options.inSampleSize = calculateInSampleSize(options, 500, 400);
                    options.inJustDecodeBounds = false;
                    urlConnection = (HttpURLConnection) new URL(url).openConnection();
                    inputStream = urlConnection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                    if (inputStream != null)
                        inputStream.close();
                }
            }
        } catch (Exception e) {
            urlConnection.disconnect();
            bitmap = null;
            Log.e(TAG, "Error has occurred while downloading image from " + url, e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return bitmap;
    }

    ////////////////////////////

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            InternetImageView.this.setImageResource(R.drawable.thumbnail_default);
        }

        @Override
        protected Bitmap doInBackground(String... param) {
            String url = param[0];
            Bitmap bitmap = null;
            bitmap = download(url);
            return bitmap;
        }


        public Bitmap download(String url) {
            return downloadBitmap(url);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                InternetImageView.this.setImageBitmap(bitmap);
            } else {
                InternetImageView.this.setImageResource(R.drawable.thumbnail_default);
                Toast.makeText(getContext(), "Null", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // BEGIN_INCLUDE (calculate_sample_size)
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }

            long totalPixels = width * height / inSampleSize;

            // Anything more than 2x the requested pixels we'll sample down further
            final long totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2;
                totalPixels /= 2;
            }
        }
        return inSampleSize;
        // END_INCLUDE (calculate_sample_size)
    }

}
