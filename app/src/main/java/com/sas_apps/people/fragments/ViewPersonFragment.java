package com.sas_apps.people.fragments;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sas_apps.people.R;
import com.sas_apps.people.adapter.PersonDetailsAdaptor;
import com.sas_apps.people.data.DatabaseHelper;
import com.sas_apps.people.data.PeopleModel;
import com.sas_apps.people.imageLoader.UniversalImageLoader;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Shashank Shinde.
 */
public class ViewPersonFragment extends Fragment {

    public ViewPersonFragment() {
        super();
        setArguments(new Bundle());
    }

    private static final String TAG = "ViewPersonFragment";
    Toolbar toolbar;
    PeopleModel mPeopleModel;
    TextView textContactName;
    CircleImageView imageContactImage;
    ImageView imageBack, imageEdit;
    ListView listDetails;

    public interface OnEditContactListener {
        void onEditContactSelected(PeopleModel people);
    }

    OnEditContactListener mOnEditContactListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_person, container, false);
        toolbar = view.findViewById(R.id.toolbar_view_people);
        imageContactImage = view.findViewById(R.id.image_person);
        textContactName = view.findViewById(R.id.text_person_name);
        imageBack = view.findViewById(R.id.image_back_home);
        imageEdit = view.findViewById(R.id.image_edit);
        listDetails = view.findViewById(R.id.list_details);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        this.mPeopleModel = getItemFromBundle();

        init();

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPeopleModel = getItemFromBundle();
                EditFragment editFragment = new EditFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(getString(R.string.key), mPeopleModel);
                editFragment.setArguments(bundle);
                android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, editFragment)
                        .addToBackStack(getString(R.string.edit_fragment))
                        .commit();
            }
        });
        return view;
    }

    private void init() {
        textContactName.setText(mPeopleModel.getName());
        UniversalImageLoader.setImage(mPeopleModel.getProfileImage(), imageContactImage, null, "");
        ArrayList<String> details = new ArrayList<>();
        details.add(mPeopleModel.getphoneNumber());
        details.add(mPeopleModel.getEmail());
        PersonDetailsAdaptor personDetailsAdaptor = new PersonDetailsAdaptor(getActivity(), R.layout.card_row, details);
        listDetails.setAdapter(personDetailsAdaptor);
        listDetails.setDivider(null);
    }


    private PeopleModel getItemFromBundle() {
//        Log.d(TAG, " getting item from bundle   " + getArguments());
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getParcelable(getString(R.string.key));
        } else {
            return null;
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_delete:
                DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
                Cursor cursor = databaseHelper.getPeopleID(mPeopleModel);

                int contactID = -1;
                while (cursor.moveToNext()) {
                    contactID = cursor.getInt(0);
                }
                if (contactID > -1) {
                    if (databaseHelper.deletePeople(contactID) > 0) {
                        Toast.makeText(getActivity(), R.string.deleted, Toast.LENGTH_SHORT).show();
                        this.getArguments().clear();
                        getActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getActivity(), R.string.save_error, Toast.LENGTH_SHORT).show();
                    }
                }
                cursor.close();
                databaseHelper.close();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
        Cursor cursor = databaseHelper.getPeopleID(mPeopleModel);
        int contactID = -1;
        while (cursor.moveToNext()) {
            contactID = cursor.getInt(0);
        }
        if (contactID > -1) {
            init();
        } else {
            this.getArguments().clear();
            getActivity().getSupportFragmentManager().popBackStack();
        }
        cursor.close();
        databaseHelper.close();
    }

}
