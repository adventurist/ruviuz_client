package stronglogic.ruviuz.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;

import stronglogic.ruviuz.CameraActivity;
import stronglogic.ruviuz.MainActivity;
import stronglogic.ruviuz.R;
import stronglogic.ruviuz.RviewActivity;
import stronglogic.ruviuz.content.Customer;
import stronglogic.ruviuz.content.RuvFileInfo;

import static android.app.Activity.RESULT_OK;
import static stronglogic.ruviuz.MainActivity.RUV_FINISH_EDIT;
import static stronglogic.ruviuz.MainActivity.RUV_IMAGE_EDIT;


/**
 * A simple {@link DialogFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditFragment.EditFragListener} interface
 * to handle interaction events.
 * Use the {@link EditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditFragment extends DialogFragment {

    private final static String TAG = "RUVIUZEditFragment";

    private static final int CAMERA_PERMISSION = 6;
    private final static int RESULT_LOAD_IMAGE = 17;


    private EditFragListener mListener;

    private MainActivity mActivity;

    private Toolbar mToolbar;

    private ImageView ruvPhoto1, ruvPhoto2, ruvPhoto3;
    private ImageButton okayBtn, clearBtn;
    private Button fileBtn, getLocBtn;
    private Spinner prefixSpinner, materialSpinner;

    private ArrayList<RuvFileInfo> ruvFiles;
    private ArrayList<String> ruvFileUrls;
    private RuvFileInfo rFileInfo;

    private int fileCount, currentRid, actionMode;
    private float width, length, slope;
    private BigDecimal price;
    private String address, postal, city, region, material, firstName, lastName, email, phone, prefix;
    private String[] fileUrls = new String[3];
    private String[] fileComments = new String[3];
    private Customer mCustomer;
    private boolean premium;

    private EditText firstEt, lastEt, emailEt, phoneEt, addressEt, cityEt, regionEt, postalEt, roofLength, roofLengthIn, roofWidth, roofWidthIn, roofSlope, priceEt, cEt1, cEt2, cEt3;


    public EditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditFragment newInstance(String param1, String param2) {
        EditFragment fragment = new EditFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        if (!((MainActivity) getActivity()).readyStatus()) {
            ((MainActivity) getActivity()).hideActivity();
            ((MainActivity) getActivity()).dismissOtherDialogs(EditFragment.this.getClass());
        }
        setStyle(DialogFragment.STYLE_NORMAL, R.style.RuvFullFrag);

        if (getArguments() != null) {
            getBundleData(getArguments());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);

            final View decorView = d.getWindow()
                    .getDecorView();

//            decorView.animate().translationY(-100)
//                    .setStartDelay(300)
//                    .setDuration(3000)
//                    .start();
            decorView.animate().rotationBy(360f)
                    .setStartDelay(50)
                    .setDuration(700)
                    .start();

        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                EditFragment.this.dismiss();
                mActivity.revealActivity();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        final View mView = inflater.inflate(R.layout.editfragment, container, false);

        mToolbar = (Toolbar) mView.findViewById(R.id.ruvFragToolbar);

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle the menu item
                return true;
            }
        });
        mToolbar.inflateMenu(R.menu.ruviuz_menu);

        if (mToolbar != null) {
            mToolbar.setNavigationIcon(R.drawable.construction);
            mToolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.ruvGreen));
            mToolbar.setElevation(8f);
            mToolbar.setTitle(getActivity().getResources().getString(R.string.app_name));
            mToolbar.setTitleTextColor(Color.BLACK);
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Log.d(TAG, item.toString());
                    switch (item.getItemId()) {

                        case R.id.actionfragTitle:
                            Log.d(TAG, "ACTIONFRAGTITLE");
                            break;
                        case R.id.roofView:
                            Intent rviewIntent = new Intent(mActivity, RviewActivity.class);
                            mActivity.putIntentData(rviewIntent);
                            mActivity.putPrefsData();
                            Log.d(TAG, "Implement this through MainActivity");
//                            rviewIntent.putExtra("baseUrl", mActivity.);
                            mActivity.startActivity(rviewIntent);
                            EditFragment.this.dismiss();
                            break;
                        case R.id.loginAction:
                            Log.d(TAG, "Login action!!");
                            mActivity.putPrefsData();
                            mActivity.loginDialog();
                            EditFragment.this.dismiss();
                            break;
                        case R.id.geoLocate:
                            if (mActivity != null) {
                                mActivity.getGeoLocation();
//                                String[] newAddress = mActivity.getAddress();
//                                cityEt.setText(newAddress[0]);
//                                provinceEt.setText(newAddress[1]);
                            }
                            break;
                        case R.id.goHome:
                            Log.d(TAG, "Going HOME");
                            mActivity.putPrefsData();
                            mActivity.welcomeDialog();
                            EditFragment.this.dismiss();
                            break;
                    }
                    return true;

                }
            });
        }

        firstEt = (EditText) mView.findViewById(R.id.firstEt);
        firstEt.setText(this.firstName);
        lastEt = (EditText) mView.findViewById(R.id.lastEt);
        lastEt.setText(this.lastName);
        emailEt = (EditText) mView.findViewById(R.id.emailEt);
        emailEt.setText(this.email);
        phoneEt = (EditText) mView.findViewById((R.id.phoneEt));
        phoneEt.setText(this.phone);
        addressEt = (EditText) mView.findViewById(R.id.addressEt);
        addressEt.setText(address);
        cityEt = (EditText) mView.findViewById(R.id.cityEt);
        cityEt.setText(city);
        regionEt = (EditText) mView.findViewById(R.id.regionEt);
        regionEt.setText(region);
        postalEt = (EditText) mView.findViewById(R.id.postalEt);
        postalEt.setText(postal);
        roofLength = (EditText) mView.findViewById(R.id.roofLength);
        String lengthText = String.valueOf(length);
        roofLength.setText(lengthText);
        roofLengthIn = (EditText) mView.findViewById(R.id.roofLengthIn);
        roofWidth = (EditText) mView.findViewById(R.id.roofWidth);
        String widthText = String.valueOf(width);
        roofWidth.setText(widthText);
        roofLengthIn = (EditText) mView.findViewById(R.id.roofLengthIn);
        roofSlope = (EditText) mView.findViewById(R.id.roofSlope);
        String slopeText = String.valueOf(slope);
        roofSlope.setText(slopeText);
        priceEt = (EditText) mView.findViewById(R.id.priceEt);
        String priceText = "$" + String.valueOf(price);
        priceEt.setText(priceText);

        prefixSpinner = (Spinner) mView.findViewById(R.id.prefixSpin);
        String[] prefixes = mActivity.getResources().getStringArray(R.array.prefixTypes);

        ArrayAdapter<String> prefixAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_dropdown_item, prefixes);

        prefixAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prefixSpinner.setAdapter(prefixAdapter);
        prefixSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                EditFragment.this.material =
                Log.d(TAG, prefixSpinner.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        materialSpinner = (Spinner) mView.findViewById(R.id.materialSpin);
        ArrayAdapter<String> materialAdapter = null;
        String[] materials = mActivity.getResources().getStringArray(R.array.roofMaterials);
        if (this.material == null) this.material = materials[0];
        int materialIndex = -1;
        for (int i = 0; i < materials.length; i++) {
            if (EditFragment.this.material.equals(materials[i])) {
                materialIndex = i;
            }
        }
        if (materialIndex == -1) {
            String[] newMaterials = new String[materials.length + 1];
            for (int j = newMaterials.length; j > 1; j--) {
                newMaterials[j - 1] = materials[j - 2];
            }
            newMaterials[0] = this.material;
            materialAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_dropdown_item, newMaterials);
            materialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            materialSpinner.setAdapter(materialAdapter);
        } else {
            materialAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_dropdown_item, materials);
            materialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            materialSpinner.setAdapter(materialAdapter);
        }

        materialSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EditFragment.this.material = materialSpinner.getItemAtPosition(position).toString();
                Log.d(TAG, "Material chosen: " + EditFragment.this.material);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fileBtn = (Button) mView.findViewById(R.id.uploadFile);

        fileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "fileBtn Click!");

                MenuBuilder menuBuilder = new MenuBuilder(mActivity);
                MenuInflater inflater = new MenuInflater(mActivity);
                inflater.inflate(R.menu.file_menu, menuBuilder);
                MenuPopupHelper uploadMenu = new MenuPopupHelper(mActivity, menuBuilder, fileBtn);
                uploadMenu.setForceShowIcon(true);

                menuBuilder.setCallback(new MenuBuilder.Callback() {
                    @Override
                    public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.takePhoto:
                                Log.d(TAG, "use Camera!!");
                                if (checkCameraHardware(mActivity)) {
                                    Intent intent = new Intent(mActivity, CameraActivity.class);
                                    mActivity.putIntentData(intent);
                                    intent.putExtra("callingClass", mActivity.getClass().getSimpleName());
                                    mActivity.putPrefsData();
                                    startActivity(intent);
                                } else {
                                    Log.d(TAG, "No Camera Hardware on Device");
                                }
                                return true;
                            case R.id.chooseFile:
                                Log.d(TAG, "Choose File!");
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent,
                                        "Select Picture"), RESULT_LOAD_IMAGE);
                                return true;
                            default:
                                return false;
                        }
                    }

                    @Override
                    public void onMenuModeChange(MenuBuilder menu) {
                    }
                });
                uploadMenu.show();
            }
        });

        ruvPhoto1 = (ImageView) mView.findViewById(R.id.ruvPic1);
        ruvPhoto2 = (ImageView) mView.findViewById(R.id.ruvPic2);
        ruvPhoto3 = (ImageView) mView.findViewById(R.id.ruvPic3);

        cEt1 = (EditText) mView.findViewById(R.id.ruvComment1);
        cEt2 = (EditText) mView.findViewById(R.id.ruvComment2);
        cEt3 = (EditText) mView.findViewById(R.id.ruvComment3);

        if (fileCount > 0) {
            if (fileUrls != null) {
                if (fileUrls[0] != null && !fileUrls[0].equals("") && ruvPhoto1.getDrawable() == null) {
                    Glide.with(mActivity)
                            .load(fileUrls[0])
                            .override(82, 68)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(ruvPhoto1);
                    if (fileComments[0] != null && !fileComments[0].equals("")) {
                        cEt1.setVisibility(View.VISIBLE);
                        cEt1.setText(fileComments[0]);
                    }
                }
                if (fileUrls[1] != null && !fileUrls[1].equals("") && ruvPhoto2.getDrawable() == null) {
                    Glide.with(mActivity)
                            .load(fileUrls[1])
                            .override(82, 68)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(ruvPhoto2);
                    if (fileComments[1] != null && !fileComments[1].equals("")) {
                        cEt2.setVisibility(View.VISIBLE);
                        cEt2.setText(fileComments[1]);
                    }
                }
                if (fileUrls[2] != null && !fileUrls[2].equals("") && ruvPhoto3.getDrawable() == null) {
                    //                    ruvPhoto3 = (ImageView) mView.findViewById(R.id.ruvPic3);
                    Glide.with(mActivity)
                            .load(fileUrls[2])
                            .override(82, 68)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(ruvPhoto3);
                    if (fileComments[2] != null && !fileComments[2].equals("")) {
                        cEt3.setVisibility(View.VISIBLE);
                        cEt3.setText(fileComments[2]);
                    }
                }
            }
        }

        ruvPhoto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Photo 1 Click");
                Bundle picEditArgs = new Bundle();
                putBundleData(picEditArgs);
                picEditArgs.putString("editImgUrl", fileUrls[0]);
                picEditArgs.putString("editCommentText", fileComments[0]);
                picEditArgs.putInt("editIndex", 0);
                mListener.editFragInteraction(picEditArgs, RUV_IMAGE_EDIT);
            }
        });
        ruvPhoto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Photo 2 Click");
                Bundle picEditArgs = new Bundle();
                putBundleData(picEditArgs);
                picEditArgs.putString("editImgUrl", fileUrls[1]);
                picEditArgs.putString("editCommentText", fileComments[1]);
                picEditArgs.putInt("editIndex", 1);
                mListener.editFragInteraction(picEditArgs, RUV_IMAGE_EDIT);
            }
        });
        ruvPhoto3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Photo 3 Click");
                Bundle picEditArgs = new Bundle();
                putBundleData(picEditArgs);
                picEditArgs.putString("editImgUrl", fileUrls[2]);
                picEditArgs.putString("editCommentText", fileComments[2]);
                picEditArgs.putInt("editIndex", 2);
                mListener.editFragInteraction(picEditArgs, RUV_IMAGE_EDIT);
            }
        });

        getLocBtn = (Button) mView.findViewById(R.id.getLocationBtn);
        getLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActivity.getGeoLocation()) {
                    String[] newAddress = mActivity.getAddress();
                    cityEt.setText(newAddress[0]);
                    regionEt.setText(newAddress[1]);
                } else {
                    Toast.makeText(mActivity, "Geolocation request failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        okayBtn = (ImageButton) mView.findViewById(R.id.okayBtn);
        okayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putFloat("length", Float.valueOf(EditFragment.this.roofLength.getText().toString()));
                bundle.putFloat("width", Float.valueOf(EditFragment.this.roofWidth.getText().toString()));
                bundle.putFloat("slope", Float.valueOf(EditFragment.this.roofSlope.getText().toString()));
                bundle.putString("material", EditFragment.this.material);
                bundle.putString("address", EditFragment.this.addressEt.getText().toString());
                bundle.putString("city", EditFragment.this.cityEt.getText().toString());
                bundle.putString("region", EditFragment.this.regionEt.getText().toString());
                bundle.putString("postal", EditFragment.this.postalEt.getText().toString());
                bundle.putString("price", EditFragment.this.priceEt.getText().toString());
                bundle.putInt("fileCount", fileCount);
                bundle.putString("firstName", firstEt.getText().toString());
                bundle.putString("lastName", lastEt.getText().toString());
                bundle.putString("email", emailEt.getText().toString());
                bundle.putString("phone", phoneEt.getText().toString());
                bundle.putString("prefix", prefix);

//                if (fileUrls != null && fileUrls.length > 0) {
//                    ArrayList<String> editFiles = new ArrayList<String>();
//                    for (String fileUrl : fileUrls) {
//                        editFiles.add(fileUrl);
//                    }
//                    bundle.putStringArrayList("fileUrls", editFiles);
//                }
                bundle.putStringArray("fileUrls", fileUrls);
                mListener.editFragInteraction(bundle, RUV_FINISH_EDIT);
            }
        });

        clearBtn = (ImageButton) mView.findViewById(R.id.clearBtn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearValues();
            }
        });

        if (checkCameraHardware(mActivity)) {
//            photoBtn = (ImageButton) mView.findViewById(R.id.takePhoto);
//            photoBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA)
//                            != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(mActivity,
//                                new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
//                    } else {
//                        if (checkCameraHardware(mActivity)) {
//                            Intent intent = new Intent(mActivity, CameraActivity.class);
//                            mActivity.putIntentData(intent);
//                            intent.putExtra("callingClass", EditFragment.this.getClass().getSimpleName());
//                            mActivity.putPrefsData();
//                            startActivity(intent);
//                        } else {
//                            Log.d(TAG, "No Camera Hardware on Device");
//                        }
//                    }
//                }
//            });
//        } else {
//            Log.d(TAG, "Camera Not Available");
        }

        if (this.actionMode == MainActivity.RUV_IMGEDIT_DELETE) {
            Snackbar.make(mView, "Image Removed", Snackbar.LENGTH_SHORT).show();
            this.actionMode = MainActivity.RUV_EDIT_MODE;
        }
        return mView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");


        if (savedInstanceState != null) {
            getBundleData(savedInstanceState);
        } else if (mActivity.getSavedEditFragState() != null) {
            getBundleData(mActivity.getSavedEditFragState());
        } else {
            getBundleData(getArguments());
        }

        if (fileCount > 0) {
            if (fileUrls != null) {
                if (fileUrls.length > 0 && fileUrls[0] != null && !fileUrls[0].equals("") && ruvPhoto1.getDrawable() == null) {
                    Glide.with(mActivity)
                            .load(fileUrls[0])
                            .override(92, 68)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(ruvPhoto1);
                }
                if (fileUrls.length > 1 && fileUrls[1] != null && !fileUrls[1].equals("") && ruvPhoto2.getDrawable() == null) {
                    Glide.with(mActivity)
                            .load(fileUrls[1])
                            .override(92, 68)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(ruvPhoto2);
                }
                if (fileUrls.length > 2 && fileUrls[2] != null && !fileUrls[2].equals("") && ruvPhoto3.getDrawable() == null) {
                    //                    ruvPhoto3 = (ImageView) mView.findViewById(R.id.ruvPic3);
                    Glide.with(mActivity)
                            .load(fileUrls[2])
                            .override(92, 68)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(ruvPhoto3);
                }
            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        putBundleData(outState);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
        }
        if (context instanceof EditFragListener) {
            mListener = (EditFragListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement EditFragListener");
        }
    }

    public void putBundleData(Bundle bundle) {
        Log.d(TAG, "putBundleData");
        bundle.putFloat("length", Float.valueOf(EditFragment.this.roofLength.getText().toString()));
        bundle.putFloat("width", Float.valueOf(EditFragment.this.roofWidth.getText().toString()));
        bundle.putFloat("slope", Float.valueOf(EditFragment.this.roofSlope.getText().toString()));
        bundle.putString("material", EditFragment.this.material);
        bundle.putString("address", EditFragment.this.addressEt.getText().toString());
        bundle.putString("city", EditFragment.this.cityEt.getText().toString());
        bundle.putString("region", EditFragment.this.regionEt.getText().toString());
        bundle.putString("postal", EditFragment.this.postalEt.getText().toString());
        bundle.putString("price", EditFragment.this.priceEt.getText().toString());
        bundle.putInt("fileCount", fileCount);
        bundle.putString("firstName", firstEt.getText().toString());
        bundle.putString("lastName", lastEt.getText().toString());
        bundle.putString("email", emailEt.getText().toString());
        bundle.putString("phone", phoneEt.getText().toString());
        bundle.putString("prefix", prefix);
        if (this.fileUrls != null) {
            bundle.putStringArray("fileUrls", this.fileUrls);
        }
        if (this.fileComments != null) {
            bundle.putStringArray("fileComments", this.fileComments);
        }
        bundle.putInt("editMode", this.actionMode);
    }

    public void getBundleData(Bundle bundle) {
        Log.d(TAG, "getBundleData");
        if (this.length <= 0)
            this.length = bundle.getFloat("length");
        if (this.width <= 0)
            this.width = bundle.getFloat("width");
        if (this.slope <= 0)
            this.slope = bundle.getFloat("slope");
        if (this.material == null || this.material.equals(""))
            this.material = bundle.getString("material");
        if (this.address == null || this.address.equals(""))
            this.address = bundle.getString("address");
        if (this.city == null || this.city.equals(""))
            this.city = bundle.getString("city");
        if (this.region == null || this.region.equals(""))
            this.region = bundle.getString("region");
        if (this.postal == null || this.postal.equals(""))
            this.postal = bundle.getString("postal");
        if (this.firstName == null || this.firstName.equals(""))
            this.firstName = bundle.getString("firstName");
        if (this.lastName == null || this.lastName.equals(""))
            this.lastName = bundle.getString("lastName");
        if (this.email == null || this.email.equals(""))
            this.email = bundle.getString("email");
        if (this.phone == null || this.phone.equals(""))
            this.phone = bundle.getString("phone");
        if (this.prefix == null || this.prefix.equals(""))
            this.prefix = bundle.getString("prefix");
        if (bundle.getString("price") != null && this.price == null)
            this.price = new BigDecimal(bundle.getString("price"));
        if (this.fileCount <= 0)
            this.fileCount = bundle.getInt("fileCount", 0);
//        if (bundle.getStringArrayList("fileUrls") != null && (this.ruvFiles != null && !ruvFileUrls.get(0).equals(""))) {
//            this.ruvFileUrls = (ArrayList<String>)bundle.getStringArrayList("fileUrls");
//        }
        if (bundle.getStringArray("fileUrls") != null) {
            if (this.fileUrls == null) this.fileUrls = new String[3];
            this.fileUrls = bundle.getStringArray("fileUrls");
//            if (this.ruvFileUrls == null) this.ruvFileUrls = new ArrayList<String>();
//            for (int i = 0; i < fileUrls.length; i++) {
//                ruvFileUrls.add(fileUrls[i]);
//            }
        }
        if (bundle.getStringArray("fileComments") != null) {
            this.fileComments = bundle.getStringArray("fileComments");
        }
        this.actionMode = bundle.getInt("editAction");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.d(TAG, "onDetach");
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        Bundle args = new Bundle();
        putBundleData(args);
        mActivity.saveEditFragState(args);
    }


    public interface EditFragListener {
        void editFragInteraction(Bundle bundle, int request);
    }


    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(mActivity, CameraActivity.class);
                    mActivity.putPrefsData();
                    intent.putExtra("callingClass", EditFragment.this.getClass().getSimpleName());
                    mActivity.putIntentData(intent);
                    startActivity(intent);
                } else {
                    Toast.makeText(mActivity, "Please grant camera permission to use the CAMERA", Toast.LENGTH_SHORT).show();
                }
                return;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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


    public void processImage(Intent data, int fileCount) {
        Log.d(TAG, "processImage");
        if (fileCount < 3) {
            Uri rawUri = data.getData();
            String imageUri = null;
            String wholeID = DocumentsContract.getDocumentId(rawUri);

            String id = wholeID.split(":")[1];

            String[] column = {MediaStore.Images.Media.DATA};

            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = mActivity.getContentResolver().
                    query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{id}, null);

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
                rFileInfo = new RuvFileInfo();
                rFileInfo.setFilePath(imageUri);
                rFileInfo.setFilename(imageUri.substring(imageUri.lastIndexOf('/') + 1));
//                rFileInfo.setNum(fileCount);
                boolean added = false;
                ruvFiles.add(rFileInfo);
                if (EditFragment.this.ruvFileUrls == null) {
                    EditFragment.this.ruvFileUrls = new ArrayList<String>();
                }
                if (EditFragment.this.fileUrls != null) {

                    int i = 0;
                    while (!added && i < 3) {
                        if (EditFragment.this.fileUrls[i] == null || EditFragment.this.fileUrls[i].equals("")) {
                            EditFragment.this.fileUrls[i] = imageUri;
                            added = true;
                        } else {
                            i++;
                        }
                    }
                }
                EditFragment.this.ruvFileUrls.add(imageUri);
                EditFragment.this.fileCount++;
                Bundle persistData = new Bundle();
                putBundleData(persistData);
                mActivity.saveEditFragState(persistData);
            } else {
                Toast.makeText(mActivity, "Unable to get content URI", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mActivity, "You have already chosen 3 images", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearValues() {
        this.address = "";
        this.postal = "";
        this.city = "";
        this.region = "";
        this.price = new BigDecimal(0);
        this.width = 0f;
        this.length = 0f;
        this.slope = 0f;
        this.premium = false;
        this.currentRid = 0;
        this.fileCount = 0;
        this.fileUrls = null;
        this.mCustomer = null;


        roofLength.setText("0");
        roofWidth.setText("0");
        roofSlope.setText("0");
        String zeroPrice = "$" + String.valueOf(this.price);
        priceEt.setText(zeroPrice);
        Glide.clear(ruvPhoto1);
        Glide.clear(ruvPhoto2);
        Glide.clear(ruvPhoto3);

    }
}