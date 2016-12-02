package stronglogic.ruviuz.tasks;

import android.os.AsyncTask;
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

/**
 * Created by logicp on 11/27/16.
 * Logs into Ruviuz
 */
public class LoginTask extends AsyncTask<String[], String, boolean[]> {

    private static final String TAG = "RuviuzLOGINTASK";

    private String email, password, authToken, baseUrl;

    private AsyncResponse delegate;

    public LoginTask(String email, String password, String baseUrl, AsyncResponse delegate) {
        this.email = email;
        this.password = password;
        this.baseUrl = baseUrl;
        this.delegate = delegate;
    }

    public interface AsyncResponse {
        void processFinish(String output);
    }

    @Override
    protected boolean[] doInBackground(String[]... params) {
        boolean mBool[] = new boolean[1];
        mBool[0] = loginRuviuz(email, password);

        return mBool;
    }

    @Override
    protected void onPostExecute(boolean[] result) {
        if (!result[0]) {
            delegate.processFinish("Login Failed");
        } else {
            super.onPostExecute(result);
            delegate.processFinish(this.authToken);
        }
    }

    //curl -i -X POSontent-Type: application/json" -d '{"email":"jiggamortis","password":"calcutta"}' http://127.0.0.1:5000/login


    private boolean loginRuviuz(String email, String password) {
        String loginUrl = baseUrl + "/login";
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(loginUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            JSONObject jsonLogin = new JSONObject();

            try {
                jsonLogin.put("email", email);
                jsonLogin.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String loginCredsJson = jsonLogin.toString();

            connection.connect();

            Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));

            writer.write(loginCredsJson);
            writer.close();

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer buffer = new StringBuffer();
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            return setData(buffer.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean setData(String result) {
        try {
            JSONObject resultJson = new JSONObject(result);
            Log.d(TAG, result);
            this.authToken = resultJson.getString("authToken");
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }


}
