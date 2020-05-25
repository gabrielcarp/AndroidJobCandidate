package app.storytel.candidate.com.ui;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;

import app.storytel.candidate.com.R;
import app.storytel.candidate.com.api.Status;
import app.storytel.candidate.com.intermediary.ScrollingViewModel;

public class ScrollingActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private PostAdapter mPostAdapter;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        mProgressBar = findViewById(R.id.loading_progress_bar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mPostAdapter = new PostAdapter(Glide.with(this), this);
        mRecyclerView.setAdapter(mPostAdapter);
        ViewModelProvider.Factory viewModelFactory = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication());
        ScrollingViewModel scrollingViewModel = new ViewModelProvider(this, viewModelFactory)
                .get(ScrollingViewModel.class);
        scrollingViewModel.getPostAndImages().observe(this, postAndImages -> {
            if (postAndImages == null || postAndImages.message == null) {
//                postAndImages.message = Status.Error;
                return;
            }
            switch (postAndImages.message) {
                case Error:
                    mProgressBar.setVisibility(View.GONE);
                    AlertDialog.Builder builder = new AlertDialog.Builder(ScrollingActivity.this);
                    builder.setMessage("Something went wrong, please try again");
                    builder.setCancelable(true);

                    builder.setPositiveButton("Retry",
                            (dialog, which) -> scrollingViewModel.loadData());
                    builder.setNegativeButton(
                            "No",
                            (dialog, id) -> dialog.cancel());
                    builder.create().show();
                    break;
                case Loading:
                    mProgressBar.setVisibility(View.VISIBLE);
                    break;
                case Success:
                    mPostAdapter.setData(postAndImages.data);
                    mProgressBar.setVisibility(View.GONE);
                    break;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}