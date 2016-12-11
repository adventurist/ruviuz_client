package stronglogic.ruviuz.views;


import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import stronglogic.ruviuz.R;
import stronglogic.ruviuz.content.Roof;
import stronglogic.ruviuz.fragments.RuvFragment;
import stronglogic.ruviuz.fragments.UpdateFragment;


/**
 * Created by logicp on 12/3/16.
 *Ruviuz
 **/

public class RuvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Roof> ruvList;
    private LayoutInflater layoutInflater;
    private Activity mActivity;
    
    private static final String TAG = "RuviuzRUVADAPTER";
    private final static int RUV_VIEW = 0;

    private String baseUrl, authToken;

    private Bundle mBundle;


    public RuvAdapter(Activity activity, ArrayList ruvList, String baseUrl, String authToken)   {
        super();
        this.mActivity = activity;
        this.ruvList = ruvList;
        this.baseUrl = baseUrl;
        this.authToken = authToken;
        layoutInflater = LayoutInflater.from(activity);
    }


    static class RuvHolder extends RecyclerView.ViewHolder  {
        TextView addressTv, priceTv, idTv, widthTv, lengthTv, slopeTv;
        ImageView ruvPhoto1, ruvPhoto2, ruvPhoto3;
        Button roofOptions;
        
        
        public RuvHolder(View mView) {
            super(mView);
            priceTv = (TextView) mView.findViewById(R.id.priceTv);
            addressTv = (TextView) mView.findViewById(R.id.addressTv);
            idTv = (TextView) mView.findViewById(R.id.idTv );
            widthTv = (TextView) mView.findViewById(R.id.widthTv);
            lengthTv = (TextView) mView.findViewById((R.id.lengthTv));
            slopeTv = (TextView) mView.findViewById(R.id.slopeTv);
            ruvPhoto1 = (ImageView) mView.findViewById(R.id.ruvPhoto1);
            ruvPhoto2  = (ImageView) mView.findViewById(R.id.ruvPhoto2);
            ruvPhoto3  = (ImageView) mView.findViewById(R.id.ruvPhoto3);
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

    
//    @Override
//    public void onViewRecycled(RecyclerView.ViewHolder holder)  {
//
////        if (holder.getClass() == RuvHolder.class) {
////            Log.d(TAG, "Video Class Recycled");
////        }
//
//    }


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
            Glide.with(mActivity)
                    .load(R.drawable.rvsamp)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(ruvHolder.ruvPhoto1);

            final int ruvId = Roof.getId();

            ruvHolder.roofOptions.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    ruvDialog(ruvId, ruvHolder.getAdapterPosition());
                }
            });

            if (Roof.isUpdated()) {
                final Roof mRoof = Roof;
                ruvHolder.itemView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorUpdated));

                final Handler xHandler = new Handler();
                xHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ruvHolder.itemView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorRow));
                        mRoof.toggleJustUpdated();
                    }
                }, 4000);


//                RelativeLayout rl = (RelativeLayout)ruvHolder.itemView;
//
//                RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) rl.getLayoutParams();
//                lp.height = rl.getHeight() + 48;
//                rl.setLayoutParams(lp);

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
        rFrag.setArguments(mBundle);
        rFrag.show(fm, "ruvFrag");
        rFrag.setTargetFragment(rFrag, 2);
    }

//    public String getImg(Roof mRuv) {
//        ArrayList<URL> photolist = mRuv.getPhotos();
////            for (int i = 0; i < photolist.size(); i++) {
////                String urlStr = photolist.get(i).toString().replace("\\//", "//");
//////                photolist.get(i) = new URL(urlStr);
////            }
//        
//        
//    }

}
