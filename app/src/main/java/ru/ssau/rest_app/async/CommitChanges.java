package ru.ssau.rest_app.async;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import ru.ssau.rest_app.async.interfaces.IAsyncTaskCallback;
import ru.ssau.rest_app.data.LinkedItem;
import ru.ssau.rest_app.data.TimeEntry;
import ru.ssau.rest_app.data.WeekEntry;

/**
 * Created by Дмитрий on 23.12.2016.
 */
public class CommitChanges extends AbstractCallbackTask {
    private LinkedItem project;
    private LinkedItem task;
    private LinkedItem activityType;
    private String[] time;
    private WeekEntry oldVals;

    private String host;
    private int port;
    private String token;
    private String login;
    public CommitChanges(IAsyncTaskCallback<String> callback, int id) {
        super(callback, id);
    }

    public CommitChanges(IAsyncTaskCallback<String> callback,
                         int id, LinkedItem project, LinkedItem task, LinkedItem activityType,
                         String[] time, WeekEntry oldVals,
                         String host, int port, String token,String login) {
        super(callback, id);
        this.project = project;
        this.task = task;
        this.activityType = activityType;
        this.time = time;
        this.oldVals = oldVals;
        this.host = host;
        this.port = port;
        this.token = token;
        this.login=login;
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpClient Client = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(host+":"+port+"/app/dispatch/api/query.json?e=ts$ExtUser&q=select+a+from+ts$ExtUser+a+where+a.login='"+login+"'&s="+token);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String user;
        try {
            user=Client.execute(httpget, responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
            user="[]";
        }
        JSONObject userJSON=null;
        try {
            JSONArray userArr=new JSONArray(user);
            if(userArr.length()==0){
                errMsg="cant get user id";
                return null;
            }
           userJSON=userArr.getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        HttpClient httpclient = new DefaultHttpClient();
        HttpPost http = new HttpPost(host+":"+port+"/app/dispatch/api/commit?s="+token);
        http.setHeader("Content-type", "application/json");
        JSONObject entities=new JSONObject();
        TimeEntry[] durs=oldVals.getDurations();
        JSONArray removeEntities=new JSONArray();
        if(time==null){            //deleting
            try {
                for(TimeEntry entry:durs) {
                    if (entry != null) {
                        JSONObject toRemove = new JSONObject();
                        toRemove.put("id", entry.getId());
                        fillTaskAndActivityType(toRemove);
                        toRemove.put("user", userJSON);
                        toRemove.put("date", entry.getDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
                        removeEntities.put(toRemove);
                    }
                }
                entities.put("removeInstances",removeEntities);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            try{

                JSONArray commitEntities=new JSONArray();
                for(int i=0;i<7;i++){
                    TimeEntry old=oldVals.getDurations()[i];
                    String t=time[i];
                    String[] splited=t.split(":");
                    int durationInMins=Integer.valueOf(splited[0])*60+Integer.valueOf(splited[1]);
                    if(old !=null && durationInMins==0 && old.getDurationInMinutes()!=0){
                        //deleting
                        JSONObject toRemove=new JSONObject();
                        toRemove.put("id",old.getId());
                        fillTaskAndActivityType(toRemove);
                        toRemove.put("user",userJSON);
                        toRemove.put("date",old.getDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
                        removeEntities.put(toRemove);
                    }else if(old==null && durationInMins>0){
                        //creating
                        JSONObject toCreate=new JSONObject();
                        toCreate.put("id","NEW-ts$TimeEntry");
                        fillTaskAndActivityType(toCreate);
                        toCreate.put("user",userJSON);
                        DateTime time= new DateTime(oldVals.getStartDate()+i*24*3600000);
                        toCreate.put("date",time.toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
                        toCreate.put("timeInMinutes",durationInMins);
                        commitEntities.put(toCreate);
                    }else if(old!=null && (durationInMins!=old.getDurationInMinutes()
                            || !old.getTaskId().equals(task.getId())
                            || !old.getProjectId().equals(project.getId())
                            || !old.getTaskTypeId().equals(activityType.getId()))){
                        //update
                        JSONObject toUpdate=new JSONObject();
                        toUpdate.put("id",old.getId());
                        fillTaskAndActivityType(toUpdate);
                        toUpdate.put("user",userJSON);
                        toUpdate.put("date",old.getDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd")));
                        toUpdate.put("timeInMinutes",durationInMins);
                        commitEntities.put(toUpdate);
                    }
                }
                entities.put("removeInstances",removeEntities);
                entities.put("commitInstances",commitEntities);
            }catch (JSONException e) {
                e.printStackTrace();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        try {
            http.setEntity(new StringEntity(entities.toString()));
            String response = (String) httpclient.execute(http, new BasicResponseHandler());
            return response;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            errMsg="unsupported encoding";
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            errMsg="client protocol exception";
        } catch (IOException e) {
            e.printStackTrace();
            errMsg="IO exception";
        }
        return null;
    }

    private void fillTaskAndActivityType(JSONObject toCreate) throws JSONException {
        JSONObject task=new JSONObject();
        task.put("id",this.task.getId());
        JSONObject proj=new JSONObject();
        proj.put("id",this.project.getId());
        task.put("project",proj);
        JSONObject actType=new JSONObject();
        actType.put("id",this.activityType.getId());
        toCreate.put("task",task);
        toCreate.put("activityType",actType);
    }
}
