package com.politecnicomalaga.sp.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public abstract class Nave extends Ovni{

    //Atributos
    private int vidas;
    private float cadencia;
    private float anchoBala;
    private float altoBala;
    private float velocidadBala;
    protected Sound sonidoDisparo;
    protected Sound sonidoMuerte;
    //Constructor
    public Nave(float x, float y, float width, float height, Estado estado, Direccion dir, String textura, int vidas, float cadencia, float anchoBala, float altoBala, float velocidadBala) {
        super(x, y, width, height, estado, dir, textura);
        this.vidas = vidas;
        this.cadencia = cadencia;
        this.anchoBala = anchoBala;
        this.altoBala = altoBala;
        this.velocidadBala = velocidadBala;
        sonidoDisparo = Gdx.audio.newSound(
            Gdx.files.internal("sounds/shoot.wav")
        );
        sonidoMuerte = Gdx.audio.newSound(
            Gdx.files.internal("sounds/enemydeath.wav")
        );
    }
    //Getters y Setters
    public int getVidas() {
        return vidas;
    }
    public void setVidas(int vidas) {
        this.vidas = vidas;
    }
    public float getCadencia() {
        return cadencia;
    }
    public void setCadencia(float cadencia) {
        this.cadencia = cadencia;
    }
    public float getAnchoBala() {
        return anchoBala;
    }
    public void setAnchoBala(float anchoBala) {
        this.anchoBala = anchoBala;
    }
    public float getAltoBala() {
        return altoBala;
    }
    public void setAltoBala(float altoBala) {
        this.altoBala = altoBala;
    }
    public float getVelocidadBala() {
        return velocidadBala;
    }
    public void setVelocidadBala(float velocidadBala) {
        this.velocidadBala = velocidadBala;
    }

    //Métodos
    //Si recibimos un disparo perdemos una vida, si llega a 0 morimos seteamos a muerto.
    public boolean recibirDisparo() {
        if (estaVivo()) {
            this.vidas--;
            if (this.vidas <= 0) {
                this.vidas = 0; // // Solo procesamos el daño si no está muerto, Evitamos vidas negativas que puedan romper algo en el controlador
                this.setEstado(Estado.MUERTO);
                sonidoMuerte.play();
                return true;
            }
        }
        return false;
    }

    //Ambas naves disparan y gestionan sus disparos pero lo hacen de manera diferente, metodos abstracto
    public abstract void disparar();
    public abstract void gestionarMisDisparos(float limiteMuerte);
}
