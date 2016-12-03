package stronglogic.ruviuz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

import stronglogic.ruviuz.content.Roof;
import stronglogic.ruviuz.tasks.RviewTask;

public class RviewActivity extends AppCompatActivity {

    private static final String TAG = "RuviuzRVIEWACTIVITY";

    private String authToken;
    private String baseUrl;

    ArrayList<Roof> roofArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rview);

        Intent mIntent = getIntent();
        this.authToken = mIntent.getStringExtra("authToken");
        this.baseUrl= mIntent.getStringExtra("baseUrl");

        roofArrayList = new ArrayList<>();

        RviewTask rviewTask = new RviewTask(authToken, baseUrl, new RviewTask.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                Log.d(TAG, output);
                Toast.makeText(getBaseContext(), output, Toast.LENGTH_LONG).show();

                if (parseData(output)) {
                    Log.d(TAG, "Success!");
                }
            }
        });

        rviewTask.execute();
    }



    public boolean parseData(String data) {

        try {

            JSONObject dataJson = new JSONObject(data);
//            JSONObject rvDataJson = new JSONObject(dataJson.getString("Roofs"));
            JSONArray rvJsonArray = new JSONArray(dataJson.toString());
            for (int i = 0; i < rvJsonArray.length(); i++) {
                JSONObject roofJson = rvJsonArray.getJSONObject(i);
                Roof roof = new Roof();
                roof.setId(Integer.valueOf(roofJson.getString("id")));
                roof.setAddress(roofJson.getString("address"));
                roof.setLength(Float.valueOf(roofJson.getString("length")));
                roof.setWidth(Float.valueOf(roofJson.getString("width")));
                roof.setSlope(Float.valueOf(roofJson.getString("slope")));
                roof.setPrice(new BigDecimal(roofJson.getString("price")));

                roofArrayList.add(roof);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
