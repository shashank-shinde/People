package com.sas_apps.people.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sas_apps.people.R;

import java.io.File;

import static android.app.Activity.RESULT_OK;

/*
 * Created by Shashank Shinde.
 */

public class SelectImageDialog extends DialogFragment {

    private static final int GALLERY_REQUEST_CODE = 3;
    TextView takePhoto, selectPhoto, cancel;
    private static final String TAG = "SelectImageDialog";
    private static final int CAMERA_REQUEST_CODE = 2;


    public interface OnPhotoReceivedListener {
        void getBitmapImage(Bitmap bitmap);
        void getImagePath(String imagePath);
    }

    static OnPhotoReceivedListener mOnPhotoReceived;


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_select_image, container, false);
        takePhoto = view.findViewById(R.id.text_dialog_takePhoto);
        selectPhoto = view.findViewById(R.id.text_dialog_selectPhoto);
        cancel = view.findViewById(R.id.text_dialog_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                getActivity().startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        });

        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                getActivity().startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnPhotoReceived = (OnPhotoReceivedListener) getTargetFragment();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }

    public static void handleEvent(int requestCode, int resultCode, Intent data){
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            mOnPhotoReceived.getBitmapImage(bitmap);
        } else if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            assert selectedImageUri != null;
            File file = new File(selectedImageUri.toString());
            mOnPhotoReceived.getImagePath(file.getPath());
        }
        else {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            mOnPhotoReceived.getBitmapImage(bitmap);
            getDialog().dismiss();
        } else if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            assert selectedImageUri != null;
            File file = new File(selectedImageUri.toString());
            mOnPhotoReceived.getImagePath(file.getPath());
            getDialog().dismiss();
        }
    }
}
