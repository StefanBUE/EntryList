package com.stefanbuehlmann.entrylist_android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stefanbuehlmann.entriesvm.service.intf.EntryServiceI;
import com.stefanbuehlmann.entriesvm.viewmodel.EntryListViewModel;
import com.stefanbuehlmann.entriesvm.viewmodel.EntryListViewModelSingleton;
import com.stefanbuehlmann.entriesvm.viewmodel.EntryViewModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class EntryListFragment extends Fragment implements PropertyChangeListener {

    private RecyclerView mEntryRecyclerView;
    private EntryAdapter mAdapter;
    private EntryListViewModel entryListVM;
    private EntryViewModel currentlySelectedEntryVM = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // this is one of the restricted calls to this singleton, dont' add more, use entryListVM
        entryListVM = EntryListViewModelSingleton.getInstance();
        entryListVM.addPropertyChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry_list, container, false);

        mEntryRecyclerView = (RecyclerView) view
                .findViewById(R.id.entry_recycler_view);
        mEntryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_entry_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_entry:
                currentlySelectedEntryVM = entryListVM.create();
                updateUI();
                entryListVM.setSelectedEntry(entryListVM.indexOf(currentlySelectedEntryVM));
                return true;
            case R.id.menu_item_delete_entry:
                System.out.println("EntryListFragment.delete");
                if (currentlySelectedEntryVM!=null) {
                    int position = entryListVM.indexOf(currentlySelectedEntryVM);
                    entryListVM.delete(position);
                    currentlySelectedEntryVM = null; // TODO get rid of this redundant variable
                    entryListVM.setSelectedEntry(EntryServiceI.NO_ID);
                    updateUI(); // todo needed ?
                } else {
                    System.out.println("nothing selectedd");
                }
                return true;
            case R.id.menu_item_randomize:
                entryListVM.randomChange();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateUI() {
        if (mAdapter == null) {
            mAdapter = new EntryAdapter(entryListVM);
            mEntryRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setEntryList(entryListVM);
            mAdapter.notifyDataSetChanged();
        }
    }

    // get changes on EntryListVM
    public void propertyChange(PropertyChangeEvent event) {
        String eventPropertyName = event.getPropertyName();
        if (eventPropertyName.equals(EntryListViewModel.ENTRY_AT_CHANGED)) {
            int position = (Integer)event.getNewValue();
            System.out.println("EntryListFragment received "+eventPropertyName+" with position "+position);
            if (position!= EntryServiceI.NO_ID) {
                mAdapter.notifyItemChanged(position);
            }
        } else if (eventPropertyName.equals(EntryListViewModel.ENTRY_AT_DELETED)) {
            int position = (Integer)event.getNewValue();
            System.out.println("EntryListFragment received "+eventPropertyName+" with position "+position);
            if (position!= EntryServiceI.NO_ID) {
                currentlySelectedEntryVM = null;
                mAdapter.notifyItemRemoved(position); // TODO, last displayed data remains on screen!!!!
            }
        } else {
            System.out.println("EntryListFragment received " + eventPropertyName );
        }
    }

    private class EntryHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, PropertyChangeListener {
        // listens on EntryViewModel.*

        private TextView mEntryIdTextView;
        private TextView mEntryNameTextView;
        private TextView mEntryDescriptionTextView;

        private EntryViewModel mEntryVM;

        public EntryHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mEntryIdTextView = (TextView) itemView.findViewById(R.id.list_item_entry_id_text_view);
            mEntryNameTextView = (TextView) itemView.findViewById(R.id.list_item_entry_name_text_view);
            mEntryDescriptionTextView = (TextView) itemView.findViewById(R.id.list_item_entry_description_text_view);
        }

        public void bindEntry(EntryViewModel entryVM) {
            mEntryVM = entryVM;
            mEntryIdTextView.setText(""+mEntryVM.getId());
            mEntryNameTextView.setText(mEntryVM.getName());
            mEntryDescriptionTextView.setText(mEntryVM.getDescription());
            mEntryVM.addPropertyChangeListener(this);  // register for changes in the detail
        }
        public void unBindEntry() {
            mEntryVM.removePropertyChangeListener(this);
        }

        @Override
        public void onClick(View v) {
            currentlySelectedEntryVM = mEntryVM;
            entryListVM.setSelectedEntry(entryListVM.indexOf(mEntryVM));
        }

        // get changes on EntryVM
        public void propertyChange(PropertyChangeEvent event) {
            String eventPropertyName = event.getPropertyName();
            if (eventPropertyName.equals(EntryViewModel.NAME_CHG)) {
                String newValue = (String)event.getNewValue();
                System.out.println("EntryListFragment EntryHolder received "+eventPropertyName+" with value "+newValue);
                if (!newValue.equals(mEntryNameTextView.getText().toString())) {
                    // update field, only if it changed
                    mEntryNameTextView.setText(newValue);
                }
            } else if (eventPropertyName.equals(EntryViewModel.DESCRIPTION_CHG)) {
                String newValue = (String)event.getNewValue();
                System.out.println("EntryListFragment EntryHolder received "+eventPropertyName+" with value "+newValue);
                if (!newValue.equals(mEntryDescriptionTextView.getText().toString())) {
                    // see above comment
                    mEntryDescriptionTextView.setText(newValue);
                }
            } else if (eventPropertyName.equals(EntryViewModel.ID_CHG)) {
                long newValue = (Long)event.getNewValue();
                System.out.println("EntryListFragment EntryHolder received "+eventPropertyName+" with value "+newValue);
                if (!((""+newValue)).equals(mEntryIdTextView.getText().toString())) {
                    // see above comment
                    mEntryIdTextView.setText(""+newValue);
                }
            } else {
                System.out.println("EntryListFragment EntryHolder received " + eventPropertyName );
            }
        }
    }

    private class EntryAdapter extends RecyclerView.Adapter<EntryHolder> {

        private EntryListViewModel mEntryListVM;

        public EntryAdapter(EntryListViewModel entryListVM) {
            mEntryListVM = entryListVM;
        }

        @Override
        public EntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.entry_list_item, parent, false);
            return new EntryHolder(view);
        }

        @Override
        public void onBindViewHolder(EntryHolder holder, int position) {
            EntryViewModel entryVM = mEntryListVM.find(position);
            holder.bindEntry(entryVM);
        }
        @Override
        public void onViewRecycled (EntryHolder holder) {
            holder.unBindEntry();
        }

        @Override
        public int getItemCount() {
            return mEntryListVM.count();
        }

        public void setEntryList(EntryListViewModel entryListVM) {
            mEntryListVM = entryListVM;
        }
    }
}
