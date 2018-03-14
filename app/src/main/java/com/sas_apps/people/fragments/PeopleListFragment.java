package com.sas_apps.people.fragments;


import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.sas_apps.people.R;
import com.sas_apps.people.adapter.PeopleListAdapter;
import com.sas_apps.people.data.DatabaseHelper;
import com.sas_apps.people.data.PeopleModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;


/**
 * Created by Shashank Shinde.
 */

public class PeopleListFragment extends Fragment {

    private static final String TAG = "PeopleListFragment";
    FloatingActionButton btnAddNew;
    ImageView imageSearch, imageBack, imageClear;
    EditText editSearch;
    ListView peopleList;
    AppBarLayout peopleBar, searchBar;
    private final static int PEOPLE_BAR = 0, SEARCH_BAR = 1;
    private int mAppBarState;
    PeopleListAdapter adapter;
    onPeopleSelectedListener mOnPeopleSelectedListener;
    OnAddContactListener mOnAddContact;


    public PeopleListFragment() {
        // Required empty public constructor
    }

    public interface OnAddContactListener {
        void onAddContact();
    }


    public interface onPeopleSelectedListener {
        void onPeopleSelected(PeopleModel people);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mOnPeopleSelectedListener = (onPeopleSelectedListener) getActivity();
            mOnAddContact = (OnAddContactListener) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "ClassCastException " + e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setAppBar(PEOPLE_BAR);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people_list, container, false);
        btnAddNew = view.findViewById(R.id.fab_add_new);
        peopleList = view.findViewById(R.id.list_people);
        imageSearch = view.findViewById(R.id.image_search);
        imageBack = view.findViewById(R.id.image_back);
        imageClear = view.findViewById(R.id.image_clear);
        peopleBar = view.findViewById(R.id.toolbar_main);
        searchBar = view.findViewById(R.id.toolbar_search);
        editSearch = view.findViewById(R.id.et_search);
        setAppBar(PEOPLE_BAR);
        setPeopleList();
        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnAddContact.onAddContact();
            }
        });
        imageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAppBar();
            }
        });
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAppBar();
                setPeopleList();
            }
        });
        imageClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                peopleList.clearTextFilter();
                editSearch.setText("");
            }
        });


        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String text = editSearch.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    private void setPeopleList() {
        final ArrayList<PeopleModel> contacts = new ArrayList<>();
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
        Cursor cursor = databaseHelper.getAllPeople();
//        Log.d(TAG, "setPeopleList: " + DatabaseUtils.dumpCursorToString(cursor));
        if (!cursor.moveToNext()) {
            Toast.makeText(getActivity(), R.string.no_contacts, Toast.LENGTH_SHORT).show();
        } else {
            do {
                contacts.add(new PeopleModel(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        databaseHelper.close();
        Collections.sort(contacts, new Comparator<PeopleModel>() {
            @Override
            public int compare(PeopleModel o1, PeopleModel o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });


        adapter = new PeopleListAdapter(getActivity(), R.layout.layout_people_list_item, contacts, "");
        peopleList.setAdapter(adapter);


        peopleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mOnPeopleSelectedListener.onPeopleSelected(contacts.get(position));
            }
        });

    }

    private void setAppBar(int state) {
        mAppBarState = state;
        InputMethodManager inputMethodManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        if (mAppBarState == SEARCH_BAR) {
            peopleBar.setVisibility(View.INVISIBLE);
            searchBar.setVisibility(View.VISIBLE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        } else if (mAppBarState == PEOPLE_BAR) {
            editSearch.setText("");
            peopleBar.setVisibility(View.VISIBLE);
            searchBar.setVisibility(View.INVISIBLE);
            View view = getView();
            try {
                assert view != null;
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            } catch (Exception e) {
                Log.i(TAG, "setAppBar " + e.getMessage());
            }
        }
    }

    private void changeAppBar() {
        if (mAppBarState == PEOPLE_BAR) {
            setAppBar(SEARCH_BAR);
        } else if (mAppBarState == SEARCH_BAR) {
            setAppBar(PEOPLE_BAR);
        }
    }


}
