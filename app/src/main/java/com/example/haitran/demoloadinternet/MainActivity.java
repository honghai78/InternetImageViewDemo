package com.example.haitran.demoloadinternet;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.AutoText;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyVolley.init(getBaseContext());
      final  InternetImageView imageView = (InternetImageView) findViewById(R.id.image);
        findViewById(R.id.btAsync).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setImageAsync("https://upload.wikimedia.org/wikipedia/commons/thumb/4/41/Siberischer_tiger_de_edit02.jpg/1024px-Siberischer_tiger_de_edit02.jpg");
            }
        });
        findViewById(R.id.rx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setImageRx("https://upload.wikimedia.org/wikipedia/commons/thumb/4/41/Siberischer_tiger_de_edit02.jpg/1024px-Siberischer_tiger_de_edit02.jpg");
            }
        });

        findViewById(R.id.volley).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Login().execute();
               // new Login().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
            }
        });
    }
    private void login()
    {
        final String TAG = "TAG";
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Process");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Response.Listener listener = new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                Log.e(TAG,  response.toString());
                progressDialog.dismiss();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError cause) {
                String s = new String(cause.networkResponse.data, Charset.forName("UTF-8"));
                Log.e(TAG, s);
                Log.e(TAG, cause.toString());
                progressDialog.dismiss();
            }
        };
        APIDemo.getInstance(MainActivity.this).login(listener, errorListener);
    }

    private  void loginUseRx()
    {
        final String TAG = "TAG";
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Process");
        progressDialog.setCancelable(false);
        progressDialog.show();
        APIDemo.getInstance(MainActivity.this).newLoginUseRxAndroid().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        VolleyError cause = (VolleyError) e.getCause();
                        String s = new String(cause.networkResponse.data, Charset.forName("UTF-8"));
                        Log.e(TAG, s);
                        Log.e(TAG, cause.toString());
                        progressDialog.dismiss();
                    }
                    @Override
                    public void onNext(JSONObject jsonObject) {
                        Log.e(TAG, "onNext " + jsonObject.toString());
                        progressDialog.dismiss();
                    }
                });
    }

    private class Login extends AsyncTask<Void, Void, JSONObject>
    {
        final String TAG = "TAG";
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Process");
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
               return APIDemo.getInstance(MainActivity.this).loginData();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject aVoid) {
            Log.e(TAG,  aVoid.toString());
            progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }
}