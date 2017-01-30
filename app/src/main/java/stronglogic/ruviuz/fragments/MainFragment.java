package stronglogic.ruviuz.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import stronglogic.ruviuz.MainActivity;
import stronglogic.ruviuz.R;
import stronglogic.ruviuz.RviewActivity;

/**
 * A simple {@link DialogFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.MainfragListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method
 * create an instance of this fragment.
 */
public class MainFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final static String TAG = "RUVIUZMAINFRAGMENT";

    private final static int CREATE_QUOTE = 33;
    private final static int SEE_QUOTES = 34;

    private String baseUrl;

    private ImageButton quoteBtn, roofListBtn;

    private MainfragListener mListener;

    private android.support.v7.widget.Toolbar mToolbar;

    private MainActivity mActivity;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.baseUrl = getArguments().getString("baseUrl");
        }
        setStyle(DialogFragment.STYLE_NORMAL, R.style.RuvFullFrag);
        if (!mActivity.readyStatus()) {
            mActivity.hideActivity();
            mActivity.dismissOtherDialogs(MainFragment.this.getClass());
        }
//        setStyle(DialogFragment.STYLE_NORMAL, R.style.RuvFullFrag);

    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        Dialog dialog = super.onCreateDialog(savedInstanceState);
//        // request a window without the title
//        Window window = dialog.getWindow();
//        window.setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        return dialog;
//    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
//            int width = ViewGroup.LayoutParams.MATCH_PARENT;
//            int height = ViewGroup.LayoutParams.MATCH_PARENT;
//            d.getWindow().setLayout(width, height);
            d.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
                dismiss();
                mActivity.revealActivity();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.mainfragment, container, false);



        mToolbar = (Toolbar) mView.findViewById(R.id.ruvFragToolbar);

        mToolbar.inflateMenu(R.menu.ruviuz_menu);

        if (mToolbar != null) {
            mToolbar.setNavigationIcon(R.drawable.construction);
//            mToolbar.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.ruvGreen));
            mToolbar.setElevation(8f);
            mToolbar.setTitle(mActivity.getResources().getString(R.string.app_name));
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
                            rviewIntent.putExtra("baseUrl", MainFragment.this.baseUrl);
                            mActivity.startActivity(rviewIntent);
                            MainFragment.this.dismiss();
                            break;
                        case R.id.loginAction:
                            Log.d(TAG, "Login action!!");
                            mActivity.loginDialog();
                            MainFragment.this.dismiss();
                            break;
                        case R.id.geoLocate:
                            Log.d(TAG, "GEOLOCATION REQUEST");
                            mActivity.getGeoLocation();
                            break;
                        case R.id.goHome:
                            Log.d(TAG, "Going HOME");
                            mActivity.welcomeDialog();
                            MainFragment.this.dismiss();
                    }
                    return true;

                }
            });
        }

        quoteBtn = (ImageButton) mView.findViewById(R.id.createQuote);
        roofListBtn = (ImageButton) mView.findViewById(R.id.seeQuotes);

        quoteBtn.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            mListener.mainfragInteraction(CREATE_QUOTE);
            }
        });

        roofListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.mainfragInteraction(SEE_QUOTES);
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
        if (context instanceof MainfragListener) {
            mListener = (MainfragListener) context;
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
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuIn
//            case R.id.roofView:
//                Intent rviewIntent = new Intent(getActivity(), RviewActivity.class);
//                rviewIntent.putExtra("authToken", authToken);
//                rviewIntent.putExtra("baseUrl", baseUrl);
//                putPrefsData();
//                setResult(RUVIUZ_DATA_PERSIST, rviewIntent);
//                this.startActivityForResult(rviewIntent, RUVIUZ_DATA_PERSIST);
//                break;
//            case R.id.loginAction:
//                Log.d(TAG, "Login action!!");
//                loginDialog();
//                break;
//            case R.id.geoLocate:
//                Log.d(TAG, "GEOLOCATION REQUEST");
//                getGeoLocation();
//                break;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//        return true;
//    }
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
    public interface MainfragListener {
        // TODO: Update argument type and name
        void mainfragInteraction(int action);
    }
}
