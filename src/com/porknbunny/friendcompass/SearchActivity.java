package com.porknbunny.friendcompass;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SearchActivity extends FragmentActivity implements TextWatcher {
    private static final String TAG = "FriendCompass.SearchActivity";
    private static final int TEMP_BUFF_SIZE = 16384;
    private EditText searchField;
    private ListView listView;
    private SearchResultsAdapter srAdapter;
    private ArrayList<String> results;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        //--- searchField ---
        searchField = (EditText) findViewById(R.id.searh_field);
        searchField.addTextChangedListener(this);
        searchField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                return (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });


        //--- resultList ---
        results = new ArrayList<String>();

        //debug
        results.add("Hello");
        results.add("Good-bye");


        srAdapter = new SearchResultsAdapter(getApplicationContext(), R.id.only_field, results);

        listView = (ListView) findViewById(R.id.result_view);
        listView.setAdapter(srAdapter);
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

    //---- SearchResultsAdapter ---
    private class SearchResultsAdapter extends ArrayAdapter<String> {
        ArrayList<String> list;
        LayoutInflater inflateService;

        public SearchResultsAdapter(Context context, int textViewResourceId, ArrayList list) {
            super(context, textViewResourceId, list);
            this.list = list;
            inflateService = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflateService.inflate(R.layout.search_item, parent, false);
                convertView.setTag(R.id.only_field, convertView.findViewById(R.id.only_field));
            }

            String searchItem = list.get(position);
            if (searchItem != null) {
                TextView bodyTextView = (TextView) convertView.getTag(R.id.only_field);
                bodyTextView.setText(searchItem);
            }
            return convertView;
        }
    }
}