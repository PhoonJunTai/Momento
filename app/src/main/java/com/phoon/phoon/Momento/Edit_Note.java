package com.phoon.phoon.Momento;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.phoon.phoon.Momento.R.id.contactLayout;
import static com.phoon.phoon.Momento.R.id.textView2;
import static com.phoon.phoon.Momento.View_Note.REQUEST_MANAGE_DOCUMENTS;

/**
 * Created by Hello on 25/3/2017.
 */

public class Edit_Note extends AppCompatActivity {
    SQLiteDatabase db;
    DbHelper mDbHelper;
    EditText mTitleText;
    EditText mDescriptionText;
    Spinner mSpinner;
    DatePicker pickerDate;
    TimePicker pickerTime;
    TextView time;
    TextView date;
    CheckBox checkBoxAlarm;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int RESULT_PICK_CONTACT = 8;
    private TextView mName;
    private TextView mAddress;
    private ImageView ivImage;
    private TextView mContact,mContactNum;
    private View mContactLayout;
    String Latitude;
    String Longitude;
    private String userChoosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 2;
    Uri uriSavedImage = null;
    Menu myMenu;
    Calendar calender;
    String StringUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editnote);
        mDbHelper = new DbHelper(this);
        db = mDbHelper.getWritableDatabase();

        mTitleText = (EditText) findViewById(R.id.txttitle);
        mDescriptionText = (EditText) findViewById(R.id.description);
        mSpinner = (Spinner) findViewById(R.id.spinnerNoteType);
        pickerDate = (DatePicker) findViewById(R.id.datePicker);
        pickerTime = (TimePicker) findViewById(R.id.timePicker);
        time = (TextView) findViewById(R.id.txtTime);
        date = (TextView) findViewById(R.id.txtDate);
        checkBoxAlarm = (CheckBox) findViewById(R.id.checkBox);
        mName = (TextView) findViewById(R.id.textView);
        mAddress = (TextView) findViewById(textView2);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        ImageViewPopUpHelper.enablePopUpOnClick(this, ivImage);
        mContact = (TextView) findViewById(R.id.contactNameTextView);
        mContactNum = (TextView) findViewById(R.id.contactNumberTextView);
        mContactLayout = (View) findViewById(contactLayout);

        mDescriptionText.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });

        final long id = getIntent().getExtras().getLong(getString(R.string.rowID));

        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this, R.array.note_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView parent, View view, int position, long id) {
                        if (id == 2) {
                            showToast(getString(R.string.alarm_active));
                            checkBoxAlarm.setEnabled(true);
                        }
                        else {
                            checkBoxAlarm.setEnabled(false);
                            checkBoxAlarm.setChecked(false);
                        }
                    }

                    public void onNothingSelected(AdapterView parent) {

                    }
                });

        Cursor cursor = db.rawQuery("select * from " + mDbHelper.TABLE_NAME + " where " + mDbHelper.C_ID + "=" + id, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                mTitleText.setText(cursor.getString(cursor.getColumnIndex(mDbHelper.TITLE)));
                mDescriptionText.setText(cursor.getString(cursor.getColumnIndex(mDbHelper.DETAIL)));
                if (cursor.getString(cursor.getColumnIndex(mDbHelper.TYPE)).equals(mSpinner.getItemAtPosition(0))){
                    mSpinner.setSelection(0);
                    checkBoxAlarm.setChecked(false);
                    checkBoxAlarm.setEnabled(false);
                    pickerDate.setVisibility(View.GONE);
                    pickerTime.setVisibility(View.GONE);
                    time.setVisibility(View.GONE);
                    date.setVisibility(View.GONE);
                }
                else if (cursor.getString(cursor.getColumnIndex(mDbHelper.TYPE)).equals(mSpinner.getItemAtPosition(1))){
                    mSpinner.setSelection(1);
                    checkBoxAlarm.setChecked(false);
                    checkBoxAlarm.setEnabled(false);
                    pickerDate.setVisibility(View.GONE);
                    pickerTime.setVisibility(View.GONE);
                    time.setVisibility(View.GONE);
                    date.setVisibility(View.GONE);
                }
                else if (cursor.getString(cursor.getColumnIndex(mDbHelper.TYPE)).equals(mSpinner.getItemAtPosition(2))) {
                    mSpinner.setSelection(2);
                    checkBoxAlarm.setChecked(true);
                    checkBoxAlarm.setEnabled(true);

                }
                if (cursor.getString(cursor.getColumnIndex(mDbHelper.TIME)).toString().equals(getString(R.string.Not_Set_Alert))) {
                    checkBoxAlarm.setChecked(false);
                    pickerDate.setVisibility(View.GONE);
                    pickerTime.setVisibility(View.GONE);
                    time.setVisibility(View.GONE);
                    date.setVisibility(View.GONE);

                }

                Latitude = cursor.getString(cursor.getColumnIndex(mDbHelper.LATITUDE));
                Longitude = cursor.getString(cursor.getColumnIndex(mDbHelper.LONGITUDE));
                StringUri = cursor.getString(cursor.getColumnIndex(mDbHelper.IMAGE));
                mName.setText(cursor.getString(cursor.getColumnIndex(mDbHelper.LOCATION_NAME)));
                mAddress.setText(cursor.getString(cursor.getColumnIndex(mDbHelper.ADDRESS)));
                mContact.setText(cursor.getString(cursor.getColumnIndex(mDbHelper.CONTACT)));
                mContactNum.setText(cursor.getString(cursor.getColumnIndex(mDbHelper.CONTACT_NO)));

            }
            cursor.close();
        }

        if (StringUri != null){
            Toast.makeText(this, "Image " + StringUri, Toast.LENGTH_LONG).show();
            Uri uriSavedImage = Uri.parse(StringUri);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_DOCUMENTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MANAGE_DOCUMENTS}, REQUEST_MANAGE_DOCUMENTS);
                ivImage.setImageURI(uriSavedImage);
            }
            else {
                Toast.makeText(this, "Image " + StringUri, Toast.LENGTH_LONG).show();
//                ivImage.setImageBitmap(BitmapFactory.decodeFile(uriSavedImage.toString()));
                ivImage.setImageURI(uriSavedImage);

            }
            ivImage.setVisibility(View.VISIBLE);
//            ivImage.setImageURI(uriSavedImage);
//            ivImage.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(uriSavedImage.toString()),600,300,true));


        }

        if(mContact.getText().toString().trim().length() > 0){
            mContactLayout.setVisibility(View.VISIBLE);
        }

        if (Latitude != null) {
            mName.setVisibility(View.VISIBLE);
            mAddress.setVisibility(View.VISIBLE);
        }

        final BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        View viewTab = bottomBar.getCurrentTab();
        View parentView = (View) viewTab.getParent();
        ViewGroup mItemContainer = (ViewGroup) parentView.findViewById(com.roughike.bottombar.R.id.bb_bottom_bar_item_container);

        for (int i = 0; i < mItemContainer.getChildCount(); i++) {
            View viewItem = mItemContainer.getChildAt(i);
            //TITLE
            TextView titleTab = (TextView) viewItem.findViewById(com.roughike.bottombar.R.id.bb_bottom_bar_title);
            titleTab.setVisibility(View.GONE);
            //ICON
            AppCompatImageView icon = (AppCompatImageView) viewItem.findViewById(com.roughike.bottombar.R.id.bb_bottom_bar_icon);
            icon.setY(6);

        }

        bottomBar.setDefaultTabPosition(3);

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId){
                    case R.id.tab_nearby:
                        try {
                            PlacePicker.IntentBuilder intentBuilder =
                                    new PlacePicker.IntentBuilder();
                            Intent intent = intentBuilder.build(Edit_Note.this);
                            startActivityForResult(intent, PLACE_PICKER_REQUEST);

                        } catch (GooglePlayServicesRepairableException
                                | GooglePlayServicesNotAvailableException e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.tab_info:
                        Toast.makeText(Edit_Note.this, "Edit your note" , Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.tab_contact:
                        pickContact();
                        break;
                    case R.id.tab_photo:
                        final CharSequence[] items = { "Take Photo", "Choose from Library",
                                "Cancel" };

                        AlertDialog.Builder builder = new AlertDialog.Builder(Edit_Note.this);
                        builder.setTitle("Add Photo!");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                boolean result=Utility.checkPermission(Edit_Note.this);

                                if (items[item].equals("Take Photo")) {
                                    userChoosenTask ="Take Photo";
                                    if(result)
                                        cameraIntent();

                                } else if (items[item].equals("Choose from Library")) {
                                    userChoosenTask ="Choose from Library";
                                    if(result)
                                        galleryIntent();

                                } else if (items[item].equals("Cancel")) {
                                    dialog.dismiss();
                                }
                            }
                        });
                        builder.show();
//                        bottomBar.clearFocus();
//                        bottomBar.setSelected(false);
//                        bottomBar.setDefaultTabPosition(3);
                        break;
                    default:
                        break;
                }
            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                switch (tabId){
                    case R.id.tab_nearby:
                        try {
                            PlacePicker.IntentBuilder intentBuilder =
                                    new PlacePicker.IntentBuilder();
//                            intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
                            Intent intent = intentBuilder.build(Edit_Note.this);
                            startActivityForResult(intent, PLACE_PICKER_REQUEST);

                        } catch (GooglePlayServicesRepairableException
                                | GooglePlayServicesNotAvailableException e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.tab_info:
                        Toast.makeText(Edit_Note.this, "Edit your note", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.tab_contact:
                        pickContact();
                        break;
                    case R.id.tab_photo:
                        final CharSequence[] items = { "Take Photo", "Choose from Library",
                                "Cancel" };

                        AlertDialog.Builder builder = new AlertDialog.Builder(Edit_Note.this);
                        builder.setTitle("Add Photo!");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                boolean result=Utility.checkPermission(Edit_Note.this);

                                if (items[item].equals("Take Photo")) {
                                    userChoosenTask ="Take Photo";
                                    if(result)
                                        cameraIntent();

                                } else if (items[item].equals("Choose from Library")) {
                                    userChoosenTask ="Choose from Library";
                                    if(result)
                                        galleryIntent();

                                } else if (items[item].equals("Cancel")) {
                                    dialog.dismiss();
                                }
                            }
                        });
                        builder.show();
                        break;
                    default:
                        bottomBar.setSelected(false);
                        bottomBar.clearFocus();
                        break;
                }
            }
        });

        mTitleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mTitleText.getText().toString().length()>0) {
                    myMenu.findItem(R.id.action_save)
                            .setVisible(true);
                    myMenu.findItem(R.id.action_save)
                            .setEnabled(true);
                    myMenu.findItem(R.id.action_back)
                            .setVisible(false);
                    myMenu.findItem(R.id.action_back)
                            .setEnabled(false);
                }
                else {
                    myMenu.findItem(R.id.action_save)
                            .setVisible(false);
                    myMenu.findItem(R.id.action_save)
                            .setEnabled(false);
                    myMenu.findItem(R.id.action_back)
                            .setVisible(true);
                    myMenu.findItem(R.id.action_back)
                            .setEnabled(true);
                }
            }
        });



        checkBoxAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    pickerDate.setVisibility(View.VISIBLE);
                    pickerTime.setVisibility(View.VISIBLE);
                    time.setVisibility(View.VISIBLE);
                    date.setVisibility(View.VISIBLE);
                } else {
                    pickerDate.setVisibility(View.GONE);
                    pickerTime.setVisibility(View.GONE);
                    time.setVisibility(View.GONE);
                    date.setVisibility(View.GONE);
                }
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        Intent setIntent = new Intent(this, MainActivity.class);
//        startActivity(setIntent);
//    }

    private void pickContact()
    {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);

    }

    private void galleryIntent()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        //folder stuff
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "MyImages");
        imagesFolder.mkdirs();

        File image = new File(imagesFolder, "QR_" + timeStamp + ".png");
        uriSavedImage = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", image);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    void showToast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_note, menu);
        myMenu = menu;
        myMenu.findItem(R.id.action_save)
                .setVisible(true);
        myMenu.findItem(R.id.action_save)
                .setEnabled(true);
        myMenu.findItem(R.id.action_back)
                .setVisible(false);
        myMenu.findItem(R.id.action_back)
                .setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_back:
                Intent openMainActivity = new Intent(this, MainActivity.class);
                startActivity(openMainActivity);
                return true;
            case R.id.action_save:
                final long id = getIntent().getExtras().getLong(getString(R.string.rowID));
                String title = mTitleText.getText().toString();
                String detail = mDescriptionText.getText().toString();
                String type = mSpinner.getSelectedItem().toString();
                String address = mAddress.getText().toString();
                String contact = mContact.getText().toString();
                String contactNum = mContactNum.getText().toString();
                String location = mName.getText().toString();
                ContentValues cv = new ContentValues();
                cv.put(mDbHelper.TITLE, title);
                cv.put(mDbHelper.DETAIL, detail);
                cv.put(mDbHelper.TYPE, type);
                cv.put(mDbHelper.TIME, getString(R.string.Not_Set));
                cv.putNull(mDbHelper.DATE);
                cv.put(mDbHelper.LOCATION_NAME, location);
                cv.put(mDbHelper.ADDRESS, address);
                cv.put(mDbHelper.LATITUDE,Latitude);
                cv.put(mDbHelper.LONGITUDE,Longitude);
                cv.put(mDbHelper.CONTACT,contact);
                cv.put(mDbHelper.CONTACT_NO, contactNum);

                if (null!=uriSavedImage) {
                    String imageuri = uriSavedImage.toString();
                    cv.put(mDbHelper.IMAGE, imageuri);
                }

                if (checkBoxAlarm.isChecked()){
                    calender = Calendar.getInstance();
                    calender.clear();
                    calender.set(Calendar.MONTH, pickerDate.getMonth());
                    calender.set(Calendar.DAY_OF_MONTH, pickerDate.getDayOfMonth());
                    calender.set(Calendar.YEAR, pickerDate.getYear());
                    calender.set(Calendar.HOUR, pickerTime.getCurrentHour());
                    calender.set(Calendar.MINUTE, pickerTime.getCurrentMinute());
                    calender.set(Calendar.SECOND, 00);

                    SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.hour_minutes));
                    String timeString = formatter.format(new Date(calender.getTimeInMillis()));
                    SimpleDateFormat dateformatter = new SimpleDateFormat(getString(R.string.dateformate));
                    String dateString = dateformatter.format(new Date(calender.getTimeInMillis()));


                    cv.put(mDbHelper.TIME, timeString);
                    cv.put(mDbHelper.DATE, dateString);

                }

                db.update(mDbHelper.TABLE_NAME, cv, mDbHelper.C_ID + "=" + id, null);
                int reqCode = (int) id;

                if (calender != null){
                    AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(this, AlarmReceiver.class);

                    String alertTitle = mTitleText.getText().toString();
                    String alertDescription = mDescriptionText.getText().toString();
                    intent.putExtra(getString(R.string.alert_title), alertTitle);
                    intent.putExtra("description", alertDescription);
                    intent.putExtra("rowid",id);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this,reqCode, intent, 0);

                    alarmMgr.set(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(), pendingIntent);
                }

                Intent openMainScreen = new Intent(Edit_Note.this, MainActivity.class);
                openMainScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(openMainScreen);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        Toast.makeText(this, " Result code is " + requestCode, Toast.LENGTH_SHORT).show();

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == PLACE_PICKER_REQUEST) {

                final Place place = PlacePicker.getPlace(data, this);
                final CharSequence name = place.getName();
                final CharSequence address = place.getAddress();
                Latitude = String.valueOf(place.getLatLng().latitude);
                Longitude = String.valueOf(place.getLatLng().longitude);

                mName.setText(name);
                mAddress.setText(address);
                mName.setVisibility(View.VISIBLE);
                mAddress.setVisibility(View.VISIBLE);

            }
            else if (requestCode == SELECT_FILE) {
                uriSavedImage = data.getData();
                ivImage.setImageURI(uriSavedImage);
                ivImage.setVisibility(View.VISIBLE);

            }
            else if (requestCode == REQUEST_CAMERA) {
                ivImage.setImageURI(uriSavedImage);
                ivImage.setVisibility(View.VISIBLE);
            }
            else if (requestCode == RESULT_PICK_CONTACT){
                Cursor cursor = null;
                try {

                    String phoneNo = null ;
                    String name = null;
                    Uri uri = data.getData();
                    cursor = getContentResolver().query(uri, null, null, null, null);
                    cursor.moveToFirst();

                    int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

                    phoneNo = cursor.getString(phoneIndex);
                    name = cursor.getString(nameIndex);

                    mContact.setText(name);
                    mContactNum.setText(phoneNo);
                    mContactLayout.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }

    }

}
