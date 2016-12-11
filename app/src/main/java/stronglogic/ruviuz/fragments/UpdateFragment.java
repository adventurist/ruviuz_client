package stronglogic.ruviuz.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import stronglogic.ruviuz.R;

/**
 * Created by logicp on 12/10/16.
 * Fraggin
 */
public class UpdateFragment extends Fragment {

    final private static String TAG = "RuviuzUPDATEFRAGMENT";

    private UpdateFragListener updateFragListener;


    public UpdateFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.d(TAG, "Fragment has arguments -> Please handle them");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.updatefragment, parent, false);
        TextView upTv = (TextView) view.findViewById(R.id.upTv);
        upTv.setText(R.string.updated);

        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            if (activity != null) {
                Log.d(TAG, "UpdateFragment attached");
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement statusFragListener");
        }
    }


    public interface UpdateFragListener {
        void upFragDismiss(String output);
    }

}
