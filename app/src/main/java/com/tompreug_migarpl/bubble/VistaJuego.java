package com.tompreug_migarpl.bubble;

import android.content.Context;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.graphics.Canvas;

import android.view.MotionEvent;
import android.view.WindowManager;

import java.util.Vector;

import static com.tompreug_migarpl.bubble.Grafico.randInt;

public class VistaJuego extends View {

    ////Bolas estaticas\\\\
    private Vector<Grafico> Bubbles; // Vector de las bolas
    //private Vector<Grafico> New_Bubbles;
    private boolean[] Activos;  //Vector que indica si Bubbles activo
    private int numBubbles; // Número inicial de bolas
    private int modificador;

    // //// Bola fija //////
    private Grafico bubble_quiet;// Gráfico de la bola

    // //// Bola en movimiento //////
    private Grafico bubble_move;
    private static int PASO_VELOCIDAD_BUBBLE = 50;
    private boolean bubbleActivo = false;

    private int min = 1;
    private int max = 4;
    private int tiradas;
    private int tirada=0;

    // //// THREAD Y TIEMPO //////
    // Thread encargado de procesar el juego
    private ThreadJuego thread = new ThreadJuego();

    public MediaPlayer mp, choq, fallo, mario;
    // Cada cuanto queremos procesar cambios (ms)
    private static int PERIODO_PROCESO = 50;//refresco de la pantalla (ms)
    // Cuando se realizó el último proceso
    private long ultimoProceso = 0;

    private Grafico bubble_roja_quiet, bubble_verde_quiet, bubble_azul_quiet, bubble_amarilla_quiet,
            bubble_roja_move, bubble_verde_move, bubble_azul_move, bubble_amarilla_move;

    public VistaJuego(Context context, AttributeSet attrs) {
        super(context, attrs);
        int up = 0;
        int down = 0;
        mp = MediaPlayer.create(context, R.raw.trumpetfanfare);//trompeta
        choq = MediaPlayer.create(context, R.raw.choque);//colision
        fallo = MediaPlayer.create(context, R.raw.fail);//fallo
        mario = MediaPlayer.create(context, R.raw.mario_lose);//fallo

        Drawable drawableBubble_roja, drawableBubble_verde, drawableBubble_azul, drawableBubble_amarilla,
                drawableBubble_quiet_roja, drawableBubble_quiet_verde, drawableBubble_quiet_azul, drawableBubble_quiet_amarilla,
                drawableBubble_move_roja, drawableBubble_move_verde, drawableBubble_move_azul, drawableBubble_move_amarilla;

        drawableBubble_roja = context.getResources().getDrawable(R.drawable.bola_roja);
        drawableBubble_verde = context.getResources().getDrawable(R.drawable.bola_verde);
        drawableBubble_azul = context.getResources().getDrawable(R.drawable.bola_azul);
        drawableBubble_amarilla = context.getResources().getDrawable(R.drawable.bola_amarilla);

        drawableBubble_quiet_roja = context.getResources().getDrawable(R.drawable.bola_roja);
        drawableBubble_quiet_verde = context.getResources().getDrawable(R.drawable.bola_verde);
        drawableBubble_quiet_azul = context.getResources().getDrawable(R.drawable.bola_azul);
        drawableBubble_quiet_amarilla = context.getResources().getDrawable(R.drawable.bola_amarilla);

        drawableBubble_move_roja = context.getResources().getDrawable(R.drawable.bola_roja);
        drawableBubble_move_verde = context.getResources().getDrawable(R.drawable.bola_verde);
        drawableBubble_move_azul = context.getResources().getDrawable(R.drawable.bola_azul);
        drawableBubble_move_amarilla = context.getResources().getDrawable(R.drawable.bola_amarilla);

        bubble_roja_quiet = new Grafico(this, drawableBubble_quiet_roja);
        bubble_roja_move = new Grafico(this, drawableBubble_move_roja);
        bubble_azul_quiet = new Grafico(this, drawableBubble_quiet_azul);
        bubble_azul_move = new Grafico(this, drawableBubble_move_azul);
        bubble_verde_quiet = new Grafico(this, drawableBubble_quiet_verde);
        bubble_verde_move = new Grafico(this, drawableBubble_move_verde);
        bubble_amarilla_quiet = new Grafico(this, drawableBubble_quiet_amarilla);
        bubble_amarilla_move = new Grafico(this, drawableBubble_move_amarilla);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int width = display.getWidth();

        SharedPreferences pref = context.getSharedPreferences("com.tompreug_migarpl.bubble_preferences", Context.MODE_PRIVATE);
        if (pref.getString("dificultad", "0").equals("0")) { //para dificultad facil
            numBubbles = 3;
            Activos =new boolean[3];
            tiradas = 5;
            modificador = ((width-3 * bubble_amarilla_move.getAncho())/2);
        }
        if (pref.getString("dificultad", "1").equals("1")) { //para dificultad medio
            numBubbles = 5;
            Activos =new boolean[5];
            tiradas = 6;
            modificador = ((width-5 * bubble_amarilla_move.getAncho())/2);
        }
        if (pref.getString("dificultad", "2").equals("2")) { //para dificultad dificil
            numBubbles = 7;
            Activos =new boolean[7];
            tiradas = 7;
            modificador = ((width-7 * bubble_amarilla_move.getAncho())/2);
        }

        Bubbles = new Vector<Grafico>();

        for (int i = 0; i < numBubbles; i++) { //no cambiar a "Bubbles.size()" lo he probado y no funciona
            // Funcion Random para crear bolas (rand Int)
            up = randInt(min, max); //para las bolas de arriba
            switch (up) {
                case 1:
                    Grafico bubble_roja = new Grafico(this, drawableBubble_roja);
                    bubble_roja.setcolor(1);
                    Bubbles.add(bubble_roja);
                    break;
                case 2:
                    Grafico bubble_azul = new Grafico(this, drawableBubble_azul);
                    bubble_azul.setcolor(2);
                    Bubbles.add(bubble_azul);
                    break;
                case 3:
                    Grafico bubble_verde = new Grafico(this, drawableBubble_verde);
                    bubble_verde.setcolor(3);
                    Bubbles.add(bubble_verde);
                    break;
                case 4:
                    Grafico bubble_amarilla = new Grafico(this, drawableBubble_amarilla);
                    bubble_amarilla.setcolor(4);
                    Bubbles.add(bubble_amarilla);
                    break;
                default:
                    break;
            }
        }
        down = randInt(min, max); //para que salga aleatoria la bola de abajo en el inicio
        switch (down) {
            case 1:
                bubble_roja_move.setcolor(1);
                bubble_quiet = bubble_roja_quiet;
                bubble_move = bubble_roja_move;
                break;
            case 2:
                bubble_azul_move.setcolor(2);
                bubble_quiet = bubble_azul_quiet;
                bubble_move = bubble_azul_move;
                break;
            case 3:
                bubble_verde_move.setcolor(3);
                bubble_quiet = bubble_verde_quiet;
                bubble_move = bubble_verde_move;
                break;
            case 4:
                bubble_amarilla_move.setcolor(4);
                bubble_quiet = bubble_amarilla_quiet;
                bubble_move = bubble_amarilla_move;
                break;
            default:
                break;
        }
    }

    private int alto_pant; //esto es para poder obtener las dimensiones de la pantalla para cualquier metodo

    @Override
    protected void onSizeChanged(int ancho, int alto, int ancho_anter, int alto_anter) { //metodo para posicionar los objetos
        super.onSizeChanged(ancho, alto, ancho_anter, alto_anter);

        alto_pant = alto; // se lo asigno a mi variable global

        // Una vez que conocemos nuestro ancho y alto.
        for (int i = 0; i < numBubbles; i++) {
            Bubbles.elementAt(i).setPosX((ancho - ((i + 1) * Bubbles.elementAt(i).getAncho()) - modificador)); // para colocar las bolas de arriba
            Bubbles.elementAt(i).setPosY(0);
            Activos[i]=true;
        }
        bubble_quiet.setPosX(ancho / 2 - (bubble_quiet.getAncho()) / 2); //para colocar la bola de abajo, en el inicio
        bubble_quiet.setPosY(alto - bubble_quiet.getAlto());

        ultimoProceso = System.currentTimeMillis();
        thread.start();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < Bubbles.size(); i++) {//esto es para dibujar las bolas de arriba
            if(Activos[i]) {
                Bubbles.elementAt(i).dibujaGrafico(canvas);
            }
        }
        if (bubbleActivo) {
            bubble_move.dibujaGrafico(canvas); //y para dibujar la bola en movimiento
        }
        else {
            bubble_quiet.dibujaGrafico(canvas); //esto es para dibujar la bola quieta
        }
    }

    protected synchronized void actualizaFisica() {
        long ahora = System.currentTimeMillis();
        int down = 0;
        // No hagas nada si el período de proceso no se ha cumplido.
        if (ultimoProceso + PERIODO_PROCESO > ahora) {
            return;
        }
        // Para una ejecución en tiempo real calculamos retardo
        double retardo = (ahora - ultimoProceso) / PERIODO_PROCESO;
        ultimoProceso = ahora; // Para la próxima vez
        // Actualizamos posición de la bola

        if (bubbleActivo) {
            bubble_move.incrementaPos(retardo);
            if (bubble_move.getPosY() < 1) { //Conseguir que se quede la bola en movimiento cuando supere el alto de la pantalla
                fallo.start();//fallo del tiro
                tirada++;
                bubbleActivo = false; //si supera el alto de la pantalla desaparece
                down = randInt(min, max); //para que salga aleatoria la bola de abajo en el inicio
                switch (down) {
                    case 1:
                        bubble_roja_move.setcolor(1);
                        bubble_quiet = bubble_roja_quiet;
                        bubble_move = bubble_roja_move;
                        break;
                    case 2:
                        bubble_azul_move.setcolor(2);
                        bubble_quiet = bubble_azul_quiet;
                        bubble_move = bubble_azul_move;
                        break;
                    case 3:
                        bubble_verde_move.setcolor(3);
                        bubble_quiet = bubble_verde_quiet;
                        bubble_move = bubble_verde_move;
                        break;
                    case 4:
                        bubble_amarilla_move.setcolor(4);
                        bubble_quiet = bubble_amarilla_quiet;
                        bubble_move = bubble_amarilla_move;
                        break;
                    default:
                        break;
                }
                bubble_quiet.setPosX(getWidth()/2-(bubble_quiet.getAncho())/2); //para colocar la bola de abajo, en los siguientes casos
                bubble_quiet.setPosY(getHeight()-bubble_quiet.getAlto());
            }
            else {
                for (int i = 0; i < Bubbles.size(); i++) {
                    if (bubble_move.verificaColision(Bubbles.elementAt(i))&& Activos[i]) {
                        destruyeBubble(i);
                        break;
                    }
                }
            }
            if (tirada == tiradas){ // si hemos llegado al maximo de tiradas
                mario.start(); //suena la musica game over
                ((Juego)getContext()).finish();
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

    private boolean disparo = false;
    private double angulo_touch;
    private double new_angulo_touch;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();

        angulo_touch = (Math.atan((alto_pant - y) / (bubble_quiet.getPosX() - x + (bubble_quiet.getAncho()) / 2)));
        if (angulo_touch < 0.0) {     // OJO Aquí entraremos cuando el angulo_touch sea negativo¡¡¡
            // angulo_touch está en radianes por que asi sale de 'atan'
            new_angulo_touch = angulo_touch;
            angulo_touch = Math.PI + new_angulo_touch;   //angulo_touch = pi (radianes) + ángulo obtenido  antes
            // con esto resolvemos el problema de que la bola salga hacia abajo al ser disparada.
        }

        if (angulo_touch>(40*Math.PI/180) && angulo_touch<(140*Math.PI/180)){
            if (bubbleActivo == false) { //dispara si la bubble esta parada
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        disparo = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (disparo) {
                            ActivaBubble();
                        }
                        break;
                }
            }
        }
        return true;
    }

    private void destruyeBubble(int i) {
        int down = 0;
        boolean contador = false;
        if(Bubbles.elementAt(i).getColor()== bubble_move.getColor()) {//compara el choque del mismo color
            choq.start(); //sonido destruye
            Activos[i]=false; // destruye la bola de arriba
            for (int a = i + 1; a < Bubbles.size(); a++) { //para que compare con las que estan alrededor (ascendente)
                if (Bubbles.elementAt(a).getColor() == bubble_move.getColor()) {
                    Activos[a]=false;
                } else a = Bubbles.size();
            }
            for (int a = i - 1; a >= 0; a--) { //(descendente)
                if (Bubbles.elementAt(a).getColor() == bubble_move.getColor()) {
                    Activos[a]=false;
                } else a = 0;
            }
        }else fallo.start();  // fallo del tiro
        bubbleActivo = false; //elimina la de abajo y pon otra
        tirada++; // incremento el contador de tirada
        down = randInt(min, max); //para que salga aleatoria la bola de abajo, en los siguientes casos
        switch (down) {
            case 1:
                bubble_roja_move.setcolor(1);
                bubble_quiet = bubble_roja_quiet;
                bubble_move = bubble_roja_move;
                break;
            case 2:
                bubble_azul_move.setcolor(2);
                bubble_quiet = bubble_azul_quiet;
                bubble_move = bubble_azul_move;
                break;
            case 3:
                bubble_verde_move.setcolor(3);
                bubble_quiet = bubble_verde_quiet;
                bubble_move = bubble_verde_move;
                break;
            case 4:
                bubble_amarilla_move.setcolor(4);
                bubble_quiet = bubble_amarilla_quiet;
                bubble_move = bubble_amarilla_move;
                break;
            default:
                break;
        }
        bubble_quiet.setPosX(getWidth()/2-(bubble_quiet.getAncho())/2); //para colocar la bola de abajo, en los siguientes casos
        bubble_quiet.setPosY(getHeight()-bubble_quiet.getAlto());
        for (int z=0; z<numBubbles; z++) { //recorro el vector para ver si hay alguna bubble activa
            if (Activos[z])
                contador = true;
        }
        if (contador == false){//si no hay bubbles termina el juego
            mp.start(); //suena las trompetas
            ((Juego)getContext()).finish();
        }
        if (tirada == tiradas){ // si hemos llegado al maximo de tiradas
            mario.start(); //suena la musica game over
            ((Juego)getContext()).finish();
        }
    }

    private void ActivaBubble() {
        bubble_move.setPosX(bubble_quiet.getPosX()); //para que salga donde esta la quieta
        bubble_move.setPosY(bubble_quiet.getPosY());
        bubble_move.setIncX(Math.cos(angulo_touch+Math.PI) * PASO_VELOCIDAD_BUBBLE); // incremento de X
        bubble_move.setIncY(Math.sin(-angulo_touch) * PASO_VELOCIDAD_BUBBLE); //incremento de Y
        bubbleActivo = true;
    }

}
