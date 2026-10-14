package org.example.proyectocrud.model;

public class Categorias {
    private String id_categoria;
    private String nombre;
    private String descripcion;
    private boolean estado; //activo o inactivo

    public Categorias(boolean estado, String descripcion, String nombre, String id_categoria) {
        this.estado = estado;
        this.descripcion = descripcion;
        this.nombre = nombre;
        this.id_categoria = id_categoria;
    }


    public String getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(String id_categoria) {
        this.id_categoria = id_categoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
