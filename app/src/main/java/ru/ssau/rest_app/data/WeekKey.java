package ru.ssau.rest_app.data;

import java.io.Serializable;

/**
 * Created by Ольга on 19.12.2016.
 */
public class WeekKey implements Serializable {
    private String taskId;
    private String projectId;
    private String taskTypeId;

    public WeekKey(String taskId, String projectId,String taskTypeId) {
        this.taskId = taskId;
        this.projectId = projectId;
        this.taskTypeId=taskTypeId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getTaskTypeId() {
        return taskTypeId;
    }

    public void setTaskTypeId(String taskTypeId) {
        this.taskTypeId = taskTypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WeekKey weekKey = (WeekKey) o;

        if (taskId != null ? !taskId.equals(weekKey.taskId) : weekKey.taskId != null) return false;
        if (projectId != null ? !projectId.equals(weekKey.projectId) : weekKey.projectId != null)
            return false;
        return taskTypeId != null ? taskTypeId.equals(weekKey.taskTypeId) : weekKey.taskTypeId == null;

    }

    @Override
    public int hashCode() {
        int result = taskId != null ? taskId.hashCode() : 0;
        result = 31 * result + (projectId != null ? projectId.hashCode() : 0);
        result = 31 * result + (taskTypeId != null ? taskTypeId.hashCode() : 0);
        return result;
    }
}
