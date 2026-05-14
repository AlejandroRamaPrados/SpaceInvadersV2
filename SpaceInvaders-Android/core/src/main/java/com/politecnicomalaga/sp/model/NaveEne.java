package com.politecnicomalaga.sp.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.Map;

public class NaveEne extends Nave{

    //Atributos
    private int probabilidadDisparo; //Entre 0 y 100 la seteamos en el controlador
    private ArrayList<DisparoEne> misDisparos; //Lista de disparos
    //Constructor
    public NaveEne(float x, float y, float width, float height, Estado estado, Direccion dir, String textura, int vidas, float cadencia, float anchoBala, float altoBala, float velocidadBala, int probabilidadDisparo) {
        super(x, y, width, height, estado, dir, textura, vidas, cadencia, anchoBala, altoBala, velocidadBala);
        this.probabilidadDisparo = probabilidadDisparo;
        this.misDisparos = new ArrayList<>();
    }
    //Getters y Setters
    public int getProbabilidadDisparo() {
        return probabilidadDisparo;
    }
    public void setProbabilidadDisparo(int probabilidadDisparo) {
        this.probabilidadDisparo = probabilidadDisparo;
    }
    public ArrayList<DisparoEne> getMisDisparos() {
        return misDisparos;
    }
    public void setMisDisparos(ArrayList<DisparoEne> misDisparos) {
        this.misDisparos = misDisparos;
    }

    //Métodos
    //Crear los disparos(Create)
    @Override
    public void disparar() {
        if (estaVivo()) {
            if (Math.random() * 100 < getProbabilidadDisparo()) {
                // Cálculo para centrar la bala perfectamente
                float posX = getX() + getMitadWidth() - (getAnchoBala() / 2f);;
                float posY = getY() - getMitadHeight();

                // Creamos el disparo y lo añadimos a la lista
                DisparoEne nuevoDisparo = new DisparoEne(posX, posY, getAnchoBala(), getAltoBala(), Estado.VIVO, Direccion.ABAJO, "sprites/disparoEne.png");
                misDisparos.add(nuevoDisparo);
                sonidoDisparo.play(0.1f);
            }
        }
    }
    //Gestionar la salida de la pantalla de los disparos (Delete)
    @Override
    public void gestionarMisDisparos(float limiteInferior) {
        for (int i = misDisparos.size() - 1 ; i >= 0; i--){//Recorremos el array al reves para no liarla con los indices
            DisparoEne d = misDisparos.get(i);

            //Si el disparo esta vivo es decir no ha colisionado lo movemos
            if (d.getEstado() == Estado.VIVO){
                d.mover(Direccion.ABAJO, getVelocidadBala());
                d.desaparecer(limiteInferior); //Preguntamos si se ha salido de la pantalla y este setea el estado a muerto
            }
            if (d.getEstado() == Estado.MUERTO) misDisparos.remove(i); //Lo eliminamos si la comprobación de desaparecer de la pantalla ya nos da que esta Muerto
        }
    }

    public void comprobarColisionDisparos(NaveAmi naveAmiga){
        for (DisparoEne d: misDisparos){
            d.comprobarColision(naveAmiga);
        }
    }

    public void pintar(SpriteBatch batch, Map<String, Texture> galeriaImagenes){
        batch.draw(galeriaImagenes.get(this.getTextura()),this.getX(),this.getY(), this.getWidth(), this.getHeight());

        for (DisparoEne d: misDisparos){
            if(d.getEstado() == Estado.VIVO) d.pintar(batch, galeriaImagenes);
        }
    }
}
