package com.example.equipo.usacdrive;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.equipo.usacdrive.atv.model.TreeNode;
import com.example.equipo.usacdrive.atv.view.AndroidTreeView;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.attr.data;

public class Perfil extends AppCompatActivity {
    EditText et6,et5;
    Button btguardar;

    final Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        btguardar = (Button) findViewById(R.id.button5);
        Bundle parametros = this.getIntent().getExtras();
        final String datos = parametros.getString("user");

        btguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et6 = (EditText) findViewById(R.id.editText6);
                et5 = (EditText) findViewById(R.id.editText5);

                String nombre = et6.getText().toString();
                String padre = et5.getText().toString();


                new Perfil.FeedTask().execute(nombre,datos, padre);
            }
        });



        //Root
        /*

        //Parent
        MyHolder.IconTreeItem nodeItem = new MyHolder.IconTreeItem(R.drawable.ic_arrow_drop_down, "Carpetas");
        TreeNode parent = new TreeNode(nodeItem).setViewHolder(new MyHolder(getApplicationContext(), true, MyHolder.DEFAULT, MyHolder.DEFAULT));

        MyHolder.IconTreeItem nodeItem1 = new MyHolder.IconTreeItem(R.drawable.ic_arrow_drop_down, "Carpetas2");
        TreeNode parent1 = new TreeNode(nodeItem1).setViewHolder(new MyHolder(getApplicationContext(), true, MyHolder.DEFAULT, MyHolder.DEFAULT));

        //Child
        MyHolder.IconTreeItem childItem = new MyHolder.IconTreeItem(R.drawable.ic_folder, "Fotos");
        TreeNode child = new TreeNode(childItem).setViewHolder(new MyHolder(getApplicationContext(), false, R.layout.child, 25));

        //Sub Child
        MyHolder.IconTreeItem subChildItem = new MyHolder.IconTreeItem(R.drawable.ic_folder, "Campamento");
        TreeNode subChild = new TreeNode(subChildItem).setViewHolder(new MyHolder(getApplicationContext(), false, R.layout.child, 50));

        //Add sub child.
        child.addChild(subChild);


        //Add child.
        parent.addChildren(child);
        root.addChild(parent);
        root.addChild(parent1);

        //Add AndroidTreeView into view.
        AndroidTreeView tView = new AndroidTreeView(getApplicationContext(), root);
        ((LinearLayout) findViewById(R.id.ll_parent)).addView(tView.getView());*/



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
