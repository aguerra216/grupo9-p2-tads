package entities;

import tads.LinkedList.MyLinkedListImpl;

public class Usuario extends Persona {
    MyLinkedListImpl<Calificacion> calificaciones;

    public Usuario(int id) {
        super(id);
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
