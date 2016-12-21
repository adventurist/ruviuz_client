package stronglogic.ruviuz.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import stronglogic.ruviuz.R;

/**
 * Created by logicp on 3/22/16.
 */
public class AddressFragment extends DialogFragment {

    final private static String TAG = "RuviuzADDRESSFRAGMENT";

    private EditText addressEt, postalCodeEt, cityEt, provinceEt;

    private AddressFragListener addressFragListener;

    private String address, city, province, postal;

    public static RuvFragment newInstance(String param1, String param2) {
        RuvFragment fragment = new RuvFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    public AddressFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.addressfragment, parent, false);

        SharedPreferences mPrefs = getActivity().getSharedPreferences("RuviuzApp", Context.MODE_PRIVATE);

        addressEt = (EditText)view.findViewById(R.id.addressText);
        postalCodeEt = (EditText)view.findViewById(R.id.postalCode);
        cityEt = (EditText)view.findViewById(R.id.cityText);
        provinceEt = (EditText)view.findViewById(R.id.provinceText);

        if (!mPrefs.getString("address", "").equals("")) {
            addressEt.setText(mPrefs.getString("address", ""));
        }
        if (!mPrefs.getString("postalcode", "").equals("")) {
            addressEt.setText(mPrefs.getString("postalcode", ""));
        }
        if (city != null) {
            cityEt.setText(city);
        }
        if (province != null) {
            provinceEt.setText(province);
        }

        Button addressBtn = (Button)view.findViewById(R.id.addressBtn);

        addressBtn.setOnClickListener(new View.OnClickListener()    {
            public void onClick(View v) {
                if (addressEt.getText().toString().length() > 0 &&
                        postalCodeEt.getText().toString().length() > 0) {
                    address = addressEt.getText().toString();
                    postal = postalCodeEt.getText().toString();
                    buttonClicked(v);
                } else {
                    Toast.makeText(getActivity(), "You must enter your email and password!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            if (activity != null) {
                addressFragListener = (AddressFragListener) activity;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement statusFragListener");
        }
    }

    public void buttonClicked(View view)    {
        if (this.address != null && this.postal != null) {
            addressFragListener.addressFragInteraction(address, postal);
        }
    }

    public interface AddressFragListener {
        void addressFragInteraction(String address, String postal);
    }

    public void setArea(String city, String province) {
        this.city = city;
        this.province = province;
    }
}
