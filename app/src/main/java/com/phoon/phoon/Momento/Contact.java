package com.phoon.phoon.Momento;

import android.support.annotation.NonNull;

import java.util.Comparator;

/**
 * Created by Hello on 22/3/2017.
 */

public class Contact implements Comparable{
    private long contact_id;
    private String Name;
    private String Avatar;
    private String Phone;
    private String Email;

    public Contact(long contact_id, String Name, String Avatar, String Phone) {
        this.contact_id = contact_id;
        this.Name = Name;
        this.Avatar = Avatar;
        //this.Email = Email;
        this.Phone = Phone;
    }

    public String getAvatar() {
        return Avatar;
    }
    public String getName() {
        return Name;
    }
    public String getPhone() {
        return Phone;
    }
//    public String getEmail() {
//        return Email;
//    }
    public void setAvatar(String mAvatar) {
        this.Avatar = mAvatar;
    }
    public void setName(String mName) {
        this.Name = mName;
    }
    public void setPhone(String mPhone) {
        this.Phone = mPhone;
    }
    public void setEmail(String mEmail) {
        this.Email = mEmail;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return 0;
    }

    public static Comparator<Contact> ContactNameComparator
            = new Comparator<Contact>() {

        public int compare(Contact contact1, Contact contact2) {

            String contactName1 = contact1.getName().toUpperCase();
            String contactName2 = contact2.getName().toUpperCase();

            //ascending order
            return contactName1.compareTo(contactName2);

        }

    };
}
