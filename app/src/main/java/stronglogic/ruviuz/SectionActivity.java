package stronglogic.ruviuz;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import android.widget.Toast;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.angrybyte.numberpicker.listener.OnValueChangeListener;
import me.angrybyte.numberpicker.view.ActualNumberPicker;
import stronglogic.ruviuz.content.Customer;
import stronglogic.ruviuz.content.Section;
import stronglogic.ruviuz.fragments.SectionFragment;
import stronglogic.ruviuz.util.RuvLocation;
import stronglogic.ruviuz.views.SectionAdapter;

import static stronglogic.ruviuz.MainActivity.updateMenuWithIcon;


public class SectionActivity extends AppCompatActivity implements SectionFragment.SectionListener {

    private static final String TAG = "RuviuzSECTIONACTIVITY";

    private final static int LENGTH_SELECTED = 300;
    private final static int WIDTH_SELECTED = 301;
    private final static int TOP_WIDTH_SELECTED = 302;

    private int activePicker;

    private me.angrybyte.numberpicker.view.ActualNumberPicker ftPicker, inPicker, sEmptyLengthPickFt, sEmptyLengthPickIn, sEmptyWidthPickFt, sEmptyWidthPickIn;
    private TextView sectionLengthTv, sectionLengthInTv, sectionWidthTv, sectionWidthInTv, sectionTwidthTv, sectionTwidthInTv, sectionTypeTv, sEmptyLength, sEmptyWidth, sEmptyWidthFtTv, sEmptyWidthInTv, sEmptyLengthFtTv, sEmptyLengthInTv, sEmptyLengthTv, sEmptyWidthTv;
    
    private Button sectionBtn;
    private FloatingActionButton doneBtn;
    private Switch fullToggle;

    RadioGroup numGroup;
    RadioButton lgtPickBtn, wdtPickBtn, tWdtPickBtn;
    private RelativeLayout widgetWrap;

    private SectionFragment sectionFrag;

    public RecyclerView rv;
    private SectionAdapter secAdapter;

    private Spinner sectionSpinner;

    private String authToken, emptyType, sectionType;

    private int fileCount, currentRid;
    private float topwidth, width, length, slope;
    private String material, address, postal, city, region;
    private String[] fileUrls = new String[3];
    private String[] fileComments = new String[3];
    private ArrayList<Section> sectionList = new ArrayList<>();
    private Customer mCustomer;
    private boolean premium, ready, editing;

    private Toolbar mToolbar;

    private View secView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_section);
        Window w = getWindow();
        w.setStatusBarColor(ContextCompat.getColor(SectionActivity.this, R.color.ruvGreenStatus));

        mToolbar = (Toolbar) findViewById(R.id.app_bar);

        setSupportActionBar(mToolbar);

        if (getSupportActionBar()!= null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.construction);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(8f);
        }


        widgetWrap = (RelativeLayout) findViewById(R.id.widgetWrap);

        addSectionFragment();

        if (getIntent() != null) {
            getIntentData(getIntent());
        }

        SectionActivity.this.activePicker = LENGTH_SELECTED;

        sectionSpinner = (Spinner) findViewById(R.id.sectionTypeSpin);
        String[] sectionTypes = getResources().getStringArray(R.array.sectionTypes);
        ArrayAdapter sectionAdapter = new ArrayAdapter<>(SectionActivity.this, android.R.layout.simple_spinner_dropdown_item, sectionTypes);
        sectionSpinner.setAdapter(sectionAdapter);
        sectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SectionActivity.this.sectionType = sectionSpinner.getItemAtPosition(position).toString();
                Log.d(TAG, "Type chosen: " + SectionActivity.this.sectionType);
                if (sectionFrag.getView() != null) {
                    TextView sectionTypeTv = (TextView) sectionFrag.getView().findViewById(R.id.sectionType);
                    sectionTypeTv.setText(SectionActivity.this.sectionType);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sectionSpinner.setSelection(0);
                SectionActivity.this.sectionType = sectionSpinner.getItemAtPosition(0).toString();
                if (sectionFrag.getView() != null) {
                    TextView sectionTypeTv = (TextView) sectionFrag.getView().findViewById(R.id.sectionType);
                    sectionTypeTv.setText(SectionActivity.this.sectionType);
                }
            }
        });



//        if (SectionActivity.this.secView != null) {
//
//            sectionTypeTv = (TextView) secView.findViewById(R.id.sectionType);
//            sectionLengthTv = (TextView) secView.findViewById(R.id.sectionLength);
//            sectionWidthTv = (TextView) secView.findViewById(R.id.sectionWidth);
//            sectionLengthInTv = (TextView) secView.findViewById(R.id.sectionLengthIn);
//            sectionWidthInTv = (TextView) secView.findViewById(R.id.sectionWidthIn);
//            sectionTwidthTv = (TextView) secView.findViewById(R.id.sectionTwidth);
//            sectionTwidthInTv = (TextView) secView.findViewById(R.id.sectionTwidthIn);
//
//        }

        sectionBtn = (Button) findViewById(R.id.dummy_button);
        sectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sectionFrag == null || !sectionFrag.isAdded()) {
                    addSectionFragment();
                } else {
                    Section section = new Section();

                    if (SectionActivity.this.secView != null) {

                            sectionTypeTv = (TextView) SectionActivity.this.secView.findViewById(R.id.sectionType);
                            sectionLengthTv = (TextView) SectionActivity.this.secView.findViewById(R.id.sectionLength);
                            sectionWidthTv = (TextView) SectionActivity.this.secView.findViewById(R.id.sectionWidth);
                            sectionLengthInTv = (TextView) SectionActivity.this.secView.findViewById(R.id.sectionLengthIn);
                            sectionWidthInTv = (TextView) SectionActivity.this.secView.findViewById(R.id.sectionWidthIn);
                            sectionTwidthTv = (TextView) SectionActivity.this.secView.findViewById(R.id.sectionTwidth);
                            sectionTwidthInTv = (TextView) SectionActivity.this.secView.findViewById(R.id.sectionTwidthIn);


                        if (sectionLengthTv != null && sectionLengthInTv != null) {
                            float secLen = (sectionLengthTv.getText().toString().equals("") ?
                                    0.0f : Float.valueOf(sectionLengthTv.getText().toString()))
                                    +  (sectionLengthInTv.getText().toString().equals("") ?
                                    0.0f : (Float.valueOf(sectionLengthInTv.getText().toString()) / 12));
                            section.setLength(secLen);
                        }


                        if (sectionWidthTv != null && sectionWidthInTv != null) {
                            float secWid = (sectionWidthTv.getText().toString().equals("") ?
                                    0.0f : Float.valueOf(sectionWidthTv.getText().toString()))
                                    +  (sectionWidthInTv.getText().toString().equals("") ?
                                    0.0f : (Float.valueOf(sectionWidthInTv.getText().toString()) / 12));
                            section.setWidth(secWid);
                        }

                        if (sectionTwidthTv != null && sectionTwidthInTv != null) {
                            float secTwid = (sectionTwidthTv.getText().toString().equals("") ?
                                    0.0f : Float.valueOf(sectionTwidthTv.getText().toString()))
                                    +  (sectionTwidthInTv.getText().toString().equals("") ?
                                    0.0f : (Float.valueOf(sectionTwidthInTv.getText().toString()) / 12));
                            section.setTopWidth(secTwid);
                        }

                        if (!fullToggle.isChecked()) {

                            SectionActivity.this.sEmptyLengthTv = (TextView) SectionActivity.this.secView.findViewById(R.id.emptyLength);
                            SectionActivity.this.sEmptyWidthTv = (TextView) SectionActivity.this.secView.findViewById(R.id.emptyWidth);
                            SectionActivity.this.sEmptyLengthInTv = (TextView) SectionActivity.this.secView.findViewById(R.id.emptyLengthIn);
                            SectionActivity.this.sEmptyWidthInTv = (TextView) SectionActivity.this.secView.findViewById(R.id.emptyWidthIn);
                            if (SectionActivity.this.sEmptyLengthTv != null && SectionActivity.this.sEmptyWidthTv != null && SectionActivity.this.sEmptyLengthInTv != null && SectionActivity.this.sEmptyWidthInTv != null) {
                                float emLen = (SectionActivity.this.sEmptyLengthTv.getText().toString().equals("") ?
                                        0.0f : Float.valueOf(SectionActivity.this.sEmptyLengthTv.getText().toString()))
                                        +  (SectionActivity.this.sEmptyLengthInTv.getText().toString().equals("") ?
                                        0.0f : (Float.valueOf(SectionActivity.this.sEmptyLengthInTv.getText().toString()) / 12));
                                float emWid = (SectionActivity.this.sEmptyWidthTv.getText().toString().equals("") ?
                                        0.0f : Float.valueOf(sEmptyWidthTv.getText().toString()))
                                        +  (SectionActivity.this.sEmptyWidthInTv.getText().toString().equals("") ?
                                        0.0f : (Float.valueOf(SectionActivity.this.sEmptyWidthInTv.getText().toString()) / 12));

                                float emptyArea = emLen * emWid;

                                section.toggleFull();
                                section.setEmptyType(SectionActivity.this.emptyType);
                                section.setMissing(emptyArea);
                                if (SectionActivity.this.sectionTypeTv != null)
                                SectionActivity.this.sectionTypeTv.setText("");
                                SectionActivity.this.sEmptyLengthTv.setText("");
                                SectionActivity.this.sEmptyWidthTv.setText("");
                                SectionActivity.this.sEmptyLengthInTv.setText("");
                                SectionActivity.this.sEmptyWidthInTv.setText("");
                                SectionActivity.this.sEmptyLengthPickFt.setValue(0);
                                SectionActivity.this.sEmptyLengthPickFt.jumpDrawablesToCurrentState();
                                SectionActivity.this.sEmptyLengthPickIn.setValue(0);
                                SectionActivity.this.sEmptyLengthPickIn.jumpDrawablesToCurrentState();
                                SectionActivity.this.sEmptyWidthPickFt.setValue(0);
                                SectionActivity.this.sEmptyWidthPickFt.jumpDrawablesToCurrentState();
                                SectionActivity.this.sEmptyWidthPickIn.setValue(0);
                                SectionActivity.this.sEmptyWidthPickIn.jumpDrawablesToCurrentState();

                            }
                        }

                        if (SectionActivity.this.slope > -1) {
                            section.setSlope(SectionActivity.this.slope);
                        }

                        section.setSectionType(SectionActivity.this.sectionType);

                        sectionList.add(section);

                        if (secAdapter != null) {
//                            setupRecycler();
                            secAdapter.notifyItemInserted(sectionList.size() - 1);
                        }

                        SectionActivity.this.ftPicker.setValue(0);
                        SectionActivity.this.ftPicker.jumpDrawablesToCurrentState();
                        SectionActivity.this.inPicker.setValue(0);
                        SectionActivity.this.inPicker.jumpDrawablesToCurrentState();

                        SectionActivity.this.sectionLengthTv.setText("");
                        SectionActivity.this.sectionWidthTv.setText("");
                        SectionActivity.this.sectionLengthInTv.setText("");
                        SectionActivity.this.sectionWidthInTv.setText("");
                        SectionActivity.this.sectionTwidthTv.setText("");
                        SectionActivity.this.sectionTwidthInTv.setText("");
                        SectionActivity.this.fullToggle.setChecked(true);
                    }
                }
            }
        });

        fullToggle = (Switch) findViewById(R.id.fullToggle);
        fullToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    final Dialog etypeDialog = new Dialog(SectionActivity.this);
                    etypeDialog.setTitle("Choose a Type");
                    if (etypeDialog.getWindow() != null) etypeDialog.getWindow().setWindowAnimations(R.style.ruvanimate);
                    etypeDialog.setContentView(R.layout.empt_dialog);
                    etypeDialog.show();
                    RadioGroup rg = (RadioGroup) etypeDialog.findViewById(R.id.emptyType);
                    rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            RadioButton radioBtn = (RadioButton) etypeDialog.findViewById(checkedId);
                            if (SectionActivity.this.secView == null) SectionActivity.this.secView = sectionFrag.getView();

                                TextView sEtypeTv = (TextView) SectionActivity.this.secView.findViewById(R.id.eType);
                                SectionActivity.this.emptyType = radioBtn.getText().toString();
                                sEtypeTv.setText(SectionActivity.this.emptyType);
                            etypeDialog.dismiss();
                        }
                    });


                    emptyPickersVisible();
                } else {
                    emptyPickersHidden();
                }
            }
        });

        numGroup = (RadioGroup) findViewById(R.id.numpickGrp);
        numGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.lgtPickBtn:
                        SectionActivity.this.activePicker = LENGTH_SELECTED;
                        break;
                    case R.id.wdtPickBtn:
                        SectionActivity.this.activePicker = WIDTH_SELECTED;
                        break;
                    case R.id.tWdthPickBtn:
                        SectionActivity.this.activePicker = TOP_WIDTH_SELECTED;

                }

            }
        });


        ftPicker = (ActualNumberPicker) findViewById(R.id.ftPicker);
        ftPicker.setListener(new OnValueChangeListener() {
            @Override
            public void onValueChanged(int oldValue, int newValue) {
                Log.d(TAG, "Value Changed");
                if (sectionFrag == null || !sectionFrag.isAdded()) addSectionFragment();
                if (SectionActivity.this.secView == null) SectionActivity.this.secView = sectionFrag.getView();
                String newMeasurement;
                Log.d(TAG, String.valueOf(activePicker));
                switch (SectionActivity.this.activePicker) {
                    case LENGTH_SELECTED:
                        TextView sectionLengthTv = (TextView) SectionActivity.this.secView.findViewById(R.id.sectionLength);
                        newMeasurement = String.valueOf((float) newValue);
                        sectionLengthTv.setText(newMeasurement);
                        break;
                    case WIDTH_SELECTED:
                        TextView sectionWidthTv = (TextView) SectionActivity.this.secView.findViewById(R.id.sectionWidth);
                        newMeasurement = String.valueOf((float) newValue);
                        sectionWidthTv.setText(newMeasurement);
                        break;
                    case TOP_WIDTH_SELECTED:
                        TextView sectionTwidthTv = (TextView) SectionActivity.this.secView.findViewById(R.id.sectionTwidth);
                        newMeasurement = String.valueOf((float) newValue);
                        sectionTwidthTv.setText(newMeasurement);
                        break;
                }

            }
        });

        inPicker = (ActualNumberPicker) findViewById(R.id.inPicker);
        inPicker.setListener(new OnValueChangeListener() {
            @Override
            public void onValueChanged(int oldValue, int newValue) {
                Log.d(TAG, "Value Changed");
                if (sectionFrag == null || !sectionFrag.isAdded()) addSectionFragment();
                if (SectionActivity.this.secView == null) SectionActivity.this.secView = sectionFrag.getView();
                String newMeasurement;
                Log.d(TAG, String.valueOf(activePicker));
                switch (SectionActivity.this.activePicker) {
                    case LENGTH_SELECTED:
                        TextView sectionLengthTvIn = (TextView) SectionActivity.this.secView.findViewById(R.id.sectionLengthIn);
                        newMeasurement = String.valueOf((float) newValue);
                        sectionLengthTvIn.setText(newMeasurement);
                        break;
                    case WIDTH_SELECTED:
                        TextView sectionWidthTvIn = (TextView) SectionActivity.this.secView.findViewById(R.id.sectionWidthIn);
                        newMeasurement = String.valueOf((float) newValue);
                        sectionWidthTvIn.setText(newMeasurement);
                        break;
                    case TOP_WIDTH_SELECTED:
                        TextView sectionTwidthTvIn = (TextView) SectionActivity.this.secView.findViewById(R.id.sectionTwidthIn);
                        newMeasurement = String.valueOf((float) newValue);
                        sectionTwidthTvIn.setText(newMeasurement);
                        break;
                }
            }
        });

        sEmptyLengthPickFt = (ActualNumberPicker) findViewById(R.id.emptyLengthPickerFt);
        sEmptyLengthPickFt.setListener(new OnValueChangeListener() {
            @Override
            public void onValueChanged(int oldValue, int newValue) {
                if (sectionFrag == null || !sectionFrag.isAdded()) addSectionFragment();
                if (sectionFrag.getView() != null) {
                    TextView sEmptyLengthTv = (TextView) sectionFrag.getView().findViewById(R.id.emptyLength);
                    if (sEmptyLengthTv != null) {
                        String newMeasurement = String.valueOf(newValue);
                        sEmptyLengthTv.setText(newMeasurement);
                    }
                }

            }
        });

        sEmptyLengthPickIn = (ActualNumberPicker) findViewById(R.id.emptyLengthPickerIn);
        sEmptyLengthPickIn.setListener(new OnValueChangeListener() {
            @Override
            public void onValueChanged(int oldValue, int newValue) {
                if (sectionFrag == null || !sectionFrag.isAdded()) addSectionFragment();
                if (sectionFrag.getView() != null) {
                    TextView sEmptyLengthInTv = (TextView) sectionFrag.getView().findViewById(R.id.emptyLengthIn);
                    if (sEmptyLengthInTv != null) {
                        float newMeasurement = (float) (newValue);
                        String newString = String.valueOf(newMeasurement);
                        sEmptyLengthInTv.setText(newString);
                    }
                }
            }
        });

        sEmptyWidthPickFt = (ActualNumberPicker) findViewById(R.id.emptyWidthPickerFt);
        sEmptyWidthPickFt.setListener(new OnValueChangeListener() {
            @Override
            public void onValueChanged(int oldValue, int newValue) {
                if (sectionFrag == null || !sectionFrag.isAdded()) addSectionFragment();
                if (sectionFrag.getView() != null) {
                    TextView sEmptyWidthTv = (TextView) sectionFrag.getView().findViewById(R.id.emptyWidth);
                    if (sEmptyWidthTv != null) {
                        String newMeasurement = String.valueOf(newValue);
                        sEmptyWidthTv.setText(newMeasurement);
                    }
                }

            }
        });

        sEmptyWidthPickIn = (ActualNumberPicker) findViewById(R.id.emptyWidthPickerIn);
        sEmptyWidthPickIn.setListener(new OnValueChangeListener() {
            @Override
            public void onValueChanged(int oldValue, int newValue) {
                if (sectionFrag == null || !sectionFrag.isAdded()) addSectionFragment();
                if (sectionFrag.getView() != null) {
                    TextView sEmptyWidthInTv = (TextView) sectionFrag.getView().findViewById(R.id.emptyWidthIn);
                    if (sEmptyWidthInTv != null) {
                        float newMeasurement = (float) newValue;
                        String newString = String.valueOf(newMeasurement);
                        sEmptyWidthInTv.setText(newString);
                    }
                }

            }
        });

        doneBtn = (FloatingActionButton) findViewById(R.id.doneBtn);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(SectionActivity.this, MainActivity.class);
                putIntentData(mIntent);
                setResult(MainActivity.SECTION_ACTIVITY_COMPLETE, mIntent);
                finish();
            }
        });

        setupRecycler();
    }

    private void addSectionFragment() {
        FragmentManager fm = getFragmentManager();
        if (sectionFrag == null) { sectionFrag = new SectionFragment(); }

            if (!sectionFrag.isAdded()) {

                fm.beginTransaction().add(R.id.section_fragment, sectionFrag).commit();

            }
        this.secView = sectionFrag.getView();
    }


    public void setupRecycler()  {
        ArrayList<Section> feedList = SectionActivity.this.sectionList;
        if (feedList.size() > -1) {
            this.secAdapter = new SectionAdapter(SectionActivity.this, feedList);
            LinearLayoutManager layoutMgr = new LinearLayoutManager(getBaseContext(),
                    LinearLayoutManager.VERTICAL, false);
            rv = (RecyclerView) findViewById(R.id.sectionView);
            rv.setAdapter(secAdapter);
            rv.setLayoutManager(layoutMgr);
            rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
//                    scrollChange(recyclerView, newState);
                }
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy)  {
//                    scrolled(recyclerView, dx, dy);
                }

            });
            layoutMgr.setAutoMeasureEnabled(true);
            layoutMgr.setRecycleChildrenOnDetach(true);
            rv.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());

        }
    }

    @Override
    public void sectionFragInteraction(Bundle bundle, int request) {

    }

    private void emptyPickersVisible() {
        if (SectionActivity.this.sEmptyLength == null) SectionActivity.this.sEmptyLength = (TextView) findViewById(R.id.emptyLengthTv);
        SectionActivity.this.sEmptyLength.setVisibility(View.VISIBLE);
        if (SectionActivity.this.sEmptyLengthPickFt == null) SectionActivity.this.sEmptyLengthPickFt = (ActualNumberPicker) findViewById(R.id.emptyLengthPickerFt);
        SectionActivity.this.sEmptyLengthPickFt.setVisibility(View.VISIBLE);
        if (SectionActivity.this.sEmptyLengthFtTv == null) SectionActivity.this.sEmptyLengthFtTv = (TextView) findViewById(R.id.emptyLengthFtTv);
        SectionActivity.this.sEmptyLengthFtTv.setVisibility(View.VISIBLE);
        if (SectionActivity.this.sEmptyLengthPickIn == null) SectionActivity.this.sEmptyLengthPickIn = (ActualNumberPicker) findViewById(R.id.emptyLengthPickerIn);
        SectionActivity.this.sEmptyLengthPickIn.setVisibility(View.VISIBLE);
        if (SectionActivity.this.sEmptyLengthInTv == null) SectionActivity.this.sEmptyLengthInTv = (TextView) findViewById(R.id.emptyLengthInTv);
        SectionActivity.this.sEmptyLengthInTv.setVisibility(View.VISIBLE);
        if (SectionActivity.this.sEmptyWidth == null) SectionActivity.this.sEmptyWidth = (TextView) findViewById(R.id.emptyWidthTv);
        SectionActivity.this.sEmptyWidth.setVisibility(View.VISIBLE);
        if (SectionActivity.this.sEmptyWidthPickFt == null) SectionActivity.this.sEmptyWidthPickFt = (ActualNumberPicker) findViewById(R.id.emptyWidthPickerFt);
        SectionActivity.this.sEmptyWidthPickFt.setVisibility(View.VISIBLE);
        if (SectionActivity.this.sEmptyWidthFtTv == null) SectionActivity.this.sEmptyWidthFtTv = (TextView) findViewById(R.id.emptyWidthFtTv);
        SectionActivity.this.sEmptyWidthFtTv.setVisibility(View.VISIBLE);
        if (SectionActivity.this.sEmptyWidthPickIn == null) SectionActivity.this.sEmptyWidthPickIn = (ActualNumberPicker) findViewById(R.id.emptyWidthPickerIn);
        SectionActivity.this.sEmptyWidthPickIn.setVisibility(View.VISIBLE);
        if (SectionActivity.this.sEmptyWidthInTv == null) SectionActivity.this.sEmptyWidthInTv = (TextView) findViewById(R.id.emptyWidthInTv);
        SectionActivity.this.sEmptyWidthInTv.setVisibility(View.VISIBLE);
    }

    private void emptyPickersHidden() {
        if (SectionActivity.this.sEmptyLength == null) SectionActivity.this.sEmptyWidth = (TextView) findViewById(R.id.emptyLengthTv);
        SectionActivity.this.sEmptyWidth.setVisibility(View.GONE);
        if (SectionActivity.this.sEmptyLengthPickFt == null) SectionActivity.this.sEmptyLengthPickFt = (ActualNumberPicker) findViewById(R.id.emptyLengthPickerFt);
        SectionActivity.this.sEmptyLengthPickFt.setVisibility(View.GONE);
        if (SectionActivity.this.sEmptyLengthFtTv == null) SectionActivity.this.sEmptyLengthFtTv = (TextView) findViewById(R.id.emptyLengthFtTv);
        SectionActivity.this.sEmptyLengthFtTv.setVisibility(View.GONE);
        if (SectionActivity.this.sEmptyLengthPickIn == null) SectionActivity.this.sEmptyLengthPickIn = (ActualNumberPicker) findViewById(R.id.emptyLengthPickerIn);
        SectionActivity.this.sEmptyLengthPickIn.setVisibility(View.GONE);
        if (SectionActivity.this.sEmptyLengthInTv == null) SectionActivity.this.sEmptyLengthInTv = (TextView) findViewById(R.id.emptyLengthInTv);
        SectionActivity.this.sEmptyLengthInTv.setVisibility(View.GONE);
        if (SectionActivity.this.sEmptyWidth == null) SectionActivity.this.sEmptyWidth = (TextView) findViewById(R.id.emptyLengthTv);
        SectionActivity.this.sEmptyWidth.setVisibility(View.GONE);
        if (SectionActivity.this.sEmptyWidthPickFt == null) SectionActivity.this.sEmptyWidthPickFt = (ActualNumberPicker) findViewById(R.id.emptyWidthPickerFt);
        SectionActivity.this.sEmptyWidthPickFt.setVisibility(View.GONE);
        if (SectionActivity.this.sEmptyWidthFtTv == null) SectionActivity.this.sEmptyWidthFtTv = (TextView) findViewById(R.id.emptyWidthFtTv);
        SectionActivity.this.sEmptyWidthFtTv.setVisibility(View.GONE);
        if (SectionActivity.this.sEmptyWidthPickIn == null) SectionActivity.this.sEmptyWidthPickIn = (ActualNumberPicker) findViewById(R.id.emptyWidthPickerIn);
        SectionActivity.this.sEmptyWidthPickIn.setVisibility(View.GONE);
        if (SectionActivity.this.sEmptyWidthInTv == null) SectionActivity.this.sEmptyWidthInTv = (TextView) findViewById(R.id.emptyWidthInTv);
        SectionActivity.this.sEmptyWidthInTv.setVisibility(View.GONE);
    }


    public void putIntentData(Intent intent) {
        intent.putExtra("authToken", this.authToken);
        intent.putExtra("slope", this.slope);
        intent.putExtra("width", this.width);
        intent.putExtra("length", this.length);
        intent.putExtra("address", this.address);
        intent.putExtra("postal", this.postal);
        intent.putExtra("city", this.city);
        intent.putExtra("region", this.region);
        intent.putExtra("premium", this.premium);
        intent.putExtra("material", this.material);
        intent.putExtra("currentRid", this.currentRid);
        intent.putExtra("fileCount", this.fileCount);
        intent.putExtra("fileUrls", this.fileUrls);
        intent.putExtra("fileComments", this.fileComments);
        intent.putExtra("baseUrl", MainActivity.baseUrl);
        intent.putExtra("editing", SectionActivity.this.editing);
        intent.putExtra("ready", this.ready);
        if (mCustomer != null) {
            try {
                JSONObject customerJson = new JSONObject();
                customerJson.put("firstName", mCustomer.getFirstname());
                customerJson.put("lastName", mCustomer.getLastname());
                customerJson.put("email", mCustomer.getEmail());
                customerJson.put("phone", mCustomer.getPhone());
                customerJson.put("prefix", mCustomer.getPrefix());
                customerJson.put("married", mCustomer.getMarried());
                intent.putExtra("customer", customerJson.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        intent.putParcelableArrayListExtra("sectionList", sectionList);
    }

    public void getIntentData(Intent intent) {
        this.ready = intent.getBooleanExtra("ready", false);
        this.authToken = intent.getStringExtra("authToken");
        this.slope = intent.getFloatExtra("slope", 0);
        this.width = intent.getFloatExtra("width", 0);
        this.length = intent.getFloatExtra("length", 0);
        this.address = intent.getStringExtra("address");
        this.postal = intent.getStringExtra("postal");
        this.city= intent.getStringExtra("city");
        this.region= intent.getStringExtra("region");
        this.premium = intent.getBooleanExtra("premium", false);
        this.material = intent.getStringExtra("material");
        this.currentRid = intent.getIntExtra("currentRid", -1);
        this.fileCount = intent.getIntExtra("fileCount", 0);
        this.fileUrls = intent.getStringArrayExtra("fileUrls");
        this.fileComments = intent.getStringArrayExtra("fileComments");
        this.editing = intent.getBooleanExtra("editing", false);
        if (intent.hasExtra("customer"))
            try {
                JSONObject customerJson = new JSONObject(intent.getStringExtra("customer"));
                if (this.mCustomer == null) this.mCustomer = new Customer();
                this.mCustomer.setFirstname(customerJson.get("firstName").toString());
                this.mCustomer.setLastname(customerJson.get("lastName").toString());
                this.mCustomer.setEmail(customerJson.get("email").toString());
                this.mCustomer.setPhone(customerJson.get("phone").toString());
                this.mCustomer.setPrefix(customerJson.get("prefix").toString());
                this.mCustomer.setMarried(Boolean.valueOf(customerJson.get("married").toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        if (intent.getParcelableArrayListExtra("sectionList") != null)
            this.sectionList = intent.getParcelableArrayListExtra("sectionList");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ruviuz_menu, menu);
        updateMenuWithIcon(menu.findItem(R.id.loginAction), Color.WHITE);
        updateMenuWithIcon(menu.findItem(R.id.geoLocate), Color.WHITE);
        updateMenuWithIcon(menu.findItem(R.id.goHome), Color.WHITE);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, item.toString());
        switch (item.getItemId()) {
            case android.R.id.home:
//                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
//                    mDrawerLayout.closeDrawer(GravityCompat.START);
//                } else {
//                    mDrawerLayout.openDrawer(GravityCompat.START);
//                }
                break;
            case R.id.roofView:
                Intent rviewIntent = new Intent(this, RviewActivity.class);
                putIntentData(rviewIntent);
                rviewIntent.putExtra("baseUrl", MainActivity.baseUrl);
                this.startActivity(rviewIntent);
                break;
            case R.id.loginAction:
                Log.d(TAG, "Login action!!");
                break;
            case R.id.geoLocate:
                Log.d(TAG, "GEOLOCATION REQUEST");
                getGeoLocation();
                break;
            case R.id.goHome:
                Log.d(TAG, "Going HOME");
//                MainActivity.putPrefsData();
//                MainActivity.dismissAllDialogs();
//                MainActivity.hideActivity();
//                MainActivity.welcomeDialog();
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }



    public boolean getGeoLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        final Geocoder geocoder = new Geocoder(SectionActivity.this, Locale.getDefault());
        RuvLocation rLocation = new RuvLocation(SectionActivity.this, new RuvLocation.GeoListener() {
            @Override
            public void sendLocation(Location location) {
                Log.d(TAG, "Getting Location\n" + location.toString());
                Toast.makeText(SectionActivity.this, location.toString(), Toast.LENGTH_SHORT).show();

                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    for (Address address : addresses) {
                        Log.d(TAG, "Address => " + address.toString());
                        String locality = address.getLocality();
                        Log.d(TAG, "Locality = " + locality);
                        String addressLine1 = null;
                        if (address.getAddressLine(1) != null) {
                            addressLine1 = address.getAddressLine(1).contains(",") ? address.getAddressLine(1).substring(0, address.getAddressLine(1).indexOf(",")) : address.getAddressLine(1);
                            Log.d(TAG, "AddressLn1 = " + addressLine1);
                        } else if (address.getAddressLine(0) != null && address.getAdminArea() == null) {
                            String country = "COUNTRY::" + address.getAddressLine(0);
                            SectionActivity.this.city = "NoLocality";
                            SectionActivity.this.region = country;
                        } else {
                            Snackbar.make(findViewById(R.id.MainParentView), "Unable to get GeoLocation", Snackbar.LENGTH_SHORT).show();
                        }
//                        if (SectionActivity.this.city == null || SectionActivity.this.city.trim().equals(""))
                        SectionActivity.this.city = locality == null ? addressLine1 : locality;
//                        if (SectionActivity.this.region == null || SectionActivity.this.region.trim().equals(""))
                        SectionActivity.this.region = address.getAdminArea() == null ? "NoRegion" : RuvLocation.provinceMap.get(address.getAdminArea());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, locationManager);

        if (rLocation.init()) {
            rLocation.getLocation();
            return true;
        }

        return false;
    }
}
