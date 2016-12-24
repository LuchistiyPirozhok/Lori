package ru.ssau.rest_app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.ssau.rest_app.R;
import ru.ssau.rest_app.WeekEntryActivity;
import ru.ssau.rest_app.data.TimeEntry;
import ru.ssau.rest_app.data.WeekEntry;
import ru.ssau.rest_app.data.WeekListModel;

/**
 * Created by Ольга on 19.12.2016.
 */
public class WeekMapAdapter extends BaseAdapter {
    private Map<Long,WeekListModel> weekMap;
    private Context ctx;
    private LayoutInflater lInflater;

    public WeekMapAdapter(Map<Long, WeekListModel> weekMap, Context ctx) {
        this.weekMap = weekMap;
        this.ctx = ctx;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return weekMap.size();
    }

    @Override
    public Object getItem(int i) {
        List<Long> tmp=new ArrayList<>(weekMap.keySet());
        Long key=tmp.get(i);
        return weekMap.get(key);

    }
    public WeekListModel getModel(int i){
        return (WeekListModel)getItem(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.list_view_elem, parent, false);
        }

        WeekListModel model=getModel(position);
        long weekStart=model.getWeekStartDate();
        long weekEnd=weekStart+1000*60*60*24*6;

        DateTimeFormatter formatter= DateTimeFormat.forPattern("dd.MM.yyyy");
        TextView textView=(TextView)view.findViewById(R.id.list_item_header);
        textView.setText(new DateTime(weekStart).toString(formatter)+"/" +
                new DateTime(weekEnd).toString(formatter));
        LinearLayout list=(LinearLayout)view.findViewById(R.id.list_item_list);
        list.removeAllViews();
        for(final WeekEntry entry : model.getEntries()){
            View innerView= lInflater.inflate(R.layout.list_item, list, false);
            ((TextView)innerView.findViewById(R.id.item_task)).setText(entry.getTaskName()+(entry.getTaskTypeName()==null ? "":"("+entry.getTaskTypeName()+")"));
            ((TextView)innerView.findViewById(R.id.item_project)).setText(entry.getProjectName());
            int durInMins=entry.getTotalTime();
            int h=durInMins/60;
            int m=durInMins%60;
            ((TextView)innerView.findViewById(R.id.item_time)).setText(h+"h "+m+"m");
            Button b = (Button)innerView.findViewById(R.id.edit_list_item_button);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ctx, WeekEntryActivity.class);
                    intent.putExtra(WeekEntryActivity.WEEK_ENTRY,entry);
                    ctx.startActivity(intent);
                }
            });
            list.addView(innerView);
        }
        return view;
    }
}
