package stronglogic.ruviuz.util;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import stronglogic.ruviuz.MainActivity;
import stronglogic.ruviuz.tasks.LoginTask;

import static stronglogic.ruviuz.MainActivity.RUV_SESSION_FAIL;
import static stronglogic.ruviuz.MainActivity.RUV_SESSION_UPDATE;
import static stronglogic.ruviuz.MainActivity.baseUrl;

/**
 * Created by logicp on 2/6/17.
 * A class for managing sessions
 */

public class RuvSessionManager {

    private final static String TAG = "RUVSESSIONMANAGER";
    private static String token;

    private static String email;

    private static String password;

    private CountDownTimer timer;

    private Activity mActivity;
    private SessionListener mListener;
    private Handler mHandler;

    private int timeValue;


    public RuvSessionManager(Activity activity, SessionListener listener) {

        this.mActivity = activity;
        this.mListener = listener;

        this.mHandler = new Handler(mActivity.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                if (inputMessage.getData().getString("tokenResponse") != null) {
                    try {
                        JSONObject tokenJson = new JSONObject(inputMessage.getData().getString("tokenResponse"));
                        if (tokenJson.has("token")) {
                            setToken(tokenJson.getString("token"));
                            mListener.returnData(getToken(), RUV_SESSION_UPDATE);
                            RuvSessionManager.this.timer.start();
                        } else {
                            mListener.returnData(tokenJson.getString("error"), RUV_SESSION_FAIL);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }


    public interface SessionListener {
        void returnData(String token, int result);
    }

    private static void setToken(String token) { RuvSessionManager.token = token; }

    public static void setEmail(String email) { RuvSessionManager.email = email; }

    public static void setPass(String pass) { RuvSessionManager.password = pass; }

    private static String getToken() { return RuvSessionManager.token; }

    private static String getPass() { return RuvSessionManager.password; }


    public void init_login() {
        LoginTask.AsyncResponse delegate = new LoginTask.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                Log.d(TAG, output);
                try {
                    JSONObject respJson = new JSONObject(output);
                    if (respJson.has("token")) {
                        //TODO possibly loses thread scope
                        RuvSessionManager.this.startTimer();
                        mListener.returnData(respJson.getString("token"), MainActivity.RUV_SESSION_SUCCESS);
                        setToken(respJson.getString("token"));
                    } else {
                        mListener.returnData(respJson.getString("error"), RUV_SESSION_FAIL);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        LoginTask loginTask = new LoginTask(RuvSessionManager.email, RuvSessionManager.password, baseUrl, delegate);
        loginTask.execute();
    }

    public void startTimer() {
        Log.d(TAG, "SessionManager Countdown");
        if (RuvSessionManager.this.timer == null) {
            RuvSessionManager.this.timer = new CountDownTimer(timeValue < 1 ? 325000 : timeValue, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (mActivity != null && mActivity instanceof MainActivity) {
                        ((MainActivity) mActivity).setProgressBar(Math.round(millisUntilFinished/1000));
                    } else {
                        Log.d(TAG, "Lost contact with MainActivity");
                    }
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "RuvTimer FINISHED");
                    Refresher refresher = new Refresher(mHandler, getToken(), getPass());
                    Thread refreshThread = new Thread(refresher);
                    refreshThread.start();
                }
            };
            RuvSessionManager.this.timer.start();
        } else {
            Log.d(TAG, "current timer count: " + getTimerCount());
//            if (getTimerCount() < 2) RuvSessionManager.this.timer.start();
        }
    }

    public int getTimerCount() {
        return this.timeValue;
    }

    public void setTimeLeft(int time) {
        Log.d(TAG, "setTimeLeft: " + String.valueOf(time));
        this.timeValue = time > this.timeValue || this.timeValue > 0 ? this.timeValue : time;
    }

    private static class Refresher implements Runnable {

        private String oldToken, password;
        private Handler mHandler;

        private Refresher(Handler handler, String oldToken, String pass) {
            this.mHandler = handler;
            this.oldToken = oldToken;
            this.password = pass;
        }

        @Override
        public void run() {
            refreshToken();
        }

        private boolean refreshToken() {
            String endpoint = baseUrl + "/token";

            HttpURLConnection connection;
            BufferedReader reader;
            String response = "";

            String mAuth = oldToken + ":jigga";
            try {
                URL url = new URL(endpoint);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Authorization", "Basic " + Base64.encodeToString(mAuth.trim().getBytes(), Base64.NO_WRAP));
                connection.connect();
                String respCode = String.valueOf(connection.getResponseCode());
                Log.d(TAG, respCode);
                if (respCode.substring(0, 1).equals("4") || respCode.substring(0, 1).equals("5")) {
                    InputStream error =  connection.getErrorStream();
                    Log.d(TAG, error.toString());
                    Bundle msgData = new Bundle();
                    msgData.putString("tokenResponse", "Error:\n" + error.toString());
                    Message outgoingMsg = new Message();
                    outgoingMsg.setData(msgData);
                    mHandler.sendMessage(outgoingMsg);
                } else {
                    InputStream stream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder bildr = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        bildr.append(line);
                    }
                    response = bildr.toString();

                    if (!response.trim().isEmpty()) {
                        Bundle msgData = new Bundle();
                        msgData.putString("tokenResponse", String.valueOf(response));
                        Message outgoingMsg = new Message();
                        outgoingMsg.setData(msgData);
                        mHandler.sendMessage(outgoingMsg);
                    }
                    return true;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return false;
        }
    }
}
