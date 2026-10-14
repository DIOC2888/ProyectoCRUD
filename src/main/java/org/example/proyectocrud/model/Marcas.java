package org.example.proyectocrud.model;

public class Marcas {
    private String id_marca;
    private String nombre;
    private String paisOrigen;
    private boolean estado; //activo o inactivo

    public Marcas(String id_marca, String nombre, String paisOrigen, boolean estado) {
        this.id_marca = id_marca;
        this.nombre = nombre;
        this.paisOrigen = paisOrigen;
        this.estado = estado;
    }

    public String getId_marca() {
        return id_marca;
    }

    public void setId_marca(String id_marca) {
        this.id_marca = id_marca;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPaisOrigen() {
        return paisOrigen;
    }

    public void setPaisOrigen(String paisOrigen) {
        this.paisOrigen = paisOrigen;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
