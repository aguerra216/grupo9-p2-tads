package entities;

import tads.LinkedList.MyLinkedListImpl;

public class Usuario {
    MyLinkedListImpl<Calificacion> calificaciones;
    private Integer id;

    public Usuario(int id) {
        this.id = id;
    }

    public MyLinkedListImpl<Calificacion> getCalificaciones() {
        return calificaciones;
    }

    public void setCalificaciones(MyLinkedListImpl<Calificacion> calificaciones) {
        this.calificaciones = calificaciones;
    }

    public void agregarCalificacion(Calificacion cal) {

        calificaciones.add(cal);
    }





}
