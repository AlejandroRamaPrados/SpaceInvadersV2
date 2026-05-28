package com.politecnicomalaga.sp.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Map;

public class Batallon {
    //Atributos
    //Composición de 4 escuadrones
    private Escuadron[] escuadrones;
    private Ovni.Direccion direccionActual;
    private float velocidad;

    // Para la animación de aparición
    private float yObjetivo;
    private boolean apareciendo;
    private float velocidadAparicion;

    // Constructor actualizado para soportar patrones
    public Batallon(float xInicial, float yInicial, float espacioVertical,
                    float width, float height,
                    Ovni.Estado estado,
                    Ovni.Direccion direccionActual,
                    String texturaNormal,
                    String texturaTanque,
                    int vidasNormal,
                    int vidasTanque,
                    float cadencia,
                    float anchoBala,
                    float altoBala,
                    float velocidadBala,
                    int probabilidadDisparoNormal,
                    int probabilidadDisparoTanque,
                    float espacioEntreNaves,
                    float velocidad,
                    boolean[][] patterns) {

        this.velocidad = velocidad;
        this.direccionActual = direccionActual;
        this.escuadrones = new Escuadron[4];

        // Animación: Empezamos más arriba de lo indicado
        this.yObjetivo = yInicial;
        float yInicioAnimacion = yInicial + 300f; // 300 pixeles arriba
        this.apareciendo = true;
        this.velocidadAparicion = 150f; // Pixeles por segundo

        loadEscuadrones(xInicial, yInicioAnimacion, espacioVertical, width, height,
            estado, direccionActual, texturaNormal, texturaTanque, vidasNormal, vidasTanque, cadencia, anchoBala, altoBala, velocidadBala,
            probabilidadDisparoNormal, probabilidadDisparoTanque, espacioEntreNaves, patterns);
    }

    private void loadEscuadrones(float x, float y, float espacioVertical,
                                 float width, float height,
                                 Ovni.Estado estado,
                                 Ovni.Direccion dir,
                                 String texturaNormal,
                                 String texturaTanque,
                                 int vidasNormal,
                                 int vidasTanque,
                                 float cadencia,
                                 float anchoBala,
                                 float altoBala,
                                 float velocidadBala,
                                 int probabilidadDisparoNormal,
                                 int probabilidadDisparoTanque,
                                 float espacioEntreNaves,
                                 boolean[][] patterns) {

        for (int i = 0; i < this.escuadrones.length; i++) {
            float yEscuadron = y - (i * (height + espacioVertical));
            boolean esTanque = (i >= 2);
            String textura = esTanque ? texturaTanque : texturaNormal;
            int vidas = esTanque ? vidasTanque : vidasNormal;
            int probDisparo = esTanque ? probabilidadDisparoTanque : probabilidadDisparoNormal;

            boolean[] pattern = (patterns != null && i < patterns.length) ? patterns[i] : null;

            this.escuadrones[i] = new Escuadron(
                x,
                yEscuadron,
                width,
                height,
                estado,
                dir,
                textura,
                vidas,
                cadencia,
                anchoBala,
                altoBala,
                velocidadBala,
                probDisparo,
                espacioEntreNaves,
                pattern
            );
        }
    }

    public void mover(float anchoPantalla, float altoPantalla, float cuantoBaja, float delta){
        if (escuadrones == null || escuadrones.length == 0) return;

        // Si está apareciendo, solo se mueve hacia abajo hasta su posición
        if (apareciendo) {
            float paso = velocidadAparicion * delta;
            for (Escuadron esc : escuadrones) {
                esc.bajar(paso);
            }
            // Comprobamos si el primer escuadrón ha llegado a su Y objetivo
            if (escuadrones[0].getNavesEnemigas()[0].getY() <= yObjetivo) {
                apareciendo = false;
            }
            return;
        }

        boolean tocarBorde = false;
        for (Escuadron esc : escuadrones) {
            if (esc.haTocadoBorde(anchoPantalla, direccionActual)){
                tocarBorde = true;
                break;
            }
        }
        if (tocarBorde){
            cambiarDireccionYBajarse(cuantoBaja);
        }
        else {
            for (Escuadron esc : escuadrones) {
                esc.moverLateralmente(direccionActual, velocidad);
            }
        }
    }

    private void cambiarDireccionYBajarse(float cuantoBaja) {
        direccionActual = (direccionActual == Ovni.Direccion.DERECHA) ? Ovni.Direccion.IZQUIERDA : Ovni.Direccion.DERECHA;
        for (Escuadron esc : escuadrones) {
            esc.bajar(cuantoBaja);
        }
        for (Escuadron esc : escuadrones) {
            esc.moverLateralmente(direccionActual, velocidad);
        }
    }

    public void disparar() {
        if (apareciendo) return; // No disparan mientras aparecen
        for (Escuadron esc : escuadrones) {
            esc.disparar();
        }
    }

    public void gestionarDisparos(float limiteMuerte) {
        for (Escuadron esc : escuadrones) {
            esc.gestionarDisparosEnemigos(limiteMuerte);
        }
    }

    public boolean tieneTropas() {
        if (escuadrones == null || escuadrones.length == 0) return false;
        for (Escuadron esc : escuadrones) {
            if (esc.tieneNavesVivas()) {
                return true;
            }
        }
        return false;
    }

    public void comprobarColisionesDisparo(NaveAmi naveAmiga){
        for (Escuadron esc : escuadrones){
            esc.comprobarColisionesDisparo(naveAmiga);
        }
    }

    public boolean comprobarSiMeHanDado(DisparoAmi disparoAmi){
        for (Escuadron esc : escuadrones){
            if (esc.comprobarSiMeHanDado(disparoAmi)) return true;
        }
        return false;
    }

    public void comprobarColisionesFisicas(NaveAmi naveAmiga){
        for (Escuadron esc : escuadrones){
            esc.comprobarColisionesFisicas(naveAmiga);
        }
    }

    public boolean hayNavesVivas(){
        return tieneTropas();
    }

    public void pintar(SpriteBatch batch, Map<String, Texture> galeriaImagenes){
        for (Escuadron esc : escuadrones){
            esc.pintar(batch, galeriaImagenes);
        }
    }
}
