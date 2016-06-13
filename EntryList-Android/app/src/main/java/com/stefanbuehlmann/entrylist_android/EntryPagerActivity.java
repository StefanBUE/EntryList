package com.stefanbuehlmann.entrylist_android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.stefanbuehlmann.entriesvm.service.intf.EntryServiceI;
import com.stefanbuehlmann.entriesvm.viewmodel.EntryListViewModel;
import com.stefanbuehlmann.entriesvm.viewmodel.EntryListViewModelSingleton;
import com.stefanbuehlmann.entriesvm.viewmodel.EntryViewModel;

public class EntryPagerActivity extends AppCompatActivity
        implements EntryFragment.Callbacks {
    private static final String EXTRA_ENTRY_ID =
            "com.stefanbuehlmann.entrylist_android.entry_id";

    private ViewPager mViewPager;
    private EntryListViewModel entryListVM;

    public static Intent newIntent(Context packageContext, long entryId) {
        Intent intent = new Intent(packageContext, EntryPagerActivity.class);
        intent.putExtra(EXTRA_ENTRY_ID, entryId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_pager);

        long entryId = (long) getIntent()
                .getSerializableExtra(EXTRA_ENTRY_ID);

        mViewPager = (ViewPager) findViewById(R.id.activity_entry_pager_view_pager);

        // this is one of the restricted calls to this singleton, dont' add more, use entryListVM
        entryListVM = EntryListViewModelSingleton.getInstance();

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
                EntryViewModel entryVM = entryListVM.find(position);
                return EntryFragment.newInstance(entryVM.getId());
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

        int index = entryListVM.indexOf(entryId);
        if (index != EntryServiceI.NO_ID) {
            mViewPager.setCurrentItem(index);
        }
    }

    @Override
    public void onEntryUpdated(EntryViewModel entryVM) {

    }
}
