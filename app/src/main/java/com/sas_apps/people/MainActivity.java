package com.sas_apps.people;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sas_apps.people.data.PeopleModel;
import com.sas_apps.people.dialog.SelectImageDialog;
import com.sas_apps.people.fragments.EditFragment;
import com.sas_apps.people.fragments.NewContactFragment;
import com.sas_apps.people.fragments.PeopleListFragment;
import com.sas_apps.people.fragments.ViewPersonFragment;
import com.sas_apps.people.imageLoader.UniversalImageLoader;



public class MainActivity extends AppCompatActivity implements
        PeopleListFragment.onPeopleSelectedListener,
        ViewPersonFragment.OnEditContactListener,
        PeopleListFragment.OnAddContactListener {

    private static final String TAG = "MainActivity";
    public static final int PERMISSION_REQUEST_CODE = 4;
    private static final String ACTION_QUICKSTART = "com.sas_apps.people.QUICKSTART";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initImageLoader();
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        if (ACTION_QUICKSTART.equals(getIntent().getAction())) {
            NewContactFragment newContactFragment = new NewContactFragment();
            manager.beginTransaction()
                    .replace(R.id.frameLayout, newContactFragment)
                    .commit();
        } else {
            PeopleListFragment peopleListFragment = new PeopleListFragment();
            manager.beginTransaction()
                    .replace(R.id.frameLayout, peopleListFragment)
                    .addToBackStack(null)
                    .commit();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SelectImageDialog.handleEvent(requestCode, resultCode, data);
    }

    @Override
    public void onEditContactSelected(PeopleModel people) {
        EditFragment editFragment = new EditFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(getString(R.string.key), people);
        editFragment.setArguments(bundle);
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frameLayout, editFragment)
                .addToBackStack(getString(R.string.edit_fragment))
                .commit();
    }

    public void verifyPermission(String[] permission) {
        ActivityCompat.requestPermissions(MainActivity.this, permission, PERMISSION_REQUEST_CODE);
    }

    public boolean checkPermission(String[] permission) {
        return ActivityCompat.checkSelfPermission(MainActivity.this, permission[0])
                == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "permission granted for " + permissions[i]);
                    } else {
                        break;
                    }
                }
                break;
        }
    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(MainActivity.this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public Bitmap compressBitmap(Bitmap bitmap, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return bitmap;
    }

    @Override
    public void onAddContact() {
        NewContactFragment fragment = new NewContactFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(getString(R.string.add_contact_fragment));
        transaction.commit();
    }

    @Override
    public void onPeopleSelected(PeopleModel people) {
        ViewPersonFragment viewPersonFragment = new ViewPersonFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(getString(R.string.key), people);
        viewPersonFragment.setArguments(bundle);
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frameLayout, viewPersonFragment)
                .addToBackStack(getString(R.string.view_fragment))
                .commit();
    }
}
