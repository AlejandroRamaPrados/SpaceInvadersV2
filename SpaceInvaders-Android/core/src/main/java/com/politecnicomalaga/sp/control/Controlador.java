package com.politecnicomalaga.sp.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.politecnicomalaga.sp.model.Batallon;
import com.politecnicomalaga.sp.model.DisparoAmi;
import com.politecnicomalaga.sp.model.DisparoEne;
import com.politecnicomalaga.sp.model.ElementoFondo;
import com.politecnicomalaga.sp.model.Escuadron;
import com.politecnicomalaga.sp.model.NaveAmi;
import com.politecnicomalaga.sp.model.NaveEne;
import com.politecnicomalaga.sp.model.Ovni;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Controlador {
    private Music musica;
    private static Controlador miSingle;
    private NaveAmi naveAmiga;
    private final float velocidadNave;
    private final int cadenciaAmiga,cadenciaEnemiga;
    private int contadorTiempoAmigo, getContadorTiempoEnemigo;
    private Batallon batallon;
    private List<ElementoFondo> fondo;

    private int puntuacion;

    private boolean jugando;

    private float btnJugarX, btnJugarY, btnAncho, btnAlto;

    private float btnSalirX, btnSalirY;
    private enum Pantalla { MENU, JUEGO, AJUSTES}
    private Pantalla pantallaActual = Pantalla.MENU;
    private float btnAjustesX, btnAjustesY;
    private float btnVolverX, btnVolverY, btnVolverAncho, btnVolverAlto;


    //CONSTRUCTOR


    private Controlador() {

        naveAmiga = new NaveAmi(300,0,60,60, Ovni.Estado.VIVO, Ovni.Direccion.NOMOVER,"sprites/naveJugador.png",3,120,15,30,8);
        velocidadNave = 1f;
        contadorTiempoAmigo=0;
        puntuacion=0;
        getContadorTiempoEnemigo=0;
        cadenciaAmiga= 180;
        cadenciaEnemiga=180;
        batallon=new Batallon(Gdx.graphics.getWidth()%2,
            Gdx.graphics.getHeight()-40,
            10,
            50,
            40,
            Ovni.Estado.VIVO,
            Ovni.Direccion.DERECHA,
            "sprites/enemigo1.png",
            "sprites/enemigo2.png",
            1,
            2,
            180,
            5,
            30,
            1,
            7,
            5,
            10,
            0.3f);

        jugando=false; //Comienza el juego parado

        fondo = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            fondo.add(new ElementoFondo((float)Math.random()*800,(float)Math.random()*600,4,4,"estrella.png",0.5f));
        }
        fondo.add(new ElementoFondo((float)Math.random()*800,200,100,100,"planet09.png",0.3f));
        fondo.add(new ElementoFondo((float)Math.random()*800,500,140,140,"planet08.png",0.4f));
        fondo.add(new ElementoFondo((float)Math.random()*800,800,120,120,"planet07.png",0.2f));

        musica = Gdx.audio.newMusic(
            Gdx.files.internal("sounds/main_music1.mp3")
        );
        musica.setLooping(true);
        musica.setVolume(0.2f);

        //Config para los botones
        float gap = 30f;
        btnAncho = 250f;
        btnAlto = 90f;

        // BotonAjustes (Lo ponemos 120 píxeles más abajo que el de salir, ajustar mas adelante) uwu
        btnAjustesX = ((Gdx.graphics.getWidth()/2) - (btnAncho/2));
        btnAjustesY = (Gdx.graphics.getHeight()/2) - (btnAlto/2); // Va en medio

        //BotonJugar
        btnJugarX = ((Gdx.graphics.getWidth()/2) - (btnAncho/2));  // Centrado en X
        btnJugarY = btnAjustesY + gap + btnAlto;  // Encima de ajustes

        // BotonSalir (Lo ponemos 120 píxeles más abajo que el de jugar, ajustar mas adelante)
        btnSalirX = ((Gdx.graphics.getWidth()/2) - (btnAncho/2));  // Centrado en X igual Jugar
        btnSalirY = btnAjustesY - gap - btnAlto;                                       // Abajo de ajustes

        // Botón volver (para salir de ajustes, payaso)
        btnVolverAncho = 200f;
        btnVolverAlto = 70f;
        btnVolverX = 20f; // Esquina inferior
        btnVolverY = 20f;
    }

    //Otros métodos
    public static Controlador getInstance(){
        if (miSingle == null){
            miSingle= new Controlador();
        }
        return miSingle;
    }
    public void click (float x, float y){
        float yReal = Gdx.graphics.getHeight() - y;

        if (pantallaActual == Pantalla.MENU) {
            // Lógica de JUGAR
            if (x >= btnJugarX && x <= (btnJugarX + btnAncho) && yReal >= btnJugarY && yReal <= (btnJugarY + btnAlto)) {
                jugando = true;
                pantallaActual = Pantalla.JUEGO;
            }
            // Lógica de SALIR
            else if (x >= btnSalirX && x <= (btnSalirX + btnAncho) && yReal >= btnSalirY && yReal <= (btnSalirY + btnAlto)) {
                Gdx.app.exit();
            }
            // Lógica de IR A AJUSTES
            else if (x >= btnAjustesX && x <= (btnAjustesX + btnAncho) && yReal >= btnAjustesY && yReal <= (btnAjustesY + btnAlto)) {
                pantallaActual = Pantalla.AJUSTES;
            }
        }
        else if (pantallaActual == Pantalla.AJUSTES) {
            // Lógica de VOLVER al menú
            if (x >= btnVolverX && x <= (btnVolverX + btnVolverAncho) && yReal >= btnVolverY && yReal <= (btnVolverY + btnVolverAlto)) {
                pantallaActual = Pantalla.MENU;
            }
        }
        else if (pantallaActual == Pantalla.JUEGO) {
            cambiarSentidoNaveAmiga(x);
        }
    }
    public void simulaMundo(float anchoPantalla, float altoPantalla){
        //Actualizar fondo
        for (ElementoFondo elementoFondo: fondo){
            elementoFondo.actualizar(altoPantalla, anchoPantalla);
        }

        if (jugando){
            jugando = naveAmiga.estaVivo() && batallon.tieneTropas();

            // Si tras comprobar resulta que has muerto, salimos para que no ejecute el resto
            if (!jugando) {
                musica.stop();
                pantallaActual = Pantalla.MENU;
                return;
            }

            musica.play();
            // Comprobar si he ganado
            if (comprobarSiGano(batallon)) {
                jugando = false;
                pantallaActual = Pantalla.MENU; // <--- Importante para que salgan los botones
                musica.stop();
                return; // Salimos del método ya que no hay nada más que simular
            }

            //disparo yo?
            contadorTiempoAmigo++;
            if (contadorTiempoAmigo==cadenciaAmiga){
                naveAmiga.disparar();
                contadorTiempoAmigo=0;
            }

            //disparan los enemigos?
            getContadorTiempoEnemigo++;
            if (getContadorTiempoEnemigo==cadenciaEnemiga){
                batallon.disparar();
                getContadorTiempoEnemigo=0;
            }

            //Colisiones
            hanDadoNaveAmiga(batallon, naveAmiga); //Con un disparo
            hematado(batallon, naveAmiga.getMisDisparos()); //He dado mi disparo
            meHanTocado(batallon, naveAmiga); // Me han chocado fisicamente

            //Movimiento
            if (naveAmiga.getX()>anchoPantalla-naveAmiga.getWidth()){
                naveAmiga.setX(anchoPantalla-naveAmiga.getWidth());
                naveAmiga.setDir(Ovni.Direccion.NOMOVER);
            }
            if (naveAmiga.getX()<0){
                naveAmiga.setX(0);
                naveAmiga.setDir(Ovni.Direccion.NOMOVER);
            }
            naveAmiga.mover(naveAmiga.getDir(),velocidadNave);
            batallon.mover(anchoPantalla,altoPantalla,20);

            //gestiono todos los disparos
            naveAmiga.gestionarMisDisparos(altoPantalla);
            batallon.gestionarDisparos(0);
        }
        if (!jugando) musica.stop();
    }

    public void pintar(SpriteBatch batch, Map<String, Texture> galeriaImagenes){
        // El fondo siempre bby
        for (ElementoFondo elementoFondo: fondo){
            elementoFondo.pintar(batch, galeriaImagenes);
        }

        if (pantallaActual == Pantalla.JUEGO) {
            naveAmiga.pintar(batch, galeriaImagenes);
            batallon.pintar(batch, galeriaImagenes);
            pintarPuntuacion(batch, galeriaImagenes);
            pintarVida(batch, galeriaImagenes);
        }
        else if (pantallaActual == Pantalla.MENU) {
            batch.draw(galeriaImagenes.get("botonComenzar"), btnJugarX, btnJugarY, btnAncho, btnAlto);
            batch.draw(galeriaImagenes.get("botonSalir"), btnSalirX, btnSalirY, btnAncho, btnAlto);
            // Tengo que meter este boton a la galeria :(
            batch.draw(galeriaImagenes.get("botonAjustes"), btnAjustesX, btnAjustesY, btnAncho, btnAlto);
        }
        else if (pantallaActual == Pantalla.AJUSTES) {
            // 1. Dibujamos un panel de fondo para la información
            batch.draw(galeriaImagenes.get("fondoEstrellas"), 100, 100, Gdx.graphics.getWidth()-200, Gdx.graphics.getHeight()-200);

            // 2. Dibujamos el botón de volver
            batch.draw(galeriaImagenes.get("botonSalir"), btnVolverX, btnVolverY, btnVolverAncho, btnVolverAlto);

        }
    }


    public void cambiarSentidoNaveAmiga (float x){
        float naveX = naveAmiga.getX();
        Ovni.Direccion actual = naveAmiga.getDir();

        if (x>naveX){
            naveAmiga.setDir(actual== Ovni.Direccion.DERECHA ? Ovni.Direccion.NOMOVER: Ovni.Direccion.DERECHA);
        } else if (x< naveX) {
            naveAmiga.setDir(actual== Ovni.Direccion.IZQUIERDA? Ovni.Direccion.NOMOVER: Ovni.Direccion.IZQUIERDA);
        }
    }

    public void hanDadoNaveAmiga(Batallon batallon, NaveAmi naveAmiga){
        batallon.comprobarColisionesDisparo(naveAmiga);
    }
    public  void hematado(Batallon batallon, List<DisparoAmi> disparoAmis){
        for (DisparoAmi disparoAmi: disparoAmis){
            if (batallon.comprobarSiMeHanDado(disparoAmi)) puntuacion+=10+ (int)(Math.random()*15);
        }
    }
    public void meHanTocado(Batallon batallon, NaveAmi naveAmiga) {
        batallon.comprobarColisionesFisicas(naveAmiga);
    }
    public boolean comprobarSiGano(Batallon batallon){
        return !batallon.hayNavesVivas();
    }

    public void pintarVida(SpriteBatch batch, Map<String, Texture> galeriaImagenes){
        for (int i = 0; i < naveAmiga.getVidas(); i++) {
            batch.draw(galeriaImagenes.get("vida.png"),20+(i*35),Gdx.graphics.getHeight()-70,60,60);
        }
    }
    public void pintarPuntuacion(SpriteBatch batch, Map<String, Texture> galeriaImagenes){
        String puntuacionString = String.valueOf(puntuacion);
        float xInicial = Gdx.graphics.getWidth()-puntuacionString.length()*17-10;
        for (int i = 0; i < puntuacionString.length(); i++) {
            char digito = puntuacionString.charAt(i);
            batch.draw(galeriaImagenes.get("Number"+digito+".png"),xInicial + (i*22), Gdx.graphics.getHeight()-45,15,15);
        }
    }
}
