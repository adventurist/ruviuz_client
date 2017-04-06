package stronglogic.ruviuz.views;


import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration;

import java.util.ArrayList;

import stronglogic.ruviuz.R;
import stronglogic.ruviuz.RviewActivity;
import stronglogic.ruviuz.content.Roof;
import stronglogic.ruviuz.content.RuvFileInfo;
import stronglogic.ruviuz.content.Section;
import stronglogic.ruviuz.fragments.RuvFragment;
import stronglogic.ruviuz.fragments.UpdateFragment;
import stronglogic.ruviuz.util.RuvFilter;


/**
 * Created by logicp on 12/3/16.
 *Ruviuz
 **/

public class RuvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater layoutInflater;
    private Activity mActivity;

    public ArrayList<Roof> ruvList;
    
    private static final String TAG = "RuviuzRUVADAPTER";
    private final static int RUV_VIEW = 0;


    private String baseUrl, authToken;

    private String[] fileUrls;

    private int reopenDialog;

    private RuvFragment rFrag;

    private SectionAdapter secAdapter;


    public RuvAdapter(Activity activity, ArrayList ruvList, String baseUrl, String authToken, int reopenDialog, String[] fileUrls)   {
        super();
        this.mActivity = activity;
        this.ruvList = ruvList;
        this.baseUrl = baseUrl;
        this.authToken = authToken;
        this.reopenDialog = reopenDialog;
        this.fileUrls = fileUrls;
        layoutInflater = LayoutInflater.from(activity);
    }


    private static class RuvHolder extends RecyclerView.ViewHolder  {
        TextView addressTv, custTv, priceTv, idTv, rdyTv, flrTv, cTv1, cTv2, cTv3, cTime1, cTime2, cTime3;
        ImageView ruvPhoto1, ruvPhoto2, ruvPhoto3;
        Button roofOptions;
        private RecyclerView rv;
        
        
        RuvHolder(View mView) {
            super(mView);
            priceTv = (TextView) mView.findViewById(R.id.priceTv);
            addressTv = (TextView) mView.findViewById(R.id.addressTv);
            custTv = (TextView) mView.findViewById(R.id.custTv);
            idTv = (TextView) mView.findViewById(R.id.idTv );
            rdyTv = (TextView) mView.findViewById(R.id.readyTv);
            flrTv = (TextView) mView.findViewById((R.id.flrTv));
            this.rv = (RecyclerView) mView.findViewById(R.id.sectionRecycler);

            LinearLayoutManager layoutMgr = new LinearLayoutManager(this.rv.getContext(),
                    LinearLayoutManager.HORIZONTAL, false);
            layoutMgr.setAutoMeasureEnabled(true);
            layoutMgr.setRecycleChildrenOnDetach(true);
            this.rv.setLayoutManager(layoutMgr);
            this.rv.setNestedScrollingEnabled(false);
            this.rv.addItemDecoration(new VerticalDividerItemDecoration.Builder(this.rv.getContext()).build());
            this.rv.setAdapter(null);
            //TODO change imlementation so that it doesn't try to instantiate the RecyclerView if there is no Section (though, in the final form of this application, ALL roofs will have sections)

            ruvPhoto1 = (ImageView) mView.findViewById(R.id.ruvPhoto1);
            ruvPhoto2  = (ImageView) mView.findViewById(R.id.ruvPhoto2);
            ruvPhoto3  = (ImageView) mView.findViewById(R.id.ruvPhoto3);
            cTv1 = (TextView) mView.findViewById(R.id.ruvComment1);
            cTv2 = (TextView) mView.findViewById(R.id.ruvComment2);
            cTv3 = (TextView) mView.findViewById(R.id.ruvComment3);
            cTime1 = (TextView) mView.findViewById(R.id.cmtTime1);
            cTime2 = (TextView) mView.findViewById(R.id.cmtTime2);
            cTime3 = (TextView) mView.findViewById(R.id.cmtTime3);
            roofOptions = (Button) mView.findViewById(R.id.roofOptions);
        }
    }

    
    @Override
    public int getItemViewType(int position)    {
        return RUV_VIEW;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)  {
        if (viewType == RUV_VIEW) {
            return new RuvHolder(LayoutInflater.from(mActivity).inflate(R.layout.ruv_row,
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
            if (!Roof.getCustomerName().equals(""))
                ruvHolder.custTv.setText(Roof.getCustomerName());
            ruvHolder.flrTv.setText(String.valueOf(Roof.getFloors()));
            ruvHolder.rdyTv.setText(String.valueOf(Roof.getCleanUpFactor()));
            ruvHolder.priceTv.setText(ruvPrice);

            ArrayList<Section> feedList = Roof.getSections();

            if (feedList.size() > 0) {
                this.secAdapter = new SectionAdapter(mActivity, feedList);
                ruvHolder.rv.setAdapter(secAdapter);
//                secAdapter.notifyDataSetChanged();
            }

            ArrayList<RuvFileInfo> rFiles = Roof.getFiles();

            if (rFiles.size() > 0) {
                    Glide.with(mActivity)
                        .load(Roof.getFiles().get(0).getUrl())
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(ruvHolder.ruvPhoto1);
                if (!Roof.getFiles().get(0).getComment().equals("")) {
                    ruvHolder.cTv1.setVisibility(View.VISIBLE);
                    ruvHolder.cTv1.setText(Roof.getFiles().get(0).getComment());
                    ruvHolder.cTime1.setVisibility(View.VISIBLE);
                    ruvHolder.cTime1.setText(Roof.getFiles().get(0).getTime());
                }
                if (rFiles.size() > 1 && rFiles.get(1) != null) {
                    Glide.with(mActivity)
                            .load(Roof.getFiles().get(1).getUrl())
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(ruvHolder.ruvPhoto2);
                    if (!Roof.getFiles().get(1).getComment().equals("")) {
                        ruvHolder.cTv2.setVisibility(View.VISIBLE);
                        ruvHolder.cTv2.setText(Roof.getFiles().get(1).getComment());
                        ruvHolder.cTime2.setVisibility(View.VISIBLE);
                        ruvHolder.cTime2.setText(Roof.getFiles().get(1).getTime());
                    }
                } else {
                    Glide.clear(ruvHolder.ruvPhoto2);
                }
                if (rFiles.size() > 2 && rFiles.get(2) != null) {
                    Glide.with(mActivity)
                            .load(Roof.getFiles().get(2).getUrl())
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(ruvHolder.ruvPhoto3);
                    if (!Roof.getFiles().get(2).getComment().equals("")) {
                        ruvHolder.cTv3.setVisibility(View.VISIBLE);
                        ruvHolder.cTv3.setText(Roof.getFiles().get(2).getComment());
                        ruvHolder.cTime3.setVisibility(View.VISIBLE);
                        ruvHolder.cTime3.setText(Roof.getFiles().get(2).getTime());
                    }
                } else {
                    Glide.clear(ruvHolder.ruvPhoto3);
                }                
            
            } else {
                Glide.clear(ruvHolder.ruvPhoto1);
                Glide.clear(ruvHolder.ruvPhoto2);
                Glide.clear(ruvHolder.ruvPhoto3);

                Glide.with(mActivity)
                        .load(R.drawable.rvsamp)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(ruvHolder.ruvPhoto1);
            }
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
        rFrag = new RuvFragment();
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

    public void swapData(ArrayList<Roof> list) {
        this.ruvList.clear();
        this.ruvList.addAll(list);
    }

    /**
     * @param position
     * Removes a row from the RuvAdapter RecyclerView
     */
    public void deletePosition(final int position) {

        RuvAdapter.this.ruvList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, RuvAdapter.this.ruvList.size());

        if (rFrag != null && rFrag.isAdded()) {
            rFrag.dismiss();
        }

    }

}
