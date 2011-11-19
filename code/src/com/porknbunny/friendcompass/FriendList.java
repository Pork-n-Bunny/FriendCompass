package com.porknbunny.friendcompass;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
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
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: pigsnowball
 * Date: 20/11/2011
 * Time: 01:51
 * To change this template use File | Settings | File Templates.
 */
public class FriendList extends FragmentActivity {
    private static final String TAG = "FriendList";
    ArrayList<String> friendList;
    FriendListAdapter friendAdapter;
    ListView listView;
    private String userName;

    private Location location;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends);


        //--- friendsList ---
        friendList = new ArrayList<String>();

        friendAdapter = new FriendListAdapter();

        listView = (ListView) findViewById(R.id.friend_list);
        listView.setAdapter(friendAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                //Business business = results.get(position);
                //Intent intent = new Intent(getApplicationContext(), NavigateActivity.class);
                //intent.putExtra("business",business);
                //startActivity(intent);
            }
        });

        //getuser
        userName = getUsername();

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
    }

    public String getUsername(){
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name);
        }

        if(!possibleEmails.isEmpty() && possibleEmails.get(0) != null){
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");
            if(parts.length > 0 && parts[0] != null)
                return parts[0];
            else
                return null;
        }else
            return null;
    }

    private class FriendListAdapter extends BaseAdapter{
        LayoutInflater inflateService;

        public FriendListAdapter() {
            inflateService = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount () {
            return friendList.size();
        }

        @Override
        public Object getItem ( int i){
            return i;
        }

        @Override
        public long getItemId ( int i){
            return i;
        }

        @Override
        public View getView ( int position, View convertView, ViewGroup parent){
            if (convertView == null) {
                convertView = inflateService.inflate(R.layout.search_item, parent, false);
                int resourceList[] = {R.id.fl_name,
                        R.id.fl_dist,};
                for (int res : resourceList) {
                    convertView.setTag(res, convertView.findViewById(res));
                }
            }

            String friend = friendList.get(position);
            if (friend != null) {
                ((TextView) convertView.getTag(R.id.fl_name)).setText(friend);
                ((TextView) convertView.getTag(R.id.fl_dist)).setText(friend);
            }
            return convertView;
        }
    }


        //--- AsyncDoSearch ---
        private class FriendQuery extends AsyncTask<String, Void, String> {
            private String url;
            private final int BUFF_SIZE = 16384;

            @Override
            protected String doInBackground(String... searchTerms) {
                ArrayList<String> newList = new ArrayList<String>();
                for (String searchTerm : searchTerms) {
                    try {
                        location.getLatitude();
                        url = "http://api.sensis.com.au/ob-20110511/test/search?key="
                                + "&query="
                                + URLEncoder.encode(searchTerm, "UTF-8")
                                + "&location="
                                + URLEncoder.encode(location.getLatitude() + ", " + location.getLongitude(), "UTF-8")
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
                    friendList = new ArrayList<String>();
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

                                friendList.add("Hello");
                            } catch (Exception e) {

                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    friendAdapter.notifyDataSetChanged();
                }
            }
        }



    private BufferedInputStream getUrl(String url) {
        int TEMP_BUFF_SIZE = 16384;
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


}