package stronglogic.ruviuz.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import stronglogic.ruviuz.MainActivity;
import stronglogic.ruviuz.R;

/**
 * A simple {@link DialogFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImageEditFragment.ImageFragListener} interface
 * to handle interaction events.
 * Use the {@link ImageEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageEditFragment extends DialogFragment {

    private final static String TAG = "RUVIUZImageEditFragment";
    private ImageFragListener mListener;

    private Toolbar mToolbar;

    private MainActivity mActivity;

    private ImageView editIv;

    private EditText commentEt;

    private Button commentBtn, deleteBtn, setMainBtn;

    private FloatingActionButton okayBtn;

    private String editImgUrl;
    private String editCommentText;
    private int editIndex, fileCount;
    private String[] fileUrls;
    private String[] fileComments;

    public ImageEditFragment() {
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
    public static ImageEditFragment newInstance(String param1, String param2) {
        ImageEditFragment fragment = new ImageEditFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!((MainActivity) getActivity()).readyStatus()) {
            ((MainActivity) getActivity()).hideActivity();
            ((MainActivity) getActivity()).dismissOtherDialogs(ImageEditFragment.this.getClass());
        }
        setStyle(DialogFragment.STYLE_NORMAL, R.style.RuvFullFrag);
        if (getArguments() != null) {
            editImgUrl = getArguments().getString("editImgUrl");
            editIndex = getArguments().getInt("editIndex");
            editCommentText = getArguments().getString("editCommentText");
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
                ImageEditFragment.this.dismiss();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View mView = inflater.inflate(R.layout.imageeditfragment, container, false);

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
            mToolbar.setBackgroundColor(Color.BLACK);
            mToolbar.setElevation(8f);
            mToolbar.setTitle(getActivity().getResources().getString(R.string.app_name));
            mToolbar.setTitleTextColor(ContextCompat.getColor(mActivity,R.color.ruvGreenAccent));
//            mToolbar.setTitleTextColor(ContextCompat.getColor(mActivity, R.color.ruvGreen));
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

        editIv = (ImageView) mView.findViewById(R.id.editImg);

        Glide.with(mActivity)
                .load(editImgUrl)
//                .override(72, 54)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(editIv);

        commentEt = (EditText) mView.findViewById(R.id.commentEt);
        if (editIndex > -1) {
            commentEt.setText(fileComments[editIndex]);
        }

        commentBtn = (Button) mView.findViewById(R.id.addComment);
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commentEt.getVisibility() == View.GONE) {
                    commentEt.setVisibility(View.VISIBLE);
                } else {
                    commentEt.setVisibility(View.GONE);
                }
            }
        });

        deleteBtn = (Button) mView.findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuBuilder menuBuilder = new MenuBuilder(mActivity);
                MenuInflater inflater = new MenuInflater(mActivity);
                inflater.inflate(R.menu.del_menu, menuBuilder);
                final MenuPopupHelper deleteMenu = new MenuPopupHelper(mActivity, menuBuilder, deleteBtn);
                deleteMenu.setForceShowIcon(true);

                menuBuilder.setCallback(new MenuBuilder.Callback() {
                    @Override
                    public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.yesDelete:
                                Log.d(TAG, "Deleting Image");
                                if (ImageEditFragment.this.fileUrls != null && ImageEditFragment.this.fileUrls[editIndex] != null) {
                                    ImageEditFragment.this.fileUrls[editIndex] = "";
                                    ImageEditFragment.this.fileComments[editIndex] = "";
                                    ImageEditFragment.this.fileCount--;
                                    Bundle mBundle = new Bundle();
                                    putBundleData(mBundle);
                                    ImageEditFragment.this.mListener.imageFragInteraction(mBundle, MainActivity.RUV_IMGEDIT_DELETE);
                                    ImageEditFragment.this.dismiss();
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

        okayBtn = (FloatingActionButton) mView.findViewById(R.id.okayBtn);
        okayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileComments[editIndex] = commentEt.getText().toString();
                Bundle sendBundle = new Bundle();
                putBundleData(sendBundle);
                ImageEditFragment.this.mListener.imageFragInteraction(sendBundle, MainActivity.RUV_IMGEDIT_FINISH);
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
        if (context instanceof ImageFragListener) {
            mListener = (ImageFragListener) context;
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
    public interface ImageFragListener {
        void imageFragInteraction(Bundle bundle, int request);
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
        }
        if (bundle.getStringArray("fileComments") != null) {
            if (this.fileComments == null) this.fileComments= new String[3];
            this.fileComments = bundle.getStringArray("fileComments");
        }
    }

    public void putBundleData(Bundle bundle) {
        Log.d(TAG, "putBundleData");
        bundle.putInt("fileCount", fileCount);
        if (this.fileUrls != null) {
            bundle.putStringArray("fileUrls", this.fileUrls);
        }
        if (this.fileComments != null)
            bundle.putStringArray("fileComments", this.fileComments);
        bundle.putInt("editMode", MainActivity.RUV_IMGEDIT_DELETE);
    }
}
