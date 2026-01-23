package modelo.bean;

import java.util.Date;

public class Empleado extends Usuario {
    
    private Date fechaAlta;
    private String observaciones;

    public Empleado() {
        super();
    }

    public Empleado(int idUsuario, Rol rol, String nombre, String apellidos, String email, String password, String telefono, boolean estado, Date fechaRegistro, Date fechaAlta, String observaciones) {
        
        super(idUsuario, rol, nombre, apellidos, email, password, telefono, estado, fechaRegistro);
        
        this.fechaAlta = fechaAlta;
        this.observaciones = observaciones;
    }

    public Date getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(Date fechaAlta) { this.fechaAlta = fechaAlta; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}