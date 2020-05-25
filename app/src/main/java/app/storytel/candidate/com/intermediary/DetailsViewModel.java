package app.storytel.candidate.com.intermediary;

import android.os.AsyncTask;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import app.storytel.candidate.com.api.NetworkResource;
import app.storytel.candidate.com.api.Repository;
import app.storytel.candidate.com.intermediary.entitites.Comment;
import app.storytel.candidate.com.intermediary.entitites.Photo;
import app.storytel.candidate.com.intermediary.entitites.Post;

import static app.storytel.candidate.com.api.Status.*;

public class DetailsViewModel extends ViewModel {

    private Repository mRepository = new Repository();
    private MutableLiveData<NetworkResource<List<Comment>>> data;
    private MutableLiveData<NetworkResource<Pair<Post, Photo>>> postAndImage;

    public LiveData<NetworkResource<List<Comment>>> getComments(int id) {
        if (data == null) {
            data = new MutableLiveData<>();
            loadComments(id);
        }
        return data;
    }

    public LiveData<NetworkResource<Pair<Post, Photo>>> getPostAndImage(int id) {
        if (postAndImage == null) {
            postAndImage = new MutableLiveData<>();
            loadPost(id);
        }
        return postAndImage;
    }

    public void reloadData(int id){
        loadComments(id);
        loadPost(id);
    }

    private void loadComments(int id) {
        new AsyncTask<Void, Void, NetworkResource<List<Comment>>>() {
            @Override
            protected NetworkResource<List<Comment>> doInBackground(Void... voids) {
                return mRepository.getComments(id);
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                data.postValue(new NetworkResource<>(null, Loading));
            }

            @Override
            protected void onPostExecute(NetworkResource<List<Comment>> comments) {
                data.postValue(comments);
            }
        }.execute();
    }

    private void loadPost(int id) {
        new AsyncTask<Void, Void, NetworkResource<PostAndImages>>() {
            @Override
            protected NetworkResource<PostAndImages> doInBackground(Void... voids) {
                return mRepository.getPostAndImages();
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                postAndImage.postValue(new NetworkResource<>(null, Loading));
            }

            @Override
            protected void onPostExecute(NetworkResource<PostAndImages> postWithImage) {
                if (postWithImage.message == Success) {
                    for (int i = 0; i < postWithImage.data.mPosts.data.size(); i++) {
                        if (postWithImage.data.mPosts.data.get(i).id == id) {
                            Post post = postWithImage.data.mPosts.data.get(i);
                            Photo photo = postWithImage.data.mPhotos.data.get(i);
                            postAndImage.postValue(new NetworkResource<>(new Pair<>(post, photo), Success));
                            return;
                        }
                    }
                }
                postAndImage.postValue(new NetworkResource<>(null, Error));
            }
        }.execute();
    }
}
