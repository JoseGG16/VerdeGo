package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import modelo.bean.Cliente; 
import modelo.bean.Direccion;
import modelo.bean.Usuario; 
import modelo.dao.DireccionDao;

@Path("/direcciones")
public class DireccionApi {

    private DireccionDao dao = new DireccionDao();

   
    private Gson gson = new GsonBuilder()
        .registerTypeAdapter(Usuario.class, new InstanceCreator<Usuario>() {
            @Override
            public Usuario createInstance(Type type) {
                return new Cliente(); 
            }
        })
        .create();

    @GET
    @Path("/listar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listar(@QueryParam("idUsuario") int idUsuario) {
        List<Direccion> lista = dao.listarPorUsuario(idUsuario);
        return Response.ok(gson.toJson(lista)).build();
    }

    @POST
    @Path("/crear")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response crear(String json) {
        try {
            Direccion d = gson.fromJson(json, Direccion.class);
            boolean exito = dao.agregar(d);
            return exito ? Response.ok("{\"status\":\"ok\"}").build() 
                         : Response.status(500).entity("{\"error\":\"Error al guardar\"}").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(400).entity("{\"error\":\"Datos inválidos\"}").build();
        }
    }

    @PUT
    @Path("/editar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editar(String json) {
        try {
            Direccion d = gson.fromJson(json, Direccion.class);
            boolean exito = dao.editar(d);
            if (exito && d.isEsPrincipal()) {
                dao.marcarComoPrincipal(d.getUsuario().getIdUsuario(), d.getIdDireccion());
            }
            return exito ? Response.ok("{\"status\":\"ok\"}").build() 
                         : Response.status(500).entity("{\"error\":\"Error al editar\"}").build();
        } catch (Exception e) {
            return Response.status(400).entity("{\"error\":\"Datos inválidos\"}").build();
        }
    }

    @DELETE
    @Path("/eliminar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminar(@QueryParam("id") int idDireccion, @QueryParam("idUsuario") int idUsuario) {
        boolean exito = dao.eliminar(idDireccion, idUsuario);
        return exito ? Response.ok("{\"status\":\"ok\"}").build() 
                     : Response.status(404).entity("{\"error\":\"No encontrada\"}").build();
    }

    @POST
    @Path("/principal")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response marcarPrincipal(String json) {
        try {
            Map<String, Double> map = gson.fromJson(json, Map.class);
            int idUsuario = map.get("idUsuario").intValue();
            int idDireccion = map.get("idDireccion").intValue();
            boolean exito = dao.marcarComoPrincipal(idUsuario, idDireccion);
            return exito ? Response.ok("{\"status\":\"ok\"}").build() 
                         : Response.status(500).entity("{\"error\":\"Fallo al actualizar\"}").build();
        } catch (Exception e) {
            return Response.status(400).entity("{\"error\":\"Datos inválidos\"}").build();
        }
    }
}