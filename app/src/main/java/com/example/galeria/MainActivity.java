package com.example.galeria;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.PackageManagerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static int RESULT_TAKE_PICTURE = 1;
    static int RESULT_REQUEST_PERMISSION = 2;

    List<String> photos = new ArrayList<>();

    String currentPhotoPath;

    MainAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Usando a toolbar que foi criada
        Toolbar toolbar = findViewById(R.id.tbMain);
        setSupportActionBar(toolbar);

        List<String> permissions = new ArrayList<>(); //Lista de permissões que serão pedidas
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        checkForPermissions(permissions);

        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES); //Carregando todas as fotos que foram salvas
        File[] files = dir.listFiles();
        for(int i = 0; i < files.length; i++){
            photos.add(files[i].getAbsolutePath());
        }

        mainAdapter = new MainAdapter(MainActivity.this, photos); //Passando a lista de fotos

        RecyclerView rvGallery = findViewById(R.id.rvGallery);
        rvGallery.setAdapter(mainAdapter);

        float w = getResources().getDimension(R.dimen.itemWidth);
        int numberOfColumns = Utils.calculateNumberofColumns(MainActivity.this, w);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, numberOfColumns); //Disponibilizar as imagens em formato de grid
        rvGallery.setLayoutManager(gridLayoutManager);
    }

    //Gerando itens no menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //Código para inflar o menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_toolbar, menu);
        return true;
    }

    //Código contendo um "if": Ao selecionar um determinado item do menu, executar uma ação (no caso, abrir a câmera)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.opCamera:
                dispatchTakePictureIntent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Função para tirar a foto
    private void dispatchTakePictureIntent() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File f = null;
        try { //Tenta criar o arquivo, caso não seja possível, mostra uma mensagem de erro
             f = createImageFile();
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "Não foi possível criar o arquivo", Toast.LENGTH_LONG).show();
            return;
        }

        currentPhotoPath = f.getAbsolutePath();

        if(f != null) { //caso o arquivo seja criado sem erro, pega a Uri (endereço do arquivo que as aplicações podem usar)
            Uri fUri = FileProvider.getUriForFile(MainActivity.this, "com.example.galeria.fileprovider", f);
            i.putExtra(MediaStore.EXTRA_OUTPUT, fUri);
            startActivityForResult(i, RESULT_TAKE_PICTURE);
        }

    }
    //Criar um arquivo para armazenar a foto que foi tirada
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()); //Pega a data atual e salvar em uma string
        String imageFileName = "JPEG_" + timeStamp; //Define o nome do arquivo a ser gerado
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES); //Define a pasta em que o arquivo vai ser salvo
        File f = File.createTempFile(imageFileName, ".jpg", storageDir);
        return f;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_TAKE_PICTURE) {
            if (resultCode == Activity.RESULT_OK){
                photos.add(currentPhotoPath); //se a pessoa tirou a foto, guarda ela em uma lista
                mainAdapter.notifyItemInserted(photos.size()-1);
            }
            else {
                File f = new File(currentPhotoPath); //se a pessoa desistiu de tirar a foto, exclui o arquivo
                f.delete();
            }
        }
    }

    private void checkForPermissions(List<String> permissions){ //Verificando se as permissões já não estão sendo concedidas
        List<String> permissionsNotGranted = new ArrayList<>();

        for(String permission : permissions) {
            if (!hasPermission(permission)) { //Se a permissão não estiver já sendo concedida, adiciona ela à lista de permissões não autorizadas
                permissionsNotGranted.add(permission);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (permissionsNotGranted.size() > 0) { //Se não houver a permissão prévia, pedir ao usuário a permissão
                requestPermissions(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]), RESULT_REQUEST_PERMISSION);
            }
        }
    }

    private boolean hasPermission(String permission){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return ActivityCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        List<String> permissionsRejected = new ArrayList<>();
        if(requestCode == RESULT_REQUEST_PERMISSION) { //Verificando se o usuário rejeitou a permissão

            for (String permission : permissions){ //Se a permissão foi rejeitada, adiciona ela à lista de permissões rejeitadas
                if (!hasPermission(permission)){
                    permissionsRejected.add(permission);
                }
            }
        }

        if (permissionsRejected.size() > 0){ //Se a permissão foi negada pelo usuário, exibe uma nova caixa de texto pedindo novamente a permissão
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                    new AlertDialog.Builder(MainActivity.this).
                            setMessage("Para usar essa app é preciso conceder essas permissões").
                            setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), RESULT_REQUEST_PERMISSION);
                                }
                            }).create().show();
                }
            }
        }
    }
    public void startPhotoActivity(String photoPath) { //Função que permite o zoom da imagem ao clicar em uma foto
        Intent i = new Intent(MainActivity.this, PhotoActivity.class);
        i.putExtra("photo_path", photoPath);
        startActivity(i);
    }
}