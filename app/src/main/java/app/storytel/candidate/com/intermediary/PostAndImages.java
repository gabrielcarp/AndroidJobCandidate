package app.storytel.candidate.com.intermediary;

import java.util.List;

import app.storytel.candidate.com.api.NetworkResource;
import app.storytel.candidate.com.api.Status;
import app.storytel.candidate.com.intermediary.entitites.Photo;
import app.storytel.candidate.com.intermediary.entitites.Post;

public class PostAndImages {
    public NetworkResource<List<Post>> mPosts = new NetworkResource<>(null, Status.Loading);
    public NetworkResource<List<Photo>> mPhotos = new NetworkResource<>(null, Status.Loading);

    public PostAndImages(List<Post> post, List<Photo> photos) {
        if (post != null && post.isEmpty() == false) {
            mPosts.data = post;
            mPosts.message = Status.Success;
        } else {
            mPosts.message = Status.Error;
        }

        if (photos != null && photos.isEmpty() == false) {
            mPhotos.data = photos;
            mPhotos.message = Status.Success;
        } else {
            mPhotos.message = Status.Error;
        }
    }
}
