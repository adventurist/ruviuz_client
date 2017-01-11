package stronglogic.ruviuz.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.math.BigDecimal;

import stronglogic.ruviuz.MainActivity;
import stronglogic.ruviuz.R;
import stronglogic.ruviuz.RviewActivity;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * A simple {@link DialogFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SlopeFragment.SlopeFragListener} interface
 * to handle interaction events.
 * Use the {@link SlopeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SlopeFragment extends DialogFragment {
    
    private SlopeFragListener mListener;

    private SensorManager sensorManager;

    private SensorEventListener mSensorListener;

    private final static int SLOPE_FRAG_SUCCESS = 39;
    private final static int SLOPE_FRAG_FAIL = 40;

    private float slope;

    private String baseUrl;
    
    private MainActivity mActivity;

//    private static TextView slopeAngleText;

    private TextView roofSlope;

    private Toolbar mToolbar;

    private ImageButton getAngle;


    public SlopeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SlopeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SlopeFragment newInstance(String param1, String param2) {
        SlopeFragment fragment = new SlopeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.slope = getArguments().getFloat("slope");
        }
        if (!((MainActivity) getActivity()).readyStatus()) {
            ((MainActivity)getActivity()).hideActivity();
            ((MainActivity)getActivity()).dismissOtherDialogs(SlopeFragment.this.getClass());
        }
        setStyle(DialogFragment.STYLE_NORMAL, R.style.RuvFullFrag);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = super.getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            MainActivity.this.findViewById(R.id.MainParentView).setPadding(0, getStatusBarHeight(getActivity()), 0, 0);\
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View mView = inflater.inflate(R.layout.slopefragment, container, false);
        Window w = getActivity().getWindow();
        View decorView = w.getDecorView();
        // Show Status Bar.
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

//        mView.setPadding(0, getStatusBarHeight(getActivity()), 0, 0);
        mToolbar = (Toolbar) mView.findViewById(R.id.ruvFragToolbar);
        mToolbar.inflateMenu(R.menu.ruviuz_menu);

        if (mToolbar != null) {
            mToolbar.setNavigationIcon(R.drawable.construction);
//            mToolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.ruvGreen));
            mToolbar.setElevation(8f);
            mToolbar.setTitle(getActivity().getResources().getString(R.string.app_name));
            mToolbar.setTitleTextColor(Color.BLACK);
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
                            rviewIntent.putExtra("baseUrl", SlopeFragment.this.baseUrl);
                            mActivity.startActivity(rviewIntent);
                            SlopeFragment.this.dismiss();
                            break;
                        case R.id.loginAction:
                            Log.d(TAG, "Login action!!");
                            mActivity.loginDialog();
                            SlopeFragment.this.dismiss();
                            break;
                        case R.id.geoLocate:
                            Log.d(TAG, "GEOLOCATION REQUEST");
                            mActivity.getGeoLocation();
                            break;
                        case R.id.goHome:
                            Log.d(TAG, "Going HOME");
                            mActivity.welcomeDialog();
                            SlopeFragment.this.dismiss();
                            break;
                    }
                    return true;

                }
            });
        }

        Button backward = (Button) mView.findViewById(R.id.metricBack);
        backward.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);

        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.slopeFragInteraction(SlopeFragment.this.slope, SLOPE_FRAG_SUCCESS);

            }
        });

        Button forward = (Button) mView.findViewById(R.id.metricForward);
        forward.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (roofSlope != null) {
                    float returnFloat = Float.valueOf(SlopeFragment.this.roofSlope.getText().toString());
                    SlopeFragment.this.slope = returnFloat;
                    mListener.slopeFragInteraction(returnFloat, SLOPE_FRAG_SUCCESS);
                } else {
                    mListener.slopeFragInteraction(1f, SLOPE_FRAG_FAIL);
                }
            }
        });

        roofSlope = (TextView) mView.findViewById(R.id.slopePicker);
        roofSlope.setText(String.valueOf(SlopeFragment.this.slope));

        final TextView slopeAngleText = (TextView) mView.findViewById(R.id.angleValue);

        mSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                BigDecimal mSlope = new BigDecimal((double)(36 * event.values[1])).setScale(2, BigDecimal.ROUND_HALF_UP);
                slopeAngleText.setText(String.valueOf(mSlope));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // TODO Auto-generated method stub

            }
        };

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);


        getAngle = (ImageButton) mView.findViewById(R.id.setAngle);
        getAngle.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        getAngle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float slopeValue = Math.abs(Float.valueOf(slopeAngleText.getText().toString()));
                SlopeFragment.this.roofSlope.setText(String.valueOf(slopeValue));
                int color = ContextCompat.getColor(getActivity(), R.color.ruvGreen);
                slopeAngleText.setTextAppearance(getActivity(), R.style.RuvShadowText);
                slopeAngleText.setTextColor(color);
                SlopeFragment.this.roofSlope.jumpDrawablesToCurrentState();
                SlopeFragment.this.sensorManager.unregisterListener(mSensorListener);

                Snackbar.make(mView, "Slope angle set to " + String.valueOf(slopeValue) + " degrees", Snackbar.LENGTH_SHORT).show();
                final Handler xHandler = new Handler();
                xHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SlopeFragment.this.sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
                        slopeAngleText.setTextColor(Color.BLACK);
                    }
                }, 4000);



            }
        });


        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity)
            this.mActivity = (MainActivity) context;
        if (context instanceof SlopeFragListener) {
            mListener = (SlopeFragListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        roofSlope = null;
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
    public interface SlopeFragListener {
        // TODO: Update argument type and name
        void slopeFragInteraction(float slope, int result);
    }
}
