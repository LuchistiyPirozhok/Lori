package ru.ssau.rest_app;


import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import ru.ssau.rest_app.adapters.LinkedItemAdapter;
import ru.ssau.rest_app.async.CommitChanges;
import ru.ssau.rest_app.async.QueryTSEntity;
import ru.ssau.rest_app.async.interfaces.IAsyncTaskCallback;
import ru.ssau.rest_app.data.LinkedItem;
import ru.ssau.rest_app.data.TimeEntry;
import ru.ssau.rest_app.data.WeekEntry;

public class WeekEntryActivity extends AppCompatActivity implements IAsyncTaskCallback<String> {
    public static final String WEEK_ENTRY="week_entry";
    public static final String START_DATE = "start_date";

    private static final String PROJECTS_ENTITY_NAME="ts$ProjectParticipant";
    private static final String ACTIVITY_TYPE_NAME="ts$ActivityType";
    private static final String TASK_NAME="ts$Task";

    private static final String PROJECT_VIEW="projectParticipant-full";
    private static final String TASK_VIEW="task-full";

    private static final int PROJECTS_ID=0;
    private static final int TASKS_ID=1;
    private static final int ACTIVITY_TYPE_ID=2;
    private static final int COMMIT_ID=3;


    private WeekEntry entry;
    private TextView[] views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context ctx=this;
        setContentView(R.layout.activity_week_entry);
        entry= (WeekEntry) getIntent().getSerializableExtra(WEEK_ENTRY);
        if(entry!=null){
            TimeEntry[] durs=entry.getDurations();
            views=new TextView[7];
            views[0]=((TextView) findViewById(R.id.monday_val));
            views[1]=((TextView) findViewById(R.id.tuesday_val));
            views[2]= ((TextView) findViewById(R.id.wedensday_val));
            views[3]=((TextView) findViewById(R.id.thursday_val));
            views[4]= ((TextView) findViewById(R.id.friday_val));
            views[5]=((TextView) findViewById(R.id.saturday_val));
            views[6]=((TextView) findViewById(R.id.sunday_val));

            views[0].setText(durs[0]!=null ? getPrettyDuration( durs[0]) :"0:00");
            views[1].setText(durs[1]!=null ? getPrettyDuration( durs[1]) :"0:00");
            views[2].setText(durs[2]!=null ? getPrettyDuration( durs[2]) :"0:00");
            views[3].setText(durs[3]!=null ? getPrettyDuration( durs[3]) :"0:00");
            views[4].setText(durs[4]!=null ? getPrettyDuration( durs[4]) :"0:00");
            views[5].setText(durs[5]!=null ? getPrettyDuration( durs[5]) :"0:00");
            views[6].setText(durs[6]!=null ? getPrettyDuration( durs[6]) :"0:00");

            for(TextView v:views){
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final View v=view;
                        String txt=((TextView)v).getText().toString();
                        String[] splited=txt.split(":");
                        int h=Integer.valueOf(splited[0]);
                        int m=Integer.valueOf(splited[1]);
                        new TimePickerDialog(ctx, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int h, int m) {
                                ((TextView)v).setText(h+":"+(m<10 ? "0":"")+m);
                            }
                        }, h, m, true).show();
                    }
                });
            }
        }
        final Spinner prjSpinner=(Spinner) findViewById(R.id.week_entry_project_value);
        final LinkedItemAdapter prjAdapter=new LinkedItemAdapter(this,new ArrayList<LinkedItem>());
        prjSpinner.setAdapter(prjAdapter);

        final Spinner taskSpinner=(Spinner) findViewById(R.id.week_entry_task_value);
        final LinkedItemAdapter taskAdapter=new LinkedItemAdapter(this,new ArrayList<LinkedItem>());
        taskSpinner.setAdapter(taskAdapter);

        final Spinner actSpinner=(Spinner) findViewById(R.id.week_entry_task_type_value);
        final LinkedItemAdapter actAdapter=new LinkedItemAdapter(this,new ArrayList<LinkedItem>());
        actSpinner.setAdapter(actAdapter);

        prjSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                LinkedItem li=prjAdapter.getLinkedItem(i);
                taskAdapter.getFilter().filter(li.getId());
                taskSpinner.post(new Runnable() {
                    @Override
                    public void run() {
                        if(entry!=null) {
                            int idx = taskAdapter.getIndexById(entry.getKey().getTaskId());
                            if (idx > 0)
                                taskSpinner.setSelection(idx, true);
                        }
                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

       final Button save =(Button) findViewById(R.id.week_entry_save_button);
       final Button del =(Button) findViewById(R.id.week_entry_delte_button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                del.setOnClickListener(null);
                LinkedItem proj= (LinkedItem) prjSpinner.getSelectedItem();
                LinkedItem task= (LinkedItem) taskSpinner.getSelectedItem();
                LinkedItem actType=(LinkedItem) actSpinner.getSelectedItem();
                SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(ctx);
                String host=sp.getString("host","");
                int port=Integer.valueOf(sp.getString("port","-1"));
                String token=sp.getString("token","");
                String login=sp.getString("login","");
                if(proj!=null && task!=null && actType!=null
                        && !host.equals("") && port!=-1 && !token.equals("") && !login.equals("")) {
                    String[] vals = new String[7];
                    for (int i=0;i<7;i++){
                        vals[i]=views[i].getText().toString();
                    }
                    new CommitChanges((IAsyncTaskCallback<String>) ctx, COMMIT_ID,proj,task,actType,
                            vals,entry,host,port,token,login).execute();
                }else{
                    Toast.makeText(ctx,"All properties must be selected!",Toast.LENGTH_LONG);
                }
            }
        });


        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save.setOnClickListener(null);
                LinkedItem proj= (LinkedItem) prjSpinner.getSelectedItem();
                LinkedItem task= (LinkedItem) taskSpinner.getSelectedItem();
                LinkedItem actType=(LinkedItem) actSpinner.getSelectedItem();
                SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(ctx);
                String host=sp.getString("host","");
                int port=Integer.valueOf(sp.getString("port","-1"));
                String token=sp.getString("token","");
                String login=sp.getString("login","");
                if(proj!=null && task!=null && actType!=null
                        && !host.equals("") && port!=-1 && !token.equals("") && !login.equals("")) {
                    new CommitChanges((IAsyncTaskCallback<String>) ctx, COMMIT_ID,proj,task,actType,
                            null,entry,host,port,token,login).execute();
                }else{
                    Toast.makeText(ctx,"All properties must be selected!",Toast.LENGTH_LONG);
                }
            }
        });


        pullLists();


    }

    private void pullLists() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String host=sp.getString("host","");
        int port=Integer.valueOf(sp.getString("port","-1"));
        String token=sp.getString("token","");
        if(!host.equals("") && port!=-1 && !token.equals("")) {
            new QueryTSEntity(this,PROJECTS_ID,token,host,port,PROJECTS_ENTITY_NAME,PROJECT_VIEW).execute();
            new QueryTSEntity(this,ACTIVITY_TYPE_ID,token,host,port,ACTIVITY_TYPE_NAME,null).execute();
            new QueryTSEntity(this,TASKS_ID,token,host,port,TASK_NAME,TASK_VIEW).execute();
        }else{
            Toast.makeText(this,"Check settings",Toast.LENGTH_SHORT).show();
        }
    }

    private String getPrettyDuration(TimeEntry entry){
        int durInMins=entry.getDurationInMinutes();
        int h=durInMins/60;
        int m=durInMins%60;
        return h+":"+((m<10) ? "0":"")+m;
    }

    @Override
    public void onTaskComplete(int id, String result, String errMsg) {
        if(result==null){
            Toast toast = Toast.makeText(getApplicationContext(), errMsg, Toast.LENGTH_LONG);
            toast.show();
        }else {
            switch (id) {
                case PROJECTS_ID:
                    try {
                        JSONArray list = new JSONArray(result);
                        int length = list.length();
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                        String login = sp.getString("login", "");
                        final Spinner prjSpinner = (Spinner) findViewById(R.id.week_entry_project_value);
                        final LinkedItemAdapter adapter = (LinkedItemAdapter) prjSpinner.getAdapter();
                        for (int i = 0; i < length; i++) {
                            JSONObject entity = list.getJSONObject(i);
                            if (entity.getJSONObject("user").getString("loginLowerCase").equals(login.toLowerCase())) {
                                JSONObject prj = entity.getJSONObject("project");
                                LinkedItem li = new LinkedItem(
                                        prj.getString("id"),
                                        prj.getString("name")
                                );
                                adapter.addLinkedItem(li);
                            }
                        }
                        adapter.getFilter().filter(null);
                        if(entry!=null){
                            prjSpinner.post(new Runnable() {
                                @Override
                                public void run() {
                                    int idx=adapter.getIndexById(entry.getKey().getProjectId());
                                    if(idx>=0)
                                        prjSpinner.setSelection(idx);
                                }
                            });

                        }

                    } catch (JSONException e) {
                        Toast.makeText(this, "Wrong response format :O", Toast.LENGTH_SHORT);
                        e.printStackTrace();
                    }
                    break;
                case TASKS_ID:
                    try {
                        JSONArray list = new JSONArray(result);
                        int length = list.length();
                        Spinner prjSpinner = (Spinner) findViewById(R.id.week_entry_project_value);
                        final Spinner taskSpinner=(Spinner) findViewById(R.id.week_entry_task_value);
                        final LinkedItemAdapter adapter= (LinkedItemAdapter) taskSpinner.getAdapter();

                        for(int i=0;i<length;i++){
                            JSONObject entity=list.getJSONObject(i);
                            JSONObject project=entity.getJSONObject("project");
                            LinkedItem li=new LinkedItem(
                                    entity.getString("id"),
                                    entity.getString("name"),
                                    project.getString("id"),
                                    project.getString("name"));
                            adapter.addLinkedItem(li);
                        }
                        LinkedItem selected=((LinkedItem)prjSpinner.getSelectedItem());
                        if(selected!=null)
                            adapter.getFilter().filter(selected.getId());
                        if(entry!=null){
                           taskSpinner.post(new Runnable() {
                                @Override
                                public void run() {
                                    int idx=adapter.getIndexById(entry.getKey().getTaskId());
                                    if(idx>=0)
                                        taskSpinner.setSelection(idx,true);
                                }
                            });

                        }

                    } catch (JSONException e) {
                        Toast.makeText(this, "Wrong response format :O", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    break;
                case ACTIVITY_TYPE_ID:
                    try {
                        JSONArray list = new JSONArray(result);
                        int length = list.length();
                        final Spinner actSpinner=(Spinner) findViewById(R.id.week_entry_task_type_value);
                        final LinkedItemAdapter adapter=(LinkedItemAdapter) actSpinner.getAdapter();
                        for(int i=0;i<length;i++){
                            JSONObject entity=list.getJSONObject(i);
                            LinkedItem li=new LinkedItem(
                                    entity.getString("id"),
                                    entity.getString("name"));
                            adapter.addLinkedItem(li);
                        }
                        adapter.getFilter().filter(null);
                        if(entry!=null){
                           actSpinner.post(new Runnable() {
                               @Override
                               public void run() {
                                   int idx=adapter.getIndexById(entry.getKey().getTaskTypeId());
                                   if(idx>=0)
                                    actSpinner.setSelection(idx);
                               }
                           });
                        }

                    } catch (JSONException e) {
                        Toast.makeText(this, "Wrong response format :O", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    break;
                case COMMIT_ID:
                    SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(this);
                    sp.edit().putBoolean("refresh",true).commit();
                    this.finish();
                    break;
                default:
                    break;
            }
        }
    }
}
