package com.example.equipo.usacdrive;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Inicio extends AppCompatActivity {
    EditText et1, et2;
    Button btIniciar, btRegistrar;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        btIniciar = (Button) findViewById(R.id.button2);
        btRegistrar = (Button) findViewById(R.id.button3);
        btIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et1 = (EditText) findViewById(R.id.editText3);
                et2 = (EditText) findViewById(R.id.editText4);
                String usuario = et1.getText().toString();
                String contra = et2.getText().toString();
                new FeedTask().execute(usuario,contra);
            }


        });

        btRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent siguiente = new Intent(Inicio.this,CrearUsuario.class);
                startActivity(siguiente);
            }
        });

    }
    public class FeedTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                OkHttpClient client = new OkHttpClient();

                String usuario = params[0];
                String contra = params[1];


                RequestBody postData = new FormBody.Builder()
                        .add("usuario", usuario)
                        .add("contra", contra)
                        .build();

                Request request = new Request.Builder()
                        //.url("http://josecarlosing10.pythonanywhere.com/Check")
                        .url("http://192.168.1.7:5000/Check")
                        .post(postData)
                        .build();

                Response response = client.newCall(request).execute();
                String result = response.body().string();
                // et1.setText(result);
                return result;

            } catch (Exception e) {
                String mensaje = e.toString();

                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            et2.setText(s);
            if (s.equals("Si")){

                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Bienvenido")

                        .setMessage("Sesion Iniciada")
                        .setPositiveButton("Ok", null);

                AlertDialog build = builder.create();
                build.show();
                Intent perfil = new Intent(Inicio.this,Perfil.class);
                perfil.putExtra("user",et1.getText().toString());
                startActivity(perfil);
            }
            else {

                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Error de Auntentificacion")

                        .setMessage("Usuario no valido")
                        .setPositiveButton("Ok", null);

                AlertDialog build = builder.create();
                build.show();

            }


        }
    }
}


