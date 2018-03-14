package com.sas_apps.people.adapter;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sas_apps.people.MainActivity;
import com.sas_apps.people.R;

import java.util.List;

/*
 * Created by Shashank Shinde.
 */

public class PersonDetailsAdaptor extends ArrayAdapter<String> {

    private static final String TAG = "PersonDetailsAdaptor";
    LayoutInflater layoutInflater;
    private List<String> mDetails = null;
    private int mLayoutResource;
    private Context mContext;
    private String mAppend;

    public PersonDetailsAdaptor(@NonNull Context context, int resource, @NonNull List<String> details) {
        super(context, resource, details);
        this.mContext = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutResource = resource;
        this.mDetails = details;
    }

    private static class ViewHolder {
        TextView property;
        ImageView rightIcon;
        ImageView leftIcon;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(mLayoutResource, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.property = convertView.findViewById(R.id.text_card);
            viewHolder.leftIcon = convertView.findViewById(R.id.image_card_left);
            viewHolder.rightIcon = convertView.findViewById(R.id.image_card_right);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final String property = getItem(position);
        viewHolder.property.setText(property);

        assert property != null;
        if (property.contains("@")) {
            viewHolder.leftIcon.setImageResource(mContext.getResources()
                    .getIdentifier("@drawable/ic_email", null, mContext.getPackageName()));
            viewHolder.leftIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("plain/text");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{property});
                    mContext.startActivity(emailIntent);
                }
            });


        } else if ((property.length() != 0)) {
            viewHolder.leftIcon.setImageResource(mContext.getResources()
                    .getIdentifier("@drawable/ic_call", null, mContext.getPackageName()));
            viewHolder.rightIcon.setImageResource(mContext.getResources()
                    .getIdentifier("@drawable/ic_message", null, mContext.getPackageName()));
            viewHolder.leftIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  phone call
                    if (((MainActivity) mContext)
                            .checkPermission(new String[]{Manifest.permission.CALL_PHONE})) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", property, null));
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
                }
            });


            //SMS Message

            viewHolder.rightIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", property, null));
                    try {
                        (mContext).startActivity(smsIntent);
                    } catch (Exception e) {
                        Log.e(TAG, "" + e.getMessage());
                        Toast.makeText(getContext(), R.string.error_msg, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


        return convertView;
    }

}
