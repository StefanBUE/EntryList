package com.stefanbuehlmann.entrylist_android;

import java.util.UUID;

public class Crime {

    private UUID mId;
    private String mTitle;
    private String mDescription;

    public Crime() {
        this(UUID.randomUUID());
    }

    public Crime(UUID id) {
        mId = id;
    }
    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) { mDescription = description; }

}
