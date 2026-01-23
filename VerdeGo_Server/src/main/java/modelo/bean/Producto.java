package modelo.bean;

import java.math.BigDecimal;

public class Producto {

    private int idProducto;
    private Categoria categoria; 
    private String nombre;
    private String descripcion;
    private String imagenUrl;
    private BigDecimal precio;
    private BigDecimal stock;
    private String tipoVenta;   
    private String unidadMedida; 
    private boolean activo;

    public Producto() {}

    public Producto(int idProducto, Categoria categoria, String nombre, String descripcion, String imagenUrl, BigDecimal precio, BigDecimal stock, String tipoVenta, String unidadMedida, boolean activo) {
        this.idProducto = idProducto;
        this.categoria = categoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
        this.precio = precio;
        this.stock = stock;
        this.tipoVenta = tipoVenta;
        this.unidadMedida = unidadMedida;
        this.activo = activo;
    }


    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    // Getter y Setter del Objeto COMPUESTO
    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
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

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public BigDecimal getStock() {
        return stock;
    }

    public void setStock(BigDecimal stock) {
        this.stock = stock;
    }

    public String getTipoVenta() {
        return tipoVenta;
    }

    public void setTipoVenta(String tipoVenta) {
        this.tipoVenta = tipoVenta;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    @Override
    public String toString() {
        return "Producto [id=" + idProducto + ", nombre=" + nombre + ", categoria=" + (categoria != null ? categoria.getNombre() : "null") + "]";
    }
}