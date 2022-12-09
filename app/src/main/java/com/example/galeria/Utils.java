package com.example.galeria;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

public class Utils {
    public static int calculateNumberofColumns(Context context, float columnWidth){ //Função para calcular o número de colunas que serão necessárias para mostrar os itens
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidth = displayMetrics.widthPixels;
        return (int) (screenWidth / columnWidth + 0.5);
    }
    //Função que permite o carregamento da imagem em um tamanho menor, a fim de salvar memória
    public static Bitmap getScaledBitmap(String imagePath, int w, int h){

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true; //linha que carega apenas as informações da imagem, tais como tamanho, nome, etc.
        BitmapFactory.decodeFile(imagePath, bmOptions);

        int photoW = bmOptions.outWidth; //Pegando a largura da imagem
        int photoH = bmOptions.outHeight; //Pegando a altura da imagem

        int scaleFactor = Math.max(photoW/w, photoH/h); //Calculando a proporção

        bmOptions.inJustDecodeBounds = false; //Carregando a imagem na memória
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeFile(imagePath, bmOptions); //retona um objeto do tipo bitmap

    }
    public static Bitmap getBitmap(String imagePath){ //Função para decodificar o arquivo, sem o redimensionar

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = false; //Carregando a imagem na memória
        return BitmapFactory.decodeFile(imagePath, bmOptions); //retona um objeto do tipo bitmap

    }
}
