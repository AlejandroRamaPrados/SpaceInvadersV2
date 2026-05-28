package com.politecnicomalaga.sp.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.lang.reflect.Array;
import java.util.Map;

public class Escuadron {
    NaveEne [] navesEnemigas;
    float  espacioEntreNaves;

    public Escuadron(float x, float y, float width, float height, Ovni.Estado estado,
                     Ovni.Direccion dir, String textura, int vidas, float cadencia, float anchoBala, float altoBala,
                     float velocidadBala, int probabilidadDisparo, float espacioEntreNaves, boolean[] pattern){

        navesEnemigas = new NaveEne[8];
        navesEnemigas = loadNaves(navesEnemigas, x, y, width, height, estado, dir, textura, vidas, cadencia,
            anchoBala, altoBala, velocidadBala, probabilidadDisparo, espacioEntreNaves, pattern);
    }

    public NaveEne [] getNavesEnemigas() {
        return navesEnemigas;
    }

    public NaveEne[] loadNaves
        (NaveEne [] navesEnemigas, float x, float y, float width, float height, Ovni.Estado estado,
         Ovni.Direccion dir, String textura, int vidas, float cadencia, float anchoBala, float altoBala,
         float velocidadBala, int probabilidadDisparo, float espacioEntreNaves, boolean[] pattern) {

        for (int i = 0; i < navesEnemigas.length; i++) {
            //Hacemos una X diferente para cada nave para que no se solapen
            float xNave = x + (i * (width + espacioEntreNaves));
            navesEnemigas[i] = new NaveEne(xNave, y, width, height, estado, dir, textura, vidas, cadencia, anchoBala, altoBala, velocidadBala, probabilidadDisparo);

            // Aplicamos el patrón: si la posición es falsa, la nave nace muerta (hueco)
            if (pattern != null && i < pattern.length && !pattern[i]) {
                navesEnemigas[i].setEstado(Ovni.Estado.MUERTO);
            }
        }

            return navesEnemigas;
    }
    public boolean haTocadoBorde(float anchoPantalla, Ovni.Direccion dirActual){
        for (NaveEne naveEne : navesEnemigas) {
            if (!naveEne.estaVivo()){
                continue;
            }
            if (naveEne.getX() + naveEne.getWidth() >= anchoPantalla && dirActual == Ovni.Direccion.DERECHA ) {
                return true;
            }
            if (naveEne.getX() <= 0 && dirActual == Ovni.Direccion.IZQUIERDA) {
                return true;
            }
        }
        return false;
    }
    public void moverLateralmente(Ovni.Direccion direccionActual, float velocidad) {
        for (NaveEne naveEne : navesEnemigas) {
            if (naveEne.estaVivo()) {
                naveEne.mover(direccionActual, velocidad);
            }
        }
    }
    public void bajar(float cuantoBaja) {
        for (NaveEne naveEne :navesEnemigas) {
            //Aqui bajan todas las naves para que no haya bug qeu la primera no se mueva y bajen de forma infinita
            naveEne.setY(naveEne.getY() - cuantoBaja);

        }
    }
    public void gestionarDisparosEnemigos(float limiteInferior) {
        for (NaveEne naveEne :navesEnemigas) {
            if (naveEne.estaVivo()) {
                naveEne.gestionarMisDisparos(limiteInferior);
            }
        }
    }
    public boolean tieneNavesVivas(){
        //Si alguna esta viva devuelve true
        for (NaveEne naveEne :navesEnemigas) {
            if (naveEne.estaVivo()) {
                return true;
            }
        }
        return false;
    }
    public void disparar(){
        //Cada nave dispara segun la probabilidad de disparo
        for (NaveEne naveEne :navesEnemigas){
            if (naveEne.estaVivo()) {
                naveEne.disparar();
            }
        }
    }

    public void comprobarColisionesDisparo(NaveAmi naveAmiga){
        for (NaveEne naveEne :navesEnemigas){
            if (naveEne.estaVivo()) {
                naveEne.comprobarColisionDisparos(naveAmiga);
            }
        }
    }

    public boolean comprobarSiMeHanDado(DisparoAmi disparoAmi){
        return disparoAmi.comprobarColision(this.navesEnemigas);
    }

    public void comprobarColisionesFisicas(NaveAmi naveAmiga){
        for (NaveEne naveEne :navesEnemigas){
            if (naveEne.estaVivo() && naveEne.colision(naveAmiga)) {
                naveEne.setEstado(Ovni.Estado.MUERTO);
                naveAmiga.setVidas(naveAmiga.getVidas() - 1);
                if (naveAmiga.getVidas() <= 0) {
                    naveAmiga.setVidas(0);
                    naveAmiga.setEstado(Ovni.Estado.MUERTO);
                }
            }
        }
    }

    public void pintar(SpriteBatch batch, Map<String, Texture> galeriaImagenes){
        for (NaveEne naveEne :navesEnemigas){
            if (naveEne.estaVivo()) {
                naveEne.pintar(batch, galeriaImagenes);
            }
        }
    }
}
