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

    //CONSTRUCTOR
    private Controlador() {
        naveAmiga = new NaveAmi(300,0,60,60, Ovni.Estado.VIVO, Ovni.Direccion.NOMOVER,"sprites/naveJugador.png",3,120,15,30,8);
        velocidadNave = 1f;
        contadorTiempoAmigo=0;
        puntuacion=0;
        getContadorTiempoEnemigo=0;
        cadenciaAmiga= 180;
        cadenciaEnemiga=180;
        batallon=new Batallon(Gdx.graphics.getWidth()%2,Gdx.graphics.getHeight()-100,10, 50,40, Ovni.Estado.VIVO, Ovni.Direccion.DERECHA, "sprites/enemigo1.png",1,180,5,30,1,7,10,0.3f);
        jugando=true;

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
    }

    //Otros métodos
    public static Controlador getInstance(){
        if (miSingle == null){
            miSingle= new Controlador();
        }
        return miSingle;
    }
    public void click (float x, float y){
        cambiarSentidoNaveAmiga(x);
    }
    public void simulaMundo(float anchoPantalla, float altoPantalla){
        //Actualizar fondo
        for (ElementoFondo elementoFondo: fondo){
            elementoFondo.actualizar(altoPantalla, anchoPantalla);
        }

        //Comprobar si he muerto
        jugando = naveAmiga.estaVivo() && batallon.tieneTropas();

        if (jugando){
            musica.play();
            //Comprobar si he ganado
            jugando=!comprobarSiGano(batallon);

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
        for (ElementoFondo elementoFondo: fondo){
            elementoFondo.pintar(batch, galeriaImagenes);
        }
        naveAmiga.pintar(batch, galeriaImagenes);
        batallon.pintar(batch, galeriaImagenes);

        pintarPuntuacion(batch, galeriaImagenes);
        pintarVida(batch, galeriaImagenes);

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
