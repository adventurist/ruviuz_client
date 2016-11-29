package stronglogic.ruviuz;

import android.app.FragmentManager;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;

import stronglogic.ruviuz.fragments.AddressFragment;
import stronglogic.ruviuz.fragments.LoginFragment;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginFragListener, AddressFragment.AddressFragListener {

    private OrientationEventListener mListener;

    private NumberPicker roofLength, roofWidth, roofSlope;
    private Switch isFlat, premiumMaterial;
    private TextView orientationText;
    private Button ruuvBtn;

    private static final String TAG = "Ruviuz";
    private static final String baseUrl = "http://10.0.2.2:5000";

    private String authToken;

    private Handler mHandler;

    float width, length, slope;
    BigDecimal price;
    String address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        roofLength = (NumberPicker) findViewById(R.id.roofLength);
        roofWidth = (NumberPicker) findViewById(R.id.roofWidth);
        roofSlope = (NumberPicker) findViewById(R.id.roofSlope);

        //set Picker ranges22

        roofLength.setMinValue(0); roofLength.setMaxValue(500); roofLength.setWrapSelectorWheel(true);
        roofLength.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.d(TAG, "Length set to " + String.valueOf(newVal));
            }
        });

        roofWidth.setMinValue(0); roofWidth.setMaxValue(500); roofWidth.setWrapSelectorWheel(true);
        roofSlope.setMinValue(0); roofSlope.setMaxValue(180);roofSlope.setWrapSelectorWheel(true);

        isFlat = (Switch)findViewById(R.id.roofFlat);
        premiumMaterial = (Switch) findViewById(R.id.premiumMaterial);

        orientationText = (TextView) findViewById(R.id.orientationText);

        mListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_UI) {
            @Override
            public void onOrientationChanged(int orientation) {
                orientationText.setText(String.valueOf(orientation));
            }
        };

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                super.handleMessage(inputMessage);
            }
        };

        if (authToken == null) {
            loginDialog();
        }

        ruuvBtn = (Button) findViewById(R.id.ruuvSubmitBtn);

        ruuvBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (address.length() < 1) {
                    addressDialog();
                }
                if (roofLength.getValue() > 0 &&
                        roofSlope.getValue() > 0 &&
                        roofWidth.getValue() > 0 &&
                        calculatePrice() != null &&
                        address != null) {

                    Bundle mBundle = new Bundle();
                    mBundle.putString("address", address);
                    mBundle.putString("price", price.toString());
                    Thread submitThread = new Thread(new RuuvThread((MainActivity)getParent(), mHandler, baseUrl, authToken, mBundle));
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        mListener.enable();
    }


    @Override
    public void onPause() {
        super.onPause();
        mListener.disable();
    }


    BigDecimal calculatePrice() {
        try {
            this.price = new BigDecimal(String.valueOf(length * width * (0.428 * slope)));
            return price;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void loginDialog() {
        FragmentManager fm = getFragmentManager();
        LoginFragment loginFragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString("baseUrl", baseUrl);
        loginFragment.setArguments(args);
        loginFragment.show(fm, "Please Login");
    }


    public void addressDialog() {
        FragmentManager fm = getFragmentManager();
        AddressFragment addressFragment = new AddressFragment();
        addressFragment.show(fm, "Please Enter Address");
    }


    @Override
    public void loginFragInteraction(String output) {
        Toast.makeText(this, output, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void addressFragInteraction(String address, String postal) {
        this.address = address + ",\n" + postal;
        Toast.makeText(this, this.address, Toast.LENGTH_SHORT).show();
    }


    private class RuuvThread implements Runnable {
        private Handler mHandler;
        private WeakReference<MainActivity> mReference;

        private String baseUrl, authToken;

        private Bundle mBundle;

        private RuuvThread(MainActivity mActivity, Handler mHandler, String baseUrl, String authToken, Bundle mBundle) {
            this.mReference = new WeakReference<MainActivity>(mActivity);
            this.mHandler = mHandler;
            this.baseUrl = baseUrl;
            this.authToken = authToken;
            this.mBundle = mBundle;
        }

        @Override
        public void run() {
            Log.d(TAG, "Must send runnable");
        }

        public Boolean sendRuuv() {
            String endpoint = baseUrl + "/roof/add";
            JSONObject ruuvJson = new JSONObject();

            if (mBundle != null) {

                try {
                    ruuvJson.put("address", mBundle.getString("address"));
                    ruuvJson.put("width", mBundle.getFloat("width"));
                    ruuvJson.put("length", mBundle.getFloat("length"));
                    ruuvJson.put("slope", mBundle.getFloat("slope"));
                    ruuvJson.put("price", mBundle.getString("price"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }
}


//curl -i -X POST -H "Content-Type: application/json" -u eyJhbGciOiJIUzI1NiIsImV4cCI6MTQ4MDI2ODQwMywiaWF0IjoxNDgwMjY3ODAzfQ.eyJpZCI6N30.erZ19HpdMw3gP5dMmkoKwuB4F_fPzOZ3cXeHogrjzUw:jigga -d '{"address":"87 Nizro Nebula","width":"50", "length":"100", "slope":"66.666", "price":"999.99"}' http://127.0.0.1:5000/roof/add

//curl -i -X GET -u jiggamortis:calcutta http://127.0.0.1:5000/token

//curl -i -X POST -H "Content-Type: application/json" -d '{"email":"jiggamortis","password":"calcutta"}' http://127.0.0.1:5000/login