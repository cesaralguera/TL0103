package com.example.tl0103;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ListaContactos extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private ListView lista;
    private DataBaseContact dataBaseContact;
    private ListaAdapter adapter;
    private Cursor cursor;
    private Button btneliminar, btncompartir;
    private int selectedPosition = ListView.INVALID_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contactos);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
        }

        lista = (ListView) findViewById(R.id.list);
        btneliminar = findViewById(R.id.btneliminar);
        btncompartir = findViewById(R.id.btncompartir);
        dataBaseContact = DataBaseContact.getInstance(this);

        if (savedInstanceState != null) {
            selectedPosition = savedInstanceState.getInt("selectedPosition");
        }

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                cursor.moveToPosition(i);
                selectedPosition = i;

                AlertaDialog(cursor.getString(cursor.getColumnIndexOrThrow("Nombre")), cursor.getString(cursor.getColumnIndexOrThrow("Telefono")));
            }
        });

        btneliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    cursor.moveToPosition(selectedPosition);
                    dataBaseContact.EjecutarConsulta("delete from Contactos where Nombre = '" + cursor.getString(cursor.getColumnIndexOrThrow("Nombre")) + "' and Telefono = '" + cursor.getString(cursor.getColumnIndexOrThrow("Telefono")) + "'");

                    Toast.makeText(ListaContactos.this, "Contacto eliminado.", Toast.LENGTH_SHORT).show();
                    LlenarContactos("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        btncompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cursor.moveToPosition(selectedPosition);

                String contacto = "BEGIN:VCARD\n" +
                        "VERSION:3.0\n" +
                        "N:" + cursor.getString(cursor.getColumnIndexOrThrow("Nombre")) + ";;;;\n" +
                        "TEL;TYPE=CELL:" + cursor.getString(cursor.getColumnIndexOrThrow("Telefono")) + "\n" +
                        "END:VCARD";

                // Crear un Intent para compartir el contacto
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/x-vcard");
                intent.putExtra(Intent.EXTRA_TEXT, contacto);

                startActivity(Intent.createChooser(intent, "Compartir contacto"));
            }
        });


        LlenarContactos("");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedPosition", lista.getSelectedItemPosition());
    }

    private void AlertaDialog(String nombre, String telefono) {

        new AlertDialog.Builder(this).setTitle("Accion")
                .setCancelable(false)
                .setMessage("Desea llamar a " + nombre)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        try {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + telefono));
                            startActivity(callIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).show();

    }


    public void LlenarContactos(String busqueda) {
        try {

            busqueda = !busqueda.equals("") ? " where Nombre like '" + busqueda + "%'" : "";
            String selectQuery = "SELECT _ID, Nombre,Telefono FROM Contactos " + busqueda;

            cursor = dataBaseContact.getDatosContactos(selectQuery);

            if (cursor == null) {
                Toast.makeText(this, "No hay Información.", Toast.LENGTH_LONG).show();
            } else {
                adapter = new ListaAdapter(this, cursor);
                lista.setAdapter(adapter);
                if (selectedPosition != ListView.INVALID_POSITION) {
                    lista.setSelection(selectedPosition);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuit = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuit.getActionView();
        searchView.setQueryHint("Buscar...");
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        LlenarContactos(query.trim());
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        LlenarContactos(newText.trim());
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}