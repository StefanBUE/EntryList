package com.stefanbuehlmann.entrylist_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.stefanbuehlmann.entriesvm.viewmodel.EntryListViewModel;
import com.stefanbuehlmann.entriesvm.viewmodel.EntryListViewModelSingleton;
import com.vals.a2ios.sqlighter.impl.SQLighterDbImpl;
import com.vals.a2ios.sqlighter.intf.Singleton;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class EntryListActivity extends SingleFragmentActivity implements PropertyChangeListener {

    @Override
    protected Fragment createFragment() {
        return new EntryListFragment();
    }

    @Override
    protected int getLayoutResId() {
        // here the switch between using the refs.xml files
        // - activity_fragment.xml with fragment_container
        // - activity_twopane.xml  with fragment_container & detail_fragment_container
        return R.layout.activity_masterdetail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Singleton.getInstance().getSqLighterDb()==null) {
            // onCreate gets called multiple times, which is NOK!
            /**
             * SQLite initialization. This portion is
             * platform specific, but common by most part.
             */

            SQLighterDbImpl db = new SQLighterDbImpl();
            String dbPath =
                    this.getApplication().getApplicationContext().getFilesDir()
                            .getParentFile().getPath() + "/databases/";
            db.setDbPath(dbPath);
            db.setDbName("Entries.db");
            if(!db.isDbFileDeployed()) {
                System.out.println("DB file gets deployed now");
            } else {
                System.out.println("DB file was deployed already");
            }
            db.setContext(this);

            /* Will overwrite destination DB file at device.
             * Good for the demo since we have the same starting point
             * and can implement tests. Production app settings most
             * likely would be different.
             */
            // db.setOverwriteDb(true);

            try {
                db.deployDbOnce();
                db.openIfClosed();
            } catch (Throwable t) {
                t.printStackTrace();
            }
            Singleton.getInstance().setSqLighterDb(db);
        }
        // this is one of the restricted calls to this singleton, dont' add more, use entryListVM
        EntryListViewModel entryListVM = EntryListViewModelSingleton.getInstance();
        entryListVM.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        String eventPropertyName = event.getPropertyName();
        if (eventPropertyName.equals(EntryListViewModel.ENTRY_AT_CHANGED)) {
            int position = (Integer)event.getNewValue();
            System.out.println("EntryListActivity received "+eventPropertyName+" with position "+position);
            // the following was in interface EntryFragment.Callbacks with onEntryUpdated
            // EntryFragment.onAttach attaches THIS to the EntryFragment
            // so the corresponding callback gets called here with this IF
            EntryListFragment listFragment = (EntryListFragment)
                    getSupportFragmentManager()
                            .findFragmentById(R.id.fragment_container);
            // TODO I had the case that listFragment was null!!! again!!!
            if (listFragment!=null) { // WHY the f*k?
                listFragment.updateUI();
            }
        } else
        if (eventPropertyName.equals(EntryListViewModel.ENTRY_AT_SELECTED)) {
            // implement(ed) interface EntryListFragment.Callbacks with onEntrySelected
            // corresponds roughly to iOS's MasterViewController.changeSelection, which calls
            // the delegateDetailViewC which implements entrySelected
            // Android does that with the Intents-Datapassing approach
            int position = (Integer)event.getNewValue();
            System.out.println("EntryListActivity received "+eventPropertyName+" with position "+position);
            if (findViewById(R.id.detail_fragment_container) == null) { // no detail > phone > EntryPagerActivity
                Intent intent = EntryPagerActivity.newIntent(this, position);
                startActivity(intent);
            } else { // detail exists > set detail fragment
                Fragment newDetail = EntryFragment.newInstance(position);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_fragment_container, newDetail)
                        .commit();
            }
        } else
        if (eventPropertyName.equals(EntryListViewModel.ENTRY_AT_DELETED)) {
            int position = (Integer)event.getNewValue();
            System.out.println("EntryListActivity received "+eventPropertyName+" with position "+position);
            if (findViewById(R.id.detail_fragment_container) == null) {
                // Intent intent = EntryPagerActivity.newIntent(this, position);
                // startActivity(intent);
            } else { // just show the next entry,... but what about at the end?
                Fragment newDetail = EntryFragment.newInstance(position);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_fragment_container, newDetail)
                        .commit();
            }
        } else {
            System.out.println("EntryListActivity received " + eventPropertyName);
        }
    }
}
