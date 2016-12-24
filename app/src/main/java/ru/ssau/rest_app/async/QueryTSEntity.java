package ru.ssau.rest_app.async;

import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import ru.ssau.rest_app.async.interfaces.IAsyncTaskCallback;

/**
 * Created by Дмитрий on 23.12.2016.
 */
public class QueryTSEntity extends AbstractCallbackTask{
    private String token;
    private String host;
    private int port;
    private String entityName;
    private String view;

    public QueryTSEntity(IAsyncTaskCallback<String> callback, int id,
                       String token, String host,int port,String entityName,String view) {
        super(callback, id);
        this.token=token;
        this.host=host;
        this.port=port;
        this.entityName=entityName;
        this.view=view;
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpClient Client = new DefaultHttpClient();
        try
        {
            HttpGet httpget = new HttpGet(host+":"+port+"/app/dispatch/api/query.json?e="+entityName+"&q=select+a+from+"+entityName+"+a&s="+token+(view==null ? "":"&view="+view));
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
