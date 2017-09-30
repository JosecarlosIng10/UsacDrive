package com.example.equipo.usacdrive;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.equipo.usacdrive.R.styleable.AlertDialog;
import static java.net.Proxy.Type.HTTP;

public class CrearUsuario extends AppCompatActivity {

    EditText et1, et2;
    Button btCrear,btRegresar;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_usuario);
        btCrear = (Button) findViewById(R.id.button);
        btRegresar=(Button) findViewById(R.id.button4);
        btCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et1 = (EditText) findViewById(R.id.editText);
                et2 = (EditText) findViewById(R.id.editText2);
                String usuario = et1.getText().toString();
                String contra = et2.getText().toString();
                new FeedTask().execute(usuario,contra);
            }


        });
        btRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regresar = new Intent(CrearUsuario.this, Inicio.class);
                startActivity(regresar);
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
                        //.url("http://josecarlosing10.pythonanywhere.com/Usuario")
                        .url("http://192.168.1.7:5000/Usuario")
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
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle("Listo")

                    .setMessage("Registro Guardado")
                    .setPositiveButton("Ok", null);

            AlertDialog build = builder.create();
            build.show();

        }
    }
}

