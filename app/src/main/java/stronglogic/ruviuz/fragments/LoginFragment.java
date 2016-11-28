package stronglogic.ruviuz.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import stronglogic.ruviuz.R;
import stronglogic.ruviuz.tasks.LoginTask;

/**
 * Created by logicp on 3/22/16.
 */
public class LoginFragment extends DialogFragment {

    final private static String TAG = "RuviuzLOGINFRAGMENT";

    private EditText email;
    private EditText password;

    private LoginFragListener loginFragListener;

    String[] login = new String[2];

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    public LoginFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.loginfragment, parent, false);

        email = (EditText)view.findViewById(R.id.email);
        email.setOnClickListener(new View.OnClickListener()    {
            @Override
            public void onClick(View v) {
                email.setText("");
            }
        });

        password = (EditText)view.findViewById(R.id.password);
        password.setOnClickListener(new View.OnClickListener()    {
            @Override
            public void onClick(View v) {
                password.setText("");
            }
        });

        Button loginBtn = (Button)view.findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener()    {
            public void onClick(View v) {
                if (email.getText().toString().length() > 0 &&
                        password.getText().toString().length() > 0) {
                    login[0] = email.getText().toString();
                    login[1] = password.getText().toString();
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
                loginFragListener = (LoginFragListener) activity;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement statusFragListener");
        }
    }

    public void buttonClicked(View view)    {
        if (this.login[0] != null && this.login.length > 1) {
            LoginTask loginTask = new LoginTask(login[0], login[1], new LoginTask.AsyncResponse() {
                @Override
                public void processFinish(String[] output) {
                    Log.d(TAG, output[0]);
                    loginFragListener.loginFragInteraction(output[0]);
                }
            });
            loginTask.execute();
        }
    }

    public interface LoginFragListener {
        void loginFragInteraction(String output);
    }


}
