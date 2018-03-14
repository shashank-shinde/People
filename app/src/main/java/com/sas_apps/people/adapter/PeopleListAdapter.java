package com.sas_apps.people.adapter;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sas_apps.contactdialog.ContactDialog;
import com.sas_apps.contactdialog.ContactDialogBuilder;
import com.sas_apps.contactdialog.OnDialogClickListener;
import com.sas_apps.people.MainActivity;
import com.sas_apps.people.R;
import com.sas_apps.people.data.PeopleModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/*
 * Created by Shashank Shinde.
 */

public class PeopleListAdapter extends ArrayAdapter<PeopleModel> {

    private static final String TAG = "PeopleListAdapter";
    private LayoutInflater mInflater;
    private List<PeopleModel> mContacts = null;
    private ArrayList<PeopleModel> arrayList;
    private int layoutResource;
    private Context mContext;
    private String mAppend;

    public PeopleListAdapter(@NonNull Context context, @LayoutRes int resource,
                             @NonNull List<PeopleModel> contacts, String append) {
        super(context, resource, contacts);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.mContext = context;
        mAppend = append;
        this.mContacts = contacts;
        arrayList = new ArrayList<>();
        this.arrayList.addAll(mContacts);
    }

    private static class ViewHolder {
        TextView name;
        CircleImageView personImage;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.name = convertView.findViewById(R.id.text_list_name);
            viewHolder.personImage = convertView.findViewById(R.id.image_list);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String name = getItem(position).getName();
        final String imagePath = getItem(position).getProfileImage();
        viewHolder.name.setText(name);


        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(mAppend + imagePath, viewHolder.personImage, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
            }
        });

        viewHolder.personImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ContactDialogBuilder(mContext)
                        .setName(name)
                        .setSubText(arrayList.get(position).getphoneNumber())
                        .setOption1Text("Make call")
                        .setOption1Drawable(R.drawable.ic_call)
                        .setOption2Text("Send message")
                        .setOption2Drawable(R.drawable.ic_message)
                        .setBackgroundColor(Color.BLACK)
                        .setImageResource(R.drawable.ic_launcher_background)
                        .setCancelable(true)
                        .setOnOption1ClickListener(new OnDialogClickListener() {
                            @Override
                            public void OnClickListener(ContactDialog contactDialog) {
                                Toast.makeText(mContext, "Calling", Toast.LENGTH_SHORT).show();
                                if (((MainActivity) mContext)
                                        .checkPermission(new String[]{Manifest.permission.CALL_PHONE})) {
                                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel",
                                            arrayList.get(position).getphoneNumber(), null));
                                    try {
                                        (mContext).startActivity(callIntent);
                                    } catch (ActivityNotFoundException e) {
                                        Log.e(TAG, "ActivityNotFoundException " + e.getMessage());
                                        Toast.makeText(getContext(), R.string.error_msg, Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.d(TAG, "Making permission request");
                                    ((MainActivity) mContext).verifyPermission(new String[]{Manifest.permission.CALL_PHONE});
                                }
                                contactDialog.dismiss();
                            }
                        })
                        .setOnOption2ClickListener(new OnDialogClickListener() {
                            @Override
                            public void OnClickListener(ContactDialog contactDialog) {
                                Toast.makeText(mContext, "Sending message", Toast.LENGTH_SHORT).show();
                                Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms",
                                        arrayList.get(position).getphoneNumber(), null));
                                try {
                                    (mContext).startActivity(smsIntent);
                                } catch (Exception e) {
                                    Log.e(TAG, "" + e.getMessage());
                                    Toast.makeText(getContext(), R.string.error_msg, Toast.LENGTH_SHORT).show();
                                }
                                contactDialog.dismiss();
                            }
                        })
                        .build().show();

            }

        });

        return convertView;
    }

    public void filter(String characterText) {
        characterText = characterText.toLowerCase(Locale.getDefault());
        mContacts.clear();
        if (characterText.length() == 0) {
            mContacts.addAll(arrayList);
        } else {
            mContacts.clear();
            for (PeopleModel contact : arrayList) {
                if (contact.getName().toLowerCase(Locale.getDefault()).contains(characterText)) {
                    mContacts.add(contact);
                }
            }
        }
        notifyDataSetChanged();
    }
}