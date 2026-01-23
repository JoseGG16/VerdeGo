package modelo.bean;

import java.util.Date;

public class Pedido {

    public enum EstadoPedido {
        PENDIENTE,
        ENVIADO,
        ENTREGADO,
        CANCELADO
    }

    private int idPedido;
    private Cliente cliente;
    private CodigoDescuento codigoDescuento; 
    private Date fechaPedido;
    private EstadoPedido estado;
    private double total;
    private int puntosUsados;

    private String dirDireccion;
    private String dirCiudad;
    private String dirCp;
    private String dirPais;

    public Pedido() {
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
    }

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

    @Override
    public String toString() {
        return "Pedido [idPedido=" + idPedido + ", cliente=" + (cliente != null ? cliente.getNombre() : "null") 
                + ", estado=" + estado + ", total=" + total + "]";
    }
}