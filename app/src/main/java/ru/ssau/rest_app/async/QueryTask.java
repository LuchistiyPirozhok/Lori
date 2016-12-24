package ru.ssau.rest_app.async;

import android.icu.util.Calendar;
import android.icu.util.TimeZone;

import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ru.ssau.rest_app.async.interfaces.IAsyncTaskCallback;

/**
 * Created by Дмитрий on 18.12.2016.
 */
public class QueryTask extends AbstractCallbackTask {
    private String token;
    private String host;
    private int port;
    private String user;


    public QueryTask(IAsyncTaskCallback<String> callback,
                     int id,
                     String host,
                     int port,
                     String user,
                     String token) {
        super(callback, id);
        this.host=host;
        this.port=port;
        this.token = token;
        this.user=user;
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpClient Client = new DefaultHttpClient();
        try
        {
            HttpGet httpget = new HttpGet(host+":"+port+"/app/dispatch/api/query.json?e=ts$TimeEntry&q=select+a+from+ts$TimeEntry+a+where+a.createdBy='"+
                    user+"'&s="+token+"&view=timeEntry-browse");
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String res=Client.execute(httpget, responseHandler);
            return res;
        }catch(HttpResponseException ex){
            errMsg="Wrong session token";
            return null;
        }catch(Exception ex){
            errMsg="Something went wrong :(";
            return null;
        }
    }
}
