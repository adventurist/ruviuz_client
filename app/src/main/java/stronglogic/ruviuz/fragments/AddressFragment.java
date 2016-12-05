package stronglogic.ruviuz.fragments;

import android.app.Activity;
import android.app.DialogFragment;
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

    private EditText addressEt;
    private EditText postalCodeEt;

    private AddressFragListener addressFragListener;

    private String address, city, province, postal;

    public static AddressFragment newInstance(String param1, String param2) {
        AddressFragment fragment = new AddressFragment();
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

        addressEt = (EditText)view.findViewById(R.id.addressText);
        addressEt.setOnClickListener(new View.OnClickListener()    {
            @Override
            public void onClick(View v) {
                addressEt.setText("");
            }
        });

        postalCodeEt = (EditText)view.findViewById(R.id.postalCode);
        postalCodeEt.setOnClickListener(new View.OnClickListener()    {
            @Override
            public void onClick(View v) {
                postalCodeEt.setText("");
            }
        });

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


}
