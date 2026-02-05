package modelo.bean;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Pedido {

    public enum EstadoPedido {
        PENDIENTE,
        ENVIADO,
        ENTREGADO,
        CANCELADO, 
    }

    private int idPedido;
    private Cliente cliente;
    private CodigoDescuento codigoDescuento; 
    private Date fechaPedido;
    private EstadoPedido estado;
    private double total;
    private int puntosUsados;
    private int puntosGanados;
    
    private double descuentoAplicado;
    private String nombreCupon;

    private String dirDireccion;
    private String dirCiudad;
    private String dirCp;
    private String dirPais;
    private List<DetallePedido> detalles;

    public Pedido() {
        this.detalles = new ArrayList<>();
    }

    public Pedido(int idPedido, Cliente cliente, CodigoDescuento codigoDescuento, Date fechaPedido, 
                  EstadoPedido estado, double total, int puntosUsados, 
                  String dirDireccion, String dirCiudad, String dirCp, String dirPais) {
        this.idPedido = idPedido;
        this.cliente = cliente;
        this.codigoDescuento = codigoDescuento;
        this.fechaPedido = fechaPedido;
        this.estado = estado;
        this.total = total;
        this.puntosUsados = puntosUsados;
        this.dirDireccion = dirDireccion;
        this.dirCiudad = dirCiudad;
        this.dirCp = dirCp;
        this.dirPais = dirPais;
        this.detalles = new ArrayList<>(); 
    }

    // --- GETTERS Y SETTERS ---

    public double getDescuentoAplicado() { return descuentoAplicado; }
    public void setDescuentoAplicado(double descuentoAplicado) { this.descuentoAplicado = descuentoAplicado; }

    public String getNombreCupon() { return nombreCupon; }
    public void setNombreCupon(String nombreCupon) { this.nombreCupon = nombreCupon; }

    public int getPuntosGanados() { return puntosGanados; }
    public void setPuntosGanados(int puntosGanados) { this.puntosGanados = puntosGanados; }
    
    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public CodigoDescuento getCodigoDescuento() { return codigoDescuento; }
    public void setCodigoDescuento(CodigoDescuento codigoDescuento) { this.codigoDescuento = codigoDescuento; }

    public Date getFechaPedido() { return fechaPedido; }
    public void setFechaPedido(Date fechaPedido) { this.fechaPedido = fechaPedido; }

    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public int getPuntosUsados() { return puntosUsados; }
    public void setPuntosUsados(int puntosUsados) { this.puntosUsados = puntosUsados; }

    public String getDirDireccion() { return dirDireccion; }
    public void setDirDireccion(String dirDireccion) { this.dirDireccion = dirDireccion; }

    public String getDirCiudad() { return dirCiudad; }
    public void setDirCiudad(String dirCiudad) { this.dirCiudad = dirCiudad; }

    public String getDirCp() { return dirCp; }
    public void setDirCp(String dirCp) { this.dirCp = dirCp; }

    public String getDirPais() { return dirPais; }
    public void setDirPais(String dirPais) { this.dirPais = dirPais; }

    public List<DetallePedido> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedido> detalles) { this.detalles = detalles; }

    @Override
    public String toString() {
        return "Pedido [idPedido=" + idPedido + ", cliente=" + (cliente != null ? cliente.getNombre() : "null") 
                + ", estado=" + estado + ", total=" + total + ", numProductos=" + (detalles != null ? detalles.size() : 0) + "]";
    }
}
