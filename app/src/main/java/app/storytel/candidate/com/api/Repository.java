package app.storytel.candidate.com.api;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import app.storytel.candidate.com.intermediary.PostAndImages;
import app.storytel.candidate.com.intermediary.entitites.Comment;
import app.storytel.candidate.com.intermediary.entitites.Photo;
import app.storytel.candidate.com.intermediary.entitites.Post;

public class Repository {
    public static final String TAG = Repository.class.getSimpleName();
    private static final String POSTS_URL = "https://jsonplaceholder.typicode.com/posts";
    private static final String PHOTOS_URL = "https://jsonplaceholder.typicode.com/photos";
    private static final String COMMENTS_URL = "https://jsonplaceholder.typicode.com/posts/%s/comments";
    private static final int CONNECTION_TIMEOUT_MILLISECONDS = 10000; // 10 seconds

    public NetworkResource<PostAndImages> getPostAndImages() {
        List<Post> posts = getPosts();
        List<Photo> photos = getPhotos();
        PostAndImages postAndImages = new PostAndImages(posts, photos);
        if (postAndImages.mPhotos.message == postAndImages.mPosts.message) {
            return new NetworkResource<>(postAndImages, postAndImages.mPosts.message);
        } else if (postAndImages.mPhotos.message == Status.Error
                || postAndImages.mPosts.message == Status.Error) {
            return new NetworkResource<>(null, Status.Error);
        } else {
            return new NetworkResource<>(postAndImages, Status.Loading);
        }
    }

    public NetworkResource<List<Comment>> getComments(int postId) {
        List<Comment> comments = null;
        String result = getData(String.format(COMMENTS_URL, postId));
        if (result != null && result.isEmpty() == false) {
            Comment[] array = new Gson().fromJson(result, Comment[].class);
            comments = Arrays.asList(array);
        }
        if (comments != null && comments.isEmpty() == false) {
            return new NetworkResource<>(comments, Status.Success);
        } else {
            return new NetworkResource<>(null, Status.Error);
        }
    }

    private List<Post> getPosts() {
        List<Post> posts = null;
        String result = getData(POSTS_URL);
        if (result != null && result.isEmpty() == false) {
            Post[] array = new Gson().fromJson(result, Post[].class);
            posts = Arrays.asList(array);
        }
        return posts;
    }

    private String getData(String URL) {
        InputStream stream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT_MILLISECONDS);
            urlConnection.setReadTimeout(CONNECTION_TIMEOUT_MILLISECONDS);
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
//                throw new IOException("HTTP error code: " + responseCode);
                Log.e(TAG, "HTTP error code: " + responseCode);
            }
            stream = urlConnection.getInputStream();
            if (stream != null) {
                return readStream(stream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return "";
    }

    ////FIXME workaround for https://github.com/bumptech/glide/issues/4074
    //Loading images from another service since placeholder.com doesn't seem to work with glide.
    //https://github.com/kaffeberoende/AndroidJobCandidate/commit/a71c3c1f75a532ac12f88bcf49e141f8987a1ccd
    private List<Photo> getPhotos() {
        List<Photo> photos = null;
        String result = getData(PHOTOS_URL);
        if (result != null && result.isEmpty() == false) {
            Photo[] array = new Gson().fromJson(result, Photo[].class);
            photos = Arrays.asList(array);
        }
        for (int i = 0; photos != null && i < photos.size(); i++) {
            photos.get(i).thumbnailUrl = String.format("https://i.picsum.photos/id/%s/200/200.jpg", i);
            photos.get(i).url = String.format("https://i.picsum.photos/id/%s/600/600.jpg", i);
        }
        return photos;
    }

    /**
     * Converts the contents of an InputStream to a String.
     */
    public String readStream(InputStream stream)
            throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        char[] rawBuffer = new char[256];
        int readSize;
        StringBuffer buffer = new StringBuffer();
        while (((readSize = reader.read(rawBuffer)) != -1)) {
            buffer.append(rawBuffer, 0, readSize);
        }
        return buffer.toString();
    }
}