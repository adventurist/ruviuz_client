package stronglogic.ruviuz.fragments;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import stronglogic.ruviuz.CameraActivity;
import stronglogic.ruviuz.MainActivity;
import stronglogic.ruviuz.R;
import stronglogic.ruviuz.RviewActivity;
import stronglogic.ruviuz.content.Customer;
import stronglogic.ruviuz.content.Roof;
import stronglogic.ruviuz.content.RuvFileInfo;
import stronglogic.ruviuz.content.Section;
import stronglogic.ruviuz.util.RuuvDelete;
import stronglogic.ruviuz.util.RuuvFile;
import stronglogic.ruviuz.util.SectionUpdate;
import stronglogic.ruviuz.views.SectionAdapter;

import static android.app.Activity.RESULT_OK;

/**
 * Created by logicp on 12/6/16.
 * Anoop's Birthday
 */
public class RuvFragment extends DialogFragment implements SectionEditFragment.SectionEditListener {

    private static final String TAG = "RuviuzRUVFRAGMENT";
    private static final int RUVIUZ_CAMERA = 15;
    private static final int RESULT_LOAD_IMAGE = 17;
    private static final int CAMERA_PERMISSION = 6;

    private static boolean SEC_EDIT_MODE = true;

    private String[] fileUrls, fileComments;

    private RuvFragListener ruvFragListener;

    private TextView idTv, cTimeTv1, cTimeTv2, cTimeTv3, ruvComment1, ruvComment2, ruvComment3;
    private EditText addressEt, priceEt, firstEt, lastEt, cityEt, regionEt, slopeEt, emailEt, phoneEt, postalEt;
    private ImageView ruvPhoto1, ruvPhoto2, ruvPhoto3;
    private Button imgBtn, updateBtn, photoBtn, delBtn;
    private Spinner materialSpinner;

    private RadioGroup rdyGroup, prefixGroup;
    private RadioButton reqBtn1, reqBtn2, reqBtn3, mr, ms, mrs;

    private MaterialNumberPicker flrPicker;

    private ArrayList<RuvFileInfo> ruvFiles;

    private Customer mCustomer;

    private int ruvId, position, fileCount, cleanupFactor, numFloors;

    private String baseUrl, authToken;
    private String material, prefix;

    private String[] materials;

    private Handler mHandler;

    private Bundle mBundle;

    private RviewActivity mActivity;

    private RuvFileInfo rFileInfo;

    public RecyclerView rv;
    private SectionAdapter secAdapter;

    private Roof ruv;
    private ArrayList<Section> sectionList;

    public static RuvFragment newInstance(String param1, String param2) {
        RuvFragment fragment = new RuvFragment();
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

        mActivity.setEditPosition(this.position);

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

                                    int i = 1;
                                    for (Section section : sectionList) {
                                        if (section.isChanged()) {
                                            Bundle sBundle = new Bundle();
                                            sBundle.putInt("id", i);
                                            boolean full = section.getMissing() == 0;
                                            section.setFull(full);
                                            sBundle.putInt("ruvId", ruvId);
                                            sBundle.putString("type", section.getSectionType());
                                            sBundle.putFloat("length", section.getLength());
                                            sBundle.putFloat("width", section.getWidth());
                                            sBundle.putFloat("topwidth", section.getTopWidth());
                                            sBundle.putFloat("slope", section.getSlope());
                                            sBundle.putBoolean("full", section.isFull());
                                            if (!section.isFull()) {
                                                sBundle.putFloat("missing", section.getMissing());
                                                sBundle.putString("etype", section.getEmptyType());
                                                if (section.getEmptyLength() > 0 && section.getEmptyWidth() > 0) {
                                                    sBundle.putFloat("elength", section.getEmptyLength());
                                                    sBundle.putFloat("ewidth", section.getEmptyWidth());
                                                }
                                            }
                                            SectionUpdate ruvSectionUpdater = new SectionUpdate(mHandler, "application/json", "POST", authToken);
                                            Thread sectionThread = new Thread(ruvSectionUpdater);
                                            sectionThread.start();
                                            i++;
                                        }
                                    }

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
                                ruv = new Roof();

                                ruv.setId(Integer.valueOf(roofJson.getString("id")));
                                idTv.setText(roofJson.getString("id"));

                                if (roofFiles.length() > 0)  {

                                    JSONObject fJson = new JSONObject(roofFiles.getJSONObject(0).getString("file"));
                                    RuvFileInfo rFile = new RuvFileInfo();
                                    String fileUrl = MainActivity.baseUrl + "/files/" + fJson.getString("filename");
                                    rFile.setUrl(fileUrl);
                                    rFile.setFilename(fJson.getString("filename"));
                                    Glide.with(mActivity)
                                            .load(MainActivity.baseUrl + "/files/" + fJson.getString("filename"))
                                            .fitCenter()
                                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                            .into(ruvPhoto1);
                                    RuvFragment.this.fileUrls[0] = fJson.getString("filename");

                                    if (roofFiles.getJSONObject(0).has("comment")) {

                                        JSONObject cJson = new JSONObject(roofFiles.getJSONObject(0).getString("comment"));
                                        ruvComment1.setText(cJson.getString("body"));
                                        rFile.setComment(cJson.getString("body"));
                                        cTimeTv1.setText(cJson.getString("entry_date"));
                                    }

                                    if (roofFiles.length() > 1 && roofFiles.get(1) != null) {

                                        fJson = new JSONObject(roofFiles.getJSONObject(1).getString("file"));
                                        RuvFileInfo rFile2 = new RuvFileInfo();
                                        String fileUrl2 = MainActivity.baseUrl + "/files/" + fJson.getString("filename");
                                        rFile2.setUrl(fileUrl2);
                                        rFile2.setFilename(fJson.getString("filename"));
                                        Glide.with(mActivity)
                                                .load(MainActivity.baseUrl + "/files/" + fJson.getString("filename"))
                                                .fitCenter()
                                                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                                .into(ruvPhoto2);
                                        RuvFragment.this.fileUrls[1] = fJson.getString("filename");

                                        if (roofFiles.getJSONObject(1).has("comment")) {
                                            JSONObject cJson = new JSONObject(roofFiles.getJSONObject(1).getString("comment"));
                                            rFile2.setComment(roofFiles.getJSONObject(1).getString("comment"));
                                            ruvComment2.setText(cJson.getString("body"));
                                            cTimeTv2.setText(cJson.getString("entry_date"));
                                        }

                                    } else {

                                        Glide.clear(ruvPhoto2);

                                    }

                                    if (roofFiles.length() > 2 && roofFiles.get(2) != null) {

                                        fJson = new JSONObject(roofFiles.getJSONObject(2).getString("file"));
                                        RuvFileInfo rFile3 = new RuvFileInfo();
                                        String fileUrl3 = MainActivity.baseUrl + "/files/" + fJson.getString("filename");
                                        rFile3.setUrl(fileUrl3);
                                        rFile3.setFilename(fJson.getString("filename"));
                                        Glide.with(mActivity)
                                                .load(MainActivity.baseUrl + "/files/" + fJson.getString("filename"))
                                                .fitCenter()
                                                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                                .into(ruvPhoto3);
                                        RuvFragment.this.fileUrls[2] = fJson.getString("filename");

                                        if (roofFiles.getJSONObject(2).has("comment")) {

                                            JSONObject cJson = new JSONObject(roofFiles.getJSONObject(2).getString("comment"));
                                            rFile3.setComment(roofFiles.getJSONObject(2).getString("comment"));
                                            ruvComment3.setText(cJson.getString("body"));
                                            cTimeTv3.setText(cJson.getString("entry_date"));
                                        }

                                    } else {

                                        Glide.clear(ruvPhoto3);

                                    }

                                } else {

                                    Glide.clear(ruvPhoto1);
                                    Glide.clear(ruvPhoto2);
                                    Glide.clear(ruvPhoto3);
                                }

                                if (handlerJson.has("Address") && addressEt != null && cityEt != null && regionEt != null) {

                                    JSONArray address = new JSONArray(handlerJson.getString("Address"));
                                    if (address.length() > 1) { Log.d(TAG, "Multiple Addresses to be handled"); }
                                        //TODO Multiple addresses not handled
                                    JSONObject addressJson = new JSONObject(address.getJSONObject(0).getString("address"));
                                    String addrString = addressJson.getString("address") + "\n" + addressJson.getString("city") + ", " + addressJson.getString("region") + "\n" + addressJson.getString("postal");
                                    addressEt.setText(addrString);
                                    cityEt.setText(addressJson.getString("city"));
                                    regionEt.setText(addressJson.getString("region"));
                                    postalEt.setText(addressJson.getString("postal"));

                                }

                                if (priceEt != null) {

                                    String ruvPrice = "$" + roofJson.getString("price");
                                    priceEt.setText(ruvPrice);

                                }

                                if (roofJson.has("rstate") && roofJson.has("floors") && handlerJson.has("Rooftype") && materialSpinner != null) {

                                    JSONObject rtypeJson = new JSONObject(handlerJson.getString("Rooftype"));
                                    RuvFragment.this.cleanupFactor = Integer.valueOf(roofJson.getString("rstate"));
                                    RuvFragment.this.numFloors = Integer.valueOf(roofJson.getString("floors"));
                                    RuvFragment.this.material = rtypeJson.getString("name");

                                    for (int i = 0; i < RuvFragment.this.materials.length; i++) {
                                        if (materialSpinner.getItemAtPosition(i).equals(RuvFragment.this.material)) {
                                            materialSpinner.setSelection(i);
                                            materialSpinner.jumpDrawablesToCurrentState();
                                        }
                                    }

                                    if (RuvFragment.this.cleanupFactor == 0) reqBtn1.setChecked(true);
                                    if (RuvFragment.this.cleanupFactor == 1) reqBtn2.setChecked(true);
                                    if (RuvFragment.this.cleanupFactor == 2) reqBtn3.setChecked(true);

                                    if (RuvFragment.this.flrPicker != null) {
                                        RuvFragment.this.flrPicker.setValue(RuvFragment.this.numFloors);
                                        RuvFragment.this.flrPicker.jumpDrawablesToCurrentState();
                                    }

                                }

                                if (handlerJson.has("Customers") && firstEt != null && lastEt != null &&
                                        phoneEt != null && emailEt != null) {

                                    mCustomer = new Customer();
                                    JSONArray custArray = new JSONArray(handlerJson.getString("Customers"));
                                    if (custArray.length() > 1) { Log.d(TAG, "Multiple Customers to be handled"); }
                                    //TODO Multiple customers not handled
                                    JSONObject custJson = new JSONObject(custArray.getJSONObject(0).getString("customer"));

                                    mCustomer.setPrefix(custJson.getString("prefix"));
                                    RuvFragment.this.prefix = custJson.getString("prefix");
                                    mCustomer.setFirstname(custJson.getString("first"));
                                    firstEt.setText(custJson.getString("first"));
                                    mCustomer.setLastname(custJson.getString("last"));
                                    lastEt.setText(custJson.getString("last"));
                                    mCustomer.setPhone(custJson.getString("phone"));
                                    phoneEt.setText(custJson.getString("phone"));
                                    mCustomer.setEmail(custJson.getString("email"));
                                    emailEt.setText(custJson.getString("email"));

                                    if (RuvFragment.this.prefix != null && RuvFragment.this.prefix.equals("Mr."))
                                        mr.setChecked(true);
                                    if (RuvFragment.this.prefix != null && RuvFragment.this.prefix.equals("Ms."))
                                        ms.setChecked(true);
                                    if (RuvFragment.this.prefix != null && RuvFragment.this.prefix.equals("Mrs."))
                                        mrs.setChecked(true);

                                }

                                if (handlerJson.has("Sections")) {

                                    JSONArray sections = new JSONArray(handlerJson.getString("Sections"));
                                    int secNum = sections.length();

                                    if (RuvFragment.this.sectionList == null) RuvFragment.this.sectionList = new ArrayList<Section>();

                                    for (int sNum = 0; sNum < secNum; sNum++) {

                                        JSONObject sectionObject = new JSONObject(sections.getJSONObject(sNum).getString("section"));
                                        String sectionType = sections.getJSONObject(sNum).getString("type");
                                        Section section = new Section();

                                        section.setId(Integer.valueOf(sectionObject.getString("id")));

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
                                        }

                                        section.setSlope(Float.valueOf(sectionObject.getString("slope")));
                                        slopeEt.setText(sectionObject.getString("slope"));
                                        section.setLength(Float.valueOf(sectionObject.getString("length")));
                                        section.setWidth(Float.valueOf(sectionObject.getString("width")));
                                        section.setTopWidth(Float.valueOf(sectionObject.getString("twidth")));

                                        if (!Boolean.valueOf(sectionObject.getString("full"))) {

                                            section.toggleFull();
                                            JSONObject emptyJson = new JSONObject(sections.getJSONObject(sNum).getString("empty"));
                                            section.setMissing(Float.valueOf(emptyJson.getString("area")));

                                            if (emptyJson.has("elength")) {
                                                section.setEmptyLength(Float.valueOf(emptyJson.getString("elength")));
                                            }

                                            if (emptyJson.has("ewidth")) {
                                                section.setEmptyWidth(Float.valueOf(emptyJson.getString("ewidth")));
                                            }

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

                if (inputMessage.getData().getString("RuuvDeleteMsg") != null) {

                    Log.d(TAG, inputMessage.getData().getString("RuuvDeleteMsg"));
                    String response = inputMessage.getData().getString("RuvDelete");
                    Toast.makeText(mActivity, response, Toast.LENGTH_SHORT).show();
                    ruvFragListener.ruvFragInteraction("Delete", inputMessage.getData().getString("Deleted"), position);

                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog().getWindow() != null) {

            Window w = getDialog().getWindow();
            w.setWindowAnimations(R.style.ruvanimate);

            ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
            int height = params.height;
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            w.setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {

        final View mView = inflater.inflate(R.layout.ruvfragment, parent, false);

        idTv = (TextView) mView.findViewById(R.id.idTv);
        imgBtn = (Button) mView.findViewById(R.id.imgBtn);
        delBtn = (Button) mView.findViewById(R.id.ruvDelete);
        cityEt = (EditText) mView.findViewById(R.id.cityEt);
        lastEt = (EditText) mView.findViewById(R.id.lastEt);
        slopeEt = (EditText) mView.findViewById(R.id.slopeEt);
        firstEt = (EditText) mView.findViewById(R.id.firstEt);
        priceEt = (EditText) mView.findViewById(R.id.priceEt);
        phoneEt = (EditText) mView.findViewById(R.id.phoneEt);
        emailEt = (EditText) mView.findViewById(R.id.emailEt);
        cTimeTv1 = (TextView) mView.findViewById(R.id.cmtTime1);
        cTimeTv2 = (TextView) mView.findViewById(R.id.cmtTime2);
        cTimeTv3 = (TextView) mView.findViewById(R.id.cmtTime3);
        updateBtn = (Button) mView.findViewById(R.id.ruvUpdate);
        regionEt = (EditText) mView.findViewById(R.id.regionEt);
        postalEt = (EditText) mView.findViewById(R.id.postalEt);
        rv = (RecyclerView) mView.findViewById(R.id.sectionView);
        addressEt = (EditText) mView.findViewById(R.id.addressEt);
        ruvPhoto1 = (ImageView) mView.findViewById(R.id.ruvPhoto1);
        ruvPhoto2  = (ImageView) mView.findViewById(R.id.ruvPhoto2);
        ruvPhoto3  = (ImageView) mView.findViewById(R.id.ruvPhoto3);
        ruvComment1 = (TextView) mView.findViewById(R.id.ruvComment1);
        ruvComment2 = (TextView) mView.findViewById(R.id.ruvComment2);
        ruvComment3 = (TextView) mView.findViewById(R.id.ruvComment3);
        materialSpinner = (Spinner) mView.findViewById(R.id.materialSpin);

        flrPicker = (MaterialNumberPicker) mView.findViewById(R.id.floorPicker);

        flrPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                RuvFragment.this.numFloors = newVal;
                View childView = flrPicker.getChildAt(0);
                if (childView != null) childView.setBackgroundColor(Color.RED);
            }
        });

        rdyGroup = (RadioGroup) mView.findViewById(R.id.rdyGroup);

        reqBtn1 = (RadioButton) mView.findViewById(R.id.noneBtn);
        reqBtn2 = (RadioButton) mView.findViewById(R.id.moderateBtn);
        reqBtn3 = (RadioButton) mView.findViewById(R.id.highBtn);

        rdyGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioBtn = (RadioButton) mView.findViewById(checkedId);
                RuvFragment.this.cleanupFactor = radioBtn.getText().toString().equals("None") ? 0 : radioBtn.getText().toString().equals("Moderate") ? 1 : 2;
            }
        });

        prefixGroup = (RadioGroup) mView.findViewById(R.id.clientPrefix);

        mr = (RadioButton) mView.findViewById(R.id.prefix_mr);
        ms = (RadioButton) mView.findViewById(R.id.prefix_ms);
        mrs = (RadioButton) mView.findViewById(R.id.prefix_mrs);

        prefixGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioBtn = (RadioButton) mView.findViewById(checkedId);
                RuvFragment.this.prefix = radioBtn.getText().toString();
            }
        });

        RuvFragment.this.materials = mActivity.getResources().getStringArray(R.array.roofMaterials);
        ArrayAdapter materialAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_dropdown_item, materials);
        materialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        materialSpinner.setAdapter(materialAdapter);

        materialSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RuvFragment.this.material = materialSpinner.getItemAtPosition(position).toString();
                Log.d(TAG, "Material chosen: " + RuvFragment.this.material);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRuv();
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
                mBundle.putString("callingClass", RuvFragment.this.getClass().getSimpleName());
                mBundle.putString("editImgUrl", fileUrls[0]);
                mBundle.putStringArray("fileUrls", fileUrls);
                mBundle.putStringArray("fileComments", fileComments);
                mBundle.putString("editCommentText", fileComments[0]);
                mBundle.putInt("editIndex", 0);
                mActivity.editImgDialog(mBundle);
            }
        });
        //TODO put comments in bundle and figure out a better workflow
        ruvPhoto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Photo 2 Click");
                Bundle mBundle = new Bundle();
                mBundle.putString("editImgUrl", fileUrls[1]);
                mBundle.putStringArray("fileUrls", fileUrls);
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
            this.secAdapter = new SectionAdapter(mActivity, sectionList, SEC_EDIT_MODE);
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

        if (ruv != null) {
            mBundle = new Bundle();
            mBundle.putString("address", addressEt.getText().toString());
            mBundle.putString("price", priceEt.getText().toString().substring(1));
            mBundle.putInt("position", position);

            mBundle.putString("address", addressEt.getText().toString());
            mBundle.putString("postal", postalEt.getText().toString());
            mBundle.putString("city", cityEt.getText().toString());
            mBundle.putString("region", regionEt.getText().toString());
            mBundle.putString("material", material);
            mBundle.putString("numFloors", String.valueOf(flrPicker.getValue()));
            mBundle.putString("cleanupFactor", String.valueOf(cleanupFactor));
            mBundle.putString("firstName", firstEt.getText().toString());
            mBundle.putString("lastName", lastEt.getText().toString());
            mBundle.putString("email", emailEt.getText().toString());
            mBundle.putString("phone", phoneEt.getText().toString());
            mBundle.putString("prefix", prefix);

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
        } else {
            Log.d(TAG, "updateRuv: ruv is null");
        }
    }

    public void deleteRuv() {
        RuuvDelete ruvDelThread = new RuuvDelete(this, authToken, ruvId, mHandler);
        Thread deleteThread = new Thread(ruvDelThread);
        deleteThread.start();
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

    @Override
    public void sectionEditInteraction(int data) {

    }


    public interface RuvFragListener {
        void ruvFragInteraction(String key, String data);

        void ruvFragInteraction(String key, String data, int position);
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
                ruuvJson.put("postal", mBundle.getString("postal"));
                ruuvJson.put("city", mBundle.getString("city"));
                ruuvJson.put("region", mBundle.getString("region"));
                ruuvJson.put("material", mBundle.getString("material"));
                ruuvJson.put("cleanupFactor", mBundle.getString("cleanupFactor"));
                ruuvJson.put("numFloors", mBundle.getString("numFloors"));
                ruuvJson.put("firstName", mBundle.getString("firstName"));
                ruuvJson.put("lastName", mBundle.getString("lastName"));
                ruuvJson.put("email", mBundle.getString("email"));
                ruuvJson.put("phone", mBundle.getString("phone"));
                ruuvJson.put("prefix", mBundle.getString("prefix"));

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