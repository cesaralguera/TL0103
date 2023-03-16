package com.example.tl0103;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button btnSalvar, btnListaC;
    private Spinner spPaises;
    private ImageView imageView;
    private ImageButton imageButton;
    private DataBaseContact dataBaseContact;
    private static MainActivity sActivity = null;
    private EditText editTextNombre, editTextTelefono, editTextNota;
    private byte[] byteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnSalvar = findViewById(R.id.btnSalvar);
        btnListaC = findViewById(R.id.btnContactos);
        editTextTelefono = findViewById(R.id.editTextPhone);
        editTextNombre = findViewById(R.id.editTextTextPersonName);
        editTextNota = findViewById(R.id.editTextTextMultiLine);
        imageButton = findViewById(R.id.imageButton);
        imageView = findViewById(R.id.imageView);
        spPaises = findViewById(R.id.spPais);
        sActivity = this;

        String[] myData = {"Afganistán (93)", "Albania (355)", "Alemania (49)", "Andorra (376)", "Angola (244)", "Antigua y Barbuda (1-268)", "Arabia Saudita (966)", "Argelia (213)", "Argentina (54)", "Armenia (374)", "Australia (61)", "Austria (43)", "Azerbaiyán (994)", "Bahamas (1-242)", "Bangladés (880)", "Barbados (1-246)", "Baréin (973)", "Bélgica (32)", "Belice (501)", "Benín (229)", "Bielorrusia (375)", "Birmania (95)", "Bolivia (591)", "Bosnia y Herzegovina (387)", "Botsuana (267)", "Brasil (55)", "Brunéi (673)", "Bulgaria (359)", "Burkina Faso (226)", "Burundi (257)", "Bután (975)", "Cabo Verde (238)", "Camboya (855)", "Camerún (237)", "Canadá (1)", "Catar (974)", "Chad (235)", "Chile (56)", "China (86)", "Chipre (357)", "Colombia (57)", "Comoras (269)", "Corea del Norte (850)", "Corea del Sur (82)", "Costa de Marfil (225)", "Costa Rica (506)", "Croacia (385)", "Cuba (53)", "Dinamarca (45)", "Dominica (1-767)", "Ecuador (593)", "Egipto (20)", "El Salvador (503)", "Emiratos Árabes Unidos (971)", "Eritrea (291)", "Eslovaquia (421)", "Eslovenia (386)", "España (34)", "Estados Unidos (1)", "Estonia (372)", "Etiopía (251)", "Filipinas (63)", "Finlandia (358)", "Fiyi (679)", "Francia (33)", "Gabón (241)", "Gambia (220)", "Georgia (995)", "Ghana (233)", "Granada (1-473)", "Grecia (30)", "Guatemala (502)", "Guinea (224)", "Guinea-Bisáu (245)", "Guinea Ecuatorial (240)", "Guyana (592)", "Haití (509)", "Honduras (504)", "Hungría (36)", "India (91)", "Indonesia (62)", "Irak (964)", "Irán (98)", "Irlanda (353)", "Islandia (354)", "Islas Marshall (692)", "Islas Salomón (677)", "Israel (972)", "Italia (39)", "Jamaica (1-876)", "Japón (81)", "Jordania (962)", "Kazajistán (7)", "Kenia (254)", "Kirguistán (996)", "Kiribati (686)", "Kuwait (965)", "Laos (856)", "Lesoto (266)", "Letonia (371)", "Líbano (961)", "Liberia (231)", "Libia (218)", "Liechtenstein (423)"};


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, myData);
        spPaises.setAdapter(adapter);


        dataBaseContact = DataBaseContact.getInstance(getInstance());

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (editTextNombre.getText().toString().trim().equals("")) {
                    AlertaDialog("debe escribir un nombre.");
                } else if (editTextTelefono.getText().toString().trim().equals("")) {
                    AlertaDialog("debe escribir un telefono.");
                } else if (editTextNota.getText().toString().trim().equals("")) {
                    AlertaDialog("debe escribir una nota.");

                } else {

                    Contactos contactos = new Contactos();

                    contactos.setTelefono(spPaises.getSelectedItem().toString().replaceAll("[^\\d]", "") + editTextTelefono.getText().toString().trim());
                    contactos.setNombre(editTextNombre.getText().toString().trim());
                    contactos.setNota(editTextNota.getText().toString().trim());
                    contactos.setPais(spPaises.getSelectedItem().toString());
                    contactos.setImagen(byteArray);

                    dataBaseContact.SalvarContactosDb(contactos);

                    Toast.makeText(MainActivity.this, "Contacto guardado.", Toast.LENGTH_SHORT).show();
                    editTextNombre.setText("");
                    editTextTelefono.setText("");
                    editTextNota.setText("");
                }
            }
        });



        btnListaC.setOnClickListener(new View.OnClickListener() {
        @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ListaContactos.class));
            }
        });


        imageButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        }
    });

}

    private void AlertaDialog(String mensaje) {

        new AlertDialog.Builder(this).setTitle("Alerta")
                .setCancelable(false)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    public static MainActivity getInstance() {
        return sActivity;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);

                // Convertir la imagen en un arreglo de bytes
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}