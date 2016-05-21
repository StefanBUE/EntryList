
package masterdetail.service.impl;

import masterdetail.service.intf.EntryServiceI;
import masterdetail.viewmodel.EntryViewModel;
import masterdetail.model.intf.EntryROI;
import masterdetail.model.intf.EntryI;
import masterdetail.model.impl.Entry;

import com.vals.a2ios.sqlighter.intf.Singleton;
import com.vals.a2ios.sqlighter.intf.SQLighterDb;
import com.vals.a2ios.sqlighter.intf.SQLighterRs;

import java.io.File;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.ArrayList;

public class DbEntryService implements EntryServiceI {
    
    public DbEntryService() {
    }
    
    /************* interface EntryServiceI *************/
    public List<EntryI> selectWhere(String whereClause) {
        
        ArrayList<EntryI> list = new ArrayList<EntryI>();
        
        SQLighterDb db = Singleton.getInstance().getSqLighterDb();
        
        System.out.println("selecting all records from entry");
        SQLighterRs rs;
        try {
            rs = db.executeSelect("select id, name, description from entry");
        } catch (Exception e) {
            System.out.println("Exception when selecting all records from entry: " + e);
            return null;
        }
        while (rs.hasNext()) {
            Long pk = rs.getLong(0);
            String name = rs.getString(1);
            String description = rs.getString(2);
            System.out.println("pk: " + pk + ", name: " + name + ", description: " + description);
            
            Entry e = new Entry();
            e.setId(pk);
            e.setName(name);
            e.setDescription(description);
            
            list.add(e);
        }
        rs.close();
        // TODO fire change
        // TODO memory clean up
        return list;
    }
    
    @Override
    public void delete (long id) {
        SQLighterDb db = Singleton.getInstance().getSqLighterDb();
        System.out.println("deleting entry where id = "+id);
        db.addParam(id); // delete records where id == 'if'
        try {
            db.executeChange("delete from entry where id = ?");
        } catch (Exception e) {
            System.out.println("Exception when attempting to delete entry with id " + id + ": " + e);
        }
    }

    @Override
    public void insertOrUpdate (EntryI entry) {
        long id = entry.getId();
        SQLighterDb db = Singleton.getInstance().getSqLighterDb();
        long rowId=0;
        if (id == EntryServiceI.NO_ID) {
            // insert it, its new
            db.addParam(entry.getName());
            db.addParam(entry.getDescription());
            try {
                rowId = db.executeChange("insert into entry(name, description) values (?, ?)");
            } catch (Exception e) {
                System.out.println("Exception when attempting to insert new entry with name '" + entry.getName()
                                   + "' and description '"+ entry.getDescription()+"': " + e);
            }
            entry.setId(rowId);
            System.out.println("inserted new entry with id = "+rowId);
        } else {
            // update it
            System.out.println("updating entry where id = "+id);
            db.addParam(entry.getName());
            db.addParam(entry.getDescription());
            db.addParam(id);
            try {
                db.executeChange("update entry set name = ?, description=? where id = ?");
            } catch (Exception e) {
                System.out.println("Exception when attempting to update entry with id " + id + ": " + e);
            }
        }
    }
}
