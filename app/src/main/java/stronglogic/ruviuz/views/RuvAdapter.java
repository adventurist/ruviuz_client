package stronglogic.ruviuz.views;


import android.app.Activity;
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


    private Bundle mBundle;
    
//    public stListener stListener, stCListener;

    public RuvAdapter(Activity activity, ArrayList ruvList, Bundle mBundle)   {
        super();
        this.mActivity = activity;
        this.ruvList = ruvList;
        this.mBundle = mBundle;
        layoutInflater = LayoutInflater.from(activity);
    }


//    public interface stListener {
//        void stLikeMessage(String value, Boolean like, int position);
//    }

//    public void setStListen(stListener stListener, stListener stCListener) {
//        this.stListener = stListener;
//        this.stCListener = stCListener;
//    }

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


            ruvHolder.roofOptions.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Toast.makeText(mActivity, "CLICKED!!", Toast.LENGTH_SHORT).show();
                }
            });

//            Glide.with(mActivity)
//                    .load(getImg(Roof))
//                    .centerCrop()
//                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
//                    .into(ruvHolder.imageView);
//            if (Roof.getImage() != null && Roof.getImage().length() > 0) {
//
//
//                String extension = Roof.getImage().substring(Roof.getImage().lastIndexOf("."));
//                Log.d("VideoDBG", extension);
//                Log.d("VideoDBG", "Attempting to place video");
//
//
//                if (ruvHolder.profileImage != null && Roof.getProfileImage() != null) {
//                    Glide.with(mActivity)
//                            .load(Roof.getProfileImage().replace("\\/\\/", "//"))
//                            .centerCrop()
//                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
//                            .into(ruvHolder.profileImage);
//                }
//            }
        }
    }

    public void ruvDialog(String sid, String cid, int position) {
//        FragmentManager fm = mActivity.getFragmentManager();
//        CommentFragment commentFragment = new CommentFragment();
//        mBundle.putString("sid", sid);
//        mBundle.putString("uaid", cid);
//        mBundle.putInt("position", position);
//        commentFragment.setArguments(mBundle);
//        commentFragment.show(fm, "Add a comment");
//        commentFragment.setTargetFragment(commentFragment, 2);
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
