package com.stefanbuehlmann.entrylist_android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.stefanbuehlmann.entriesvm.viewmodel.EntryListViewModel;
import com.stefanbuehlmann.entriesvm.viewmodel.EntryListViewModelSingleton;
import com.stefanbuehlmann.entriesvm.viewmodel.EntryViewModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class EntryPagerActivity extends AppCompatActivity implements PropertyChangeListener {
    private static final String EXTRA_ENTRY_POSITION =
            "com.stefanbuehlmann.entrylist_android.entry_position";

    private ViewPager mViewPager;
    private EntryListViewModel entryListVM;

    public static Intent newIntent(Context packageContext, int position) {
        Intent intent = new Intent(packageContext, EntryPagerActivity.class);
        intent.putExtra(EXTRA_ENTRY_POSITION, position);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_pager);

        int position1 = (int)getIntent()
                .getSerializableExtra(EXTRA_ENTRY_POSITION);

        mViewPager = (ViewPager) findViewById(R.id.activity_entry_pager_view_pager);

        // this is one of the restricted calls to this singleton, dont' add more, use entryListVM
        entryListVM = EntryListViewModelSingleton.getInstance();

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
                // EntryViewModel entryVM = entryListVM.find(position);
                return EntryFragment.newInstance(position); // entryVM.getId());
            }

            @Override
            public int getCount() {
                return entryListVM.count();
            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() { // TODO depricated!!!
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                EntryViewModel entryVM = entryListVM.find(position);

                if (entryVM.getName() != null) {
                    setTitle(entryVM.getName());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        EntryViewModel entryVM = entryListVM.find(position1);
        // WHY can I have an inexisting position?
        // this seems to have the effect, that deleting the last causes the first to be used next
        if (entryVM != null) {
            mViewPager.setCurrentItem(position1);
            setTitle(entryVM.getName());
        }
        entryListVM.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        String eventPropertyName = event.getPropertyName();
        if (eventPropertyName.equals(EntryListViewModel.ENTRY_AT_DELETED)) {
            int position = (Integer)event.getNewValue();
            System.out.println("EntryPagerActivity received "+eventPropertyName+" with position "+position);
            // TODO do we need to inform mViewPager about the deletion?
        } else {
            System.out.println("EntryFragment received " + eventPropertyName );
        }
    }
}
