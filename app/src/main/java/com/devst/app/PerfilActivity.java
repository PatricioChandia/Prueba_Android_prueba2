package com.devst.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;


public class PerfilActivity extends AppCompatActivity {

    private ImageView imgPerfil;
    private EditText edtNombre, edtCorreo, edtEdad;
    private Button btnGuardar;
    private Uri imagenSeleccionada;

    private static final String PREFS_NAME = "perfil_usuario";

    // Lanzador para seleccionar imagen de la galería
    private final ActivityResultLauncher<String> seleccionarImagen =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    imagenSeleccionada = uri;
                    imgPerfil.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Referencias UI
        imgPerfil = findViewById(R.id.imgPerfil);
        edtNombre = findViewById(R.id.edtNombre);
        edtCorreo = findViewById(R.id.edtCorreo);
        edtEdad = findViewById(R.id.edtEdad);
        btnGuardar = findViewById(R.id.btnGuardarPerfil);

        // Si venimos desde LoginActivity
        String email = getIntent().getStringExtra("email_usuario");
        if (email != null && !email.isEmpty()) {
            edtCorreo.setText(email);
        }

        // Cargar datos si ya existen
        cargarDatosPerfil();

        // Evento: seleccionar imagen
        imgPerfil.setOnClickListener(v -> seleccionarImagen.launch("image/*"));

        // Evento: guardar perfil
        btnGuardar.setOnClickListener(v -> guardarDatosPerfil());
    }

    private void guardarDatosPerfil() {
        String nombre = edtNombre.getText().toString().trim();
        String edad = edtEdad.getText().toString().trim();
        String correo = edtCorreo.getText().toString().trim();

        if (nombre.isEmpty() || edad.isEmpty()) {
            Toast.makeText(this, "Por favor completa nombre y edad", Toast.LENGTH_SHORT).show();
            return;
        }

        String imagenUriGuardada = "";
        if (imagenSeleccionada != null) {
            try {
                // Copiar la imagen seleccionada al almacenamiento interno
                InputStream inputStream = getContentResolver().openInputStream(imagenSeleccionada);
                File archivoDestino = new File(getFilesDir(), "perfil.jpg");
                OutputStream outputStream = new FileOutputStream(archivoDestino);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                inputStream.close();
                outputStream.close();

                imagenUriGuardada = archivoDestino.getAbsolutePath();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al guardar imagen", Toast.LENGTH_SHORT).show();
            }
        }

        // Guardar todo en SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("nombre", nombre);
        editor.putString("edad", edad);
        editor.putString("correo", correo);
        editor.putString("imagenRutaLocal", imagenUriGuardada);
        editor.apply();

        Toast.makeText(this, "Perfil guardado correctamente", Toast.LENGTH_SHORT).show();
    }


    private void cargarDatosPerfil() {
        try {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

            String nombre = prefs.getString("nombre", "");
            String edad = prefs.getString("edad", "");
            String correo = prefs.getString("correo", "");
            String imagenRuta = prefs.getString("imagenRutaLocal", "");

            edtNombre.setText(nombre);
            edtEdad.setText(edad);
            edtCorreo.setText(correo);

            if (!imagenRuta.isEmpty()) {
                File archivo = new File(imagenRuta);
                if (archivo.exists()) {
                    imgPerfil.setImageURI(Uri.fromFile(archivo));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}
