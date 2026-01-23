package modelo.dao;

import java.sql.*;
import java.util.List;
import modelo.bean.DetallePedido;
import modelo.bean.Pedido;

public class PedidoDao {

    public boolean registrarPedido(Pedido pedido, List<DetallePedido> detalles) {
        Connection con = null;
        boolean exito = false;

        try {
            con = JdbcConnection.getConnection();
            con.setAutoCommit(false); // INICIO DE TRANSACCIÓN

            String sqlPedido = "INSERT INTO pedidos (id_cliente, id_codigo_descuento, fecha_pedido, estado, total, puntos_usados, "
                             + "dir_direccion, dir_ciudad, dir_cp, dir_pais) "
                             + "VALUES (?, ?, NOW(), 'PENDIENTE', ?, ?, ?, ?, ?, ?)";

            PreparedStatement psPedido = con.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
            
            psPedido.setInt(1, pedido.getCliente().getIdUsuario());
            
            if (pedido.getCodigoDescuento() != null) {
                psPedido.setInt(2, pedido.getCodigoDescuento().getIdCodigo());
            } else {
                psPedido.setNull(2, java.sql.Types.INTEGER);
            }

            psPedido.setDouble(3, pedido.getTotal());
            psPedido.setInt(4, pedido.getPuntosUsados());
            
            psPedido.setString(5, pedido.getDirDireccion());
            psPedido.setString(6, pedido.getDirCiudad());
            psPedido.setString(7, pedido.getDirCp());
            psPedido.setString(8, pedido.getDirPais());

            psPedido.executeUpdate();

            ResultSet rsKeys = psPedido.getGeneratedKeys();
            int idPedido = 0;
            if (rsKeys.next()) {
                idPedido = rsKeys.getInt(1);
            } else {
                throw new SQLException("No se pudo obtener ID del pedido");
            }

            String sqlDetalle = "INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
            PreparedStatement psDetalle = con.prepareStatement(sqlDetalle);

            for (DetallePedido det : detalles) {
                psDetalle.setInt(1, idPedido);
                psDetalle.setInt(2, det.getProducto().getIdProducto());
                psDetalle.setDouble(3, det.getCantidad());
                psDetalle.setDouble(4, det.getPrecioUnitario());
                psDetalle.addBatch(); 
            }
            psDetalle.executeBatch(); 

            int puntosGanados = (int) pedido.getTotal(); 
            int puntosGastados = pedido.getPuntosUsados();
            int balancePuntos = puntosGanados - puntosGastados;

            if (balancePuntos != 0) {
                String sqlPuntos = "UPDATE clientes SET puntos_fidelizacion = puntos_fidelizacion + ? WHERE id_cliente = ?";
                PreparedStatement psPuntos = con.prepareStatement(sqlPuntos);
                psPuntos.setInt(1, balancePuntos);
                psPuntos.setInt(2, pedido.getCliente().getIdUsuario());
                psPuntos.executeUpdate();
                
               
            }

            con.commit(); 
            exito = true;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback(); // DESHACER TODO SI FALLA
            } catch (SQLException ex) { ex.printStackTrace(); }
        } finally {
            try {
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException ex) { ex.printStackTrace(); }
        }
        return exito;
    }
}