package com.phoon.phoon.Momento;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Hello on 22/3/2017.
 */

public class ContactArrayAdapter extends ArrayAdapter<Contact> {

    Context mContext;
    ArrayList<Contact> mObject;
    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 223;
    public static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 224;

    public ContactArrayAdapter(Context context, ArrayList<Contact> objects) {
        super(context, 0, objects);
        mContext = context;
        mObject = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Bitmap bitmap;
        Bitmap circularBitmap;

        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.contacts_list_item, parent, false);

        final Contact contact = getItem(position);

        //Contact name setting
        TextView nameTextView = (TextView) view.findViewById(R.id.text1);
        nameTextView.setText(contact.getName());

        ImageView imgv = (ImageView) view.findViewById(R.id.image1);

        try {
            if (contact.getAvatar() != null) {
                Uri my_contact_Uri = Uri.parse(contact.getAvatar());
                if (my_contact_Uri != null) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(view.getContext().getContentResolver(), my_contact_Uri);
                        circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 100);
                        imgv.setImageBitmap(circularBitmap);
                    } catch (Exception e) {
                        imgv.setImageResource(R.drawable.man);
                    }
                }


            } else {
                imgv.setImageResource(R.drawable.man);
            }

        } catch (Exception e) {
            imgv.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.person));
        }


        ImageView callimageicon = (ImageView) view.findViewById(R.id.callimageView);
        callimageicon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String selected_num = contact.getPhone();
                PackageManager pm = mContext.getPackageManager();
                int hasPerm = pm.checkPermission(
                        Manifest.permission.CALL_PHONE,
                        mContext.getPackageName());
                if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                    // do stuff
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + selected_num));
                    mContext.startActivity(intent);
                } else {
                    ActivityCompat.requestPermissions((Activity) mContext,
                            new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE);
                }
            }
        });

        ImageView messageimageicon = (ImageView) view.findViewById(R.id.messageImageView);
        messageimageicon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final PackageManager pm = mContext.getPackageManager();
                int hasPerm = pm.checkPermission(
                        Manifest.permission.SEND_SMS,
                        mContext.getPackageName());
                if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                    LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mContext);
                    View mView = layoutInflaterAndroid.inflate(R.layout.sms_dialog_box, null);
                    AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(mContext);
                    alertDialogBuilderUserInput.setView(mView);

                    final String selected_num = contact.getPhone();
                    final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
                    final TextView contactTextView = (TextView) mView.findViewById(R.id.dialogTitle);
                    contactTextView.setText(contact.getName());
                    alertDialogBuilderUserInput
                            .setCancelable(false)
                            .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogBox, int id) {
                                    String sms = userInputDialogEditText.getText().toString();
                                    SmsManager smsManager = SmsManager.getDefault();
                                    smsManager.sendTextMessage(selected_num, null, sms, null, null);

                                }
                            })

                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogBox, int id) {
                                            dialogBox.cancel();
                                        }
                                    });

                    AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                    alertDialogAndroid.show();
                }// end permission if
                else {
                    ActivityCompat.requestPermissions((Activity) mContext,
                            new String[]{Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
            }
        });

        return view;
    }

}
