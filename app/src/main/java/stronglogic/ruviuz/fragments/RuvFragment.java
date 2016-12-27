package stronglogic.ruviuz.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import stronglogic.ruviuz.R;
import stronglogic.ruviuz.content.RuvFileInfo;
import stronglogic.ruviuz.util.RuuvFile;

import static android.app.Activity.RESULT_OK;

/**
 * Created by logicp on 12/6/16.
 * Anoop's Birthday
 */
public class RuvFragment extends DialogFragment {

    final private static String TAG = "RuviuzRUVFRAGMENT";
    final private static int RESULT_LOAD_IMAGE = 17;

    private RuvFragListener ruvFragListener;

    private TextView idTv;
    private EditText addressEt, priceEt, widthEt, lengthEt, slopeEt;
    private ImageView ruvPhoto1, ruvPhoto2, ruvPhoto3;
    private Button imgBtn, updateBtn;

    private ArrayList<RuvFileInfo> ruvFiles;

    private int ruvId, position;

    private String baseUrl, authToken;

    private Handler mHandler;

    private Bundle mBundle;

    private Activity mActivity;

    private RuvFileInfo rFileInfo;

    private ArrayList<String> ruvFileUrls;

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
                            Log.d(TAG, handlerJson.toString());

                            if (handlerJson.has("File")) {
                                Log.d(TAG, handlerJson.getString("File"));
                                Toast.makeText(mActivity, "Created File:: " + handlerJson.getString("File"), Toast.LENGTH_SHORT).show();
                            }

                            if (handlerJson.has("Update")) {
                                handlerJson.put("position", position);

                                ruvFragListener.ruvFragInteraction("Update", handlerJson.toString());

                                if (handlerJson.getString("Update").equals("Success")) {
                                    dismiss();
                                }

                                if (!handlerJson.getString("FilesNotFound").isEmpty()) {
                                    JSONArray filesNotFoundJsonArr = new JSONArray((handlerJson.getString("FilesNotFound")));

                                    for (int i = 0; i < filesNotFoundJsonArr.length(); i++) {

                                        JSONObject fileToSendJson = (filesNotFoundJsonArr.getJSONObject(i));
                                        String filePath = ruvFiles.get(Integer.valueOf(fileToSendJson.getString("num"))).getFilePath();
                                        RuuvFile ruuvFile = new RuuvFile(mActivity, mHandler, baseUrl, authToken, filePath, ruvId);
                                        Thread sendFileThread = new Thread(ruuvFile);
                                        sendFileThread.start();
                                    }
                                }
                            }

                            if (handlerJson.has("RuvGet")) {

                                ruvFragListener.ruvFragInteraction("GetRoof", handlerJson.getString("Roof"));
                                JSONObject roofJson = new JSONObject(handlerJson.getString("Roof"));
                                JSONArray roofFiles = new JSONArray(handlerJson.getString("Files"));

                                if (roofFiles.length() > 0)  {
                                    Glide.with(mActivity)
                                            .load(baseUrl + "/files/" + roofFiles.getJSONObject(0).getString("file"))
                                            .fitCenter()
                                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                            .into(ruvPhoto1);
                                    if (roofFiles.length() > 1 && roofFiles.get(1) != null) {
                                        Glide.with(mActivity)
                                                .load(baseUrl + "/files/" + roofFiles.getJSONObject(1).getString("file"))
                                                .fitCenter()
                                                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                                .into(ruvPhoto2);
                                    } else {
//                                        Glide.clear(ruvPhoto2);
                                    }
                                    if (roofFiles.length() > 2 && roofFiles.get(2) != null) {
                                        Glide.with(mActivity)
                                                .load(baseUrl + "/files/" + roofFiles.getJSONObject(2).getString("file"))
                                                .fitCenter()
                                                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                                .into(ruvPhoto3);
                                    } else {
//                                        Glide.clear(ruvPhoto3);
                                    }
                                } else {
//                                    Glide.clear(ruvPhoto1);
//                                    Glide.clear(ruvPhoto2);
//                                    Glide.clear(ruvPhoto3);
                                }
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
                                    String ruvPrice = "$" + roofJson.getString("price");
                                    priceEt.setText(ruvPrice);
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

                if (inputMessage.getData().getString("RuuvResponse") != null) {
                    Log.d(TAG, inputMessage.getData().getString("RuuvResponse"));
                    try {
                        JSONObject returnedJson = new JSONObject(inputMessage.getData().getString("RuuvResponse"));

                        if (returnedJson.has("File")) {
                            Toast.makeText(mActivity, "Created File:: " + returnedJson.getString("File"), Toast.LENGTH_SHORT).show();
                        }

                        ruvFragListener.ruvFragInteraction("FileUpdate", returnedJson.toString());


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
        imgBtn = (Button) mView.findViewById(R.id.imgBtn);
        updateBtn = (Button) mView.findViewById(R.id.ruvUpdate);

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), RESULT_LOAD_IMAGE);
            }
        });
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
                this.mActivity = activity;
                ruvFragListener = (RuvFragListener) activity;
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
        mBundle.putString("price", priceEt.getText().toString().substring(1));
        mBundle.putFloat("width", Float.valueOf((widthEt.getText().toString())));
        mBundle.putFloat("length", Float.valueOf(lengthEt.getText().toString()));
        mBundle.putFloat("slope", Float.valueOf(slopeEt.getText().toString()));
        mBundle.putInt("ruvId", ruvId);
        mBundle.putInt("position", position);

        if (ruvFiles != null && ruvFiles.size() > 0) {
            ArrayList<String> rFilesArray = new ArrayList<>();
            for (int i = 0; i < ruvFiles.size(); i++) {
                rFilesArray.add(ruvFiles.get(i).getFilename());
            }
            mBundle.putStringArrayList("ruvFiles", rFilesArray);
        }
        RuvUpThread ruvUpThread = new RuvUpThread(this, mHandler, baseUrl, authToken, mBundle);
        Thread updateThread = new Thread(ruvUpThread);
        updateThread.start();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_LOAD_IMAGE:
                if (resultCode == RESULT_OK && data != null) {
                    if (ruvFiles == null) {
                        ruvFiles = new ArrayList<>();
                    }
                    processImage(data, ruvFiles.size());
                }
                break;
        }
    }


    public interface RuvFragListener {
        void ruvFragInteraction(String key, String data);
    }

    public void processImage(Intent data, int fileCount) {
        if (fileCount < 3) {
            Uri rawUri = data.getData();
            String imageUri = null;
            String wholeID = DocumentsContract.getDocumentId(rawUri);

            String id = wholeID.split(":")[1];

            String[] column = { MediaStore.Images.Media.DATA };

            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = mActivity.getContentResolver().
                    query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{ id }, null);

            String filePath = "";
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndex(column[0]);

                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                imageUri = filePath;
                cursor.close();
            }
            if (imageUri != null) {
                File file = new File(imageUri);
                String filepath = file.getPath();
                rFileInfo = new RuvFileInfo();
                rFileInfo.setFilePath(file.getPath());
                rFileInfo.setFilename(filepath.substring(filepath.lastIndexOf('/') + 1));
                rFileInfo.setNum(fileCount);
                Uri imgUri = Uri.parse(imageUri);
                ruvFiles.add(rFileInfo);
                if (fileCount == 0) {
                    ruvPhoto1.setImageURI(imgUri);
                }
                if (fileCount == 1) {
                    ruvPhoto2.setImageURI(imgUri);
                }
                if (fileCount == 2) {
                    ruvPhoto3.setImageURI(imgUri);
                }
            } else {
                Toast.makeText(mActivity, "Unable to get content URI", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mActivity, "You have already chosen 3 images", Toast.LENGTH_SHORT).show();
        }
    }

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

                if (mBundle.getStringArrayList("ruvFiles") != null) {
                    JSONArray ruuvFileJsonArr = new JSONArray();
                    ArrayList<String> ruvFiles = mBundle.getStringArrayList("ruvFiles");

                    if (ruvFiles != null) {
                        for (int i = 0; i < ruvFiles.size(); i++) {
                            JSONObject ruuvFileJson = new JSONObject().put("file", ruvFiles.get(i))
                                                                        .put("num", String.valueOf(i));
                            ruuvFileJsonArr.put(ruuvFileJson);
                        }
                    }
                    ruuvJson.put("files", ruuvFileJsonArr);
                    Log.d(TAG, "RUUVJSON==::>>" + ruuvJson.toString());

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