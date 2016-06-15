package com.stefanbuehlmann.entrylist_android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.stefanbuehlmann.entriesvm.service.intf.EntryServiceI;
import com.stefanbuehlmann.entriesvm.viewmodel.EntryListViewModel;
import com.stefanbuehlmann.entriesvm.viewmodel.EntryListViewModelSingleton;
import com.stefanbuehlmann.entriesvm.viewmodel.EntryViewModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class EntryFragment extends Fragment implements PropertyChangeListener {

    private static final String BUNDLE_ARG_ENTRY_POSITION = "entry_position";

    private EntryListViewModel mEntryListVM;
    private EntryViewModel mEntryVM;

    private EditText mEntryNameField;
    private EditText mEntryDescriptionField;

    public static EntryFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putSerializable(BUNDLE_ARG_ENTRY_POSITION, position);

        EntryFragment fragment = new EntryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int position = (int) getArguments().getSerializable(BUNDLE_ARG_ENTRY_POSITION);
        setHasOptionsMenu(true);

        mEntryListVM = EntryListViewModelSingleton.getInstance();
        if (position!= EntryServiceI.NO_ID) {
            mEntryVM = mEntryListVM.find(position);
        } else {
            System.out.println("error: EntryFragment.onCreate with position "+position);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mEntryVM.save();
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (getActivity() == null) {
                    return;
                }
                mEntryVM.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        mEntryDescriptionField = (EditText) v.findViewById(R.id.entry_description);
        mEntryDescriptionField.setText(mEntryVM.getDescription());
        mEntryDescriptionField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (getActivity() == null) {
                    return;
                }
                mEntryVM.setDescription(s.toString()); // triggers the PropertyChangeListener below
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // listen on the model, update fields if the model changes
        // needed for processes updating the model in parallel
        mEntryVM.addPropertyChangeListener(this);  // TODO remove in some CloseView method?

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_entry, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_entry:
                System.out.println("EntryFragment.delete");
                mEntryListVM.delete(mEntryListVM.indexOf(mEntryVM));
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        String eventPropertyName = event.getPropertyName();
        if (eventPropertyName.equals(EntryViewModel.NAME_CHG)) {
            String newValue = (String)event.getNewValue();
            System.out.println("EntryFragment received "+eventPropertyName+" with value "+newValue);
            if (!newValue.equals(mEntryNameField.getText().toString())) {
                // update field, if it wasn't set in above onTextChanged(would reset cursor)
                mEntryNameField.setText(newValue);
            }
        } else if (eventPropertyName.equals(EntryViewModel.DESCRIPTION_CHG)) {
            String newValue = (String)event.getNewValue();
            System.out.println("EntryFragment received "+eventPropertyName+" with value "+newValue);
            if (!newValue.equals(mEntryDescriptionField.getText().toString())) {
                // see above comment
                mEntryDescriptionField.setText(newValue);
            }
        } else if (eventPropertyName.equals(EntryViewModel.ID_CHG)) {
            // change of ID only occurs at initialization from -1 to next sequence value
            System.out.println("EntryFragment received "+eventPropertyName);
        } else {
            System.out.println("EntryFragment received "+eventPropertyName);
        }
    }
}
