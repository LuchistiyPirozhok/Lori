package ru.ssau.rest_app.data;

import java.io.Serializable;

/**
 * Created by Дмитрий on 19.12.2016.
 */
public class WeekEntry implements Serializable{
    private long startDate;

    private WeekKey key;
    private String projectName;
    private String taskName;
    private String taskTypeName;
    private TimeEntry[] timeEntries=new TimeEntry[7];

    public WeekEntry(WeekKey key,String taskName,String projectName,String taskTypeName,long startDate) {
        this.key=key;
        this.taskName=taskName;
        this.projectName=projectName;
        this.taskTypeName=taskTypeName;
        this.startDate=startDate;
    }
    public long getStartDate() {
        return startDate;
    }

    public void applyTimeEntry(TimeEntry entry){
        timeEntries[entry.getDate().getDayOfWeek()-1]=entry;
    }
    public int getTotalTime(){
        int res=0;
        for(int i=0;i<7;i++){
            if(timeEntries[i]!=null) {
                res += timeEntries[i].getDurationInMinutes();
            }
        }
        return res;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public TimeEntry[] getDurations() {
        return timeEntries;
    }

    public WeekKey getKey() {
        return key;
    }

    public void setKey(WeekKey key) {
        this.key = key;
    }

    public String getTaskTypeName() {
        return taskTypeName;
    }

    public void setTaskTypeName(String taskTypeName) {
        this.taskTypeName = taskTypeName;
    }
}
