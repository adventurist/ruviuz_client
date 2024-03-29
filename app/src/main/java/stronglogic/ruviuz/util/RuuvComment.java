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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by logicp on 12/24/16.
 * Making a publicly accessible class for sending files
 */

public class RuuvComment implements Runnable {

    private static final String TAG = "RUVIUZRuuvComment";

    private Handler mHandler;
    private WeakReference<Activity> mReference;
    private Activity mActivity;

    private Bundle mBundle;

    private String baseUrl, authToken;


    public RuuvComment(Activity mActivity, Handler mHandler, String baseUrl, String authToken, Bundle bundle) {
        this.mReference = new WeakReference<Activity>(mActivity);
        this.mHandler = mHandler;
        this.baseUrl = baseUrl;
        this.authToken = authToken;
        this.mActivity = mActivity;
        this.mBundle = bundle;
    }

    @Override
    public void run() {
        sendComment();
    }

    private boolean sendComment() {
        String endpoint = baseUrl + "/comment/add";
        JSONObject commentJson = new JSONObject();

        if (mBundle != null) {

            try {
                final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
                final SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT, Locale.CANADA);
                final String nowTime = sdf.format(Calendar.getInstance().getTime());

                commentJson.put("comment_body", mBundle.getString("comment_body"));
                commentJson.put("ruvfid", mBundle.getString("ruvfid"));
                commentJson.put("entry_date", nowTime);
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
                writer.write(commentJson.toString());
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