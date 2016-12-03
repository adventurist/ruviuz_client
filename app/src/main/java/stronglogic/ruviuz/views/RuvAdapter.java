//package twitcher.twitcherurlconnect;
//
//import android.app.Activity;
//import android.app.FragmentManager;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.MediaController;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.devbrackets.android.exomedia.listener.OnCompletionListener;
//import com.devbrackets.android.exomedia.listener.OnErrorListener;
//import com.devbrackets.android.exomedia.listener.OnPreparedListener;
//import com.devbrackets.android.exomedia.ui.widget.EMVideoView;
//
//import java.util.ArrayList;
//
////import im.ene.lab.toro.ToroPlayerViewHelper;
////import im.ene.lab.toro.ToroViewHolder;
////import im.ene.lab.toro.VideoPlayerManager;
////import im.ene.lab.toro.VideoPlayerManagerImpl;
////import im.ene.lab.toro.ext.ToroVideoViewHolder;
////import im.ene.lab.toro.ext.layeredvideo.SimpleVideoPlayer;
////import im.ene.lab.toro.player.widget.ToroVideoView;
//
////import android.widget.AdapterView;
//
///**
// * Created by logicp on 5/16/16.
// */
//public class StatusesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//    private ArrayList<StatusItem> statusesList;
//    private LayoutInflater layoutInflater;
//    private Activity mActivity;
//
//    protected boolean isPlayable = false;
//    private TwitchItemClickListener clickListener;
////    protected VideoPlayerManager delegate;
////    private final Cineer.Player videoPlayer = null;
////    SimpleVideoPlayer videoPlayer;
//
//    private String TAG;
//
//    private Bundle mBundle;
//
//    final private static int STATUS_VIEW = 0;
//    final private static int VIDEO_VIEW = 1;
//    final private static int COMMENT_VIEW = 2;
//
//    public void setOnItemClickListener(TwitchItemClickListener onItemClickListener) {
//        this.clickListener = onItemClickListener;
//    }
//
//    public stListener stListener, stCListener;
//
//    public StatusesAdapter(Activity activity, ArrayList statusesList, Bundle mBundle)   {
//        super();
////        this.delegate = new VideoPlayerManagerImpl();
//        this.mActivity = activity;
//        this.statusesList = statusesList;
//        this.mBundle = mBundle;
//        this.TAG = mActivity.getApplicationInfo().toString();
//        layoutInflater = LayoutInflater.from(activity);
//    }
//
//
//    public interface stListener {
//        void stLikeMessage(String value, Boolean like, int position);
//    }
//
//    public void setStListen(stListener stListener, stListener stCListener) {
//        this.stListener = stListener;
//        this.stCListener = stCListener;
//    }
//
//    static class StHolder extends RecyclerView.ViewHolder  {
//        TextView statusBody, statusDate, statusMessage, posterName, likesView;
//        ImageView profileImage, imageView, likeBtn;
//        Button commentBtn;
//
//        String videoUrl;
//
//        public String getVideoUrl() {
//            return videoUrl;
//        }
//
//        public StHolder(View mView) {
//            super(mView);
//            posterName = (TextView) mView.findViewById(R.id.posterName);
//            profileImage = (ImageView) mView.findViewById(R.id.profileImage);
//            statusDate = (TextView) mView.findViewById(R.id.statusDate);
//            statusMessage = (TextView) mView.findViewById(R.id.statusMessage);
//            statusBody = (TextView) mView.findViewById((R.id.stFeedBody));
//            imageView = (ImageView) mView.findViewById(R.id.stFeedImage);
//            likesView = (TextView) mView.findViewById(R.id.likes);
//            likeBtn = (ImageView) mView.findViewById(R.id.likeBtn);
//            commentBtn = (Button) mView.findViewById(R.id.commentBtn);
//        }
//    }
//
//
//    class StVolder extends RecyclerView.ViewHolder {
//
////        protected final OnPlayerStateChangeListener helper = null;
//        protected boolean isPlayable = false;
////        protected final ToroPlayerViewHelper helper;
//        protected MediaController vController;
//
//        TextView statusBody, statusDate, statusMessage, posterName, likesView;
//        ImageView profileImage, likeBtn, videoImg, videoImgBtn;
////        VideoView simpleVideo;
//        EMVideoView simple2Video;
////        ToroVideoView twitVideo;
//
//        Button commentBtn;
//
//        public StVolder(View mView) {
//            super(mView);
//            posterName = (TextView) mView.findViewById(R.id.posterName);
//            profileImage = (ImageView) mView.findViewById(R.id.profileImage);
//            statusDate = (TextView) mView.findViewById(R.id.statusDate);
//            statusMessage = (TextView) mView.findViewById(R.id.statusMessage);
//            statusBody = (TextView) mView.findViewById((R.id.stFeedBody));
////            simpleVideo = (VideoView) mView.findViewById(R.id.simple_video_view);
//            simple2Video = (EMVideoView) mView.findViewById(R.id.simple_video_view);
////            twitVideo = (ToroVideoView) mView.findViewById(R.id.video_view);
////            videoImg = (ImageView) mView.findViewById(R.id.thumbnail);
////            videoImgBtn = (ImageView) mView.findViewById(R.id.video_play_img_btn);
//            likesView = (TextView) mView.findViewById(R.id.likes);
//            likeBtn = (ImageView) mView.findViewById(R.id.likeBtn);
//            commentBtn = (Button) mView.findViewById(R.id.commentBtn);
////            helper = new ToroPlayerViewHelper(this, mView);
//            vController = new MediaController(mActivity);
//        }
//
////        @Override protected ToroVideoView findVideoView(View itemView) {
////            return (ToroVideoView)itemView.findViewById(R.id.video_view);
////        }
//
//        private StatusItem mItem;
//
////        @Override public void bind(RecyclerView.Adapter stAdapter, Object object) {
////            Log.d("STAdapt", "attempting to bind");
////            if (object != null && object instanceof StatusItem) {
////                Log.d("STAdapt", "Setting media");
////                twitVideo.setMedia(Uri.parse(((StatusItem) object).getImage()));
////            }
////        }
//
////        @Override
////        public @Nullable String getMediaId() {
////            return this.mItem != null ? this.mItem.getImage() + "@" + getAdapterPosition() : null;
////        }
//    }
//
//    static class commentHolder extends RecyclerView.ViewHolder  {
//        ImageView commentImg;
//        TextView commentText;
//        TextView commentLikes;
//        ImageView likeCommentBtn;
//        ImageView delCommentBtn;
//
//        public commentHolder(View mView)    {
//            super(mView);
//            commentImg = (ImageView) mView.findViewById(R.id.commentImg);
//            commentText = (TextView) mView.findViewById(R.id.commentTxt);
//            commentLikes = (TextView) mView.findViewById(R.id.cLikes);
//            likeCommentBtn = (ImageView) mView.findViewById(R.id.likeCommentBtn);
//            delCommentBtn = (ImageView) mView.findViewById(R.id.delCommentBtn);
//        }
//    }
//
//
//    @Override
//    public int getItemViewType(int position)    {
//        if (statusesList.get(position).getIsComment()) {
//            return COMMENT_VIEW;
//        } else {
//            if (statusesList.get(position).getIsVideo()) {
//                return VIDEO_VIEW;
//            } else {
//                return STATUS_VIEW;
//            }
//        }
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)  {
//        if (viewType == STATUS_VIEW) {
//            return new StHolder(LayoutInflater.from(mActivity).inflate(R.layout.status,
//                    parent, false));
//        } else if (viewType == VIDEO_VIEW) {
//            return new StVolder(LayoutInflater.from(mActivity).inflate(R.layout.vstatus, parent, false));
//        } else {
//            return new commentHolder(LayoutInflater.from(mActivity).inflate(R.layout.commentlayout,
//                    parent, false));
//        }
//    }
//
//    @Override
//    public void onViewRecycled(RecyclerView.ViewHolder holder)  {
//
//        if (holder.getClass() == StVolder.class) {
//            Log.d(TAG, "Video Class Recycled");
//        }
//
//    }
//
//
//    @Override
//    public int getItemCount()  {return this.statusesList.size();}
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
//
//        StatusItem statusItem = statusesList.get(position);
//        final String sid = statusItem.getSid();
//        Log.d("Is Video?:", String.valueOf(statusItem.getIsVideo()));
//        if (holder.getItemViewType() == COMMENT_VIEW) {
//            final commentHolder commentHolder = (commentHolder) holder;
//            if (commentHolder.commentImg != null && statusItem.getProfileImage() != null) {
//                try {
//                    Glide.with(mActivity)
//                            .load(statusItem.getProfileImage())
//                            .centerCrop()
//                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
//                            .into(commentHolder.commentImg);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            final boolean cLike = statusItem.getCLike();
//            final String cid = statusItem.getCid();
//            String cLikes = statusItem.getCLikes() + " like this";
//            commentHolder.commentText.setText(statusItem.getCommentBody());
//            commentHolder.commentLikes.setText(cLikes);
//            if (cLike) {
//                commentHolder.likeCommentBtn.setBackgroundResource(R.drawable.skeleton13);
//            } else {
//                commentHolder.likeCommentBtn.setBackgroundResource(R.drawable.like);
//            }
//            commentHolder.likeCommentBtn.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    if (cLike) {
//                        commentHolder.likeCommentBtn.setBackgroundResource(R.drawable.like);
//                    } else {
//                        commentHolder.likeCommentBtn.setBackgroundResource(R.drawable.skeleton13);
//                    }
//                    stCListener.stLikeMessage(cid, cLike, position);
//                    notifyItemChanged(position);
//                }
//            });
//            commentHolder.delCommentBtn.setBackgroundResource(R.drawable.del);
//            Log.d("StAdaptDBG", "Comment item added");
//        } else if (holder.getItemViewType() == VIDEO_VIEW) {
//            final StVolder vHolder = (StVolder) holder;
//            Log.d("StAdaptVid", "We have a video holder");
//
//            if (vHolder.simple2Video != null) {
//                vHolder.simple2Video.setVideoURI(Uri.parse(statusItem.getImage()));
//                vHolder.simple2Video.setVisibility(View.VISIBLE);
//                if (vHolder.simple2Video.getVideoControls() != null)
//                    vHolder.simple2Video.getVideoControls().setTitle("TwitcherVideo");
//                vHolder.simple2Video.setReleaseOnDetachFromWindow(false);
//                vHolder.simple2Video.setOnPreparedListener(new OnPreparedListener() {
//                    @Override
//                    public void onPrepared() {
//                        Log.d(TAG, "Video Prepared");
//                        vHolder.simple2Video.showControls();
//                    }
//                });
//
//                vHolder.simple2Video.setOnCompletionListener(new OnCompletionListener() {
//                    @Override
//                    public void onCompletion() {
//                        vHolder.simple2Video.stopPlayback();
//                        vHolder.simple2Video.start();
////                            vHolder.simple2Video.stopPlayback();
////                            vHolder.simple2Video.seekTo(0);
//
//                    }
//                });
//
//                vHolder.simple2Video.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (vHolder.simple2Video.isPlaying()) {
//                            vHolder.simple2Video.pause();
//                        } else {
//
//                            vHolder.simple2Video.start();
//                        }
//                    }
//                });
//
//                vHolder.simple2Video.setOnErrorListener(new OnErrorListener() {
//                    @Override
//                    public boolean onError() {
////                            vHolder.simple2Video.getContext()
//                        for (StackTraceElement value : Thread.currentThread().getStackTrace()) {
//                            Log.e("VideoError", value.toString());
//                        }
//                        Log.d("ExoMediaDebug", "error detected");
//                        return false;
//                    }
//                });
//
//                vHolder.simple2Video.setOnPreparedListener(new OnPreparedListener() {
//                    @Override
//                    public void onPrepared() {
//                        Log.d("OnPrep", "SO PREPARED");
//                        if (vHolder.simple2Video.isPlaying()) {
//                            vHolder.simple2Video.stopPlayback();
//                        }
//                    }
//                });
//            }
////                        vHolder.simpleVideo.setVideoURI(Uri.parse(statusItem.getImage()));
////                vHolder.simpleVideo.setVisibility(View.VISIBLE);
////                vHolder.vController.show();
////            vHolder.simpleVideo.setVideoPath(statusItem.getImage());
////                vHolder.simpleVideo.setMediaController(vHolder.vController);
////                vHolder.simpleVideo.requestFocus();
////
////                vHolder.simpleVideo.setBackgroundColor(Color.CYAN);
//        } else {
//            final StHolder stHolder = (StHolder) holder;
//            final Boolean like = statusItem.getLike();
//
//            Log.d("REGULARISHERE", statusItem.getSid());
//
//            stHolder.statusBody.setText(statusItem.getContent());
//            stHolder.statusMessage.setText(statusItem.getStMessage());
//            stHolder.statusDate.setText(statusItem.getdate());
//            stHolder.posterName.setText(statusItem.getPoster());
//
//            String likeTxt = statusItem.getLikes() + " likes";
//            stHolder.likesView.setText(likeTxt);
//            if (like) {
//                stHolder.likeBtn.setBackgroundResource(R.drawable.skeleton13);
//            } else {
//                stHolder.likeBtn.setBackgroundResource(R.drawable.like);
//            }
//
//            stHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    if (like) {
//                        stHolder.likeBtn.setBackgroundResource(R.drawable.like);
//                    } else {
//                        stHolder.likeBtn.setBackgroundResource(R.drawable.skeleton13);
//                    }
//                    stListener.stLikeMessage(sid, like, position);
//                    notifyItemChanged(position);
//                }
//            });
//
////                        Log.d("on" + statusItem.getSid(), " " + statusItem.getImage());
//            Glide.with(mActivity)
//                    .load(getImg(statusItem))
//                    .centerCrop()
//                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
//                    .into(stHolder.imageView);
//            if (statusItem.getImage() != null && statusItem.getImage().length() > 0) {
//
//
//                String extension = statusItem.getImage().substring(statusItem.getImage().lastIndexOf("."));
//                Log.d("VideoDBG", extension);
//                Log.d("VideoDBG", "Attempting to place video");
//
//
//                if (stHolder.profileImage != null && statusItem.getProfileImage() != null) {
//                    Glide.with(mActivity)
//                            .load(statusItem.getProfileImage().replace("\\/\\/", "//"))
//                            .centerCrop()
//                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
//                            .into(stHolder.profileImage);
//                }
//            }
//            stHolder.commentBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    commentDialog(cid, sid, position);
//                }
//            });
//
//        }
//    }
//
//    public void commentDialog(String sid, String cid, int position) {
//        FragmentManager fm = mActivity.getFragmentManager();
//        CommentFragment commentFragment = new CommentFragment();
//        mBundle.putString("sid", sid);
//        mBundle.putString("uaid", cid);
//        mBundle.putInt("position", position);
//        commentFragment.setArguments(mBundle);
//        commentFragment.show(fm, "Add a comment");
//        commentFragment.setTargetFragment(commentFragment, 2);
//    }
//
//    public String getImg(StatusItem mItem) {
//        if ((mItem.getImage()) == null) return null;
//        if (mItem.getImage().substring(mItem.getImage().lastIndexOf(".")).equals(".mp4")) return null;
//        String url = mItem.getImage();
//
//        return url.replace("\\/\\/", "//");
//    }
//
//}
