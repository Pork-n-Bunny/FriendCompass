package com.porknbunny.friendcompass;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: pigsnowball
 * Date: 19/11/2011
 * Time: 17:28
 * To change this template use File | Settings | File Templates.
 */
public class SapiAsync extends AsyncTask<Void, Void, Boolean> {
    private Context mContext;
    private ArrayList<String> currentResults;
    private ArrayList<String> newResults;
    private String searchTerm;
    private Location location;
    private BaseAdapter adapter;

    public SapiAsync(Context context, ArrayList<String> currentResults, String searchTerm, Location location, BaseAdapter adapter) {
        super();
        this.currentResults = currentResults;
        this.searchTerm = searchTerm;
        this.location = location;
        this.adapter = adapter;
        mContext = context;
    }


    @Override
    protected Boolean doInBackground(Void nothing) {

        HasThumbnail hasThumbnail = hasThumbnailArray[0];
        if (hasThumbnail.getThumbnail() == null && hasThumbnail.getThumbnailURL() != null) {

            //Log.d(TAG, "AsyncTask working on " + hasThumbnail.getThumbnailURL());

            String url = hasThumbnail.getThumbnailURL();
            boolean useFiles = false;
            String filename = "";

            try {
                MessageDigest md = MessageDigest.getInstance("SHA");

                md.update(url.getBytes());
                byte[] digest = md.digest();


                for (byte single : digest) {
                    String hex = Integer.toHexString(0xFF & single);
                    if (hex.length() == 1) {
                        // could use a for loop, but we're only dealing with a single byte
                        filename += "0";
                    }
                    filename += hex;
                }

                useFiles = true;
            } catch (Exception e) {
                Log.w(TAG, "Hash algorithm not found.");
            }

            String path = mContext.getCacheDir() + File.separator + filename;

            File cacheFile = new File(path);
            if (cacheFile.exists()) {
                hasThumbnail.setThumbnail(cacheFile);
                hasThumbnail.setThumbDownloading(false);
                return true;
            }

            ByteDownload byteDownload = new ByteDownload(url);
            BufferedInputStream input = byteDownload.get();
            if (input != null) {
                try {
                    OutputStream output = new BufferedOutputStream(new FileOutputStream(cacheFile));

                    byte[] buffer = new byte[16384]; // Adjust if you want
                    int bytesRead;
                    while ((bytesRead = input.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                    output.close();
                    input.close();
                } catch (Exception e) {
                    Log.w(TAG, "Coundn't write " + cacheFile.getPath());
                }

                hasThumbnail.setThumbnail(cacheFile);
                hasThumbnail.setThumbDownloading(false);

                //write to cache


                return true;
            }
        } else {
            Log.w(TAG, "Synch has saved us from a catastrophe!");

        }
        hasThumbnail.setThumbDownloading(false);
        return false;
    }

    @Override
    protected void onPostExecute(Boolean imageDownloaded) {
        if (imageDownloaded) {
            adapter.notifyDataSetChanged();
        }
    }


}
