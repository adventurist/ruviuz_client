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
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private final static int COMMENT_1 = 101;
    private final static int COMMENT_2 = 102;
    private final static int COMMENT_3 = 103;

    private FileFragListener mListener;

    private Toolbar mToolbar;

    private MainActivity mActivity;

    private EditText cEt1, cEt2, cEt3;
    private ImageView ruvpic1, ruvpic2, ruvpic3;
    private ImageButton cameraBtn, uploadBtn, okayBtn;

    private int fileCount;
    private String[] fileUrls;
    private String[] fileComments = new String[3];

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

//        LinearLayout imageLayout = (LinearLayout) mView.findViewById(R.id.fileWrap);
        if (fileUrls != null) {
            if (fileUrls[0] != null && !fileUrls[0].equals("")) {
                ruvpic1.setVisibility(View.VISIBLE);
                Glide.with(mActivity)
                        .load(fileUrls[0])
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(ruvpic1);
                Button commentBtn1 = (Button) mView.findViewById(R.id.cBtn1);
                commentBtn1.setVisibility(View.VISIBLE);

                commentBtn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cEt1 = (EditText) mView.findViewById(R.id.commentEt1);
                        cEt1.setText(fileComments[0]);
                        cEt1.setVisibility(View.VISIBLE);
                    }
                });

                Button mainBtn1 = (Button) mView.findViewById(R.id.mainBtn1);
                mainBtn1.setVisibility(View.VISIBLE);

                final Button delBtn1 = (Button) mView.findViewById(R.id.delBtn1);
                delBtn1.setVisibility(View.VISIBLE);
                delBtn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MenuBuilder menuBuilder = new MenuBuilder(mActivity);
                        MenuInflater inflater = new MenuInflater(mActivity);
                        inflater.inflate(R.menu.del_menu, menuBuilder);
                        final MenuPopupHelper deleteMenu = new MenuPopupHelper(mActivity, menuBuilder, delBtn1);
                        deleteMenu.setForceShowIcon(true);

                        menuBuilder.setCallback(new MenuBuilder.Callback() {
                            @Override
                            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.yesDelete:
                                        Log.d(TAG, "Deleting Image");
                                        if (FileFragment.this.fileUrls != null && FileFragment.this.fileUrls[0] != null) {
                                            FileFragment.this.fileUrls[0] = "";
                                            FileFragment.this.fileCount--;
                                            Bundle mBundle = new Bundle();
                                            putBundleData(mBundle);
                                            FileFragment.this.mListener.fileFragInteraction(fileUrls, fileComments, fileCount, 69);
                                            FileFragment.this.dismiss();
                                        }
                                        return true;
                                    case R.id.noDelete:
                                        Log.d(TAG, "Cancelling delete");
                                        if (deleteMenu.isShowing()) deleteMenu.dismiss();
                                        return true;
                                    default:
                                        return false;
                                }
                            }

                            @Override
                            public void onMenuModeChange(MenuBuilder menu) {
                            }
                        });
                        deleteMenu.show();
                    }
                });

            }

            if (fileUrls[1] != null && !fileUrls[1].equals("")) {
                ruvpic2.setVisibility(View.VISIBLE);
                Glide.with(mActivity)
                        .load(fileUrls[1])
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(ruvpic2);

                Button commentBtn2 = (Button) mView.findViewById(R.id.cBtn2);
                commentBtn2.setVisibility(View.VISIBLE);
                cEt2 = (EditText) mView.findViewById(R.id.commentEt2);
                cEt2.setText(fileComments[1]);


                commentBtn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cEt2.setVisibility(View.VISIBLE);
                    }
                });

                Button mainBtn2 = (Button) mView.findViewById(R.id.mainBtn2);
                mainBtn2.setVisibility(View.VISIBLE);

                final Button delBtn2 = (Button) mView.findViewById(R.id.delBtn2);
                delBtn2.setVisibility(View.VISIBLE);

                delBtn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MenuBuilder menuBuilder = new MenuBuilder(mActivity);
                        MenuInflater inflater = new MenuInflater(mActivity);
                        inflater.inflate(R.menu.del_menu, menuBuilder);
                        final MenuPopupHelper deleteMenu = new MenuPopupHelper(mActivity, menuBuilder, delBtn2);
                        deleteMenu.setForceShowIcon(true);

                        menuBuilder.setCallback(new MenuBuilder.Callback() {
                            @Override
                            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.yesDelete:
                                        Log.d(TAG, "Deleting Image");
                                        if (FileFragment.this.fileUrls != null && FileFragment.this.fileUrls[1] != null) {
                                            FileFragment.this.fileUrls[1] = "";
                                            FileFragment.this.fileCount--;
                                            Bundle mBundle = new Bundle();
                                            putBundleData(mBundle);
                                            FileFragment.this.mListener.fileFragInteraction(fileUrls, fileComments, fileCount, 69);
                                            FileFragment.this.dismiss();
                                        }
                                        return true;
                                    case R.id.noDelete:
                                        Log.d(TAG, "Cancelling delete");
                                        if (deleteMenu.isShowing()) deleteMenu.dismiss();
                                        return true;
                                    default:
                                        return false;
                                }
                            }

                            @Override
                            public void onMenuModeChange(MenuBuilder menu) {
                            }
                        });
                        deleteMenu.show();
                    }
                });
            }

            if (fileUrls[2] != null && !fileUrls[2].equals("")) {
                ruvpic3.setVisibility(View.VISIBLE);
                Glide.with(mActivity)
                        .load(fileUrls[2])
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(ruvpic3);

                Button commentBtn3 = (Button) mView.findViewById(R.id.cBtn3);
                commentBtn3.setVisibility(View.VISIBLE);

                commentBtn3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cEt3 = (EditText) mView.findViewById(R.id.commentEt3);
                        Log.d(TAG, String.valueOf(cEt3.getViewTreeObserver()));
                        cEt3.setVisibility(View.VISIBLE);
                        cEt3.setText(fileComments[2]);
                    }
                });

                Button mainBtn3 = (Button) mView.findViewById(R.id.mainBtn3);
                mainBtn3.setVisibility(View.VISIBLE);

                final Button delBtn3 = (Button) mView.findViewById(R.id.delBtn3);
                delBtn3.setVisibility(View.VISIBLE);

                delBtn3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MenuBuilder menuBuilder = new MenuBuilder(mActivity);
                        MenuInflater inflater = new MenuInflater(mActivity);
                        inflater.inflate(R.menu.del_menu, menuBuilder);
                        final MenuPopupHelper deleteMenu = new MenuPopupHelper(mActivity, menuBuilder, delBtn3);
                        deleteMenu.setForceShowIcon(true);

                        menuBuilder.setCallback(new MenuBuilder.Callback() {
                            @Override
                            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.yesDelete:
                                        Log.d(TAG, "Deleting Image");
                                        if (FileFragment.this.fileUrls != null && FileFragment.this.fileUrls[2] != null) {
                                            FileFragment.this.fileUrls[2] = "";
                                            FileFragment.this.fileCount--;
                                            Bundle mBundle = new Bundle();
                                            putBundleData(mBundle);
                                            FileFragment.this.mListener.fileFragInteraction(fileUrls, fileComments, fileCount, 69);
                                            FileFragment.this.dismiss();
                                        }
                                        return true;
                                    case R.id.noDelete:
                                        Log.d(TAG, "Cancelling delete");
                                        if (deleteMenu.isShowing()) deleteMenu.dismiss();
                                        return true;
                                    default:
                                        return false;
                                }
                            }

                            @Override
                            public void onMenuModeChange(MenuBuilder menu) {
                            }
                        });
                        deleteMenu.show();
                    }
                });

            }

            cameraBtn = (ImageButton) mView.findViewById(R.id.takePicBtn);
            cameraBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "use Camera!!");
                    if (checkCameraHardware(mActivity)) {
                        Intent intent = new Intent(mActivity, CameraActivity.class);
                        mActivity.putIntentData(intent);
                        intent.putExtra("callingClass", FileFragment.this.getClass().getSimpleName());
                        mActivity.putPrefsData();
                        startActivity(intent);
                    } else {
                        Log.d(TAG, "No Camera Hardware on Device");
                    }
                }
            });

        }
        
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
        okayBtn = (ImageButton) mView.findViewById(R.id.okayBtn);
        okayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cEt1 != null)
                    fileComments[0] = cEt1.getText().toString();
                if (cEt2 != null)
                    fileComments[1] = cEt2.getText().toString();
                if (cEt3 != null)
                    fileComments[2] = cEt3.getText().toString();

                mListener.fileFragInteraction(FileFragment.this.fileUrls, FileFragment.this.fileComments, FileFragment.this.fileCount, MainActivity.RUV_ADD_FILES);
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


    protected void addMainBtn(GridLayout gridLayout) {
        Button setMainBtn = new Button(mActivity);
        setMainBtn.setText(mActivity.getResources().getString(R.string.set_main));
        setMainBtn.setTextSize(12);
        setMainBtn.setBackgroundResource(R.drawable.ruvbtn_wt);
        GridLayout.LayoutParams mainBtnParams = new GridLayout.LayoutParams();
        mainBtnParams.setGravity(Gravity.START);
        mainBtnParams.setMarginStart(48);
        setMainBtn.setLayoutParams(mainBtnParams);
        gridLayout.addView(setMainBtn);
    }

//    protected Button addCommentBtn(final GridLayout gridLayout, final int index) {
//        final Button commentBtn = new Button(mActivity);
//        commentBtn.setText(R.string.comment);
//        commentBtn.setTextSize(12);
//        commentBtn.setBackgroundResource(R.drawable.ruvbtn_wt);
//        GridLayout.LayoutParams cBtnParams = new GridLayout.LayoutParams(GridLayout.spec(GridLayout.UNDEFINED, 1f),      GridLayout.spec(GridLayout.UNDEFINED, 1f));
//        cBtnParams.setGravity(Gravity.CENTER_HORIZONTAL);
//        commentBtn.setLayoutParams(cBtnParams);
//        gridLayout.addView(commentBtn);
//
//        commentBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                EditText commentEt = new EditText(mActivity);
//                commentEt.setTextSize(12);
//                commentEt.setPadding(12, 12, 12, 12);
//                commentEt.setHint(R.string.enter_comment);
//                commentEt.setSingleLine(false);
//                commentEt.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
//                commentEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
//                commentEt.setMinLines(5);
//                commentEt.setMaxLines(35);
//                commentEt.setVerticalScrollBarEnabled(true);
//                commentEt.setMovementMethod(ScrollingMovementMethod.getInstance());
//                commentEt.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
//                commentEt.setBackgroundResource(R.drawable.thin_outline);
//                GridLayout.LayoutParams cEtParams = new GridLayout.LayoutParams(GridLayout.spec(GridLayout.UNDEFINED, 1f),      GridLayout.spec(1, 3f));
//                cEtParams.setGravity(Gravity.CENTER_HORIZONTAL);
//                cEtParams.setMargins(0, 8, 0, 0);
//                commentEt.setLayoutParams(cEtParams);
//                commentEt.setMinHeight(96);
//                commentEt.setMinWidth(32);
//                commentEt.setMaxWidth(320);
//                commentEt.setLayoutParams(cEtParams);
//                gridLayout.addView(commentEt);
//                commentEt.setId(View.generateViewId());
//                if (fileComments[index] != null) {
//                    commentEt.setText(fileComments[index]);
//                }
//                Log.d(TAG, String.valueOf(commentEt.getId()));
//                commentEts.add(commentEt);
//
//            }
//        });
//        return commentBtn;
//    }
//
//    protected void addCommentEditText(GridLayout gridLayout, Button commentBtn, int index) {
//        EditText commentEt = new EditText(mActivity);
//        commentEt.setTextSize(12);
//        commentEt.setHint(R.string.enter_comment);
//        commentEt.setSingleLine(false);
//        commentEt.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
//        commentEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
//        GridLayout.LayoutParams cEtParams = new GridLayout.LayoutParams(GridLayout.spec(GridLayout.UNDEFINED, 1f),      GridLayout.spec(1, 2f));
//        cEtParams.setGravity(Gravity.CENTER_HORIZONTAL);
//        commentEt.setLayoutParams(cEtParams);
//        gridLayout.addView(commentEt);
//        commentEt.setId(View.generateViewId());
//        if (fileComments[index] != null) {
//            commentEt.setText(fileComments[index]);
//        }
//        Log.d(TAG, String.valueOf(commentEt.getId()));
//        commentEts.add(commentEt);
//        commentBtn.setOnClickListener(null);
//    }
//
//    protected void addDeleteBtn(GridLayout gridLayout, final int index) {
//        final Button deleteBtn = new Button(mActivity);
//        deleteBtn.setBackgroundResource(R.drawable.ruvbtn_wt);
//        deleteBtn.setText(mActivity.getResources().getString(R.string.delete));
//        deleteBtn.setTextSize(12);
//        GridLayout.LayoutParams delBtnParams = new GridLayout.LayoutParams();
//        delBtnParams.setGravity(Gravity.END);
//        delBtnParams.setMarginEnd(48);
//        deleteBtn.setLayoutParams(delBtnParams);
//        gridLayout.addView(deleteBtn);
//
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
//                            case R.id.yesDelete:
//                                Log.d(TAG, "Deleting Image");
//                                if (FileFragment.this.fileUrls != null && FileFragment.this.fileUrls[index] != null) {
//                                    FileFragment.this.fileUrls[index] = "";
//                                    fileComments[index] = "";
//                                    FileFragment.this.fileCount--;
//                                    Bundle mBundle = new Bundle();
//                                    putBundleData(mBundle);
////                                    FileFragment.this.mListener.fileFragInteraction(mBundle, MainActivity.RUV_IMGEDIT_DELETE);
//                                    FileFragment.this.dismiss();
//                                }
//                                return true;
//                            case R.id.noDelete:
//                                Log.d(TAG, "Cancelling delete");
//                                if (deleteMenu.isShowing()) deleteMenu.dismiss();
//                                return true;
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
//
//    }
//

    public interface FileFragListener {
        void fileFragInteraction(String[] fileUrls, String[] fileComments, int fileCount, int result);
    }

    public void getBundleData(Bundle bundle) {
        Log.d(TAG, "getBundleData");
        if (this.fileCount <= 0)
            this.fileCount = bundle.getInt("fileCount", 0);
        if (bundle.getStringArray("fileUrls") != null) {
            if (this.fileUrls == null) this.fileUrls = new String[3];
            this.fileUrls = bundle.getStringArray("fileUrls");
        }
        if (bundle.getStringArray("fileComments") != null) {
            this.fileComments = bundle.getStringArray("fileComments");
        }
    }

    public void putBundleData(Bundle bundle) {
        Log.d(TAG, "putBundleData");
        bundle.putInt("fileCount", fileCount);
        if (this.fileUrls != null) {
            bundle.putStringArray("fileUrls", this.fileUrls);
        }
        if (this.fileComments!= null) {
            bundle.putStringArray("fileComments", this.fileComments);
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

            if (rawUri != null) {

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
