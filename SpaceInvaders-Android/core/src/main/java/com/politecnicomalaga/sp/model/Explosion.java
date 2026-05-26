package com.politecnicomalaga.sp.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Map;

public class Explosion {
    private float x, y, tam;
    private float tiempoVida; // En segundos

    public Explosion(float x, float y, float tam) {
        this.x = x;
        this.y = y;
        this.tam = tam;
        this.tiempoVida = 0.5f; // Duración de la explosión (medio segundo)
    }

    public boolean actualizar(float delta) {
        tiempoVida -= delta;
        return tiempoVida <= 0; // Devuelve true si debe desaparecer
    }

    public void pintar(SpriteBatch batch, Map<String, Texture> galeria) {
        batch.draw(galeria.get("explosion.png"), x, y, tam, tam);
    }
}
