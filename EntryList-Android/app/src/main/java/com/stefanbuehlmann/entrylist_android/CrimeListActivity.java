package com.stefanbuehlmann.entrylist_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.vals.a2ios.sqlighter.impl.SQLighterDbImpl;
import com.vals.a2ios.sqlighter.intf.Singleton;

public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        // here the switch between using the refs.xml files
        // - activity_fragment.xml with fragment_container
        // - activity_twopane.xml  with fragment_container & detail_fragment_container
        return R.layout.activity_masterdetail;
    }
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
*/
        /**
         * SQLite initialization. This portion is
         * platform specific, but common by most part.
         */
/*
        SQLighterDbImpl db = new SQLighterDbImpl();
        String dbPath =
                this.getApplication().getApplicationContext().getFilesDir()
                        .getParentFile().getPath() + "/databases/";
        db.setDbPath(dbPath);
        db.setDbName("sqlite.sqlite");
        if(!db.isDbFileDeployed()) {
            System.out.println("DB file is not deployed zz");
        } else {
            System.out.println("DB file is deployed zz");
        }
        db.setContext(this);
*/
        /* Will overwrite destination DB file at device.
         * Good for the demo since we have the same starting point
         * and can implement tests. Production app settings most
         * likely would be different.
         */
/*
        db.setOverwriteDb(true);
        try {
            db.deployDbOnce();
            db.openIfClosed();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        Singleton.getInstance().setSqLighterDb(db);
*//*
    }
*/

    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment listFragment = (CrimeListFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
