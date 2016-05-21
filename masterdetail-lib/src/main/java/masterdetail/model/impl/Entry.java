
/* the implementation of an Entry, please only publish interfaces of it, ROI interface if possible */

package masterdetail.model.impl;

import masterdetail.model.intf.EntryI;

public class Entry implements EntryI {
    
    private long id=-2;
    private String name="";
    private String description="";

    public long getId() { return id; };
    public void setId(long id) { this.id = id; };
    public String getName() { return name; };
    public void setName(String name) { this.name = name; } ;
    public String getDescription() { return description; };
    public void setDescription(String description) { this.description = description; } ;
    
}
