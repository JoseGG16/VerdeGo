package modelo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import modelo.bean.DetallePedido;
import modelo.bean.Pedido;
import modelo.bean.Producto;


public class PedidoDao {

    /**
     * REGISTRAR PEDIDO (INSERT)
     */
public boolean registrarPedido(Pedido p, List<DetallePedido> detalles) {
        
        // 1. CALCULO DE PUNTOS
        double ratioEurosPuntos = 1.0; 
        int puntosGanadosCalculados = (int) (p.getTotal() * ratioEurosPuntos);
        p.setPuntosGanados(puntosGanadosCalculados);

        String sqlPedido = "INSERT INTO pedidos (id_cliente, id_codigo_descuento, fecha_pedido, estado, total, puntos_usados, puntos_ganados, descuento_aplicado, nombre_cupon, dir_direccion, dir_ciudad, dir_cp, dir_pais) VALUES (?, ?, NOW(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
        String sqlMovimiento = "INSERT INTO movimientos_puntos (id_cliente, id_pedido, puntos, tipo, fecha, motivo) VALUES (?, ?, ?, ?, NOW(), ?)";
        
        String sqlConsultarCupon = "SELECT codigo, tipo, valor FROM codigos_descuento WHERE id_codigo = ?";

        Connection con = null;
        try {
            con = JdbcConnection.getConnection();
            con.setAutoCommit(false); 

            // 2. CALCULO DE DESCUENTO (SNAPSHOT) - CONSULTANDO BD
            double descuentoGuardar = 0.0;
            String nombreCuponGuardar = null;

            if (p.getCodigoDescuento() != null) {
                try (PreparedStatement pstCup = con.prepareStatement(sqlConsultarCupon)) {
                    pstCup.setInt(1, p.getCodigoDescuento().getIdCodigo());
                    try (ResultSet rsCup = pstCup.executeQuery()) {
                        if (rsCup.next()) {
                            nombreCuponGuardar = rsCup.getString("codigo");
                            String tipoStr = rsCup.getString("tipo");
                            double valorCupon = rsCup.getDouble("valor");

                            if ("FIJO".equals(tipoStr)) {
                                descuentoGuardar = valorCupon;
                            } else {
                                // C�lculo inverso para porcentaje: Descuento = (Total / (1 - %)) - Total
                                double porcentaje = valorCupon / 100.0;
                                if (porcentaje < 1.0) {
                                    double precioOriginal = p.getTotal() / (1.0 - porcentaje);
                                    descuentoGuardar = precioOriginal - p.getTotal();
                                }
                            }
                        }
                    }
                }
            }
            
            // Actualizar el objeto con lo calculado
            p.setDescuentoAplicado(descuentoGuardar);
            p.setNombreCupon(nombreCuponGuardar);

            // --- 1. INSERTAR EL PEDIDO ---
            PreparedStatement pst = con.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
            pst.setInt(1, p.getCliente().getIdUsuario()); 
            
            if (p.getCodigoDescuento() != null) {
                pst.setInt(2, p.getCodigoDescuento().getIdCodigo());
            } else {
                pst.setNull(2, java.sql.Types.INTEGER);
            }
            
            pst.setString(3, "PENDIENTE"); 
            pst.setDouble(4, p.getTotal());
            pst.setInt(5, p.getPuntosUsados());
            pst.setInt(6, p.getPuntosGanados());
            
            pst.setDouble(7, p.getDescuentoAplicado());
            pst.setString(8, p.getNombreCupon());
            
            pst.setString(9, p.getDirDireccion());
            pst.setString(10, p.getDirCiudad());
            pst.setString(11, p.getDirCp());
            pst.setString(12, p.getDirPais());

            int filas = pst.executeUpdate();
            if (filas == 0) throw new SQLException("No se pudo guardar el pedido");

            ResultSet rsKeys = pst.getGeneratedKeys();
            int idPedidoGenerado = 0;
            if (rsKeys.next()) {
                idPedidoGenerado = rsKeys.getInt(1);
            } else {
                throw new SQLException("No se obtuvo el ID del pedido");
            }

            // --- 2. INSERTAR DETALLES ---
            PreparedStatement pstDet = con.prepareStatement(sqlDetalle);
            for (DetallePedido dp : detalles) {
                pstDet.setInt(1, idPedidoGenerado);
                pstDet.setInt(2, dp.getProducto().getIdProducto());
                pstDet.setDouble(3, dp.getCantidad());
                pstDet.setDouble(4, dp.getPrecioUnitario());
                pstDet.addBatch(); 
            }
            pstDet.executeBatch(); 

            // --- 3. REGISTRAR MOVIMIENTOS DE PUNTOS ---
            if (p.getPuntosUsados() > 0) {
                PreparedStatement pstMov = con.prepareStatement(sqlMovimiento);
                pstMov.setInt(1, p.getCliente().getIdUsuario());
                pstMov.setInt(2, idPedidoGenerado);
                pstMov.setInt(3, -p.getPuntosUsados()); 
                pstMov.setString(4, "GASTADO");
                pstMov.setString(5, "Canje en Pedido #" + idPedidoGenerado);
                pstMov.executeUpdate();
            }

            if (p.getPuntosGanados() > 0) {
                PreparedStatement pstMov = con.prepareStatement(sqlMovimiento);
                pstMov.setInt(1, p.getCliente().getIdUsuario());
                pstMov.setInt(2, idPedidoGenerado);
                pstMov.setInt(3, p.getPuntosGanados()); 
                pstMov.setString(4, "OBTENIDO");
                pstMov.setString(5, "Ganados en Pedido #" + idPedidoGenerado);
                pstMov.executeUpdate();
            }

            // --- 4. ACTUALIZAR SALDO ---
            actualizarPuntosCliente(con, p.getCliente().getIdUsuario(), p.getPuntosUsados(), p.getPuntosGanados());

            con.commit(); 
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try { if (con != null) con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    
    public List<Pedido> listarPorCliente(int idCliente) {
        List<Pedido> lista = new ArrayList<>();
        
        String sql = "SELECT * FROM pedidos WHERE id_cliente = ? ORDER BY fecha_pedido DESC";

        try (Connection con = JdbcConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, idCliente);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Pedido p = new Pedido();
                p.setIdPedido(rs.getInt("id_pedido"));
                p.setFechaPedido(rs.getTimestamp("fecha_pedido")); 
                
                String estadoStr = rs.getString("estado");
                try {
                    p.setEstado(Pedido.EstadoPedido.valueOf(estadoStr.toUpperCase()));
                } catch (Exception e) {
                    p.setEstado(Pedido.EstadoPedido.PENDIENTE);
                }

                p.setTotal(rs.getDouble("total"));
                p.setPuntosUsados(rs.getInt("puntos_usados"));
                
                try {
                    p.setPuntosGanados(rs.getInt("puntos_ganados"));
                } catch (SQLException e) {
                    p.setPuntosGanados((int) rs.getDouble("total"));
                }

                p.setDescuentoAplicado(rs.getDouble("descuento_aplicado"));
                p.setNombreCupon(rs.getString("nombre_cupon"));

                p.setDirDireccion(rs.getString("dir_direccion"));
                p.setDirCiudad(rs.getString("dir_ciudad"));
                p.setDirCp(rs.getString("dir_cp"));
                p.setDirPais(rs.getString("dir_pais"));
                
                p.setDetalles(listarDetallesDePedido(p.getIdPedido(), con));
                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private List<DetallePedido> listarDetallesDePedido(int idPedido, Connection con) {
        List<DetallePedido> detalles = new ArrayList<>();
        String sql = "SELECT d.*, p.nombre, p.imagen_url, p.unidad_medida " +
                     "FROM detalles_pedido d " +
                     "JOIN productos p ON d.id_producto = p.id_producto " +
                     "WHERE d.id_pedido = ?";
        
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, idPedido);
            ResultSet rs = pst.executeQuery();
            
            while(rs.next()) {
                DetallePedido dp = new DetallePedido();
                dp.setCantidad(rs.getDouble("cantidad"));
                dp.setPrecioUnitario(rs.getDouble("precio_unitario"));
                
                Producto prod = new Producto();
                prod.setIdProducto(rs.getInt("id_producto"));
                prod.setNombre(rs.getString("nombre"));
                prod.setImagenUrl(rs.getString("imagen_url")); 
                prod.setUnidadMedida(rs.getString("unidad_medida"));
                
                dp.setProducto(prod);
                detalles.add(dp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return detalles;
    }

    
    public boolean cancelarPedido(int idPedido) {
        String sql = "UPDATE pedidos SET estado = 'CANCELADO' WHERE id_pedido = ? AND estado = 'PENDIENTE'";
        
        try (Connection con = JdbcConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setInt(1, idPedido);
            int filas = pst.executeUpdate();
            return filas > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void actualizarPuntosCliente(Connection con, int idCliente, int usados, int ganados) throws SQLException {
        String select = "SELECT puntos_fidelizacion FROM clientes WHERE id_cliente = ?";
        int actuales = 0;
        
        try(PreparedStatement pst = con.prepareStatement(select)) {
            pst.setInt(1, idCliente);
            ResultSet rs = pst.executeQuery();
            if(rs.next()) {
                actuales = rs.getInt("puntos_fidelizacion");
            }
        }

        int nuevos = actuales - usados + ganados;
        if(nuevos < 0) nuevos = 0;

        String update = "UPDATE clientes SET puntos_fidelizacion = ? WHERE id_cliente = ?";
        try(PreparedStatement pst = con.prepareStatement(update)) {
            pst.setInt(1, nuevos);
            pst.setInt(2, idCliente);
            pst.executeUpdate();
        }
    }
}
