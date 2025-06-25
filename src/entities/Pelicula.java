package entities;
import tads.LinkedList.MyLinkedListImpl;

public class Pelicula {
    private int idPelicula;
    private String titulo;
    private String idiomaOriginal;
    private double revenue;
    private MyLinkedListImpl<Genero> listaGeneros;
    private Integer idColeccion;
    private MyLinkedListImpl<Calificacion> listaRatings;
    private MyLinkedListImpl<Integer> listaActores;

    public Pelicula() {}


    public Pelicula(int idPelicula, String titulo, String idiomaOriginal, double revenue,  MyLinkedListImpl<Genero> listaGeneros, Integer idColeccion) {
        this.idPelicula = idPelicula;
        this.titulo = titulo;
        this.idiomaOriginal = idiomaOriginal;
        this.revenue = revenue;
        this.listaGeneros = listaGeneros;
        this.idColeccion = idColeccion;
        this.listaRatings = new MyLinkedListImpl<>();
        this.listaActores = new MyLinkedListImpl<>();

    }
    public void agregarActor(int idActor) {
        listaActores.add(idActor);
    }

    public MyLinkedListImpl<Integer> getListaActors() {
        return listaActores;
    }

    public void setListaActors(MyLinkedListImpl<Integer> listaActors) {
        this.listaActores = listaActors;
    }

    public void agregarRating(Calificacion c) {
        listaRatings.add(c);
    }

    public MyLinkedListImpl<Calificacion> getListaRatings() {
        return listaRatings;
    }

    public void setListaRatings(MyLinkedListImpl<Calificacion> listaRatings) {
        this.listaRatings = listaRatings;
    }

    public Integer getIdColeccion() {
        return idColeccion;
    }

    public void setIdColeccion(Integer idColeccion) {
        this.idColeccion = idColeccion;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public int getIdPelicula() {
        return idPelicula;
    }

    public void setIdPelicula(int idPelicula) {
        this.idPelicula = idPelicula;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIdiomaOriginal() {
        return idiomaOriginal;
    }

    public void setIdiomaOriginal(String idiomaOriginal) {
        this.idiomaOriginal = idiomaOriginal;
    }

    public MyLinkedListImpl<Genero> getListaGeneros() {
        return listaGeneros;
    }

    public void setListaGeneros(MyLinkedListImpl<Genero> listaGeneros) {
        this.listaGeneros = listaGeneros;
    }
}
