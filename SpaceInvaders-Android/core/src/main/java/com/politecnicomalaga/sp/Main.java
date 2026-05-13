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

        image = new Texture("enemigo1.png");
        galeriaImagenes.put("enemigo1.png",image);
        image = new Texture("enemigo2.png");
        galeriaImagenes.put("enemigo2.png",image);
        image = new Texture("naveJugador.png");
        galeriaImagenes.put("naveJugador.png",image);
        image = new Texture("disparoAmi.png");
        galeriaImagenes.put("disparoAmi.png", image);
        image = new Texture("disparoEne.png");
        galeriaImagenes.put("disparoEne.png", image);
        image= new Texture("planets/estrella.png");
        galeriaImagenes.put("estrella.png", image);
        image= new Texture("planets/planet00.png");
        galeriaImagenes.put("planet00.png", image);
        image= new Texture("planets/planet01.png");
        galeriaImagenes.put("planet01.png", image);
        image= new Texture("planets/planet02.png");
        galeriaImagenes.put("planet02.png", image);
        image= new Texture("planets/planet03.png");
        galeriaImagenes.put("planet03.png", image);
        image= new Texture("planets/planet04.png");
        galeriaImagenes.put("planet04.png", image);
        image= new Texture("planets/planet05.png");
        galeriaImagenes.put("planet05.png", image);
        image= new Texture("planets/planet06.png");
        galeriaImagenes.put("planet06.png", image);
        image= new Texture("planets/planet07.png");
        galeriaImagenes.put("planet07.png", image);
        image= new Texture("planets/planet08.png");
        galeriaImagenes.put("planet08.png", image);
        image= new Texture("planets/planet09.png");
        galeriaImagenes.put("planet09.png", image);
        image= new Texture("planets/planet09.png");
        galeriaImagenes.put("planet09.png", image);


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
