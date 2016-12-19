package stronglogic.ruviuz;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import stronglogic.ruviuz.fragments.AddressFragment;
import stronglogic.ruviuz.fragments.LoginFragment;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginFragListener, AddressFragment.AddressFragListener, Handler.Callback {

    private static final int CAMERA_PERMISSION = 6;

    private android.support.v7.widget.Toolbar mToolbar;

    private LoginFragment mLoginFrag;
    private AddressFragment mAddressFrag;

    private OrientationEventListener mListener;

    private NumberPicker roofLength, roofWidth, roofSlope;
    private Switch isFlat, premiumMaterial;
    private TextureView textureView;
    private TextView orientationText;
    private ImageView photo1, photo2, photo3;
    private ImageButton photoBtn;
    private Button ruuvBtn, addressBtn;

    private static final String TAG = "Ruviuz";
//    private static final String baseUrl = "http://10.0.2.2:5000";
    private static final String baseUrl = "http://52.43.250.94:5000";

    private String authToken;

    private IncomingHandler mHandler;

    private SharedPreferences prefs;

    private boolean hasCamera;

    int fileCount, currentRid;
    float width, length, slope;
    BigDecimal price;
    String address;
    String[] fileUrls = new String[3];
    boolean premium;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mGoogleApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.app_bar);

        if (getIntent() != null) {
            getIntentData(getIntent());
        }

        setSupportActionBar(mToolbar);

        if (getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.drawable.rlogo);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setElevation(8f);
        }

        if (checkCameraHardware(this)) {
            photoBtn = (ImageButton) findViewById(R.id.takePhoto);
            photoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
                    } else {
                        if (checkCameraHardware(MainActivity.this)) {
                            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                            intent = putIntentData(intent);
                            startActivity(intent);
                        } else {
                            Log.d(TAG, "No Camera Hardware on Device");
                        }
                    }
                }
            });
        } else {
            Log.d(TAG, "Camera Not Available");
        }

//        if (mGoogleApi == null) {
//            mGoogleApi = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
//        }

        roofLength = (NumberPicker) findViewById(R.id.roofLength);
        roofWidth = (NumberPicker) findViewById(R.id.roofWidth);
        roofSlope = (NumberPicker) findViewById(R.id.roofSlope);

        //set Picker ranges
        roofLength.setMinValue(0);
        roofLength.setMaxValue(500);
        roofLength.setWrapSelectorWheel(true);
        roofLength.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.d(TAG, "Length set to " + String.valueOf(newVal));
            }
        });

        roofWidth.setMinValue(0);
        roofWidth.setMaxValue(500);
        roofWidth.setWrapSelectorWheel(true);
        roofSlope.setMinValue(0);
        roofSlope.setMaxValue(180);
        roofSlope.setWrapSelectorWheel(true);

        isFlat = (Switch) findViewById(R.id.roofFlat);
        premiumMaterial = (Switch) findViewById(R.id.premiumMaterial);

        premiumMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setPremium(isChecked);
            }
        });

        orientationText = (TextView) findViewById(R.id.orientationText);

        mListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_UI) {
            @Override
            public void onOrientationChanged(int orientation) {
                orientationText.setText(String.valueOf(orientation));
            }
        };

        mHandler = new IncomingHandler(this);

        if (authToken == null) {
            loginDialog();
        }

        photo1 = (ImageView) findViewById(R.id.ruvPic1);
        photo2 = (ImageView) findViewById(R.id.ruvPic2);
        photo3 = (ImageView) findViewById(R.id.ruvPic3);

        ruuvBtn = (Button) findViewById(R.id.ruuvSubmitBtn);

        ruuvBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (address == null) {
                    addressDialog();
                } else {
                    if (roofLength.getValue() > -1 &&
                            roofSlope != null &&
                            roofWidth.getValue() > -1 &&
                            address != null) {

                        slope = roofSlope.getValue();
                        length = roofLength.getValue();
                        width = roofWidth.getValue();

                        Bundle mBundle = new Bundle();
                        mBundle.putString("address", address);
                        mBundle.putString("price", calculatePrice().toString());
                        mBundle.putFloat("width", roofWidth.getValue());
                        mBundle.putFloat("length", roofLength.getValue());
                        mBundle.putFloat("slope", roofSlope.getValue());
                        RuuvThread ruuvThread = new RuuvThread((MainActivity) getParent(), mHandler, baseUrl, authToken, mBundle);
                        Thread submitThread = new Thread(ruuvThread);
                        submitThread.start();
                    }
                }
            }
        });

        addressBtn = (Button) findViewById(R.id.addressGetDialog);
        addressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressDialog();
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApi = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    public void onResume() {
        super.onResume();
        mListener.enable();

        if (getIntent() != null) {
            Intent xIntent = getIntent();
            getIntentData(xIntent);
            if (xIntent.hasExtra("uri")) {
                if (fileCount + 1 == 4) {
                    Toast.makeText(MainActivity.this, "You cannot add more photos", Toast.LENGTH_SHORT);
                } else {
                    this.fileCount++;
                    if (fileUrls == null) {
                        this.fileUrls = new String[3];
                    }
                    if (fileUrls[fileCount - 1] == null) {
                        fileUrls[fileCount - 1] = "";
                    }
                    Bundle stuff = xIntent.getExtras();
                    String uriS = stuff.getString("uri");
                    fileUrls[fileCount - 1] = uriS;
                }
            }
        }
        if (fileCount > 0) {
            if (fileUrls[0] != null && photo1.getDrawable() == null) {
                Glide.with(MainActivity.this)
                        .load(fileUrls[0])
                        .override(72, 54)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(photo1);
            }
            if (fileUrls[1] != null && photo2.getDrawable() == null) {
                photo2 = (ImageView) findViewById(R.id.ruvPic2);
                Glide.with(MainActivity.this)
                        .load(fileUrls[1])
                        .override(72, 54)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(photo2);
            }
            if (fileUrls[2] != null && photo3.getDrawable() == null) {
                photo3 = (ImageView) findViewById(R.id.ruvPic3);
                Glide.with(MainActivity.this)
                        .load(fileUrls[2])
                        .override(72, 54)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(photo3);
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        mListener.disable();
    }

    void setPremium(boolean checked) {
        this.premium = checked;
    }


    BigDecimal calculatePrice() {
        try {
            BigDecimal mPrice = new BigDecimal((length * width * (0.428 * slope)));
            if (premium) {
                mPrice = mPrice.multiply(new BigDecimal(2));
            }
            this.price = mPrice;
            return mPrice;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void loginDialog() {

        if (mLoginFrag == null) {
            mLoginFrag = new LoginFragment();
        }

        if (!mLoginFrag.isAdded()) {
            FragmentManager fm = getFragmentManager();

            Bundle args = new Bundle();
            args.putString("baseUrl", baseUrl);
            if (getPrefCreds() != null) {
                args.putString("email", prefs.getString("email", "Email"));
                args.putString("password", prefs.getString("password", "Password"));
            }
            mLoginFrag.setArguments(args);
            mLoginFrag.show(fm, "Please Login");
        }
    }


    public void addressDialog() {
        if (mAddressFrag == null) {
            FragmentManager fm = getFragmentManager();
            mAddressFrag = new AddressFragment();
            mAddressFrag.show(fm, "Please Enter Addresss");
        }
    }


    @Override
    public void loginFragInteraction(String output) {
        Toast.makeText(this, output, Toast.LENGTH_SHORT).show();
        this.authToken = output;


        if (mLoginFrag != null) {
            mLoginFrag.dismiss();
        }
    }


    @Override
    public void addressFragInteraction(String address, String postal) {
        this.address = address + ",\n" + postal;
        Toast.makeText(this, this.address, Toast.LENGTH_SHORT).show();
        addressBtn.setAlpha(1f);

        if (mAddressFrag != null) {
            mAddressFrag.dismiss();
        }
    }

    @Override
    public boolean handleMessage(Message inputMessage) {
        if (inputMessage.getData().getString("RuuvResponse") != null) {
            Log.d(TAG, inputMessage.getData().getString("RuuvResponse"));
            try {
                JSONObject returnedJson = new JSONObject(inputMessage.getData().getString("RuuvResponse"));
                if (returnedJson.has("Roof")) {
                    JSONObject ruuvJson = new JSONObject(returnedJson.getString("Roof"));
                    this.currentRid = Integer.valueOf(ruuvJson.getString("id"));
                    Toast.makeText(MainActivity.this, "Created Roof:: " + returnedJson.getString("Roof"), Toast.LENGTH_SHORT).show();

                }
                if (returnedJson.has("File")) {
                    Toast.makeText(MainActivity.this, "Created File:: " + returnedJson.getString("File"), Toast.LENGTH_SHORT).show();
                }

                if (fileCount > 0) {
                    if (sendRuuvFiles() > 0) {
                        fileCount = 0;
                        fileUrls[0] = null;
                        fileUrls[1] = null;
                        fileUrls[2] = null;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    private int sendRuuvFiles() {
        int sent = 0;
        for (String url : fileUrls) {
            //private RuuvFile(MainActivity mActivity, Handler mHandler, String baseUrl, String authToken, String fileUrl) {
            if (url != null) {
                RuuvFile rFile = new RuuvFile(MainActivity.this, mHandler, this.baseUrl, this.authToken, url, currentRid);
                Thread sendFileThread = new Thread(rFile);
                sendFileThread.start();
                sent++;
            }
        }
        return sent;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApi.connect();
        AppIndex.AppIndexApi.start(mGoogleApi, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApi.disconnect();
        AppIndex.AppIndexApi.end(mGoogleApi, getIndexApiAction());
        mGoogleApi.disconnect();
    }

    public String[] getPrefCreds()  {
        this.prefs = getSharedPreferences("RuviuzApp", MODE_PRIVATE);
        String[] creds = new String[2];
        creds[0] = prefs.getString("email", "0");
        creds[1] = prefs.getString("pass", "0");

        return creds;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ruviuz_menu, menu);
        int color = ContextCompat.getColor(this, R.color.colorAccent);
        updateMenuWithIcon(menu.findItem(R.id.loginAction), color);
        updateMenuWithIcon(menu.findItem(R.id.geoLocate), color);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.roofView:
                Intent rviewIntent = new Intent(this, RviewActivity.class);
                rviewIntent.putExtra("authToken", authToken);
                rviewIntent.putExtra("baseUrl", baseUrl);
                this.startActivity(rviewIntent);
                break;
            case R.id.loginAction:
                Log.d(TAG, "Login action!!");
                loginDialog();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    static class IncomingHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        IncomingHandler(MainActivity mActivity) {
            this.mActivity = new WeakReference<MainActivity>(mActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity mainActivity = mActivity.get();
            if (mainActivity != null) {
                mainActivity.handleMessage(msg);
            }
        }
    }

    /**
     * Updates a menu item in the dropdown to show it's icon that was declared in XML.
     *
     * @param item
     *         the item to update
     * @param color
     *         the color to tint with
     */
    private static void updateMenuWithIcon(@NonNull final MenuItem item, final int color) {
        SpannableStringBuilder builder = new SpannableStringBuilder()
                .append("*") // the * will be replaced with the icon via ImageSpan
//                .append("    ") // This extra space acts as padding. Adjust as you wish
                .append(item.getTitle());

        // Retrieve the icon that was declared in XML and assigned during inflation
        if (item.getIcon() != null && item.getIcon().getConstantState() != null) {
            Drawable drawable = item.getIcon().getConstantState().newDrawable();

            // Mutate this drawable so the tint only applies here
            drawable.mutate().setTint(color);

            // Needs bounds, or else it won't show up (doesn't know how big to be)
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            ImageSpan imageSpan = new ImageSpan(drawable);
            builder.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            item.setTitle(builder);
        }
    }


    public Intent putIntentData(Intent intent) {
        intent.putExtra("authToken", this.authToken);
        intent.putExtra("slope", this.slope);
        intent.putExtra("width", this.width);
        intent.putExtra("length", this.length);
        intent.putExtra("address", this.address);
        intent.putExtra("premium", this.premium);
        intent.putExtra("currentRid", this.currentRid);
        intent.putExtra("fileCount", this.fileCount);
        intent.putExtra("fileUrls", this.fileUrls);

        return intent;
    }
    
    public void getIntentData(Intent intent) {
        this.authToken = intent.getStringExtra("authToken");
        this.slope = intent.getFloatExtra("slope", 0);
        this.width = intent.getFloatExtra("width", 0);
        this.length = intent.getFloatExtra("length", 0);
        this.address = intent.getStringExtra("address");
        this.premium = intent.getBooleanExtra("premium", false);
        this.currentRid = intent.getIntExtra("currentRid", -1);
        this.fileCount = intent.getIntExtra("fileCount", 0);
        this.fileUrls = intent.getStringArrayExtra("fileUrls");
    }

    private static class RuuvThread implements Runnable {
        private Handler mHandler;
        private WeakReference<MainActivity> mReference;
        private Activity mActivity;

        private String baseUrl, authToken;

        private Bundle mBundle;

        private RuuvThread(MainActivity mActivity, Handler mHandler, String baseUrl, String authToken, Bundle mBundle) {
            this.mReference = new WeakReference<MainActivity>(mActivity);
            this.mHandler = mHandler;
            this.baseUrl = baseUrl;
            this.authToken = authToken;
            this.mBundle = mBundle;
            this.mActivity = mActivity;
        }

        @Override
        public void run() {
            sendRuuv();
        }

        private boolean sendRuuv() {
            String endpoint = baseUrl + "/roof/add";
            JSONObject ruuvJson = new JSONObject();

            if (mBundle != null) {

                try {
                    ruuvJson.put("address", mBundle.getString("address"));
                    ruuvJson.put("width", mBundle.getFloat("width"));
                    ruuvJson.put("length", mBundle.getFloat("length"));
                    ruuvJson.put("slope", mBundle.getFloat("slope"));
                    ruuvJson.put("price", mBundle.getString("price"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                String response = null;

                try {
                    URL url = new URL(endpoint);
                    String mAuth = authToken + ":jigga";
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setRequestProperty("Authorization", "Basic " + Base64.encodeToString(mAuth.trim().getBytes(), Base64.NO_WRAP));
                    connection.connect();

                    Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                    writer.write(ruuvJson.toString());
                    writer.close();
                    Log.d(TAG, String.valueOf(connection.getResponseCode()));
                    InputStream stream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder bildr = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        bildr.append(line);
                    }

                    response = bildr.toString();

                    if (!response.trim().isEmpty()) {
                        Bundle msgData = new Bundle();
                        msgData.putString("RuuvResponse", String.valueOf(response));
                        Message outgoingMsg = new Message();
                        outgoingMsg.setData(msgData);
                        mHandler.sendMessage(outgoingMsg);
                    }
                    return true;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(this, CameraActivity.class);
                        startActivity(intent);
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the CAMERA", Toast.LENGTH_SHORT).show();
                }
                return;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private static class RuuvFile implements Runnable {
        private Handler mHandler;
        private WeakReference<MainActivity> mReference;
        private Activity mActivity;

        private String baseUrl, authToken;

        private String fileUrl;
        private int ruvId;

        private RuuvFile(MainActivity mActivity, Handler mHandler, String baseUrl, String authToken, String fileUrl, int ruvId) {
            this.mReference = new WeakReference<MainActivity>(mActivity);
            this.mHandler = mHandler;
            this.baseUrl = baseUrl;
            this.authToken = authToken;
            this.fileUrl = fileUrl;
            this.mActivity = mActivity;
            this.ruvId = ruvId;
        }

        @Override
        public void run() {
            sendFile();
        }

        private boolean sendFile() {
            String endpoint = baseUrl + "/file/upload";
            try {
                File file = new File(fileUrl);
                FileInputStream fileInputStream = null;
                DataOutputStream outputStream = null;
                Writer writer = null;
                BufferedReader reader = null;

                HttpURLConnection connection = null;
                String response = null;
                String twoHyphens = "--";
                String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
                String lineEnd = "\r\n";
                try {
                    URL url = new URL(endpoint);
                    String mAuth = authToken + ":jigga";
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("User-Agent", "Ruviuz Android");
                    connection.setRequestProperty("Authorization", "Basic " + Base64.encodeToString(mAuth.trim().getBytes(), Base64.NO_WRAP));
                    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                    outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "upload" + "\"; filename=\"" + file.getName() + "\"" + lineEnd);
                    outputStream.writeBytes("Content-Type: " + HttpURLConnection.guessContentTypeFromName(file.getName()) + lineEnd);
                    outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
                    outputStream.writeBytes(lineEnd);

                    fileInputStream = new FileInputStream(file);
                    byte[] buffer = new byte[4096];
                    int bytesRead = -1;

                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    outputStream.writeBytes(lineEnd);

                    if (ruvId > -1) {
                        outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                        outputStream.writeBytes("Content-Disposition: form-data; name=\"rid\"" + lineEnd);
                        outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                        outputStream.writeBytes(lineEnd);
                        outputStream.writeBytes(String.valueOf(ruvId));
                        outputStream.writeBytes(lineEnd);
                        outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    }

                    //TODO consolidate differences between BufferedWriter and DataOutputStream (do we use both, or can we use one over the other to handle the overall transaction
                    outputStream.flush();
//                    writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));

                    if (200 != connection.getResponseCode()) {
                        Log.d(TAG, connection.getResponseMessage());
                    }

                    InputStream stream = connection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder bildr = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        bildr.append(line);
                    }

                    response = bildr.toString();

                    fileInputStream.close();
                    outputStream.flush();
                    outputStream.close();

                    if (!response.trim().isEmpty()) {
                        Bundle msgData = new Bundle();
                        msgData.putString("RuuvResponse", String.valueOf(response));
                        Message outgoingMsg = new Message();
                        outgoingMsg.setData(msgData);
                        mHandler.sendMessage(outgoingMsg);
                    }
                    return true;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}