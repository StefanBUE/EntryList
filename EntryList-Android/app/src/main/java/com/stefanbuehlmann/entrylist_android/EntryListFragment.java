package com.stefanbuehlmann.entrylist_android;

import android.app.Activity;
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

import com.stefanbuehlmann.entriesvm.viewmodel.EntryListViewModel;
import com.stefanbuehlmann.entriesvm.viewmodel.EntryListViewModelSingleton;
import com.stefanbuehlmann.entriesvm.viewmodel.EntryViewModel;

public class EntryListFragment extends Fragment {

    private RecyclerView mEntryRecyclerView;
    private EntryAdapter mAdapter;
    private Callbacks mCallbacks;
    private EntryListViewModel entryListVM;

    /**
     * Required interface for hosting activities.
     */
    public interface Callbacks {
        void onEntrySelected(EntryViewModel entryVM);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // this is one of the restricted calls to this singleton, dont' add more, use entryListVM
        entryListVM = EntryListViewModelSingleton.getInstance();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
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
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
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
                EntryViewModel entryVM = entryListVM.create();
                updateUI();
                mCallbacks.onEntrySelected(entryVM);
                return true;
            case R.id.menu_item_delete_entry:
                return true;
            case R.id.menu_item_settings:
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

    private class EntryHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

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
        }

        @Override
        public void onClick(View v) {
            mCallbacks.onEntrySelected(mEntryVM);
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
        public int getItemCount() {
            return mEntryListVM.count();
        }

        public void setEntryList(EntryListViewModel entryListVM) {
            mEntryListVM = entryListVM;
        }
    }
}
