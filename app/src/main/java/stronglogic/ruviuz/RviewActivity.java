package stronglogic.ruviuz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

import stronglogic.ruviuz.content.Customer;
import stronglogic.ruviuz.content.Roof;
import stronglogic.ruviuz.content.RuvFileInfo;
import stronglogic.ruviuz.fragments.RuvFragment;
import stronglogic.ruviuz.tasks.RviewTask;
import stronglogic.ruviuz.views.RuvAdapter;

import static stronglogic.ruviuz.MainActivity.getStatusBarHeight;
import static stronglogic.ruviuz.MainActivity.sendViewToBack;

public class RviewActivity extends AppCompatActivity implements RuvFragment.RuvFragListener {

    private static final String TAG = "RuviuzRVIEWACTIVITY";
    private static final int RUVIUZ_CAMERA = 15;
    private static final int WELCOME_REQUEST = 60;
    private static final int LOGIN_REQUEST = 61;
    private static final int CLEAR_REQUEST = 62;
    private static final int ADDRESS_REQUEST = 63;
    private static final int CUSTOMER_REQUEST = 64;
    private static final int METRIC_REQUEST = 65;
    private static final int CAMERA_REQUEST = 66;
    private static final int GEOLOCATION_REQUEST = 67;

    private android.support.v7.widget.Toolbar mToolbar;

    private String authToken;
    private String baseUrl;

    private int currentRid, fileCount, reopenDialog;
    private float slope, width, length;
    private boolean premium, ready;
    private String address, city, region, postal;
    private String[] fileUrls = new String[3];

    private Customer mCustomer;

    private ArrayList<Roof> roofArrayList;
    public RecyclerView rv;

    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rview);

        getPrefsData();

        Window w = getWindow();
        View decorView = w.getDecorView();
        // Show Status Bar.
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        RviewActivity.this.findViewById(R.id.activity_rview).setPadding(0, getStatusBarHeight(RviewActivity.this), 0, 0);

        w.setStatusBarColor(ContextCompat.getColor(RviewActivity.this, R.color.ruvGreenStatus));

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);

        setSupportActionBar(mToolbar);

        if (getSupportActionBar()!= null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.construction);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(8f);
        }

        Intent mIntent = getIntent();

        getIntentData(mIntent);

        if (mIntent.hasExtra("fromCamera") && mIntent.getBooleanExtra("fromCamera", false)) {
            if (mIntent.hasExtra("uri")) {
//                getIntentData(xIntent);
                if (fileCount + 1 == 4) {
                    Toast.makeText(RviewActivity.this, "You cannot add more photos", Toast.LENGTH_SHORT).show();
                } else {
                    this.fileCount++;
                    if (fileUrls == null) {
                        this.fileUrls = new String[3];
                    }
                    if (fileUrls[fileCount - 1] == null) {
                        fileUrls[fileCount - 1] = "";
                    }
                    String uriS = mIntent.getStringExtra("uri");
                    fileUrls[fileCount - 1] = uriS;
                    reopenDialog = currentRid;
                }
            }
        }

        roofArrayList = new ArrayList<>();

        RviewTask rviewTask = new RviewTask(authToken, baseUrl, new RviewTask.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                Log.d(TAG, output);

                if (parseData(output)) {
                    updateUi();
                    Log.d(TAG, "UpdatedUI");
                }
            }
        });
        rviewTask.execute();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.side_menu);
        mDrawerView = (NavigationView) findViewById(R.id.left_drawer);
        ActionBarDrawerToggle drawerToggle = setupDrawerToggle();
        mDrawerLayout.addDrawerListener(drawerToggle);
        mDrawerView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d(TAG, item.toString());
                switch (item.getTitle().toString()) {
                    case ("Roof List"):
                        Intent reloadIntent = new Intent(RviewActivity.this, IndexViewActivity.class);
                        putIntentData(reloadIntent);
                        startActivity(reloadIntent);
                        break;
                    case ("Take Photo"):
                        goToMain(CAMERA_REQUEST);
                        break;
                    case ("Measure"):
                        goToMain(METRIC_REQUEST);
//                        mDrawerLayout.closeDrawers();
                        break;
                    case ("Client"):
                        goToMain(CUSTOMER_REQUEST);
                        break;
                    case ("Address"):
                        goToMain(ADDRESS_REQUEST);
                    case ("Get Location"):
                        goToMain(GEOLOCATION_REQUEST);
                        break;
                    case ("Upload"):
                        Log.d(TAG, "Upload to be implemented in MainActivity");
                        break;
                    case ("Clear"):
                        goToMain(CLEAR_REQUEST);
                        break;
                    case ("Login"):
                        goToMain(LOGIN_REQUEST);
                        break;
                    case ("Logout"):
                        Log.d(TAG, "Logout to be implemented in MainActivity");
                        break;
                    case ("Start"):
                        Log.d(TAG, "Going HOME");
                        goToMain(WELCOME_REQUEST);
                        break;
                    default:
                        break;
                }
//               }
                return false;
            }
        });
    }


    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.OpenDrawer,  R.string.CloseDrawer) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                mDrawerLayout.bringChildToFront(drawerView);
                mDrawerLayout.requestLayout();
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    mDrawerLayout.setBackgroundColor(ContextCompat.getColor(RviewActivity.this, R.color.colorRow));
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
                    mDrawerLayout.setBackgroundColor(ContextCompat.getColor(RviewActivity.this, R.color.colorRow));
                    sendViewToBack(mDrawerLayout);
                }
            }
        };
    }

    public void goToMain(int request) {
        Intent mIntent = new Intent(RviewActivity.this, MainActivity.class);
        mIntent.putExtra("REQUEST", request);
        startActivity(mIntent);
    }

    public boolean parseData(String data) {

        try {
            JSONObject dataJson = new JSONObject(data);
            JSONArray rvJsonArray = new JSONArray(dataJson.getString("Roofs"));
            for (int i = 0; i < rvJsonArray.length(); i++) {
                JSONObject keyPairJson = rvJsonArray.getJSONObject(i);
                JSONObject roofJson = new JSONObject(keyPairJson.getString("roof"));
                Roof roof = new Roof();
                roof.setId(Integer.valueOf(roofJson.getString("id")));
                roof.setAddress(roofJson.getString("address"));
                roof.setLength(Float.valueOf(roofJson.getString("length")));
                roof.setWidth(Float.valueOf(roofJson.getString("width")));
                roof.setSlope(Float.valueOf(roofJson.getString("slope")));
                roof.setPrice(new BigDecimal(roofJson.getString("price")));
                if (roofJson.has("files")) {
                    JSONArray rFiles = new JSONArray(roofJson.getString("files"));
                    int fileNum = rFiles.length();
                    Log.d(TAG, "Number of files for" + roof.getId() + ": " + String.valueOf(fileNum));
                    ArrayList<RuvFileInfo> filesArray = new ArrayList<>();
                    for (int fNum = 0; fNum < fileNum; fNum++) {
                        JSONObject fileObject = rFiles.getJSONObject(fNum);
                        RuvFileInfo rFile = new RuvFileInfo();
                        rFile.setUrl(baseUrl + "/files/" + fileObject.getString(String.valueOf(fNum)));
                        if (fileObject.has("comment")) rFile.setComment(fileObject.getString("comment"));
                        filesArray.add(rFile);
                    }
                    roof.setFiles(filesArray);
                }
                roofArrayList.add(roof);
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }


    public ArrayList<Roof> getFeed()  {
        return this.roofArrayList;
    }


    public void updateUi()  {
        final ArrayList<Roof> feedList = getFeed();
        if (feedList.size() > 0) {
            final RuvAdapter ruvAdapter = new RuvAdapter(RviewActivity.this, feedList, baseUrl, authToken, reopenDialog, fileUrls);
            rv = (RecyclerView) findViewById(R.id.recycView);
            rv.setAdapter(ruvAdapter);
            rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
//                    scrollChange(recyclerView, newState);
                }
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy)  {
//                    scrolled(recyclerView, dx, dy);
                }

            });
            LinearLayoutManager layoutMgr = new LinearLayoutManager(getBaseContext(),
                    LinearLayoutManager.VERTICAL, false);
            layoutMgr.setAutoMeasureEnabled(true);
            layoutMgr.setRecycleChildrenOnDetach(true);
            rv.setLayoutManager(layoutMgr);
//            rv.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ruviuz_menu, menu);
        updateMenuWithIcon(menu.findItem(R.id.goHome), Color.WHITE);
        updateMenuWithIcon(menu.findItem(R.id.loginAction), Color.WHITE);
        updateMenuWithIcon(menu.findItem(R.id.geoLocate), Color.WHITE);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "MENU ITEM ID::" + String.valueOf(item.getItemId()));
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
                rviewIntent.putExtra("authToken", authToken);
                rviewIntent.putExtra("baseUrl", baseUrl);
                this.startActivity(rviewIntent);
                break;
            case R.id.loginAction:
                Log.d(TAG, "Login action!!");
                Intent loginIntent = new Intent(RviewActivity.this, MainActivity.class);
                putIntentData(loginIntent);
                loginIntent.putExtra("PERSIST", true);
                loginIntent.putExtra("REQUEST", MainActivity.LOGIN_REQUEST);
                startActivity(loginIntent);
                break;
            case R.id.geoLocate:
                Log.d(TAG, "GEOLOCATION REQUEST");
//                MainActivity.getGeoLocation();
                break;
            case R.id.goHome:
                Log.d(TAG, "Going HOME");
//                dismissAllDialogs();
//                hideActivity();
//                welcomeDialog();
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }




    @Override
    public void ruvFragInteraction(String key, String data) {
        if (key.equals("Update")) {
            try {
                JSONObject respJson = new JSONObject(data);
                if (respJson.getString("Update").equals("Success")) {
                    int ruvPosition = Integer.valueOf(respJson.getString("position"));
                    JSONObject ruvJson = new JSONObject(respJson.getString("Roof"));
                    Roof mRuv = roofArrayList.get(ruvPosition);
                    mRuv.setAddress(ruvJson.getString("address"));
                    mRuv.setLength(Float.valueOf(ruvJson.getString("length")));
                    mRuv.setWidth(Float.valueOf(ruvJson.getString("width")));
                    mRuv.setPrice(new BigDecimal((ruvJson.getString("price"))));
                    mRuv.setSlope(Float.valueOf(ruvJson.getString("slope")));
                    mRuv.toggleJustUpdated();
                    roofArrayList.set(ruvPosition, mRuv);
                    rv.getAdapter().notifyItemChanged(ruvPosition);
                    Toast.makeText(this, "RuuvUpdate successful.", Toast.LENGTH_SHORT).show();
                    if (getFragmentManager().findFragmentByTag("ruvFrag") != null) {
                        RuvFragment rFrag = (RuvFragment)getFragmentManager().findFragmentByTag("ruvFrag");
                        rFrag.dismiss();
                    }
                } else {
                    Toast.makeText(this, "RuuvUpdate failed.", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
        }
        if (key.equals("GetRuv")) {
            Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
        }
        if (key.equals("FileUpdate")) {
            Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
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
        this.baseUrl = intent.getStringExtra("baseUrl");
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
    }

    public void putPrefsData() {
        SharedPreferences.Editor prefEdit = RviewActivity.this.getSharedPreferences("RuviuzApp", Context.MODE_PRIVATE).edit();
        prefEdit.putString("address", address);
        prefEdit.putString("postal", postal);
        prefEdit.putString("city", city);
        prefEdit.putString("region", region);
//        prefEdit.putString("price", String.valueOf(price));
        prefEdit.putFloat("width", width);
        prefEdit.putFloat("length", length);
        prefEdit.putFloat("slope", slope);
        prefEdit.putBoolean("premium", premium);
        prefEdit.putInt("currentRid", currentRid);
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
        SharedPreferences mPrefs = RviewActivity.this.getSharedPreferences("RuviuzApp", Context.MODE_PRIVATE);
        this.address = mPrefs.getString("address", "");
        this.postal = mPrefs.getString("postal", "");
//        this.price = new BigDecimal(mPrefs.getString("price", "0"));
        this.width = mPrefs.getFloat("width", 0f);
        this.length = mPrefs.getFloat("length", 0f);
        this.slope = mPrefs.getFloat("slope", 0f);
        this.premium = mPrefs.getBoolean("premium", false);
        this.currentRid = mPrefs.getInt("currentRid", 0);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
        switch (requestCode) {
            case RUVIUZ_CAMERA:
                if (resultCode == RESULT_OK) {

                    //TODO do stuff
                    Log.d(TAG, "RETURNED in onActivityResult RUVIUZ CAMERA");
                }
                break;
        }
    }
}
