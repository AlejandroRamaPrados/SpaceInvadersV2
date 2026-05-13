package com.politecnicomalaga.sp.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.politecnicomalaga.sp.model.Batallon;
import com.politecnicomalaga.sp.model.DisparoAmi;
import com.politecnicomalaga.sp.model.DisparoEne;
import com.politecnicomalaga.sp.model.Escuadron;
import com.politecnicomalaga.sp.model.NaveAmi;
import com.politecnicomalaga.sp.model.NaveEne;
import com.politecnicomalaga.sp.model.Ovni;

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
    private boolean jugando;

    //CONSTRUCTOR
    private Controlador() {
        naveAmiga = new NaveAmi(300,0,60,60, Ovni.Estado.VIVO, Ovni.Direccion.NOMOVER,"sprites/naveJugador.png",1,120,15,30,8);
        velocidadNave = 1f;
        contadorTiempoAmigo=0;
        getContadorTiempoEnemigo=0;
        cadenciaAmiga= 180;
        cadenciaEnemiga=180;
        batallon=new Batallon(Gdx.graphics.getWidth()%2,Gdx.graphics.getHeight()-40,10, 50,40, Ovni.Estado.VIVO, Ovni.Direccion.DERECHA, "sprites/enemigo1.png",1,180,5,30,1,7,10,0.3f);
        jugando=true;
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
        naveAmiga.pintar(batch, galeriaImagenes);
        batallon.pintar(batch, galeriaImagenes);
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
            batallon.comprobarSiMeHanDado(disparoAmi);
        }
    }
    public void meHanTocado(Batallon batallon, NaveAmi naveAmiga) {
        batallon.comprobarColisionesFisicas(naveAmiga);
    }
    public boolean comprobarSiGano(Batallon batallon){
        return !batallon.hayNavesVivas();
    }
}
