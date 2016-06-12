package com.stefanbuehlmann.entriesvm.viewmodel;

import com.stefanbuehlmann.entriesvm.service.impl.DbEntryService;

/** This is a singleton class to hold the global EntryListViewModel
 * I don't like this solution at all, but the Android way with intents and stuff
 * makes it hard to not use singletons. Even the bignerdranch examples use
 * singletons all over. In order to avoid overuse of it, the call to this
 * singleton is restricted to two places, see below!
 * TODO: ask the authors how to improve
 * Created by stefa on 12.06.2016.
 */
public class EntryListViewModelSingleton {

    private static EntryListViewModel singleton;

    private EntryListViewModelSingleton() {
        DbEntryService dbEntryService = new DbEntryService();
        singleton = new EntryListViewModel(dbEntryService);
    }

    // Access to this singleton is restricted to two places:
    // - CrimeListFragment.onCreate
    // - CrimePagerActivity.onCreate
    // - CrimeFragment.onCreate
    // please keep it that way in order to not inter tangle more!
    // TODO: who knows a better solution to pass an instance variable
    // from CrimeListFragment to CrimePagerActivity ?
    public static synchronized EntryListViewModel getInstance() {
        if(singleton == null) {
            new EntryListViewModelSingleton();
        }
        return singleton;
    }
}
