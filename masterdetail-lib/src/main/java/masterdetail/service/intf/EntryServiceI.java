
package masterdetail.service.intf;

import java.beans.PropertyChangeListener;
import java.util.List;

import masterdetail.model.intf.EntryROI;
import masterdetail.model.intf.EntryI; 

public interface EntryServiceI {
    public long NO_ID = -1;

    public List<EntryI> selectWhere(String whereClause);
    
    public void delete (long id);
    
    public void insertOrUpdate (EntryI entry); // it was an insert, if entrie's id changed
    
/*  // TODO support for reordering not yet implemented
 
    public void move(int fromIndex, int toIndex);

*/
}

