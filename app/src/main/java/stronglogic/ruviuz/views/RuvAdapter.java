package stronglogic.ruviuz.views;


import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import stronglogic.ruviuz.R;
import stronglogic.ruviuz.content.Roof;
import stronglogic.ruviuz.fragments.RuvFragment;


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
            addressTv = (TextView) mView.findViewById(R.id.addressTv);
            priceTv = (TextView) mView.findViewById(R.id.priceTv);
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

        Roof Roof = ruvList.get(position);
        if (holder.getItemViewType() == RUV_VIEW) {
            final RuvHolder ruvHolder = (RuvHolder) holder;

            ruvHolder.idTv.setText(String.valueOf(Roof.getId()));
            ruvHolder.addressTv.setText(Roof.getAddress());
            ruvHolder.widthTv.setText(String.valueOf(Roof.getWidth()));
            ruvHolder.lengthTv.setText(String.valueOf(Roof.getLength()));
            ruvHolder.slopeTv.setText(String.valueOf(Roof.getSlope()));
            ruvHolder.priceTv.setText(String.valueOf(Roof.getPrice()));
            final int ruvId = Roof.getId();

            ruvHolder.roofOptions.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Toast.makeText(mActivity, "CLICKED!!", Toast.LENGTH_SHORT).show();
                    ruvDialog(ruvId);
                }
            });
        }
    }

    public void ruvDialog(int id) {
        FragmentManager fm = mActivity.getFragmentManager();
        RuvFragment rFrag = new RuvFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString("authToken", authToken);
        mBundle.putString("baseUrl", baseUrl);
        mBundle.putInt("ruvId", id);
        rFrag.setArguments(mBundle);
        rFrag.show(fm, "Modify a Roof");
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
