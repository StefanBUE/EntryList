package com.stefanbuehlmann.entriesvm.viewmodel;

import com.stefanbuehlmann.entriesvm.model.impl.Entry;
import com.stefanbuehlmann.entriesvm.model.intf.EntryI;
import com.stefanbuehlmann.entriesvm.service.intf.EntryServiceI;


import java.util.ArrayList;
import java.util.List;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * A view model for presenting detail items.
 */
public class EntryListViewModel extends ViewModel implements PropertyChangeListener {
    // TODO recycle unused EntryViewModels ?
    
    public static final String ENTRY_AT_CHG = "EntryListViewModel_EntryAt_chg";

    private ArrayList<EntryViewModel> list = null; // = new ArrayList<EntryViewModel>();
    private EntryServiceI entryService;
    
    private void clearList() { // free memory of the list and remove all listeners
        if (list!= null) {
            for (EntryViewModel entryVM: list) {
                entryVM.removePropertyChangeListener(this);
                // TODO at this point I could queue the VM for later re-use
            }
            list.clear();
        }
    }
    private void wrapList(List<EntryI> l) { // wrap up each EntryI in a EntryViewModel and attach this as listener
        if (list==null) {
            list = new ArrayList<EntryViewModel>();
        }
        assert list.size()==0;
        for (EntryI entry: l) {
            EntryViewModel entryVM = new EntryViewModel(entryService);
            entryVM.init(entry);
            list.add(entryVM);
            entryVM.addPropertyChangeListener(this);
        }
    }
    
    public EntryListViewModel(EntryServiceI entryService) {
        this.entryService = entryService;
        // load data (currently I load all, no where clause)
        List<EntryI> l = this.entryService.selectWhere("");
        // clear the old list
        clearList();
        // now we must wrap up the elements in a ArrayList<EntryViewModel> list
        wrapList(l);
    }

    public void init() { // TODO ich glaube das wird niergends gebraucht
        System.out.println("hier");
    }

    public EntryViewModel create() {
        EntryI entry = new Entry();
        entry.setId(EntryServiceI.NO_ID);
        EntryViewModel entryViewModel = new EntryViewModel(entryService);
        entryViewModel.init(entry);
        list.add(entryViewModel); // TODO fire change
        entryViewModel.addPropertyChangeListener(this);
        return entryViewModel;
    }
    
    public void delete (int id) {
        // TODO propagate to DB and fire change, unregister ChangeListener???
        EntryViewModel entryVM = list.remove(id);
        if (entryVM != null) {
            entryVM.removePropertyChangeListener(this);
            if (entryVM.getId()!=EntryServiceI.NO_ID) { // noId means, its wasn't inserted yet
                this.entryService.delete(entryVM.getId());
            }
            // TODO at this point I could queue the VM for later re-use
        }
    }
    
    public void move(int fromIndex, int toIndex) {
        // TODO propagate to DB and fire change
        if (fromIndex == toIndex) {
            return;
        }
        EntryViewModel entry = list.remove(fromIndex);
        list.add(toIndex, entry);
    }
    
    // public void delete (DetailEntry detailEntry);
    
    public void update(EntryViewModel entry) {
        // TODO propagate to DB  and fire change
    }
    
    public EntryViewModel find(int id) {
        return list.get(id);
    }
    
    public int indexOf(EntryViewModel entry) {
        return list.indexOf(entry);
    }
    
    // public Boolean exists(int id);
    
    // public List<DetailEntry> findAll();
    
    public int count() {
        return list.size();
    }
    
    public void propertyChange(PropertyChangeEvent event) {
        String chg = event.getPropertyName();
        if (chg!=null && (chg.equals(EntryViewModel.NAME_CHG)
                          || chg.equals(EntryViewModel.DESCRIPTION_CHG)
                          || chg.equals(EntryViewModel.ID_CHG))) {
            EntryViewModel entry = (EntryViewModel)event.getSource();
            int idx = this.indexOf(entry);
            this.firePropertyChange(ENTRY_AT_CHG, -1, idx);
        }
    }
}
