package com.politecnicomalaga.sp.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.politecnicomalaga.sp.control.Controlador;

import java.util.Map;

import javax.naming.ldap.Control;

public class ElementoFondo extends Ovni{
    private float velocidad;
    public ElementoFondo(float x, float y, float width, float height, String textura, float velocidad) {
        super(x, y, width, height, Estado.VIVO, Direccion.ABAJO, textura);
        this.velocidad = velocidad;
    }

    public void actualizar(float delta, float altoPantalla, float anchoPantalla){
        this.setY(this.getY() - (velocidad * delta)); //Se mueve hacia abajo usando delta para ser responsive
        if(this.getY() < -this.getHeight()){
            if (!this.getTextura().equals("estrella.png")){
                int planeta = (int)(Math.random()*10);
                this.setTextura("planet0"+planeta+".png");
            }
            this.setY(altoPantalla+this.getHeight());
            this.setX((float)Math.random()* anchoPantalla);
        }
    }
    public void pintar(SpriteBatch batch, Map<String, Texture> galeriaImagenes){
        batch.draw(galeriaImagenes.get(this.getTextura()),this.getX(),this.getY(), this.getWidth(), this.getHeight());
    }

}
