package stronglogic.ruviuz;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
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
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
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
import java.util.List;
import java.util.Locale;

import stronglogic.ruviuz.content.RuvMenuItem;
import stronglogic.ruviuz.fragments.AddressFragment;
import stronglogic.ruviuz.fragments.CustomerFragment;
import stronglogic.ruviuz.fragments.LoginFragment;
import stronglogic.ruviuz.fragments.MetricFragment;
import stronglogic.ruviuz.util.RuuvFile;
import stronglogic.ruviuz.util.RuvLocation;
import stronglogic.ruviuz.views.RuvMenuAdapter;

import static android.R.drawable.ic_menu_camera;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginFragListener, AddressFragment.AddressFragListener, MetricFragment.OnFragmentInteractionListener, CustomerFragment.OnFragmentInteractionListener, Handler.Callback {

    private static final int CAMERA_PERMISSION = 6;
    private static final int RUVIUZ_DATA_PERSIST = 14;
    private static final int METRICFRAG_COMPLETE = 21;

    private android.support.v7.widget.Toolbar mToolbar;

    private FragmentManager fm;

    private LoginFragment mLoginFrag;
    private AddressFragment mAddressFrag;
    private MetricFragment metricFrag;
    private CustomerFragment mCustomerFrag;

    private OrientationEventListener mListener;

    private NumberPicker roofLength, roofWidth, roofSlope;
    private Switch isFlat, premiumMaterial;
    private TextView orientationText;
    private ImageView photo1, photo2, photo3;
    private ImageButton photoBtn;
    private Button ruuvBtn, addressBtn, clearBtn, metricBtn;

    private static final String TAG = "Ruviuz";
//    private static final String baseUrl = "http://10.0.2.2:5000";
    private static final String baseUrl = "http://52.43.250.94:5000";

    private String authToken;

    private IncomingHandler mHandler;

    private SharedPreferences prefs;

    private RuvLocation rLocation;

    private Geocoder geocoder;

    int fileCount, currentRid;
    float width, length, slope;
    BigDecimal price;
    String address, postal, city, province;
    String[] fileUrls = new String[3];
    boolean premium;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mGoogleApi;


    private RuvMenuItem[] ruuvMenuItems;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Log.d(TAG, "This device has the following screen size: \n" + String.valueOf(size.x) + "x\n" + String.valueOf(size.y) + "y");

        mToolbar = (Toolbar) findViewById(R.id.app_bar);

        if (getIntent() != null) {
            getIntentData(getIntent());
        }

        this.prefs = MainActivity.this.getSharedPreferences("RuviuzApp", Context.MODE_PRIVATE);

        getPrefsData();

        setSupportActionBar(mToolbar);

        if (getSupportActionBar()!= null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.rlogo);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(8f);
        }

        if (checkCameraHardware(this)) {
            photoBtn = (ImageButton) findViewById(R.id.takePhoto);
            photoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
                    } else {
                        if (checkCameraHardware(MainActivity.this)) {
                            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                            putIntentData(intent);
                            intent.putExtra("authToken", authToken);
                            intent.putExtra("callingClass", MainActivity.this.getClass().getSimpleName());
                            putPrefsData();
                            setResult(RUVIUZ_DATA_PERSIST);
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

//        if (mGoogleApi == null) {
//            mGoogleApi = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
//        }

        roofLength = (NumberPicker) findViewById(R.id.roofLength);
        roofWidth = (NumberPicker) findViewById(R.id.roofWidth);
        roofSlope = (NumberPicker) findViewById(R.id.roofSlope);

        //set Picker ranges
        roofLength.setMinValue(0);
        roofLength.setMaxValue(500);
        roofLength.setValue(Math.round(length));
        if (prefs.contains("length")) {
            roofLength.setValue(Math.round(prefs.getFloat("length", 0f)));
        }

        roofLength.setWrapSelectorWheel(true);
        roofLength.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.d(TAG, "Length set to " + String.valueOf(newVal));
            }
        });

        roofWidth.setMinValue(0);
        roofWidth.setMaxValue(500);
        roofWidth.setValue(Math.round(width));
        roofWidth.setWrapSelectorWheel(true);
        roofSlope.setMinValue(0);
        roofSlope.setMaxValue(180);
        roofSlope.setValue(Math.round(slope));
        roofSlope.setWrapSelectorWheel(true);

        isFlat = (Switch) findViewById(R.id.roofFlat);
        premiumMaterial = (Switch) findViewById(R.id.premiumMaterial);
        premiumMaterial.setChecked(premium);

        premiumMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setPremium(isChecked);
            }
        });

        orientationText = (TextView) findViewById(R.id.orientationText);

        mListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_UI) {
            @Override
            public void onOrientationChanged(int orientation) {
                orientationText.setText(String.valueOf(orientation));
            }
        };

        mHandler = new IncomingHandler(this);

        if (authToken == null) {
            loginDialog();
        }

        photo1 = (ImageView) findViewById(R.id.ruvPic1);
        photo2 = (ImageView) findViewById(R.id.ruvPic2);
        photo3 = (ImageView) findViewById(R.id.ruvPic3);

        ruuvBtn = (Button) findViewById(R.id.ruuvSubmitBtn);

        ruuvBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (address == null) {
                    addressDialog();
                } else {
                    if (roofLength.getValue() > -1 &&
                            roofSlope != null &&
                            roofWidth.getValue() > -1 &&
                            address != null) {

                        slope = roofSlope.getValue();
                        length = roofLength.getValue();
                        width = roofWidth.getValue();

                        Bundle mBundle = new Bundle();
                        mBundle.putString("address", address);
                        mBundle.putString("postal", postal);
                        mBundle.putString("city", city);
                        mBundle.putString("region", province);
                        mBundle.putString("price", calculatePrice().toString());
                        mBundle.putFloat("width", roofWidth.getValue());
                        mBundle.putFloat("length", roofLength.getValue());
                        mBundle.putFloat("slope", roofSlope.getValue());
                        RuuvThread ruuvThread = new RuuvThread((MainActivity) getParent(), mHandler, baseUrl, authToken, mBundle);
                        Thread submitThread = new Thread(ruuvThread);
                        submitThread.start();
                    }
                }
            }
        });

        addressBtn = (Button) findViewById(R.id.addressGetDialog);
        addressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressDialog();
            }
        });

        if (!address.equals("")) {
            addressBtn.setAlpha(1f);
        }

        metricBtn = (Button) findViewById(R.id.metricfragOpen);

        metricBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMetric();
            }
        });

        clearBtn = (Button) findViewById(R.id.clearBtn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearValues();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApi = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        String[] menuItemsArray = getResources().getStringArray(R.array.ruvitems);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.side_menu);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        makeSideMenu(mDrawerLayout, mDrawerList, menuItemsArray);
    }


    public void makeSideMenu(final DrawerLayout mDrawerLayout, ListView mDrawerList, String[] mItems) {
        ruuvMenuItems = new RuvMenuItem[mItems.length];

        if (mItems.length > 8) {
                String[] actionNames = getResources().getStringArray(R.array.actionnames);
                ruuvMenuItems[0] = new RuvMenuItem(R.drawable.rooflist, mItems[0], RuvMenuItem.type.ACTIVITY, actionNames[0]);
                ruuvMenuItems[1] = new RuvMenuItem(R.drawable.metric, mItems[1], RuvMenuItem.type.FRAGMENT, actionNames[1]);
                ruuvMenuItems[2] = new RuvMenuItem(R.drawable.customer, mItems[2], RuvMenuItem.type.FRAGMENT,actionNames[2]);
                ruuvMenuItems[3] = new RuvMenuItem(R.drawable.address, mItems[3], RuvMenuItem.type.FRAGMENT, actionNames[3]);
                ruuvMenuItems[4] = new RuvMenuItem(R.drawable.geolocate, mItems[4], RuvMenuItem.type.FRAGMENT, actionNames[4]);
                ruuvMenuItems[5] = new RuvMenuItem(R.drawable.getimg, mItems[5], RuvMenuItem.type.INTENT_ACTION, actionNames[5]);
                ruuvMenuItems[6] = new RuvMenuItem(ic_menu_camera, mItems[6], RuvMenuItem.type.ACTIVITY, actionNames[6]);
                ruuvMenuItems[7] = new RuvMenuItem(R.drawable.destroy, mItems[7], RuvMenuItem.type.INTENT_ACTION, actionNames[7]);
                ruuvMenuItems[8] = new RuvMenuItem(R.drawable.login2, mItems[8], RuvMenuItem.type.FRAGMENT, actionNames[8]);
                ruuvMenuItems[9] = new RuvMenuItem(R.drawable.logout, mItems[9], RuvMenuItem.type.INTENT_ACTION, actionNames[9]);
        }

        // Set the adapter for the list view
        mDrawerList.setAdapter(new RuvMenuAdapter(this,
                R.layout.drawer_list_item, ruuvMenuItems));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, String.valueOf(ruuvMenuItems[position]));
            }
        });

        MainActivity.this.getClass().get

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mToolbar, R.string.OpenDrawer, R.string.CloseDrawer) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if (mToolbar != null) mToolbar.setTitle("Ruviuz");

                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (mToolbar != null) mToolbar.setTitle("Choose Your Destiny.");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                mDrawerLayout.bringChildToFront(drawerView);
                mDrawerLayout.requestLayout();
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    mDrawerLayout.setBackgroundColor(Color.BLACK);
                    sendViewToBack(mDrawerLayout);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                    mDrawerLayout.setBackgroundColor(Color.TRANSPARENT);
                    mDrawerLayout.bringToFront();
                }
            }

        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        Log.d(TAG, "Drawer Open? => " + String.valueOf(drawerOpen));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.enable();

        if (getIntent() != null) {
            Intent xIntent = getIntent();
//            getIntentData(xIntent);

            if (xIntent.hasExtra("uri")) {
//                getIntentData(xIntent);
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
        }

        if (fileCount > 0) {
            if (fileUrls != null) {
                if (fileUrls[0] != null && photo1.getDrawable() == null) {
                    Glide.with(MainActivity.this)
                            .load(fileUrls[0])
                            .override(72, 54)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(photo1);
                }
                if (fileUrls[1] != null && photo2.getDrawable() == null) {
                    photo2 = (ImageView) findViewById(R.id.ruvPic2);
                    Glide.with(MainActivity.this)
                            .load(fileUrls[1])
                            .override(72, 54)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(photo2);
                }
                if (fileUrls[2] != null && photo3.getDrawable() == null) {
                    photo3 = (ImageView) findViewById(R.id.ruvPic3);
                    Glide.with(MainActivity.this)
                            .load(fileUrls[2])
                            .override(72, 54)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(photo3);
                }
            }
        }
        getPrefCreds();
    }


    @Override
    public void onPause() {
        super.onPause();
        mListener.disable();
        putPrefsData();
        if (rLocation != null) {
            rLocation = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        putPrefsData();
    }

    void setPremium(boolean checked) {
        this.premium = checked;
    }


    BigDecimal calculatePrice() {
        try {
            BigDecimal mPrice = new BigDecimal((length * width * (0.428 * slope)));
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


    public void loginDialog() {

        if (mLoginFrag == null) {
            mLoginFrag = new LoginFragment();
        }

        if (!mLoginFrag.isAdded()) {
            FragmentManager fm = getFragmentManager();

            Bundle args = new Bundle();
            args.putString("baseUrl", baseUrl);
            if (getPrefCreds() != null) {
                args.putString("email", prefs.getString("email", "Email"));
                args.putString("password", prefs.getString("password", "Password"));
            }
            mLoginFrag.setArguments(args);
            mLoginFrag.show(fm, "Please Login");
        }
    }


    public static void addressDialog(FragmentManager fm, AddressFragment mAddressFrag) {
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
        FragmentManager fm = getFragmentManager();
        if (mCustomerFrag == null) {
            mCustomerFrag = new CustomerFragment();
            if (!mCustomerFrag .isAdded()) {
                mCustomerFrag.show(fm, "Please Enter Address");
            }
        }
        if (mCustomerFrag != null) {
            fm.beginTransaction().remove(mCustomerFrag ).commit();
            mCustomerFrag = null;
            mCustomerFrag = new CustomerFragment();
            mCustomerFrag.show(fm, "Please Enter Address");
        }
    }

    public void getMetric() {
        updateValues();
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


    @Override
    public void loginFragInteraction(String output) {
        Toast.makeText(this, output, Toast.LENGTH_SHORT).show();
        this.authToken = output;


        if (mLoginFrag != null) {
            mLoginFrag.dismiss();
            getFragmentManager().beginTransaction().remove(mLoginFrag).commit();
        }
    }


    @Override
    public void addressFragInteraction(String address, String postal, String city, String province) {
        this.address = address;
        this.postal = postal;
        this.city = city;
        this.province = province;
        Toast.makeText(this, this.address, Toast.LENGTH_SHORT).show();
        addressBtn.setAlpha(1f);

        if (mAddressFrag != null) {
            mAddressFrag.dismiss();
            getFragmentManager().beginTransaction().remove(mAddressFrag).commit();
        }
    }

    @Override
    public boolean handleMessage(Message inputMessage) {
        if (inputMessage.getData().getString("RuuvResponse") != null) {
            Log.d(TAG, inputMessage.getData().getString("RuuvResponse"));
            try {
                JSONObject returnedJson = new JSONObject(inputMessage.getData().getString("RuuvResponse"));
                if (returnedJson.has("Roof")) {
                    JSONObject ruuvJson = new JSONObject(returnedJson.getString("Roof"));
                    if (ruuvJson.getString("id") != null) {
                        this.currentRid = Integer.valueOf(ruuvJson.getString("id"));
                    }
                    Toast.makeText(MainActivity.this, "Created Roof:: " + returnedJson.getString("Roof"), Toast.LENGTH_SHORT).show();
                }
                if (returnedJson.has("File")) {
                    Toast.makeText(MainActivity.this, "Created File:: " + returnedJson.getString("File"), Toast.LENGTH_SHORT).show();
                }

                if (fileCount > 0) {
                    if (sendRuuvFiles() > 0) {
                        fileCount = 0;
                        fileUrls[0] = null;
                        fileUrls[1] = null;
                        fileUrls[2] = null;
                    }
                }
            clearValues();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
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

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApi.disconnect();
        AppIndex.AppIndexApi.end(mGoogleApi, getIndexApiAction());
        mGoogleApi.disconnect();
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
        prefEdit.putString("region", province);
        prefEdit.putString("price", String.valueOf(price));
        prefEdit.putFloat("width", width);
        prefEdit.putFloat("length", length);
        prefEdit.putFloat("slope", slope);
        prefEdit.putBoolean("premium", premium);
        prefEdit.putInt("currentRid", currentRid);;
        prefEdit.commit();
    }


    public void getPrefsData() {
        SharedPreferences mPrefs = MainActivity.this.getSharedPreferences("RuviuzApp", Context.MODE_PRIVATE);
        this.address = mPrefs.getString("address", "");
        this.postal = mPrefs.getString("postal", "");
        this.price = new BigDecimal(mPrefs.getString("price", "0"));
        this.width = mPrefs.getFloat("width", 0f);
        this.length = mPrefs.getFloat("length", 0f);
        this.slope = mPrefs.getFloat("slope", 0f);
        this.premium = mPrefs.getBoolean("premium", false);
        this.currentRid = mPrefs.getInt("currentRid", 0);
    }

    public void updateValues() {
        this.width = (float)roofWidth.getValue();
        this.length = (float)roofLength.getValue();
        this.slope = (float)roofSlope.getValue();
    }

    public void clearValues() {
        this.address = "";
        this.postal = "";
        this.city = "";
        this.province = "";
        this.price = new BigDecimal(0);
        this.width = 0f;
        this.length = 0f;
        this.slope = 0f;
        this.premium = false;
        this.currentRid = 0;
        this.fileCount = 0;

        putPrefsData();

        premiumMaterial.setChecked(false);
        roofLength.setValue(0);
        roofWidth.setValue(0);
        roofSlope.setValue(0);
        photo1.setImageDrawable(null);
        photo2.setImageDrawable(null);
        photo3.setImageDrawable(null);
        addressBtn.setAlpha(0.2f);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ruviuz_menu, menu);
        int color = ContextCompat.getColor(this, R.color.colorAccent);
        updateMenuWithIcon(menu.findItem(R.id.loginAction), color);
        updateMenuWithIcon(menu.findItem(R.id.geoLocate), color);
//        updateMenuWithIcon(menu.findItem(R.id.side_menu), color);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, item.getTitle().toString());
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
//                    sendViewToBack(mDrawerLayout);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
//                    mDrawerLayout.bringToFront();
                }
                break;
            case R.id.roofView:
                Intent rviewIntent = new Intent(this, RviewActivity.class);
                rviewIntent.putExtra("authToken", authToken);
                rviewIntent.putExtra("baseUrl", baseUrl);
                putPrefsData();
                setResult(RUVIUZ_DATA_PERSIST, rviewIntent);
                this.startActivityForResult(rviewIntent, RUVIUZ_DATA_PERSIST);
                break;
            case R.id.loginAction:
                Log.d(TAG, "Login action!!");
                loginDialog();
                break;
            case R.id.geoLocate:
                Log.d(TAG, "GEOLOCATION REQUEST");
                getGeoLocation();
                break;
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
                        String addressLine1 = address.getAddressLine(1).substring(0, address.getAddressLine(1).indexOf(","));
                        Log.d(TAG, "Locality = " + locality);
                        Log.d(TAG, "AddressLn1 = " + addressLine1);
                        MainActivity.this.city = locality == null ? addressLine1 : locality;
                        MainActivity.this.province = RuvLocation.provinceMap.get(address.getAdminArea());
                    }
                    MainActivity.this.putPrefsData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, locationManager);

        if (rLocation.init()) {
            rLocation.getLocation();
        }

        return false;
    }

    @Override
    public void metricfragInteraction(Float[] values, String data) {
        Log.d(TAG, data);
        if (data.equals("METRIC_SUCCESS")) {
            Log.d(TAG, "METRICFRAG_COMPLETE");
            this.length = values[0];
            roofLength.setValue(Math.round(values[0]));
            this.width = values[1];
            roofWidth.setValue(Math.round(values[1]));
            this.slope = values[2];
            roofSlope.setValue(Math.round(values[2]));

            if (metricFrag != null && metricFrag.isAdded()) {
                metricFrag.dismiss();
            }

            customerDialog();
        }
    }

    @Override
    public void customerfragInteraction(Uri uri) {
        Log.d(TAG, "CUSTOMERFRAGINTERACTION");
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

    /**
     * Updates a menu item in the dropdown to show it's icon that was declared in XML.
     *
     * @param item
     *         the item to update
     * @param color
     *         the color to tint with
     */
    private static void updateMenuWithIcon(@NonNull final MenuItem item, final int color) {
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


    public void putIntentData(Intent intent) {
        updateValues();
        intent.putExtra("authToken", this.authToken);
        intent.putExtra("slope", this.slope);
        intent.putExtra("width", this.width);
        intent.putExtra("length", this.length);
        intent.putExtra("address", this.address);
        intent.putExtra("postal", this.postal);
        intent.putExtra("city", this.city);
        intent.putExtra("region", this.province);
        intent.putExtra("premium", this.premium);
        intent.putExtra("currentRid", this.currentRid);
        intent.putExtra("fileCount", this.fileCount);
        intent.putExtra("fileUrls", this.fileUrls);

    }
    
    public void getIntentData(Intent intent) {
        if (!intent.hasExtra("fileCount")) {
            Log.d(TAG, "NO FILE COUNT LINE 715 MAIN");
        }
        this.authToken = intent.getStringExtra("authToken");
        this.slope = intent.getFloatExtra("slope", 0);
        this.width = intent.getFloatExtra("width", 0);
        this.length = intent.getFloatExtra("length", 0);
        this.address = intent.getStringExtra("address");
        this.postal = intent.getStringExtra("postal");
        this.city= intent.getStringExtra("city");
        this.province= intent.getStringExtra("region");
        this.premium = intent.getBooleanExtra("premium", false);
        this.currentRid = intent.getIntExtra("currentRid", -1);
        this.fileCount = intent.getIntExtra("fileCount", 0);
        this.fileUrls = intent.getStringArrayExtra("fileUrls");
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


    private static class RuuvThread implements Runnable {
        private Handler mHandler;
        private WeakReference<MainActivity> mReference;
        private Activity mActivity;

        private String baseUrl, authToken;

        private Bundle mBundle;

        private RuuvThread(MainActivity mActivity, Handler mHandler, String baseUrl, String authToken, Bundle mBundle) {
            this.mReference = new WeakReference<MainActivity>(mActivity);
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

                    response = bildr.toString();

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