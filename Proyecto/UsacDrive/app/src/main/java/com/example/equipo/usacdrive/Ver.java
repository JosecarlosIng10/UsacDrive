package com.example.equipo.usacdrive;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class Ver extends AppCompatActivity {


    ImageView imgview;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver);
         imgview = (ImageView) findViewById(R.id.imageView);

         /*Bundle parametros = this.getIntent().getExtras();
         final byte[] byteArray = parametros.getByteArray("mapa");

         Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
         ImageView image = (ImageView) findViewById(R.id.imageView);

         image.setImageBitmap(Bitmap.createScaledBitmap(bmp, image.getWidth(),
                 image.getHeight(), false));*/

    }
}
