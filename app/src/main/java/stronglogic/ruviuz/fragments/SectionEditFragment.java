package stronglogic.ruviuz.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import me.angrybyte.numberpicker.listener.OnValueChangeListener;
import me.angrybyte.numberpicker.view.ActualNumberPicker;
import stronglogic.ruviuz.R;
import stronglogic.ruviuz.content.Customer;
import stronglogic.ruviuz.content.Section;
import stronglogic.ruviuz.views.SectionAdapter;

import static stronglogic.ruviuz.SectionActivity.LENGTH_SELECTED;
import static stronglogic.ruviuz.SectionActivity.TOP_WIDTH_SELECTED;
import static stronglogic.ruviuz.SectionActivity.WIDTH_SELECTED;


/**
 * A simple {@link DialogFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SectionEditFragment.SectionEditListener} interface
 * to handle interaction events.
 * Use the {@link SectionEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SectionEditFragment extends DialogFragment {

    private final static String TAG = "SectionEditFragment";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    
    private Activity mActivity;

    private SectionEditListener mListener;

    private int activePicker;
    private int activeEmptyPicker;

    private me.angrybyte.numberpicker.view.ActualNumberPicker ftPicker, inPicker, emptFtPicker, emptInPicker;

    private Section section;


    private TextView sectionLengthTv, sectionLengthInTv, sectionWidthTv, sectionWidthInTv, sectionTwidthTv, sectionTwidthInTv, sectionTypeTv, selectedTv, emptTv, emptyTv, emptFtTv, emptInTv, sEmptyWidthInTv, emptSelTv, sEmptyLengthInTv, sEmptyLengthTv, sEmptyWidthTv;

    private Button sectionBtn;
    private FloatingActionButton doneBtn;
    private Switch fullToggle;

    private RadioGroup numGroup, emptyNumGroup;
    private RadioButton lgtPickBtn;

    private RelativeLayout widgetWrap;

    private SectionFragment sectionFrag;

    public RecyclerView rv;
    private SectionAdapter secAdapter;

    private Spinner sectionSpinner;

    private String authToken, emptyType, sectionType;

    private int fileCount, currentRid, numFloors, cleanupFactor;
    private float topwidth, width, length, slope;
    private String material, address, postal, city, region;
    private String[] fileUrls = new String[3];
    private String[] fileComments = new String[3];
    private ArrayList<Section> sectionList = new ArrayList<>();
    private Customer mCustomer;
    private boolean premium, ready, editing;

    private Toolbar mToolbar;

    public SectionEditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SectionEditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SectionEditFragment newInstance(String param1, String param2) {
        SectionEditFragment fragment = new SectionEditFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            this.section = getArguments().getParcelable("section");
        }


    }


    @Override
    public void onStart() {
        super.onStart();

        setStyle(DialogFragment.STYLE_NORMAL, R.style.RuvFullFrag);

        Dialog dialog = super.getDialog();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_section_edit, container, false);

        createLayout(view);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SectionEditListener) {
            mListener = (SectionEditListener) context;
            this.mActivity = (Activity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SectionEditListener");
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
    
    public void createLayout(final View view) {
        Window w = mActivity.getWindow();
        w.setStatusBarColor(ContextCompat.getColor(mActivity, R.color.ruvGreenStatus));

        widgetWrap = (RelativeLayout) view.findViewById(R.id.widgetWrap);

        //TODO replace with regular widgets from same fragment
//        addSectionFragment();

        SectionEditFragment.this.activePicker = LENGTH_SELECTED;
        SectionEditFragment.this.activeEmptyPicker = LENGTH_SELECTED;

        SectionEditFragment.this.lgtPickBtn = (RadioButton) view.findViewById(R.id.lgtPickBtn);

        sectionTypeTv = (TextView) view.findViewById(R.id.sectionType);
        sectionLengthTv = (TextView) view.findViewById(R.id.sectionLength);
        sectionWidthTv = (TextView) view.findViewById(R.id.sectionWidth);
        sectionLengthInTv = (TextView) view.findViewById(R.id.sectionLengthIn);
        sectionWidthInTv = (TextView) view.findViewById(R.id.sectionWidthIn);
        sectionTwidthTv = (TextView) view.findViewById(R.id.sectionTopWidth);
        sectionTwidthInTv = (TextView) view.findViewById(R.id.sectionTopWidthIn);

        sectionSpinner = (Spinner) view.findViewById(R.id.sectionTypeSpin);
        String[] sectionTypes = getResources().getStringArray(R.array.sectionTypes);
        ArrayAdapter sectionAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_dropdown_item, sectionTypes);
        sectionSpinner.setAdapter(sectionAdapter);
        sectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SectionEditFragment.this.sectionType = sectionSpinner.getItemAtPosition(position).toString();
                Log.d(TAG, "Type chosen: " + SectionEditFragment.this.sectionType);
//                view.findViewById(R.id.sectionType);
                sectionTypeTv.setText(SectionEditFragment.this.sectionType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sectionSpinner.setSelection(0);
                SectionEditFragment.this.sectionType = sectionSpinner.getItemAtPosition(0).toString();
//                view.findViewById(R.id.sectionType);
                sectionTypeTv.setText(SectionEditFragment.this.sectionType);
            }
        });

//        sectionBtn = (Button) view.findViewById(R.id.dummy_button);
//        sectionBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Section section = new Section();
//
//                if (SectionEditFragment.this.secView != null) {
//
//                    sectionTypeTv = (TextView) view.findViewById(R.id.sectionType);
//                    sectionLengthTv = (TextView) view.findViewById(R.id.sectionLength);
//                    sectionWidthTv = (TextView) view.findViewById(R.id.sectionWidth);
//                    sectionLengthInTv = (TextView) view.findViewById(R.id.sectionLengthIn);
//                    sectionWidthInTv = (TextView) view.findViewById(R.id.sectionWidthIn);
//                    sectionTwidthTv = (TextView) view.findViewById(R.id.sectionTwidth);
//                    sectionTwidthInTv = (TextView) view.findViewById(R.id.sectionTwidthIn);
//
//
//                    if (sectionLengthTv != null && sectionLengthInTv != null) {
//                        float secLen = (sectionLengthTv.getText().toString().equals("") ?
//                                0.0f : Float.valueOf(sectionLengthTv.getText().toString()))
//                                +  (sectionLengthInTv.getText().toString().equals("") ?
//                                0.0f : (Float.valueOf(sectionLengthInTv.getText().toString()) / 12));
//                        section.setLength(secLen);
//                    }
//
//
//                    if (sectionWidthTv != null && sectionWidthInTv != null) {
//                        float secWid = (sectionWidthTv.getText().toString().equals("") ?
//                                0.0f : Float.valueOf(sectionWidthTv.getText().toString()))
//                                +  (sectionWidthInTv.getText().toString().equals("") ?
//                                0.0f : (Float.valueOf(sectionWidthInTv.getText().toString()) / 12));
//                        section.setWidth(secWid);
//                    }
//
//                    if (sectionTwidthTv != null && sectionTwidthInTv != null) {
//                        float secTwid = (sectionTwidthTv.getText().toString().equals("") ?
//                                0.0f : Float.valueOf(sectionTwidthTv.getText().toString()))
//                                +  (sectionTwidthInTv.getText().toString().equals("") ?
//                                0.0f : (Float.valueOf(sectionTwidthInTv.getText().toString()) / 12));
//                        section.setTopWidth(secTwid);
//                    }
//
//                    if (!fullToggle.isChecked()) {
//
//                        SectionEditFragment.this.sEmptyLengthTv = (TextView) view.findViewById(R.id.emptyLength);
//                        SectionEditFragment.this.sEmptyWidthTv = (TextView) view.findViewById(R.id.emptyWidth);
//                        SectionEditFragment.this.sEmptyLengthInTv = (TextView) view.findViewById(R.id.emptyLengthIn);
//                        SectionEditFragment.this.sEmptyWidthInTv = (TextView) view.findViewById(R.id.emptyWidthIn);
//                        SectionEditFragment.this.emptyTv = (TextView) view.findViewById(R.id.eType);
//
//                        if (SectionEditFragment.this.sEmptyLengthTv != null && SectionEditFragment.this.sEmptyWidthTv != null && SectionEditFragment.this.sEmptyLengthInTv != null && SectionEditFragment.this.sEmptyWidthInTv != null) {
//
//                            float emLen = (SectionEditFragment.this.sEmptyLengthTv.getText().toString().equals("") ?
//                                    0.0f : Float.valueOf(SectionEditFragment.this.sEmptyLengthTv.getText().toString()))
//                                    +  (SectionEditFragment.this.sEmptyLengthInTv.getText().toString().equals("") ?
//                                    0.0f : (Float.valueOf(SectionEditFragment.this.sEmptyLengthInTv.getText().toString()) / 12));
//                            float emWid = (SectionEditFragment.this.sEmptyWidthTv.getText().toString().equals("") ?
//                                    0.0f : Float.valueOf(sEmptyWidthTv.getText().toString()))
//                                    +  (SectionEditFragment.this.sEmptyWidthInTv.getText().toString().equals("") ?
//                                    0.0f : (Float.valueOf(SectionEditFragment.this.sEmptyWidthInTv.getText().toString()) / 12));
//
//                            float emptyArea = emLen * emWid;
//
//                            section.toggleFull();
//                            section.setEmptyType(SectionEditFragment.this.emptyType);
//                            section.setMissing(emptyArea);
//
////                                if (SectionEditFragment.sectionTypeTv != null)
////                                SectionEditFragment.sectionTypeTv.setText("");
//                            SectionEditFragment.this.sEmptyLengthTv.setText("");
//                            SectionEditFragment.this.sEmptyWidthTv.setText("");
//                            SectionEditFragment.this.sEmptyLengthInTv.setText("");
//                            SectionEditFragment.this.sEmptyWidthInTv.setText("");
//
//                            SectionEditFragment.this.emptFtPicker.setValue(0);
//                            SectionEditFragment.this.emptFtPicker.jumpDrawablesToCurrentState();
//                            SectionEditFragment.this.emptInPicker.setValue(0);
//                            SectionEditFragment.this.emptInPicker.jumpDrawablesToCurrentState();
//
//
//                            emptyTv.setText("");
//
//                        }
//                    }
//
//                    if (SectionEditFragment.this.slope > -1) {
//                        section.setSlope(SectionEditFragment.this.slope);
//                    }
//
//                    section.setSectionType(SectionEditFragment.this.sectionType);
//
//                    sectionList.add(section);
//
//                    if (secAdapter != null) {
////                            setupRecycler();
//                        secAdapter.notifyItemInserted(sectionList.size() - 1);
//                    }
//
//                    SectionEditFragment.this.ftPicker.setValue(0);
//                    SectionEditFragment.this.ftPicker.jumpDrawablesToCurrentState();
//                    SectionEditFragment.this.inPicker.setValue(0);
//                    SectionEditFragment.this.inPicker.jumpDrawablesToCurrentState();
//
//                    SectionEditFragment.this.lgtPickBtn.setChecked(true);
//
//                    SectionEditFragment.this.sectionLengthTv.setText("");
//                    SectionEditFragment.this.sectionWidthTv.setText("");
//                    SectionEditFragment.this.sectionLengthInTv.setText("");
//                    SectionEditFragment.this.sectionWidthInTv.setText("");
//                    SectionEditFragment.this.sectionTwidthTv.setText("");
//                    SectionEditFragment.this.sectionTwidthInTv.setText("");
//                    SectionEditFragment.this.fullToggle.setChecked(true);
//                }
//
//            }
//        });

        fullToggle = (Switch) view.findViewById(R.id.fullToggle);
        fullToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!isChecked) {
                    final Dialog etypeDialog = new Dialog(mActivity);
                    etypeDialog.setTitle("Choose a Type");

                    if (etypeDialog.getWindow() != null) etypeDialog.getWindow().setWindowAnimations(R.style.ruvanimate);
                    etypeDialog.setContentView(R.layout.empt_dialog);
                    etypeDialog.show();

                    RadioGroup rg = (RadioGroup) etypeDialog.findViewById(R.id.emptyType);
                    rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {

                            RadioButton radioBtn = (RadioButton) etypeDialog.findViewById(checkedId);

                            TextView sEtypeTv = (TextView) view.findViewById(R.id.emptyType);
                            SectionEditFragment.this.emptyType = radioBtn.getText().toString();
                            sEtypeTv.setText(SectionEditFragment.this.emptyType);
                            final Handler xHandler = new Handler();
                            xHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    etypeDialog.dismiss();
                                }
                            }, 400);
                        }
                    });

                    emptyPickersVisible(view);
                } else {
                    emptyPickersHidden(view);
                }
            }
        });

        selectedTv = (TextView) view.findViewById(R.id.selectedTv);

        numGroup = (RadioGroup) view.findViewById(R.id.numpickGrp);
        numGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                SectionEditFragment.this.ftPicker.setValue(0);
                SectionEditFragment.this.inPicker.setValue(0);
                SectionEditFragment.this.ftPicker.jumpDrawablesToCurrentState();
                SectionEditFragment.this.inPicker.jumpDrawablesToCurrentState();

                switch (checkedId) {

                    case R.id.lgtPickBtn:
                        SectionEditFragment.this.activePicker = LENGTH_SELECTED;
                        SectionEditFragment.this.selectedTv.setText(R.string.length);
                        break;
                    case R.id.wdtPickBtn:
                        SectionEditFragment.this.activePicker = WIDTH_SELECTED;
                        SectionEditFragment.this.selectedTv.setText(R.string.width);
                        break;
                    case R.id.tWdthPickBtn:
                        SectionEditFragment.this.activePicker = TOP_WIDTH_SELECTED;
                        SectionEditFragment.this.selectedTv.setText(R.string.top_width);
                        break;

                }
            }
        });

        emptyNumGroup = (RadioGroup) view.findViewById(R.id.emptyNumpickGrp);
        emptyNumGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                SectionEditFragment.this.emptFtPicker.setValue(0);
                SectionEditFragment.this.emptInPicker.setValue(0);
                SectionEditFragment.this.emptFtPicker.jumpDrawablesToCurrentState();
                SectionEditFragment.this.emptInPicker.jumpDrawablesToCurrentState();

                switch (checkedId) {

                    case R.id.emptyLgtPickBtn:
                        SectionEditFragment.this.activeEmptyPicker = LENGTH_SELECTED;
                        if (SectionEditFragment.this.emptSelTv != null)
                            SectionEditFragment.this.emptSelTv.setText(R.string.length);
                        break;
                    case R.id.emptyWdtPickBtn:
                        SectionEditFragment.this.activeEmptyPicker = WIDTH_SELECTED;
                        if (SectionEditFragment.this.emptSelTv != null)
                            SectionEditFragment.this.emptSelTv.setText(R.string.width);
                        break;

                }
            }
        });


        ftPicker = (ActualNumberPicker) view.findViewById(R.id.ftPicker);
        ftPicker.setListener(new OnValueChangeListener() {
            @Override
            public void onValueChanged(int oldValue, int newValue) {
                Log.d(TAG, "Value Changed");
                if (oldValue != newValue) {
                    String newMeasurement;
                    Log.d(TAG, String.valueOf(activePicker));
                    switch (SectionEditFragment.this.activePicker) {
                        case LENGTH_SELECTED:
                            TextView sectionLengthTv = (TextView) view.findViewById(R.id.sectionLength);
                            newMeasurement = String.valueOf((float) newValue);
                            sectionLengthTv.setText(newMeasurement);
                            break;
                        case WIDTH_SELECTED:
                            TextView sectionWidthTv = (TextView) view.findViewById(R.id.sectionWidth);
                            newMeasurement = String.valueOf((float) newValue);
                            sectionWidthTv.setText(newMeasurement);
                            break;
                        case TOP_WIDTH_SELECTED:
                            TextView sectionTwidthTv = (TextView) view.findViewById(R.id.sectionTopWidth);
                            newMeasurement = String.valueOf((float) newValue);
                            sectionTwidthTv.setText(newMeasurement);
                            break;
                    }
                }
            }
        });


        inPicker = (ActualNumberPicker) view.findViewById(R.id.inPicker);
        inPicker.setListener(new OnValueChangeListener() {
            @Override
            public void onValueChanged(int oldValue, int newValue) {
                Log.d(TAG, "Value Changed");
                if (oldValue != newValue) {
                    String newMeasurement;
                    Log.d(TAG, String.valueOf(activePicker));
                    switch (SectionEditFragment.this.activePicker) {
                        case LENGTH_SELECTED:
                            TextView sectionLengthTvIn = (TextView) view.findViewById(R.id.sectionLengthIn);
                            newMeasurement = String.valueOf((float) newValue);
                            sectionLengthTvIn.setText(newMeasurement);
                            break;
                        case WIDTH_SELECTED:
                            TextView sectionWidthTvIn = (TextView) view.findViewById(R.id.sectionWidthIn);
                            newMeasurement = String.valueOf((float) newValue);
                            sectionWidthTvIn.setText(newMeasurement);
                            break;
                        case TOP_WIDTH_SELECTED:
                            TextView sectionTwidthTvIn = (TextView) view.findViewById(R.id.sectionTopWidth);
                            newMeasurement = String.valueOf((float) newValue);
                            sectionTwidthTvIn.setText(newMeasurement);
                            break;
                    }
                }
            }
        });

        emptFtPicker = (ActualNumberPicker) view.findViewById(R.id.emptyPickerFt);
        emptFtPicker.setListener(new OnValueChangeListener() {
            @Override
            public void onValueChanged(int oldValue, int newValue) {
                Log.d(TAG, "Value Changed");
                String newMeasurement = String.valueOf(newValue);
                switch (SectionEditFragment.this.activeEmptyPicker) {
                    case LENGTH_SELECTED:
                        TextView sEmptyLengthTv = (TextView) view.findViewById(R.id.emptyLength);
                        if (sEmptyLengthTv != null) {
                            sEmptyLengthTv.setText(newMeasurement);
                        }
                        break;
                    case WIDTH_SELECTED:
                        TextView emptyWidthTv = (TextView) view.findViewById(R.id.emptyWidth);
                        newMeasurement = String.valueOf((float) newValue);
                        emptyWidthTv.setText(newMeasurement);
                        break;
                }
            }
        });

        emptInPicker = (ActualNumberPicker) view.findViewById(R.id.emptyPickerIn);
        emptInPicker.setListener(new OnValueChangeListener() {
            @Override
            public void onValueChanged(int oldValue, int newValue) {
                Log.d(TAG, "Value Changed");
                String newMeasurement = String.valueOf(newValue);
                switch (SectionEditFragment.this.activeEmptyPicker) {
                    case LENGTH_SELECTED:
                        TextView sEmptyLengthTv = (TextView) view.findViewById(R.id.emptyLengthIn);
                        if (sEmptyLengthTv != null) {
                            sEmptyLengthTv.setText(newMeasurement);
                        }
                        break;
                    case WIDTH_SELECTED:
                        TextView emptyWidthTv = (TextView) view.findViewById(R.id.emptyWidthIn);
                        newMeasurement = String.valueOf((float) newValue);
                        emptyWidthTv.setText(newMeasurement);
                        break;
                }
            }
        });


        if (section != null) {
            sectionTypeTv.setText(section.getSectionType());
            sectionLengthTv.setText(String.valueOf(section.getLength()));
            sectionWidthTv.setText(String.valueOf(section.getWidth()));
//            sectionLengthInTv.setText();
//            sectionWidthInTv.setText();
            sectionTwidthTv.setText(String.valueOf(section.getTopWidth()));
//            sectionWidthInTv.setText();

            for (int i = sectionTypes.length; i > 0; i--) {
                if (sectionSpinner.getItemAtPosition(i - 1).equals(section.getSectionType())) sectionSpinner.setSelection(i - 1);
            }

            if (!section.isFull()) fullToggle.setChecked(false);

            selectedTv.setText(section.getSectionType());

        }


//        doneBtn = (FloatingActionButton) view.findViewById(R.id.doneBtn);
//        doneBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //TODO Change this
//                // Intent mIntent = new Intent(mActivity, MainActivity.class);
////                putIntentData(mIntent);
////                setResult(MainActivity.SECTION_ACTIVITY_COMPLETE, mIntent);
////                finish();
//            }
//        });

//        setupRecycler();
    }

    private void emptyPickersVisible(View view) {
        if (SectionEditFragment.this.emptTv == null) SectionEditFragment.this.emptTv = (TextView) view.findViewById(R.id.emptyTv);
        SectionEditFragment.this.emptTv.setVisibility(View.VISIBLE);
        if (SectionEditFragment.this.emptFtPicker == null) SectionEditFragment.this.emptFtPicker = (ActualNumberPicker) view.findViewById(R.id.emptyPickerFt);
        SectionEditFragment.this.emptFtPicker.setVisibility(View.VISIBLE);
        if (SectionEditFragment.this.emptInPicker == null) SectionEditFragment.this.emptInPicker = (ActualNumberPicker) view.findViewById(R.id.emptyPickerIn);
        SectionEditFragment.this.emptInPicker.setVisibility(View.VISIBLE);
        if (SectionEditFragment.this.emptFtTv== null) SectionEditFragment.this.emptFtTv = (TextView) view.findViewById(R.id.emptyFtTv);
        SectionEditFragment.this.emptFtTv.setVisibility(View.VISIBLE);
        if (SectionEditFragment.this.emptInTv == null) SectionEditFragment.this.emptInTv = (TextView) view.findViewById(R.id.emptyInTv);
        SectionEditFragment.this.emptInTv.setVisibility(View.VISIBLE);
        if (SectionEditFragment.this.emptSelTv == null) SectionEditFragment.this.emptSelTv = (TextView) view.findViewById(R.id.selectedEmptyTv);
        SectionEditFragment.this.emptSelTv.setVisibility(View.VISIBLE);
        if (SectionEditFragment.this.emptyNumGroup == null) SectionEditFragment.this.emptyNumGroup = (RadioGroup) view.findViewById(R.id.emptyNumpickGrp);
        SectionEditFragment.this.emptyNumGroup.setVisibility(View.VISIBLE);
    }

    private void emptyPickersHidden(View view) {
        if (SectionEditFragment.this.emptTv == null) SectionEditFragment.this.emptTv = (TextView) view.findViewById(R.id.emptyTv);
        SectionEditFragment.this.emptTv.setVisibility(View.GONE);
        if (SectionEditFragment.this.emptFtPicker == null) SectionEditFragment.this.emptFtPicker = (ActualNumberPicker) view.findViewById(R.id.emptyPickerFt);
        SectionEditFragment.this.emptFtPicker.setVisibility(View.GONE);
        if (SectionEditFragment.this.emptInPicker == null) SectionEditFragment.this.emptInPicker = (ActualNumberPicker) view.findViewById(R.id.emptyPickerIn);
        SectionEditFragment.this.emptInPicker.setVisibility(View.GONE);
        if (SectionEditFragment.this.emptFtTv== null) SectionEditFragment.this.emptFtTv = (TextView) view.findViewById(R.id.emptyFtTv);
        SectionEditFragment.this.emptFtTv.setVisibility(View.GONE);
        if (SectionEditFragment.this.emptInTv == null) SectionEditFragment.this.emptInTv = (TextView) view.findViewById(R.id.emptyInTv);
        SectionEditFragment.this.emptInTv.setVisibility(View.GONE);
        if (SectionEditFragment.this.emptSelTv == null) SectionEditFragment.this.emptSelTv = (TextView) view.findViewById(R.id.selectedEmptyTv);
        SectionEditFragment.this.emptSelTv.setVisibility(View.GONE);
        if (SectionEditFragment.this.emptyNumGroup == null) SectionEditFragment.this.emptyNumGroup = (RadioGroup) view.findViewById(R.id.emptyNumpickGrp);
        SectionEditFragment.this.emptyNumGroup.setVisibility(View.GONE);
    }

    public interface SectionEditListener {
        // TODO: Update argument type and name
        void sectionEditInteraction(int data);
    }
}
