
/* the interface of the read-only aspects of an Entry */

package com.stefanbuehlmann.entriesvm.model.intf;

public interface EntryROI {
    public long getId();
    public String getName();
    public String getDescription();
}
  