package com.example.tl0103;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ListaAdapter extends CursorAdapter {


    public ListaAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);

    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.list_contactos, viewGroup, false);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final ViewHolder viewHolder = new ViewHolder();

        try {

            viewHolder.item1 = (TextView) view.findViewById(R.id.text1);
            viewHolder.item1.setText(cursor.getString(cursor.getColumnIndexOrThrow("Nombre"))+" | "+cursor.getString(cursor.getColumnIndexOrThrow("Telefono")));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class ViewHolder {
        TextView item1;

    }
}
