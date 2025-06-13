package entities;

import tads.LinkedList.MyLinkedListImpl;

public class Saga {
    private Integer id;
    private String nombre;
    private MyLinkedListImpl<Integer> peliculas;

    public Saga() {
        this.id = null;
        this.nombre = null;
        this.peliculas = new MyLinkedListImpl<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public MyLinkedListImpl<Integer> getPeliculas() {
        return peliculas;
    }

    public void setPeliculas(MyLinkedListImpl<Integer> peliculas) {
        this.peliculas = peliculas;
    }

    public void agregarPelicula(Integer id) {
        this.peliculas.add(id);
    }


}
