
/* the full interface of an Entry, including read/write-aspects */

package com.stefanbuehlmann.entriesvm.model.intf;

public interface EntryI extends EntryROI{   
    public void setId(long id);
    public void setName(String name);
    public void setDescription(String description);
}
