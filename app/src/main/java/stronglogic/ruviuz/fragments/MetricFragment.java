package stronglogic.ruviuz.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.math.BigDecimal;

import stronglogic.ruviuz.R;


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

    private me.angrybyte.numberpicker.view.ActualNumberPicker roofLengthFt, roofWidthFt, roofSlope;

    private ImageButton getAngle;

    private OnFragmentInteractionListener mListener;

    private SensorManager sensorManager;

    private SensorEventListener mSensorListener;

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


    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = super.getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        Dialog dialog = super.onCreateDialog(savedInstanceState);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color
//                .TRANSPARENT));
//        return dialog;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View mView = inflater.inflate(R.layout.metricfragment, container, false);

//        mView.setAlpha(0.75f);

        roofLengthFt = (me.angrybyte.numberpicker.view.ActualNumberPicker) mView.findViewById(R.id.lengthPickerFt);
        roofLengthFt.setValue(Math.round(values[0]));

        roofWidthFt = (me.angrybyte.numberpicker.view.ActualNumberPicker) mView.findViewById(R.id.widthPickerFt);
        roofWidthFt.setValue(Math.round(values[1]));
        roofSlope = (me.angrybyte.numberpicker.view.ActualNumberPicker) mView.findViewById(R.id.slopePicker);
        roofSlope.setValue(Math.round(values[2]));
        
//        Button backward = (Button) mView.findViewById(R.id.metricBack);
//        backward.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
//
//        backward.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MetricFragment.this.dismiss();
//            }
//        });

        Button forward = (Button) mView.findViewById(R.id.metricForward);
        forward.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MetricFragment.this.values = getValues(MetricFragment.this.values);
                mListener.metricfragInteraction(MetricFragment.this.values, "METRIC_SUCCESS");

            }
        });


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
        getAngle.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getAngle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int slopeInt = Math.round(Math.abs(Float.valueOf(slopeAngleText.getText().toString())));
                MetricFragment.this.roofSlope.setValue(slopeInt);
                int color = ContextCompat.getColor(getActivity(), R.color.ruvGreen);
                slopeAngleText.setTextColor(color);
                MetricFragment.this.roofSlope.jumpDrawablesToCurrentState();
                MetricFragment.this.sensorManager.unregisterListener(mSensorListener);

                Snackbar.make(mView, "Slope angle set to " + String.valueOf(slopeInt) + " degrees", Snackbar.LENGTH_SHORT).show();
                final Handler xHandler = new Handler();
                xHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MetricFragment.this.sensorManager.registerListener(mSensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
                        slopeAngleText.setTextColor(Color.WHITE);
                    }
                }, 4000);



            }
        });


        return mView;
    }


    public Float[] getValues(Float[] values) {
        if (this.roofLengthFt != null && this.roofWidthFt != null && this.roofSlope != null) {
            values[0] = (float) roofLengthFt.getValue();
            values[1] = (float) roofWidthFt.getValue();
            values[2] = (float) roofSlope.getValue();
        }
        return values;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
