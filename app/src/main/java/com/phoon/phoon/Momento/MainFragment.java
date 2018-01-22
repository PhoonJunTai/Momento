package com.phoon.phoon.Momento;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * Created by Hello on 22/3/2017.
 */

public class MainFragment extends Fragment {

    SQLiteDatabase db;
    DbHelper mDbHelper;
    ListView list;
    ImageView alarmImage;
    SimpleCursorAdapter adapter;
    Cursor cursor;

    // Empty public constructor, required by the system
    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main,
                container, false);

        list = (ListView) view.findViewById(R.id.commentslist);
        mDbHelper = new DbHelper(getContext());
        db = mDbHelper.getWritableDatabase();
        alarmImage = (ImageView) view.findViewById(R.id.alarmImage);

        String[] from = {mDbHelper.TITLE, mDbHelper.LOCATION_NAME, mDbHelper.TYPE, mDbHelper.TIME, mDbHelper.DATE};
        final String[] column = {mDbHelper.C_ID, mDbHelper.TITLE, mDbHelper.DETAIL, mDbHelper.TYPE, mDbHelper.TIME, mDbHelper.DATE,
                mDbHelper.LOCATION_NAME, mDbHelper.ADDRESS, mDbHelper.LATITUDE, mDbHelper.LONGITUDE, mDbHelper.IMAGE,
                mDbHelper.CONTACT, mDbHelper.CONTACT_NO};
        int[] to = {R.id.title, R.id.location, R.id.type, R.id.time, R.id.date};

        cursor = db.query(mDbHelper.TABLE_NAME, column, null, null, null, null, null);
        adapter = new SimpleCursorAdapter(getActivity(), R.layout.list_entry, cursor, from, to, 0);

        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> listView, View view, int position,
                                    long id) {
                Intent intent = new Intent(getActivity(), View_Note.class);
                intent.putExtra(getString(R.string.rowID), id);
                startActivity(intent);
            }

        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                final CharSequence[] items = {"Edit", "Delete",
                        "Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.pick_action));
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        boolean result = Utility.checkPermission(getContext());

                        if (items[item].equals("Edit")) {
                            Intent editIntent = new Intent(getActivity(), Edit_Note.class);
                            editIntent.putExtra(getString(R.string.rowID), id);
                            startActivity(editIntent);
                        } else if (items[item].equals("Delete")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder
                                    .setTitle(getString(R.string.delete_title))
                                    .setMessage(getString(R.string.delete_message))
                                    .setIcon(R.drawable.trash)
                                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            db.delete(DbHelper.TABLE_NAME, DbHelper.C_ID + "=" + id, null);
                                            db.close();
                                            Intent refresh = new Intent(getActivity(), MainActivity.class);
                                            startActivity(refresh);
                                            getActivity().finish();
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.cancel), null)                        //Do nothing on no
                                    .show();
                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
                return true;
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        adapter.getCursor().requery();

    }

}
