package app.storytel.candidate.com.ui;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import app.storytel.candidate.com.intermediary.PostAndImages;
import app.storytel.candidate.com.R;
import app.storytel.candidate.com.intermediary.entitites.Post;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    public static final String TAG = PostAdapter.class.getSimpleName();
    private PostAndImages mData;
    private RequestManager mRequestManager;
    private Activity mActivity;
    private RequestOptions mRequestOptions;

    public PostAdapter(RequestManager requestManager, Activity activity) {
        mRequestManager = requestManager;
        mActivity = activity;
        mRequestOptions = new RequestOptions()
                .fitCenter()
                .dontAnimate()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false));
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Post post = mData.mPosts.data.get(position);
        holder.title.setText(post.title);
        holder.body.setText(post.body);
        //int index = new Random().nextInt(mData.mPhotos.size() - 1);

        ////FIXME workaround for https://github.com/bumptech/glide/issues/4074
        //Loading images from another service since placeholder.com doesn't seem to work with glide.
        //https://github.com/kaffeberoende/AndroidJobCandidate/commit/a71c3c1f75a532ac12f88bcf49e141f8987a1ccd
        String imageUrl = mData.mPhotos.data.get(position).thumbnailUrl;

        mRequestManager.load(imageUrl).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Log.w(TAG, "failed loading image:" + e.getMessage());
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).apply(mRequestOptions).into(holder.image);
        holder.itemView.setOnClickListener(v -> {
// Pass data object in the bundle and populate details activity.
            Intent intent = new Intent(mActivity, DetailsActivity.class);
            intent.putExtra(DetailsActivity.POST_ID, post.id);
//            ActivityOptionsCompat options1 = ActivityOptionsCompat.
//                    makeSceneTransitionAnimation(mActivity, holder.image, "profile");
//            mActivity.startActivity(intent, options1.toBundle());
            mActivity.startActivity(intent);
        });
    }

    public void setData(PostAndImages data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (mData == null || mData.mPosts == null) ? 0 : mData.mPosts.data.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView body;
        ImageView image;

        PostViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            body = itemView.findViewById(R.id.body);
            image = itemView.findViewById(R.id.image);
        }
    }
}
