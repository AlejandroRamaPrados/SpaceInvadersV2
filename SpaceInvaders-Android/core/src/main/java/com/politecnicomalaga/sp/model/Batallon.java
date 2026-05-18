package com.politecnicomalaga.sp.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Map;

public class Batallon {
    //Atributos
    //Composición de 4 escuadrones
    private Escuadron[] escuadrones;
    private Ovni.Direccion direccionActual;
    private float velocidad;

    //Constructor
    //Con todos los parámetros necesarios para inicializar
    public Batallon(float xInicial, float yInicial, float espacioVertical,
                    float width, float height,
                    Ovni.Estado estado,
                    Ovni.Direccion direccionActual,
                    String texturaNormal,
                    String texturaTanque,
                    int vidasNormal,
                    int vidasTanque,
                    float cadencia,
                    float anchoBala,
                    float altoBala,
                    float velocidadBala,
                    int probabilidadDisparoNormal,
                    int probabilidadDisparoTanque,
                    float espacioEntreNaves,
                    float velocidad) {
        this.velocidad = velocidad;
        this.direccionActual = direccionActual;
        this.escuadrones = new Escuadron[4]; //Inicializamos el número de escuadrones 4.

        //Cargamos los escuadrones con nuestro método load
        loadEscuadrones(xInicial, yInicial, espacioVertical, width, height,
            estado, direccionActual, texturaNormal,texturaTanque, vidasNormal,vidasTanque, cadencia, anchoBala, altoBala, velocidadBala,
            probabilidadDisparoNormal,probabilidadDisparoTanque, espacioEntreNaves);
    }

    // Método load, en filas
    private void loadEscuadrones(float x, float y, float espacioVertical,
                                 float width, float height,
                                 Ovni.Estado estado,
                                 Ovni.Direccion dir,
                                 String texturaNormal,
                                 String texturaTanque,
                                 int vidasNormal,
                                 int vidasTanque,
                                 float cadencia,
                                 float anchoBala,
                                 float altoBala,
                                 float velocidadBala,
                                 int probabilidadDisparoNormal,
                                 int probabilidadDisparoTanque,
                                 float espacioEntreNaves) {

        for (int i = 0; i < this.escuadrones.length; i++) {

            float yEscuadron = y - (i * (height + espacioVertical));

            boolean esTanque = (i >= 2); // últimos 2 son tanques

            String textura = esTanque ? texturaTanque : texturaNormal;
            int vidas = esTanque ? vidasTanque : vidasNormal;
            int probDisparo = esTanque ? probabilidadDisparoTanque : probabilidadDisparoNormal;

            this.escuadrones[i] = new Escuadron(
                x,
                yEscuadron,
                width,
                height,
                estado,
                dir,
                textura,
                vidas,
                cadencia,
                anchoBala,
                altoBala,
                velocidadBala,
                probDisparo,
                espacioEntreNaves
            );
        }
    }

    //Getters y Setters
    public Escuadron[] getEscuadrones() {
        return escuadrones;
    }
    public void setEscuadrones(Escuadron[] escuadrones) {
        this.escuadrones = escuadrones;
    }
    public Ovni.Direccion getDireccionActual() {
        return direccionActual;
    }
    public void setDireccionActual(Ovni.Direccion direccionActual) {
        this.direccionActual = direccionActual;
    }
    public float getVelocidad() {
        return velocidad;
    }
    public void setVelocidad(float velocidad) {
        this.velocidad = velocidad;
    }
    //Métodos
    //Mover los escuadrones
    public void mover(float anchoPantalla, float altoPantalla, float cuantoBaja){ //Nos deberán pasar el ancho de la pantalla el alto de la pantalla y cuanto queremos que baje cada vez que llega al borde
        if (escuadrones == null || escuadrones.length == 0) return; //Si por lo que sea no se ha inicializado todavía no hacemos nada

        boolean tocarBorde = false;

        for (Escuadron esc : escuadrones) { //Recorremos los escuadrones preguntando si han tocado el borde
            if (esc.haTocadoBorde(anchoPantalla, direccionActual)){
                tocarBorde = true;
                break;
            }
        }
        if (tocarBorde){ //En ese caso cambiamos la dirección y bajamos el batallón, utilizando el método cambiarDireccionYBajarse
            cambiarDireccionYBajarse(cuantoBaja);
        }
        else {
            for (Escuadron esc : escuadrones) { //Caso contrario, movemos los escuadrones lateralmente usando el método implementado en escuadron
                esc.moverLateralmente(direccionActual, velocidad);
            }
        }
    }
    //Invertir la dirección y bajar el batallón
    private void cambiarDireccionYBajarse(float cuantoBaja) {
        //Invertimos la dirección
        direccionActual = (direccionActual == Ovni.Direccion.DERECHA) ? Ovni.Direccion.IZQUIERDA : Ovni.Direccion.DERECHA;
        //Bajar el batallón completo usando el método de la clase escuadron
        for (Escuadron esc : escuadrones) {
            esc.bajar(cuantoBaja);
        }
        //Los movemos un pixel para que no este a true tocar borde por si acaso
        for (Escuadron esc : escuadrones) {
            esc.moverLateralmente(direccionActual, velocidad);
        }
    }

    //El batallón dice disparar y los escuadrones ya se encargan de gestionar lo suyo.
    public void disparar() {
        for (Escuadron esc : escuadrones) {
            esc.disparar();
        }
    }

    //Gestionamos los disparos de los enemigos, batallón se lo pasa a escuadron y escuadron a nave Enemiga que se encarga del CRUD
    public void gestionarDisparos(float limiteMuerte) {
        for (Escuadron esc : escuadrones) {
            esc.gestionarDisparosEnemigos(limiteMuerte);
        }
    }

    //Comprobamos si quedan tropas, si no quedan terminamos el juego más fácil para el controlador.
    public boolean tieneTropas() {
        if (escuadrones == null || escuadrones.length == 0) return false; //Comprobamos si por lo que sea está vacío en ese caso obviamente no hay tropas

        //El problema es que cuando mueren las naves siguen existiendo péro su estado está en Muerto
        //Recorremos los escuadrones y preguntamos si tienen naves vivas
        for (Escuadron esc : escuadrones) {
            if (esc.tieneNavesVivas()) {
                return true; // En cuanto uno tenga una nave viva, el batallón sigue activo
            }
        }
        return false; // Si revisa todos y nadie tiene naves vivas, se acabó el juego, hemos ganado
    }

    public void comprobarColisionesDisparo(NaveAmi naveAmiga){
        for (Escuadron esc : escuadrones){
            esc.comprobarColisionesDisparo(naveAmiga);
        }
    }
    public boolean comprobarSiMeHanDado(DisparoAmi disparoAmi){
        for (Escuadron esc : escuadrones){
            if (esc.comprobarSiMeHanDado(disparoAmi)) return true;
        }
        return false;
    }

    public void comprobarColisionesFisicas(NaveAmi naveAmiga){
        for (Escuadron esc : escuadrones){
            esc.comprobarColisionesFisicas(naveAmiga);
        }
    }
    public boolean hayNavesVivas(){
        return tieneTropas();
    }

    public void pintar(SpriteBatch batch, Map<String, Texture> galeriaImagenes){
        for (Escuadron esc : escuadrones){
            esc.pintar(batch, galeriaImagenes);
        }
    }
}
