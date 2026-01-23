package modelo.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modelo.bean.Direccion;
import modelo.bean.Usuario;
import modelo.bean.Cliente; // <--- IMPORTANTE: Importamos la clase hija concreta


public class DireccionDao {

    public List<Direccion> listarPorUsuario(int idUsuario) {
        List<Direccion> lista = new ArrayList<>();
        String sql = "SELECT * FROM direcciones WHERE id_usuario = ? ORDER BY es_principal DESC, id_direccion DESC";

        try (Connection con = JdbcConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Direccion d = new Direccion();
                d.setIdDireccion(rs.getInt("id_direccion"));
                
                Usuario u = new Cliente(); 
                u.setIdUsuario(idUsuario);
                d.setUsuario(u);
                
                d.setAlias(rs.getString("alias"));
                d.setCalle(rs.getString("calle"));
                d.setCiudad(rs.getString("ciudad"));
                d.setCodigoPostal(rs.getString("codigo_postal"));
                d.setPais(rs.getString("pais"));
                d.setEsPrincipal(rs.getBoolean("es_principal"));
                
                lista.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Direccion obtenerPorId(int idDireccion) {
        Direccion d = null;
        String sql = "SELECT * FROM direcciones WHERE id_direccion = ?";

        try (Connection con = JdbcConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idDireccion);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                d = new Direccion();
                d.setIdDireccion(rs.getInt("id_direccion"));
                
                Usuario u = new Cliente();
                u.setIdUsuario(rs.getInt("id_usuario"));
                d.setUsuario(u);
                
                d.setAlias(rs.getString("alias"));
                d.setCalle(rs.getString("calle"));
                d.setCiudad(rs.getString("ciudad"));
                d.setCodigoPostal(rs.getString("codigo_postal"));
                d.setPais(rs.getString("pais"));
                d.setEsPrincipal(rs.getBoolean("es_principal"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return d;
    }

    public boolean agregar(Direccion d) {
        Connection con = null;
        boolean exito = false;
        
        try {
            con = JdbcConnection.getConnection();
            
            String sqlCheck = "SELECT COUNT(*) FROM direcciones WHERE id_usuario = ?";
            PreparedStatement psCheck = con.prepareStatement(sqlCheck);
            psCheck.setInt(1, d.getUsuario().getIdUsuario());
            ResultSet rs = psCheck.executeQuery();
            boolean esPrimera = false;
            if (rs.next() && rs.getInt(1) == 0) {
                esPrimera = true;
            }

            String sql = "INSERT INTO direcciones (id_usuario, alias, calle, ciudad, codigo_postal, pais, es_principal) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            
            ps.setInt(1, d.getUsuario().getIdUsuario());
            ps.setString(2, d.getAlias());
            ps.setString(3, d.getCalle());
            ps.setString(4, d.getCiudad());
            ps.setString(5, d.getCodigoPostal());
            ps.setString(6, d.getPais());
            ps.setBoolean(7, esPrimera || d.isEsPrincipal());

            if(esPrimera || d.isEsPrincipal()){
                quitarPrincipalOtras(con, d.getUsuario().getIdUsuario(), -1);
            }

            exito = ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException e) {}
        }
        return exito;
    }

    public boolean editar(Direccion d) {
        String sql = "UPDATE direcciones SET alias=?, calle=?, ciudad=?, codigo_postal=?, pais=? WHERE id_direccion=? AND id_usuario=?";
        
        try (Connection con = JdbcConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, d.getAlias());
            ps.setString(2, d.getCalle());
            ps.setString(3, d.getCiudad());
            ps.setString(4, d.getCodigoPostal());
            ps.setString(5, d.getPais());
            ps.setInt(6, d.getIdDireccion());
            ps.setInt(7, d.getUsuario().getIdUsuario());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(int idDireccion, int idUsuario) {
        String sql = "DELETE FROM direcciones WHERE id_direccion=? AND id_usuario=?";
        try (Connection con = JdbcConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idDireccion);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean marcarComoPrincipal(int idUsuario, int idDireccion) {
        Connection con = null;
        boolean exito = false;

        try {
            con = JdbcConnection.getConnection();
            con.setAutoCommit(false); // INICIO TRANSACCIÓN

            String sqlReset = "UPDATE direcciones SET es_principal = 0 WHERE id_usuario = ?";
            PreparedStatement psReset = con.prepareStatement(sqlReset);
            psReset.setInt(1, idUsuario);
            psReset.executeUpdate();

            String sqlSet = "UPDATE direcciones SET es_principal = 1 WHERE id_direccion = ? AND id_usuario = ?";
            PreparedStatement psSet = con.prepareStatement(sqlSet);
            psSet.setInt(1, idDireccion);
            psSet.setInt(2, idUsuario);
            int rows = psSet.executeUpdate();

            if (rows > 0) {
                con.commit();
                exito = true;
            } else {
                con.rollback();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            try { if (con != null) con.rollback(); } catch (SQLException ex) {}
        } finally {
            try { if (con != null) { con.setAutoCommit(true); con.close(); } } catch (SQLException e) {}
        }
        return exito;
    }

    private void quitarPrincipalOtras(Connection con, int idUsuario, int idExcepcion) throws SQLException {
        String sql = "UPDATE direcciones SET es_principal = 0 WHERE id_usuario = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, idUsuario);
        ps.executeUpdate();
    }
}