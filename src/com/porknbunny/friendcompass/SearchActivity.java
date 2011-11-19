package com.porknbunny.friendcompass;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SearchActivity extends FragmentActivity implements TextWatcher {
    private static final String TAG = "FriendCompass.SearchActivity";
    private static final int TEMP_BUFF_SIZE = 16384;
    private EditText searchField;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        searchField = (EditText) findViewById(R.id.searchField);
        searchField.addTextChangedListener(this);
    }

    private String getMetaData(String key) {
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            String value = bundle.getString(key);
            //Log.d(TAG, "Meta-data: " + key + " - " +value);
            return value;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to load meta-data Key: " + key + ", NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
        }
        return "";
    }


    private BufferedInputStream getUrl(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setReadTimeout(5000);
            InputStream inputStream = (InputStream) connection.getContent();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, TEMP_BUFF_SIZE);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                return bufferedInputStream;
            }
            Log.w(TAG, "Incorrect response for " + url + " : " + connection.getResponseCode());
        } catch (Exception e) {
            e.printStackTrace();
            //Log.w(TAG, e.getMessage());
            Log.w(TAG, "Could not get " + url);
        }
        return null;
    }


    //---- TextWatcher ----
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void afterTextChanged(Editable editable) {
        //do search

    }
}