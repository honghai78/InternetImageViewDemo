package com.example.haitran.demoloadinternet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import rx.Observable;
import rx.functions.Func0;


/**
 * Created by hai.tran on 8/3/2016.
 */
public class APIDemo {
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_CONSULTATION_TIME = "consultation_time";

    /**
     * If default date is null then date use is today
     */

    public static final String DEFAULT_DATE = "2016-04-13";

    /**
     * If ROOT is 0 then Server root is staging api
     * else api is product
     */
    public static int ROOT = 0;
    public static String SERVER_ROOT;


    public static APIDemo mInstance;
    private Context mContext;

    public APIDemo(Context context) {
        SERVER_ROOT = (ROOT == 0) ? "https://app-3105.on-aptible.com/" : "https://app-2135.on-aptible.com/";
        Log.e("TAG", SERVER_ROOT);
        mContext = context;
    }

    public static APIDemo getInstance(Context context) {
        if (mInstance == null)
            mInstance = new APIDemo(context);
        else
            mInstance.mContext = context;
        return mInstance;
    }

    public Context getContext() {
        return mContext;
    }

    public JSONObject loginData() throws ExecutionException, InterruptedException {
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put(KEY_USERNAME, getContext().getString(R.string.user_root));
            jsonRequest.put(KEY_PASSWORD, getContext().getString(R.string.pass_root));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        String url = SERVER_ROOT + "api/v1/login";
        final Request.Priority priority = Request.Priority.IMMEDIATE;
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jsonRequest, future, future);
        MyVolley.getRequestQueue().add(req);
        return future.get();
    }

    public Observable<JSONObject> newLoginUseRxAndroid() {
        return Observable.defer(new Func0<Observable<JSONObject>>() {
            @Override
            public Observable<JSONObject> call() {
                try {
                    return Observable.just(loginData());
                } catch (InterruptedException | ExecutionException e) {
                    Log.e("routes", e.getMessage());
                    return Observable.error(e);
                }
            }
        });
    }


    /**
     * Function use login with admin account
     */
    public void login(Response.Listener listener, Response.ErrorListener errorListener) {
        final CookieManager manager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);
        Log.e("TAG", "RUN");
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put(KEY_USERNAME, mContext.getString(R.string.user_root));
            jsonRequest.put(KEY_PASSWORD, mContext.getString(R.string.pass_root));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST,
                SERVER_ROOT + "api/v1/login",
                jsonRequest, listener, errorListener);
        MyVolley.getRequestQueue().add(stringRequest);
    }

}