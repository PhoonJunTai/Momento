package com.phoon.phoon.Momento;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.provider.ContactsContract.CommonDataKinds.*;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import static com.phoon.phoon.Momento.ContactArrayAdapter.MY_PERMISSIONS_REQUEST_CALL_PHONE;
import static com.phoon.phoon.Momento.ContactArrayAdapter.MY_PERMISSIONS_REQUEST_SEND_SMS;

/**
 * Created by Hello on 22/3/2017.
 */

public class ContactFragment extends Fragment {

    private static final int REQUEST_READ_CONTACTS = 1;
    public static ListView contactsname;
    public static Cursor cursor1;
    ArrayList<Contact> contactList;

    // Empty public constructor, required by the system
    public ContactFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        contactList = new ArrayList<Contact>();

        View view = inflater.inflate(R.layout.fragment_contact,
                container, false);
        contactsname = (ListView) view.findViewById(R.id.listViewContact);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS);
        } else {
            loadContact();
        }

        return view;
    }

    public void loadContact() {

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        String phonenumber = "";

        CursorLoader contactsCursorLoader = new CursorLoader(getContext(),
                ContactsContract.Contacts.CONTENT_URI,
                null,
                ContactsContract.Contacts.HAS_PHONE_NUMBER + ">=1",
                null,
                null);

        Cursor contactsCursor = contactsCursorLoader.loadInBackground();
        if (contactsCursor.moveToFirst()) {
            while (!contactsCursor.isAfterLast()) {

                Context context = null;
                //contact list
                ContentResolver cr = getContext().getContentResolver();

                //get the id
                long contact_id = contactsCursor.getLong(
                        contactsCursor.getColumnIndex(ContactsContract.Contacts._ID));

                String cid = contactsCursor.getString(
                        contactsCursor.getColumnIndex(ContactsContract.Contacts._ID));

                //get the name
                String name = contactsCursor.getString(
                        contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));

                String photoUri = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Data.PHOTO_THUMBNAIL_URI));

                //int hasPhoneNumber = Integer.parseInt(contactsCursor.getString(contactsCursor.getColumnIndex(HAS_PHONE_NUMBER)));
                Cursor phoneCursor = cr.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{cid}, null);

                // Query and loop for every phone number of the contact
                phoneCursor.moveToNext();
                int type = phoneCursor.getInt(phoneCursor.getColumnIndex(Phone.TYPE));
                if (type == Phone.TYPE_MOBILE) {
                    //Log.i("Type work","working");
                    phonenumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                    phoneCursor.close();

                } else {

                }
                phoneCursor.close();
                contactList.add(new Contact(contact_id, name, photoUri, phonenumber));
                contactsCursor.moveToNext();
            }
        }
        if (!contactList.isEmpty()) {
            Collections.sort(contactList, Contact.ContactNameComparator);
            contactsname.setAdapter(new ContactArrayAdapter(getContext(), contactList));
        }
    }

@Override
public void onRequestPermissionsResult(int requestCode,
                                       String permissions[], int[] grantResults) {
    switch (requestCode) {
        case MY_PERMISSIONS_REQUEST_CALL_PHONE: {

            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(getActivity(), "Permission denied to read your make phone call", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        case MY_PERMISSIONS_REQUEST_SEND_SMS: {

            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {

                Toast.makeText(getActivity(), "Permission denied to send SMS", Toast.LENGTH_SHORT).show();
            }
            return;
        }

    }
}

}
