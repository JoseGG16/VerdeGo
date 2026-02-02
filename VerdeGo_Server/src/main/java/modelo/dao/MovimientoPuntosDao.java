package modelo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import modelo.bean.Cliente;
import modelo.bean.MovimientoPuntos;
import modelo.bean.Pedido;

public class MovimientoPuntosDao {

    /**
     * Obtiene el historial de puntos de un cliente ordenado por fecha (más reciente primero).
     */
    public List<MovimientoPuntos> listarPorCliente(int idCliente) {
        List<MovimientoPuntos> lista = new ArrayList<>();
        
        // Ordenamos DESC para que salga primero el último movimiento
        String sql = "SELECT * FROM movimientos_puntos WHERE id_cliente = ? ORDER BY fecha DESC";

        try (Connection con = JdbcConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                MovimientoPuntos mp = new MovimientoPuntos();
                mp.setIdMovimiento(rs.getInt("id_movimiento"));
                
                // Rellenamos el Cliente solo con el ID (suficiente para referencia)
                Cliente c = new Cliente();
                c.setIdUsuario(rs.getInt("id_cliente"));
                mp.setCliente(c);
                
                // Rellenamos el Pedido si existe (puede ser NULL si fue un regalo manual)
                int idPedido = rs.getInt("id_pedido");
                if (!rs.wasNull()) {
                    Pedido p = new Pedido();
                    p.setIdPedido(idPedido);
                    mp.setPedido(p);
                }

                mp.setPuntos(rs.getInt("puntos"));
                
                // CONVERSIÓN DE STRING (BBDD) A ENUM (JAVA)
                String tipoString = rs.getString("tipo"); // "OBTENIDO", "GASTADO"...
                try {
                    mp.setTipo(MovimientoPuntos.TipoMovimiento.valueOf(tipoString));
                } catch (IllegalArgumentException e) {
                    // Si en la BBDD hay algo raro, por defecto ponemos OBTENIDO o lo manejamos
                    mp.setTipo(MovimientoPuntos.TipoMovimiento.OBTENIDO);
                }

                // Usamos getTimestamp para tener fecha y hora exacta
                mp.setFecha(rs.getTimestamp("fecha"));
                mp.setMotivo(rs.getString("motivo"));

                lista.add(mp);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}