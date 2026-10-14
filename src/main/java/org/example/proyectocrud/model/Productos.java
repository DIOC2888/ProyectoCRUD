package org.example.proyectocrud.model;

public class Productos {
    private String id_producto;
    private String codigo;
    private String nombre;
    private String categoria;
    private String marca;
    private String descripcion;
    private double precio;
    private int stock;
    private String fechaCreacion;
    private String id_categoria;
    private String id_marca;
    private boolean estado; //activo o inactivo


    public Productos(String id_producto, String codigo, String nombre, String categoria, String marca, String descripcion, double precio, int stock, String fechaCreacion, String id_categoria, String id_marca, boolean estado) {
        this.id_producto = id_producto;
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.marca = marca;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.fechaCreacion = fechaCreacion;
        this.id_categoria = id_categoria;
        this.id_marca = id_marca;
        this.estado = estado;
    }

    public String getId_producto() {
        return id_producto;
    }

    public void setId_producto(String id_producto) {
        this.id_producto = id_producto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(String id_categoria) {
        this.id_categoria = id_categoria;
    }

    public String getId_marca() {
        return id_marca;
    }

    public void setId_marca(String id_marca) {
        this.id_marca = id_marca;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
