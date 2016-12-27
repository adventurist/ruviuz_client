package stronglogic.ruviuz.util;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by logicp on 12/24/16.
 * Making a publicly accessible class for sending files
 */

public class RuuvFile implements Runnable {

    private static final String TAG = "RUVIUZRuuvFile";

    private Handler mHandler;
    private WeakReference<Activity> mReference;
    private Activity mActivity;

    private String baseUrl, authToken;

    private String fileUrl;
    private int ruvId;

    public RuuvFile(Activity mActivity, Handler mHandler, String baseUrl, String authToken, String fileUrl, int ruvId) {
        this.mReference = new WeakReference<Activity>(mActivity);
        this.mHandler = mHandler;
        this.baseUrl = baseUrl;
        this.authToken = authToken;
        this.fileUrl = fileUrl;
        this.mActivity = mActivity;
        this.ruvId = ruvId;
    }

    @Override
    public void run() {
        sendFile();
    }

    private boolean sendFile() {
        String endpoint = baseUrl + "/file/upload";
        try {
            File file = new File(fileUrl);
            FileInputStream fileInputStream = null;
            DataOutputStream outputStream = null;
            Writer writer = null;
            BufferedReader reader = null;

            HttpURLConnection connection = null;
            String response = null;
            String twoHyphens = "--";
            String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
            String lineEnd = "\r\n";
            try {
                URL url = new URL(endpoint);
                String mAuth = authToken + ":jigga";
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("User-Agent", "Ruviuz Android");
                connection.setRequestProperty("Authorization", "Basic " + Base64.encodeToString(mAuth.trim().getBytes(), Base64.NO_WRAP));
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "upload" + "\"; filename=\"" + file.getName() + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: " + HttpURLConnection.guessContentTypeFromName(file.getName()) + lineEnd);
                outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
                outputStream.writeBytes(lineEnd);

                fileInputStream = new FileInputStream(file);
                byte[] buffer = new byte[4096];
                int bytesRead = -1;

                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.writeBytes(lineEnd);

                if (ruvId > -1) {
                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"rid\"" + lineEnd);
                    outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                    outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes(String.valueOf(ruvId));
                    outputStream.writeBytes(lineEnd);
                    outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                }

                outputStream.flush();

                if (200 != connection.getResponseCode()) {
                    Log.d(TAG, connection.getResponseMessage());
                }

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder bildr = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    bildr.append(line);
                }

                response = bildr.toString();

                fileInputStream.close();
                outputStream.flush();
                outputStream.close();

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
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}