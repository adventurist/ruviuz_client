package stronglogic.ruviuz.util;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

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

import stronglogic.ruviuz.tasks.LoginTask;

import static stronglogic.ruviuz.MainActivity.baseUrl;

/**
 * Created by logicp on 2/6/17.
 * A class for managing sessions
 */

class RuvSessionManager {

    private final static String TAG = "RUVSESSIONMANAGER";
    private static String token;

    private static String email;

    private static String password;

    private static CountDownTimer timer;

    private Activity mActivity;
    private SessionListener mListener;


    public static void setToken(String token) { RuvSessionManager.token = token; }

    public static void setEmail(String email) { RuvSessionManager.email = email; }

    public static void setPass(String pass) { RuvSessionManager.password = pass; }

    public static String getToken() { return RuvSessionManager.token; }

    private RuvSessionManager(Activity activity, SessionListener listener) {

        this.mActivity = activity;
        this.mListener = listener;

        if (RuvSessionManager.timer == null) {
            RuvSessionManager.timer = new CountDownTimer(575000, 600) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    Refresher refresher = new Refresher(new Handler(mActivity.getMainLooper()), getToken());
                    Thread refreshThread = new Thread(refresher);
                    refreshThread.start();
                }
            };
        }
    }


    interface SessionListener {
        void returnData(String token);
    }


    public void init_login() {
        LoginTask loginTask = new LoginTask(RuvSessionManager.email, RuvSessionManager.password, baseUrl, new LoginTask.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                Log.d(TAG, output);
                mListener.returnData("STILL NEED TO PARSE DATA IN SESSION MANAGER");
                setToken(output);
            }
        });
        loginTask.execute();
    }

    private static class Refresher implements Runnable {

        private String oldToken;
        private Handler mHandler;

        private Refresher(Handler handler, String oldToken) {
            this.mHandler = handler;
            this.oldToken = oldToken;
        }

        @Override
        public void run() {
            refreshToken();
        }

        private boolean refreshToken() {
            String endpoint = baseUrl + "/token";
            JSONObject ruuvJson = new JSONObject();

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String response = null;

            try {
                URL url = new URL(endpoint);
                String mAuth = this.oldToken + ":jigga";
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Authorization", "Basic " + Base64.encodeToString(mAuth.trim().getBytes(), Base64.NO_WRAP));
                connection.connect();

                Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                writer.write(ruuvJson.toString());
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
                    msgData.putString("tokenResponse", String.valueOf(response));
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
}
