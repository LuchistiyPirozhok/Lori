package ru.ssau.rest_app.async;

import android.os.AsyncTask;

import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

import ru.ssau.rest_app.async.interfaces.IAsyncTaskCallback;

/**
 * Created by Дмитрий on 18.12.2016.
 */
public class LoginTask extends AbstractCallbackTask{

    private final String host;
    private final int port;
    private final String user;
    private final String password;
    private final String locale;

    public LoginTask(IAsyncTaskCallback<String> callback,
                     int id,
                     String host,
                     int port,
                     String user,
                     String password,
                     String locale){
        super(callback, id);
        this.callback=callback;
        this.id=id;
        this.host=host;
        this.port=port;
        this.user=user;
        this.password=password;
        this.locale=locale;
    }

    @Override
    protected String doInBackground(String... strings) {

        HttpClient Client = new DefaultHttpClient();
        try
        {
            HttpGet httpget = new HttpGet(host+":"+port+"/app/dispatch/api/login?u="+user+"&p="+password+"&l="+locale);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            return Client.execute(httpget, responseHandler);
        }catch(HttpResponseException ex){
            errMsg="Wrong login or password";
            return null;
        }catch(Exception ex){
            errMsg="Something went wrong :(";
            return null;
        }
    }

}
