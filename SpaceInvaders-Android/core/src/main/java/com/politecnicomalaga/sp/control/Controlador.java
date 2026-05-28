package com.politecnicomalaga.sp.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.politecnicomalaga.sp.Main;
import com.politecnicomalaga.sp.model.Batallon;
import com.politecnicomalaga.sp.model.DisparoAmi;
import com.politecnicomalaga.sp.model.DisparoEne;
import com.politecnicomalaga.sp.model.ElementoFondo;
import com.politecnicomalaga.sp.model.Escuadron;
import com.politecnicomalaga.sp.model.Explosion;
import com.politecnicomalaga.sp.model.NaveAmi;
import com.politecnicomalaga.sp.model.NaveEne;
import com.politecnicomalaga.sp.model.Ovni;
import com.politecnicomalaga.sp.model.button;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.badlogic.gdx.Preferences;

public class Controlador {
    private Preferences prefs;
    private int recordPuntuacion;
    private Music musicaJuego, musicaMenu;
    private List<Explosion> explosiones;
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

    private float btnAncho, btnAlto;
    private enum Pantalla { MENU, JUEGO, AJUSTES}
    private Pantalla pantallaActual = Pantalla.MENU;
    private button btnJugar, btnSalir, btnAjustes, btnVolver, btnTitulo;

    private float volumenActual; // Rango de 0 a 1
    private button btnMasVol, btnMenosVol;
    private float barraX, barraY, barraAncho, barraAlto;

    // Elementos visuales de Ajustes
    private float infoX, infoY, infoW, infoH;
    private float musicX, musicY, musicW, musicH;

    //CONSTRUCTOR


    private Controlador(float anchoPantalla, float altoPantalla) {
        // En el constructor Controlador(...)
        prefs = Gdx.app.getPreferences("SpaceInvadersSave");
        recordPuntuacion = prefs.getInteger("highscore", 0);
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


        jugando=false; //Comienza el juego parado

        fondo = new ArrayList<>();
        // Velocidades ajustadas: % de pantalla por segundo (independiente de FPS)
        for (int i = 0; i < 20; i++) {
            fondo.add(new ElementoFondo((float)Math.random()*anchoPantalla,(float)Math.random()*altoPantalla,uW,uW,"estrella.png",altoPantalla*0.1f));
        }
        fondo.add(new ElementoFondo((float)Math.random()*anchoPantalla,altoPantalla*0.2f,uW*25,uW*25,"planet09.png",altoPantalla*0.25f));
        fondo.add(new ElementoFondo((float)Math.random()*anchoPantalla,altoPantalla*0.5f,uW*35,uW*35,"planet08.png",altoPantalla*0.20f));
        fondo.add(new ElementoFondo((float)Math.random()*anchoPantalla,altoPantalla*0.8f,uW*30,uW*30,"planet07.png",altoPantalla*0.35f));

        // Inicializamos las músicas
        volumenActual = 0.5f;

        musicaJuego = Gdx.audio.newMusic(Gdx.files.internal("sounds/main_music1.mp3"));
        musicaJuego.setLooping(true);
        musicaJuego.setVolume(0.2f);

        musicaMenu = Gdx.audio.newMusic(Gdx.files.internal("sounds/menuAmbiental.mp3"));
        musicaMenu.setLooping(true);
        musicaMenu.setVolume(0.3f);

        //Config para los botones adaptada a la pantalla
        btnAncho = 20 * uW;
        btnAlto = 13* uH;
        float gap = 2 * uH;

        float centerX = (anchoPantalla - btnAncho) / 2f;
        float centerY = (altoPantalla - btnAlto) / 2f;

        // --- PANTALLA MENU ---
        btnJugar = new button(centerX, centerY, btnAncho, btnAlto, Ovni.Estado.VIVO, Ovni.Direccion.NOMOVER, "botonComenzar");
        btnAjustes = new button(centerX, btnJugar.getY() -btnAlto-gap, btnAncho, btnAlto, Ovni.Estado.VIVO, Ovni.Direccion.NOMOVER, "botonAjustes");

        float tituloAncho = 3 * btnAncho;
        float tituloX = (anchoPantalla - tituloAncho) / 2f;
        btnTitulo = new button(tituloX, btnJugar.getY() + btnAlto + 2*gap, tituloAncho, btnAlto, Ovni.Estado.VIVO, Ovni.Direccion.NOMOVER, "titulo");

        btnSalir = new button(centerX, btnAjustes.getY() - btnAlto - gap, btnAncho, btnAlto, Ovni.Estado.VIVO, Ovni.Direccion.NOMOVER, "botonSalir");

        // --- PANTALLA AJUSTES (Layout Vertical Corregido) ---
        infoW = 32 * uW;
        infoH = 77 * uH;
        infoX = (anchoPantalla - infoW) / 2f;
        infoY = altoPantalla - infoH - 3 * uH;

        musicW = 20 * uW;
        musicH = 12 * uH;
        musicX = (anchoPantalla - musicW) / 2f;
        musicY = infoY - musicH ;

        barraAncho = 40 * uW;
        barraAlto = 3 * uH;
        barraX = (anchoPantalla - barraAncho) / 2f;
        barraY = musicY - barraAlto - uH;

        float btnVolSize = 3.5f * uW;
        btnMenosVol = new button(barraX - btnVolSize - uW, barraY + (barraAlto/2f) - (btnVolSize/2f), btnVolSize, btnVolSize, Ovni.Estado.VIVO, Ovni.Direccion.NOMOVER, "botonMenos");
        btnMasVol = new button(barraX + barraAncho + uW, barraY + (barraAlto/2f) - (btnVolSize/2f), btnVolSize, btnVolSize, Ovni.Estado.VIVO, Ovni.Direccion.NOMOVER, "botonMas");

        float btnVolverW = 22 * uW;
        float btnVolverH = 10 * uH;
        btnVolver = new button( 0.5f* uW, 2 * uH, btnVolverW, btnVolverH, Ovni.Estado.VIVO, Ovni.Direccion.NOMOVER, "botonSalir");

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
        float yReal = altoPantalla - y;

        if (pantallaActual == Pantalla.MENU) {
            if (btnJugar.click(x, yReal)) {
                reiniciarJuego();
                jugando = true;
                pantallaActual = Pantalla.JUEGO;
            } else if (btnSalir.click(x, yReal)) {
                Gdx.app.exit();
            } else if (btnAjustes.click(x, yReal)) {
                pantallaActual = Pantalla.AJUSTES;
            }
        }
        else if (pantallaActual == Pantalla.AJUSTES) {
            if (btnVolver.click(x, yReal)) {
                pantallaActual = Pantalla.MENU;
            }
            else if (btnMasVol.click(x, yReal)) {
                volumenActual = Math.min(1.0f, volumenActual + 0.01f);
                actualizarVolumen();
            } else if (btnMenosVol.click(x, yReal)) {
                volumenActual = Math.max(0.0f, volumenActual - 0.01f);
                actualizarVolumen();
            }
        }
        else if (pantallaActual == Pantalla.JUEGO) {
            cambiarSentidoNaveAmiga(x);
        }
    }
    public void simulaMundo(float delta){
        //Actualizar fondo con delta para que sea responsive
        for (ElementoFondo elementoFondo: fondo){
            elementoFondo.actualizar(delta, altoPantalla, anchoPantalla);
        }

        // Gestión de la música según la pantalla actual
        if (pantallaActual == Pantalla.MENU || pantallaActual == Pantalla.AJUSTES) {
            if (musicaJuego.isPlaying()) musicaJuego.stop();
            if (!musicaMenu.isPlaying()) musicaMenu.play();
        } else {
            if (musicaMenu.isPlaying()) musicaMenu.stop();
        }

        if (jugando){
            jugando = naveAmiga.estaVivo() && batallon.tieneTropas();

            // Si tras comprobar resulta que has muerto, salimos para que no ejecute el resto
            if (!jugando) {
                comprobarYGuardarRecord();
                musicaJuego.stop();
                pantallaActual = Pantalla.MENU;
                return;
            }
            if (!musicaJuego.isPlaying()) musicaJuego.play();

            // Comprobar si he ganado
            if (comprobarSiGano(batallon)) {
                comprobarYGuardarRecord();
                jugando = false;
                pantallaActual = Pantalla.MENU; // <--- Importante para que salgan los botones
                musicaJuego.stop();
                return; // Salimos del método ya que no hay nada más que simular
            }

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
        if (!jugando && pantallaActual == Pantalla.JUEGO) musicaJuego.stop();
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
            for (Explosion explosion : explosiones) {
                explosion.pintar(batch, galeriaImagenes);
            }
        }
        else if (pantallaActual == Pantalla.MENU) {
            btnTitulo.pintar(batch, galeriaImagenes);
            btnJugar.pintar(batch, galeriaImagenes);
            btnSalir.pintar(batch, galeriaImagenes);
            btnAjustes.pintar(batch, galeriaImagenes);

            // --- DIBUJAR RECORD ---
            float tamNum = anchoPantalla * 0.03f;
            float altoImagenLabel = tamNum * 0.8f;
            float anchoImagenLabel = tamNum * 4f;

            float margen = anchoPantalla * 0.02f;

            // Calculamos la posición X para que el número aparezca a la derecha y el label justo antes
            String recordS = String.valueOf(recordPuntuacion);
            float anchoTotalRecord = anchoImagenLabel + (recordS.length() * tamNum);

            float recordX = anchoPantalla - anchoTotalRecord - margen;
            float recordY = altoPantalla - tamNum - margen;

            // 1. Dibujamos el asset de "highscore"
            batch.draw(galeriaImagenes.get("highscore"), recordX, recordY + (tamNum - altoImagenLabel)/2f, anchoImagenLabel, altoImagenLabel);

            // 2. Dibujamos los números justo después (sumando el ancho de la imagen)
            pintarRecord(batch, galeriaImagenes, recordX + anchoImagenLabel, recordY, tamNum);
        }
        else if (pantallaActual == Pantalla.AJUSTES) {
            // Dibujamos elementos centrados
            batch.draw(galeriaImagenes.get("infoMenu"), infoX, infoY, infoW, infoH);
            batch.draw(galeriaImagenes.get("musicHeader"), musicX, musicY, musicW, musicH);

            // Control de volumen
            btnMasVol.pintar(batch, galeriaImagenes);
            btnMenosVol.pintar(batch, galeriaImagenes);
            batch.draw(galeriaImagenes.get("barra2"), barraX, barraY, barraAncho, barraAlto);
            batch.draw(galeriaImagenes.get("barra"), barraX, barraY, barraAncho * volumenActual, barraAlto);

            // Botón volver centrado abajo
            btnVolver.pintar(batch, galeriaImagenes);
        }
    }


    public void cambiarSentidoNaveAmiga (float x){
        float naveX = naveAmiga.getX();
        Ovni.Direccion actual = naveAmiga.getDir();

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
    public void reiniciarJuego() {
        float uW = anchoPantalla / 100f;
        float uH = altoPantalla / 100f;
        float tamEnemigo = uW * 5f;

        // 1. Resetear nave amiga
        naveAmiga.setVidas(3); // O las vidas iniciales que prefieras
        naveAmiga.setEstado(Ovni.Estado.VIVO);
        naveAmiga.setX(anchoPantalla / 2); // Centrar nave

        // 2. Resetear Batallón (esto debería recrear los enemigos)
        // Dependiendo de cómo esté tu clase Batallon, podrías necesitar un método reset allí
        this.batallon = new Batallon(
            anchoPantalla % 2,
            altoPantalla - tamEnemigo * 1.5f,
            uH * 2,
            tamEnemigo,
            tamEnemigo * 0.8f,
            Ovni.Estado.VIVO,
            Ovni.Direccion.DERECHA,
            "sprites/enemigo1.png",
            "sprites/enemigo2.png",
            1,
            2,
            180,
            uW * 1.5f,
            uH * 6,
            0.1f * anchoPantalla,
            7,
            5,
            10,
            0.06f * anchoPantalla
        );

        // 3. Resetear puntuación y listas
        naveAmiga.getMisDisparos().clear();
        this.puntuacion = 0;
        this.explosiones.clear();

        // 4. Volver a activar la música
        musicaMenu.stop();
        musicaJuego.play();
    }
    private void actualizarVolumen() {
        musicaJuego.setVolume(volumenActual);
        musicaMenu.setVolume(volumenActual);
    }
    private void comprobarYGuardarRecord() {
        if (puntuacion > recordPuntuacion) {
            recordPuntuacion = puntuacion;
            prefs.putInteger("highscore", recordPuntuacion);
            prefs.flush(); // IMPORTANTE: Esto guarda físicamente los datos en el disco
        }
    }

    public void pintarRecord(SpriteBatch batch, Map<String, Texture> galeriaImagenes, float x, float y, float tamNum) {
        String recordString = String.valueOf(recordPuntuacion);

        for (int i = 0; i < recordString.length(); i++) {
            char digito = recordString.charAt(i);
            // Usamos tamNum que viene por parámetro
            Texture tex = galeriaImagenes.get("Number" + digito + ".png");
            if (tex != null) {
                batch.draw(tex, x + (i * tamNum), y, tamNum, tamNum);
            }
        }
    }
}
