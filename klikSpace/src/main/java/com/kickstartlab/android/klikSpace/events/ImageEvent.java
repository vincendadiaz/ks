package com.kickstartlab.android.klikSpace.events;

/**
 * Created by awidarto on 3/19/15.
 */
public class ImageEvent {
    private String action = "refresh";

    private String entityId;
    private String entityType;

    public ImageEvent(String action, String entityId, String entityType ){
        this.action = action;
        this.entityId = entityId;
        this.entityType = entityType;
    }

    public ImageEvent(String action){
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
}
