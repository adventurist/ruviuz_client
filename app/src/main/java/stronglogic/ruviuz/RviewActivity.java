package stronglogic.ruviuz;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

import stronglogic.ruviuz.content.Roof;
import stronglogic.ruviuz.fragments.RuvFragment;
import stronglogic.ruviuz.tasks.RviewTask;
import stronglogic.ruviuz.views.RuvAdapter;

public class RviewActivity extends AppCompatActivity implements RuvFragment.RuvFragListener {

    private static final String TAG = "RuviuzRVIEWACTIVITY";

    private android.support.v7.widget.Toolbar mToolbar;

    private String authToken;
    private String baseUrl;

    private int currentRid, fileCount;
    private float slope, width, length;
    private boolean premium;
    private String address;
    private String[] fileUrls = new String[3];

    private ArrayList<Roof> roofArrayList;
    public RecyclerView rv;
    private RuvAdapter ruvAdapter;

    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rview);

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);

        if (getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.drawable.rlogo);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setElevation(8f);
        }

        Intent mIntent = getIntent();
        this.authToken = mIntent.getStringExtra("authToken");
        this.baseUrl= mIntent.getStringExtra("baseUrl");

        getIntentData(mIntent);

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
                    ArrayList<String> photoArray = new ArrayList<>();
                    for (int fNum = 0; fNum < fileNum; fNum++) {
                        JSONObject fileObject = rFiles.getJSONObject(fNum);
                        photoArray.add(fNum, baseUrl + "/files/" + fileObject.getString(String.valueOf(fNum)));
                        Log.d(TAG, "Photo path is: " + photoArray.get(fNum));
                    }
                roof.setPhotos(photoArray);
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
            final RuvAdapter ruvAdapter = new RuvAdapter(RviewActivity.this, feedList, baseUrl, authToken);
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
        int color = ContextCompat.getColor(this, R.color.colorAccent);
        updateMenuWithIcon(menu.findItem(R.id.loginAction), color);
        updateMenuWithIcon(menu.findItem(R.id.geoLocate), color);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.roofView:
                Intent rviewIntent = new Intent(this, RviewActivity.class);
                rviewIntent.putExtra("authToken", authToken);
                rviewIntent.putExtra("baseUrl", baseUrl);
                this.startActivity(rviewIntent);
                break;
            case R.id.loginAction:
                Log.d(TAG, "Login action!!");
//                loginDialog();
                break;
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
        intent.putExtra("premium", this.premium);
        intent.putExtra("currentRid", this.currentRid);
        intent.putExtra("fileCount", this.fileCount);
        intent.putExtra("fileUrls", this.fileUrls);
    }

    public void getIntentData(Intent intent) {
        this.authToken = intent.getStringExtra("authToken");
        this.slope = intent.getFloatExtra("slope", 0);
        this.width = intent.getFloatExtra("width", 0);
        this.length = intent.getFloatExtra("length", 0);
        this.address = intent.getStringExtra("address");
        this.premium = intent.getBooleanExtra("premium", false);
        this.currentRid = intent.getIntExtra("currentRid", -1);
        this.fileCount = intent.getIntExtra("fileCount", 0);
        this.fileUrls = intent.getStringArrayExtra("fileUrls");
    }
}
