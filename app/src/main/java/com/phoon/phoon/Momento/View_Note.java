package com.phoon.phoon.Momento;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.phoon.phoon.Momento.ContactArrayAdapter.MY_PERMISSIONS_REQUEST_CALL_PHONE;
import static com.phoon.phoon.Momento.ContactArrayAdapter.MY_PERMISSIONS_REQUEST_SEND_SMS;

/**
 * Created by Hello on 24/3/2017.
 */

public class View_Note extends AppCompatActivity implements OnMapReadyCallback {
    SQLiteDatabase db;
    DbHelper dbHelper;
    long id=0;
    String Latitude;
    String Longitude;
    GoogleMap gmap;
    String StringUri;
    String Location;
    private ImageView ivImage;
    public static final int REQUEST_MANAGE_DOCUMENTS = 1;
    private TextView title;
    private TextView detail;
    private TextView notetype;
    private TextView time;
    private TextView date;
    private TextView contact;
    private TextView contactNum;
    private View contactLayout;
    private ImageView ivCall,ivSMS,ivShare;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewnote);
        id = getIntent().getExtras().getLong(getString(R.string.rowID));
        Log.i("This is create id", " " + id);

        ivImage = (ImageView) findViewById(R.id.TakenImage);
        ImageViewPopUpHelper.enablePopUpOnClick(this, ivImage);

        dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + dbHelper.TABLE_NAME + " where " + dbHelper.C_ID + "=" + id, null);

        title = (TextView) findViewById(R.id.title);
        detail = (TextView) findViewById(R.id.detail);
        notetype = (TextView) findViewById(R.id.note_type_ans);
        time = (TextView) findViewById(R.id.alertvalue);
        date = (TextView) findViewById(R.id.datevalue);
        contact = (TextView) findViewById(R.id.contact_name);
        contactNum = (TextView) findViewById(R.id.contact_num);
        contactLayout = (View) findViewById(R.id.contactview);
        ivCall = (ImageView) findViewById(R.id.callimageView);
        ivSMS = (ImageView) findViewById(R.id.messageImageView);
        ivShare = (ImageView) findViewById(R.id.shareImageView);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                title.setText(cursor.getString(cursor.getColumnIndex(dbHelper.TITLE)));
                detail.setText(cursor.getString(cursor.getColumnIndex(dbHelper.DETAIL)));
                notetype.setText(cursor.getString(cursor.getColumnIndex(dbHelper.TYPE)));
                time.setText(cursor.getString(cursor.getColumnIndex(dbHelper.TIME)));
                date.setText(cursor.getString(cursor.getColumnIndex(dbHelper.DATE)));
                Latitude = cursor.getString(cursor.getColumnIndex(dbHelper.LATITUDE));
                Longitude = cursor.getString(cursor.getColumnIndex(dbHelper.LONGITUDE));
                StringUri = cursor.getString(cursor.getColumnIndex(dbHelper.IMAGE));
                Location = cursor.getString(cursor.getColumnIndex(dbHelper.LOCATION_NAME));
                contact.setText(cursor.getString(cursor.getColumnIndex(dbHelper.CONTACT)));
                contactNum.setText(cursor.getString(cursor.getColumnIndex(dbHelper.CONTACT_NO)));
            }
            cursor.close();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        //If no location were set, Google Map do not need to be display.
        if (Latitude == null) {
            mapFragment.getView().setVisibility(View.GONE);
        }
        else
            mapFragment.getMapAsync(this);

        if(contact.getText().toString().trim().length() > 0){
            contactLayout.setVisibility(View.VISIBLE);
            final String selected_num = contactNum.getText().toString();

            ivCall.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    PackageManager pm = View_Note.this.getPackageManager();
                    int hasPerm = pm.checkPermission(
                            Manifest.permission.CALL_PHONE,
                            View_Note.this.getPackageName());
                    if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                        // do stuff
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + selected_num));
                        View_Note.this.startActivity(intent);
                    } else {
                        ActivityCompat.requestPermissions((Activity) View_Note.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                MY_PERMISSIONS_REQUEST_CALL_PHONE);
                    }
                }
            });

            ivSMS.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final PackageManager pm = View_Note.this.getPackageManager();
                    int hasPerm = pm.checkPermission(
                            Manifest.permission.SEND_SMS,
                            View_Note.this.getPackageName());
                    if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(View_Note.this);
                        View mView = layoutInflaterAndroid.inflate(R.layout.sms_dialog_box, null);
                        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(View_Note.this);
                        alertDialogBuilderUserInput.setView(mView);

//                        final String selected_num = contactNum.getText().toString();
                        final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
                        final TextView contactTextView = (TextView) mView.findViewById(R.id.dialogTitle);
                        contactTextView.setText(contact.getText().toString());
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
                        ActivityCompat.requestPermissions((Activity) View_Note.this,
                                new String[]{Manifest.permission.SEND_SMS},
                                MY_PERMISSIONS_REQUEST_SEND_SMS);
                    }
                }
            });

            ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String TextToShare = title.getText() + "\nDescription :\n" ;
                    if (Latitude != null){
                        String geoUri =  Location + "\n" + "http://maps.google.com/maps?q=loc:" + Latitude + "," + Longitude ;
                        TextToShare = title.getText() + "\nDescription :\n" + detail.getText() + "\nLocation : " + geoUri;
                    }

                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                    smsIntent.putExtra("sms_body", TextToShare);
                    smsIntent.putExtra("address", selected_num );
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    startActivity(smsIntent);
                }
            });
        }

        if(date.getText().toString().trim().length() > 0){
            date.setVisibility(View.VISIBLE);
        }

        if (StringUri != null){
            Uri uriSavedImage = Uri.parse(StringUri);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_DOCUMENTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MANAGE_DOCUMENTS}, REQUEST_MANAGE_DOCUMENTS);
                ivImage.setImageURI(uriSavedImage);
            }
            else {
                ivImage.setImageURI(uriSavedImage);

            }
            ivImage.setVisibility(View.VISIBLE);

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_viewnote, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //final long id = getIntent().getExtras().getLong(getString(R.string.rowID));
        String TextToShare = TextToShare = title.getText() + "\nDescription :\n" ;
        if (Latitude != null){
            String geoUri =  Location + "\n" + "http://maps.google.com/maps?q=loc:" + Latitude + "," + Longitude ;
            TextToShare = title.getText() + "\nDescription :\n" + detail.getText() + "\nLocation : " + geoUri;
        }

        switch (item.getItemId()) {
            case R.id.action_back:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_edit:

                Intent openEditNote = new Intent(View_Note.this, Edit_Note.class);
                openEditNote.putExtra(getString(R.string.rowID), id);
                startActivity(openEditNote);
                return true;

            case R.id.action_discard:
                AlertDialog.Builder builder = new AlertDialog.Builder(View_Note.this);
                builder
                        .setTitle(getString(R.string.delete_title))
                        .setMessage(getString(R.string.delete_message))
                        .setIcon(R.drawable.trash)
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                db.delete(DbHelper.TABLE_NAME, DbHelper.C_ID + "=" + id, null);
                                db.close();
                                finish();

                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), null)                        //Do nothing on no
                        .show();
                return true;
            case R.id.action_share_sms:
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.putExtra("sms_body", TextToShare);
                smsIntent.setType("vnd.android-dir/mms-sms");
                startActivity(smsIntent);
                return true;
            case R.id.action_share:
                if (StringUri != null){
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, TextToShare);
                sendIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse(StringUri));
                sendIntent.setType("image/*");
                sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(sendIntent, "Send to.."));
                }
                else{
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, TextToShare);
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, "Send to.."));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (Latitude != null && Longitude != null && !Latitude.isEmpty() && !Longitude.isEmpty()) {
            LatLng myLocation = new LatLng(Double.parseDouble(Latitude), Double.parseDouble(Longitude));
            gmap = googleMap;
            gmap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
            gmap.animateCamera(CameraUpdateFactory.zoomTo(15));
            gmap.addMarker(new MarkerOptions()
                    .position(myLocation).title(Location));
        }
    }
}
