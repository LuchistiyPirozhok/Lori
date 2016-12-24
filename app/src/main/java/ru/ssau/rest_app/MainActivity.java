package ru.ssau.rest_app;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.MutableDateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ru.ssau.rest_app.adapters.WeekMapAdapter;
import ru.ssau.rest_app.async.LoginTask;
import ru.ssau.rest_app.async.QueryTask;
import ru.ssau.rest_app.async.interfaces.IAsyncTaskCallback;
import ru.ssau.rest_app.data.TimeEntry;
import ru.ssau.rest_app.data.WeekEntry;
import ru.ssau.rest_app.data.WeekKey;
import ru.ssau.rest_app.data.WeekListModel;


public class MainActivity extends AppCompatActivity implements IAsyncTaskCallback<String>{
    private static final int LOGIN_TASK_ID=1;
    private static final int QUERY_TASK_ID=2;
    private String sessionToken=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_refresh:
                login();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, PrefActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_add:
                DateTime time=DateTime.now();
                final Context ctx=this;
                new DatePickerDialog(ctx, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                        DateTime t=new DateTime( y,m+1,d,0,0);
                        MutableDateTime time =t.toMutableDateTime();
                        time.setTime(0,0,0,0);
                        int dow=time.getDayOfWeek();
                        if(dow!=DateTimeConstants.MONDAY){
                            time.setMillis(time.getMillis()-(dow-1)*1000*60*60*24);
                        }
                        long weekstart=time.getMillis();
                        Intent intent = new Intent(ctx, WeekEntryActivity.class);
                        WeekEntry entry=new WeekEntry(new WeekKey("","",""),"","","",weekstart);
                        intent.putExtra(WeekEntryActivity.WEEK_ENTRY,entry);
                        ctx.startActivity(intent);
                    }
                },time.getYear(),time.getMonthOfYear()-1,time.getDayOfMonth()).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void login() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String host=sp.getString("host","");
        int port=Integer.valueOf(sp.getString("port","-1"));
        String login=sp.getString("login","");
        String pass=sp.getString("password","");
        if(!host.equals("") && port!=-1 && !login.equals("") && !pass.equals("")) {
            new LoginTask(this,
                    LOGIN_TASK_ID,host,port,login,pass,"ru").execute();
        }else{
            Toast.makeText(this,"Check settings",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onTaskComplete(int id, String result,String errMsg) {
        if(result==null){
            Toast toast = Toast.makeText(getApplicationContext(), errMsg, Toast.LENGTH_LONG);
            toast.show();
        }else{
            switch(id){
                case LOGIN_TASK_ID:

                    sessionToken = result;
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                    refresh(sp);
                    sp.edit().putString("token",result).commit();
                    break;
                case QUERY_TASK_ID:
                    try {
                        JSONArray list = new JSONArray(result);
                        int length=list.length();
                        Map<Long,WeekListModel> weekMap=new TreeMap<>(new Comparator<Long>(){
                            @Override
                            public int compare(Long a1, Long a2) {
                                long la1=a1.longValue();
                                long la2=a2.longValue();
                                return la1==la2 ? 0 : (la1>la2 ? 1:-1);
                            }
                        });
                        for (int i=0;i<length;i++){
                            TimeEntry entry=new TimeEntry();
                            JSONObject elem=list.getJSONObject(i);
                            entry.setId(elem.getString("id"));
                            entry.setDescription(elem.getString("description"));
                            entry.setDate(DateTime.parse(elem.getString("date")));
                            entry.setDurationInMinutes(Integer.parseInt(elem.getString("timeInMinutes")));
                            JSONObject task=elem.getJSONObject("task");
                            if(task!=null) {
                                entry.setTaskId(task.getString("id"));
                                entry.setTaskName(task.getString("name"));
                                JSONObject project = task.getJSONObject("project");
                                if(project!=null) {
                                    entry.setProjectId(project.getString("id"));
                                    entry.setProjectName(project.getString("name"));
                                }
                            }
                            JSONObject activityType=elem.getJSONObject("activityType");
                            if(activityType!=null){
                                entry.setTaskTypeId(activityType.getString("id"));
                                entry.setTaskTypeName(activityType.getString("name"));
                            }
                            MutableDateTime time = entry.getDate().toMutableDateTime();
                            time.setTime(0,0,0,0);
                            int dow=time.getDayOfWeek();
                            if(dow!=DateTimeConstants.MONDAY){
                                time.setMillis(time.getMillis()-(dow-1)*1000*60*60*24);
                            }
                            long weekstart=time.getMillis();
                            if(weekMap.containsKey(weekstart)){
                                weekMap.get(weekstart).applyTimeEntry(entry);
                            }else{
                                WeekListModel wlm=new WeekListModel(weekstart);
                                wlm.applyTimeEntry(entry);
                                weekMap.put(weekstart,wlm);
                            }
                        }
                        WeekMapAdapter adapter=new WeekMapAdapter(weekMap,this);
                        ((ListView)findViewById(R.id.main_list_view)).setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void refresh(SharedPreferences sp) {
        String host=sp.getString("host","");
        int port=Integer.valueOf(sp.getString("port","-1"));
        String login=sp.getString("login","");
        new QueryTask(this,
                QUERY_TASK_ID, host, port, login, sessionToken).execute();
    }
    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(this);
        boolean refresh=sp.getBoolean("refresh",false);
        if(refresh){
            sp.edit().putBoolean("refresh",false).commit();
            refresh(sp);
        }
    }

}
