package modelo.bean;

import java.util.Date;

public abstract class Usuario {
    
    private int idUsuario;
    private Rol rol;
    private String nombre;
    private String apellidos;
    private String email;
    private String password;
    private String telefono;
    private boolean estado;
    private Date fechaRegistro;

    public Usuario() {
    }

    public Usuario(int idUsuario, Rol rol, String nombre, String apellidos, String email, String password, String telefono, boolean estado, Date fechaRegistro) {
        this.idUsuario = idUsuario;
        this.rol = rol;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    public Date getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}