package com.stefanbuehlmann.entriesvm.viewmodel;

import com.stefanbuehlmann.entriesvm.model.impl.Entry;
import com.stefanbuehlmann.entriesvm.model.intf.EntryI;
import com.stefanbuehlmann.entriesvm.service.intf.EntryServiceI;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * A view model for presenting detail items.
 */
public class EntryListViewModel extends ViewModel implements PropertyChangeListener {
    // TODO recycle unused EntryViewModels ?

    public static final String ENTRY_AT_CHANGED = "changed_entry_at_EntryListViewModel";
    public static final String ENTRY_AT_DELETED = "deleted_entry_at_EntryListViewModel";
    public static final String ENTRY_AT_SELECTED = "selected_entry_at_EntryListViewModel";

    private ArrayList<EntryViewModel> list = null;
    private EntryServiceI entryService;
    Random randomGenerator = new Random();

    // selectedEntry is purely a convenience store for index, it has no other function,
    // except, that there is a change communicated when changed
    private int selectedEntry = EntryServiceI.NO_ID;

    public int getSelectedEntry() {
        return selectedEntry;
    }

    public void setSelectedEntry(int selectedEntry) {
        this.selectedEntry = selectedEntry;
        this.firePropertyChange(ENTRY_AT_SELECTED, -1, selectedEntry);
    }

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
        clearList();
        wrapList(l);
    }

    public void init() { // TODO ich glaube das wird niergends gebraucht // no usage in Android. What about iOS?
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
    
    public void delete (int index) {
        EntryViewModel entryVM = list.remove(index); // TODO führte zu IndexOutOfBoundException !
        if (entryVM != null) {
            entryVM.removePropertyChangeListener(this);
            entryVM.delete();
            // TODO at this point I could queue the VM for later re-use
        }
        this.firePropertyChange(ENTRY_AT_DELETED, -1, index);
    }
    
    public void move(int fromIndex, int toIndex) { // no usage in Android. What about iOS?
        // TODO propagate to DB and fire change
        // currently, the data model does not store any ordering information
        // only iOS interface does...
        if (fromIndex == toIndex) {
            return;
        }
        EntryViewModel entry = list.remove(fromIndex);
        list.add(toIndex, entry);
    }
    
    public void update(EntryViewModel entry, int really_needed) { // no usage in Android. What about iOS?
        // TODO propagate to DB  and fire change
        // use existing code in EntryViewModel !!!
    }
    
    public EntryViewModel find(int index) {
        if (index<0 || index >= list.size()) {
            return null;
        } else {
            return list.get(index);
        }
    }

    public int indexOf(EntryViewModel entry) {
        return list.indexOf(entry);
    }

    public int indexOf(long entryId) {
        int result;
        int count = count();
        for (result = 0; result < count; result++) {
            if (list.get(result).getId() == entryId) {
                return result;
            }
        }
        return EntryServiceI.NO_ID;
    }

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
            this.firePropertyChange(ENTRY_AT_CHANGED, -1, idx);
        }
    }

    public void randomChange() {
        for (EntryViewModel entryVM: list) {
            String name = entryVM.getName();
            if (name.contains("xx")) {
                entryVM.setName(name.replace("xx", "x"));
            }
        }
        int randomIndex = randomGenerator.nextInt(list.size());
        EntryViewModel entryVM = list.get(randomIndex);
        entryVM.setName(entryVM.getName()+"xxx");
    }
}
