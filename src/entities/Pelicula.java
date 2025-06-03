package entities;
import tads.LinkedList.MyLinkedListImpl;

public class Pelicula {
    private int idPelicula;
    private String titulo;
    private String idiomaOriginal;
    private double revenue;
    private MyLinkedListImpl<Genero> listaGeneros;

    public Pelicula() {}

    public Pelicula(int idPelicula, String titulo, String idiomaOriginal, double revenue,  MyLinkedListImpl<Genero> listaGeneros) {
        this.idPelicula = idPelicula;
        this.titulo = titulo;
        this.idiomaOriginal = idiomaOriginal;
        this.revenue = revenue;
        this.listaGeneros = listaGeneros;
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
