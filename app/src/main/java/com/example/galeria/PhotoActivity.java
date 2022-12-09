package com.example.galeria;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;

public class PhotoActivity extends AppCompatActivity {

    String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        Toolbar toolbar = findViewById(R.id.tbPhoto);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); //Botão de volta

        Intent i = getIntent();
        photoPath = i.getStringExtra("photo_path"); //Pegando o caminho da foto

        Bitmap bitmap = Utils.getBitmap(photoPath);
        ImageView imPhoto = findViewById(R.id.imPhoto);
        imPhoto.setImageBitmap(bitmap);


    }
    //Gerando itens no menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Código para inflar o menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photo_toolbar, menu);
        return true;
    }

    //Código contendo um "if": Ao selecionar um determinado item do menu, executar uma ação (compartilhar)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.opShare: //Compartilhando a foto
                Intent i = new Intent(Intent.ACTION_SEND);
                Uri photoUri = FileProvider.getUriForFile(PhotoActivity.this, "com.example.galeria.fileprovider", new File(photoPath));
                i.putExtra(Intent.EXTRA_STREAM, photoUri);
                i.setType("image/jpeg"); //Tipo de arquivo
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}