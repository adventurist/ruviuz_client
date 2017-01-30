package stronglogic.ruviuz.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import stronglogic.ruviuz.MainActivity;
import stronglogic.ruviuz.R;

/**
 * A simple {@link DialogFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WelcomeFragment.WelcomeFragListener} interface
 * to handle interaction events.
 * Use the {@link WelcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomeFragment extends DialogFragment {

    private final static int CREATE_ACCOUNT = 36;
    private final static int REQUEST_LOGIN = 35;


    private ImageButton loginBtn, newAccountBtn;

    private WelcomeFragListener mListener;

    private MainActivity mActivity;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WelcomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WelcomeFragment newInstance(String param1, String param2) {
        WelcomeFragment fragment = new WelcomeFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity)getActivity()).dismissOtherDialogs(WelcomeFragment.this.getClass());
        if (!((MainActivity) getActivity()).readyStatus()) {
            ((MainActivity)getActivity()).hideActivity();
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        Dialog dialog = new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                mActivity.revealActivity();
            }
        };
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color
                .TRANSPARENT));
        return dialog;

    }

    @Override
    public void onCancel(DialogInterface dialog) {
        mActivity.revealActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.welcomefragment, container, false);

        mView.setAlpha(0.92f);

        loginBtn = (ImageButton) mView.findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.welcomeInteraction(REQUEST_LOGIN);
            }
        });

        newAccountBtn = (ImageButton) mView.findViewById(R.id.newAccountBtn);

        newAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.welcomeInteraction(REQUEST_LOGIN);
            }
        });

        return mView;
    }
    

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.mActivity = (MainActivity) context;
        }
        if (context instanceof WelcomeFragListener) {
            mListener = (WelcomeFragListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement WelcomeFragListener");
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
    public interface WelcomeFragListener {
        // TODO: Update argument type and name
        void welcomeInteraction(int action);
    }
}
