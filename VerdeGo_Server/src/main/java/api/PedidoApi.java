package api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

import modelo.bean.CodigoDescuento;
import modelo.dao.CodigoDescuentoDao;
import modelo.dao.UsuarioDao; 

import java.util.ArrayList;
import java.util.List;
import modelo.bean.*;
import modelo.dao.PedidoDao;

@Path("/pedidos")
public class PedidoApi {

    @POST
    @Path("/validar-cupon")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String validarCupon(String json) {
        Gson gson = new Gson();
        CuponRequest request = gson.fromJson(json, CuponRequest.class);
        
        CodigoDescuentoDao dao = new CodigoDescuentoDao();
        CodigoDescuento cupon = dao.buscarPorCodigo(request.codigo);

        if (cupon != null) {
            return gson.toJson(cupon);
        } else {
            return "{\"error\": \"Cupón no válido o expirado\"}";
        }
    }
        
    @POST
    @Path("/finalizar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String finalizarPedido(String json) {
        Gson gson = new Gson();
        PedidoRequest req = gson.fromJson(json, PedidoRequest.class);
        
        PedidoDao dao = new PedidoDao();
        
        Pedido p = new Pedido();
        
        Cliente c = new Cliente();
        c.setIdUsuario(req.idCliente);
        p.setCliente(c);
        
        if(req.codigoDescuento != null && !req.codigoDescuento.isEmpty()) {
             CodigoDescuento cd = new CodigoDescuento();
             cd.setIdCodigo(req.idCodigoDescuento); 
             p.setCodigoDescuento(cd);
        }

        p.setTotal(req.total);
        p.setPuntosUsados(req.puntosUsados);
        
        p.setDirDireccion(req.direccion);
        p.setDirCiudad(req.ciudad);
        p.setDirCp(req.cp);
        p.setDirPais(req.pais);

        List<DetallePedido> detalles = new ArrayList<>();
        for (ProductoRequest pr : req.productos) {
            DetallePedido dp = new DetallePedido();
            Producto prod = new Producto();
            prod.setIdProducto(pr.id);
            dp.setProducto(prod);
            dp.setCantidad(pr.cantidad);
            dp.setPrecioUnitario(pr.precio);
            detalles.add(dp);
        }

        boolean exito = dao.registrarPedido(p, detalles);

        if (exito) {
            // LOGICA PARA DEVOLVER PUNTOS ACTUALIZADOS ---
            UsuarioDao usuarioDao = new UsuarioDao();
            // Usamos el método que creamos en el paso anterior
            int nuevosPuntos = usuarioDao.obtenerPuntosActuales(req.idCliente);
            
            // Devolvemos el status OK y los puntos nuevos
            return "{\"status\":\"ok\", \"nuevosPuntos\":" + nuevosPuntos + "}";
            // --------------------------------------------------------
        } else {
            return "{\"error\":\"Error al procesar el pedido\"}";
        }
    }
    
    private class PedidoRequest {
        int idCliente;
        double total;
        int puntosUsados;
        String codigoDescuento; 
        int idCodigoDescuento;  
        String direccion, ciudad, cp, pais;
        List<ProductoRequest> productos;
    }
    
    private class ProductoRequest {
        int id;
        double cantidad;
        double precio;
    }
    
    private class CuponRequest {
        String codigo;
    }
}
