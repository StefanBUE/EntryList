package com.stefanbuehlmann.entrylist_android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.stefanbuehlmann.entriesvm.viewmodel.EntryListViewModel;
import com.stefanbuehlmann.entriesvm.viewmodel.EntryListViewModelSingleton;
import com.stefanbuehlmann.entriesvm.viewmodel.EntryViewModel;

public class EntryFragment extends Fragment {

    private static final String BUNDLE_ARG_ENTRY_ID = "entry_id";

    private EntryViewModel mEntryVM;
    private EditText mEntryIdField;
    private EditText mEntryNameField;
    private EditText mEntryDescriptionField;
    private Callbacks mCallbacks;

    /**
     * Required interface for hosting activities.
     */
    public interface Callbacks {
        void onEntryUpdated(EntryViewModel entryVM);
    }

    public static EntryFragment newInstance(long entryId) {
        Bundle args = new Bundle();
        args.putSerializable(BUNDLE_ARG_ENTRY_ID, entryId);

        EntryFragment fragment = new EntryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long entryId = (long) getArguments().getSerializable(BUNDLE_ARG_ENTRY_ID);

        EntryListViewModel entryListVM = EntryListViewModelSingleton.getInstance();
        mEntryVM = entryListVM.find(entryListVM.indexOf(entryId));
    }

    @Override
    public void onPause() {
        super.onPause();
        mEntryVM.save();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_entry, container, false);

        // TODO: read only field for the ID
        mEntryNameField = (EditText) v.findViewById(R.id.entry_title);
        mEntryNameField.setText(mEntryVM.getName());
        mEntryNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (getActivity() == null) {
                    return;
                }
                mEntryVM.setName(s.toString());
                updateEntry();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mEntryDescriptionField = (EditText) v.findViewById(R.id.entry_description);
        mEntryDescriptionField.setText(mEntryVM.getDescription());
        mEntryDescriptionField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (getActivity() == null) {
                    return;
                }
                mEntryVM.setDescription(s.toString());
                updateEntry();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return v;
    }

    private void updateEntry() {
        mEntryVM.save();
        mCallbacks.onEntryUpdated(mEntryVM);
    }
}
