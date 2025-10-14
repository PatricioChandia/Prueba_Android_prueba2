package com.devst.app;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.database.Cursor;
import android.provider.ContactsContract;

import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;


import androidx.activity.EdgeToEdge;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;

public class HomeActivity extends AppCompatActivity {

//vatiables
    private String emailUsuario;
    private TextView tvBienvenida;
    private Button btnLinterna;
    private CameraManager camara;
    private String cameraID= null;
    private boolean luz = false;

    //Launcher para activity y flash-camara
    private final ActivityResultLauncher<Intent> editarPerfilLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
               if(result.getResultCode() == RESULT_OK && result.getData()!= null){
                   String nombre = result.getData().getStringExtra("nombre_editado");
                   if(nombre != null){
                       tvBienvenida.setText("Hola, "+ nombre);
                   }
               }
            });
    private final ActivityResultLauncher<String> permisosCamaraLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), obtener ->{
                if(obtener){
                    alternarluz(); //Metodo Apagar y encender Luz
                }else {
                    Toast.makeText(this, "Permiso de camara denegado!", Toast.LENGTH_SHORT).show();
                }
            });

    //Launcher para seleccionar contacto
    private final ActivityResultLauncher<Intent> seleccionarContactoLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri contactUri = result.getData().getData();
                    if (contactUri != null) {
                        obtenerDatosContacto(contactUri);
                    }
                }
            });
    //Permiso para leer contactos
    private final ActivityResultLauncher<String> permisoContactosLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    abrirSelectorContactos();
                } else {
                    Toast.makeText(this, "Permiso para leer contactos denegado", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        //Referencias
        tvBienvenida =findViewById(R.id.tvBienvenida);
        Button btnIrPerfil = findViewById(R.id.btnIrPerfil);
        Button btnAbrirWeb = findViewById(R.id.btnAbrirWeb);
        Button btnEnviarCorreo = findViewById(R.id.btnEnviarCorreo);
        Button btnCompartir = findViewById(R.id.btnCompartir);
        btnLinterna = findViewById(R.id.btnLinterna);
        Button btnCamara = findViewById(R.id.btnCamara);
        Button btnLlamar = findViewById(R.id.btnLlamar);
        EditText txtNumeroTelefono = findViewById(R.id.txtNumeroTelefono);
        Button btnSMS = findViewById(R.id.btnSMS);
        Button btnSeleccionarContacto = findViewById(R.id.btnSeleccionarContacto);

        //Recibir datos del login
        emailUsuario = getIntent().getStringExtra("email_usuario");
        if(emailUsuario == null) emailUsuario = "";
        tvBienvenida.setText("Bienvenido: " + emailUsuario);

        //Evento ir al perfil
        btnIrPerfil.setOnClickListener(View ->{
            Intent perfil = new Intent(HomeActivity.this, PerfilActivity.class);
            perfil.putExtra("email_usuario", emailUsuario);
            editarPerfilLauncher.launch(perfil);
        });

        //Evento abrir web
        btnAbrirWeb.setOnClickListener(view ->{
            Uri url = Uri.parse("http://www.santotomas.cl");
            Intent viewWeb = new Intent(Intent.ACTION_VIEW, url);
            startActivity(viewWeb);
        });

        //Evento Enviar Correo
        btnEnviarCorreo.setOnClickListener( view ->{
            Intent email = new Intent(Intent.ACTION_SENDTO);
            email.setData(Uri.parse("mailto:"));
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{emailUsuario});
            email.putExtra(Intent.EXTRA_SUBJECT, "Asunto de Android");
            email.putExtra(Intent.EXTRA_TEXT, "Mensaje del cuerpo(contenido)");
            startActivity(Intent.createChooser(email, "Enviar Correo con:"));
        });

        //Evento compartir texto
        btnCompartir.setOnClickListener(v -> {
            Intent compartir = new Intent(Intent.ACTION_SEND);
            compartir.setType("text/plain");
            compartir.putExtra(Intent.EXTRA_TEXT, "Hola desde Android 👌");
            startActivity(Intent.createChooser(compartir, "Compartir desde:"));
        });

        //Linterna Cámara
        /*Creamos la estructura para el flash principalmente
        * y el inicio de la camara, agregando try-catch*/
        camara = (CameraManager) getSystemService(CAMERA_SERVICE);

        try {
            for(String id: camara.getCameraIdList()){
                CameraCharacteristics cc = camara.getCameraCharacteristics(id);
                Boolean disponibleflash = cc.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                Integer dato = cc.get(CameraCharacteristics.LENS_FACING);
                if(Boolean.TRUE.equals(disponibleflash)
                        && dato != null
                        && dato ==CameraCharacteristics.LENS_FACING_BACK){
                    cameraID = id; //Priorizar la camara trasera con el flash
                    break;
                }
            }
        }catch (CameraAccessException e){
            Toast.makeText(this, "No se puede acceder a la camara", Toast.LENGTH_SHORT).show();
        }

        btnLinterna.setOnClickListener(v -> {
            if(cameraID == null){
                Toast.makeText(this, "El dispositivo no tiene Flash disponible", Toast.LENGTH_SHORT).show();
                return;
            }

            //Datos de carga de permisos
            boolean cargadato = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED;
            if(cargadato){
                alternarluz();
            }else{
                permisosCamaraLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        // Evento para abrir la cámara
        btnCamara.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CamaraActivity.class);
            startActivity(intent);
        });

        //Evento Para Llamar
        btnLlamar.setOnClickListener(v -> {
            // Si el cuadro está oculto, lo mostramos
            if (txtNumeroTelefono.getVisibility() == View.GONE) {
                txtNumeroTelefono.setVisibility(View.VISIBLE);
                txtNumeroTelefono.requestFocus();
                btnLlamar.setText("Marcar número");
            }
            else { // Si ya está visible, intentamos hacer la llamada
                String numero = txtNumeroTelefono.getText().toString().trim();

                if (numero.isEmpty()) {
                    Toast.makeText(this, "Ingrese un número telefónico", Toast.LENGTH_SHORT).show();
                    return;
                }

                String uriNumero = "tel:" + numero;
                Intent intentLlamar = new Intent(Intent.ACTION_DIAL);
                intentLlamar.setData(Uri.parse(uriNumero));
                startActivity(intentLlamar);

                // Volver a ocultar el cuadro después de abrir el marcador
                txtNumeroTelefono.setVisibility(View.GONE);
                btnLlamar.setText("Llamar");
                txtNumeroTelefono.setText("");
            }
        });

        //Evento enviar SMS
        btnSMS.setOnClickListener(v -> {
            // Creamos el Intent sin número predefinido
            Uri uri = Uri.parse("smsto:"); // <- vacío para permitir elegir el contacto
            Intent intentSMS = new Intent(Intent.ACTION_SENDTO, uri);
            intentSMS.putExtra("sms_body", "Hola! Este es un mensaje desde mi app 🫠👌.");

            try {
                startActivity(intentSMS);
            } catch (Exception e) {
                Toast.makeText(this, "No se pudo abrir la app de SMS", Toast.LENGTH_SHORT).show();
            }
        });

        //Evento para Abrir Contactos
        btnSeleccionarContacto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_GRANTED) {
                abrirSelectorContactos();
            } else {
                permisoContactosLauncher.launch(Manifest.permission.READ_CONTACTS);
            }
        });


    }

    private void alternarluz() {
        try {
            luz =! luz;
            camara.setTorchMode(cameraID, luz);
            btnLinterna.setText(luz ? "Apagar Linterna" : "Encender Linterna");
        }catch (CameraAccessException e){
            Toast.makeText(this, "Error del controlador de linterna", Toast.LENGTH_SHORT).show();
        }
    }

    //Metodo para abrir contactos
    private void abrirSelectorContactos() {
        Intent intentSeleccionar = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        seleccionarContactoLauncher.launch(intentSeleccionar);
    }


    //Metodo para obtener nombre o numero del contacto
    private void obtenerDatosContacto(Uri contactUri) {
        try {
            String nombre = null;
            String numero = null;

            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

                int tieneTelefono = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                if (tieneTelefono > 0) {
                    Cursor telefonosCursor = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contactId},
                            null
                    );
                    if (telefonosCursor != null && telefonosCursor.moveToFirst()) {
                        numero = telefonosCursor.getString(
                                telefonosCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        );
                        telefonosCursor.close();
                    }
                }
                cursor.close();
            }

            if (numero != null) {
                EditText txtNumeroTelefono = findViewById(R.id.txtNumeroTelefono);
                txtNumeroTelefono.setVisibility(View.VISIBLE);
                txtNumeroTelefono.setText(numero);
                Toast.makeText(this, "Contacto seleccionado: " + nombre, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "El contacto no tiene número telefónico", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al acceder al contacto", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onPause(){
        super.onPause();
        if(cameraID != null && luz){
            try {
                camara.setTorchMode(cameraID, false);
                luz = false;
                if(btnLinterna != null) btnLinterna.setText("Encender Linterna");
            }catch (CameraAccessException ignore){}
        }
    }
}






