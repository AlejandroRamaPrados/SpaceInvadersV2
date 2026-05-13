package com.politecnicomalaga.sp.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.Map;

public class NaveAmi extends Nave {
    //Atributos
    private ArrayList<DisparoAmi> misDisparos; //Para el crud de disparos

    //Constructor
    public NaveAmi(float x, float y, float width, float height, Estado estado, Direccion dir, String textura, int vidas, float cadencia, float anchoBala, float altoBala, float velocidadBala) {
        super(x, y, width, height, estado, dir, textura, vidas, cadencia, anchoBala, altoBala, velocidadBala);
        this.misDisparos = new ArrayList<>();
    }
    //Getters y Setters
    public ArrayList<DisparoAmi> getMisDisparos() {
        return misDisparos;
    }
    public void setMisDisparos(ArrayList<DisparoAmi> misDisparos) {
        this.misDisparos = misDisparos;
    }

    //Métodos
    //Crear los disparos(Create)
    @Override
    public void disparar() { //Aquí no tenemos random no tiene sentido usarlo, Definimos la cadencia que tendra nuestra nave en el controlador
        if (estaVivo()) {
            // Cálculo de posición centrada (Igual que en NaveEne pero hacia ARRIBA)
            float posX = getX() + getMitadWidth() - (getAnchoBala() / 2f);
            float posY = getY() + getMitadHeight();

            // Creamos el disparo y lo añadimos a la lista
            DisparoAmi nuevoDisparo = new DisparoAmi(posX, posY, getAnchoBala(), getAltoBala(), Estado.VIVO, Direccion.ARRIBA, "sprites/disparoAmi.png");
            misDisparos.add(nuevoDisparo);
            sonidoDisparo.play(0.1f);
        }
    }

    //Gestionar la salida de la pantalla de los disparos (Delete)
    @Override
    public void gestionarMisDisparos(float limiteSuperior) {
        for (int i = misDisparos.size() - 1 ; i >= 0; i--){//Recorremos el array al reves para no liarla con los indices
            DisparoAmi d = misDisparos.get(i);

            //Si el disparo esta vivo es decir no ha colisionado lo movemos
            if (d.getEstado() == Estado.VIVO){
                d.mover(Direccion.ARRIBA, getVelocidadBala());
                d.desaparecer(limiteSuperior); //Preguntamos si se ha salido de la pantalla y este setea el estado a muerto
            }
            if (d.getEstado() == Estado.MUERTO) misDisparos.remove(i); //Lo eliminamos si la comprobación de desaparecer de la pantalla ya nos da que esta Muerto
        }
    }

    public void pintar(SpriteBatch batch, Map<String, Texture> galeriaImagenes){
        batch.draw(galeriaImagenes.get(this.getTextura()),this.getX(),this.getY(), this.getWidth(), this.getHeight());

        for (DisparoAmi d: misDisparos){
            if(d.getEstado() == Estado.VIVO) d.pintar(batch, galeriaImagenes);
        }
    }
}
