package stronglogic.ruviuz.views;


import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import stronglogic.ruviuz.R;
import stronglogic.ruviuz.content.Roof;
import stronglogic.ruviuz.fragments.RuvFragment;
import stronglogic.ruviuz.fragments.UpdateFragment;
import stronglogic.ruviuz.util.RuvFilter;


/**
 * Created by logicp on 12/3/16.
 *Ruviuz
 **/

public class IndexAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Roof> filteredList;
    private LayoutInflater layoutInflater;
    private Activity mActivity;

    public ArrayList<Roof> ruvList;

    private static final String TAG = "RuviuzIndexAdapter";
    private final static int RUV_VIEW = 0;

    private String baseUrl, authToken;

    private String[] fileUrls;

    private int reopenDialog;

    private Bundle mBundle;

    private RuvFilter ruvFilter;


    public IndexAdapter(Activity activity, ArrayList ruvList, String baseUrl, String authToken, int reopenDialog, String[] fileUrls)   {
        super();
        this.mActivity = activity;
        this.ruvList = ruvList;
        this.filteredList = new ArrayList<>();
        this.baseUrl = baseUrl;
        this.authToken = authToken;
        this.reopenDialog = reopenDialog;
        this.fileUrls = fileUrls;
        layoutInflater = LayoutInflater.from(activity);
    }


    public void setFilter(RuvFilter filter) {
        this.ruvFilter = filter;
    }

    public void setFilterType(RuvFilter.filterType filterType) {
        if (filterType == RuvFilter.filterType.CUSTOMER || filterType == RuvFilter.filterType.ADDRESS) {
            if (!ruvFilter.setType(filterType)) {
                Log.d(TAG, "Can't toggle filter");
            }
        }
    }


    private static class RuvHolder extends RecyclerView.ViewHolder  {
        TextView addressTv, priceTv, idTv, widthTv, lengthTv, slopeTv;
        ImageButton roofOptions;

        RuvHolder(View mView) {
            super(mView);
            priceTv = (TextView) mView.findViewById(R.id.priceTv);
            addressTv = (TextView) mView.findViewById(R.id.addressTv);
            idTv = (TextView) mView.findViewById(R.id.idTv );
            widthTv = (TextView) mView.findViewById(R.id.widthTv);
            lengthTv = (TextView) mView.findViewById((R.id.lengthTv));
            slopeTv = (TextView) mView.findViewById(R.id.slopeTv);
            roofOptions = (ImageButton) mView.findViewById(R.id.roofOptions);
        }
    }

    
    @Override
    public int getItemViewType(int position)    {
        return RUV_VIEW;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)  {
        if (viewType == RUV_VIEW) {
            return new RuvHolder(LayoutInflater.from(mActivity).inflate(R.layout.index_row,
                    parent, false));
        }
        return null;
    }


    @Override
    public int getItemCount()  {return this.ruvList.size();}

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        Roof Roof = ruvList.get(holder.getAdapterPosition());
        if (holder.getItemViewType() == RUV_VIEW) {
            final RuvHolder ruvHolder = (RuvHolder) holder;
            String ruvPrice = "$" + String.valueOf(Roof.getPrice());
            ruvHolder.idTv.setText(String.valueOf(Roof.getId()));
            ruvHolder.addressTv.setText(Roof.getAddress());
            ruvHolder.widthTv.setText(String.valueOf(Roof.getWidth()));
            ruvHolder.lengthTv.setText(String.valueOf(Roof.getLength()));
            ruvHolder.slopeTv.setText(String.valueOf(Roof.getSlope()));
            ruvHolder.priceTv.setText(ruvPrice);
            final int ruvId = Roof.getId();

            ruvHolder.roofOptions.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    ruvDialog(ruvId, ruvHolder.getAdapterPosition());
                }
            });

            if (reopenDialog > 0) {
                ruvDialog(reopenDialog, ruvHolder.getAdapterPosition());
            }

            if (Roof.isUpdated()) {
                final Roof mRoof = Roof;
                ruvHolder.itemView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorUpdated));

                final Handler xHandler = new Handler();
                xHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ruvHolder.itemView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.common_google_signin_btn_text_dark_disabled));
                        mRoof.toggleJustUpdated();
                    }
                }, 4000);


                FragmentManager fm = mActivity.getFragmentManager();
                UpdateFragment upFrag = new UpdateFragment();
                fm.beginTransaction().add(upFrag, "upFrag").commit();


                final Handler zHandler = new Handler();
                zHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        removeUpFrag(ruvHolder.itemView);
                    }
                }, 10000);
            }
        }
    }

    private void removeUpFrag(View view) {
        if (mActivity.getFragmentManager().findFragmentByTag("upFrag") != null) {
            UpdateFragment upFrag = (UpdateFragment) mActivity.getFragmentManager().findFragmentByTag("upFrag");
            if (upFrag.isAdded()) {
                mActivity.getFragmentManager().beginTransaction().remove(upFrag).commit();
            }

//            RelativeLayout rl = (RelativeLayout)view;
//
//            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) rl.getLayoutParams();
//            lp.height = rl.getHeight() - 48;
//            rl.setLayoutParams(lp);
        }
    }

    private void ruvDialog(int id, int position) {
        FragmentManager fm = mActivity.getFragmentManager();
        RuvFragment rFrag = new RuvFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString("authToken", authToken);
        mBundle.putString("baseUrl", baseUrl);
        mBundle.putInt("ruvId", id);
        mBundle.putInt("position", position);
        mBundle.putStringArray("fileUrls", fileUrls);
        rFrag.setArguments(mBundle);
        rFrag.show(fm, "ruvFrag");
        rFrag.setTargetFragment(rFrag, 2);
    }

}
