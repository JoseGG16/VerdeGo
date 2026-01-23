package modelo.bean;

import java.util.Date;

public class Cliente extends Usuario {
    
    private int puntosFidelizacion;
    private String paypalEmail;

    
    public Cliente() {
        super();
    }

    public Cliente(int idUsuario, Rol rol, String nombre, String apellidos, String email, String password, String telefono, boolean estado, Date fechaRegistro, int puntosFidelizacion, String paypalEmail) {
        
        super(idUsuario, rol, nombre, apellidos, email, password, telefono, estado, fechaRegistro);
        
        this.puntosFidelizacion = puntosFidelizacion;
        this.paypalEmail= paypalEmail;
    }

    public String getPaypalEmail() { return paypalEmail; }
    public void setPaypalEmail(String paypalEmail) { this.paypalEmail = paypalEmail; }
    public int getPuntosFidelizacion() { return puntosFidelizacion; }
    public void setPuntosFidelizacion(int puntosFidelizacion) { this.puntosFidelizacion = puntosFidelizacion; }
}