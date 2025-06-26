package entities;
import tads.LinkedList.MyLinkedListImpl;

public class Persona {
    private int id;
    private String nombre;
    private MyLinkedListImpl<Integer> listaPeliculas;

    public Persona(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        listaPeliculas = new MyLinkedListImpl<>();
    }

    public void agregarPelicula (Integer id) {
        this.listaPeliculas.add(id);
    }

    public MyLinkedListImpl<Integer> getListaPeliculas() {
        return listaPeliculas;
    }

    public void setListaPeliculas(MyLinkedListImpl<Integer> listaPeliculas) {
        this.listaPeliculas = listaPeliculas;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
