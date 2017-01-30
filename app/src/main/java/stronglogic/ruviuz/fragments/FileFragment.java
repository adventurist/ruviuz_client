package stronglogic.ruviuz.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import stronglogic.ruviuz.CameraActivity;
import stronglogic.ruviuz.MainActivity;
import stronglogic.ruviuz.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link DialogFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FileFragment.FileFragListener} interface
 * to handle interaction events.
 * Use the {@link FileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FileFragment extends DialogFragment {

    private final static String TAG = "RUVIUZImageEditFragment";
    
    private static final int CAMERA_PERMISSION = 6;
    private final static int RESULT_LOAD_IMAGE = 17;
    
    private FileFragListener mListener;

    private Toolbar mToolbar;

    private MainActivity mActivity;

    private ImageView ruvpic1, ruvpic2, ruvpic3;

    private ImageButton cameraBtn, uploadBtn, okayBtn;

    private int editIndex, fileCount;
    private String[] fileUrls;

    public FileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ImageEditFragment.
     */
    public static FileFragment newInstance(String param1, String param2) {
        FileFragment fragment = new FileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!((MainActivity) getActivity()).readyStatus()) {
            ((MainActivity) getActivity()).hideActivity();
            ((MainActivity) getActivity()).dismissOtherDialogs(FileFragment.this.getClass());
        }
        setStyle(DialogFragment.STYLE_NORMAL, R.style.RuvFullFrag);
        if (getArguments() != null) {
            getBundleData(getArguments());
        }
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
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                mActivity.slopeDialog();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View mView = inflater.inflate(R.layout.filefragment, container, false);

        mToolbar = (Toolbar) mView.findViewById(R.id.ruvFragToolbar);

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle the menu item
                return true;
            }
        });
        mToolbar.inflateMenu(R.menu.ruviuz_menu);

        if (mToolbar != null) {
            mToolbar.setNavigationIcon(R.drawable.construction);
            mToolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.ruvGreen));
            mToolbar.setElevation(8f);
            mToolbar.setTitle(getActivity().getResources().getString(R.string.app_name));
            mToolbar.setTitleTextColor(Color.BLACK);
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Log.d(TAG, item.toString());
//                    switch (item.getItemId()) {
//
//                        case R.id.actionfragTitle:
//                            Log.d(TAG, "ACTIONFRAGTITLE");
//                            break;
//                        case R.id.roofView:
//                            Intent rviewIntent = new Intent(mActivity, RviewActivity.class);
//                            mActivity.putIntentData(rviewIntent);
//                            mActivity.putPrefsData();
//                            Log.d(TAG, "Implement this through MainActivity");
////                            rviewIntent.putExtra("baseUrl", mActivity.);
//                            mActivity.startActivity(rviewIntent);
//                            ImageEditFragment.this.dismiss();
//                            break;
//                        case R.id.loginAction:
//                            Log.d(TAG, "Login action!!");
//                            mActivity.putPrefsData();
//                            mActivity.loginDialog();
//                            ImageEditFragment.this.dismiss();
//                            break;
//                        case R.id.geoLocate:
//                            if (mActivity != null) {
//                                mActivity.getGeoLocation();
////                                String[] newAddress = mActivity.getAddress();
////                                cityEt.setText(newAddress[0]);
////                                provinceEt.setText(newAddress[1]);
//                            }
//                            break;
//                        case R.id.goHome:
//                            Log.d(TAG, "Going HOME");
//                            mActivity.putPrefsData();
//                            mActivity.welcomeDialog();
//                            ImageEditFragment.this.dismiss();
//                            break;
//                    }
                    return true;

                }
            });
        }

        ruvpic1 = (ImageView) mView.findViewById(R.id.ruvpic1);
        ruvpic2 = (ImageView) mView.findViewById(R.id.ruvpic2);
        ruvpic3 = (ImageView) mView.findViewById(R.id.ruvpic3);

        if (fileUrls[0] != null && !fileUrls[0].equals("")) {
            Glide.with(mActivity)
                    .load(fileUrls[0])
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(ruvpic1);

            Button setMainBtn = new Button(mActivity);
            setMainBtn.setText("MY TEXT");
            setMainBtn.setTextSize(20);
            RelativeLayout.LayoutParams mainBtnParams = new RelativeLayout.LayoutParams(32, 32);
            mainBtnParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            addButtonToView(setMainBtn, mainBtnParams, ruvpic1.getId(), mView);

            Button commentBtn = new Button(mActivity);
            RelativeLayout.LayoutParams commentBtnParams = new RelativeLayout.LayoutParams(32, 32);
            commentBtnParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            addButtonToView(commentBtn, commentBtnParams, ruvpic1.getId(), mView);

            Button deleteBtn = new Button(mActivity);
            RelativeLayout.LayoutParams delBtnParams = new RelativeLayout.LayoutParams(32, 32);
            delBtnParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            addButtonToView(deleteBtn, delBtnParams, ruvpic1.getId(), mView);
        }

        if (fileUrls[1] != null && !fileUrls[1].equals("")) {
            Glide.with(mActivity)
                    .load(fileUrls[1])
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(ruvpic2);

            Button setMainBtn = new Button(mActivity);
            setMainBtn.setText("MY TEXT");
            setMainBtn.setTextSize(20);
            RelativeLayout.LayoutParams mainBtnParams = new RelativeLayout.LayoutParams(32, 32);
            mainBtnParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            addButtonToView(setMainBtn, mainBtnParams, ruvpic2.getId(), mView);

            Button commentBtn = new Button(mActivity);
            RelativeLayout.LayoutParams commentBtnParams = new RelativeLayout.LayoutParams(32, 32);
            commentBtnParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            addButtonToView(commentBtn, commentBtnParams, ruvpic2.getId(), mView);

            Button deleteBtn = new Button(mActivity);
            RelativeLayout.LayoutParams delBtnParams = new RelativeLayout.LayoutParams(32, 32);
            delBtnParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            addButtonToView(deleteBtn, delBtnParams, ruvpic2.getId(), mView);
        }

        if (fileUrls[2] != null && !fileUrls[2].equals("")) {
            Glide.with(mActivity)
                    .load(fileUrls[2])
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(ruvpic3);

            Button setMainBtn = new Button(mActivity);
            setMainBtn.setText("MY TEXT");
            setMainBtn.setTextSize(20);
            RelativeLayout.LayoutParams mainBtnParams = new RelativeLayout.LayoutParams(32, 32);
            mainBtnParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            addButtonToView(setMainBtn, mainBtnParams, ruvpic3.getId(), mView);

            Button commentBtn = new Button(mActivity);
            RelativeLayout.LayoutParams commentBtnParams = new RelativeLayout.LayoutParams(32, 32);
            commentBtnParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            addButtonToView(commentBtn, commentBtnParams, ruvpic3.getId(), mView);

            Button deleteBtn = new Button(mActivity);
            RelativeLayout.LayoutParams delBtnParams = new RelativeLayout.LayoutParams(32, 32);
            delBtnParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            addButtonToView(deleteBtn, delBtnParams, ruvpic3.getId(), mView);
        }

        cameraBtn = (ImageButton) mView.findViewById(R.id.takePicBtn);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "use Camera!!");
                if (checkCameraHardware(mActivity)) {
                    Intent intent = new Intent(mActivity, CameraActivity.class);
                    mActivity.putIntentData(intent);
                    intent.putExtra("callingClass", mActivity.getClass().getSimpleName());
                    mActivity.putPrefsData();
                    startActivity(intent);
                } else {
                    Log.d(TAG, "No Camera Hardware on Device");
                }
            }
        });
        
        uploadBtn = (ImageButton) mView.findViewById(R.id.fileChooseBtn);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Choose File!");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), RESULT_LOAD_IMAGE);
            }
        });
//        deleteBtn = (Button) mView.findViewById(R.id.deleteBtn);
//        deleteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MenuBuilder menuBuilder = new MenuBuilder(mActivity);
//                MenuInflater inflater = new MenuInflater(mActivity);
//                inflater.inflate(R.menu.del_menu, menuBuilder);
//                final MenuPopupHelper deleteMenu = new MenuPopupHelper(mActivity, menuBuilder, deleteBtn);
//                deleteMenu.setForceShowIcon(true);
//
//                menuBuilder.setCallback(new MenuBuilder.Callback() {
//                    @Override
//                    public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
//                        switch (item.getItemId()) {
////                            case R.id.yesDelete:
////                                Log.d(TAG, "Deleting Image");
////                                if (FileFragment.this.fileUrls != null && FileFragment.this.fileUrls[editIndex] != null) {
////                                    FileFragment.this.fileUrls[editIndex] = "";
////                                    FileFragment.this.fileCount--;
////                                    Bundle mBundle = new Bundle();
////                                    putBundleData(mBundle);
////                                    FileFragment.this.mListener.fileFragInteraction(mBundle, MainActivity.RUV_IMGEDIT_DELETE);
////                                    FileFragment.this.dismiss();
////                                }
////                                return true;
////                            case R.id.noDelete:
////                                Log.d(TAG, "Cancelling delete");
////                                if (deleteMenu.isShowing()) deleteMenu.dismiss();
////                                return true;
//                            default:
//                                return false;
//                        }
//                    }
//
//                    @Override
//                    public void onMenuModeChange(MenuBuilder menu) {
//                    }
//                });
//                deleteMenu.show();
//            }
//        });

        okayBtn = (ImageButton) mView.findViewById(R.id.okayBtn);
        okayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.revealActivity();
                dismiss();
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
        if (context instanceof FileFragListener) {
            mListener = (FileFragListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FileFragInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    protected void addButtonToView(Button button, RelativeLayout.LayoutParams params, int viewId, View parent) {
        RelativeLayout imageLayout = (RelativeLayout) parent.findViewById(R.id.fileWrap);
        params.addRule(RelativeLayout.BELOW, viewId);
        button.setLayoutParams(params);
        imageLayout.addView(button);
        String TestCruft = "CRUFT";
    }

    public interface FileFragListener {
        void fileFragInteraction(String[] fileUrls, int fileCount, int result);
    }

    public void getBundleData(Bundle bundle) {
        Log.d(TAG, "getBundleData");
//        if (this.length <= 0 )
//            this.length = bundle.getFloat("length");
//        if (this.width <= 0)
//            this.width = bundle.getFloat("width");
//        if (this.slope <= 0)
//            this.slope = bundle.getFloat("slope");
//        if (this.material == null || this.material.equals(""))
//            this.material = bundle.getString("material");
//        if (this.address == null || this.address.equals(""))
//            this.address = bundle.getString("address");
//        if (this.city == null || this.city.equals(""))
//            this.city = bundle.getString("city");
//        if (this.region == null || this.region.equals(""))
//            this.region = bundle.getString("region");
//        if (this.postal == null || this.postal.equals(""))
//            this.postal = bundle.getString("postal");
//        if (this.firstName== null || this.firstName.equals(""))
//            this.firstName = bundle.getString("firstName");
//        if (this.lastName == null || this.lastName.equals(""))
//            this.lastName = bundle.getString("lastName");
//        if (this.email == null || this.email.equals(""))
//            this.email = bundle.getString("email");
//        if (this.phone == null || this.phone.equals(""))
//            this.phone = bundle.getString("phone");
//        if (this.prefix == null || this.prefix.equals(""))
//            this.prefix = bundle.getString("prefix");
//        if (bundle.getString("price") != null && this.price == null)
//            this.price = new BigDecimal(bundle.getString("price"));
        if (this.fileCount <= 0)
            this.fileCount = bundle.getInt("fileCount", 0);
//        if (bundle.getStringArrayList("fileUrls") != null && (this.ruvFiles != null && !ruvFileUrls.get(0).equals(""))) {
//            this.ruvFileUrls = (ArrayList<String>)bundle.getStringArrayList("fileUrls");
//        }
        if (bundle.getStringArray("fileUrls") != null) {
            if (this.fileUrls == null) this.fileUrls = new String[3];
            this.fileUrls = bundle.getStringArray("fileUrls");
//            if (this.ruvFileUrls == null) this.ruvFileUrls = new ArrayList<String>();
//            for (int i = 0; i < fileUrls.length; i++) {
//                ruvFileUrls.add(fileUrls[i]);
//            }
        }
    }

    public void putBundleData(Bundle bundle) {
        Log.d(TAG, "putBundleData");
        bundle.putInt("fileCount", fileCount);
        if (this.fileUrls != null) {
            bundle.putStringArray("fileUrls", this.fileUrls);
        }
        bundle.putInt("editMode", MainActivity.RUV_IMGEDIT_DELETE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_LOAD_IMAGE:
                if (resultCode == RESULT_OK && data != null) {
                    processImage(data, fileCount);
                }
                break;
        }
    }


    public void processImage(Intent data, int fileCount) {
        Log.d(TAG, "processImage");
        if (fileCount < 3) {
            Uri rawUri = data.getData();
            String imageUri = null;
            String wholeID = DocumentsContract.getDocumentId(rawUri);

            String id = wholeID.split(":")[1];

            String[] column = {MediaStore.Images.Media.DATA};

            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = mActivity.getContentResolver().
                    query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{id}, null);

            String filePath = "";
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndex(column[0]);

                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                imageUri = filePath;
                cursor.close();
            }
            if (imageUri != null) {
                boolean added = false;

                if (FileFragment.this.fileUrls != null) {

                    int i = 0;
                    while (!added && i < 3) {
                        if (FileFragment.this.fileUrls[i] == null || FileFragment.this.fileUrls[i].equals("")) {
                            FileFragment.this.fileUrls[i] = imageUri;
                            added = true;
                        } else {
                            i++;
                        }
                    }
                }
                FileFragment.this.fileCount++;
                Bundle persistData = new Bundle();
                putBundleData(persistData);
                mActivity.saveEditFragState(persistData);
            } else {
                Toast.makeText(mActivity, "Unable to get content URI", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mActivity, "You have already chosen 3 images", Toast.LENGTH_SHORT).show();
        }
    }
    
    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(mActivity, CameraActivity.class);
                    mActivity.putPrefsData();
                    intent.putExtra("callingClass", FileFragment.this.getClass().getSimpleName());
                    mActivity.putIntentData(intent);
                    startActivity(intent);
                } else {
                    Toast.makeText(mActivity, "Please grant camera permission to use the CAMERA", Toast.LENGTH_SHORT).show();
                }
                return;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
