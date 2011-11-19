package com.porknbunny.friendcompass;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends FragmentActivity implements TextWatcher {
    private static final String TAG = "FriendCompass.SearchActivity";
    private static final int TEMP_BUFF_SIZE = 16384;
    private EditText searchField;
    private ListView listView;
    private SearchResultsAdapter srAdapter;
    private ArrayList<String> results;
    private Location location;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        //--- searchField ---
        searchField = (EditText) findViewById(R.id.searh_field);
        searchField.addTextChangedListener(this);
        searchField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                boolean isEnter = (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER);
                if (isEnter) {
                    //DownloadWebPageTask task = new DownloadWebPageTask();
                    //task.execute(new String[]{"chinese"});

                    results.add("no");
                    Toast.makeText(getApplicationContext(), "" + results.size(), Toast.LENGTH_SHORT).show();
                    srAdapter.notifyDataSetChanged();
                }
                return isEnter;
            }
        });

        //--- resultList ---
        results = new ArrayList<String>();

        //debug
        results.add("Hello");
        results.add("Good-bye");

        srAdapter = new SearchResultsAdapter();

        listView = (ListView) findViewById(R.id.result_view);
        listView.setAdapter(srAdapter);


        //-- location --
        LocationManager locationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
        List<String> providers = locationManager.getAllProviders();

        double accuracy = -1;
        for (String provider : providers) {
            Location tempLoc = locationManager.getLastKnownLocation(provider);
            if (accuracy < 0 || tempLoc.getAccuracy() < accuracy) {
                location = tempLoc;
            }
        }
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
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

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

    }

    @Override
    public void afterTextChanged(Editable editable) {
        //do search
        //DownloadWebPageTask task = new DownloadWebPageTask();
        //task.execute(new String[]{"chinese"});
    }

    //---- SearchResultsAdapter ---
    private class SearchResultsAdapter extends BaseAdapter {
        LayoutInflater inflateService;

        public SearchResultsAdapter() {
            inflateService = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return results.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.v(TAG, "" + position + " " + results.size());


            if (convertView == null) {
                convertView = inflateService.inflate(R.layout.search_item, parent, false);
                convertView.setTag(R.id.only_field, convertView.findViewById(R.id.only_field));
            }

            String searchItem = results.get(position);
            if (searchItem != null) {
                TextView bodyTextView = (TextView) convertView.getTag(R.id.only_field);
                bodyTextView.setText(searchItem);
            }
            return convertView;
        }
    }


    //--- AsyncDoSearch ---
    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        //private String url = "http://api.sensis.com.au/ob-20110511/test/search?key=cd4n3ez5zsf56ehevh6phr8w&query=hello&location=-37.818712214939296%2C+144.9567931238562&sortBy=DISTANCE";
        private final int BUFF_SIZE = 16384;

        private String url = "http://www.google.com/";

        private DownloadWebPageTask() {
        }

        @Override
        protected String doInBackground(String... searchTerms) {
            ArrayList<String> newList = new ArrayList<String>();
            for (String searchTerm : searchTerms) {
                try {
                    URL searchUrl = new URL("http://api.sensis.com.au/ob-20110511/test/search?key="
                            + getMetaData("SAPI_KEY")

                            + "&query="

                            + URLEncoder.encode(searchTerm, "UTF-8")

                            + "&location="

                            + URLEncoder.encode(location.getLatitude() + ", " + location.getLongitude(), "UTF-8"));
                } catch (MalformedURLException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                BufferedInputStream inputStream = getUrl(url);

                if (inputStream != null) {

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(BUFF_SIZE);
                    try {
                        if (inputStream != null) {
                            byte[] tempBuff = new byte[BUFF_SIZE];
                            int readCount;
                            while ((readCount = inputStream.read(tempBuff)) != -1) {
                                byteArrayOutputStream.write(tempBuff, 0, readCount);
                            }
                            byte[] newBuff = byteArrayOutputStream.toByteArray();
                            inputStream.close();
                            byteArrayOutputStream.close();
                            String temp = new String(newBuff, "US_ASCII");
                            return temp;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                results.add(result);
                srAdapter.notifyDataSetChanged();
            }
        }
    }
}