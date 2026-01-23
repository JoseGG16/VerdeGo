package api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.google.gson.Gson;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;

import modelo.bean.Cliente; // Importante: Necesitamos Cliente
import modelo.bean.Usuario;
import modelo.dao.UsuarioDao;

@Path("/usuarios") 
public class UsuarioApi {
	@Context
    private HttpServletRequest request;
	@POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String login(String jsonEntrada) {
        
        Gson gson = new Gson();
        UsuarioDao dao = new UsuarioDao();
        
        Cliente credenciales = gson.fromJson(jsonEntrada, Cliente.class);
        
        Usuario logueado = dao.validarLogin(credenciales.getEmail(), credenciales.getPassword());
        
        if (logueado != null) {
            
            HttpSession session = request.getSession(true);
            session.setAttribute("usuarioEnSesion", logueado);
            session.setMaxInactiveInterval(1800);
            
            
            logueado.setPassword(null); 

            return gson.toJson(logueado);
        } else {
            return "{\"error\":\"Credenciales incorrectas\"}";
        }
    }
	
	@POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public String logout() {
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            session.invalidate(); 
        }
        
        return "{\"status\":\"ok\"}";
    }
    @POST
    @Path("/registro")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String registro(String jsonEntrada) {
        
        Gson gson = new Gson();
        UsuarioDao dao = new UsuarioDao();
        
        Cliente nuevoUsuario = gson.fromJson(jsonEntrada, Cliente.class);
        
        boolean exito = dao.registrarUsuario(nuevoUsuario);
        
        if (exito) {
            return "{\"status\":\"ok\", \"mensaje\":\"Usuario registrado correctamente\"}";
        } else {
            return "{\"error\":\"No se pudo registrar. Puede que el email ya exista.\"}";
        }
    }
    
    @javax.ws.rs.PUT
    @javax.ws.rs.Path("/modificar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String modificar(String jsonEntrada) {
        
        Gson gson = new Gson();
        UsuarioDao dao = new UsuarioDao();
        
        Cliente usuarioModificado = gson.fromJson(jsonEntrada, Cliente.class);
        
        boolean exito = dao.modificarUsuario(usuarioModificado);
        
        if (exito) {
            return gson.toJson(usuarioModificado);
        } else {
            return "{\"error\":\"No se pudieron guardar los cambios\"}";
        }
    }
    
    @POST
    @Path("/vincular-paypal")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String vincularPaypal(String jsonEntrada) {
        Gson gson = new Gson();
        UsuarioDao dao = new UsuarioDao();
        
        
        Cliente datos = gson.fromJson(jsonEntrada, Cliente.class);
        
        boolean exito = dao.vincularPaypal(datos.getIdUsuario(), datos.getPaypalEmail());
        
        if (exito) return "{\"status\":\"ok\"}";
        else return "{\"error\":\"Error al vincular PayPal\"}";
    }

    @POST
    @Path("/desvincular-paypal")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String desvincularPaypal(String jsonEntrada) {
        Gson gson = new Gson();
        UsuarioDao dao = new UsuarioDao();
        Cliente datos = gson.fromJson(jsonEntrada, Cliente.class); 
        
        boolean exito = dao.desvincularPaypal(datos.getIdUsuario());
        
        if (exito) return "{\"status\":\"ok\"}";
        else return "{\"error\":\"Error al desvincular\"}";
    }
    
    @GET
    @Path("/prueba")
    @Produces(MediaType.TEXT_PLAIN)
    public String probarConexion() {
        return "¡Conexión Exitosa! La API te escucha";
    }
}