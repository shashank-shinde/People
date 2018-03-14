package com.sas_apps.people.fragments;


import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sas_apps.people.MainActivity;
import com.sas_apps.people.R;
import com.sas_apps.people.data.DatabaseHelper;
import com.sas_apps.people.data.PeopleModel;
import com.sas_apps.people.dialog.SelectImageDialog;
import com.sas_apps.people.imageLoader.UniversalImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;


public class NewContactFragment extends Fragment implements SelectImageDialog.OnPhotoReceivedListener {


    private static final String TAG = "NewContactFragment";
    private EditText mPhoneNumber, mName, mEmail;
    private CircleImageView mContactImage;
    private Spinner mSelectDevice;
    private Toolbar toolbar;
    TextView heading;
    private String mSelectedImagePath;
    private int mPreviousKeyStroke;
    public static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    public NewContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_contact, container, false);


        mPhoneNumber = view.findViewById(R.id.etContactPhone);
        mName = view.findViewById(R.id.etContactName);
        mEmail = view.findViewById(R.id.etContactEmail);
        mContactImage = view.findViewById(R.id.contactImage);
        mSelectDevice = view.findViewById(R.id.selectDevice);
        toolbar = view.findViewById(R.id.editContactToolbar);
        Log.d(TAG, "onCreateView: started.");
        heading =  view.findViewById(R.id.textContactToolbar);
        heading.setText("Add new contact");
        mSelectedImagePath = null;
        UniversalImageLoader.setImage(null, mContactImage, null, "");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);


        ImageView ivBackArrow =  view.findViewById(R.id.ivBackArrow);
        ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked back arrow.");
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        ImageView confirmNewContact =  view.findViewById(R.id.ivCheckMark);
        confirmNewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to save new contact.");
                if(isTextNotEmpty(mName.getText().toString())){
                    Log.d(TAG, "onClick: saving new contact. " + mName.getText().toString() );

                    DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
                    PeopleModel contact = new PeopleModel(mName.getText().toString(),
                            mPhoneNumber.getText().toString(),
                            mSelectDevice.getSelectedItem().toString(),
                            mEmail.getText().toString(),
                            mSelectedImagePath);
                    if(databaseHelper.addPeople(contact)){
                        Toast.makeText(getActivity(), R.string.saved, Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();
                    }else{
                        Toast.makeText(getActivity(), R.string.save_error, Toast.LENGTH_SHORT).show();
                    }
                    databaseHelper.close();
                }

            }
        });

        ImageView ivCamera =  view.findViewById(R.id.ivCamera);
        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < PERMISSIONS.length; i++) {
                    String[] permission = {PERMISSIONS[i]};
                    if (((MainActivity) getActivity()).checkPermission(permission)) {
                        if (i == PERMISSIONS.length - 1) {
                            Log.d(TAG, "onClick: opening the 'image selection dialog box'.");
                            SelectImageDialog dialog = new SelectImageDialog();
                            dialog.show(getFragmentManager(), "dialog");
                            dialog.setTargetFragment(NewContactFragment.this, 0);
                        }
                    } else {
                        ((MainActivity) getActivity()).verifyPermission(permission);
                    }
                }


            }
        });


//        -----------------     Phone number formant
        mPhoneNumber.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                mPreviousKeyStroke = keyCode;
                return false;
            }
        });


        mPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String number = s.toString();
                if (number.length() == 3 && mPreviousKeyStroke != KeyEvent.KEYCODE_DEL
                        && !number.contains("(")) {
                    number = String.format("(%s", s.toString().substring(0, 3));
                    mPhoneNumber.setText(number);
                    mPhoneNumber.setSelection(number.length());
                } else if (number.length() == 5 && mPreviousKeyStroke != KeyEvent.KEYCODE_DEL
                        && !number.contains(")")) {
                    number = String.format("(%s) %s",
                            s.toString().substring(1, 4),
                            s.toString().substring(4, 5));
                    mPhoneNumber.setText(number);
                    mPhoneNumber.setSelection(number.length());
                } else if (number.length() == 10 && mPreviousKeyStroke != KeyEvent.KEYCODE_DEL
                        && !number.contains("-")) {
                    number = String.format("(%s) %s-%s",
                            s.toString().substring(1, 4),
                            s.toString().substring(6, 9),
                            s.toString().substring(9, 10));
                    mPhoneNumber.setText(number);
                    mPhoneNumber.setSelection(number.length());

                }
            }
        });

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.contact_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_delete:
                Log.d(TAG, "onOptionsItemSelected: deleting contact.");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void getBitmapImage(Bitmap bitmap) {
        Log.d(TAG, "getBitmapImage: got the bitmap: " + bitmap);
        if (bitmap != null) {
            ((MainActivity) getActivity()).compressBitmap(bitmap, 50);
            mContactImage.setImageBitmap(bitmap);
        }
    }

    @Override
    public void getImagePath(String imagePath) {
        Log.d(TAG, "getImagePath: got the image path: " + imagePath);
        if (!imagePath.equals("")) {
            imagePath = imagePath.replace(":/", "://");
            mSelectedImagePath = imagePath;
            UniversalImageLoader.setImage(imagePath, mContactImage, null, "");
        }
    }

    private boolean isTextNotEmpty(String string) {
        return !string.equals("");
    }

}
