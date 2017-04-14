package stronglogic.ruviuz.util;

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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;

import stronglogic.ruviuz.MainActivity;

/**
 * Created by logicp on 4/12/17.
 */

public class RuuvHttpClient {

    public static final String TAG = "RuuvHttpClient";
    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom rnd = new SecureRandom();

    Handler mHandler;

    private String authToken, reqMethod, contentType, destination, returnKey;
    private JSONObject payload;
    private boolean getCodes, getResponse;

    public RuuvHttpClient(Handler mHandler, String authToken, String reqMethod) {
        this.mHandler = mHandler;
        this.authToken = authToken;
        this.reqMethod = reqMethod;
    }

    public RuuvHttpClient(JSONObject payload, String returnKey, String destination, String contentType, String reqMethod, String authToken, Handler mHandler, boolean getResponse, boolean getCodes) {
        this.getResponse = getResponse;
        this.getCodes = getCodes;
        this.payload = payload;
        this.returnKey = returnKey;
        this.destination = destination;
        this.contentType = contentType;
        this.reqMethod = reqMethod;
        this.authToken = authToken;
        this.mHandler = mHandler;
    }

    public boolean performRequest() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String response = null;

        try {

            URL url = new URL(MainActivity.baseUrl + destination);
            String mAuth = authToken + randomString(5);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(reqMethod);
            connection.setRequestProperty("Content-Type", contentType);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization", "Basic " + Base64.encodeToString(mAuth.trim().getBytes(), Base64.NO_WRAP));
            connection.connect();

            Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            writer.write(payload.toString());
            writer.close();

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
                response = respJson.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (!response.trim().isEmpty()) {
                Bundle msgData = new Bundle();
                msgData.putString(returnKey, String.valueOf(response));
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

    private String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append( AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public void setGetResponse(boolean getResponse) {
        this.getResponse = getResponse;
    }

    public void setGetCodes(boolean getCodes) {
        this.getCodes = getCodes;
    }

    public void setPayload(JSONObject payload) {
        this.payload = payload;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setReqMethod(String reqMethod) {
        this.reqMethod = reqMethod;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setReturnKey(String returnKey) {
        this.returnKey = returnKey;
    }
}