package com.crystal.formulario;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.crystal.formulario.clases.Constantes;
import com.crystal.formulario.clases.Contacto;
import com.crystal.formulario.fragment.DatePickerFragment;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etFechaNacimiento, etNombreFormulario, etTelefonoFormulario, etEmailFormulario, etDescripcionFormulario;
    private Button btnSiguiente;

    private String nombre, fecha, telefono, email, descripcion;
    private Contacto contacto;
    private int edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permisos();
    }

    private void permisos() {
        int estadoDePermiso = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE);
        if (estadoDePermiso == PackageManager.PERMISSION_GRANTED) {
            inicializar();
            eventos();
        } else {
            mensajeSimpleDialog("Alerta", getResources().getString(R.string.alertaPermisosLlamadas));
        }
    }

    private void inicializar() {
        if(getIntent().getExtras() != null){
            edit = getIntent().getExtras().getInt(getResources().getString(R.string.Edit));
        }

        etNombreFormulario = findViewById(R.id.etNombreFormulario);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        etTelefonoFormulario = findViewById(R.id.etTelefonoFormulario);
        etEmailFormulario = findViewById(R.id.etEmailFormulario);
        etDescripcionFormulario = findViewById(R.id.etDescripcionFormulario);
        btnSiguiente = findViewById(R.id.btnSiguiente);

        if(edit == Constantes.RESP_NUEVO_CONTACTO){
            nuevoContacto();
        }else if(edit == Constantes.RESP_EDITAR_CONTACTO){
            contacto = (Contacto) getIntent().getSerializableExtra(getResources().getString(R.string.contacto));
            llenarCampos();
        }
    }

    private void eventos() {
        etFechaNacimiento.setOnClickListener(this);
        btnSiguiente.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.etFechaNacimiento:
                showDatePickerDialog();
                break;
            case R.id.btnSiguiente:
                validarCampos(v);
                break;
        }
    }

    private void validarCampos(View v) {
        cargarDatos();
        if(nombre.isEmpty()){
            //Tambien se puede usar el setError
            //etNombreFormulario.setError(getResources().getString(R.string.nombreInvalido));
            mensajeFab(v, getResources().getString(R.string.nombreInvalido));
            etNombreFormulario.requestFocus();
        }else if(fecha.isEmpty()){
            mensajeFab(v, getResources().getString(R.string.fechaInvalida));
            etFechaNacimiento.requestFocus();
        }else if(telefono.isEmpty()){
            mensajeFab(v, getResources().getString(R.string.telefonoInvalido));
            etTelefonoFormulario.requestFocus();
        }else if(email.isEmpty() || !email.contains("@")){
            mensajeFab(v, getResources().getString(R.string.emailInvalido));
            etEmailFormulario.requestFocus();
        }else if(descripcion.isEmpty()){
            mensajeFab(v, getResources().getString(R.string.descripcionInvalida));
            etDescripcionFormulario.requestFocus();
        }else{
            irConfirmarDatos();
        }
    }

    private void irConfirmarDatos() {
        Intent i = new Intent(MainActivity.this, ConfirmarDatosActivity.class);
        i.putExtra(getResources().getString(R.string.contacto), contacto);
        startActivity(i);
        finish();
    }

    private void mensajeFab(View v, String msj) {
        Snackbar.make(v, msj, Snackbar.LENGTH_INDEFINITE)
                .setAction(getResources().getString(R.string.txt_btnSnackbar), v1 -> {

                })
                .show();
    }

    private void cargarDatos() {
        nombre = etNombreFormulario.getText().toString();
        fecha = etFechaNacimiento.getText().toString();
        telefono = etTelefonoFormulario.getText().toString();
        email = etEmailFormulario.getText().toString();
        descripcion = etDescripcionFormulario.getText().toString();
        contacto = new Contacto(nombre, fecha, telefono, email, descripcion);
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance((datePicker, year, month, day) -> {
            final String selectedDate = day + "/" + (month+1) + "/" + year;
            etFechaNacimiento.setText(selectedDate);
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");
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
        etNombreFormulario.setText("");
        etNombreFormulario.requestFocus();
        etFechaNacimiento.setText("");
        etTelefonoFormulario.setText("");
        etEmailFormulario.setText("");
        etDescripcionFormulario.setText("");
    }

    private void llenarCampos() {
        etNombreFormulario.setText(contacto.getNombre());
        etFechaNacimiento.setText(contacto.getFechaNacimiento());
        etTelefonoFormulario.setText(contacto.getTelefono());
        etEmailFormulario.setText(contacto.getEmail());
        etDescripcionFormulario.setText(contacto.getDescripcion());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constantes.PERMISO_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                inicializar();
                eventos();
            } else {
                finish();
            }
        }
    }

    public void mensajeSimpleDialog(String titulo, String msj){
        int icon = R.drawable.msj_alert_30;
        if (titulo.equals(getResources().getString(R.string.error))){
            icon = R.drawable.msj_error_30;
        } else if(titulo.equals(getResources().getString(R.string.exito))){
            icon = R.drawable.msj_exito_30;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(titulo)
                .setCancelable(false)
                .setMessage(msj)
                .setIcon(icon)
                .setNegativeButton("Cerrar app", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setPositiveButton("Aceptar", (dialog, which) ->
                {
                    dialog.dismiss();
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            Constantes.PERMISO_PHONE);
                });
        AlertDialog alerta = builder.create();
        alerta.show();
    }
}