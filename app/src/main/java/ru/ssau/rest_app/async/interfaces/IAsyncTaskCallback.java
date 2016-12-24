package ru.ssau.rest_app.async.interfaces;

/**
 * Created by Дмитрий on 18.12.2016.
 */
public interface IAsyncTaskCallback<T> {
   public void onTaskComplete(int id,T result,String errMsg);
}
