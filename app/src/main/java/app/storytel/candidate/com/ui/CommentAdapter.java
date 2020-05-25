package app.storytel.candidate.com.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import app.storytel.candidate.com.R;
import app.storytel.candidate.com.intermediary.entitites.Comment;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> mData;

    public CommentAdapter() {
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comment comment = mData.get(position);
        holder.title.setText(comment.name);
        holder.body.setText(comment.body);
    }

    public void setData(List<Comment> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : Math.min(mData.size(), 3);
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView body;

        public CommentViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title1);
            body = itemView.findViewById(R.id.description1);
        }
    }
}
