package modelo.bean;

import java.util.Date;

public class CodigoDescuento {

    public enum TipoDescuento {
        PORCENTAJE, 
        FIJO
    }

    private int idCodigo;
    private String codigo;
    private TipoDescuento tipo;
    private double valor;
    private Date fechaCaducidad;
    private boolean activo;

    public CodigoDescuento() {
    }

    public CodigoDescuento(int idCodigo, String codigo, TipoDescuento tipo, double valor, Date fechaCaducidad, boolean activo) {
        this.idCodigo = idCodigo;
        this.codigo = codigo;
        this.tipo = tipo;
        this.valor = valor;
        this.fechaCaducidad = fechaCaducidad;
        this.activo = activo;
    }


    public int getIdCodigo() {
        return idCodigo;
    }

    public void setIdCodigo(int idCodigo) {
        this.idCodigo = idCodigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public TipoDescuento getTipo() {
        return tipo;
    }

    public void setTipo(TipoDescuento tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Date getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(Date fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "CodigoDescuento [idCodigo=" + idCodigo + ", codigo=" + codigo + ", tipo=" + tipo + ", valor=" + valor
                + ", fechaCaducidad=" + fechaCaducidad + ", activo=" + activo + "]";
    }
}