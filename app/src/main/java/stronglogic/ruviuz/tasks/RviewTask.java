package stronglogic.ruviuz.tasks;

import android.os.AsyncTask;
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

/**
 * Created by logicp on 11/27/16.
 * Logs into Ruviuz
 */
public class RviewTask extends AsyncTask<String[], String, boolean[]> {

    private static final String TAG = "RuviuzRVIEWTASK";

    private String authToken, baseUrl;

    private JSONObject dataJson;

    private AsyncResponse delegate;

    public RviewTask(String authToken, String baseUrl, AsyncResponse delegate) {
        this.authToken = authToken;
        this.baseUrl = baseUrl;
        this.delegate = delegate;
    }

    public interface AsyncResponse {
        void processFinish(String output);
    }

    @Override
    protected boolean[] doInBackground(String[]... params) {
        boolean mBool[] = new boolean[1];
        mBool[0] = getRoofs(authToken);

        return mBool;
    }

    @Override
    protected void onPostExecute(boolean[] result) {
        if (!result[0]) {
            delegate.processFinish("Login Failed");
        } else {
            super.onPostExecute(result);
            delegate.processFinish(this.dataJson.toString());
        }
    }

    //curl -i -X POSontent-Type: application/json" -d '{"email":"jiggamortis","password":"calcutta"}' http://127.0.0.1:5000/login


    private boolean getRoofs(String authToken) {
        baseUrl = baseUrl == null ? MainActivity.baseUrl : baseUrl;
        String loginUrl = baseUrl + "/roofs/all";
        HttpURLConnection connection;
        BufferedReader reader;

        try {
            URL url = new URL(loginUrl);
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

            if (bildr.length() > 0) {
                try {
                    dataJson = new JSONObject(bildr.toString());
                    return true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
