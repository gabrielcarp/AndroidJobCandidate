package app.storytel.candidate.com.intermediary;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import app.storytel.candidate.com.api.NetworkResource;
import app.storytel.candidate.com.api.Repository;

public class ScrollingViewModel extends ViewModel {

    private Repository mRepository = new Repository();
    private MutableLiveData<NetworkResource<PostAndImages>> data;

    public LiveData<NetworkResource<PostAndImages>> getPostAndImages() {
        if (data == null) {
            data = new MutableLiveData<>();
            loadData();
        }
        return data;
    }

    public void loadData() {
        new AsyncTask<Void, Void, NetworkResource<PostAndImages>>() {
            @Override
            protected NetworkResource<PostAndImages> doInBackground(Void... voids) {
                return mRepository.getPostAndImages();
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                data.postValue(new NetworkResource<>(null, app.storytel.candidate.com.api.Status.Loading));
            }

            @Override
            protected void onPostExecute(NetworkResource<PostAndImages> postAndImages) {
                data.postValue(postAndImages);
            }
        }.execute();
    }
}