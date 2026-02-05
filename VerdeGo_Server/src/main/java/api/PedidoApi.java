package api;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

import modelo.bean.Cliente;
import modelo.bean.CodigoDescuento;
import modelo.bean.DetallePedido;
import modelo.bean.Pedido;
import modelo.bean.Producto;
import modelo.dao.CodigoDescuentoDao;
import modelo.dao.PedidoDao;
import modelo.dao.UsuarioDao;

@Path("/pedidos")
public class PedidoApi {

    // 1. VALIDAR CUP�N
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
            return "{\"error\": \"Cup�n no v�lido o expirado\"}";
        }
    }
        
    // 2. FINALIZAR PEDIDO (COMPRA)
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
            // Devolver puntos actualizados
            UsuarioDao usuarioDao = new UsuarioDao();
            int nuevosPuntos = usuarioDao.obtenerPuntosActuales(req.idCliente);
            return "{\"status\":\"ok\", \"nuevosPuntos\":" + nuevosPuntos + "}";
        } else {
            return "{\"error\":\"Error al procesar el pedido\"}";
        }
    }

    // 3. LISTAR PEDIDOS (HISTORIAL)
    @GET
    @Path("/listar")
    @Produces(MediaType.APPLICATION_JSON)
    public String listarPedidos(@QueryParam("idUsuario") int idUsuario) {
        PedidoDao dao = new PedidoDao();
        List<Pedido> pedidos = dao.listarPorCliente(idUsuario);
        
        Gson gson = new Gson();
        return gson.toJson(pedidos);
    }

    // 4. CANCELAR PEDIDO
    @POST
    @Path("/cancelar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String cancelarPedido(String json) {
        Gson gson = new Gson();
        CancelRequest req = gson.fromJson(json, CancelRequest.class);
        
        PedidoDao dao = new PedidoDao();
        boolean exito = dao.cancelarPedido(req.idPedido);
        
        if (exito) {
            return "{\"status\":\"ok\", \"mensaje\":\"Pedido cancelado correctamente\"}";
        } else {
            return "{\"error\":\"No se pudo cancelar. Puede que ya no est� pendiente.\"}";
        }
    }

    // --- CLASES INTERNAS AUXILIARES (DTOs) ---
    
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

    private class CancelRequest {
        int idPedido;
    }
}
