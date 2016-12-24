package ru.ssau.rest_app.data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by Дмитрий on 18.12.2016.
 */
public class WeekListModel implements Serializable {

    private long weekStartDate;
    private List<WeekEntry> entries =new ArrayList<>();

    public WeekListModel(long weekStartDate){
        this.weekStartDate=weekStartDate;
    }

    public void applyTimeEntry(TimeEntry entry){
        WeekKey key=entry.getWeekKey();
        WeekEntry selected=null;
        for(WeekEntry weekEntry: entries){
            if(weekEntry.getKey().equals(key)){
                selected=weekEntry;
                break;
            }
        }
        if(selected==null) {
            selected = new WeekEntry(key, entry.getTaskName(), entry.getProjectName(),entry.getTaskTypeName(),weekStartDate);
            entries.add(selected);
        }
        selected.applyTimeEntry(entry);
    }

    public void clear(){
        entries.clear();
    }

    public long getWeekStartDate() {
        return weekStartDate;
    }

    public void setWeekStartDate(long weekStartDate) {
        this.weekStartDate = weekStartDate;
    }
    public List<WeekEntry> getEntries(){
       return entries;
    }
}
