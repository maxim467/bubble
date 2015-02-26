package com.tompreug_migarpl.bubble;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

 import java.util.Random;
import java.util.Vector;

import static com.tompreug_migarpl.bubble.Grafico.randInt;


public class VistaJuego extends View {


    private Vector<Grafico> Bubbles;
    private int numBubbles = 7;


    // //// BOLA FIJA //////
    private Grafico bubble_quiet;// Gráfico de la nave

    private int giroBubble; // Incremento de dirección


    // //// MISIL //////
    private Grafico bubble_move;
    private static int PASO_VELOCIDAD_BUBBLE = 12;
    private boolean bubbleActivo = false;
    private int tiempoBubble;


    int min = 1;
    int max = 4;


    // //// THREAD Y TIEMPO //////
    // Thread encargado de procesar el juego
    private ThreadJuego thread = new ThreadJuego();
    // Cada cuanto queremos procesar cambios (ms)
    private static int PERIODO_PROCESO = 50;
    // Cuando se realizó el último proceso
    private long ultimoProceso = 0;


    public VistaJuego(Context context, AttributeSet attrs) {

        super(context, attrs);

        int x = 0;
        int y =0;

        Drawable drawableBubble_roja, drawableBubble_azul, drawableBubble_verde, drawableBubble_amarilla;
        Drawable drawableBubble_quiet_roja, drawableBubble_quiet_azul, drawableBubble_quiet_verde, drawableBubble_quiet_amarilla;

        drawableBubble_roja = context.getResources().getDrawable(
                R.drawable.bola_roja);
        drawableBubble_azul = context.getResources().getDrawable(
                R.drawable.bola_azul);
        drawableBubble_verde = context.getResources().getDrawable(
                R.drawable.bola_verde);
        drawableBubble_amarilla = context.getResources().getDrawable(
                R.drawable.bola_amarilla);


        drawableBubble_quiet_roja = context.getResources().getDrawable(
                R.drawable.bola_roja);
        drawableBubble_quiet_azul = context.getResources().getDrawable(
                R.drawable.bola_azul);
        drawableBubble_quiet_verde = context.getResources().getDrawable(
                R.drawable.bola_verde);
        drawableBubble_quiet_amarilla = context.getResources().getDrawable(
                R.drawable.bola_amarilla);
/*

        drawableBubble_move_roja = context.getResources().getDrawable(
                R.drawable.bola_roja);
        drawableBubble_move_azul = context.getResources().getDrawable(
                R.drawable.bola_azul);

        drawableBubble_move_verde = context.getResources().getDrawable(
                R.drawable.bola_verde);

        drawableBubble_move_amarilla = context.getResources().getDrawable(
                R.drawable.bola_amarilla);
*/

        Bubbles = new Vector<Grafico>();


        for (int i = 0; i < numBubbles; i++) {

            // Funcion Random para crear bolas (rand Int)


            x = randInt(min, max);


            switch (x) {
                case 1:

                    Grafico bubble_roja = new Grafico(this, drawableBubble_roja);


                    Bubbles.add(bubble_roja);
                    break;
                case 2:
                    Grafico bubble_azul = new Grafico(this, drawableBubble_azul);


                    Bubbles.add(bubble_azul);

                    break;
                case 3:
                    Grafico bubble_verde = new Grafico(this, drawableBubble_verde);


                    Bubbles.add(bubble_verde);

                    break;
                case 4:
                    Grafico bubble_amarilla = new Grafico(this, drawableBubble_amarilla);


                    Bubbles.add(bubble_amarilla);
                    break;
                default:
                    Grafico bubble_negra = new Grafico(this, drawableBubble_roja);


                    Bubbles.add(bubble_negra);
                    break;
            }

        }

            y = randInt(min, max);

            switch (y) {
                case 1:

                    Grafico bubble_roja_quiet = new Grafico(this, drawableBubble_quiet_roja);
                            bubble_quiet=bubble_roja_quiet;
                            bubble_move=bubble_roja_quiet;

                    break;
                case 2:
                    Grafico bubble_azul_quiet = new Grafico(this, drawableBubble_quiet_azul);

                             bubble_quiet=bubble_azul_quiet;
                             bubble_move=bubble_azul_quiet;
                    break;
                case 3:
                    Grafico bubble_verde_quiet = new Grafico(this, drawableBubble_quiet_verde);

                             bubble_quiet=bubble_verde_quiet;
                             bubble_move=bubble_verde_quiet;
                    break;
                case 4:
                    Grafico bubble_amarilla_quiet = new Grafico(this, drawableBubble_quiet_amarilla);

                            bubble_quiet=bubble_amarilla_quiet;
                            bubble_move=bubble_amarilla_quiet;
                    break;
                default:
                    Grafico bubble_negra_quiet = new Grafico(this, drawableBubble_quiet_roja);

                            bubble_quiet=bubble_negra_quiet;
                            bubble_move=bubble_negra_quiet;

        }


    }



    @Override protected void onSizeChanged(int ancho, int alto,
                                           int ancho_anter, int alto_anter) {

        super.onSizeChanged(ancho, alto, ancho_anter, alto_anter);



        for (int i = 0; i < numBubbles; i++) {


                Bubbles.elementAt(i).setPosX((ancho-((i+1)*Bubbles.elementAt(i).getAncho())));
                Bubbles.elementAt(i).setPosY(0);

            }


                    bubble_quiet.setPosX(ancho/2-(bubble_quiet.getAncho())/2);
                    bubble_quiet.setPosY(alto-bubble_quiet.getAlto());

        ultimoProceso = System.currentTimeMillis();
        thread.start();




    }


    @Override
    protected synchronized void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        for (int i = 0; i < Bubbles.size(); i++) {

            Bubbles.elementAt(i).dibujaGrafico(canvas);

        }

        bubble_quiet.dibujaGrafico(canvas);



        if (bubbleActivo) {
            bubble_move.dibujaGrafico(canvas);
        }


    }


    protected synchronized void actualizaFisica() {
        long ahora = System.currentTimeMillis();
        // No hagas nada si el período de proceso no se ha cumplido.
        if (ultimoProceso + PERIODO_PROCESO > ahora) {
            return;
        }
        // Para una ejecución en tiempo real calculamos retardo
        double retardo = (ahora - ultimoProceso) / PERIODO_PROCESO;
        ultimoProceso = ahora; // Para la próxima vez
        // Actualizamos velocidad y dirección de la nave a partir de
        // giroNave y aceleracionNave (según la entrada del jugador)
        //nave.setAngulo((int) (nave.getAngulo() + giroNave * retardo));
        //double nIncX = nave.getIncX() + aceleracionNave *
          //      Math.cos(Math.toRadians(nave.getAngulo())) * retardo;
        //double nIncY = nave.getIncY() + aceleracionNave *
          //      Math.sin(Math.toRadians(nave.getAngulo())) * retardo;
        // Actualizamos si el módulo de la velocidad no excede el máximo
        //if (Math.hypot(nIncX,nIncY) <= Grafico.getMaxVelocidad()){
          //  nave.setIncX(nIncX);
         //   nave.setIncY(nIncY);
        //}
        // Actualizamos posiciones X e Y
        bubble_move.incrementaPos(retardo);
        //for (Grafico asteroide : Asteroides) {
          //  asteroide.incrementaPos(retardo);


        // Actualizamos posición de misil
        if (bubbleActivo) {

            bubble_move.incrementaPos(retardo);
            tiempoBubble-=retardo;
            if (tiempoBubble < 0) {
                bubbleActivo = false;
            }
            else {
                for (int i = 0; i < numBubbles; i++)
                    if (bubble_move.verificaColision(Bubbles.elementAt(i))) {
                        destruyeBubble(i);
                        break;
                    }
            }
        }

    }

    class ThreadJuego extends Thread {
        @Override
        public void run() {
            while (true) {
                actualizaFisica();
            }
        }
    }


    @Override
    public boolean onKeyUp(int codigoTecla, KeyEvent evento) {
        super.onKeyUp(codigoTecla, evento);
        // Suponemos que vamos a procesar la pulsación
        boolean procesada = true;
        switch (codigoTecla) {
            case KeyEvent.KEYCODE_DPAD_UP:
                //aceleracionNave = 0;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                //giroNave = 0;
                break;
            default:
                // Si estamos aquí, no hay pulsación que nos interese
                procesada = false;
                break;
        }
        return procesada;
    }
    private float mX=0, mY=0;
    private boolean disparo=false;

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
              disparo=true;
               break;
           // case MotionEvent.ACTION_MOVE:
            //    float dx = Math.abs(x - mX);
            //    float dy = Math.abs(y - mY);
             //   if (dy<6 && dx>6){
            //        giroNave = Math.round((x - mX) / 2);
             //       disparo = false;
             //   } else if (dx<6 && dy>6){
              //      aceleracionNave = Math.round((mY - y) / 25);
              //      disparo = false;
                //}
               // break;
            case MotionEvent.ACTION_UP:
                //giroNave = 0;
               // aceleracionNave = 0;
                if (disparo){
                    ActivaBubble();
                }
                break;
        }
        mX=x; mY=y;
        return true;
    }

    private void destruyeBubble(int i) {
        Bubbles.removeElementAt(i);
        bubbleActivo = false;
    }


    private void ActivaBubble() {

        bubble_move.setPosX(bubble_quiet.getPosX() + bubble_quiet.getAncho() / 2 - bubble_move.getAncho()
                / 2);
        bubble_move.setPosY(bubble_quiet.getPosY() + bubble_quiet.getAlto() / 2 - bubble_move.getAlto() / 2);
        //bubble_move.setAngulo(bubble_quiet.getAngulo());
        //bubble_move.setIncX(Math.cos(Math.toRadians(bubble_move.getAngulo()))
             //   * PASO_VELOCIDAD_BUBBLE);
        bubble_move.setIncY(Math.sin(Math.toRadians(45))
                * (-(PASO_VELOCIDAD_BUBBLE)));
        tiempoBubble = (int) Math.min(
                this.getWidth() / Math.abs(bubble_move.getIncX()), this.getHeight()
                        / Math.abs(bubble_move.getIncY())) - 2;
        bubbleActivo = true;
    }



}