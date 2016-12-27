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

    private EditText addressEt, postalEt, cityEt, provinceEt;

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
        postalEt = (EditText)view.findViewById(R.id.postalCode);
        cityEt = (EditText)view.findViewById(R.id.cityText);
        provinceEt = (EditText)view.findViewById(R.id.provinceText);

        if (!mPrefs.getString("address", "").equals("")) {
            addressEt.setText(mPrefs.getString("address", ""));
        }
        if (!mPrefs.getString("postal", "").equals("")) {
            postalEt.setText(mPrefs.getString("postal", ""));
        }
        if (!mPrefs.getString("city", "").equals("")) {
            cityEt.setText(mPrefs.getString("city", ""));
        } 
        if (!mPrefs.getString("region", "").equals("")) {
            provinceEt.setText(mPrefs.getString("region", ""));
        }

        Button addressBtn = (Button)view.findViewById(R.id.addressBtn);

        addressBtn.setOnClickListener(new View.OnClickListener()    {
            public void onClick(View v) {
                if (addressEt.getText().toString().length() > 0 &&
                        postalEt.getText().toString().length() > 0) {
                    address = addressEt.getText().toString();
                    postal = postalEt.getText().toString();
                    city = cityEt.getText().toString();
                    province = provinceEt.getText().toString();
                    buttonClicked(v);
                } else {
                    Toast.makeText(getActivity(), "Enter Complete Address, bitch", Toast.LENGTH_SHORT).show();
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
            addressFragListener.addressFragInteraction(address, postal, city, province);
        }
    }

    public interface AddressFragListener {
        void addressFragInteraction(String address, String postal, String city, String province);
    }

    public void setArea(String city, String province) {
        this.city = city;
        this.province = province;
    }
}
