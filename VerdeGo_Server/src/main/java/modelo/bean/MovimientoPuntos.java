package modelo.bean;

import java.util.Date;

public class MovimientoPuntos {

    public enum TipoMovimiento {
        OBTENIDO,
        GASTADO,
        CADUCADO
    }

    private int idMovimiento;
    private Cliente cliente;
    private Pedido pedido;
    private int puntos;
    private TipoMovimiento tipo;
    private Date fecha;
    private String motivo;

    public MovimientoPuntos() {
    }

    public MovimientoPuntos(int idMovimiento, Cliente cliente, Pedido pedido, int puntos, TipoMovimiento tipo, Date fecha, String motivo) {
        this.idMovimiento = idMovimiento;
        this.cliente = cliente;
        this.pedido = pedido;
        this.puntos = puntos;
        this.tipo = tipo;
        this.fecha = fecha;
        this.motivo = motivo;
    }

    public int getIdMovimiento() { return idMovimiento; }
    public void setIdMovimiento(int idMovimiento) { this.idMovimiento = idMovimiento; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { this.puntos = puntos; }

    public TipoMovimiento getTipo() { return tipo; }
    public void setTipo(TipoMovimiento tipo) { this.tipo = tipo; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}