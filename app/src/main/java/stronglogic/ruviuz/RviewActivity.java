package stronglogic.ruviuz;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import stronglogic.ruviuz.content.Customer;
import stronglogic.ruviuz.content.Roof;
import stronglogic.ruviuz.content.RuvFileInfo;
import stronglogic.ruviuz.content.Section;
import stronglogic.ruviuz.fragments.ImageEditFragment;
import stronglogic.ruviuz.fragments.RuvFragment;
import stronglogic.ruviuz.fragments.SectionEditFragment;
import stronglogic.ruviuz.tasks.RviewTask;
import stronglogic.ruviuz.util.RuvFilter;
import stronglogic.ruviuz.util.db.RuvDBContract;
import stronglogic.ruviuz.util.db.RuvDBHelper;
import stronglogic.ruviuz.views.RuvAdapter;

import static stronglogic.ruviuz.MainActivity.baseUrl;
import static stronglogic.ruviuz.MainActivity.getStatusBarHeight;
import static stronglogic.ruviuz.MainActivity.sendViewToBack;

public class RviewActivity extends AppCompatActivity implements RuvFragment.RuvFragListener, ImageEditFragment.ImageFragListener, SectionEditFragment.SectionEditListener {

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

    public static final int RVIEW_IMG_EDIT = 900;

    private android.support.v7.widget.Toolbar mToolbar;

    private RuvFragment rFrag;
    private SectionEditFragment secEditFrag;
    private ImageEditFragment imgEditFrag;

    private String authToken;

    private int currentRid, fileCount, reopenDialog;
    private float slope, width, length;
    private boolean premium, ready;
    private String address, city, region, postal;
    private String[] fileUrls = new String[3];
    private String[] fileComments = new String[3];

    private Customer mCustomer;

    private ArrayList<Roof> roofArrayList;
    private ArrayList<Section> sectionList;
    private List<HashMap<String, Object>> priceMenuData = new ArrayList<>();
    private List<HashMap<String, Object>> alphabetList = new ArrayList<>();
    private ListPopupWindow popupWindow;
    public RecyclerView rv;
    private RuvDBHelper ruvDbHelper;
    private RuvAdapter ruvAdapter;
    private RuvFilter ruvFilter;
    private RuvFilter.filterType filterType = RuvFilter.filterType.ADDRESS;

    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawerView;
    private SearchView searchView;

    protected int editPosition;

    @TargetApi(23)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rview);

        setupWindowAnimations();

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

        final AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottomBar);

// Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Price", R.drawable.ic_dollar_symbol, R.color.ruvGreen);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Jigga", R.drawable.mapmark, R.color.colorUpdated);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Janga", R.drawable.client, R.color.colorUpdated);

        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

        bottomNavigation.setCurrentItem(AHBottomNavigation.CURRENT_ITEM_NONE);
        bottomNavigation.setAccentColor(Color.parseColor("#F63D2B"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));
        bottomNavigation.setForceTint(true);
        bottomNavigation.setTranslucentNavigationEnabled(true);
        bottomNavigation.setColored(true);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch(position) {
                    case (0):
                        showPriceMenu(bottomNavigation);
                        break;
                    case (1):
                        showLetterMenu(bottomNavigation);
                        break;
                    case (2):
                        ruvFilter.resetList();
                        ruvAdapter.notifyDataSetChanged();
                        break;
                }

             return true;
            }
        });

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
                        Intent reloadIntent = new Intent(RviewActivity.this, RviewActivity.class);
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

        //Set up lists for use in the BottomNavigation Menu (for quick searches)
        setPriceList();
        setAlphabetList();

        ruvDbHelper = new RuvDBHelper(RviewActivity.this);

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

    private void showPriceMenu(final View anchor) {

        popupWindow = new ListPopupWindow(this);

        ListAdapter adapter = new SimpleAdapter(
                this,
                priceMenuData,
                R.layout.price_search_menu,
                new String[] {"mTitle", "mIcon"}, // These are just the keys that the data uses (constant strings)
                new int[] {R.id.shoe_select_text, R.id.shoe_select_icon}); // The view ids to map the data to

        popupWindow.setAnchorView(anchor);
        popupWindow.setAdapter(adapter);
        popupWindow.setWidth(450);
        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int key = getPriceHashmap().keyAt(position);

                ruvFilter.setType(RuvFilter.filterType.PRICE);
                ruvFilter.filter(String.valueOf(key));

                popupWindow.dismiss();
            }
        });
        popupWindow.show();
    }

    public void setPriceList() {

        SparseArray priceArray = getPriceHashmap();
        int mapSize = priceArray.size();

        for (int i = 0; i < mapSize; i++) {

            int k = priceArray.keyAt(i);

            HashMap <String, Object> map = new HashMap<>();
            map.put("mTitle", priceArray.get(k).toString());
            map.put("mIcon", R.drawable.ic_dollar_symbol);
            priceMenuData.add(map);

        }
    }

    public SparseArray getPriceHashmap() {

        SparseArray<String> priceArray = new SparseArray<>();
        priceArray.append(500, "Under $500");
        priceArray.append(2500, "Under $2500");
        priceArray.append(5000, "Under $5000");
        priceArray.append(8500, "Under $8500");
        priceArray.append(12500, "Under $12500");
        priceArray.append(18000, "Under $18000");
        priceArray.append(25000, "Under $25000");
        priceArray.append(50000, "Under $50000");
        priceArray.append(100000, "Under $100000");

        return priceArray;
    }


    private void showLetterMenu(final View anchor) {

        popupWindow = new ListPopupWindow(this);

        ListAdapter adapter = new SimpleAdapter(
                this,
                alphabetList,
                R.layout.price_search_menu,
                new String[] { "mTitle", "mIcon" }, // These are just the keys that the data uses (constant strings)
                new int[] {R.id.shoe_select_text, R.id.shoe_select_icon}); // The view ids to map the data to

        popupWindow.setAnchorView(anchor);
        popupWindow.setAdapter(adapter);
        popupWindow.setWidth(200);
        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                char charValue = getAlphabet()[position];

                ruvFilter.setType(RuvFilter.filterType.CUSTOMER_FIRST);
                ruvFilter.filter(String.valueOf(charValue));

                popupWindow.dismiss();
            }
        });
        popupWindow.show();
    }

    public void setAlphabetList() {

        char[] alphabet = getAlphabet();

        for (int i = 0; i < 26; i++) {

            HashMap <String, Object> map = new HashMap<>();
            map.put("mTitle", alphabet[i]);
            map.put("mIcon", R.drawable.ic_format_color_text_black_24dp);
            alphabetList.add(map);

        }
    }

    public char[] getAlphabet() {

        return "abcdefghijklmnopqrstuvwxyz".toCharArray();

    }

    private void setupWindowAnimations() {
        Transition fade = TransitionInflater.from(this).inflateTransition(R.transition.fade);

//        Fade fade = new Fade();
        fade.setDuration(1000);
        getWindow().setEnterTransition(fade);
    }


    public void goToMain(int request) {
        Intent mIntent = new Intent(RviewActivity.this, MainActivity.class);
        mIntent.putExtra("REQUEST", request);
        startActivity(mIntent);
    }

    public boolean parseData(String data) {

        if (!data.equals("Error")) {
            try {
                JSONObject dataJson = new JSONObject(data);
                JSONArray rvJsonArray = new JSONArray(dataJson.getString("Roofs"));
                for (int i = 0; i < rvJsonArray.length(); i++) {
                    JSONObject keyPairJson = rvJsonArray.getJSONObject(i);
                    JSONObject roofJson = new JSONObject(keyPairJson.getString("roof"));
                    JSONObject addrJson = new JSONObject(roofJson.getString("address"));
                    Roof roof = new Roof();
                    roof.setId(Integer.valueOf(roofJson.getString("id")));
                    roof.setPrice(new BigDecimal(roofJson.getString("price")));
                    roof.setFloors(Integer.valueOf(roofJson.getString("floors")));
                    roof.setCleanUpFactor(Integer.valueOf(roofJson.getString("rstate")));
                    String addrStr = addrJson.getString("address") + "\n" + addrJson.getString("city") + "," + addrJson.getString("region") + "\n" + addrJson.getString("postal");
                    roof.setAddress(addrStr);
                    if (roofJson.has("customer"))
                        roof.setCustomerName(roofJson.getString("customer"));
                    if (roofJson.has("files")) {
                        JSONArray rFiles = new JSONArray(roofJson.getString("files"));
                        int fileNum = rFiles.length();
                        Log.d(TAG, "Number of files for " + roof.getId() + ": " + String.valueOf(fileNum));
                        ArrayList<RuvFileInfo> filesArray = new ArrayList<>();
                        for (int fNum = 0; fNum < fileNum; fNum++) {
                            JSONObject fileObject = rFiles.getJSONObject(fNum);
                            RuvFileInfo rFile = new RuvFileInfo();
                            rFile.setUrl(baseUrl + "/files/" + fileObject.getString(String.valueOf(fNum)));
                            if (fileObject.has("comment"))
                                rFile.setComment(fileObject.getString("comment"));
                            if (fileObject.has("time"))
                                rFile.setTime(fileObject.getString("time"));
                            filesArray.add(rFile);
                        }
                        roof.setFiles(filesArray);
                    }
                    if (roofJson.has("sections")) {
                        JSONArray sections = new JSONArray(roofJson.getString("sections"));
                        int secNum = sections.length();
                        ArrayList<Section> sectionsArray = new ArrayList<>();
                        for (int sNum = 0; sNum < secNum; sNum++) {
                            JSONObject sectionObject = sections.getJSONObject(sNum);
//                        }
                            Section section = new Section();
                            if (sectionObject.has("type")) {
                                if (sectionObject.getString("type").equals(Section.SectionType.MANSARD)) {
                                    section.setSectionType(Section.SectionType.MANSARD);
                                } else if (sectionObject.getString("type").equals(Section.SectionType.HIP_RECTANGLE)) {
                                    section.setSectionType(Section.SectionType.HIP_RECTANGLE);
                                } else if (sectionObject.getString("type").equals(Section.SectionType.HIP_SQUARE)) {
                                    section.setSectionType(Section.SectionType.HIP_SQUARE);
                                } else if (sectionObject.getString("type").equals(Section.SectionType.GABLE)) {
                                    section.setSectionType(Section.SectionType.GABLE);
                                }
//                            else if (sectionObject.getString("type").equals(Section.SectionType.LEAN-TO-ROOF))
                            }
                            section.setSlope(Float.valueOf(sectionObject.getString("slope")));
                            section.setLength(Float.valueOf(sectionObject.getString("length")));
                            section.setWidth(Float.valueOf(sectionObject.getString("width")));
                            section.setTopWidth(Float.valueOf(sectionObject.getString("twidth")));
                            if (!Boolean.valueOf(sectionObject.getString("full"))) {
                                section.toggleFull();
                                if (sectionObject.has("empty"))
                                    section.setMissing(Float.valueOf(sectionObject.getString("empty")));
                                if (sectionObject.has("etype")) {
                                    if (sectionObject.getString("etype").equals(Section.EmptyType.CHIMNEY)) {
                                        section.setEmptyType(Section.EmptyType.CHIMNEY);
                                    } else if (sectionObject.getString("etype").equals(Section.EmptyType.SKY_LIGHT)) {
                                        section.setEmptyType(Section.EmptyType.SKY_LIGHT);
                                    } else if (sectionObject.getString("etype").equals(Section.EmptyType.OTHER)) {
                                        section.setEmptyType(Section.EmptyType.OTHER);
                                    }
                                }
                            }
                            sectionsArray.add(section);
                        }
                        roof.setSections(sectionsArray);
                    }
                    roofArrayList.add(roof);
                }
                Collections.sort(roofArrayList, new Comparator<Roof>() {
                    @Override
                    public int compare(Roof o1, Roof o2) {
                        return o1.getId() > o2.getId() ? 1 : o1.getId() < o2.getId() ? -1 : 0;
                    }
                });
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            Snackbar.make(RviewActivity.this.findViewById(R.id.activity_rview), "Login Successful", Snackbar.LENGTH_SHORT).show();
            return false;
        }
    }


    public ArrayList<Roof> getFeed()  {
        return this.roofArrayList;
    }


    public void updateUi()  {
        final ArrayList<Roof> feedList = getFeed();
        if (feedList.size() > 0) {
            this.ruvAdapter = new RuvAdapter(RviewActivity.this, feedList, baseUrl, authToken, reopenDialog, fileUrls);
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
            ruvFilter = new RuvFilter(ruvAdapter, roofArrayList);
            this.filterType = RuvFilter.filterType.ADDRESS;
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
    public boolean onPrepareOptionsMenu(Menu menu) {

        final SimpleCursorAdapter searchAdapter = new SimpleCursorAdapter(this, R.layout.search_suggest, null, new String[] { "pattern" }, new int[] { R.id.suggest_text }, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        searchView.setSuggestionsAdapter(searchAdapter);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                String suggestion = getSuggestion(position);
                searchView.setQuery(suggestion, true);
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                String suggestion = getSuggestion(position);
                searchView.setQuery(suggestion, true);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "QueryTxt::" + query);
                ruvFilter.setType(RviewActivity.this.filterType);
                ruvFilter.filter(query);
                ruvDbHelper.insertSuggestion(69, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "TxtChng::" + newText);
                ruvFilter.setType(RviewActivity.this.filterType);
                ruvFilter.filter(newText);
                searchAdapter.changeCursor(ruvDbHelper.getCursor(69, newText));

                return false;
            }
        });

        return true;
    }


    private String getSuggestion(int position) {
        Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
        return cursor.getString(cursor.getColumnIndex(RuvDBContract.RuvEntry.PATTERN));
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.search);
        final MenuItem searchToggle = menu.findItem(R.id.searchFacets);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        ViewGroup.LayoutParams navButtonsParams = new ViewGroup.LayoutParams(RviewActivity.this.mToolbar.getHeight() * 2 / 3, mToolbar.getHeight() * 2 / 3);

        final Button typeBtn = new Button(this);
        typeBtn.setBackground(getDrawable(R.drawable.mapmark));

        ((LinearLayout) searchView.getChildAt(0)).addView(typeBtn, navButtonsParams);

        ((LinearLayout) searchView.getChildAt(0)).setGravity(Gravity.BOTTOM);

        searchToggle.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, String.valueOf(item.getItemId()));
                switch (item.getItemId()) {
                    case R.id.searchPrice:
                        Log.d(TAG, "Searching Price");
                        if (RviewActivity.this.filterType != RuvFilter.filterType.PRICE) {
                            RviewActivity.this.filterType = RuvFilter.filterType.PRICE;
                        } else {
                            RviewActivity.this.filterType = RuvFilter.filterType.ADDRESS;
                        }

                }
                return false;
            }
        });


        typeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicking Choose Button");
                if (RviewActivity.this.filterType == null) {
                    RviewActivity.this.filterType = RuvFilter.filterType.ADDRESS;
                } else {
                    if (RviewActivity.this.filterType == RuvFilter.filterType.ADDRESS) {
                        RviewActivity.this.filterType = RuvFilter.filterType.CUSTOMER;
                        typeBtn.setBackground(getDrawable(R.drawable.client));
                    } else {
                        RviewActivity.this.filterType = RuvFilter.filterType.ADDRESS;
                        typeBtn.setBackground(getDrawable(R.drawable.mapmark));
                    }
                }

            }
        });
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
                break;
            case R.id.searchPrice:
                Log.d(TAG, "Searching Price");
//                if (RviewActivity.this.filterType != RuvFilter.filterType.PRICE) {
//                    RviewActivity.this.filterType = RuvFilter.filterType.PRICE;
//                    ruvFilter.setType(RviewActivity.this.filterType);
                if (!ruvFilter.isMulti()) ruvFilter.setMultiSearch(true);
                ruvFilter.addSearchType(RviewActivity.this.filterType);

                final Dialog etypeDialog = new Dialog(RviewActivity.this);
                etypeDialog.setTitle("Choose a Type");

                if (etypeDialog.getWindow() != null) etypeDialog.getWindow().setWindowAnimations(R.style.ruvanimate);
                etypeDialog.setContentView(R.layout.search_dialog);
                etypeDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                etypeDialog.show();

                RadioGroup rg = (RadioGroup) etypeDialog.findViewById(R.id.emptyType);
                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

//                        RadioButton radioBtn = (RadioButton) etypeDialog.findViewById(checkedId);

                        final Handler xHandler = new Handler();
                        xHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                etypeDialog.dismiss();
                            }
                        }, 400);
                    }
                });
//                } else {
//                    RviewActivity.this.filterType = RuvFilter.filterType.ADDRESS;
//                    ruvFilter.setType(RviewActivity.this.filterType);
//                }
                break;
            case R.id.searchAddress:
                Log.d(TAG, "Searching Address");
//                if (RviewActivity.this.filterType != RuvFilter.filterType.ADDRESS) {
//                    RviewActivity.this.filterType = RuvFilter.filterType.PRICE;
//                    ruvFilter.setType(RviewActivity.this.filterType);
                if (!ruvFilter.isMulti()) ruvFilter.setMultiSearch(true);
                ruvFilter.addSearchType(RviewActivity.this.filterType);
//                } else {
//                    RviewActivity.this.filterType = RuvFilter.filterType.ADDRESS;
//                    ruvFilter.setType(RviewActivity.this.filterType);
//                }
                break;
            case R.id.searchClient:
                Log.d(TAG, "Searching Client Name");
//                if (RviewActivity.this.filterType != RuvFilter.filterType.PRICE) {
//                    RviewActivity.this.filterType = RuvFilter.filterType.PRICE;
//                    ruvFilter.setType(RviewActivity.this.filterType);
                if (!ruvFilter.isMulti()) ruvFilter.setMultiSearch(true);
                ruvFilter.addSearchType(RviewActivity.this.filterType);
//                } else {
//                    RviewActivity.this.filterType = RuvFilter.filterType.ADDRESS;
//                    ruvFilter.setType(RviewActivity.this.filterType);
//                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    public void setEditPosition(int position) {
        this.editPosition = position;
    }

    @Override
    public void ruvFragInteraction(String key, String data, int position) {

        if (key.equals("Delete")) {
            ruvAdapter.deletePosition(position);
        }
    }

    /**
     * Interface callback method from RuvFragment
     * @param key
     * Key to determine the type of action fired from the RuvFragment
     * @param data
     * Data reflects the result of retrieving a roof or an attempt to update a roofing quote
     */
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

                    mRuv.setPrice(new BigDecimal((ruvJson.getString("price"))));
                    mRuv.toggleJustUpdated();
                    roofArrayList.set(ruvPosition, mRuv);
                    rv.getAdapter().notifyItemChanged(ruvPosition);
                    Toast.makeText(this, "RuuvUpdate successful.", Toast.LENGTH_SHORT).show();
                    if (getFragmentManager().findFragmentByTag("ruvFrag") != null) {
                        rFrag = (RuvFragment)getFragmentManager().findFragmentByTag("ruvFrag");
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


    /**
     *
     * @param intent
     * intent for outgoing activity data
     */
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
        intent.putExtra("fileComments", this.fileComments);
        intent.putExtra("baseUrl", baseUrl);
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
        intent.putParcelableArrayListExtra("sectionList", sectionList);
    }

    /**
     * @param intent
     * Incoming intent which evoked this activity
     */
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
        this.sectionList = intent.getParcelableArrayListExtra("sectionList");
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

    @Override
    public void imageFragInteraction(Bundle bundle, int request) {
        Log.d(TAG, String.valueOf(request));
    }


    public void editSection(Section section) {
        if (secEditFrag == null) {
            secEditFrag = new SectionEditFragment();
        }

        Bundle sectionBundle = new Bundle();
        sectionBundle.putParcelable("section", section);

        secEditFrag.setArguments(sectionBundle);

        if (!secEditFrag.isAdded()) {
            FragmentManager fm = getFragmentManager();
            secEditFrag.show(fm, "secEditFrag");
        }

    }

    @Override
    public void sectionEditInteraction(int data) {

    }
}
