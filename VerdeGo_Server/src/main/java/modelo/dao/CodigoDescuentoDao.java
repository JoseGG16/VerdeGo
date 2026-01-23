package modelo.dao;

import java.sql.*;


import modelo.bean.CodigoDescuento;

public class CodigoDescuentoDao {

    public CodigoDescuento buscarPorCodigo(String codigo) {
        CodigoDescuento cupon = null;
        String sql = "SELECT * FROM codigos_descuento WHERE codigo = ? AND activo = true AND fecha_caducidad >= CURDATE()";

        try (Connection con = JdbcConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                cupon = new CodigoDescuento();
                cupon.setIdCodigo(rs.getInt("id_codigo"));
                cupon.setCodigo(rs.getString("codigo"));
                
                String tipoStr = rs.getString("tipo");
                cupon.setTipo(CodigoDescuento.TipoDescuento.valueOf(tipoStr));
                
                cupon.setValor(rs.getDouble("valor"));
                cupon.setFechaCaducidad(rs.getDate("fecha_caducidad"));
                cupon.setActivo(rs.getBoolean("activo"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cupon;
    }
}