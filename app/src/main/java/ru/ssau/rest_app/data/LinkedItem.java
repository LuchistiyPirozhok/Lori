package ru.ssau.rest_app.data;

/**
 * Created by Дмитрий on 23.12.2016.
 */
public class LinkedItem {
    private String id;
    private String name;
    private String linkedId;
    private String linkedName;

    public LinkedItem(String id, String name, String linkedId, String linkedName) {
        this.id = id;
        this.name = name;
        this.linkedId = linkedId;
        this.linkedName = linkedName;
    }
    public LinkedItem(String id, String name) {
       this(id,name,null,null);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLinkedId() {
        return linkedId;
    }

    public void setLinkedId(String linkedId) {
        this.linkedId = linkedId;
    }

    public String getLinkedName() {
        return linkedName;
    }

    public void setLinkedName(String linkedName) {
        this.linkedName = linkedName;
    }
}
