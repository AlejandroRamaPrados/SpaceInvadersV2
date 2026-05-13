package com.politecnicomalaga.sp;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.politecnicomalaga.sp.control.Controlador;

import java.util.HashMap;
import java.util.Map;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;

    private float anchoPantalla,altoPantalla;

    private float y,x;
    Map<String,Texture> galeriaImagenes;


    @Override
    public void create() {
        batch = new SpriteBatch();
        galeriaImagenes = new HashMap<>();

        image = new Texture("sprites/enemigo1.png");
        galeriaImagenes.put("sprites/enemigo1.png",image);
        image = new Texture("sprites/enemigo2.png");
        galeriaImagenes.put("sprites/enemigo2.png",image);
        image = new Texture("sprites/naveJugador.png");
        galeriaImagenes.put("sprites/naveJugador.png",image);
        image = new Texture("sprites/disparoAmi.png");
        galeriaImagenes.put("sprites/disparoAmi.png", image);
        image = new Texture("sprites/disparoEne.png");
        galeriaImagenes.put("sprites/disparoEne.png", image);

        anchoPantalla = Gdx.graphics.getWidth();
        altoPantalla = Gdx.graphics.getHeight();

    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        //Control de entrada
        if(Gdx.input.justTouched()){
            x= Gdx.input.getX();
            y=Gdx.input.getY();
            Controlador.getInstance().click(x,y);
        }

        //Control de estado
        Controlador.getInstance().simulaMundo(anchoPantalla,altoPantalla);


        //Pintar el mundo
        batch.begin();
        Controlador.getInstance().pintar(batch, galeriaImagenes);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        for (Texture imagen : galeriaImagenes.values()) {
            imagen.dispose();
        }
    }
}
