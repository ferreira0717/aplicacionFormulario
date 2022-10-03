package com.crystal.formulario;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crystal.formulario.clases.Constantes;
import com.crystal.formulario.clases.Contacto;

public class ConfirmarDatosActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvTelefonoCD, tvNombreCD, tvEmailCD, tvFechaCD, tvDescripcionCD;
    CardView cwLlamada, cwCorreo;
    Contacto contacto;
    Button btnEditarDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_datos);
        this.setTitle(R.string.menuTitleConfirmarDatos);

        inicializar();
        eventos();
    }

    private void inicializar() {
        contacto = (Contacto) getIntent().getSerializableExtra(getResources().getString(R.string.contacto));
        if(contacto != null){
            tvTelefonoCD = findViewById(R.id.tvTelefonoCD);
            tvNombreCD = findViewById(R.id.tvNombreCD);
            tvEmailCD = findViewById(R.id.tvEmailCD);
            tvFechaCD = findViewById(R.id.tvFechaCD);
            tvDescripcionCD = findViewById(R.id.tvDescripcionCD);
            btnEditarDatos = findViewById(R.id.btnEditarDatos);
            cwLlamada = findViewById(R.id.cwLlamada);
            cwCorreo = findViewById(R.id.cwCorreo);

            llenarCampos();
        }else{
            mensajeSimpleDialog(getResources().getString(R.string.error), getResources().getString(R.string.problemasObjetoContacto));
        }
    }

    private void llenarCampos() {
        tvTelefonoCD.setText(contacto.getTelefono());
        tvNombreCD.setText(contacto.getNombre());
        tvEmailCD.setText(contacto.getEmail());
        tvFechaCD.setText(contacto.getFechaNacimiento());
        tvDescripcionCD.setText(contacto.getDescripcion());
    }

    private void eventos() {
        btnEditarDatos.setOnClickListener(this);
        cwLlamada.setOnClickListener(this);
        cwCorreo.setOnClickListener(this);
    }

    //Alert Dialog para mostrar mensajes de error, alertas o información
    public void mensajeSimpleDialog(String titulo, String msj){
        int icon = R.drawable.msj_alert_30;
        if (titulo.equals(getResources().getString(R.string.error))){
            icon = R.drawable.msj_error_30;
        } else if(titulo.equals(getResources().getString(R.string.exito))){
            icon = R.drawable.msj_exito_30;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmarDatosActivity.this);
        builder.setTitle(titulo)
                .setCancelable(false)
                .setMessage(msj)
                .setIcon(icon)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nuevoContacto();
                    }
                });
        AlertDialog alerta = builder.create();
        alerta.show();
    }

    private void regresarMain() {
        Intent i = new Intent(ConfirmarDatosActivity.this, MainActivity.class);
        i.putExtra(getResources().getString(R.string.Edit), Constantes.RESP_EDITAR_CONTACTO);
        i.putExtra(getResources().getString(R.string.contacto), contacto);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflaterMenu = getMenuInflater();
        inflaterMenu.inflate(R.menu.menucontacto, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mItemNuevoContacto) {
            nuevoContacto();
        }
        return super.onOptionsItemSelected(item);
    }

    private void nuevoContacto() {
        Intent i = new Intent(ConfirmarDatosActivity.this, MainActivity.class);
        i.putExtra(getResources().getString(R.string.Edit), Constantes.RESP_NUEVO_CONTACTO);
        startActivity(i);
        finish();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnEditarDatos:
                regresarMain();
                break;
            case R.id.cwLlamada:
                llamar();
                break;
            case R.id.cwCorreo:
                EnviarCorreo();
                break;
        }
    }

    @SuppressLint("IntentReset")
    private void EnviarCorreo() {
        Intent emailIntent = new Intent((Intent.ACTION_SEND));
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, contacto.getEmail());
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent, "Email "));
    }

    private void llamar() {
        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+contacto.getTelefono())));
    }
}