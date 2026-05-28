package com.politecnicomalaga.sp.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.politecnicomalaga.sp.Main;
import com.politecnicomalaga.sp.model.*;
import java.util.*;

public class Controlador {
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

    private enum Pantalla { MENU, JUEGO, AJUSTES }
    private Pantalla pantallaActual = Pantalla.MENU;
    private button btnJugar, btnSalir, btnAjustes, btnVolver, btnTitulo, btnMasVol, btnMenosVol;
    private float infoX, infoY, infoW, infoH, musicX, musicY, musicW, musicH, barraX, barraY, barraAncho, barraAlto;

    private Controlador(float anchoPantalla, float altoPantalla) {
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

        float bVs = 3.5f * uW; //Size de los botones de volumen
        btnMenosVol = new button(barraX - bVs - uW, barraY + (barraAlto - bVs)/2f, bVs, bVs, Ovni.Estado.VIVO, Ovni.Direccion.NOMOVER, "botonMenos");
        btnMasVol = new button(barraX + barraAncho + uW, barraY + (barraAlto - bVs)/2f, bVs, bVs, Ovni.Estado.VIVO, Ovni.Direccion.NOMOVER, "botonMas");
        btnVolver = new button(0.5f * uW, 2 * uH, 22 * uW, 10 * uH, Ovni.Estado.VIVO, Ovni.Direccion.NOMOVER, "botonSalir");
        explosiones = new ArrayList<>();
    }

    public static Controlador getInstance(float w, float h) {
        if (miSingle == null) {
            miSingle = new Controlador(w, h);
        }
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
    }

    public void simulaMundo(float delta) {
        fondo.forEach(e -> e.actualizar(delta, altoPantalla, anchoPantalla));
        if (pantallaActual != Pantalla.JUEGO) {
            if (musicaJuego.isPlaying()) {
                musicaJuego.stop();
            }
            if (!musicaMenu.isPlaying()) {
                musicaMenu.play();
            }
        } else if (musicaMenu.isPlaying()) {
            musicaMenu.stop();
        }

        if (jugando) {
            if ((contadorTiempoAmigo += delta) >= cadenciaAmiga) {
                naveAmiga.disparar();
                contadorTiempoAmigo = 0;
            }
            if ((getContadorTiempoEnemigo += delta) >= cadenciaEnemiga) {
                batallon.disparar();
                getContadorTiempoEnemigo = 0;
            }

            batallon.comprobarColisionesDisparo(naveAmiga);
            hematado(batallon, naveAmiga.getMisDisparos());
            batallon.comprobarColisionesFisicas(naveAmiga);

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
            batallon.mover(anchoPantalla,altoPantalla,(altoPantalla/100)*5f*0.8f, delta);
            naveAmiga.setDir(Ovni.Direccion.NOMOVER);
            naveAmiga.gestionarMisDisparos(altoPantalla);
            batallon.gestionarDisparos(0);
            for (int i = explosiones.size() - 1; i >= 0; i--) {
                if (explosiones.get(i).actualizar(delta)) {
                    explosiones.remove(i);
                }
            }

            if (!naveAmiga.estaVivo()) {
                jugando = false;
                musicaJuego.stop();
                pantallaActual = Pantalla.MENU;
            } else if (!batallon.hayNavesVivas()) {
                crearNuevoBatallon();
            } else if (!musicaJuego.isPlaying()) {
                musicaJuego.play();
            }
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
            btnTitulo.pintar(batch, galeria);
            btnJugar.pintar(batch, galeria);
            btnSalir.pintar(batch, galeria);
            btnAjustes.pintar(batch, galeria);
        } else if (pantallaActual == Pantalla.AJUSTES) {
            batch.draw(galeria.get("infoMenu"), infoX, infoY, infoW, infoH);
            batch.draw(galeria.get("musicHeader"), musicX, musicY, musicW, musicH);
            btnMasVol.pintar(batch, galeria);
            btnMenosVol.pintar(batch, galeria);
            batch.draw(galeria.get("barra2"), barraX, barraY, barraAncho, barraAlto);
            batch.draw(galeria.get("barra"), barraX, barraY, barraAncho * volumenActual, barraAlto);
            btnVolver.pintar(batch, galeria);
        }
    }

    public void cambiarSentidoNaveAmiga(float x) {
        naveAmiga.setDir(x > naveAmiga.getX() ? Ovni.Direccion.DERECHA : Ovni.Direccion.IZQUIERDA);
    }

    public void hematado(Batallon batallon, List<DisparoAmi> disparos) {
        for (DisparoAmi d : disparos) {
            if (batallon.comprobarSiMeHanDado(d)) {
                puntuacion += 10 + (int)(Math.random() * 15);
                crearExplosion(d.getX(), d.getY() + d.getHeight() + naveAmiga.getHeight()*0.25f, naveAmiga.getWidth()*1.5f);
            }
        }
    }

    public void pintarVida(SpriteBatch batch, Map<String, Texture> galeria) {
        float t = anchoPantalla * 0.1f;
        for (int i = 0; i < naveAmiga.getVidas(); i++) {
            batch.draw(galeria.get("vida.png"), (i*t/2), altoPantalla-t, t, t);
        }
    }

    public void pintarPuntuacion(SpriteBatch batch, Map<String, Texture> galeria) {
        String s = String.valueOf(puntuacion);
        float t = anchoPantalla * 0.04f; //tamaño
        float m = anchoPantalla * 0.02f; //margen
        float x = anchoPantalla - (s.length()*t) - m; //posición en x
        for (int i = 0; i < s.length(); i++) {
            batch.draw(galeria.get("Number" + s.charAt(i) + ".png"), x + (i*t), altoPantalla-t-m, t, t);
        }
    }

    public void crearExplosion(float x, float y, float tam) {
        explosiones.add(new Explosion(x - tam/2, y - tam/2, tam));
    }

    private void crearNuevoBatallon() {
        float uW = anchoPantalla/100f;
        float uH = altoPantalla/100f;
        float tE = uW*5f; //Tmaño nave escuadrón
        batallon = new Batallon(anchoPantalla%2, altoPantalla-tE*1.5f, uH*2, tE, tE*0.8f, Ovni.Estado.VIVO, Ovni.Direccion.DERECHA, "sprites/enemigo2.png", "sprites/enemigo1.png", 1, 2, 180, uW*1.5f, uH*6, 0.1f*anchoPantalla, 7, 5, 10, 0.06f*anchoPantalla, generarPatronesSimetricos());
    }

    private boolean[][] generarPatronesSimetricos() {
        boolean[][] patterns = new boolean[4][8]; //Mapeamos todas las naves pero como booleanos
        for (int row = 0; row < 4; row++) {
            boolean hasActiveCell = false; //Ponemos que de normal la fila no tiene ninguna celda activa
            for (int column = 0; column < 4; column++) {
                // Decidimos si esta celda está activa (75% de probabilidad)
                boolean isActive = Math.random() > 0.25;
                // Aplicamos el valor a la izquierda y su reflejo a la derecha
                patterns[row][column] = isActive;
                patterns[row][7 - column] = isActive;
                if (isActive) hasActiveCell = true;
            }
            // Si la fila se quedó vacía, activamos una columna aleatoria y su simétrica
            if (!hasActiveCell) {
                int randomColumnIndex = (int)(Math.random()*4);
                patterns[row][randomColumnIndex] = true;
                patterns[row][7 - randomColumnIndex] = true;
            }
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
}
