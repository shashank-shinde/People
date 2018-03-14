package com.sas_apps.people.fragments;


import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.sas_apps.people.MainActivity;
import com.sas_apps.people.R;
import com.sas_apps.people.data.DatabaseHelper;
import com.sas_apps.people.data.PeopleModel;
import com.sas_apps.people.dialog.SelectImageDialog;
import com.sas_apps.people.imageLoader.UniversalImageLoader;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Shashank Shinde.
 */
public class EditFragment extends Fragment implements SelectImageDialog.OnPhotoReceivedListener {
    private static final String TAG = "EditFragment";
    ImageView imageBack, imageDone, imageCamera;
    public static CircleImageView personPic;
    EditText etName, etNumber, etEmail;
    Toolbar toolbar;
    private String mSelectedImagePath;
    PeopleModel mPeopleModel;
    private Spinner mSelectDevice;


    public static final String[] PHOTO_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_DOCUMENTS,
            Manifest.permission.CAMERA};

    public EditFragment() {
        super();
        setArguments(new Bundle());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit, container, false);
        imageBack = view.findViewById(R.id.image_back_edit);
        imageDone = view.findViewById(R.id.image_done);
        imageCamera = view.findViewById(R.id.image_Camera);
        personPic = view.findViewById(R.id.image_input);
        etName = view.findViewById(R.id.et_name);
        etEmail = view.findViewById(R.id.et_email);
        mSelectDevice = view.findViewById(R.id.selectDevice);
        etNumber = view.findViewById(R.id.et_number);
        toolbar = view.findViewById(R.id.toolbar_edit);
        mPeopleModel = getContactFromBundle();
        if (mPeopleModel != null) {
            setContent(mPeopleModel);
        }
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < PHOTO_PERMISSIONS.length; i++) {
                    String[] permission = {PHOTO_PERMISSIONS[i]};
                    if (((MainActivity) getActivity()).checkPermission(permission)) {
                        if (i == PHOTO_PERMISSIONS.length - 1) {
                            Log.d(TAG, "onClick: opening the 'image selection dialog box'.");
                            SelectImageDialog dialog = new SelectImageDialog();
                            dialog.setTargetFragment(EditFragment.this, 0);
                            dialog.show(getFragmentManager(), getString(R.string.DialogKey));
                        }
                    } else {
                        ((MainActivity) getActivity()).verifyPermission(permission);
                    }
                }
            }
        });


        imageDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: saving the edited contact.");
                if (checkStringIfNull(etName.getText().toString())) {
                    Log.d(TAG, "onClick: saving changes to the contact: " + etName.getText().toString());

                    DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
                    Cursor cursor = databaseHelper.getPeopleID(mPeopleModel);

                    int contactID = -1;
                    while (cursor.moveToNext()) {
                        contactID = cursor.getInt(0);
                    }
                    if (contactID > -1) {
                        if (mSelectedImagePath != null) {
                            mPeopleModel.setProfileImage(mSelectedImagePath);
                        }
                        mPeopleModel.setName(etName.getText().toString());
                        mPeopleModel.setphoneNumber(etNumber.getText().toString());
                        mPeopleModel.setDevice("Mobile");
                        mPeopleModel.setEmail(etEmail.getText().toString());

                        databaseHelper.updatePeople(mPeopleModel, contactID);
                        Toast.makeText(getActivity(), "Contact Updated", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getActivity(), "Database Error", Toast.LENGTH_SHORT).show();
                    }
                    cursor.close();
                    databaseHelper.close();
                }
            }
        });


        return view;
    }

    private void setContent(PeopleModel people) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.contact_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etName.setText(people.getName());
        etNumber.setText(people.getphoneNumber());
        etEmail.setText(people.getEmail());
        UniversalImageLoader.setImage(mPeopleModel.getProfileImage(), personPic, null, "");
    }

    private PeopleModel getContactFromBundle() {
        Log.d(TAG, "getContactFromBundle: arguments: " + getArguments());
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getParcelable(getString(R.string.key));
        } else {
            return null;
        }
    }


    public void getBitmapImage(Bitmap bitmap) {
        Log.d(TAG, "getBitmapImage: got the bitmap: " + bitmap);
        if (bitmap != null) {
            Log.d(TAG, "getImagePath: Setting image");
            personPic.setImageBitmap(compressBitmap(bitmap, 50));
            ;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void getImagePath(String imagePath) {
        Log.d(TAG, "getImagePath: image path: " + imagePath);

        if (imagePath != null) {
            imagePath = imagePath.replace(":/", "://");
            mSelectedImagePath = imagePath;
            Log.d(TAG, "getImagePath: Setting image");
            UniversalImageLoader.setImage(imagePath, personPic, null, "");
        }
    }

    private boolean checkStringIfNull(String string) {
        return !string.equals("");
    }

    public Bitmap compressBitmap(Bitmap bitmap, int quality) {
        Bitmap imageBitmap = bitmap;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return imageBitmap;
    }
}
