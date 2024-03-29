package stronglogic.ruviuz.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import stronglogic.ruviuz.R;

/**
 * A simple {@link android.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SectionFragment.SectionListener} interface
 * to handle interaction events.
 * Use the {@link SectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SectionFragment extends Fragment {

    private Activity mActivity;
    private SectionListener mListener;
    
    private TextView sectionType, sectionLength, sectionWidth, emptyLength, emptyWidth, sectionLengthIn, sectionWidthIn, emptyLengthIn, emptyWidthIn, eType;

    public SectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SectionFragment newInstance(String param1, String param2) {
        SectionFragment fragment = new SectionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_section, container, false);

//        sectionType = (TextView) mView.findViewById(R.id.sectionType);
//        sectionLength = (TextView) mView.findViewById(R.id.sectionLength);
//        sectionWidth = (TextView) mView.findViewById(R.id.sectionWidth);
//        emptyLength = (TextView) mView.findViewById(R.id.emptyLength);
//        emptyWidth = (TextView) mView.findViewById(R.id.emptyWidth);
//
//        sectionLengthIn = (TextView) mView.findViewById(R.id.sectionLengthIn);
//        sectionWidthIn = (TextView) mView.findViewById(R.id.sectionWidthIn);
//        emptyLengthIn = (TextView) mView.findViewById(R.id.emptyLengthIn);
//        emptyWidthIn = (TextView) mView.findViewById(R.id.emptyWidthIn);
//
//        eType = (TextView) mView.findViewById(R.id.eType);

//        RuvDrawable vDrawable = new RuvDrawable(Color.BLACK,Color.GREEN,Color.LTGRAY,2,Color.RED,50);
//        View vView = new View(mActivity);
//        vView.setBackground(vDrawable);
//
//        LinearLayout drawLayout = (LinearLayout) mView.findViewById(R.id.drawLayout);
//        drawLayout.addView(vView);
        return mView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.mActivity = (Activity) context;
        }
        if (context instanceof SectionListener) {
            mListener = (SectionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SectionListener");
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
    public interface SectionListener {
        // TODO: Update argument type and name
        void sectionFragInteraction(Bundle bundle, int request);
    }
}
