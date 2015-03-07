package com.tompreug_migarpl.bubble;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import java.util.Random;



/**
 * Created by MiguelGarcia on 24/2/15.
 */
public class Grafico {
    private Drawable drawable;   //Imagen que dibujaremos
    private double posX, posY;   //Posición
    private double incX, incY;   //Velocidad desplazamiento
    private double angulo;//Ángulo y velocidad rotación
    private int ancho, alto;     //Dimensiones de la imagen
    private int radioColision;   //Para determinar colisión
    //Donde dibujamos el gráfico (usada en view.ivalidate)
    private View view;
    // Para determinar el espacio a borrar (view.ivalidate)
    public static final int MAX_VELOCIDAD = 20;


    public Grafico(View view, Drawable drawable){
        this.view = view;
        this.drawable = drawable;
        ancho = drawable.getIntrinsicWidth();
        alto = drawable.getIntrinsicHeight();
        radioColision = (alto+ancho)/4;
    }




    public void dibujaGrafico(Canvas canvas){
        canvas.save();
        int x=(int) (posX+ancho/2);
        int y=(int) (posY+alto/2);
        canvas.rotate((float) angulo,(float) x,(float) y);
        drawable.setBounds((int)posX, (int)posY, (int)posX+ancho, (int)posY+alto);
        drawable.draw(canvas);
        canvas.restore();
        int rInval = (int) Math.hypot(ancho,alto)/2 + MAX_VELOCIDAD;
        view.invalidate(x-rInval, y-rInval, x+rInval, y+rInval);
    }




    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;

    }


    public void incrementaPos(double factor){ //factor es el retardo
        posX+=incX * factor; //actualizamos la posicion de X
        // Si salimos de la pantalla, corregimos posición
        if(posX<0) {
            incX = -incX;
        }
        if(posX>view.getWidth()-ancho) {
            incX = -incX;
        }
        posY+=incY * factor; //actualizamos la posicion de Y
        //if(posY<0) {
         //  incY=-incY;
        //}

        //angulo=angulo+factor;
    }



    public double distancia(Grafico g) {
        return Math.hypot(posX-g.posX, posY-g.posY);
    }
    public boolean verificaColision(Grafico g) {
        return(distancia(g) < (radioColision+g.radioColision));
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double getIncX() {
        return incX;
    }

    public void setIncX(double incX) {
        this.incX = incX;
    }

    public double getIncY() {
        return incY;
    }

    public void setIncY(double incY) {
        this.incY = incY;
    }

    public double getAngulo() {
        return angulo;
    }

    public void setAngulo(double angulo) {
        this.angulo = angulo;
    }

   //    public int getRotacion() {
   //  return rotacion;
    //}

    //public void setRotacion(int rotacion) {
    // this.rotacion = rotacion;
    // }

    public int getAncho() {
        return ancho;
    }

    public void setAncho(int ancho) {
        this.ancho = ancho;
    }

    public int getAlto() {
        return alto;
    }

    public void setAlto(int alto) {
        this.alto = alto;
    }

    public int getRadioColision() {
        return radioColision;
    }

    public void setRadioColision(int radioColision) {
        this.radioColision = radioColision;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public static int getMaxVelocidad() {
        return MAX_VELOCIDAD;
    }



}
