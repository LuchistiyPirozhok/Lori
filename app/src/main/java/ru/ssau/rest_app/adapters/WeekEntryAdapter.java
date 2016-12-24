package ru.ssau.rest_app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ru.ssau.rest_app.R;
import ru.ssau.rest_app.WeekEntryActivity;
import ru.ssau.rest_app.data.WeekEntry;

/**
 * Created by Ольга on 19.12.2016.
 */
public class WeekEntryAdapter extends BaseAdapter {
    private Context ctx;
    private List<WeekEntry> entries;
    private LayoutInflater lInflater;
    public WeekEntryAdapter(Context ctx, List<WeekEntry> entries) {
        this.ctx=ctx;
        this.entries=entries;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return entries.size();
    }
    public WeekEntry getEntry(int i){
        return (WeekEntry) getItem(i);
    }

    @Override
    public Object getItem(int i) {
        return entries.get(i);
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
            view = lInflater.inflate(R.layout.list_item, parent, false);
        }

        final WeekEntry entry=getEntry(position);
        ((TextView)view.findViewById(R.id.item_task)).setText(entry.getTaskName()+(entry.getTaskTypeName()==null ? "":"("+entry.getTaskTypeName()+")"));
        ((TextView)view.findViewById(R.id.item_project)).setText(entry.getProjectName());
        int durInMins=entry.getTotalTime();
        int h=durInMins/60;
        int m=durInMins%60;
        ((TextView)view.findViewById(R.id.item_time)).setText(h+"h "+m+"m");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, WeekEntryActivity.class);
                intent.putExtra(WeekEntryActivity.WEEK_ENTRY,entry);
            }
        });
        parent.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        ((ViewGroup) view).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        return view;
    }


}
