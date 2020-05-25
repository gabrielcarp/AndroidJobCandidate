package app.storytel.candidate.com.ui;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import app.storytel.candidate.com.R;
import app.storytel.candidate.com.api.Status;
import app.storytel.candidate.com.intermediary.DetailsViewModel;

public class DetailsActivity extends AppCompatActivity {
    public static final String POST_ID = "Id_post";
    private RecyclerView mRecyclerView;
    private CommentAdapter mCommentAdapter;
    private ImageView mImageView;
    private TextView mTextView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mProgressBar = findViewById(R.id.loading_progress_bar);
        mImageView = findViewById(R.id.backdrop);
        mTextView = findViewById(R.id.details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mCommentAdapter = new CommentAdapter();
        mRecyclerView.setAdapter(mCommentAdapter);
        ViewModelProvider.Factory viewModelFactory = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication());
        DetailsViewModel detailsViewModel = new ViewModelProvider(this, viewModelFactory)
                .get(DetailsViewModel.class);
        int postId = 1;
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(POST_ID)) {
            postId = extras.getInt(POST_ID);
        }
        int finalPostId = postId;
        detailsViewModel.getComments(postId).observe(this, comments -> {
            if (comments == null || comments.message == null) {
//                comments.message = Status.Error;
                return;
            }
            switch (comments.message) {
                case Error:
                    mProgressBar.setVisibility(View.GONE);
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);
                    builder.setMessage("Something went wrong, please try again");
                    builder.setCancelable(true);

                    builder.setPositiveButton("Retry",
                            (dialog, which) -> detailsViewModel.reloadData(finalPostId));
                    builder.setNegativeButton(
                            "No",
                            (dialog, id) -> dialog.cancel());
                    builder.create().show();
                    break;
                case Loading:
                    mProgressBar.setVisibility(View.VISIBLE);
                    break;
                case Success:
                    mCommentAdapter.setData(comments.data);
                    mProgressBar.setVisibility(View.GONE);
                    break;
            }
        });
        RequestOptions requestOptions = new RequestOptions()
                .fitCenter()
                .dontAnimate()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);
        detailsViewModel.getPostAndImage(postId).observe(this, postWithImage -> {
            if (postWithImage.message == Status.Success) {
                String imageURL = postWithImage.data.second.url;
                Glide.with(this).load(imageURL).apply(requestOptions).into(mImageView);
                mTextView.setText(postWithImage.data.first.body);
                getSupportActionBar().setTitle(postWithImage.data.first.title);
            }
        });
    }
}