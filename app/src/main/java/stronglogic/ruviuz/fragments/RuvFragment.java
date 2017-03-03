package stronglogic.ruviuz.fragments;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

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

import stronglogic.ruviuz.CameraActivity;
import stronglogic.ruviuz.MainActivity;
import stronglogic.ruviuz.R;
import stronglogic.ruviuz.RviewActivity;
import stronglogic.ruviuz.content.RuvFileInfo;
import stronglogic.ruviuz.content.Section;
import stronglogic.ruviuz.util.RuuvFile;
import stronglogic.ruviuz.views.SectionAdapter;

import static android.app.Activity.RESULT_OK;

/**
 * Created by logicp on 12/6/16.
 * Anoop's Birthday
 */
public class RuvFragment extends DialogFragment {

    final private static String TAG = "RuviuzRUVFRAGMENT";
    private static final int RUVIUZ_CAMERA = 15;
    final private static int RESULT_LOAD_IMAGE = 17;
    private static final int CAMERA_PERMISSION = 6;

    private String[] fileUrls, fileComments;

    private RuvFragListener ruvFragListener;

    private TextView idTv;
    private EditText addressEt, priceEt, custEt;
    private ImageView ruvPhoto1, ruvPhoto2, ruvPhoto3;
    private Button imgBtn, updateBtn, photoBtn;

    private ArrayList<RuvFileInfo> ruvFiles;

    private int ruvId, position, fileCount;

    private String baseUrl, authToken;

    private Handler mHandler;

    private Bundle mBundle;

    private RviewActivity mActivity;

    private RuvFileInfo rFileInfo;

    public RecyclerView rv;
    private SectionAdapter secAdapter;
    private ArrayList<Section> sectionList;

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

        this.fileUrls = new String[3];
        this.fileComments = new String[3];

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
                                    JSONObject fJson = new JSONObject(roofFiles.getJSONObject(0).getString("file"));
                                    Glide.with(mActivity)
                                            .load(MainActivity.baseUrl + "/files/" + fJson.getString("filename"))
                                            .fitCenter()
                                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                            .into(ruvPhoto1);
                                    RuvFragment.this.fileUrls[0] = fJson.getString("filename");
                                    if (roofFiles.length() > 1 && roofFiles.get(1) != null) {
                                        fJson = new JSONObject(roofFiles.getJSONObject(1).getString("file"));
                                        Glide.with(mActivity)
                                                .load(MainActivity.baseUrl + "/files/" + fJson.getString("filename"))
                                                .fitCenter()
                                                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                                .into(ruvPhoto2);
                                        RuvFragment.this.fileUrls[1] = fJson.getString("filename");
                                    } else {
//                                        Glide.clear(ruvPhoto2);
                                    }
                                    if (roofFiles.length() > 2 && roofFiles.get(2) != null) {
                                        fJson = new JSONObject(roofFiles.getJSONObject(2).getString("file"));
                                        Glide.with(mActivity)
                                                .load(MainActivity.baseUrl + "/files/" + fJson.getString("filename"))
                                                .fitCenter()
                                                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                                .into(ruvPhoto3);
                                        RuvFragment.this.fileUrls[2] = fJson.getString("filename");
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
                                if (handlerJson.has("Address") && addressEt != null) {
                                    JSONArray address = new JSONArray(handlerJson.getString("Address"));
                                    if (address.length() > 1) { Log.d(TAG, "Multiple Addresses to be handled"); }
                                        //TODO Multiple addresses not handled
                                    JSONObject addressJson = new JSONObject(address.getJSONObject(0).getString("address"));
                                    String addrString = addressJson.getString("address") + "\n" + addressJson.getString("city") + ", " + addressJson.getString("region") + "\n" + addressJson.getString("postal");
                                    addressEt.setText(addrString);
                                }
                                if (priceEt != null) {
                                    String ruvPrice = "$" + roofJson.getString("price");
                                    priceEt.setText(ruvPrice);
                                }
                                if (handlerJson.has("Customers") && custEt != null) {
                                    JSONArray custArray = new JSONArray(handlerJson.getString("Customers"));
                                    if (custArray.length() > 1) { Log.d(TAG, "Multiple Customers to be handled"); }
                                    //TODO Multiple customers not handled
                                    JSONObject custJson = new JSONObject(custArray.getJSONObject(0).getString("customer"));
                                    String custString = custJson.getString("first") + " " + custJson.getString("last") + "\n" + custJson.getString("email") + "\n" + custJson.getString("phone");
                                    custEt.setText(custString);
                                }
                                if (handlerJson.has("Sections")) {
                                    JSONArray sections = new JSONArray(handlerJson.getString("Sections"));
                                    int secNum = sections.length();
                                    if (RuvFragment.this.sectionList == null) RuvFragment.this.sectionList = new ArrayList<Section>();
                                    for (int sNum = 0; sNum < secNum; sNum++) {
                                        JSONObject sectionObject = new JSONObject(sections.getJSONObject(sNum).getString("section"));
                                        String sectionType = sections.getJSONObject(sNum).getString("type");
//                        }
                                        Section section = new Section();
                                        if (sectionType != null) {
                                            switch (sectionType) {
                                                case Section.SectionType.MANSARD:
                                                    section.setSectionType(Section.SectionType.MANSARD);
                                                    break;
                                                case Section.SectionType.HIP_RECTANGLE:
                                                    section.setSectionType(Section.SectionType.HIP_RECTANGLE);
                                                    break;
                                                case Section.SectionType.HIP_SQUARE:
                                                    section.setSectionType(Section.SectionType.HIP_SQUARE);
                                                    break;
                                                case Section.SectionType.GABLE:
                                                    section.setSectionType(Section.SectionType.GABLE);
                                                    break;
                                            }
//                            else if (sectionObject.getString("type").equals(Section.SectionType.LEAN-TO-ROOF))
                                        }
                                        section.setSlope(Float.valueOf(sectionObject.getString("slope")));
                                        section.setLength(Float.valueOf(sectionObject.getString("length")));
                                        section.setWidth(Float.valueOf(sectionObject.getString("width")));
                                        section.setTopWidth(Float.valueOf(sectionObject.getString("twidth")));
                                        if (!Boolean.valueOf(sectionObject.getString("full"))) {
                                            section.toggleFull();
                                            JSONObject emptyJson = new JSONObject(sections.getJSONObject(sNum).getString("empty"));
                                                section.setMissing(Float.valueOf(emptyJson.getString("area")));
                                                if (emptyJson.getString("name").equals(Section.EmptyType.CHIMNEY)) {
                                                    section.setEmptyType(Section.EmptyType.CHIMNEY);
                                                } else if (emptyJson.getString("name").equals(Section.EmptyType.SKY_LIGHT)) {
                                                    section.setEmptyType(Section.EmptyType.SKY_LIGHT);
                                                } else if (emptyJson.getString("name").equals(Section.EmptyType.OTHER)) {
                                                    section.setEmptyType(Section.EmptyType.OTHER);
                                                }
                                        }
                                        sectionList.add(section);
                                    }
                                    if (rv != null) {
                                        setupRecycler(sectionList, rv);
                                    }
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
    public void onStart() {
        if (getDialog().getWindow() != null) getDialog().getWindow().setWindowAnimations(
                    R.style.ruvanimate);
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.ruvfragment, parent, false);

        idTv = (TextView) mView.findViewById(R.id.idTv);
        priceEt = (EditText) mView.findViewById(R.id.priceEt);
        addressEt = (EditText) mView.findViewById(R.id.addressEt);
        custEt = (EditText) mView.findViewById(R.id.custEt);
        rv = (RecyclerView) mView.findViewById(R.id.sectionView);
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

        if (checkCameraHardware(mActivity)) {
            photoBtn = (Button) mView.findViewById(R.id.takePhoto);
            photoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(mActivity,
                                new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
                    } else {
                        if (checkCameraHardware(mActivity)) {
                            Intent intent = new Intent(mActivity, CameraActivity.class);
                            putIntentData(intent);
                            intent.putExtra("currentRid", ruvId);
                            intent.putExtra("callingClass", RuvFragment.this.getClass().getSimpleName());
//                            intent.putExtra("baseUrl", baseUrl);
//                            putPrefsData();
//                            setResult(RUVIUZ_DATA_PERSIST);
//                            startActivityForResult(intent, RUVIUZ_DATA_PERSIST);
                            startActivity(intent);
                        } else {
                            Log.d(TAG, "No Camera Hardware on Device");
                        }
                    }
                }
            });
        } else {
            Log.d(TAG, "Camera Not Available");
        }

        ruvPhoto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle mBundle = new Bundle();
            }
        });


        ruvPhoto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Photo 1 Click");
                Bundle mBundle = new Bundle();
                mBundle.putString("editImgUrl", fileUrls[0]);
                mBundle.putString("editCommentText", fileComments[0]);
                mBundle.putInt("editIndex", 0);
                mActivity.editImgDialog(mBundle);
            }
        });
        ruvPhoto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Photo 2 Click");
                Bundle mBundle = new Bundle();
                mBundle.putString("editImgUrl", fileUrls[1]);
                mBundle.putString("editCommentText", fileComments[1]);
                mBundle.putInt("editIndex", 1);
                mActivity.editImgDialog(mBundle);

            }
        });
        ruvPhoto3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Photo 3 Click");
                Bundle mBundle = new Bundle();
                mBundle.putString("editImgUrl", fileUrls[2]);
                mBundle.putString("editCommentText", fileComments[2]);
                mBundle.putInt("editIndex", 2);
                mActivity.editImgDialog(mBundle);
            }
        });

        return mView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if (context instanceof RviewActivity) {
                this.mActivity = (RviewActivity) context;
            }
            if (context != null) {
                ruvFragListener = (RuvFragListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement RuvFragListener");
        }
    }

    public void setupRecycler(ArrayList<Section> sectionList, RecyclerView rv) {
        if (sectionList != null && sectionList.size() > -1) {
            this.secAdapter = new SectionAdapter(mActivity, sectionList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
            layoutManager.setAutoMeasureEnabled(true);
            layoutManager.setRecycleChildrenOnDetach(true);
            rv.setLayoutManager(layoutManager);
            rv.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mActivity).build());
            rv.setAdapter(secAdapter);
            rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
//                    scrollChange(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                    scrolled(recyclerView, dx, dy);
                }

            });


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
//        mBundle.putFloat("width", Float.valueOf((widthEt.getText().toString())));
//        mBundle.putFloat("length", Float.valueOf(lengthEt.getText().toString()));
//        mBundle.putFloat("slope", Float.valueOf(slopeEt.getText().toString()));
//        mBundle.putInt("ruvId", ruvId);
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
//                rFileInfo.setNum(fileCount);
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
            String endpoint = MainActivity.baseUrl + "/roofs/" + String.valueOf(ruvId);

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


    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(mActivity, CameraActivity.class);
//                    putPrefsData();
                    Log.d(TAG, this.getClass().toString());
                    Log.d(TAG, this.getClass().getName());
                    Log.d(TAG, this.getClass().getSimpleName());
                    intent.putExtra("authToken", authToken);
                    intent.putExtra("callingClass", RuvFragment.this.getClass().getSimpleName());
//                    putIntentData(intent);
//                    setResult(RUVIUZ_DATA_PERSIST);
//                    startActivityForResult(intent, RUVIUZ_DATA_PERSIST);
                    startActivity(intent);
                } else {
                    Toast.makeText(mActivity, "Please grant camera permission to use the CAMERA", Toast.LENGTH_SHORT).show();
                }
                return;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public void putIntentData(Intent intent) {
        intent.putExtra("authToken", this.authToken);
        intent.putExtra("baseUrl", this.baseUrl);
        intent.putExtra("fileCount", this.fileCount);
        intent.putExtra("fileUrls", this.fileUrls);
    }

    public void getIntentData(Intent intent) {
        this.authToken = intent.getStringExtra("authToken");
        this.baseUrl = intent.getStringExtra("baseUrl");
        this.fileCount = intent.getIntExtra("fileCount", 0);
        this.fileUrls = intent.getStringArrayExtra("fileUrls");
    }

}