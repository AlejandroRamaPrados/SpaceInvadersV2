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
import com.politecnicomalaga.sp.model.Explosion;
import com.politecnicomalaga.sp.model.NaveAmi;
import com.politecnicomalaga.sp.model.NaveEne;
import com.politecnicomalaga.sp.model.Ovni;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Controlador {
    private List<Explosion> explosiones;
    private Music musica;
    private static Controlador miSingle;
    private NaveAmi naveAmiga;
    private final float velocidadNave;
    private final float cadenciaAmiga,cadenciaEnemiga;
    private float contadorTiempoAmigo, getContadorTiempoEnemigo;
    private Batallon batallon;
    private List<ElementoFondo> fondo;
    private int puntuacion;
    private boolean jugando;

    // Variables de control responsive
    private final float velBase; // Velocidad adaptada al ancho
    private final float anchoPantalla,altoPantalla;

    //CONSTRUCTOR
    private Controlador(float anchoPantalla, float altoPantalla) {
        //Inicializamos variables de control responsive
        this.anchoPantalla = anchoPantalla;
        this.altoPantalla = altoPantalla;
        velBase = anchoPantalla*0.2f;

        //Definición de unidad de medida (1% del ancho)
        float uW = anchoPantalla / 100f;
        float uH = altoPantalla / 100f;

        //Inicialización de contadores
        contadorTiempoAmigo=0;
        getContadorTiempoEnemigo=0;
        puntuacion=0;
        cadenciaAmiga= 1.5f; //Ahora las cadencais se miden en segundos en vez de frames
        cadenciaEnemiga=2.0f;

        //Inicialización de elementos
        float tamNave = uW *6f;
        naveAmiga = new NaveAmi(anchoPantalla/2 - tamNave/2, 0, tamNave, tamNave, Ovni.Estado.VIVO, Ovni.Direccion.NOMOVER,
                                "sprites/naveJugador.png",3,120,uW*1.5f,uW*5,0.2f*anchoPantalla);
        velocidadNave = velBase;

        float tamEnemigo = uW*5f;
        batallon=new Batallon(anchoPantalla%2, altoPantalla-tamEnemigo*1.5f, uH*2,
            tamEnemigo, tamEnemigo*0.8f, Ovni.Estado.VIVO, Ovni.Direccion.DERECHA,
            "sprites/enemigo1.png", "sprites/enemigo2.png", 1,
            2, 180, uW*1.5f, uH*6, 0.1f*anchoPantalla, 7,
            5, 10, 0.06f*anchoPantalla);

        jugando=true;

        fondo = new ArrayList<>();
        // Velocidades ajustadas: % de pantalla por segundo (independiente de FPS)
        for (int i = 0; i < 20; i++) {
            fondo.add(new ElementoFondo((float)Math.random()*anchoPantalla,(float)Math.random()*altoPantalla,uW,uW,"estrella.png",altoPantalla*0.1f));
        }
        fondo.add(new ElementoFondo((float)Math.random()*anchoPantalla,altoPantalla*0.2f,uW*25,uW*25,"planet09.png",altoPantalla*0.25f));
        fondo.add(new ElementoFondo((float)Math.random()*anchoPantalla,altoPantalla*0.5f,uW*35,uW*35,"planet08.png",altoPantalla*0.20f));
        fondo.add(new ElementoFondo((float)Math.random()*anchoPantalla,altoPantalla*0.8f,uW*30,uW*30,"planet07.png",altoPantalla*0.35f));

        musica = Gdx.audio.newMusic(
            Gdx.files.internal("sounds/main_music1.mp3")
        );
        musica.setLooping(true);
        musica.setVolume(0.2f);

        explosiones = new ArrayList<>();
    }

    //Otros métodos
    public static Controlador getInstance(float anchoPantalla, float altoPantalla){
        if (miSingle == null){
            miSingle= new Controlador(anchoPantalla, altoPantalla);
        }
        return miSingle;
    }
    public void click (float x, float y){
        cambiarSentidoNaveAmiga(x);
    }
    public void simulaMundo(float delta){
        //Actualizar fondo con delta para que sea responsive
        for (ElementoFondo elementoFondo: fondo){
            elementoFondo.actualizar(delta, altoPantalla, anchoPantalla);
        }

        //Comprobar si he muerto
        jugando = naveAmiga.estaVivo() && batallon.tieneTropas();

        if (jugando){
            musica.play();
            //Comprobar si he ganado
            jugando=!comprobarSiGano(batallon);

            //disparo yo?
            contadorTiempoAmigo+=delta;
            if (contadorTiempoAmigo>=cadenciaAmiga){
                naveAmiga.disparar();
                contadorTiempoAmigo=0f;
            }

            //disparan los enemigos?
            getContadorTiempoEnemigo+=delta;
            if (getContadorTiempoEnemigo>=cadenciaEnemiga){
                batallon.disparar();
                getContadorTiempoEnemigo=0f;
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
            batallon.mover(anchoPantalla,altoPantalla,(altoPantalla/100)*5f*0.8f);
            naveAmiga.setDir(Ovni.Direccion.NOMOVER);

            //gestiono todos los disparos
            naveAmiga.gestionarMisDisparos(altoPantalla);
            batallon.gestionarDisparos(0);

            for (int i = explosiones.size() - 1; i >= 0; i--) {
                if (explosiones.get(i).actualizar(delta)) {
                    explosiones.remove(i);
                }
            }
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

        for (Explosion explosion : explosiones) {
            explosion.pintar(batch, galeriaImagenes);
        }
    }

    public void cambiarSentidoNaveAmiga (float x){
        float naveX = naveAmiga.getX();

        if (x>naveX){
            naveAmiga.setDir(Ovni.Direccion.DERECHA);
        } else if (x< naveX) {
            naveAmiga.setDir(Ovni.Direccion.IZQUIERDA);
        }
    }

    public void hanDadoNaveAmiga(Batallon batallon, NaveAmi naveAmiga){
        batallon.comprobarColisionesDisparo(naveAmiga);
    }
    public  void hematado(Batallon batallon, List<DisparoAmi> disparoAmis){
        for (DisparoAmi disparoAmi: disparoAmis){
            if (batallon.comprobarSiMeHanDado(disparoAmi)) {
                puntuacion+=10+ (int)(Math.random()*15);

                float impactoX = disparoAmi.getX();
                float impactoY = disparoAmi.getY() + disparoAmi.getHeight() + (naveAmiga.getHeight() * 0.25f);
                crearExplosion(impactoX, impactoY, naveAmiga.getWidth() * 1.5f);
            }
        }
    }
    public void meHanTocado(Batallon batallon, NaveAmi naveAmiga) {
        batallon.comprobarColisionesFisicas(naveAmiga);
    }
    public boolean comprobarSiGano(Batallon batallon){
        return !batallon.hayNavesVivas();
    }

    public void pintarVida(SpriteBatch batch, Map<String, Texture> galeriaImagenes){
        float tamIcono = anchoPantalla * 0.1f;
        float margen = anchoPantalla * 0.0001f;
        for (int i = 0; i < naveAmiga.getVidas(); i++) {
            batch.draw(galeriaImagenes.get("vida.png"),margen +(i*tamIcono/2),altoPantalla-tamIcono-margen,tamIcono,tamIcono);
        }
    }
    public void pintarPuntuacion(SpriteBatch batch, Map<String, Texture> galeriaImagenes){
        String puntuacionString = String.valueOf(puntuacion);
        float tamNum = anchoPantalla * 0.04f;
        float margen = anchoPantalla * 0.02f;
        float xInicial = anchoPantalla - (puntuacionString.length()*tamNum)-margen;
        for (int i = 0; i < puntuacionString.length(); i++) {
            char digito = puntuacionString.charAt(i);
            batch.draw(galeriaImagenes.get("Number"+digito+".png"),xInicial + (i*tamNum), altoPantalla-tamNum-margen,tamNum,tamNum);
        }
    }

    public void crearExplosion(float x, float y, float tam){
        explosiones.add(new Explosion(x - tam/2, y - tam/2, tam));
    }
}
