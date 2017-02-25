package stronglogic.ruviuz.util;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import stronglogic.ruviuz.MainActivity;

/**
 * Created by logicp on 12/24/16.
 * Making a publicly accessible class for sending files
 */

public class RuuvPrice implements Runnable {

    private static final String TAG = "RUVIUZRuuvPrice";

    private Handler mHandler;
    private WeakReference<Activity> mReference;
    private Activity mActivity;

    private Bundle mBundle;

    private String baseUrl, authToken;


    public RuuvPrice(Activity mActivity, Handler mHandler, String authToken, Bundle bundle) {
        this.mReference = new WeakReference<Activity>(mActivity);
        this.mHandler = mHandler;
        this.authToken = authToken;
        this.mActivity = mActivity;
        this.mBundle = bundle;
    }

    @Override
    public void run() {
        updatePrice();
    }

    private boolean updatePrice() {
        if (mBundle != null) {

            int rid = mBundle.getInt("rid");
            String endpoint = MainActivity.baseUrl + "/calculate/" + String.valueOf(rid);
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String response = null;

            try {
                URL url = new URL(endpoint);
                String mAuth = authToken + ":jigga";
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Authorization", "Basic " + Base64.encodeToString(mAuth.trim().getBytes(), Base64.NO_WRAP));
                connection.connect();

//                Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
//                writer.close();
                String respCode = String.valueOf(connection.getResponseCode());
                Log.d(TAG, respCode);
                if (respCode.equals("401") || respCode.equals("404")) {
                    response = "Error";
                } else {
                    InputStream stream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder bildr = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        bildr.append(line);
                    }
                    response = bildr.toString();
                }

                if (!response.trim().isEmpty()) {
                    Bundle msgData = new Bundle();
                    msgData.putString("PriceResponse", String.valueOf(response));
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
        return false;
    }
}