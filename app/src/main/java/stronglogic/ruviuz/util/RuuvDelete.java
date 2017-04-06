package stronglogic.ruviuz.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import stronglogic.ruviuz.MainActivity;
import stronglogic.ruviuz.fragments.RuvFragment;

/**
 * Created by logicp on 3/26/17.
 * Thread to Delete Ruviuz Roof quotes
 */

public class RuuvDelete implements Runnable {

    public static final String TAG = "RuuvDELETE";

    private Handler mHandler;
    private WeakReference<RuvFragment> mReference;
    private RuvFragment rFrag;

    private int ruvId;

    private final String authToken;


    public RuuvDelete(RuvFragment rFrag, String authToken, int ruvId, Handler handler) {
        this.mReference = new WeakReference<RuvFragment>(rFrag);
        this.rFrag = rFrag;
        this.authToken = authToken;
        this.ruvId = ruvId;
        this.mHandler = handler;
    }


    @Override
    public void run() {
        deleteRuv();
    }

    private boolean deleteRuv() {
        final int ruvId = this.ruvId;
        String endpoint = MainActivity.baseUrl + "/roofs/delete/" + String.valueOf(ruvId);

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String response = null;

        try {
            URL url = new URL(endpoint);
            String mAuth = authToken + ":jigga";
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization", "Basic " + Base64.encodeToString(mAuth.trim().getBytes(), Base64.NO_WRAP));
            connection.connect();

            Log.d(TAG, String.valueOf(connection.getResponseCode()));
            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder bildr = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                bildr.append(line);
            }

            try {
                JSONObject respJson = new JSONObject(bildr.toString());
                respJson.put("RuvDelete", "RuvDelete");
                response = respJson.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }



            if (response != null && !response.trim().isEmpty()) {
                Bundle msgData = new Bundle();
                msgData.putString("RuuvDeleteMsg", String.valueOf(response));
                Message outgoingMsg = new Message();
                outgoingMsg.setData(msgData);
                mHandler.sendMessage(outgoingMsg);
            }
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}