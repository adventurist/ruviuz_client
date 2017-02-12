package stronglogic.ruviuz.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import me.angrybyte.numberpicker.view.ActualNumberPicker;
import stronglogic.ruviuz.MainActivity;
import stronglogic.ruviuz.R;
import stronglogic.ruviuz.RviewActivity;


/**
 * A simple {@link DialogFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MetricFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MetricFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MetricFragment extends DialogFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final static String TAG = "RUVIUZMETRICFRAGMENT";

    private Float[] values;

    private static TextView slopeAngleText;

    private ActualNumberPicker roofLengthFt, roofWidthFt;

    private TextView roofSlope;

    private ImageButton getAngle;

    private OnFragmentInteractionListener mListener;

    private SensorManager sensorManager;

    private SensorEventListener mSensorListener;

    private Toolbar mToolbar;
    
    private MainActivity mActivity;

    public MetricFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MetricFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MetricFragment newInstance(String param1, String param2) {
        MetricFragment fragment = new MetricFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        values = new Float[3];

        if (getArguments() != null) {
            values[0] = getArguments().getFloat("length");
            values[1] = getArguments().getFloat("width");
            values[2] = getArguments().getFloat("slope");
        }

        if (!((MainActivity) getActivity()).readyStatus()) {
            ((MainActivity)getActivity()).hideActivity();
            ((MainActivity)getActivity()).dismissOtherDialogs(MetricFragment.this.getClass());
        }
        setStyle(DialogFragment.STYLE_NORMAL, R.style.RuvFullFrag);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);

        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
                mActivity.addressDialog();
            }
        };
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View mView = inflater.inflate(R.layout.metricfragment, container, false);

//        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {if (!((MainActivity) getActivity()).readyStatus()) {
//                ((MainActivity)getActivity()).hideActivity();
//            }
//                // Handle the menu item
//                return true;
//            }
//        });

        mToolbar = (Toolbar) mView.findViewById(R.id.ruvFragToolbar);

        mToolbar.inflateMenu(R.menu.ruviuz_menu);

        if (mToolbar != null) {
            mToolbar.setNavigationIcon(R.drawable.construction);
            mToolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.ruvGreen));
            mToolbar.setElevation(8f);
            mToolbar.setTitle(getActivity().getResources().getString(R.string.app_name));
            mToolbar.setTitleTextColor(Color.BLACK);
//            mToolbar.setTitleTextColor(ContextCompat.getColor(mActivity, R.color.ruvGreen));
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
                            MetricFragment.this.dismiss();
                            break;
                        case R.id.loginAction:
                            Log.d(TAG, "Login action!!");
                            mActivity.putPrefsData();
                            mActivity.loginDialog();
                            MetricFragment.this.dismiss();
                            break;
                        case R.id.geoLocate:
                            Log.d(TAG, "GEOLOCATION REQUEST");
                            mActivity.getGeoLocation();
                            break;
                        case R.id.goHome:
                            Log.d(TAG, "Going HOME");
                            mActivity.welcomeDialog();
                            MetricFragment.this.dismiss();
                            break;

                    }
                    return true;

                }
            });
        }

        roofLengthFt = (ActualNumberPicker) mView.findViewById(R.id.lengthPickerFt);
        roofLengthFt.setValue(Math.round(values[0]));

        roofWidthFt = (ActualNumberPicker) mView.findViewById(R.id.widthPickerFt);
        roofWidthFt.setValue(Math.round(values[1]));
//        roofSlope = (TextView) mView.findViewById(R.id.slopePicker);
//        roofSlope.setText(String.valueOf(values[2]));

        Button backward = (Button) mView.findViewById(R.id.metricBack);
        backward.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);

        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.metricfragInteraction(MetricFragment.this.values, "BACK_TO_ADDRESS");

//                MetricFragment.this.dismiss();
            }
        });

        Button forward = (Button) mView.findViewById(R.id.metricForward);
        forward.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MetricFragment.this.values = getValues(MetricFragment.this.values);
                mListener.metricfragInteraction(MetricFragment.this.values, "METRIC_SUCCESS");

            }
        });


//        final TextView slopeAngleText = (TextView) mView.findViewById(R.id.angleValue);
//
//        mSensorListener = new SensorEventListener() {
//            @Override
//            public void onSensorChanged(SensorEvent event) {
//                BigDecimal mSlope = new BigDecimal((double)(36 * event.values[1])).setScale(2, BigDecimal.ROUND_HALF_UP);
//                slopeAngleText.setText(String.valueOf(mSlope));
//            }
//
//            @Override
//            public void onAccuracyChanged(Sensor sensor, int accuracy) {
//                // TODO Auto-generated method stub
//
//            }
//        };
//
//        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
//        sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
//
//
//        getAngle = (ImageButton) mView.findViewById(R.id.setAngle);
//        getAngle.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
//        getAngle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                float slopeValue = Math.abs(Float.valueOf(slopeAngleText.getText().toString()));
//                MetricFragment.this.roofSlope.setText(String.valueOf(slopeValue));
//                int color = ContextCompat.getColor(getActivity(), R.color.ruvGreen);
//                slopeAngleText.setTextAppearance(getActivity(), R.style.RuvShadowText);
//                slopeAngleText.setTextColor(color);
//                MetricFragment.this.roofSlope.jumpDrawablesToCurrentState();
//                MetricFragment.this.sensorManager.unregisterListener(mSensorListener);
//
//                Snackbar.make(mView, "Slope angle set to " + String.valueOf(slopeValue ) + " degrees", Snackbar.LENGTH_SHORT).show();
//                final Handler xHandler = new Handler();
//                xHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        MetricFragment.this.sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
//                        slopeAngleText.setTextColor(Color.BLACK);
//
//                    }
//                }, 4000);
//
//
//
//            }
//        });


        return mView;
    }


    public Float[] getValues(Float[] values) {
        if (this.roofLengthFt != null && this.roofWidthFt != null) {
            values[0] = (float) roofLengthFt.getValue();
            values[1] = (float) roofWidthFt.getValue();
//            values[2] = Float.valueOf(roofSlope.getText().toString());
        }
        return values;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
        }
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        slopeAngleText = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void metricfragInteraction(Float[] values, String data);
    }
}
