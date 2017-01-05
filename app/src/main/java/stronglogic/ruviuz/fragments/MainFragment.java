package stronglogic.ruviuz.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;

import stronglogic.ruviuz.R;

/**
 * A simple {@link DialogFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.MainfragListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final static int CREATE_QUOTE = 33;
    private final static int SEE_QUOTES = 34;

    private ImageButton quoteBtn, roofListBtn;

    private MainfragListener mListener;

    private android.support.v7.widget.Toolbar mToolbar;


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
//        if (getArguments() != null) {
//        }
        setStyle(DialogFragment.STYLE_NORMAL, R.style.RuvFullFrag);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
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
//        Display display = getActivity().getWindowManager().getDefaultDisplay();
//        int width = display.getWidth();
//        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, **220**, getResources().getDisplayMetrics());
//
//        Dialog dialog = super.getDialog();
//        if (dialog != null) {
//            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.mainfragment, container, false);

        mToolbar = (Toolbar) mView.findViewById(R.id.ruvFragToolbar);

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle the menu item
                return true;
            }
        });
        mToolbar.inflateMenu(R.menu.ruviuz_menu);
        mToolbar.setTitle("TEST");

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
