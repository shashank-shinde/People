package com.sas_apps.people.data;
/*
 * Created by Shashank Shinde.
 */

import android.os.Parcel;
import android.os.Parcelable;

public class PeopleModel implements Parcelable {

    private String name;
    private String phoneNumber;
    private String device;
    private String email;
    private String profileImage;

    public PeopleModel(String name, String phoneNumber, String device, String email, String profileImage) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.device = device;
        this.email = email;
        this.profileImage = profileImage;
    }


    private PeopleModel(Parcel in) {
        name = in.readString();
        phoneNumber = in.readString();
        device = in.readString();
        email = in.readString();
        profileImage = in.readString();
    }

    public static final Creator<PeopleModel> CREATOR = new Creator<PeopleModel>() {
        @Override
        public PeopleModel createFromParcel(Parcel in) {
            return new PeopleModel(in);
        }

        @Override
        public PeopleModel[] newArray(int size) {
            return new PeopleModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phoneNumber);
        dest.writeString(device);
        dest.writeString(email);
        dest.writeString(profileImage);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getphoneNumber() {
        return phoneNumber;
    }

    public void setphoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", phonenumber='" + phoneNumber + '\'' +
                ", device='" + device + '\'' +
                ", email='" + email + '\'' +
                ", profileImage='" + profileImage + '\'' +
                '}';
    }
}
