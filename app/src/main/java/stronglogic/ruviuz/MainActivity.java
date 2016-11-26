package stronglogic.ruviuz;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.OrientationEventListener;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private OrientationEventListener mListener;

    private NumberPicker roofLength, roofWidth, roofSlope;
    private Switch isFlat, premiumMaterial;
    private TextView orientationText;

    private final String TAG = "Ruviuz";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        roofLength = (NumberPicker) findViewById(R.id.roofLength);
        roofWidth = (NumberPicker) findViewById(R.id.roofWidth);
        roofSlope = (NumberPicker) findViewById(R.id.roofSlope);

        //set Picker ranges

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
}
