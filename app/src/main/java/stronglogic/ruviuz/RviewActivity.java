package stronglogic.ruviuz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

    ArrayList<Roof> roofArrayList;
    RecyclerView rv;

    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rview);

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);

        Intent mIntent = getIntent();
        this.authToken = mIntent.getStringExtra("authToken");
        this.baseUrl= mIntent.getStringExtra("baseUrl");

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
    public void ruvFragInteraction(String result) {
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    }
}
