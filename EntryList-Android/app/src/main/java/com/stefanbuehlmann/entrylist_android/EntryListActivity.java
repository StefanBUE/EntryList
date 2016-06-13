package com.stefanbuehlmann.entrylist_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.stefanbuehlmann.entriesvm.viewmodel.EntryViewModel;
import com.vals.a2ios.sqlighter.impl.SQLighterDbImpl;
import com.vals.a2ios.sqlighter.intf.Singleton;

public class EntryListActivity extends SingleFragmentActivity
        implements EntryListFragment.Callbacks, EntryFragment.Callbacks {

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

            db.setOverwriteDb(true);
            try {
                db.deployDbOnce();
                db.openIfClosed();
            } catch (Throwable t) {
                t.printStackTrace();
            }
            Singleton.getInstance().setSqLighterDb(db);
        }
    }

    // implement interface EntryListFragment.Callbacks with onEntrySelected
    // corresponds roughly to iOS's MasterViewController.changeSelection, which calls
    // the delegateDetailViewC which implements entrySelected
    // Android does that with the Intents-Datapassing approach
    @Override
    public void onEntrySelected(EntryViewModel entryVM) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = EntryPagerActivity.newIntent(this, entryVM.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = EntryFragment.newInstance(entryVM.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    // implement interface EntryFragment.Callbacks with onEntryUpdated
    @Override
    public void onEntryUpdated(EntryViewModel entryVM) {
        EntryListFragment listFragment = (EntryListFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
