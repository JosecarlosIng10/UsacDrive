package com.example.equipo.usacdrive;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.equipo.usacdrive.atv.model.TreeNode;
import com.example.equipo.usacdrive.atv.view.AndroidTreeView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.attr.data;

public class Perfil extends AppCompatActivity {

    EditText et6, et5;
    Button btguardar, btsubir,btmodificar, bteliminar;
    //ImageView imgview;

    private static final int READ_REQUEST_CODE = 42;
    int SELECCIONAR_DIRECTORIO_ORIGEN = 1;
    boolean estadoBooleanOrigen=false;

    final Context context = this;
    private int VALOR_RETORNO = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        btguardar = (Button) findViewById(R.id.button5);
        btsubir = (Button) findViewById(R.id.button6);
        btmodificar=(Button) findViewById(R.id.button7);
        bteliminar=(Button) findViewById(R.id.button8);
        et6 = (EditText) findViewById(R.id.editText6);
        et5 = (EditText) findViewById(R.id.editText5);
        //imgview = (ImageView) findViewById(R.id.imageView3);

        Bundle parametros = this.getIntent().getExtras();
        final String datos = parametros.getString("user");

        btguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String nombre = et6.getText().toString();
                String padre = et5.getText().toString();


                new Perfil.FeedTask().execute(nombre, datos, padre);

            }
        });
        btmodificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedTask2 fed = new FeedTask2();
                String nombre = et5.getText().toString();
                String nuevo = et6.getText().toString();

                fed.doInBackground(datos,nombre,nuevo);


            }
        });

        bteliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FeedTask3 fed = new FeedTask3();
                String nombre = et5.getText().toString();

                fed.doInBackground(datos,nombre);

            }
        });
        btsubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentOrigen = new Intent(Intent.ACTION_GET_CONTENT);
                intentOrigen.setType("image/*");
                startActivityForResult(intentOrigen, SELECCIONAR_DIRECTORIO_ORIGEN);
                estadoBooleanOrigen = true;
            }


        });



    }
     Uri uri;
    @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {



          uri= data.getData();



            File f = new File(uri.getPath());
            String fileName= f.getPath();

            String val="";
            byte[] bytes= convertImageToByte(uri);


            llam(et5.getText().toString(),fileName,bytes);
            //String scheme = uri.getScheme();


            //llamar(convertImageToByte(uri));
        }


    }
    public void llam(String nombre, String fileName, byte[] str){
        FeedTask1 feed = new FeedTask1();
        feed.doInBackground("jose",fileName,str);
        //new Perfil.FeedTask1().execute("jose", "fotos", str);
    }





    public byte[] convertImageToByte(Uri uri){
        byte[] data = null;

        try {
            ContentResolver cr = getBaseContext().getContentResolver();
            InputStream inputStream = cr.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            data = baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void llamar(byte[] array){
       /* Bitmap bmp = BitmapFactory.decodeByteArray(array, 0, array.length);
        ImageView image = (ImageView) findViewById(R.id.imageView3);



        image.setImageBitmap(Bitmap.createScaledBitmap(bmp, image.getWidth(),
                image.getHeight(), false));*/
       // image.setImageBitmap(bmp);
    }

    public class FeedTask3  {





        String doInBackground(String user, String nombre) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            try {
                OkHttpClient client = new OkHttpClient();




                RequestBody postData = new FormBody.Builder()
                        .add("usuario", user)
                        .add("carpeta", nombre)

                        .build();

                Request request = new Request.Builder()
                        //.url("http://josecarlosing10.pythonanywhere.com/CrearCarpeta")
                        .url("http://192.168.1.7:5000/EliminarCarpeta")
                        .post(postData)
                        .build();

                Response response = client.newCall(request).execute();
                String result = response.body().string();
                if (result.equals("True")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setTitle("Listo")

                            .setMessage("Se elimino con exito")
                            .setPositiveButton("Ok", null);

                    AlertDialog build = builder.create();
                    build.show();
                }

                // et1.setText(result);
                return result;

            } catch (Exception e) {
                String mensaje = e.toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Bienvenido")

                        .setMessage(mensaje)
                        .setPositiveButton("Ok", null);

                AlertDialog build = builder.create();
                build.show();

                return null;
            }

        }



    }
    public class FeedTask2  {





        String doInBackground(String user, String nombre, String nuevo) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            try {
                OkHttpClient client = new OkHttpClient();




                RequestBody postData = new FormBody.Builder()
                        .add("usuario", user)
                        .add("nombre", nombre)
                        .add("nuevo", nuevo)
                        .build();

                Request request = new Request.Builder()
                        //.url("http://josecarlosing10.pythonanywhere.com/CrearCarpeta")
                        .url("http://192.168.1.7:5000/ModificarCarpeta")
                        .post(postData)
                        .build();

                Response response = client.newCall(request).execute();
                String result = response.body().string();
                if (result.equals("True")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setTitle("Listo")

                            .setMessage("Se modifico con exito")
                            .setPositiveButton("Ok", null);

                    AlertDialog build = builder.create();
                    build.show();
                }

                // et1.setText(result);
                return result;

            } catch (Exception e) {
                String mensaje = e.toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Bienvenido")

                        .setMessage(mensaje)
                        .setPositiveButton("Ok", null);

                AlertDialog build = builder.create();
                build.show();

                return null;
            }

        }



    }


    public class FeedTask1  {





        String doInBackground(String user, String nombre, byte[] archivo) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            try {
                OkHttpClient client = new OkHttpClient();



                String fotos = "none";
                RequestBody postData = new FormBody.Builder()
                        .add("usuario", user)
                        .add("carpeta", fotos)
                        .add("nombre", nombre)
                        .add("archivo",archivo.toString())
                        .build();

                Request request = new Request.Builder()
                        //.url("http://josecarlosing10.pythonanywhere.com/CrearCarpeta")
                        .url("http://192.168.1.7:5000/CrearArchivo")
                        .post(postData)
                        .build();

                Response response = client.newCall(request).execute();
                String result = response.body().string();
                // et1.setText(result);
                return result;

            } catch (Exception e) {
                String mensaje = e.toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Bienvenido")

                        .setMessage(mensaje)
                        .setPositiveButton("Ok", null);

                AlertDialog build = builder.create();
                build.show();

                return null;
            }

        }



    }


    public class FeedTask extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {
            try {
                OkHttpClient client = new OkHttpClient();

                String nombre = params[0];
                String user = params[1];
                String padre = params[2];




                RequestBody postData = new FormBody.Builder()
                        .add("usuario",user)
                        .add("nombre", nombre)
                        .add("padre",padre)
                        .build();

                Request request = new Request.Builder()
                        //.url("http://josecarlosing10.pythonanywhere.com/CrearCarpeta")
                        .url("http://192.168.1.7:5000/CrearCarpeta")
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
            //et6.setText(s);
            crear(s);
            /*if (s.equals("Si")){

                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Bienvenido")

                        .setMessage("Sesion Iniciada")
                        .setPositiveButton("Ok", null);

                AlertDialog build = builder.create();
                build.show();
                Intent perfil = new Intent(Inicio.this,Perfil.class);
                startActivity(perfil);
            }
            else {

                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Error de Auntentificacion")

                        .setMessage("Usuario no valido")
                        .setPositiveButton("Ok", null);

                AlertDialog build = builder.create();
                build.show();

            }*/


        }
         AndroidTreeView tView;


        public TreeNode verHijos(String[] substring, String padre, TreeNode aux){

            for (int ii=0; ii< substring.length; ii++){
                String[] datos1 = substring[ii].split("-");
                if (datos1[0].equals(padre)){
                    MyHolder.IconTreeItem subChildItem1 = new MyHolder.IconTreeItem(R.drawable.ic_arrow_drop_down, datos1[1]);
                    TreeNode hijo = new TreeNode(subChildItem1).setViewHolder(new MyHolder(getApplicationContext(), true, MyHolder.DEFAULT, MyHolder.DEFAULT));
                    hijo.setText(datos1[1]);



                   aux.addChild(verHijos(substring,datos1[1],hijo));

                }
            }

            return aux;
        }
        public void crear(String cadena){

                ((LinearLayout) findViewById(R.id.ll_parent1)).removeAllViews();
            TreeNode root = TreeNode.root();
            final MyHolder.IconTreeItem nodeItem = new MyHolder.IconTreeItem(R.drawable.ic_arrow_drop_down, "Carpetas");
            TreeNode parent = new TreeNode(nodeItem).setViewHolder(new MyHolder(getApplicationContext(), true, MyHolder.DEFAULT, MyHolder.DEFAULT));
            parent.setText("carpeta");
        String [] substring = cadena.split("@");

            for (int i=0; i< substring.length; i++){
                String[] datos = substring[i].split("-");
                if (datos[0].equals("root")){

                    MyHolder.IconTreeItem subChildItem = new MyHolder.IconTreeItem(R.drawable.ic_arrow_drop_down, datos[1]);
                    TreeNode subChild = new TreeNode(subChildItem).setViewHolder(new MyHolder(getApplicationContext(), true, MyHolder.DEFAULT, MyHolder.DEFAULT));
                    subChild.setText(datos[1]);



                    parent.addChild( verHijos(substring,datos[1],subChild));

                }
             }




            root.addChild(parent);
            //parent.addChild("");

             tView= new AndroidTreeView(getApplicationContext(), root);

            tView.setDefaultNodeClickListener(new TreeNode.TreeNodeClickListener() {
                @Override
                public void onClick(TreeNode node, Object value) {

                  et5.setText(node.getText());
                }
            });
            //((LinearLayout)findViewById(R.id.ll_parent) ).removeAllViewsInLayout();

            ((LinearLayout) findViewById(R.id.ll_parent1)).addView(tView.getView());


        }
    }


}
