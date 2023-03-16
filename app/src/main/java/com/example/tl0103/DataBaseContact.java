package com.example.tl0103;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

import java.util.List;

public class DataBaseContact extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Contactos";
    private static final int DATABASE_VERSION = 1;
    private static SQLiteDatabase db;
    private static DataBaseContact sInstance;

    public DataBaseContact(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();
    }


    public static synchronized DataBaseContact getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DataBaseContact(context.getApplicationContext());
        }

        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void createTable(SQLiteDatabase db) {


        String queryTabla = " CREATE TABLE Contactos(  " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Telefono Text ," +
                "Nombre Text," +
                "Pais Text," +
                "Nota Text," +
                "Imagen Text )";

        db.execSQL(queryTabla);
    }


    public void SalvarContactosDb(Contactos contactos) {

        db = this.getWritableDatabase();
        db.beginTransaction();

        String sqlQuery = "insert or replace into Contactos(Telefono,Nombre,Pais,Nota,Imagen) values (?,?,?,?,?)";

        SQLiteStatement statement = db.compileStatement(sqlQuery);


        statement.bindString(1, contactos.getTelefono());
        statement.bindString(2, contactos.getNombre());
        statement.bindString(3, contactos.getPais());
        statement.bindString(4, contactos.getNota());
        statement.bindBlob(5, contactos.getImagen());


        try {
            statement.executeInsert();
            statement.clearBindings();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

    }

    public Cursor getDatosContactos(String query) {
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        try {
            if (cursor == null) {
                return null;
            } else if (!cursor.moveToFirst()) {
                cursor.close();
                return null;
            }

            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        return null;
    }


    public void EjecutarConsulta(String query){
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            db.execSQL(query);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
