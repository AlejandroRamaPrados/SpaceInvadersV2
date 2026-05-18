package com.politecnicomalaga.sp.model;

import com.badlogic.gdx.Gdx;

public class Ovni {
    //Atributos
    private float x,y;
    private float width,height;
    private float mitadWidth,mitadHeight;
    public enum Estado {VIVO, MUERTO};
    private Estado estado;
    public enum Direccion {ARRIBA, ABAJO, DERECHA, IZQUIERDA, NOMOVER}
    private Direccion dir;
    private String textura;

    //Constructor
    public Ovni(float x, float y, float width, float height, Estado estado, Direccion dir, String textura) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.estado = estado;
        this.textura = textura;
        mitadHeight=this.height/2;
        mitadWidth=this.width/2;
        this.dir=dir;
    }

    //Getters y Setters
    public float getX() {
        return x;
    }
    public void setX(float x) {
        this.x = x;
    }
    public float getY() {
        return y;
    }
    public void setY(float y) {
        this.y = y;
    }
    public float getWidth() {
        return width;
    }
    public void setWidth(float width) {
        this.width = width;
    }
    public float getHeight() {
        return height;
    }
    public void setHeight(float height) {
        this.height = height;
    }
    public float getMitadWidth() {
        return mitadWidth;
    }
    public void setMitadWidth(float mitadWidth) {
        this.mitadWidth = mitadWidth;
    }
    public float getMitadHeight() {
        return mitadHeight;
    }
    public void setMitadHeight(float mitadHeight) {
        this.mitadHeight = mitadHeight;
    }
    public Estado getEstado() {
        return estado;
    }
    public void setEstado(Estado estado) {
        this.estado = estado;
    }
    public Direccion getDir() {
        return dir;
    }
    public void setDir(Direccion dir) {
        this.dir = dir;
    }
    public String getTextura() {
        return textura;
    }
    public void setTextura(String textura) {
        this.textura = textura;
    }
    public float getXEsquina (){ return x-mitadWidth; }
    public float  getYEsquina(){ return y-mitadHeight; }

    //Métodos
    //Comprobar la colisión con otra nave
    public boolean colision (Ovni otraNave){
        return this.x < otraNave.getX() + otraNave.getWidth() &&
                this.x + this.width > otraNave.getX() &&
                this.y < otraNave.getY() + otraNave.getHeight() &&
                this.y + this.height > otraNave.getY();
    }

    public void mover(Direccion direccion, float velocidad){
        float espacioRecorrido = velocidad * Gdx.graphics.getDeltaTime();
        switch (direccion){
            case ABAJO:
                this.setY(this.y-espacioRecorrido);
                break;

            case ARRIBA:
                this.setY(this.y+espacioRecorrido);
                break;

            case DERECHA:
                this.setX(this.x+espacioRecorrido);
                break;

            case IZQUIERDA:
                this.setX(this.x-espacioRecorrido);
                break;
        }
    }
    //Para verificar si esta vivo mas comodo
    public boolean estaVivo() {
        return this.estado == Estado.VIVO;
    }
}
