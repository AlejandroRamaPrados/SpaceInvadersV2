package com.politecnicomalaga.sp.model;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.Map;

public class DisparoAmi extends Disparo{
    //No hay atributos nuevos, por lo tanto, tampoco getters ni setters nuevos
    //Constructor
    public DisparoAmi(float x, float y, float width, float height, Estado estado, Direccion dir, String textura) {
        super(x, y, width, height, estado, dir, textura);
    }

    //Métodos
    //Si el disparo sale de la pantalla desaparece
    @Override
    public void desaparecer(float limiteSuperior) {
        if (this.getY() > limiteSuperior) {
            this.setEstado(Estado.MUERTO);
        }
    }
    //Comprobamos si el disparo ha colisionado con alguna nave enemiga
    public boolean comprobarColision(NaveEne[] enemigos) {
        // Si ya impactó (y está pendiente de borrar) evitamos "doblekill"
        // Y evitamos que una bala muerta siga matando mientras espera ser borrada
        if (this.getEstado() == Estado.MUERTO) return false;

        //Recorre todos los enemigos para ver si han tocado el disparo
        for (NaveEne enemigo : enemigos) {
            if (enemigo.estaVivo() && this.colision(enemigo)) {
                boolean muerto = enemigo.recibirDisparo(); //Tocar recibir disparos según la vida de la nave enemiga "1"
                this.setEstado(Estado.MUERTO);
                return muerto;
            }
        }
        return false;
    }

    public void pintar(SpriteBatch batch, Map<String, Texture> galeriaImagenes){
        batch.draw(galeriaImagenes.get(this.getTextura()),this.getX(),this.getY(), this.getWidth(), this.getHeight());
    }
}
