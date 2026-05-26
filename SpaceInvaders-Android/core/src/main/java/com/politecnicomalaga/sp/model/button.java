package com.politecnicomalaga.sp.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Map;

public class button  extends Ovni{
    public button(float x, float y, float width, float height, Estado estado, Direccion dir, String textura) {
        super(x, y, width, height, estado, dir, textura);
    }
    public void pintar(SpriteBatch batch, Map<String, Texture> galeriaImagenes){
        batch.draw(galeriaImagenes.get(getTextura()), getX(), getY(), getWidth(), getHeight());
    }

    public boolean click(float x, float y){
        return x >= getX() && x <= (getX() + getWidth()) && y >= getY() && y <= (getY() + getHeight());
    }

}
