package stronglogic.ruviuz;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.angrybyte.numberpicker.listener.OnValueChangeListener;
import me.angrybyte.numberpicker.view.ActualNumberPicker;
import stronglogic.ruviuz.content.Customer;
import stronglogic.ruviuz.content.Section;
import stronglogic.ruviuz.fragments.SectionFragment;
import stronglogic.ruviuz.views.SectionAdapter;


public class SectionActivity extends AppCompatActivity implements SectionFragment.SectionListener {

    private me.angrybyte.numberpicker.view.ActualNumberPicker sWidthPickFt, sWidthPickIn, sLengthPickFt, sLengthPickIn, sEmptyLengthPickFt, sEmptyLengthPickIn, sEmptyWidthPickFt, sEmptyWidthPickIn;
    private TextView sEmptyLength, sEmptyWidth, sEmptyWidthFtTv, sEmptyWidthInTv, sEmptyLengthFtTv, sEmptyLengthInTv;
    
    private Button sectionBtn;
    private FloatingActionButton doneBtn;
    private Switch fullToggle;
    private RadioGroup emptyTypeGroup;
    private RadioButton chimney, skylight, emptyOther;
    private RelativeLayout widgetWrap;

    private SectionFragment sectionFrag;

    public RecyclerView rv;
    private SectionAdapter secAdapter;

    private String authToken, emptyType;

//    private MainActivity.IncomingHandler mHandler;
//
//    private Map<String, RuvFileInfo> ruvFiles;
//
//    private SharedPreferences prefs;
//
//    private RuvLocation rLocation;
//
//    private Geocoder geocoder;

    private int fileCount, currentRid;
    private float width, length, slope;
    private String material, address, postal, city, region;
    private String[] fileUrls = new String[3];
    private String[] fileComments = new String[3];
    private ArrayList<Section> sectionList = new ArrayList<>();
    private Customer mCustomer;
    private boolean premium, ready, editing;

    private View sectionFragView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_section);
        Window w = getWindow();
        w.setStatusBarColor(ContextCompat.getColor(SectionActivity.this, R.color.ruvGreenStatus));

        widgetWrap = (RelativeLayout) findViewById(R.id.widgetWrap);

        if (getIntent() != null) {
            getIntentData(getIntent());
        }

        sectionBtn = (Button) findViewById(R.id.dummy_button);
        sectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sectionFrag == null || !sectionFrag.isAdded()) {
                    addSectionFragment();
                } else {
                    Section section = new Section();
                    if (sectionFrag.getView() != null) {
                        TextView sectionLengthTv = (TextView) sectionFrag.getView().findViewById(R.id.sectionLength);
                        TextView sectionWidthTv = (TextView) sectionFrag.getView().findViewById(R.id.sectionWidth);
                        TextView sectionLengthInTv = (TextView) sectionFrag.getView().findViewById(R.id.sectionLengthIn);
                        TextView sectionWidthInTv = (TextView) sectionFrag.getView().findViewById(R.id.sectionWidthIn);
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
                        if (!fullToggle.isChecked()) {
                            TextView sEmptyLengthTv = (TextView) sectionFrag.getView().findViewById(R.id.emptyLength);
                            TextView sEmptyWidthTv = (TextView) sectionFrag.getView().findViewById(R.id.emptyWidth);
                            TextView sEmptyLengthInTv = (TextView) sectionFrag.getView().findViewById(R.id.emptyLengthIn);
                            TextView sEmptyWidthInTv = (TextView) sectionFrag.getView().findViewById(R.id.emptyWidthIn);
                            if (sEmptyLengthTv != null && sEmptyWidthTv != null && sEmptyLengthInTv != null && sEmptyWidthInTv != null) {
                                float emLen = (sEmptyLengthTv.getText().toString().equals("") ?
                                        0.0f : Float.valueOf(sEmptyLengthTv.getText().toString()))
                                        +  (sEmptyLengthInTv.getText().toString().equals("") ?
                                        0.0f : (Float.valueOf(sEmptyLengthInTv.getText().toString()) / 12));
                                float emWid = (sEmptyWidthTv.getText().toString().equals("") ?
                                        0.0f : Float.valueOf(sEmptyWidthTv.getText().toString()))
                                        +  (sEmptyWidthInTv.getText().toString().equals("") ?
                                        0.0f : (Float.valueOf(sEmptyWidthInTv.getText().toString()) / 12));

                                float emptyArea = emLen * emWid;

                                if (emptyTypeGroup.getCheckedRadioButtonId() != -1) {
                                    RadioButton selectedButton = (RadioButton) findViewById(emptyTypeGroup.getCheckedRadioButtonId());
                                    if (selectedButton.getText().toString().equals(Section.CHIMNEY)) {
                                        section.setEmptyType(Section.CHIMNEY);
                                    }
                                    if (selectedButton.getText().toString().equals(Section.SKY_LIGHT)) {
                                        section.setEmptyType(Section.SKY_LIGHT);
                                    }
                                    if (selectedButton.getText().toString().equals(Section.OTHER)) {
                                        section.setEmptyType(Section.OTHER);
                                    }
                                }

                                section.toggleFull();
                                section.setMissing(emptyArea);
                                sEmptyLengthTv.setText("");
                                sEmptyWidthTv.setText("");
                                sEmptyLengthInTv.setText("");
                                sEmptyWidthInTv.setText("");
                                sWidthPickFt.setValue(0);
                                sWidthPickIn.setValue(0);
                                sLengthPickFt.setValue(0);
                                sLengthPickIn.setValue(0);
                                sEmptyLengthPickFt.setValue(0);
                                sEmptyLengthPickIn.setValue(0);
                                sEmptyWidthPickFt.setValue(0);
                                sEmptyWidthPickIn.setValue(0);

                            }
                        }

                        if (SectionActivity.this.slope > -1) {
                            section.setSlope(SectionActivity.this.slope);
                        }

                        sectionList.add(section);

                        if (secAdapter != null) {
//                            setupRecycler();
                            secAdapter.notifyItemInserted(sectionList.size() - 1);
                        }

                        sectionLengthTv.setText("");
                        sectionWidthTv.setText("");
                        sectionLengthInTv.setText("");
                        sectionWidthInTv.setText("");
                        fullToggle.setChecked(true);
                    }
                }
            }
        });

        fullToggle = (Switch) findViewById(R.id.fullToggle);
        fullToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    emptyPickersVisible();
                } else {
                    emptyPickersHidden();
                }
            }
        });
        sLengthPickFt = (ActualNumberPicker) findViewById(R.id.lengthPickerFt);
        sLengthPickFt.setListener(new OnValueChangeListener() {
            @Override
            public void onValueChanged(int oldValue, int newValue) {
                if (sectionFrag == null || !sectionFrag.isAdded()) addSectionFragment();
                if (sectionFrag.getView() != null) {
                    TextView sectionLengthTv = (TextView) sectionFrag.getView().findViewById(R.id.sectionLength);
                    String newMeasurement = String.valueOf((float) newValue);
                    sectionLengthTv.setText(newMeasurement);
                }

            }
        });
        sWidthPickFt = (ActualNumberPicker) findViewById(R.id.widthPickerFt);
        sWidthPickFt.setListener(new OnValueChangeListener() {
            @Override
            public void onValueChanged(int oldValue, int newValue) {
                if (sectionFrag == null || !sectionFrag.isAdded()) addSectionFragment();
                if (sectionFrag.getView() != null) {
                    TextView sectionWidthTv = (TextView) sectionFrag.getView().findViewById(R.id.sectionWidth);
                    String newMeasurement = String.valueOf(newValue);
                    sectionWidthTv.setText(newMeasurement);
                }

            }
        });
        sLengthPickIn = (ActualNumberPicker) findViewById(R.id.lengthPickerIn);
        sLengthPickIn.setListener(new OnValueChangeListener() {
            @Override
            public void onValueChanged(int oldValue, int newValue) {
                if (sectionFrag == null || !sectionFrag.isAdded()) addSectionFragment();
                if (sectionFrag.getView() != null) {
                    TextView sectionLengthInTv = (TextView) sectionFrag.getView().findViewById(R.id.sectionLengthIn);
                    if (sectionLengthInTv != null) {
                        float newMeasurement = (float) newValue;
                        String newString = String.valueOf(newMeasurement);
                        sectionLengthInTv.setText(newString);
                    }
                }

            }
        });
        sWidthPickIn = (ActualNumberPicker) findViewById(R.id.widthPickerIn);
        sWidthPickIn.setListener(new OnValueChangeListener() {
            @Override
            public void onValueChanged(int oldValue, int newValue) {
                if (sectionFrag == null || !sectionFrag.isAdded()) addSectionFragment();
                if (sectionFrag.getView() != null) {
                    TextView sectionWidthInTv = (TextView) sectionFrag.getView().findViewById(R.id.sectionWidthIn);
                    if (sectionWidthInTv != null) {
                        float newMeasurement = (float) newValue;
                        String newString = String.valueOf(newMeasurement);
                        sectionWidthInTv.setText(newString);
                    }
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

        emptyTypeGroup = (RadioGroup) findViewById(R.id.emptyType);
        chimney = (RadioButton) findViewById(R.id.chimney);
        if (this.emptyType != null && this.emptyType.equals("Chimney"))
            chimney.setChecked(true);
        skylight = (RadioButton) findViewById(R.id.skylight);
        if (this.emptyType != null && this.emptyType.equals("Skylight"))
            skylight.setChecked(true);
        emptyOther = (RadioButton) findViewById(R.id.emptyOther);
        if (this.emptyType != null && this.emptyType.equals("Other"))
            emptyOther.setChecked(true);

        emptyTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioBtn = (RadioButton) findViewById(checkedId);
                if (sectionFrag.getView() != null) {
                    TextView sEtypeTv = (TextView) sectionFrag.getView().findViewById(R.id.eType);
                    SectionActivity.this.emptyType = radioBtn.getText().toString();
                    sEtypeTv.setText(SectionActivity.this.emptyType);
                }
            }
        });
        
        addSectionFragment();
        setupRecycler();
    }

    private void addSectionFragment() {
        FragmentManager fm = getFragmentManager();
        if (sectionFrag == null) { sectionFrag = new SectionFragment(); }

            if (!sectionFrag.isAdded()) {

                fm.beginTransaction().add(R.id.section_fragment, sectionFrag).commit();

            }
        this.sectionFragView = sectionFrag.getView();
    }


    public void setupRecycler()  {
        ArrayList<Section> feedList = SectionActivity.this.sectionList;
        if (feedList.size() > -1) {
            this.secAdapter = new SectionAdapter(SectionActivity.this, feedList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            rv = (RecyclerView) findViewById(R.id.sectionView);
            rv.setAdapter(secAdapter);
            rv.setLayoutManager(layoutManager);
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
            LinearLayoutManager layoutMgr = new LinearLayoutManager(getBaseContext(),
                    LinearLayoutManager.VERTICAL, false);
            layoutMgr.setAutoMeasureEnabled(true);
            layoutMgr.setRecycleChildrenOnDetach(true);
            rv.setLayoutManager(layoutMgr);
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
        if (SectionActivity.this.emptyTypeGroup == null) SectionActivity.this.emptyTypeGroup = (RadioGroup) findViewById(R.id.emptyType);
        SectionActivity.this.emptyTypeGroup.setVisibility(View.VISIBLE);
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
        if (SectionActivity.this.emptyTypeGroup == null) SectionActivity.this.emptyTypeGroup = (RadioGroup) findViewById(R.id.emptyType);
        SectionActivity.this.emptyTypeGroup.setVisibility(View.GONE);
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
        intent.putExtra("currentRid", this.currentRid);
        intent.putExtra("fileCount", this.fileCount);
        intent.putExtra("fileUrls", this.fileUrls);
        intent.putExtra("baseUrl", MainActivity.baseUrl);
        intent.putExtra("editing", SectionActivity.this.editing);
        this.ready = intent.getBooleanExtra("ready", false);
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

}