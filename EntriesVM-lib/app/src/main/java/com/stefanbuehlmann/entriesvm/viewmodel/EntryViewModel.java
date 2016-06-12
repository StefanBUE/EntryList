package com.stefanbuehlmann.entriesvm.viewmodel;

import com.stefanbuehlmann.entriesvm.model.intf.EntryROI;
import com.stefanbuehlmann.entriesvm.model.intf.EntryI;
import com.stefanbuehlmann.entriesvm.service.intf.EntryServiceI;

/**
 * A view model for presenting detail items.
 */
public class EntryViewModel extends ViewModel {
    public static final String ID_CHG = "EntryViewModel_Id_chg";
    public static final String NAME_CHG = "EntryViewModel_Name_chg";
    public static final String DESCRIPTION_CHG = "EntryViewModel_Description_chg";
    
    private long id;
    private String name;
    private String description;
    private EntryI entryOutOfSync; // might be out of syn unless 'save'ed again!
    private EntryServiceI entryService;

    public EntryViewModel(EntryServiceI entryService) {
        this.entryService = entryService;
    }
    // load data, possibly overwriting older stuff, reusing view
    public void init(EntryI entry) {
        this.entryOutOfSync = entry;
        setId(entry.getId());
        setName(entry.getName());
        setDescription(entry.getDescription());
    }
    
    // the following setters fire the properties
    public long getId() { return id; };
    private void setId(long id) { // id is r/o, nobody must modify it externally
        long old = this.id;
        this.id = id;
        firePropertyChange(ID_CHG, old, id);
    };
    public String getName() { return name; };
    public void setName(String name) {
        String old = this.name;
        this.name = name;
        firePropertyChange(NAME_CHG, old, name); // TODO optimize change messages, in case name is already equal
    };
    public String getDescription() { return description; };
    public void setDescription(String description) {
        String old = this.description;
        this.description = description;
        firePropertyChange(DESCRIPTION_CHG, old, description);
    };
    
    public void save() {
        this.save(this.name,this.description);
    }
    public void save(String name, String description) {
        // update the values of current id and write it to the service
        this.setName(name);
        this.setDescription(description);
        entryOutOfSync.setName(this.name);
        entryOutOfSync.setDescription(this.description);
        
        this.entryService.insertOrUpdate(entryOutOfSync);
        long newId = entryOutOfSync.getId();
        if (newId != this.id) {
            // it was an insert, adapt the field id
            this.setId(newId);
        }
    }
}
