package ru.ssau.rest_app.async;

import android.os.AsyncTask;

import ru.ssau.rest_app.async.interfaces.IAsyncTaskCallback;

/**
 * Created by Дмитрий on 18.12.2016.
 */
public abstract class AbstractCallbackTask extends AsyncTask<String,Void,String> {
    protected IAsyncTaskCallback<String> callback;
    protected int id;
    protected String errMsg="";

    public AbstractCallbackTask(IAsyncTaskCallback<String> callback, int id){
        this.callback=callback;
        this.id=id;
    }

    @Override
    abstract protected String doInBackground(String... strings);

    @Override
    protected void onPostExecute(String s) {
        if(callback!=null){
            callback.onTaskComplete(id,s,errMsg);
        }
    }
}
