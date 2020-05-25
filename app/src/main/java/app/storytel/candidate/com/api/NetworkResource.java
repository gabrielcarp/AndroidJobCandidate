package app.storytel.candidate.com.api;

import androidx.annotation.Nullable;

// A generic class that contains data and status about loading this data.
public class NetworkResource<T> {
    public T data;
    public Status message;

    public NetworkResource(@Nullable T data, Status message){
        data = data;
        message = message;
    }
}