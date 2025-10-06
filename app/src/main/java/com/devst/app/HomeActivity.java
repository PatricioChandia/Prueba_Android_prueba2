package com.devst.app;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;

import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


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






