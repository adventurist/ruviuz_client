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
public class LoginTask extends AsyncTask<String[], String, String[]> {

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
        void processFinish(String[] output);
    }

    @Override
    protected String[] doInBackground(String[]... params) {
        String consequence = loginRuviuz(email, password);
        if (consequence != null) {
            String[] mString = new String[1];
            mString[0] = consequence;
            return mString;
        }
        return null;
    }

    @Override
    protected void onPostExecute(String[] result) {
        if (null == result) {
            String[] errStr = new String[2];
            errStr[0] = "Login Failed";
            delegate.processFinish(errStr);
        } else {
            super.onPostExecute(result);
            delegate.processFinish(result);
        }
    }

    //curl -i -X POSontent-Type: application/json" -d '{"email":"jiggamortis","password":"calcutta"}' http://127.0.0.1:5000/login


    public String loginRuviuz(String email, String password) {
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

            if (setData(buffer.toString())) {
                return "TRUE!!!";
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "farlse";
        } catch (IOException e) {
            e.printStackTrace();
            return "Farlses";
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
        return "Login Attempt Successful!";
    }

    public boolean setData(String result) {
        try {
            JSONObject resultJson = new JSONObject(result);
            Log.d(TAG, result);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }


}
