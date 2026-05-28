package com.politecnicomalaga.sp.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.politecnicomalaga.sp.Main;
import com.politecnicomalaga.sp.model.*;
import java.util.*;
import com.badlogic.gdx.Preferences;

public class Controlador {
    private Preferences prefs;
    private int recordPuntuacion;
    private Music musicaJuego, musicaMenu;
    private List<Explosion> explosiones;
    private static Controlador miSingle;
    private NaveAmi naveAmiga;
    private final float velocidadNave, cadenciaAmiga, cadenciaEnemiga, anchoPantalla, altoPantalla;
    private float contadorTiempoAmigo, getContadorTiempoEnemigo, volumenActual = 0.5f;
    private Batallon batallon;
    private List<ElementoFondo> fondo;
    private int puntuacion;
    private boolean jugando;

    private enum Pantalla { MENU, JUEGO, AJUSTES , DERROTA}
    private Pantalla pantallaActual = Pantalla.MENU;
    private button btnJugar, btnSalir, btnAjustes, btnVolver, btnTitulo, btnMasVol, btnMenosVol, btnMenuDerrota;
    private float infoX, infoY, infoW, infoH, musicX, musicY, musicW, musicH, barraX, barraY, barraAncho, barraAlto;

    private Controlador(float anchoPantalla, float altoPantalla) {
        prefs = Gdx.app.getPreferences("SpaceInvadersSave");
        recordPuntuacion = prefs.getInteger("highscore", 0);
        this.anchoPantalla = anchoPantalla;
        this.altoPantalla = altoPantalla;
        float uW = anchoPantalla / 100f;
        float uH = altoPantalla / 100f;

        velocidadNave = anchoPantalla * 0.2f;
        cadenciaAmiga = 1.5f;
        cadenciaEnemiga = 2.0f;

        naveAmiga = new NaveAmi(anchoPantalla/2 - uW*3, 0, uW*6, uW*6, Ovni.Estado.VIVO, Ovni.Direccion.NOMOVER, "sprites/naveJugador.png", 3, 120, uW*1.5f, uW*5, 0.2f*anchoPantalla);
        crearNuevoBatallon();

        fondo = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            fondo.add(new ElementoFondo((float)Math.random()*anchoPantalla, (float)Math.random()*altoPantalla, uW, uW, "estrella.png", altoPantalla*0.1f));
        }
        fondo.add(new ElementoFondo((float)Math.random()*anchoPantalla, altoPantalla*0.2f, uW*25, uW*25, "planet09.png", altoPantalla*0.25f));
        fondo.add(new ElementoFondo((float)Math.random()*anchoPantalla, altoPantalla*0.5f, uW*35, uW*35, "planet08.png", altoPantalla*0.20f));
        fondo.add(new ElementoFondo((float)Math.random()*anchoPantalla, altoPantalla*0.8f, uW*30, uW*30, "planet07.png", altoPantalla*0.35f));

        musicaJuego = Gdx.audio.newMusic(Gdx.files.internal("sounds/main_music1.mp3"));
        musicaMenu = Gdx.audio.newMusic(Gdx.files.internal("sounds/menuAmbiental.mp3"));
        musicaJuego.setLooping(true);
        musicaMenu.setLooping(true);
        actualizarVolumen();

        float bW = 20 * uW;
        float bH = 13 * uH;
        float gap = 2 * uH;
        float cX = (anchoPantalla - bW) / 2f;
        btnJugar = new button(cX, (altoPantalla - bH) / 2f, bW, bH, Ovni.Estado.VIVO, Ovni.Direccion.NOMOVER, "botonComenzar");
        btnAjustes = new button(cX, btnJugar.getY() - bH - gap, bW, bH, Ovni.Estado.VIVO, Ovni.Direccion.NOMOVER, "botonAjustes");
        btnTitulo = new button((anchoPantalla - 3*bW)/2f, btnJugar.getY() + bH + 2*gap, 3*bW, bH, Ovni.Estado.VIVO, Ovni.Direccion.NOMOVER, "titulo");
        btnSalir = new button(cX, btnAjustes.getY() - bH - gap, bW, bH, Ovni.Estado.VIVO, Ovni.Direccion.NOMOVER, "botonSalir");

        infoW = 32 * uW;
        infoH = 77 * uH;
        infoX = (anchoPantalla - infoW) / 2f;
        infoY = altoPantalla - infoH - 3 * uH;
        musicW = 20 * uW;
        musicH = 12 * uH;
        musicX = (anchoPantalla - musicW) / 2f;
        musicY = infoY - musicH;
        barraAncho = 40 * uW;
        barraAlto = 3 * uH;
        barraX = (anchoPantalla - barraAncho) / 2f;
        barraY = musicY - barraAlto - uH;

        float bVs = 3.5f * uW;
        btnMenosVol = new button(barraX - bVs - uW, barraY + (barraAlto - bVs)/2f, bVs, bVs, Ovni.Estado.VIVO, Ovni.Direccion.NOMOVER, "botonMenos");
        btnMasVol = new button(barraX + barraAncho + uW, barraY + (barraAlto - bVs)/2f, bVs, bVs, Ovni.Estado.VIVO, Ovni.Direccion.NOMOVER, "botonMas");
        btnVolver = new button(0.5f * uW, 2 * uH, 22 * uW, 10 * uH, Ovni.Estado.VIVO, Ovni.Direccion.NOMOVER, "botonSalir");
        btnMenuDerrota = new button(cX, btnAjustes.getY() - bH - gap, bW, bH, Ovni.Estado.VIVO, Ovni.Direccion.NOMOVER, "botonSalir");
        explosiones = new ArrayList<>();
    }

    public static Controlador getInstance(float w, float h) {
        if (miSingle == null) miSingle = new Controlador(w, h);
        return miSingle;
    }

    public void click(float x, float y) {
        float yr = altoPantalla - y;
        if (pantallaActual == Pantalla.MENU) {
            if (btnJugar.click(x, yr)) {
                reiniciarJuego();
                jugando = true;
                pantallaActual = Pantalla.JUEGO;
            } else if (btnSalir.click(x, yr)) {
                Main.salir();
            } else if (btnAjustes.click(x, yr)) {
                pantallaActual = Pantalla.AJUSTES;
            }
        } else if (pantallaActual == Pantalla.AJUSTES) {
            if (btnVolver.click(x, yr)) {
                pantallaActual = Pantalla.MENU;
            } else if (btnMasVol.click(x, yr)) {
                volumenActual = Math.min(1.0f, volumenActual + 0.005f);
                actualizarVolumen();
            } else if (btnMenosVol.click(x, yr)) {
                volumenActual = Math.max(0.0f, volumenActual - 0.005f);
                actualizarVolumen();
            }
        } else if (pantallaActual == Pantalla.JUEGO) {
            cambiarSentidoNaveAmiga(x);
        }
        else if (pantallaActual == Pantalla.DERROTA) {
            if (btnJugar.click(x, yr)) {
                reiniciarJuego();
                jugando = true;
                pantallaActual = Pantalla.JUEGO;
            } else if (btnMenuDerrota.click(x, yr)) {
                pantallaActual = Pantalla.MENU;
            }
        }
    }

    public void simulaMundo(float delta) {
        fondo.forEach(e -> e.actualizar(delta, altoPantalla, anchoPantalla));

        if (pantallaActual != Pantalla.JUEGO) {
            if (musicaJuego.isPlaying()) musicaJuego.stop();
            if (!musicaMenu.isPlaying()) musicaMenu.play();
        } else {
            if (musicaMenu.isPlaying()) musicaMenu.stop();
        }

        if (jugando) {
            if (!naveAmiga.estaVivo() || !batallon.tieneTropas()) {
                comprobarYGuardarRecord();
                musicaJuego.stop();
                jugando = false;
                pantallaActual = (!naveAmiga.estaVivo()) ? Pantalla.DERROTA : Pantalla.MENU;
                return;
            }

            if (!musicaJuego.isPlaying()) musicaJuego.play();

            contadorTiempoAmigo += delta;
            if (contadorTiempoAmigo >= cadenciaAmiga) {
                naveAmiga.disparar();
                contadorTiempoAmigo = 0;
            }

            getContadorTiempoEnemigo += delta;
            if (getContadorTiempoEnemigo >= cadenciaEnemiga) {
                batallon.disparar();
                getContadorTiempoEnemigo = 0;
            }

            batallon.comprobarColisionesDisparo(naveAmiga);
            hematado(batallon, naveAmiga.getMisDisparos());
            batallon.comprobarColisionesFisicas(naveAmiga);

            if (naveAmiga.getX() > anchoPantalla - naveAmiga.getWidth()) naveAmiga.setX(anchoPantalla - naveAmiga.getWidth());
            else if (naveAmiga.getX() < 0) naveAmiga.setX(0);

            naveAmiga.mover(naveAmiga.getDir(), velocidadNave);
            batallon.mover(anchoPantalla, altoPantalla, (altoPantalla / 100) * 5f * 0.8f, delta);
            naveAmiga.setDir(Ovni.Direccion.NOMOVER);

            naveAmiga.gestionarMisDisparos(altoPantalla);
            batallon.gestionarDisparos(0);

            for (int i = explosiones.size() - 1; i >= 0; i--) {
                if (explosiones.get(i).actualizar(delta)) explosiones.remove(i);
            }

            if (!batallon.hayNavesVivas()) crearNuevoBatallon();
        }
    }

    public void pintar(SpriteBatch batch, Map<String, Texture> galeria) {
        fondo.forEach(e -> e.pintar(batch, galeria));

        if (pantallaActual == Pantalla.JUEGO) {
            naveAmiga.pintar(batch, galeria);
            batallon.pintar(batch, galeria);
            pintarPuntuacion(batch, galeria);
            pintarVida(batch, galeria);
            explosiones.forEach(e -> e.pintar(batch, galeria));
        } else if (pantallaActual == Pantalla.MENU) {
            float uW = anchoPantalla / 100f;
            float uH = altoPantalla / 100f;
            float bW = 20 * uW; // Tamaño original definido en tu constructor
            float bH = 13 * uH;
            float gapMenu = 2 * uH;
            float cX = (anchoPantalla - bW) / 2f;

            btnJugar.setWidth(bW);
            btnJugar.setHeight(bH);
            btnJugar.setX(cX);
            btnJugar.setY((altoPantalla - bH) / 2f);

            btnAjustes.setWidth(bW);
            btnAjustes.setHeight(bH);
            btnAjustes.setX(cX);
            btnAjustes.setY(btnJugar.getY() - bH - gapMenu);

            btnSalir.setWidth(bW);
            btnSalir.setHeight(bH);
            btnSalir.setX(cX);
            btnSalir.setY(btnAjustes.getY() - bH - gapMenu);


            btnTitulo.pintar(batch, galeria);
            btnJugar.pintar(batch, galeria);
            btnSalir.pintar(batch, galeria);
            btnAjustes.pintar(batch, galeria);

            float tamNum = anchoPantalla * 0.03f;
            float recordX = anchoPantalla - (tamNum * 4f + String.valueOf(recordPuntuacion).length() * tamNum) - anchoPantalla * 0.02f;
            float recordY = altoPantalla - tamNum - anchoPantalla * 0.02f;

            batch.draw(galeria.get("highscore"), recordX, recordY, tamNum * 4f, tamNum * 0.8f);
            pintarRecord(batch, galeria, recordX + tamNum * 4f, recordY, tamNum);

        } else if (pantallaActual == Pantalla.AJUSTES) {
            batch.draw(galeria.get("infoMenu"), infoX, infoY, infoW, infoH);
            batch.draw(galeria.get("musicHeader"), musicX, musicY, musicW, musicH);
            btnMasVol.pintar(batch, galeria);
            btnMenosVol.pintar(batch, galeria);
            batch.draw(galeria.get("barra2"), barraX, barraY, barraAncho, barraAlto);
            batch.draw(galeria.get("barra"), barraX, barraY, barraAncho * volumenActual, barraAlto);
            btnVolver.pintar(batch, galeria);
        }
        else if (pantallaActual == Pantalla.DERROTA) {
            // 1. DIBUJAR IMAGEN GAME OVER
            Texture goTex = galeria.get("gameOver");
            float xGO = 0;
            float anchoGO = 0;

            if (goTex != null) {
                anchoGO = anchoPantalla * 0.85f;
                float proporcion = (float) goTex.getHeight() / (float) goTex.getWidth();
                float altoGO = anchoGO * proporcion;

                // Evitar que tape los botones
                if (altoGO > altoPantalla * 0.50f) {
                    altoGO = altoPantalla * 0.50f;
                    anchoGO = altoGO / proporcion;
                }

                xGO = (anchoPantalla - anchoGO) / 2f;
                float yGO = altoPantalla * 0.42f;

                batch.draw(goTex, xGO, yGO, anchoGO, altoGO);
            }
            // 2. PUNTUACIÓN / RÉCORD CENTRADO
            float tamNumG = anchoPantalla * 0.03f;

            String recordString = String.valueOf(puntuacion);
            float anchoRecord = recordString.length() * tamNumG;

            // Centrado respecto a la pantalla
            float puntosX = (anchoPantalla - anchoRecord) / 2f;

            // Si prefieres centrar respecto a la imagen Game Over:
            // float puntosX = xGO + (anchoGO - anchoRecord) / 2f;

            pintarPuntuacionActual(
                batch,
                galeria,
                puntosX,
                altoPantalla * 0.30f,
                tamNumG
            );

            // 3. BOTONES
            float btnW = anchoPantalla * 0.15f;
            float btnH = altoPantalla * 0.08f;
            float gapBtn = anchoPantalla * 0.05f;

            btnJugar.setX((anchoPantalla / 2f) - btnW - gapBtn / 2f);
            btnJugar.setY(altoPantalla * 0.10f);
            btnJugar.setWidth(btnW);
            btnJugar.setHeight(btnH);

            btnMenuDerrota.setX((anchoPantalla / 2f) + gapBtn / 2f);
            btnMenuDerrota.setY(altoPantalla * 0.10f);
            btnMenuDerrota.setWidth(btnW);
            btnMenuDerrota.setHeight(btnH);

            btnJugar.pintar(batch, galeria);
            btnMenuDerrota.pintar(batch, galeria);
        }
    }

    public void cambiarSentidoNaveAmiga(float x) {
        naveAmiga.setDir(x > naveAmiga.getX() ? Ovni.Direccion.DERECHA : Ovni.Direccion.IZQUIERDA);
    }

    public void hematado(Batallon batallon, List<DisparoAmi> disparos) {
        for (DisparoAmi d : disparos) {
            if (batallon.comprobarSiMeHanDado(d)) {
                puntuacion += 10 + (int)(Math.random() * 15);
                crearExplosion(d.getX(), d.getY() + d.getHeight(), naveAmiga.getWidth()*1.5f);
            }
        }
    }

    public void pintarVida(SpriteBatch batch, Map<String, Texture> galeria) {
        float t = anchoPantalla * 0.1f;
        for (int i = 0; i < naveAmiga.getVidas(); i++) batch.draw(galeria.get("vida.png"), (i*t/2), altoPantalla-t, t, t);
    }

    public void pintarPuntuacion(SpriteBatch batch, Map<String, Texture> galeria) {
        String s = String.valueOf(puntuacion);
        float t = anchoPantalla * 0.04f, m = anchoPantalla * 0.02f;
        float x = anchoPantalla - (s.length()*t) - m;
        for (int i = 0; i < s.length(); i++) batch.draw(galeria.get("Number" + s.charAt(i) + ".png"), x + (i*t), altoPantalla-t-m, t, t);
    }

    public void crearExplosion(float x, float y, float tam) {
        explosiones.add(new Explosion(x - tam/2, y - tam/2, tam));
    }

    private void crearNuevoBatallon() {
        float uW = anchoPantalla/100f, uH = altoPantalla/100f, tE = uW*5f;
        batallon = new Batallon(anchoPantalla%2, altoPantalla-tE*1.5f, uH*2, tE, tE*0.8f, Ovni.Estado.VIVO, Ovni.Direccion.DERECHA, "sprites/enemigo2.png", "sprites/enemigo1.png", 1, 2, 180, uW*1.5f, uH*6, 0.1f*anchoPantalla, 7, 5, 10, 0.06f*anchoPantalla, generarPatronesSimetricos());
    }

    private boolean[][] generarPatronesSimetricos() {
        boolean[][] patterns = new boolean[4][8];
        for (int row = 0; row < 4; row++) {
            boolean hasActiveCell = false;
            for (int column = 0; column < 4; column++) {
                boolean isActive = Math.random() > 0.25;
                patterns[row][column] = patterns[row][7 - column] = isActive;
                if (isActive) hasActiveCell = true;
            }
            if (!hasActiveCell) patterns[row][(int)(Math.random()*4)] = patterns[row][7 - (int)(Math.random()*4)] = true;
        }
        return patterns;
    }

    public void reiniciarJuego() {
        naveAmiga.setVidas(3);
        naveAmiga.setEstado(Ovni.Estado.VIVO);
        naveAmiga.setX(anchoPantalla/2 - naveAmiga.getWidth()/2);
        crearNuevoBatallon();
        naveAmiga.getMisDisparos().clear();
        puntuacion = 0;
        explosiones.clear();
        musicaMenu.stop();
        musicaJuego.play();
    }

    private void actualizarVolumen() {
        musicaJuego.setVolume(volumenActual * 0.4f);
        musicaMenu.setVolume(volumenActual * 0.6f);
    }

    private void comprobarYGuardarRecord() {
        if (puntuacion > recordPuntuacion) {
            recordPuntuacion = puntuacion;
            prefs.putInteger("highscore", recordPuntuacion);
            prefs.flush();
        }
    }

    public void pintarRecord(SpriteBatch batch, Map<String, Texture> galeria, float x, float y, float tamNum) {
        String s = String.valueOf(recordPuntuacion);
        for (int i = 0; i < s.length(); i++) batch.draw(galeria.get("Number" + s.charAt(i) + ".png"), x + (i * tamNum), y, tamNum, tamNum);
    }

    public void pintarPuntuacionActual(SpriteBatch batch, Map<String, Texture> galeria, float x, float y, float tam) {
        String s = String.valueOf(puntuacion);
        for (int i = 0; i < s.length(); i++) batch.draw(galeria.get("Number" + s.charAt(i) + ".png"), x + (i * tam), y, tam, tam);
    }
}
