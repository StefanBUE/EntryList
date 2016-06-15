
package com.stefanbuehlmann.entriesvm.service.intf;

import java.beans.PropertyChangeListener;
import java.util.List;

import com.stefanbuehlmann.entriesvm.model.intf.EntryROI;
import com.stefanbuehlmann.entriesvm.model.intf.EntryI;

public interface EntryServiceI {
    public int NO_ID = -1;

    public List<EntryI> selectWhere(String whereClause);
    
    public void delete(EntryI entry);
    
    public void insertOrUpdate(EntryI entry); // it was an insert, if entrie's id changed

}

