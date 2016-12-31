package stronglogic.ruviuz.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import me.angrybyte.numberpicker.listener.OnValueChangeListener;
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
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int METRICFRAG_COMPLETE = 21;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Float[] values;

    private OnFragmentInteractionListener mListener;

    private me.angrybyte.numberpicker.view.ActualNumberPicker roofLength, roofWidth, roofSlope;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.metricfragment, container, false);

        roofLength = (me.angrybyte.numberpicker.view.ActualNumberPicker) mView.findViewById(R.id.lengthPicker);
        roofLength.setValue(Math.round(values[0]));
        roofLength.setListener(new OnValueChangeListener() {
            @Override
            public void onValueChanged(int oldValue, int newValue) {
//                mListener.metricfragInteraction(String.valueOf(newValue));
            }
        });

        roofWidth = (me.angrybyte.numberpicker.view.ActualNumberPicker) mView.findViewById(R.id.widthPicker);
        roofWidth.setValue(Math.round(values[1]));
        roofSlope = (me.angrybyte.numberpicker.view.ActualNumberPicker) mView.findViewById(R.id.slopePicker);
        roofSlope.setValue(Math.round(values[2]));


        Button backward = (Button) mView.findViewById(R.id.metricBack);

        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MetricFragment.this.dismiss();
            }
        });

        Button forward = (Button) mView.findViewById(R.id.metricForward);

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MetricFragment.this.values = getValues(MetricFragment.this.values);
                mListener.metricfragInteraction(MetricFragment.this.values, "METRIC_SUCCESS");

//                Intent intent = new Intent();
//                intent.putExtra("MetricFrag", "success");
//                getActivity().startActivityForResult(intent, METRICFRAG_COMPLETE);

            }
        });

        return mView;
    }

    public Float[] getValues(Float[] values) {
        if (this.roofLength != null && this.roofWidth != null && this.roofSlope != null) {
            values[0] = (float) roofLength.getValue();
            values[1] = (float) roofWidth.getValue();
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
