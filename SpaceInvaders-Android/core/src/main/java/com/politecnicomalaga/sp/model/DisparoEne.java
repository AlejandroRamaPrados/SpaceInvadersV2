package com.politecnicomalaga.sp.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Map;

public class DisparoEne extends Disparo{
    //No hay atributos nuevos, por lo tanto, tampoco getters ni setters nuevos
    //Constructor
    public DisparoEne(float x, float y, float width, float height, Estado estado, Direccion dir, String textura) {
        super(x, y, width, height, estado, dir, textura);
    }
    //Métodos
    //Si el disparo sale de la pantalla desaparece
    @Override
    public void desaparecer(float limiteInferior) {
        if (this.getY()<=limiteInferior) {
            this.setEstado(Estado.MUERTO);
        }
    }
    //Comprobamos si el disparo ha colisionado con la nave amiga
    public boolean comprobarColision(NaveAmi naveAmi) {
        // Si ya impactó (y está pendiente de borrar) evitamos "doblekill"
        // Y evitamos que una bala muerta siga matando mientras espera ser borrada
        if (this.getEstado() == Estado.MUERTO) return false;

        if (naveAmi.estaVivo() && this.colision(naveAmi)) {
            naveAmi.recibirDisparo();
            this.setEstado(Estado.MUERTO);
            return true;
        }
        return false;
    }

    public void pintar(SpriteBatch batch, Map<String, Texture> galeriaImagenes){
        batch.draw(galeriaImagenes.get(this.getTextura()),this.getX(),this.getY(), this.getWidth(), this.getHeight());
    }
}
