package stronglogic.ruviuz.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

import stronglogic.ruviuz.R;

/**
 * Created by logicp on 12/6/16.
 * Anoop's Birthday
 */
public class RuvFragment extends DialogFragment {

    final private static String TAG = "RuviuzRUVFRAGMENT";

    private RuvFragListener ruvFragListener;

    private TextView idTv;
    private EditText addressEt, priceEt, widthEt, lengthEt, slopeEt;
    private ImageView ruvPhoto1, ruvPhoto2, ruvPhoto3;
    private Button updateBtn;

    private int ruvId, position;

    private String baseUrl, authToken;

    private Handler mHandler;

    private Bundle mBundle;


    public static RuvFragment newInstance(String param1, String param2) {
        RuvFragment fragment = new RuvFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    public RuvFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.baseUrl = getArguments().getString("baseUrl");
        this.authToken = getArguments().getString("authToken");
        this.ruvId = getArguments().getInt("ruvId");
        this.position = getArguments().getInt("position");

        this.mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                if (inputMessage.getData().getString("RuuvUpdateMsg") != null) {
                    try {
                        JSONObject handlerJson = new JSONObject(inputMessage.getData().getString("RuuvUpdateMsg"));
                        try {
                            if (handlerJson.has("Update")) {
                                handlerJson.put("position", position);
                                ruvFragListener.ruvFragInteraction("Update", handlerJson.toString());
                            } else if (handlerJson.has("RuvGet")) {

                                ruvFragListener.ruvFragInteraction("GetRoof", handlerJson.getString("Roof"));
                                JSONObject roofJson = new JSONObject(handlerJson.getString("Roof"));
                                if (idTv != null) {
                                    idTv.setText(roofJson.getString("id"));
                                }
                                if (addressEt != null) {
                                    addressEt.setText(roofJson.getString("address"));
                                }
                                if (lengthEt != null) {
                                    lengthEt.setText(roofJson.getString("length"));
                                }
                                if (priceEt != null) {
                                    priceEt.setText(roofJson.getString("price"));
                                }
                                if (widthEt != null) {
                                    widthEt.setText(roofJson.getString("width"));
                                }
                                if (slopeEt != null) {
                                    slopeEt.setText(roofJson.getString("slope"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.ruvfragment, parent, false);

        idTv = (TextView) mView.findViewById(R.id.idTv);
        priceEt = (EditText) mView.findViewById(R.id.priceEt);
        addressEt = (EditText) mView.findViewById(R.id.addressEt);
        widthEt = (EditText) mView.findViewById(R.id.widthEt);
        lengthEt = (EditText) mView.findViewById((R.id.lengthEt));
        slopeEt = (EditText) mView.findViewById(R.id.slopeEt);
        ruvPhoto1 = (ImageView) mView.findViewById(R.id.ruvUpPhoto1);
        ruvPhoto2  = (ImageView) mView.findViewById(R.id.ruvUpPhoto2);
        ruvPhoto3  = (ImageView) mView.findViewById(R.id.ruvUpPhoto3);
        updateBtn = (Button) mView.findViewById(R.id.ruvUpdate);

        updateBtn.setOnClickListener(new View.OnClickListener()    {
            public void onClick(View v) {
                updateRuv();
            }
        });
        getRuv();

        return mView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            if (activity != null) {
                ruvFragListener= (RuvFragListener) activity;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement RuvFragListener");
        }
    }


    public void getRuv() {
        RuvGetThread rgThread = new RuvGetThread(this, mHandler, baseUrl, authToken, this.ruvId);
        Thread getThread = new Thread(rgThread);
        getThread.start();

    }

    public void updateRuv()    {


        mBundle = new Bundle();
        mBundle.putString("address", addressEt.getText().toString());
        mBundle.putString("price", priceEt.getText().toString());
        mBundle.putFloat("width", Float.valueOf((widthEt.getText().toString())));
        mBundle.putFloat("length", Float.valueOf(lengthEt.getText().toString()));
        mBundle.putFloat("slope", Float.valueOf(slopeEt.getText().toString()));
        mBundle.putInt("ruvId", ruvId);
        mBundle.putInt("position", position);
        RuvUpThread ruvUpThread = new RuvUpThread(this, mHandler, baseUrl, authToken, mBundle);
        Thread updateThread = new Thread(ruvUpThread);
        updateThread.start();
    }

    public interface RuvFragListener {
        void ruvFragInteraction(String key, String data);
    }


//    static class IncomingHandler extends Handler {
//        private final WeakReference<RuvFragment> rFrag;
//
//        IncomingHandler(RuvFragment rFrag) {
//            this.rFrag = new WeakReference<RuvFragment>(rFrag);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//
//            if (rFrag!= null) {
//                rFrag.handleMessage(msg);
//            }
//        }
//    }


    private static class RuvUpThread implements Runnable {

        private Handler mHandler;
        private WeakReference<RuvFragment> mReference;
        private RuvFragment rFrag;

        private String baseUrl, authToken;

        private Bundle mBundle;


        private RuvUpThread(RuvFragment rFrag, Handler mHandler, String baseUrl, String authToken, Bundle bundle) {
            this.mReference = new WeakReference<RuvFragment>(rFrag);
            this.mHandler = mHandler;
            this.baseUrl = baseUrl;
            this.authToken = authToken;
            this.rFrag = rFrag;
            this.mBundle = bundle;
        }


        @Override
        public void run() {
            updateRuv();
        }

        private boolean updateRuv() {
            final int ruvId = mBundle.getInt("ruvId");
            final int position = mBundle.getInt("position");
            String endpoint = baseUrl + "/roof/update/" + String.valueOf(ruvId);
            JSONObject ruuvJson = new JSONObject();

            try {
                ruuvJson.put("address", mBundle.getString("address"));
                ruuvJson.put("width", mBundle.getFloat("width"));
                ruuvJson.put("length", mBundle.getFloat("length"));
                ruuvJson.put("slope", mBundle.getFloat("slope"));
                ruuvJson.put("price", mBundle.getString("price"));
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
                writer.write(ruuvJson.toString());
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
//                    respJson.put("RuvGet", "RuvGet");
                    response = respJson.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (!response.trim().isEmpty()) {
                    Bundle msgData = new Bundle();
                    msgData.putString("RuuvUpdateMsg", String.valueOf(response));
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

    private static class RuvGetThread implements Runnable {

        private Handler mHandler;
        private WeakReference<RuvFragment> mReference;
        private RuvFragment rFrag;

        private String baseUrl, authToken;
        private int ruvId;

        private Bundle mBundle;


        private RuvGetThread(RuvFragment rFrag, Handler mHandler, String baseUrl, String authToken, int id) {
            this.mReference = new WeakReference<RuvFragment>(rFrag);
            this.mHandler = mHandler;
            this.baseUrl = baseUrl;
            this.authToken = authToken;
            this.rFrag = rFrag;
            this.ruvId = id;
        }


        @Override
        public void run() {
            getRuv();
        }

        private boolean getRuv() {
            final int ruvId = this.ruvId;
            String endpoint = baseUrl + "/roofs/" + String.valueOf(ruvId);

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String response = null;

            try {
                URL url = new URL(endpoint);
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

                try {
                    JSONObject respJson = new JSONObject(bildr.toString());
                    respJson.put("RuvGet", "RuvGet");
                    response = respJson.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                if (!response.trim().isEmpty()) {
                    Bundle msgData = new Bundle();
                    msgData.putString("RuuvUpdateMsg", String.valueOf(response));
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