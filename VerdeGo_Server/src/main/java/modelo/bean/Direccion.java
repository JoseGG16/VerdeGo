package modelo.bean;

public class Direccion {
    
    private int idDireccion;
    private Usuario usuario; // Cambio realizado: Ahora es un objeto Usuario completo
    private String alias;
    private String calle;
    private String ciudad;
    private String codigoPostal;
    private String pais;
    private boolean esPrincipal;

    // Constructor vacío
    public Direccion() {}

    // Constructor completo actualizado con (Usuario usuario)
    public Direccion(int idDireccion, Usuario usuario, String alias, String calle, String ciudad, String codigoPostal, String pais, boolean esPrincipal) {
        this.idDireccion = idDireccion;
        this.usuario = usuario;
        this.alias = alias;
        this.calle = calle;
        this.ciudad = ciudad;
        this.codigoPostal = codigoPostal;
        this.pais = pais;
        this.esPrincipal = esPrincipal;
    }

    // Getters y Setters

    public int getIdDireccion() { 
        return idDireccion; 
    }
    
    public void setIdDireccion(int idDireccion) { 
        this.idDireccion = idDireccion; 
    }

    public Usuario getUsuario() { 
        return usuario; 
    }
    
    public void setUsuario(Usuario usuario) { 
        this.usuario = usuario; 
    }

    public String getAlias() { 
        return alias; 
    }
    
    public void setAlias(String alias) { 
        this.alias = alias; 
    }

    public String getCalle() { 
        return calle; 
    }
    
    public void setCalle(String calle) { 
        this.calle = calle; 
    }

    public String getCiudad() { 
        return ciudad; 
    }
    
    public void setCiudad(String ciudad) { 
        this.ciudad = ciudad; 
    }

    public String getCodigoPostal() { 
        return codigoPostal; 
    }
    
    public void setCodigoPostal(String codigoPostal) { 
        this.codigoPostal = codigoPostal; 
    }

    public String getPais() { 
        return pais; 
    }
    
    public void setPais(String pais) { 
        this.pais = pais; 
    }

    public boolean isEsPrincipal() { 
        return esPrincipal; 
    }
    
    public void setEsPrincipal(boolean esPrincipal) { 
        this.esPrincipal = esPrincipal; 
    }

    @Override
    public String toString() {
        return "Direccion [idDireccion=" + idDireccion + ", usuario=" + (usuario != null ? usuario.getIdUsuario() : "null") + ", alias=" + alias
                + ", calle=" + calle + ", ciudad=" + ciudad + ", codigoPostal=" + codigoPostal + ", pais=" + pais
                + ", esPrincipal=" + esPrincipal + "]";
    }
}