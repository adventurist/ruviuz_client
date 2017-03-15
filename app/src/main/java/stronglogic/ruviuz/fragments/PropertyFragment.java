package stronglogic.ruviuz.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import stronglogic.ruviuz.MainActivity;
import stronglogic.ruviuz.R;
import stronglogic.ruviuz.RviewActivity;

import static com.google.android.gms.internal.zzs.TAG;
import static stronglogic.ruviuz.R.drawable.construction;

/**
 * A simple {@link DialogFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PropertyFragment.PropertyListener} interface
 * to handle interaction events.
 * Use the {@link PropertyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PropertyFragment extends DialogFragment implements View.OnClickListener {

    private PropertyListener mListener;

    private MainActivity mActivity;

    private ImageButton upBtn, dwnBtn;

    private MaterialNumberPicker flrPicker;

    private RadioGroup rdyGroup;

    private TextView roofSlope;

    private Spinner materialSpinner;

    private Toolbar mToolbar;

    private int numFloors, cleanupFactor;

    private String material;


    public PropertyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SlopeFragment.
     */
    public static PropertyFragment newInstance(String param1, String param2) {
        PropertyFragment fragment = new PropertyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        if (!((MainActivity) getActivity()).readyStatus()) {
            ((MainActivity)getActivity()).hideActivity();
            ((MainActivity)getActivity()).dismissOtherDialogs(PropertyFragment.this.getClass());
        }
        setStyle(DialogFragment.STYLE_NORMAL, R.style.RuvFullFrag);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = super.getDialog();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            MainActivity.this.findViewById(R.id.MainParentView).setPadding(0, getStatusBarHeight(getActivity()), 0, 0);\
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
                mActivity.revealActivity();
                PropertyFragment.this.dismiss();
            }
        };
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        mActivity.revealActivity();
        PropertyFragment.this.dismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View mView = inflater.inflate(R.layout.fragment_property, container, false);
        Window w = getActivity().getWindow();
        View decorView = w.getDecorView();
        // Show Status Bar.
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

//        mView.setPadding(0, getStatusBarHeight(getActivity()), 0, 0);
        mToolbar = (Toolbar) mView.findViewById(R.id.ruvFragToolbar);
        mToolbar.inflateMenu(R.menu.ruviuz_menu);

        if (mToolbar != null) {
            mToolbar.setNavigationIcon(construction);
//            mToolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.ruvGreen));
            mToolbar.setElevation(8f);
            mToolbar.setTitle(getActivity().getResources().getString(R.string.app_name));
            mToolbar.setTitleTextColor(Color.BLACK);
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
                            rviewIntent.putExtra("baseUrl", MainActivity.baseUrl);
                            mActivity.startActivity(rviewIntent);
                            PropertyFragment.this.dismiss();
                            break;
                        case R.id.loginAction:
                            Log.d(TAG, "Login action!!");
                            mActivity.putPrefsData();
                            mActivity.loginDialog();
                            PropertyFragment.this.dismiss();
                            break;
                        case R.id.geoLocate:
                            Log.d(TAG, "GEOLOCATION REQUEST");
                            mActivity.getGeoLocation();
                            break;
                        case R.id.goHome:
                            Log.d(TAG, "Going HOME");
                            mActivity.welcomeDialog();
                            PropertyFragment.this.dismiss();
                            break;
                    }
                    return true;

                }
            });
        }

        materialSpinner = (Spinner) mView.findViewById(R.id.materialSpin);
        final String[] materials = mActivity.getResources().getStringArray(R.array.roofMaterials);

        ArrayAdapter materialAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_dropdown_item, materials);
        materialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        materialSpinner.setAdapter(materialAdapter);

        if (this.material == null) {
            this.material = materials[0];
        } else {
            for (int i = materials.length; i > 0; i--) {
                if (materialSpinner.getItemAtPosition(i - 1).equals(this.material)) materialSpinner.setSelection(i - 1);
            }
        }

        materialSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PropertyFragment.this.material = materialSpinner.getItemAtPosition(position).toString();
                Log.d(TAG, "Material chosen: " + PropertyFragment.this.material);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button backward = (Button) mView.findViewById(R.id.backBtn);
        backward.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);

        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mActivity.addressDialog();
            }
        });

        Button forward = (Button) mView.findViewById(R.id.fwrdBtn);
        forward.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (material == null || material.equals("")) material = materials[0];
                mListener.propertyFragInteraction(numFloors, material, cleanupFactor);
            }
        });
        flrPicker = (MaterialNumberPicker) mView.findViewById(R.id.floorPicker);
        flrPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                PropertyFragment.this.numFloors = newVal;
                View childView = flrPicker.getChildAt(0);
                if (childView != null) childView.setBackgroundColor(Color.RED);
            }
        });

        upBtn = (ImageButton) mView.findViewById(R.id.floorUp);
        upBtn.setOnClickListener(this);

        dwnBtn = (ImageButton) mView.findViewById(R.id.floorDown);
        dwnBtn.setOnClickListener(this);

        if (PropertyFragment.this.numFloors < 0) {
            PropertyFragment.this.numFloors = 0;
        }

        rdyGroup = (RadioGroup) mView.findViewById(R.id.rdyGroup);
        rdyGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioBtn = (RadioButton) mView.findViewById(checkedId);
                PropertyFragment.this.cleanupFactor = radioBtn.getText().toString().equals("None") ? 0 : radioBtn.getText().toString().equals("Moderate") ? 1 : 2;
            }
        });
        ViewGroup vg = (ViewGroup) decorView;
        Drawable rVector = (Drawable)mActivity.getDrawable(R.drawable.ruv_vector);

//        Animatable ruvMan = (Animatable) rVector, vg;
        ImageView ruvmanView = (ImageView) mView.findViewById(R.id.ruvMan);
        ruvmanView.setBackground(rVector);
        if (rVector instanceof Animatable) {
            ((Animatable) rVector).start();
        }
        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity)
            this.mActivity = (MainActivity) context;
        if (context instanceof PropertyListener) {
            mListener = (PropertyListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PropertyFragInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        roofSlope = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floorUp:
                this.numFloors++;
                flrPicker.setValue(flrPicker.getValue() + 1);
                flrPicker.jumpDrawablesToCurrentState();
                break;
            case R.id.floorDown:
                this.numFloors--;
                flrPicker.setValue(flrPicker.getValue() - 1);
                flrPicker.jumpDrawablesToCurrentState();
                break;
        }
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
    public interface PropertyListener {
        // TODO: Update argument type and name
        void propertyFragInteraction(int numFloors, String material, int cleanupFactor);
    }
}
