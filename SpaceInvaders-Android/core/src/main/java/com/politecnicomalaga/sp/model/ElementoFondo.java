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

    public void actualizar(float altoPantalla, float anchoPantalla){
        this.setY(this.getY()-velocidad); //Se mueve hacia abajo
        if(this.getY() < -this.getHeight()){ //Al poner que sea menor que su negativo hace que no se corte la imagen al llegar al fondo suino cuando ya sea haya ido completamente
            //Si sale por abajo reaparece arriba con un X aleatoria
            if (!this.getTextura().equals("estrella.png")){
                int planeta = (int)(Math.random()*10);
                this.setTextura("planet0"+planeta+".png");
            }
            this.setY(altoPantalla+this.getHeight()); //lo devolvemos al inicio
            this.setX((float)Math.random()* anchoPantalla);
        }
    }
    public void pintar(SpriteBatch batch, Map<String, Texture> galeriaImagenes){
        batch.draw(galeriaImagenes.get(this.getTextura()),this.getX(),this.getY(), this.getWidth(), this.getHeight());
    }

}
