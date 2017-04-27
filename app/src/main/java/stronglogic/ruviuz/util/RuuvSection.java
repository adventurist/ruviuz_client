package stronglogic.ruviuz.util;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by logicp on 12/24/16.
 * Making a publicly accessible class for sending files
 */

public class RuuvSection implements Runnable {

    private static final String TAG = "RUVIUZRuuvSection";

    private Handler mHandler;
    private WeakReference<Activity> mReference;
    private Activity mActivity;

    private Bundle mBundle;

    private String baseUrl, authToken;

    private sectionListener mListener;


    public interface sectionListener {
        void sectionThreadComplete(int result);
    }


    public RuuvSection(Activity mActivity, Handler mHandler, String baseUrl, String authToken, Bundle bundle, sectionListener listener) {
        this.mReference = new WeakReference<Activity>(mActivity);
        this.mHandler = mHandler;
        this.baseUrl = baseUrl;
        this.authToken = authToken;
        this.mActivity = mActivity;
        this.mBundle = bundle;
        this.mListener = listener;
    }

    @Override
    public void run() {
        sendSection();
    }

    private boolean sendSection() {
        String endpoint = baseUrl + "/section/add";
        JSONObject sectionJson = new JSONObject();

        if (mBundle != null) {

            try {
                sectionJson.put("rid", mBundle.getInt("ruvId"));
                sectionJson.put("type", mBundle.getString("type"));
                sectionJson.put("length", mBundle.getFloat("length") > 0.0f ? mBundle.getFloat("length") : 0);
                sectionJson.put("width", mBundle.getFloat("width") > 0.0f ? mBundle.getFloat("width") : 0);
                sectionJson.put("topwidth", mBundle.getFloat("topwidth") > 0.0f ? mBundle.getFloat("topwidth") : 0);
                sectionJson.put("slope", mBundle.getFloat("slope") > 0.0f ? mBundle.getFloat("slope") : 0);
                sectionJson.put("empty", mBundle.getFloat("missing") > 0.0f ? mBundle.getFloat("missing") : 0);
                sectionJson.put("full", mBundle.getBoolean("full") ? 1 : 0);

                if (!mBundle.getBoolean("full")) {
                    sectionJson.put("etype", mBundle.getString("etype"));
                    sectionJson.put("elength", String.valueOf(mBundle.getFloat("elength")));
                    sectionJson.put("ewidth", String.valueOf(mBundle.getFloat("ewidth")));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
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

                Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                writer.write(sectionJson.toString());
                writer.close();
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
                    msgData.putString("RuuvResponse", String.valueOf(response));
                    Message outgoingMsg = new Message();
                    outgoingMsg.setData(msgData);
                    mHandler.sendMessage(outgoingMsg);
                }
                if (respCode.equals("201")) {
                    mListener.sectionThreadComplete(mBundle.getInt("id"));
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