package com.politecnicomalaga.sp;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
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

    private float anchoPantalla, altoPantalla;

    private float y, x;
    Map<String, Texture> galeriaImagenes;


    @Override
    public void create() {
        batch = new SpriteBatch();
        galeriaImagenes = new HashMap<>();

        image = new Texture("sprites/enemigo1.png");
        galeriaImagenes.put("sprites/enemigo1.png", image);
        image = new Texture("sprites/enemigo2.png");
        galeriaImagenes.put("sprites/enemigo2.png", image);
        image = new Texture("sprites/naveJugador.png");
        galeriaImagenes.put("sprites/naveJugador.png", image);
        image = new Texture("sprites/disparoAmi.png");
        galeriaImagenes.put("sprites/disparoAmi.png", image);
        image = new Texture("sprites/disparoEne.png");
        galeriaImagenes.put("sprites/disparoEne.png", image);
        image = new Texture("sprites/vida.png");
        galeriaImagenes.put("vida.png", image);


        image = new Texture("planets/estrella.png");
        galeriaImagenes.put("estrella.png", image);
        image = new Texture("planets/planet00.png");
        galeriaImagenes.put("planet00.png", image);
        image = new Texture("planets/planet01.png");
        galeriaImagenes.put("planet01.png", image);
        image = new Texture("planets/planet02.png");
        galeriaImagenes.put("planet02.png", image);
        image = new Texture("planets/planet03.png");
        galeriaImagenes.put("planet03.png", image);
        image = new Texture("planets/planet04.png");
        galeriaImagenes.put("planet04.png", image);
        image = new Texture("planets/planet05.png");
        galeriaImagenes.put("planet05.png", image);
        image = new Texture("planets/planet06.png");
        galeriaImagenes.put("planet06.png", image);
        image = new Texture("planets/planet07.png");
        galeriaImagenes.put("planet07.png", image);
        image = new Texture("planets/planet08.png");
        galeriaImagenes.put("planet08.png", image);
        image = new Texture("planets/planet09.png");
        galeriaImagenes.put("planet09.png", image);
        image = new Texture("planets/planet09.png");
        galeriaImagenes.put("planet09.png", image);

        image = new Texture("numbers/Number0.png");
        galeriaImagenes.put("Number0.png", image);
        image = new Texture("numbers/Number1.png");
        galeriaImagenes.put("Number1.png", image);
        image = new Texture("numbers/Number2.png");
        galeriaImagenes.put("Number2.png", image);
        image = new Texture("numbers/Number3.png");
        galeriaImagenes.put("Number3.png", image);
        image = new Texture("numbers/Number4.png");
        galeriaImagenes.put("Number4.png", image);
        image = new Texture("numbers/Number5.png");
        galeriaImagenes.put("Number5.png", image);
        image = new Texture("numbers/Number6.png");
        galeriaImagenes.put("Number6.png", image);
        image = new Texture("numbers/Number7.png");
        galeriaImagenes.put("Number7.png", image);
        image = new Texture("numbers/Number8.png");
        galeriaImagenes.put("Number8.png", image);
        image = new Texture("numbers/Number9.png");
        galeriaImagenes.put("Number9.png", image);


        image = new Texture("sprites/explosion.png");
        galeriaImagenes.put("explosion.png", image);

        anchoPantalla = Gdx.graphics.getWidth();
        altoPantalla = Gdx.graphics.getHeight();


        image = new Texture("textMenu/Jugar.png");
        galeriaImagenes.put("botonComenzar", image);
        image = new Texture("textMenu/Salir.png");
        galeriaImagenes.put("botonSalir", image);
        image = new Texture("textMenu/Ayuda.png");
        galeriaImagenes.put("botonAjustes", image);
        image = new Texture("textMenu/Ayuda.png");
        galeriaImagenes.put("botonAjustes", image);
        image = new Texture("textMenu/FondoEstrellas.png");
        galeriaImagenes.put("fondoEstrellas", image);
        image = new Texture("textMenu/TituloSpaceSinFondo.png");
        galeriaImagenes.put("titulo", image);

        image = new Texture("settingMenu/infoMenu.png");
        galeriaImagenes.put("infoMenu", image);
        image = new Texture("settingMenu/musicHeader.png");
        galeriaImagenes.put("musicHeader", image);

        image = new Texture("settingMenu/mas.png");
        galeriaImagenes.put("botonMas", image);
        image = new Texture("settingMenu/menos.png");
        galeriaImagenes.put("botonMenos", image);
        image = new Texture("settingMenu/barra.png");
        galeriaImagenes.put("barra", image);
        image = new Texture("settingMenu/barra2.png");
        galeriaImagenes.put("barra2", image);

        image = new Texture("textMenu/Highscore.png");
        galeriaImagenes.put("highscore", image);

        image = new Texture("GameOver/GameOverPrototipo.png");
        galeriaImagenes.put("gameOver", image);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        //Control de entrada
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            Controlador.getInstance(anchoPantalla,altoPantalla).cambiarSentidoNaveAmiga(0);
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            Controlador.getInstance(anchoPantalla,altoPantalla).cambiarSentidoNaveAmiga(anchoPantalla);
        } else if(Gdx.input.isTouched()) {
            x = Gdx.input.getX();
            y = Gdx.input.getY();
            Controlador.getInstance(anchoPantalla, altoPantalla).click(x, y);
        }


        //Control de estado
        Controlador.getInstance(anchoPantalla, altoPantalla).simulaMundo(Gdx.graphics.getDeltaTime());


        //Pintar el mundo
        batch.begin();
        Controlador.getInstance(anchoPantalla, altoPantalla).pintar(batch, galeriaImagenes);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        for (Texture imagen : galeriaImagenes.values()) {
            imagen.dispose();
        }
    }

    public static  void salir(){
        Gdx.app.exit();
    }
}

