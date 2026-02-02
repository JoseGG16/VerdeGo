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

            // ---------------------------------------------------------
            // 1. INSERTAR EL PEDIDO (CABECERA)
            // ---------------------------------------------------------
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

            // RECUPERAR ID DEL PEDIDO GENERADO
            ResultSet rsKeys = psPedido.getGeneratedKeys();
            int idPedido = 0;
            if (rsKeys.next()) {
                idPedido = rsKeys.getInt(1);
            } else {
                throw new SQLException("No se pudo obtener ID del pedido");
            }

            // ---------------------------------------------------------
            // 2. INSERTAR DETALLES Y ACTUALIZAR STOCK
            // ---------------------------------------------------------
            String sqlDetalle = "INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
            PreparedStatement psDetalle = con.prepareStatement(sqlDetalle);

            String sqlStock = "UPDATE productos SET stock = stock - ? WHERE id_producto = ?";
            PreparedStatement psStock = con.prepareStatement(sqlStock);

            for (DetallePedido det : detalles) {
                // Detalle
                psDetalle.setInt(1, idPedido);
                psDetalle.setInt(2, det.getProducto().getIdProducto());
                psDetalle.setDouble(3, det.getCantidad());
                psDetalle.setDouble(4, det.getPrecioUnitario());
                psDetalle.addBatch(); 

                // Stock
                psStock.setDouble(1, det.getCantidad());
                psStock.setInt(2, det.getProducto().getIdProducto());
                psStock.addBatch();
            }

            psDetalle.executeBatch(); 
            psStock.executeBatch();

            // ---------------------------------------------------------
            // 3. GESTIÓN DE PUNTOS (SALDO + HISTORIAL)
            // ---------------------------------------------------------
            
            // A) Calcular ganancia y gasto
            int puntosGanados = (int) pedido.getTotal(); // 1€ = 1 punto
            int puntosGastados = pedido.getPuntosUsados();
            int balanceTotal = puntosGanados - puntosGastados;

            // B) Actualizar el saldo actual del cliente
            if (balanceTotal != 0) {
                String sqlUpdatePuntos = "UPDATE clientes SET puntos_fidelizacion = puntos_fidelizacion + ? WHERE id_cliente = ?";
                PreparedStatement psUpdatePuntos = con.prepareStatement(sqlUpdatePuntos);
                psUpdatePuntos.setInt(1, balanceTotal);
                psUpdatePuntos.setInt(2, pedido.getCliente().getIdUsuario());
                psUpdatePuntos.executeUpdate();
            }

            // C) Insertar en el HISTORIAL (movimientos_puntos) 
            String sqlHistorial = "INSERT INTO movimientos_puntos (id_cliente, id_pedido, puntos, tipo, motivo, fecha) VALUES (?, ?, ?, ?, ?, NOW())";
            PreparedStatement psHistorial = con.prepareStatement(sqlHistorial);

            // C.1 - Registro de puntos OBTENIDOS (si ganó algo)
            if (puntosGanados > 0) {
                psHistorial.setInt(1, pedido.getCliente().getIdUsuario());
                psHistorial.setInt(2, idPedido);
                psHistorial.setInt(3, puntosGanados); // Guardamos en positivo
                psHistorial.setString(4, "OBTENIDO"); // Enum de tu BBDD
                psHistorial.setString(5, "Compra Pedido #" + idPedido);
                psHistorial.executeUpdate();
            }

            // C.2 - Registro de puntos GASTADOS (si usó puntos)
            if (puntosGastados > 0) {
                psHistorial.setInt(1, pedido.getCliente().getIdUsuario());
                psHistorial.setInt(2, idPedido);
                psHistorial.setInt(3, -puntosGastados); 
                psHistorial.setString(4, "GASTADO");    
                psHistorial.setString(5, "Canje en Pedido #" + idPedido);
                psHistorial.executeUpdate();
            }

            // ---------------------------------------------------------
            // 4. CONFIRMAR TRANSACCIÓN
            // ---------------------------------------------------------
            con.commit(); 
            exito = true;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) { ex.printStackTrace(); }
        } finally {
            try {
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException ex) { ex.printStackTrace(); }
        }
        return exito;
    }
}
