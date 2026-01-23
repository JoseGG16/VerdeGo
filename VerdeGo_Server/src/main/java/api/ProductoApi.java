package api;

import com.google.gson.Gson;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import modelo.bean.Producto;
import modelo.dao.ProductoDao;

@Path("/productos") 
public class ProductoApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listar() {
        ProductoDao dao = new ProductoDao();
        List<Producto> productos = dao.listarProductos();
        Gson gson = new Gson();
        String jsonRespuesta = gson.toJson(productos);

        return Response.ok(jsonRespuesta).build();
    }
    
    @GET
    @Path("/destacados")
    @Produces(MediaType.APPLICATION_JSON)
    public String getDestacados() {
        ProductoDao dao = new ProductoDao();
        
        List<Producto> lista = dao.listarProductos(); 
        
        List<Producto> destacados = lista.stream()
            .limit(4) 
            .collect(Collectors.toList());

        return new Gson().toJson(destacados);
    }
}
