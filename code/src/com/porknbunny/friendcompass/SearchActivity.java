package com.porknbunny.friendcompass;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItem;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends FragmentActivity implements TextWatcher {
    private static final String TAG = "FriendCompass.SearchActivity";
    private static final int TEMP_BUFF_SIZE = 16384;
    private EditText searchField;
    private ListView listView;
    private SearchResultsAdapter srAdapter;
    private ArrayList<Business> results;
    private Location location;
    private Location edistant;
    private Friend friend;
    
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        friend = (Friend) getIntent().getExtras().getSerializable("friend");
        
        
        
        //---home
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //--- searchField ---
        searchField = (EditText) findViewById(R.id.search_field);
        searchField.addTextChangedListener(this);
        searchField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                boolean isEnter = (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER);
                if (isEnter) {
                    new SAPIQuery().execute(new String[]{searchField.getText().toString()});
                    searchField.clearFocus();
                }
                return isEnter;
            }
        });

        //--- resultList ---
        results = new ArrayList<Business>();

        srAdapter = new SearchResultsAdapter();

        listView = (ListView) findViewById(R.id.result_view);
        listView.setAdapter(srAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Business business = results.get(position);
                Intent intent = new Intent(getApplicationContext(), NavigateActivity.class);
                intent.putExtra("business",business.getId());
                intent.putExtra("friend",friend);
                startActivity(intent);
            }
        });


        //-- location --
        LocationManager locationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
        List<String> providers = locationManager.getAllProviders();

        double accuracy = -1;
        if (providers.size() == 0) {
            Log.w(TAG, "No providers available");
        }
        for (String provider : providers) {
            Location tempLoc = locationManager.getLastKnownLocation(provider);
            if (tempLoc != null && (accuracy < 0 || tempLoc.getAccuracy() < accuracy)) {
                location = tempLoc;
            }
        }

        edistant = new Location("pnb");
        edistant.setLatitude((location.getLatitude()+friend.getLat())/2);
        edistant.setLongitude((location.getLongitude()+friend.getLongi())/2);
        
        //initial search
        new SAPIQuery().execute(new String[]{""});
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
        SAPIQuery task = new SAPIQuery();
        task.execute(new String[]{searchField.getText().toString()});
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
            if (convertView == null) {
                convertView = inflateService.inflate(R.layout.search_item, parent, false);
                int resourceList[] = {R.id.si_address,
                        R.id.si_category,
                        R.id.si_distance,
                        R.id.si_edistance,
                        R.id.si_name,
                        R.id.si_phone,
                        R.id.si_suburb};
                for (int res : resourceList) {
                    convertView.setTag(res, convertView.findViewById(res));
                }
            }

            Business business = results.get(position);
            if (business != null) {
                ((TextView) convertView.getTag(R.id.si_address)).setText(business.getAddressLine());
                ((TextView) convertView.getTag(R.id.si_category)).setText(business.getCategory());
                ((TextView) convertView.getTag(R.id.si_distance)).setText("" + NumberFormat.getInstance().format((int) business.getLocation().distanceTo(location)) + "m");
                ((TextView) convertView.getTag(R.id.si_edistance)).setText("" + NumberFormat.getInstance().format((int) business.getLocation().distanceTo(edistant)) + "m");
                ((TextView) convertView.getTag(R.id.si_name)).setText(business.getName());
                ((TextView) convertView.getTag(R.id.si_phone)).setText(business.getPhoneNumber());
                ((TextView) convertView.getTag(R.id.si_suburb)).setText(business.getSuburb());
            }
            return convertView;
        }
    }


    //--- AsyncDoSearch ---
    private class SAPIQuery extends AsyncTask<String, Void, String> {
        private String url;
        private final int BUFF_SIZE = 16384;
        private final String sapi_key;

        private SAPIQuery() {
            sapi_key = getMetaData("SAPI_KEY");
        }

        @Override
        protected String doInBackground(String... searchTerms) {
            ArrayList<String> newList = new ArrayList<String>();
            for (String searchTerm : searchTerms) {
                try {
                    location.getLatitude();
                    url = "http://api.sensis.com.au/ob-20110511/test/search?key="
                            + sapi_key
                            + "&query="
                            + URLEncoder.encode(searchTerm, "UTF-8")
                            + "&location="
                            + URLEncoder.encode(edistant.getLatitude() +", " + edistant.getLongitude(), "UTF-8")
                            + "&sortBy=DISTANCE";
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
                results = new ArrayList<Business>();
                //time to parse some JSON!
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject jsonBusiness = jsonArray.getJSONObject(i);

                            String name = jsonBusiness.getString("name");
                            String id = jsonBusiness.getString("id");

                            //address and gps
                            JSONObject address = jsonBusiness.getJSONObject("primaryAddress");
                            String latitudeStr = address.getString("latitude");
                            String longitudeStr = address.getString("longitude");
                            Double latitude = new Double(latitudeStr);
                            Double longitude = new Double(longitudeStr);
                            String addressLine = address.getString("addressLine");
                            String suburb = address.getString("suburb");

                            //category
                            String category = "";
                            try {
                                JSONArray categories = jsonBusiness.getJSONArray("categories");
                                for (int j = 0; j < categories.length(); j++) {
                                    JSONObject cat = categories.getJSONObject(j);
                                    category = cat.getString(name);
                                }
                            } catch (Exception e) {

                            }

                            //phone
                            JSONArray contacts = jsonBusiness.getJSONArray("primaryContacts");
                            String phone = "";
                            try {
                                for (int j = 0; j < contacts.length(); j++) {
                                    JSONObject contact = contacts.getJSONObject(j);
                                    if (contact.getString("type").compareTo("Phone") == 0) {
                                        phone = contact.getString("value");
                                    }
                                }
                            } catch (Exception e) {

                            }

                            Business business = new Business(name,
                                    latitude,
                                    longitude,
                                    addressLine,
                                    suburb,
                                    id,
                                    category,
                                    phone);
                            results.add(business);
                        } catch (Exception e) {

                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                srAdapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}