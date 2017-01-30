package stronglogic.ruviuz.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import stronglogic.ruviuz.MainActivity;
import stronglogic.ruviuz.R;
import stronglogic.ruviuz.RviewActivity;

/**
 * A simple {@link DialogFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CustomerFragment.CustomerFragListener} interface
 * to handle interaction events.
 * Use the {@link CustomerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final static String TAG = "RUVIUZCUSTOMERFRAGMENT";
    Button customerBtn;

    private CustomerFragListener mListener;

    private Toolbar mToolbar;
    
    private MainActivity mActivity;

    private String firstName, lastName, email, phone, prefix;

    private RadioGroup prefixGroup;
    private RadioButton mr, mrs, ms;

    private EditText firstEt, lastEt, emailEt, phoneeT;

    public CustomerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomerFragment.
     */
    public static CustomerFragment newInstance(String param1, String param2) {
        CustomerFragment fragment = new CustomerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.prefix = getArguments().getString("prefix");
            this.firstName = getArguments().getString("firstName");
            this.lastName = getArguments().getString("lastName");
            this.email = getArguments().getString("email");
            this.phone = getArguments().getString("phone");
        }
        if (!mActivity.readyStatus()) {
            mActivity.hideActivity();
            mActivity.dismissOtherDialogs(CustomerFragment.this.getClass());
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
    public void onCancel(DialogInterface dialog) {
        mActivity.mainDialog();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View mView = inflater.inflate(R.layout.customerfragment, container, false);

        mToolbar = (Toolbar) mView.findViewById(R.id.ruvFragToolbar);

        mToolbar.inflateMenu(R.menu.ruviuz_menu);

        prefixGroup = (RadioGroup) mView.findViewById(R.id.clientPrefix);
        mr = (RadioButton) mView.findViewById(R.id.prefix_mr);
        if (this.prefix != null && this.prefix.equals("Mr."))
            mr.setChecked(true);
        ms = (RadioButton) mView.findViewById(R.id.prefix_ms);
        if (this.prefix != null && this.prefix.equals("Ms."))
            ms.setChecked(true);
        mrs = (RadioButton) mView.findViewById(R.id.prefix_mrs);
        if (this.prefix != null && this.prefix.equals("Mrs."))
            mrs.setChecked(true);

        prefixGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioBtn = (RadioButton) mView.findViewById(checkedId);
                CustomerFragment.this.prefix = radioBtn.getText().toString();
            }
        });

        firstEt = (EditText) mView.findViewById(R.id.customerFirst);
        if (firstName != null) firstEt.setText(firstName);
        lastEt = (EditText) mView.findViewById(R.id.customerLast);
        if (lastName != null) lastEt.setText(lastName);
        emailEt = (EditText) mView.findViewById(R.id.email);
        if (email != null) emailEt.setText(email);
        phoneeT = (EditText) mView.findViewById(R.id.phone);
        if (phone != null) phoneeT.setText(phone);
        phoneeT.addTextChangedListener(new PhoneNumberFormattingTextWatcher());


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
                            CustomerFragment.this.dismiss();
                            break;
                        case R.id.loginAction:
                            Log.d(TAG, "Login action!!");
                            mActivity.putPrefsData();
                            mActivity.loginDialog();
                            CustomerFragment.this.dismiss();
                            break;
                        case R.id.geoLocate:
                            Log.d(TAG, "GEOLOCATION REQUEST");
                            mActivity.getGeoLocation();
                            break;
                        case R.id.goHome:
                            Log.d(TAG, "Going HOME");
                            mActivity.putPrefsData();
                            mActivity.welcomeDialog();
                            CustomerFragment.this.dismiss();
                            break;
                    }

                    return true;

                }
            });
        }
        customerBtn = (Button) mView.findViewById(R.id.customerBtn);

        customerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] customerName = new String[2];
                customerName[0] = firstEt.getText().toString();
                customerName[1] = lastEt.getText().toString();
                String email = emailEt.getText().toString();
                String phone = phoneeT.getText().toString();
                String mPrefix = CustomerFragment.this.prefix == null ? "Mr" : CustomerFragment.this.prefix;

                mListener.customerfragInteraction(customerName, email, phone, false, mPrefix);
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
        if (context instanceof CustomerFragListener) {
            mListener = (CustomerFragListener) context;
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
    public interface CustomerFragListener {
        // TODO: Update argument type and name
        void customerfragInteraction(String[] name, String email, String phone, boolean married, String prefix);
    }
}
