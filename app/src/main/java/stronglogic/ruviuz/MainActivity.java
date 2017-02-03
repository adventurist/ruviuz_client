package stronglogic.ruviuz;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

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
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import stronglogic.ruviuz.content.Customer;
import stronglogic.ruviuz.content.RuvFileInfo;
import stronglogic.ruviuz.fragments.AddressFragment;
import stronglogic.ruviuz.fragments.CustomerFragment;
import stronglogic.ruviuz.fragments.EditFragment;
import stronglogic.ruviuz.fragments.FileFragment;
import stronglogic.ruviuz.fragments.ImageEditFragment;
import stronglogic.ruviuz.fragments.LoginFragment;
import stronglogic.ruviuz.fragments.MainFragment;
import stronglogic.ruviuz.fragments.MetricFragment;
import stronglogic.ruviuz.fragments.SlopeFragment;
import stronglogic.ruviuz.fragments.WelcomeFragment;
import stronglogic.ruviuz.util.RuuvComment;
import stronglogic.ruviuz.util.RuuvFile;
import stronglogic.ruviuz.util.RuvLocation;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginFragListener, MainFragment.MainfragListener, WelcomeFragment.WelcomeFragListener, AddressFragment.AddressFragListener, MetricFragment.OnFragmentInteractionListener, SlopeFragment.SlopeFragListener, FileFragment.FileFragListener, CustomerFragment.CustomerFragListener, EditFragment.EditFragListener, ImageEditFragment.ImageFragListener, Handler.Callback {

    private static final String TAG = "RuviuzMAINACTIVITY";
    
    private static final int CAMERA_PERMISSION = 6;
    private static final int RUVIUZ_DATA_PERSIST = 14;
    private static final int METRICFRAG_COMPLETE = 21;
    private static final int CURATION_MODE = 32;
    private final static int CREATE_QUOTE = 33;
    private final static int SEE_QUOTES = 34;
    private final static int REQUEST_LOGIN = 35;
    private final static int CREATE_ACCOUNT = 36;
    private final static int FILE_ADD_MODE = 37;
    private final static int SLOPE_FRAG_SUCCESS = 39;
    public static final int RUV_ADD_FILES = 40;
    public static final int WELCOME_REQUEST = 60;
    public static final int LOGIN_REQUEST = 61;
    public static final int CLEAR_REQUEST = 62;
    public static final int ADDRESS_REQUEST = 63;
    public static final int CUSTOMER_REQUEST = 64;
    public static final int METRIC_REQUEST = 65;
    public static final int CAMERA_REQUEST = 66;
    public static final int GEOLOCATION_REQUEST = 67;
    public static final int RUV_EDIT_MODE = 70;
    public static final int RUV_IMAGE_EDIT = 71;
    public static final int RUV_FINISH_EDIT = 72;
    public static final int RUV_IMGEDIT_DELETE = 73;
    public static final int RUV_EDIT_OFF = 79;

    public static final String baseUrl = "http://52.43.250.94:5000";

    private android.support.v7.widget.Toolbar mToolbar;

    private LoginFragment mLoginFrag;
    private MainFragment mainFragment;
    private AddressFragment mAddressFrag;
    private MetricFragment metricFrag;
    private SlopeFragment slopeFrag;
    private FileFragment fileFrag;
    private CustomerFragment mCustomerFrag;
    private WelcomeFragment mWelcomeFrag;
    private EditFragment editFrag;
    private ImageEditFragment imgEditFrag;

    private TextView addressTv, nameTv, phoneTv, emailTv, roofLength, roofWidth, roofSlope, currentPrice, materialTv;
    private Switch isFlat, premiumMaterial;
    private ImageView photo1, photo2, photo3;
    private ImageButton ruuvBtn, calculateBtn;
    private Button addressBtn, metricBtn, draftBtn, editBtn;
    private ImageButton clearBtn, photoBtn, clientBtn;

    private String authToken;

    private IncomingHandler mHandler;

    private Map<String, RuvFileInfo> ruvFiles;

    private SharedPreferences prefs;

    private RuvLocation rLocation;

    private Geocoder geocoder;

    private int fileCount, commentCount, currentRid, lastAction;
    private float width, length, slope;
    private BigDecimal price;
    private String material, address, postal, city, region;
    private String[] fileUrls = new String[3];
    private String[] fileComments = new String[3];
    private ArrayList<Map<String, String>> callbackFiles;
    private Customer mCustomer;
    private boolean premium, ready, editing;

    private Bundle editFragArgs;
    private int editMode = RUV_EDIT_OFF;

    public void saveEditFragState(Bundle args) {
        editFragArgs = args;
    }

    public Bundle getSavedEditFragState() {
        return editFragArgs;
    }
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mGoogleApi;

    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window w = getWindow();
        View decorView = w.getDecorView();
        // Show Status Bar.
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        mToolbar = (Toolbar) findViewById(R.id.app_bar);

        MainActivity.this.findViewById(R.id.MainParentView).setPadding(0, getStatusBarHeight(MainActivity.this), 0, 0);

        w.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.ruvGreenStatus));

        this.prefs = MainActivity.this.getSharedPreferences("RuviuzApp", Context.MODE_PRIVATE);
        getPrefsData();


        setSupportActionBar(mToolbar);

        if (getSupportActionBar()!= null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.construction);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(8f);
        }

//        if (checkCameraHardware(this)) {
//            photoBtn = (ImageButton) findViewById(R.id.takePhoto);
//            photoBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
//                            != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(MainActivity.this,
//                                new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
//                    } else {
//                        if (checkCameraHardware(MainActivity.this)) {
//                            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
//                            putIntentData(intent);
//                            intent.putExtra("authToken", authToken);
//                            intent.putExtra("callingClass", MainActivity.this.getClass().getSimpleName());
//                            putPrefsData();
//                            setResult(RUVIUZ_DATA_PERSIST);
//                            startActivity(intent);
//                        } else {
//                            Log.d(TAG, "No Camera Hardware on Device");
//                        }
//                    }
//                }
//            });
//        } else {
//            Log.d(TAG, "Camera Not Available");
//        }

//        if (mGoogleApi == null) {
//            mGoogleApi = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
//        }

        roofLength = (TextView) findViewById(R.id.roofLength);
        roofWidth = (TextView) findViewById(R.id.roofWidth);
        roofSlope = (TextView) findViewById(R.id.roofSlope);

        //set Picker ranges
        roofLength.setText(String.valueOf(length));
//        if (prefs.contains("length")) {
//            roofLength.setText(String.valueOf(prefs.getFloat("length", 0f)));
//        }

        roofWidth.setText(String.valueOf(width));
        roofSlope.setText(String.valueOf(MainActivity.this.slope));

        addressTv = (TextView) findViewById(R.id.addressTv);
        String addressString = address + "\n" + city + ", " + region + "\n" + postal;
        addressTv.setText(addressString);
        nameTv = (TextView) findViewById(R.id.nameTv);
        emailTv = (TextView) findViewById(R.id.emailTv);
        phoneTv = (TextView) findViewById(R.id.phoneTv);

        if (mCustomer != null) {
            String nameString = mCustomer.getPrefix() + " " + mCustomer.getFirstname() + " " + mCustomer.getLastname();
            nameTv.setText(nameString);
            emailTv.setText(mCustomer.getEmail());
            phoneTv.setText(mCustomer.getPhone());
        }
        materialTv = (TextView) findViewById(R.id.materialTv);
//        premiumMaterial = (Switch) findViewById(R.id.premiumMaterial);
//        premiumMaterial.setChecked(premium);
//
//        premiumMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    premiumMaterial.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));
//                } else {
//                    premiumMaterial.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.ruvMatGreen));
//                }
//                setPremium(isChecked);
//            }
//        });


        mHandler = new IncomingHandler(this);

//        if (authToken == null) {
//            loginDialog();
//        }

        photo1 = (ImageView) findViewById(R.id.ruvPic1);
        photo2 = (ImageView) findViewById(R.id.ruvPic2);
        photo3 = (ImageView) findViewById(R.id.ruvPic3);

        ruuvBtn = (ImageButton) findViewById(R.id.ruuvSubmitBtn);

        ruuvBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (address == null) {
                    addressDialog();
                } else if (authToken == null) {
                    Snackbar.make(MainActivity.this.findViewById(R.id.MainParentView), "You are not logged in", Snackbar.LENGTH_SHORT).show();
                    loginDialog();
                } else {
                    if (!roofLength.getText().toString().equals("")&&
                            !roofSlope.getText().toString().equals("") &&
                            !roofWidth.getText().toString().equals("") &&
                            (address != null)) {

                        slope = Float.parseFloat(String.valueOf(roofSlope.getText()));
                        length = Float.parseFloat(String.valueOf(roofWidth.getText()));
                        width = Float.parseFloat(String.valueOf(roofWidth.getText()));

                        Bundle mBundle = new Bundle();
                        mBundle.putString("address", address);
                        mBundle.putString("postal", postal);
                        mBundle.putString("city", city);
                        mBundle.putString("region", region);
                        mBundle.putString("price", calculatePrice().toString());
                        mBundle.putFloat("width", Float.parseFloat(String.valueOf(roofWidth.getText())));
                        mBundle.putFloat("length", Float.parseFloat(String.valueOf(roofLength.getText())));
                        mBundle.putFloat("slope", Float.parseFloat(String.valueOf(roofSlope.getText())));
                        mBundle.putString("firstName", mCustomer.getFirstname());
                        mBundle.putString("lastName", mCustomer.getLastname());
                        mBundle.putString("email", mCustomer.getEmail());
                        mBundle.putString("phone", mCustomer.getPhone());
                        mBundle.putString("prefix", mCustomer.getPrefix());

                        String finalPrice = MainActivity.this.price.toString();
                        MainActivity.this.currentPrice.setText(finalPrice);
                        RuuvThread ruuvThread = new RuuvThread((MainActivity) getParent(), mHandler, baseUrl, authToken, mBundle);
                        Thread submitThread = new Thread(ruuvThread);
                        submitThread.start();
                    }
                }
            }
        });

        editBtn = (Button) findViewById(R.id.editQuote);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.editing = true;
                editDialog();
                hideActivity();
            }
        });
//        addressBtn = (Button) findViewById(R.id.addressGetDialog);
//        addressBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addressDialog();
//            }
//        });
//
//        if (!address.equals("")) {
//            addressBtn.setAlpha(1f);
//        }
//
//        metricBtn = (Button) findViewById(R.id.metricfragOpen);
//
//        metricBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getMetric();
//            }
//        });

        currentPrice = (TextView) findViewById(R.id.currentPrice);
        String mPrice = "$" + calculatePrice().toString();
        currentPrice.setText(mPrice);
        calculateBtn = (ImageButton) findViewById(R.id.calculateBtn);
        calculateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mPrice = "$" + calculatePrice().toString();
                currentPrice.setText(mPrice);
            }
        });

//        clientBtn = (ImageButton) findViewById(R.id.clientBtn);
//        clientBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                customerDialog();
//            }
//        });
//
//
//        if (mCustomer != null) {
//            clientBtn.setAlpha(1f);
//        }

//        clearBtn = (ImageButton) findViewById(R.id.clearBtn);
//        clearBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clearValues();
//            }
//        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApi = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

//        draftBtn = (Button) findViewById(R.id.quoteStatus);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.side_menu);
        mDrawerView = (NavigationView) findViewById(R.id.left_drawer);
        ActionBarDrawerToggle drawerToggle = setupDrawerToggle();
        mDrawerLayout.addDrawerListener(drawerToggle);
        mDrawerView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d(TAG, item.toString());
                Intent mIntent = new Intent();
                switch (item.getTitle().toString()) {
                    case ("Roof List"):
                        mIntent.setClass(MainActivity.this, IndexViewActivity.class);
                        putIntentData(mIntent);
                        mIntent.putExtra("authToken", authToken);
                        mIntent.putExtra("baseUrl", baseUrl);
                        putPrefsData();
                        startActivity(mIntent);

                        break;
                    case ("Take Photo"):
                        Log.d(TAG, "Getting da camera goin");
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "Need camera permission");
                            ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
                        } else {
                            if (checkCameraHardware(MainActivity.this)) {
                                Log.d(TAG, "Starting Camera Intent");
                                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                                putIntentData(intent);
                                intent.putExtra("authToken", authToken);
                                intent.putExtra("callingClass", MainActivity.this.getClass().getSimpleName());
                                putPrefsData();
                                setResult(RUVIUZ_DATA_PERSIST);
                                startActivity(intent);
                            } else {
                                Log.d(TAG, "No Camera Hardware on Device");
                            }
                        }
//
//                           mIntent.setClass(MainActivity.this, CameraActivity.class);
//                           putIntentData(mIntent);
//                           mIntent.putExtra("authToken", authToken);
//                           mIntent.putExtra("baseUrl", baseUrl);
//                           putPrefsData();
//                           startActivity(mIntent);
                           break;
                       case ("Measure"):
                           getMetric();
                           hideActivity();
                           mDrawerLayout.closeDrawers();
                           break;
                       case ("Client"):
                           customerDialog();
                           hideActivity();
                           mDrawerLayout.closeDrawers();
                           break;
                       case ("Address"):
                           addressDialog();
                           hideActivity();
                           mDrawerLayout.closeDrawers();
                           break;
                       case ("Get Location"):
                           getGeoLocation();
                           mDrawerLayout.closeDrawers();
                           break;
                       case ("Upload"):
                           Log.d(TAG, "Upload to be implemented in MainActivity");
                           break;
                       case ("Clear"):
                           clearValues();
                           break;
                       case ("Login"):
                           loginDialog();
                           hideActivity();
                           mDrawerLayout.closeDrawers();
                           break;
                       case ("Logout"):
                           Log.d(TAG, "Logout to be implemented in MainActivity");
                           break;
                        case ("Start"):
                           Log.d(TAG, "Going HOME");
                           welcomeDialog();
                            hideActivity();
                          mDrawerLayout.closeDrawers();
                            break;
                       default:
                           break;
                   }
//               }
                return false;
            }
        });
//        if (!ready) {
//            hideActivity();
//        }
        if (authToken == null) {
            hideActivity();
            customerDialog();
        }
        if (getIntent() != null && getIntent().hasExtra("PERSIST")) {
            getIntentData(getIntent());
        }
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.OpenDrawer,  R.string.CloseDrawer) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                mDrawerLayout.bringChildToFront(drawerView);
                mDrawerLayout.requestLayout();
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    mDrawerLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorRow));
                    sendViewToBack(mDrawerLayout);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                    mDrawerLayout.setBackgroundColor(Color.TRANSPARENT);
                    mDrawerLayout.bringToFront();
                }
            }


            @Override
            public void onDrawerOpened(View drawerView) {
                Log.d(TAG, "DRAWER OPENED!");
//                if (mToolbar != null) mToolbar.setTitle("Choose Your Destiny.");
                if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                    mDrawerLayout.setBackgroundColor(Color.TRANSPARENT);
                    mDrawerLayout.bringToFront();
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
//                if (mToolbar != null) mToolbar.setTitle("Ruviuz");
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    mDrawerLayout.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorRow));
                    sendViewToBack(mDrawerLayout);
                }
            }
        };
    }


    @Override
    public void onResume() {
        super.onResume();

        if (this.prefs == null)
            this.prefs = MainActivity.this.getSharedPreferences("RuviuzApp", Context.MODE_PRIVATE);

        if (getIntent() != null) {
            Intent xIntent = getIntent();

            if (fileUrls != null && (fileUrls.length > 0 || fileCount == 0)) {
                int realCount = 0;

                if (fileUrls.length == fileComments.length) {
                    if (this.ruvFiles == null) {
                        this.ruvFiles = new HashMap<>();

                    }
                    for (int i = fileUrls.length; i > 0; i--) {
                        if (fileUrls[i - 1] != null && !fileUrls[i - 1].equals("") &&
                                fileComments[i - 1] != null && !fileComments[i- 1].equals("")) {
                            RuvFileInfo rFile = new RuvFileInfo();
                            rFile.setUrl(fileUrls[i - 1]);
                            rFile.setFilename(fileUrls[i - 1].substring(fileUrls[i - 1].lastIndexOf('/') + 1));
                            rFile.setComment(fileComments[i - 1]);
                            ruvFiles.put(fileUrls[i - 1], rFile);
                        }
                    }
                }

                for (int i = fileUrls.length; i > 0; i--) {
                    realCount = !fileUrls[i - 1].equals("") ? realCount + 1 : realCount;
                }
                this.fileCount = realCount;
            }
            if (fileComments != null && (fileComments.length > 0 || commentCount == 0)) {
                int realCount = 0;
                for (int i = fileComments.length; i > 0; i--) {
                    realCount = !fileComments[i - 1].equals("") ? realCount + 1 : realCount;
                }
                this.commentCount = realCount;
            }

            if (xIntent.hasExtra("uri")) {
                if (fileCount + 1 == 4) {
                    Toast.makeText(MainActivity.this, "You cannot add more photos", Toast.LENGTH_SHORT).show();
                } else {
                    this.fileCount++;
                    if (fileUrls == null) {
                        this.fileUrls = new String[3];
                    }
                    if (fileUrls[fileCount - 1] == null) {
                        fileUrls[fileCount - 1] = "";
                    }
                    Bundle stuff = xIntent.getExtras();
                    String uriS = stuff.getString("uri");
                    fileUrls[fileCount - 1] = uriS;
                }
            }
            if (xIntent.hasExtra("callingClass")) {
                if (xIntent.getStringExtra("callingClass").equals("FileFragment")) {
                    fileDialog();
                    return;
                }
            }
        }


        if (fileCount > 0) {
            if (fileUrls != null) {
                if (fileUrls[0] != null && photo1.getDrawable() == null) {
                    Glide.with(MainActivity.this)
                            .load(fileUrls[0])
                            .override(92, 68)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(photo1);
                }
                if (fileUrls[1] != null && photo2.getDrawable() == null) {
                    photo2 = (ImageView) findViewById(R.id.ruvPic2);
                    Glide.with(MainActivity.this)
                            .load(fileUrls[1])
                            .override(92, 68)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(photo2);
                }
                if (fileUrls[2] != null && photo3.getDrawable() == null) {
                    photo3 = (ImageView) findViewById(R.id.ruvPic3);
                    Glide.with(MainActivity.this)
                            .load(fileUrls[2])
                            .override(92, 68)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(photo3);
                }
            }
        } else {
            refreshUi();
        }
        draftCheck();
        if (authToken == null) {
            hideActivity();
            dismissAllDialogs();
            welcomeDialog();
        } else {
            dismissAllDialogs();
        }

        if ((mainFragment == null || !mainFragment.isAdded()) && (mLoginFrag == null || !mLoginFrag.isAdded()) && (mCustomerFrag == null || !mCustomerFrag.isAdded()) && (metricFrag == null || !metricFrag.isAdded()) && (slopeFrag == null || !slopeFrag.isAdded()) && (mWelcomeFrag == null || !mWelcomeFrag.isAdded())) {
            revealActivity();
        }

        if (isEditing()) {
            hideActivity();
            editDialog();
        }
        if (this.lastAction == RUV_ADD_FILES) {
            //obliged to draw attention
            hideActivity();
            fileDialog();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        putPrefsData();
        if (rLocation != null) {
            rLocation = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        putPrefsData();
        if (getIntent().hasExtra("uri"))
            getIntent().removeExtra("uri");
        dismissAllDialogs();
    }

    @Override
    public void onAttachFragment(Fragment mFragment) {
        super.onAttachFragment(mFragment);

//        if (mFragment instanceof MetricFragment) {
//            this.mListener.disable();
//            this.mListener = null;
//        }
    }


    void setPremium(boolean checked) {
        this.premium = checked;
    }


    BigDecimal calculatePrice() {
        try {
            BigDecimal mPrice = new BigDecimal((double)(length == 0 ? 1 : length * width == 0 ? 1 : width * (0.428 * slope == 0 ? 1 : slope))).setScale(2, BigDecimal.ROUND_HALF_UP);

            if (premium) {
                mPrice = mPrice.multiply(new BigDecimal(2));
            }
            this.price = mPrice;
            return mPrice;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void welcomeDialog() {
        dismissAllDialogs();
        FragmentManager fm = getFragmentManager();
        if (mWelcomeFrag == null) {
            mWelcomeFrag= new WelcomeFragment();
            if (!mWelcomeFrag.isAdded()) {
                mWelcomeFrag.show(fm, "Welcome to Ruviuz");
            }
        }
        if (mWelcomeFrag != null) {
            fm.beginTransaction().remove(mWelcomeFrag).commit();
            mWelcomeFrag = null;
            mWelcomeFrag = new WelcomeFragment();
            mWelcomeFrag.show(fm, "Welcome to Ruviuz");
        }
    }


    public void loginDialog() {

        FragmentManager fm = getFragmentManager();
        if (mLoginFrag == null) {
            mLoginFrag = new LoginFragment();
            if (!mLoginFrag.isAdded()) {

                Bundle args = new Bundle();
                args.putString("baseUrl", MainActivity.baseUrl);
                if (getPrefCreds() != null) {
                    args.putString("email", prefs.getString("email", "Email"));
                    args.putString("password", prefs.getString("password", "Password"));
                }
                mLoginFrag.setArguments(args);
                mLoginFrag.show(fm, "Please Login");
            }

        } else {
            fm.beginTransaction().remove(mLoginFrag).commit();
            mLoginFrag = null;
            mLoginFrag = new LoginFragment();
            Bundle args = new Bundle();
            args.putString("baseUrl", MainActivity.baseUrl);
            if (getPrefCreds() != null) {
                args.putString("email", prefs.getString("email", "Email"));
                args.putString("password", prefs.getString("password", "Password"));
            }
            mLoginFrag.setArguments(args);
            mLoginFrag.show(fm, "Please Login");
        }
    }

    public void mainDialog() {
        dismissAllDialogs();
        FragmentManager fm = getFragmentManager();
        if (mainFragment == null) {
            mainFragment = new MainFragment();
            Bundle fragBundle = new Bundle();
            fragBundle.putString("baseUrl", baseUrl);
            mainFragment.setArguments(fragBundle);
            if (!mainFragment.isAdded()) {
                mainFragment.show(fm, "Choose an action");
            }
        }
        if (mainFragment!= null) {
            fm.beginTransaction().remove(mainFragment).commit();
            mainFragment = null;
            mainFragment = new MainFragment();
            mainFragment.show(fm, "Choose an action");
        }
    }



    public void addressDialog() {
        dismissAllDialogs();
        FragmentManager fm = getFragmentManager();
        if (mAddressFrag == null) {
            mAddressFrag = new AddressFragment();
            if (!mAddressFrag.isAdded()) {
                mAddressFrag.show(fm, "Please Enter Address");
            }
        }
        if (mAddressFrag != null) {
            fm.beginTransaction().remove(mAddressFrag).commit();
            mAddressFrag = null;
            mAddressFrag = new AddressFragment();
            mAddressFrag.show(fm, "Please Enter Address");
        }
    }

    public void customerDialog() {
        dismissAllDialogs();
        FragmentManager fm = getFragmentManager();

        Bundle args = new Bundle();
        if (mCustomer != null) {
            args.putString("firstName", mCustomer.getFirstname());
            args.putString("lastName", mCustomer.getLastname());
            args.putString("email", mCustomer.getEmail());
            args.putString("phone", mCustomer.getPhone());
            args.putString("prefix", mCustomer.getPrefix());
        }

        if (mCustomerFrag == null) {
            mCustomerFrag = new CustomerFragment();
            mCustomerFrag.setArguments(args);
            if (!mCustomerFrag .isAdded()) {
                mCustomerFrag.show(fm, "Please Enter Address");
            }
        }
        if (mCustomerFrag != null) {
            fm.beginTransaction().remove(mCustomerFrag ).commit();
            mCustomerFrag = null;
            mCustomerFrag = new CustomerFragment();
            mCustomerFrag.setArguments(args);
            mCustomerFrag.show(fm, "Please Enter Address");
        }
    }

    public void getMetric() {
        updateValues();
        dismissAllDialogs();
        Bundle mBundle = new Bundle();
        mBundle.putFloat("length", length);
        mBundle.putFloat("width", width);
        mBundle.putFloat("slope", slope);
        
        FragmentManager fm = getFragmentManager();
        if (metricFrag == null) {
            metricFrag = new MetricFragment();
            metricFrag.setArguments(mBundle);
            if (!metricFrag.isAdded()) {
                metricFrag.show(fm, "Please Enter Metrics");
            }
        } else {
            fm.beginTransaction().remove(metricFrag).commit();
            metricFrag = null;
            metricFrag = new MetricFragment();
            metricFrag.setArguments(mBundle);
            metricFrag.show(fm, "Please Enter Metrics");
        }
    }


    public void editDialog() {
        updateValues();
        dismissAllDialogs();
        Bundle mBundle = new Bundle();
        mBundle.putInt("editMode", editMode);
        mBundle.putFloat("length", length);
        mBundle.putFloat("width", width);
        mBundle.putFloat("slope", slope);
        mBundle.putString("material", material);
        mBundle.putString("address", address);
        mBundle.putString("city", city);
        mBundle.putString("region", region);
        mBundle.putString("postal", postal);
        mBundle.putString("price", String.valueOf(price));
        mBundle.putInt("fileCount", fileCount);
        if (fileUrls != null && fileUrls.length > 0) {
//            ArrayList<String> editFiles = new ArrayList<String>();
//            for (String fileUrl : fileUrls) {
//                editFiles.add(fileUrl);
//            }
        mBundle.putStringArray("fileUrls", fileUrls);
        }
        if (mCustomer != null) {
            mBundle.putString("firstName", mCustomer.getFirstname());
            mBundle.putString("lastName", mCustomer.getLastname());
            mBundle.putString("email", mCustomer.getEmail());
            mBundle.putString("phone", mCustomer.getPhone());
            mBundle.putString("prefix", mCustomer.getPrefix());
        }
        saveEditFragState(mBundle);
        FragmentManager fm = getFragmentManager();
        if (editFrag == null) {
            editFrag= new EditFragment();
            editFrag.setArguments(mBundle);
            if (!editFrag.isAdded()) {
                editFrag.show(fm, "Please Enter Metrics");
            }
        } else {
            fm.beginTransaction().remove(editFrag).commit();
            editFrag= null;
            editFrag = new EditFragment();
            editFrag.setArguments(mBundle);
            editFrag.show(fm, "Please Enter Metrics");
        }
    }

    public void slopeDialog() {
        updateValues();
        Bundle mBundle = new Bundle();
        mBundle.putFloat("slope", slope);

        FragmentManager fm = getFragmentManager();
        if (slopeFrag == null) {
            slopeFrag = new SlopeFragment();
            slopeFrag.setArguments(mBundle);
            if (!slopeFrag.isAdded()) {
                slopeFrag.show(fm, "Please Enter Slope");
            }
        } else {
            fm.beginTransaction().remove(slopeFrag).commit();
            slopeFrag = null;
            slopeFrag = new SlopeFragment();
            slopeFrag.setArguments(mBundle);
            slopeFrag.show(fm, "Please Enter Slope");
        }
    }

    public void fileDialog() {
        FragmentManager fm = getFragmentManager();
        Bundle mBundle = new Bundle();
        mBundle.putStringArray("fileUrls", fileUrls);
        mBundle.putInt("fileCount", fileCount);
        mBundle.putStringArray("fileComments", fileComments);
        this.lastAction = RUV_ADD_FILES;

        if (fileFrag == null) {
            fileFrag = new FileFragment();
            fileFrag.setArguments(mBundle);
            if (!fileFrag.isAdded()) {
                fileFrag.show(fm, "Add Images");
            }
        } else {
            fm.beginTransaction().remove(fileFrag).commit();
            fileFrag = null;
            fileFrag = new FileFragment();
            fileFrag.setArguments(mBundle);
            fileFrag.show(fm, "Add Images");
        }
    }

    public void editImgDialog(Bundle mBundle) {
        FragmentManager fm = getFragmentManager();
        if (imgEditFrag == null) {
            imgEditFrag = new ImageEditFragment();
            imgEditFrag.setArguments(mBundle);
            if (!imgEditFrag.isAdded()) {
                imgEditFrag.show(fm, "Edit Image");
            }
        } else {
            fm.beginTransaction().remove(imgEditFrag).commit();
            imgEditFrag = null;
            imgEditFrag = new ImageEditFragment();
            imgEditFrag.setArguments(mBundle);
            imgEditFrag.show(fm, "Please Enter Metrics");
        }
    }


    @Override
    public void loginFragInteraction(String output) {
        Toast.makeText(this, output, Toast.LENGTH_SHORT).show();
        this.authToken = output;


        if (mLoginFrag != null) {
            mLoginFrag.dismiss();
            getFragmentManager().beginTransaction().remove(mLoginFrag).commit();
            mainDialog();
        }
    }


    @Override
    public void addressFragInteraction(String address, String postal, String city, String region) {
        this.address = address;
        this.postal = postal;
        this.city = city;
        this.region = region;
        String addressString = address + "\n" + city + ", " + region + "\n" + postal;

        this.addressTv.setText(addressString);

        Toast.makeText(this, this.address, Toast.LENGTH_SHORT).show();
//        addressBtn.setAlpha(1f);

        if (mAddressFrag != null) {
            mAddressFrag.dismiss();
            getFragmentManager().beginTransaction().remove(mAddressFrag).commit();
        }
        if (!ready)
        getMetric();
    }


    @Override
    public void metricfragInteraction(Float[] values, String data) {
        Log.d(TAG, data);
        if (data.equals("METRIC_SUCCESS")) {
            Log.d(TAG, "METRICFRAG_COMPLETE");
            MainActivity.this.length = values[0];
            roofLength.setText(String.valueOf(values[0]));
//            roofLength.jumpDrawablesToCurrentState();
            MainActivity.this.width = values[1];
            roofWidth.setText(String.valueOf(values[1]));
//            roofWidth.jumpDrawablesToCurrentState();
            MainActivity.this.slope = values[2];
            roofSlope.setText(String.valueOf(MainActivity.this.slope));
//            roofSlope.jumpDrawablesToCurrentState();

            if (metricFrag != null && metricFrag.isAdded()) {
                metricFrag.dismiss();
            }

            if (mainFragment != null && mainFragment.isAdded()) {
                mainFragment.dismiss();
            }

            putPrefsData();

            slopeDialog();

        }

        if (data.equals("BACK_TO_ADDRESS")) {

            addressDialog();

            if (metricFrag != null && metricFrag.isAdded()) {
                metricFrag.dismiss();
            }

            if (mainFragment != null && mainFragment.isAdded()) {
                mainFragment.dismiss();
            }

        }
    }

    @Override
    public void customerfragInteraction(String[] name, String email, String phone,boolean married, String prefix) {
        Log.d(TAG, "CUSTOMERFRAGINTERACTION");
        if (mCustomer == null) mCustomer = new Customer();

        mCustomer.setFirstname(name[0]);
        mCustomer.setLastname(name[1]);
        mCustomer.setEmail(email);
        mCustomer.setPhone(phone);
        mCustomer.setMarried(married);
        mCustomer.setPrefix(prefix);

        String nameString = prefix + name[0] + " " + name[1];
        MainActivity.this.nameTv.setText(nameString);
        MainActivity.this.emailTv.setText(email);
        MainActivity.this.phoneTv.setText(phone);

        if (mCustomerFrag != null && mCustomerFrag.isAdded()) {
            mCustomerFrag.dismiss();
        }
        addressDialog();
    }

    @Override
    public void welcomeInteraction(int action) {

        Log.d(TAG, "welcomeInteraction");
        if (mWelcomeFrag != null && mWelcomeFrag.isAdded()) {
            mWelcomeFrag.dismiss();
        }

        if (action == REQUEST_LOGIN) {
            if (mWelcomeFrag != null && mWelcomeFrag.isAdded()) {
                mWelcomeFrag.dismiss();
            }

            loginDialog();
        }

        if (action == CREATE_ACCOUNT) {
            //TODO CREATE ACCOUNT DIALOG
        }
    }

    @Override
    public void mainfragInteraction(int action) {
        MainActivity.this.lastAction = action;

        if (action == CREATE_QUOTE) {
            hideActivity();
            MainActivity.this.ready = false;
            if (authToken == null) {
                Snackbar.make(MainActivity.this.findViewById(R.id.MainParentView), "You Must First Login", Snackbar.LENGTH_SHORT).show();
                loginDialog();
                mainFragment.dismiss();
            } else {
                customerDialog();
                mainFragment.dismiss();
            }
        }
        if (action == SEE_QUOTES) {
            revealActivity();
            if (authToken == null) {
                Snackbar.make(MainActivity.this.findViewById(R.id.MainParentView), "You Must First Login", Snackbar.LENGTH_SHORT).show();
                loginDialog();
                mainFragment.dismiss();
            } else {
                Intent mIntent = new Intent(MainActivity.this, RviewActivity.class);
                putIntentData(mIntent);
                mIntent.putExtra("authToken", authToken);
                mIntent.putExtra("baseUrl", MainActivity.baseUrl);
                putPrefsData();
                startActivity(mIntent);
                mainFragment.dismiss();
            }
        }

        if (MainActivity.this.lastAction == CREATE_QUOTE) {
            if (mainFragment != null && mainFragment.isAdded()) {
                mainFragment.dismiss();
            }
        }
    }

    @Override
    public void slopeFragInteraction(float value, int result) {
        if (result == SLOPE_FRAG_SUCCESS) {
            MainActivity.this.slope = value;
            roofSlope.setText(String.valueOf(MainActivity.this.slope));

            draftCheck();

            if (slopeFrag != null && slopeFrag.isAdded()) {
                slopeFrag.dismiss();
            }
            fileDialog();
        } else {
            slopeFrag.dismiss();
            getMetric();
        }
    }

    @Override
    public void fileFragInteraction(String[] newFileUrls, String[] newFileComments, int newFileCount, int result) {
        if (result == RUV_ADD_FILES) {
            MainActivity.this.fileUrls = newFileUrls;
            MainActivity.this.fileComments = newFileComments;
            MainActivity.this.fileCount = newFileCount;
            refreshUi();
            revealActivity();

            if (fileFrag != null && fileFrag.isAdded()) {
                fileFrag.dismiss();
            }
        } else {
            fileFrag.dismiss();
            revealActivity();
        }
//        if (this.lastAction == RUV_ADD_FILES) {
//
//        }
    }

    @Override
    public void editFragInteraction(Bundle bundle, int request) {
        Log.d(TAG, "Edit Frag Interaction received");
        if (request == RUV_IMAGE_EDIT) {
            editImgDialog(bundle);
        } else if (request == RUV_FINISH_EDIT) {
            this.length = bundle.getFloat("length");
            this.width = bundle.getFloat("width");
            this.slope = bundle.getFloat("slope");
            this.material = bundle.getString("material");
            this.address = bundle.getString("address");
            this.city = bundle.getString("city");
            this.region = bundle.getString("region");
            this.postal = bundle.getString("postal");

            if (this.mCustomer == null) this.mCustomer = new Customer();

            this.mCustomer.setFirstname(bundle.getString("firstName"));
            this.mCustomer.setLastname(bundle.getString("lastName"));
            this.mCustomer.setEmail(bundle.getString("email"));
            this.mCustomer.setPhone(bundle.getString("phone"));
            this.mCustomer.setPrefix(bundle.getString("prefix"));

            String nameString = mCustomer.getPrefix() + " " + mCustomer.getFirstname() + " " + mCustomer.getLastname();
            MainActivity.this.nameTv.setText(nameString);
            MainActivity.this.emailTv.setText(mCustomer.getEmail());
            MainActivity.this.phoneTv.setText(mCustomer.getPhone());

            roofLength.setText(String.valueOf(length));
            roofWidth.setText(String.valueOf(width));
            roofSlope.setText(String.valueOf(slope));

            this.materialTv.setText(this.material);

            String addressString = address + "\n" + city + ", " + region + "\n" + postal;
            addressTv.setText(addressString);
            String priceString = bundle.getString("price").substring(1).replaceAll(",", "");
            this.price = new BigDecimal(priceString);
            this.fileCount = bundle.getInt("fileCount", 0);
            this.editMode = bundle.getInt("editMode", RUV_EDIT_OFF);
//        if (bundle.getStringArrayList("fileUrls") != null) {
//            ArrayList<String> fileUrls = bundle.getStringArrayList("fileUrls");
//            if (this.fileUrls == null) this.fileUrls = new String[3];
//
//            for (int i = fileUrls.size() - 1; i >= 0; i--) {
//                this.fileUrls[i] = fileUrls.get(i);
//            }
//        }
            if (bundle.getStringArray("fileUrls") != null) {
                if (this.fileUrls == null) {
                    this.fileUrls = new String[3];
                    this.fileUrls = bundle.getStringArray("fileUrls");
                } else {
                    String[] recFileUrls = bundle.getStringArray("fileUrls");
                    if (this.fileUrls[0] != null && recFileUrls[0] != null) {
                        this.fileUrls[0] = recFileUrls[0];
                    }
                    if (this.fileUrls[1] != null && recFileUrls[1] != null) {
                        this.fileUrls[1] = recFileUrls[1];
                    }
                    if (this.fileUrls[2] != null && recFileUrls[2] != null) {
                        this.fileUrls[2] = recFileUrls[2];
                    }
                }
            }
            if (fileCount > 0) {
                if (fileUrls != null) {
                    if (fileUrls[0] != null && !fileUrls[0].equals("")) {
                        Glide.with(MainActivity.this)
                                .load(fileUrls[0])
                                .override(92, 68)
                                .fitCenter()
                                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                .into(photo1);
                    } else { Glide.clear(photo1); }
                    if (fileUrls[1] != null && !fileUrls[1].equals("")) {
                        photo2 = (ImageView) findViewById(R.id.ruvPic2);
                        Glide.with(MainActivity.this)
                                .load(fileUrls[1])
                                .override(92, 68)
                                .fitCenter()
                                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                .into(photo2);
                    } else { Glide.clear(photo2); }
                    if (fileUrls[2] != null && !fileUrls[2].equals("")) {
                        photo3 = (ImageView) findViewById(R.id.ruvPic3);
                        Glide.with(MainActivity.this)
                                .load(fileUrls[2])
                                .override(92, 68)
                                .fitCenter()
                                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                .into(photo3);
                    } else { Glide.clear(photo3); }
                }
            }

            String finalPrice = "$" + MainActivity.this.price.toString();
            MainActivity.this.currentPrice.setText(finalPrice);

            if (editFrag != null) {
                editFrag.dismiss();
                getFragmentManager().beginTransaction().remove(editFrag).commit();
            }
            putPrefsData();
            refreshUi();
            revealActivity();
            this.lastAction = CURATION_MODE;
        }
    }

    @Override
    public void imageFragInteraction(Bundle bundle, int request) {
        if (request == RUV_IMGEDIT_DELETE) {
            if (bundle.getStringArray("fileUrls") != null) {
                this.fileUrls = bundle.getStringArray("fileUrls");
            }
            this.fileCount = bundle.getInt("fileCount");
            this.editMode = bundle.getInt("editMode");
            refreshUi();
            editDialog();
        }
    }

    @Override
    public boolean handleMessage(Message inputMessage) {
        if (inputMessage.getData().getString("RuuvResponse") != null) {
            String response = inputMessage.getData().getString("RuuvResponse");
            Log.d(TAG, response);
            if (response.equals("Error")) {
                Snackbar.make(MainActivity.this.findViewById(R.id.MainParentView), "Error submitting quote", Snackbar.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject returnedJson = new JSONObject(response);
                    if (returnedJson.has("Roof")) {
                        JSONObject ruuvJson = new JSONObject(returnedJson.getString("Roof"));
                        if (ruuvJson.getString("id") != null) {
                            this.currentRid = Integer.valueOf(ruuvJson.getString("id"));
                        }
                        Toast.makeText(MainActivity.this, "Created Roof:: " + returnedJson.getString("Roof"), Toast.LENGTH_SHORT).show();
                    }
                    if (returnedJson.has("File")) {
                        Toast.makeText(MainActivity.this, "Created File:: " + returnedJson.getString("File"), Toast.LENGTH_SHORT).show();

                        JSONObject fileJson = new JSONObject(returnedJson.getString("File"));

                        if (fileJson.has("id")) {
                            if (ruvFiles.get(fileJson.getString("filename")) != null && !ruvFiles.get(fileJson.getString("filename")).getComment().equals("")) {
                                Bundle cBundle = new Bundle();
                                cBundle.putString("comment_body", ruvFiles.get(fileJson.getString("filename")).getComment());
                                cBundle.putString("ruvfid", fileJson.getString("id"));
                                RuuvComment rComment = new RuuvComment(MainActivity.this, mHandler, baseUrl, this.authToken, cBundle);
                                Thread sendCommentThread = new Thread(rComment);
                                sendCommentThread.start();
                            }
                        }
                    }
                    if (returnedJson.has("Comment")) {
                        Toast.makeText(MainActivity.this, "Created Comment:: " + returnedJson.getString("Comment"), Toast.LENGTH_SHORT).show();
                    }

                    if (fileCount > 0) {
                        if (sendRuuvFiles() > 0) {
                            fileCount = 0;
                            fileUrls[0] = null;
                            fileUrls[1] = null;
                            fileUrls[2] = null;
                        }
                    }
                    if (commentCount > 0) {
//                        if(sendComments() > 0) {
//                            commentCount = 0;
//                            fileComments[0] = null;
//                            fileComments[1] = null;
//                            fileComments[2] = null;
//                        }
                    }
                    clearValues();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        return false;
    }

    private int sendRuuvFiles() {
        int sent = 0;
        for (String url : fileUrls) {
            if (url != null) {
                RuuvFile rFile = new RuuvFile(MainActivity.this, mHandler, baseUrl, this.authToken, url, currentRid);
                Thread sendFileThread = new Thread(rFile);
                sendFileThread.start();
                sent++;
            }
        }
        return sent;
    }

    private int sendRuuvComments() {
        int sent = 0;
        for (String comment : fileComments) {
            if (comment != null) {
                Bundle mBundle = new Bundle();

                sent++;
            }
        }
        return sent;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApi.connect();
        AppIndex.AppIndexApi.start(mGoogleApi, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        putPrefsData();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApi.disconnect();
        AppIndex.AppIndexApi.end(mGoogleApi, getIndexApiAction());
        mGoogleApi.disconnect();
    }

    public boolean isEditing() {
        return this.editing;
    }

    public String[] getPrefCreds()  {
        String[] creds = new String[2];
        creds[0] = prefs.getString("email", "0");
        creds[1] = prefs.getString("pass", "0");

        return creds;
    }

    public void putPrefsData() {
        updateValues();
        SharedPreferences.Editor prefEdit = MainActivity.this.getSharedPreferences("RuviuzApp", Context.MODE_PRIVATE).edit();
        prefEdit.putString("address", address);
        prefEdit.putString("postal", postal);
        prefEdit.putString("city", city);
        prefEdit.putString("region", region);
        prefEdit.putString("price", String.valueOf(price));
        prefEdit.putFloat("width", width);
        prefEdit.putFloat("length", length);
        prefEdit.putFloat("slope", slope);
        prefEdit.putBoolean("premium", premium);
        prefEdit.putInt("currentRid", currentRid);
        prefEdit.putString("fileUrl1", fileUrls[0]);
        prefEdit.putString("fileUrl2", fileUrls[1]);
        prefEdit.putString("fileUrl3", fileUrls[2]);
        prefEdit.putString("fileComment1", fileComments[0]);
        prefEdit.putString("fileComment2", fileComments[1]);
        prefEdit.putString("fileComment3", fileComments[2]);
        if (mCustomer != null) {
            try {
                JSONObject customerJson = new JSONObject();
                customerJson.put("firstName", mCustomer.getFirstname());
                customerJson.put("lastName", mCustomer.getLastname());
                customerJson.put("email", mCustomer.getEmail());
                customerJson.put("phone", mCustomer.getPhone());
                customerJson.put("married", mCustomer.getMarried());
                customerJson.put("prefix", mCustomer.getPrefix());
                prefEdit.putString("customer", customerJson.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        prefEdit.commit();
    }


    public void getPrefsData() {
        SharedPreferences mPrefs = MainActivity.this.getSharedPreferences("RuviuzApp", Context.MODE_PRIVATE);
        this.address = mPrefs.getString("address", "");
        this.postal = mPrefs.getString("postal", "");
        this.city = mPrefs.getString("city","");
        this.region = mPrefs.getString("region", "");
        this.price = new BigDecimal(mPrefs.getString("price", "0"));
        this.width = mPrefs.getFloat("width", 0f);
        this.length = mPrefs.getFloat("length", 0f);
        this.slope = mPrefs.getFloat("slope", 0f);
        this.premium = mPrefs.getBoolean("premium", false);
        this.currentRid = mPrefs.getInt("currentRid", 0);

        if (this.fileUrls == null) {
            this.fileUrls = new String[3];
        }
        if (this.ruvFiles == null) {
            this.ruvFiles = new HashMap<>();
        }
        fileUrls[0] = mPrefs.getString("fileUrl1", "");
        fileUrls[1] = mPrefs.getString("fileUrl2", "");
        fileUrls[2] = mPrefs.getString("fileUrl3", "");
        fileComments[0] = mPrefs.getString("fileComment1", "");
        fileComments[1] = mPrefs.getString("fileComment2", "");
        fileComments[2] = mPrefs.getString("fileComment3", "");

        for (int i = 0; i < 3; i++) {
            if (!fileUrls[i].equals("") && !fileComments[i].equals("")) {
                RuvFileInfo rFile = new RuvFileInfo();
                rFile.setUrl(fileUrls[i]);
                rFile.setComment(fileComments[i]);
                ruvFiles.put(fileUrls[i], rFile);
            }
        }
        try {
            JSONObject customerJson = new JSONObject(mPrefs.getString("customer", ""));
            if (this.mCustomer == null) this.mCustomer = new Customer();
            this.mCustomer.setFirstname(customerJson.get("firstName").toString());
            this.mCustomer.setLastname(customerJson.get("lastName").toString());
            this.mCustomer.setEmail(customerJson.get("email").toString());
            this.mCustomer.setPhone(customerJson.get("phone").toString());
            this.mCustomer.setMarried(Boolean.valueOf(customerJson.get("married").toString()));
            this.mCustomer.setPrefix(customerJson.get("prefix").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateValues() {
        this.width = Float.parseFloat(String.valueOf(roofWidth.getText()));
        this.length = Float.parseFloat(String.valueOf(roofLength.getText()));
        this.slope = Float.parseFloat(String.valueOf(roofSlope.getText()));
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
        this.mCustomer = null;

        putPrefsData();

//        premiumMaterial.setChecked(false);
        roofLength.setText("0");
        roofWidth.setText("0");
        roofSlope.setText("0");
        currentPrice.setText(R.string.zero);
        Glide.clear(photo1); Glide.clear(photo2); Glide.clear(photo3);
//          addressBtn.setAlpha(0.2f);
//        draftBtn.setAlpha(0.2f);
//        draftBtn.setText("Draft");

        if (getIntent().hasExtra("uri"))
            getIntent().removeExtra("uri");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ruviuz_menu, menu);
        updateMenuWithIcon(menu.findItem(R.id.loginAction), Color.WHITE);
        updateMenuWithIcon(menu.findItem(R.id.geoLocate), Color.WHITE);
        updateMenuWithIcon(menu.findItem(R.id.goHome), Color.WHITE);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, item.toString());
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                break;
            case R.id.roofView:
                Intent rviewIntent = new Intent(this, RviewActivity.class);
                putIntentData(rviewIntent);
                rviewIntent.putExtra("baseUrl", MainActivity.baseUrl);
                putPrefsData();
                setResult(RUVIUZ_DATA_PERSIST, rviewIntent);
                this.startActivityForResult(rviewIntent, RUVIUZ_DATA_PERSIST);
                break;
            case R.id.loginAction:
                Log.d(TAG, "Login action!!");
                putPrefsData();
                loginDialog();
                break;
            case R.id.geoLocate:
                Log.d(TAG, "GEOLOCATION REQUEST");
                getGeoLocation();
                break;
            case R.id.goHome:
                Log.d(TAG, "Going HOME");
                putPrefsData();
                dismissAllDialogs();
                hideActivity();
                welcomeDialog();
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    public static void sendViewToBack(final View child) {
        final ViewGroup parent = (ViewGroup)child.getParent();
        if (null != parent) {
            parent.removeView(child);
            parent.addView(child, 0);
        }
    }

    public boolean getGeoLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        rLocation = new RuvLocation(MainActivity.this, new RuvLocation.GeoListener() {
            @Override
            public void sendLocation(Location location) {
                Log.d(TAG, "Getting Location\n" + location.toString());
                Toast.makeText(MainActivity.this, location.toString(), Toast.LENGTH_SHORT).show();

                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    for (Address address : addresses) {
                        Log.d(TAG, "Address => " + address.toString());
                        String locality = address.getLocality();
                        Log.d(TAG, "Locality = " + locality);
                        String addressLine1 = null;
                        if (address.getAddressLine(1) != null) {
                            addressLine1 = address.getAddressLine(1).contains(",") ? address.getAddressLine(1).substring(0, address.getAddressLine(1).indexOf(",")) : address.getAddressLine(1);
                            Log.d(TAG, "AddressLn1 = " + addressLine1);
                        } else if (address.getAddressLine(0) != null && address.getAdminArea() == null) {
                            String country = "COUNTRY::" + address.getAddressLine(0);
                            MainActivity.this.city = "NoLocality";
                            MainActivity.this.region = country;
                        } else {
                            Snackbar.make(findViewById(R.id.MainParentView), "Unable to get GeoLocation", Snackbar.LENGTH_SHORT).show();
                        }
//                        if (MainActivity.this.city == null || MainActivity.this.city.trim().equals(""))
                        MainActivity.this.city = locality == null ? addressLine1 : locality;
//                        if (MainActivity.this.region == null || MainActivity.this.region.trim().equals(""))
                        MainActivity.this.region = address.getAdminArea() == null ? "NoRegion" : RuvLocation.provinceMap.get(address.getAdminArea());
                    }
                    MainActivity.this.putPrefsData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, locationManager);

        if (rLocation.init()) {
            rLocation.getLocation();
            return true;
        }

        return false;
    }


    public String[] getAddress() {
        String[] mAddress = new String[2];
        mAddress[0] = city;
        mAddress[1] = region;

        return mAddress;
    }

    public boolean readyStatus() {
        return this.ready;
    }


    static class IncomingHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        IncomingHandler(MainActivity mActivity) {
            this.mActivity = new WeakReference<MainActivity>(mActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity mainActivity = mActivity.get();
            if (mainActivity != null) {
                mainActivity.handleMessage(msg);
            }
        }
    }

    private void draftCheck() {
        Log.d(TAG,"draftCheck");
        if ( MainActivity.this.address != null &&
             MainActivity.this.postal != null &&
             MainActivity.this.city  != null &&
             MainActivity.this.region != null  &&
             width > 0 && slope > 0 && length > 0 &&
             !MainActivity.this.address.equals("") &&
                !MainActivity.this.postal.equals("") &&
                !MainActivity.this.city.equals("") &&
                !MainActivity.this.region.equals("")) {
//            draftBtn.setText(R.string.ready);
//            draftBtn.setAlpha(1f);
            MainActivity.this.ready = true;
        }
    }

    /**
     * Updates a menu item in the dropdown to show it's icon that was declared in XML.
     *
     * @param item
     *         the item to update
     * @param color
     *         the color to tint with
     */
    public static void updateMenuWithIcon(@NonNull final MenuItem item, final int color) {
        SpannableStringBuilder builder = new SpannableStringBuilder()
                .append("*") // the * will be replaced with the icon via ImageSpan
//                .append("    ") // This extra space acts as padding. Adjust as you wish
                .append(item.getTitle());

        // Retrieve the icon that was declared in XML and assigned during inflation
        if (item.getIcon() != null && item.getIcon().getConstantState() != null) {
            Drawable drawable = item.getIcon().getConstantState().newDrawable();

            // Mutate this drawable so the tint only applies here
            drawable.mutate().setTint(color);

            // Needs bounds, or else it won't show up (doesn't know how big to be)
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            ImageSpan imageSpan = new ImageSpan(drawable);
            builder.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            item.setTitle(builder);
        }
    }
    
    public void hideActivity() {
        if (MainActivity.this.findViewById(R.id.MainParentView) != null) {
            MainActivity.this.getWindow().getDecorView().getRootView().setBackgroundColor(Color.BLACK);
            MainActivity.this.findViewById(R.id.MainParentView).setVisibility(View.INVISIBLE);
            MainActivity.this.findViewById(R.id.side_menu).setVisibility(View.INVISIBLE);
        }
        if (roofLength != null)
        roofLength.setVisibility(View.INVISIBLE);
        if (roofWidth != null)
        roofWidth.setVisibility(View.INVISIBLE);
        if (roofSlope != null)
        roofSlope.setVisibility(View.INVISIBLE);
        if (currentPrice != null)
        currentPrice.setVisibility(View.INVISIBLE);
//        if (addressBtn != null)
//        addressBtn.setVisibility(View.INVISIBLE);
//        if (metricBtn != null)
//        metricBtn.setVisibility(View.INVISIBLE);
//        if (draftBtn != null)
//        draftBtn.setVisibility(View.INVISIBLE);
        if (ruuvBtn != null)
        ruuvBtn.setVisibility(View.INVISIBLE);
        if (editBtn != null)
        editBtn.setVisibility(View.INVISIBLE);
        if (calculateBtn != null)
        calculateBtn.setVisibility(View.INVISIBLE);
        if (roofLength != null)
        roofLength.setVisibility(View.INVISIBLE);
        if (roofWidth != null)
            roofWidth.setVisibility(View.INVISIBLE);
        if (roofSlope != null)
            roofSlope.setVisibility(View.INVISIBLE);
        if (materialTv != null)
            materialTv.setVisibility(View.INVISIBLE);
    }

    
    public void revealActivity() {
        MainActivity.this.findViewById(R.id.MainParentView).setVisibility(View.VISIBLE);
        MainActivity.this.findViewById(R.id.side_menu).setVisibility(View.VISIBLE);
        roofLength.setVisibility(View.VISIBLE);
        roofWidth.setVisibility(View.VISIBLE);
        roofSlope.setVisibility(View.VISIBLE);
        currentPrice.setVisibility(View.VISIBLE);
        if (currentPrice != null)
            currentPrice.setVisibility(View.VISIBLE);
//        if (addressBtn != null)
//            addressBtn.setVisibility(View.VISIBLE);
//        if (metricBtn != null)
//            metricBtn.setVisibility(View.VISIBLE);
//        if (draftBtn != null)
//            draftBtn.setVisibility(View.VISIBLE);
        if (ruuvBtn != null)
            ruuvBtn.setVisibility(View.VISIBLE);
        if (editBtn != null)
            editBtn.setVisibility(View.VISIBLE);
        if (calculateBtn != null)
            calculateBtn.setVisibility(View.VISIBLE);
        if (roofLength != null)
            roofLength.setVisibility(View.VISIBLE);
        if (roofWidth != null)
            roofWidth.setVisibility(View.VISIBLE);
        if (roofSlope != null)
            roofSlope.setVisibility(View.VISIBLE);
        if (materialTv != null)
            materialTv.setVisibility(View.VISIBLE);
    }

    public void putIntentData(Intent intent) {
        updateValues();
        intent.putExtra("authToken", this.authToken);
        intent.putExtra("slope", this.slope);
        intent.putExtra("width", this.width);
        intent.putExtra("length", this.length);
        intent.putExtra("address", this.address);
        intent.putExtra("postal", this.postal);
        intent.putExtra("city", this.city);
        intent.putExtra("region", this.region);
        intent.putExtra("premium", this.premium);
        intent.putExtra("currentRid", this.currentRid);
        intent.putExtra("fileCount", this.fileCount);
        intent.putExtra("fileUrls", this.fileUrls);
        intent.putExtra("baseUrl", MainActivity.baseUrl);
        intent.putExtra("editing", MainActivity.this.isEditing());
        this.ready = intent.getBooleanExtra("ready", false);
        if (mCustomer != null) {
            try {
                JSONObject customerJson = new JSONObject();
                customerJson.put("firstName", mCustomer.getFirstname());
                customerJson.put("lastName", mCustomer.getLastname());
                customerJson.put("email", mCustomer.getEmail());
                customerJson.put("phone", mCustomer.getPhone());
                customerJson.put("prefix", mCustomer.getPrefix());
                customerJson.put("married", mCustomer.getMarried());
                intent.putExtra("customer", customerJson.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void getIntentData(Intent intent) {
        Bundle extras = intent.getExtras();
        this.ready = intent.getBooleanExtra("ready", false);
        this.authToken = intent.getStringExtra("authToken");
        this.slope = intent.getFloatExtra("slope", 0);
        this.width = intent.getFloatExtra("width", 0);
        this.length = intent.getFloatExtra("length", 0);
        this.address = intent.getStringExtra("address");
        this.postal = intent.getStringExtra("postal");
        this.city= intent.getStringExtra("city");
        this.region= intent.getStringExtra("region");
        this.premium = intent.getBooleanExtra("premium", false);
        this.currentRid = intent.getIntExtra("currentRid", -1);
        this.fileCount = intent.getIntExtra("fileCount", 0);
        this.fileUrls = intent.getStringArrayExtra("fileUrls");
        this.fileComments = intent.getStringArrayExtra("fileComments");
        this.editing = intent.getBooleanExtra("editing", false);
        if (intent.hasExtra("customer"))
            try {
                JSONObject customerJson = new JSONObject(intent.getStringExtra("customer"));
                if (this.mCustomer == null) this.mCustomer = new Customer();
                this.mCustomer.setFirstname(customerJson.get("firstName").toString());
                this.mCustomer.setLastname(customerJson.get("lastName").toString());
                this.mCustomer.setEmail(customerJson.get("email").toString());
                this.mCustomer.setPhone(customerJson.get("phone").toString());
                this.mCustomer.setPrefix(customerJson.get("prefix").toString());
                this.mCustomer.setMarried(Boolean.valueOf(customerJson.get("married").toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        if (intent.hasExtra("REQUEST")) {
            handleRequest(extras.getInt("REQUEST", 0));
        }
    }


    public void putBundleData(Bundle bundle) {
        bundle.putString("authToken", this.authToken);
        bundle.putFloat("slope", this.slope);
        bundle.putFloat("width", this.width);
        bundle.putFloat("length", this.length);
        bundle.putString("address", this.address);
        bundle.putString("postal", this.postal);
        bundle.putBoolean("premium", this.premium);
        bundle.putInt("currentRid", this.currentRid);
//        bundle.putExtra("fileCount", this.fileCount);
//        bundle.putExtra("fileUrls", this.fileUrls);
    }

    public void handleRequest(int request) {
        Log.d(TAG, "Rview REQUEST:: " + request);
        if (request == WELCOME_REQUEST) {
            welcomeDialog();
        }
        if (request == LOGIN_REQUEST) {
            welcomeDialog();
        }
        if (request == CLEAR_REQUEST) {
            clearValues();
        }
        if (request == ADDRESS_REQUEST) {
            addressDialog();
        }
        if (request == CUSTOMER_REQUEST) {
            customerDialog();
        }
        if (request == CAMERA_REQUEST) {
            if (checkCameraHardware(MainActivity.this)) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                putIntentData(intent);
                intent.putExtra("authToken", authToken);
                intent.putExtra("callingClass", MainActivity.this.getClass().getSimpleName());
                putPrefsData();
                setResult(RUVIUZ_DATA_PERSIST);
                startActivity(intent);
            } else {
                Log.d(TAG, "No Camera Hardware on Device");
            }
        }
        if (request == METRIC_REQUEST) {
            getMetric();
        }
        if (request == GEOLOCATION_REQUEST) {
            getGeoLocation();
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
        switch (requestCode) {
            case RUVIUZ_DATA_PERSIST:
                if (resultCode == RESULT_OK) {
                    getIntentData(data);
                }
                break;
            case METRICFRAG_COMPLETE:
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "METRICFRAG_COMPLETE");
                    if (metricFrag != null && metricFrag.isAdded()) {
                        metricFrag.dismiss();
                    }
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(this, CameraActivity.class);
                    putPrefsData();
                    intent.putExtra("authToken", authToken);
                    intent.putExtra("callingClass", MainActivity.this.getClass().getSimpleName());
                    putIntentData(intent);
                    setResult(RUVIUZ_DATA_PERSIST);
//                    startActivityForResult(intent, RUVIUZ_DATA_PERSIST);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the CAMERA", Toast.LENGTH_SHORT).show();
                }
                return;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void refreshUi() {
        if (fileCount == 0) {
            Glide.clear(this.photo1); Glide.clear(this.photo2); Glide.clear(this.photo3);
        } else {
            if (fileCount > 0) {
                if (fileUrls != null) {
                    if (fileUrls[0] != null && photo1.getDrawable() == null) {
                        Glide.with(MainActivity.this)
                                .load(fileUrls[0])
                                .override(92, 68)
                                .fitCenter()
                                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                .into(photo1);
                    }
                    if (fileUrls[1] != null && photo2.getDrawable() == null) {
                        photo2 = (ImageView) findViewById(R.id.ruvPic2);
                        Glide.with(MainActivity.this)
                                .load(fileUrls[1])
                                .override(92, 68)
                                .fitCenter()
                                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                .into(photo2);
                    }
                    if (fileUrls[2] != null && photo3.getDrawable() == null) {
                        photo3 = (ImageView) findViewById(R.id.ruvPic3);
                        Glide.with(MainActivity.this)
                                .load(fileUrls[2])
                                .override(92, 68)
                                .fitCenter()
                                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                .into(photo3);
                    }
                }
            }
        }
    }

    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }


    /**
     * Dismiss all DialogFragments added to given FragmentManager and child fragments
     */
    //TODO still not working consistently
    public void dismissAllDialogs() {
        if (mainFragment != null && mainFragment.isAdded()) mainFragment.dismiss();
        if (mLoginFrag != null && mLoginFrag.isAdded()) mLoginFrag.dismiss();
        if (mCustomerFrag != null && mCustomerFrag.isAdded()) mCustomerFrag.dismiss();
        if (mAddressFrag != null && mAddressFrag.isAdded()) mAddressFrag.dismiss();
        if (metricFrag != null && metricFrag.isAdded()) metricFrag.dismiss();
        if (slopeFrag != null && slopeFrag.isAdded()) slopeFrag.dismiss();
        if (editFrag != null && editFrag.isAdded()) editFrag.dismiss();
    }

    public void dismissOtherDialogs(Class mClass) {
        if (mainFragment != null && mainFragment.isAdded() && mainFragment.getClass() != mClass) mainFragment.dismiss();
        if (mLoginFrag != null && mLoginFrag.isAdded() && mLoginFrag.getClass() != mClass) mLoginFrag.dismiss();
        if (mCustomerFrag != null && mCustomerFrag.isAdded() && mCustomerFrag.getClass() != mClass) mCustomerFrag.dismiss();
        if (mAddressFrag != null && mAddressFrag.isAdded() && mAddressFrag.getClass() != mClass) mAddressFrag.dismiss();
        if (metricFrag != null && metricFrag.isAdded() && metricFrag.getClass() != mClass) metricFrag.dismiss();
        if (slopeFrag != null && slopeFrag.isAdded() && slopeFrag.getClass() != mClass) slopeFrag.dismiss();
        if (editFrag != null && editFrag.isAdded() && editFrag.getClass() != mClass) editFrag.dismiss();
    }

    public static int getStatusBarHeight(Activity a) {
        int result = 0;
        int resourceId = a.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = a.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private static class RuuvThread implements Runnable {
        private Handler mHandler;
        private WeakReference<MainActivity> mReference;
        private Activity mActivity;

        private String baseUrl, authToken;

        private Bundle mBundle;

        private RuuvThread(MainActivity mActivity, Handler mHandler, String baseUrl, String authToken, Bundle mBundle) {
            this.mReference = new WeakReference<>(mActivity);
            this.mHandler = mHandler;
            this.baseUrl = baseUrl;
            this.authToken = authToken;
            this.mBundle = mBundle;
            this.mActivity = mActivity;
        }

        @Override
        public void run() {
            sendRuuv();
        }

        private boolean sendRuuv() {
            String endpoint = baseUrl + "/roof/add";
            JSONObject ruuvJson = new JSONObject();

            if (mBundle != null) {

                try {
//                    String mAddress = mBundle.getString("address") + "\n" + mBundle.getString("postal");
                    ruuvJson.put("address", mBundle.getString("address"));
                    ruuvJson.put("postal", mBundle.getString("postal"));
                    ruuvJson.put("city", mBundle.getString("city"));
                    ruuvJson.put("region", mBundle.getString("region"));
                    ruuvJson.put("width", mBundle.getFloat("width"));
                    ruuvJson.put("length", mBundle.getFloat("length"));
                    ruuvJson.put("slope", mBundle.getFloat("slope"));
                    ruuvJson.put("price", mBundle.getString("price"));
                    ruuvJson.put("firstName", mBundle.getString("firstName"));
                    ruuvJson.put("lastName", mBundle.getString("lastName"));
                    ruuvJson.put("email", mBundle.getString("email"));
                    ruuvJson.put("phone", mBundle.getString("phone"));
                    ruuvJson.put("prefix", mBundle.getString("prefix"));
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
            }
            return false;
        }
    }
}