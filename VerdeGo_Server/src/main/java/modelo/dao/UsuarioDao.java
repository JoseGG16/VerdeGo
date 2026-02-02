package modelo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import modelo.bean.Cliente;
import modelo.bean.Empleado;
import modelo.bean.Rol;
import modelo.bean.Usuario;

public class UsuarioDao {

    /**
     * Valida el login buscando por email y password.
     * Devuelve un objeto Cliente o Empleado según el rol.
     */
    public Usuario validarLogin(String email, String password) {
        Usuario usuario = null;
        
        String sql = "SELECT u.*, r.nombre AS nombre_rol, r.descripcion " +
                     "FROM usuarios u " +
                     "JOIN roles r ON u.id_rol = r.id_rol " +
                     "WHERE u.email = ? AND u.password = ? AND u.estado = 1"; 

        try (Connection con = JdbcConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            
            String passHasheada = Seguridad.hashearPassword(password);
            ps.setString(2, passHasheada);
            // -----------------------------------------------------------
            
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Rol rol = new Rol();
                rol.setIdRol(rs.getInt("id_rol"));
                rol.setNombre(rs.getString("nombre_rol"));
                rol.setDescripcion(rs.getString("descripcion"));

                int idRol = rol.getIdRol();

                if (idRol == 2) { 
                    Cliente cliente = new Cliente();
                    cargarDatosComunes(cliente, rs, rol);
                    
                    buscarDatosCliente(con, cliente);
                    usuario = cliente;

                } else { 
                    
                    Empleado empleado = new Empleado();
                    cargarDatosComunes(empleado, rs, rol);
                    
                    buscarDatosEmpleado(con, empleado);
                    usuario = empleado;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error en login: " + e.getMessage());
        }

        return usuario;
    }

    public boolean registrarUsuario(Usuario usuario) {
        boolean registrado = false;
        Connection connection = null;

        String sqlUsuario = "INSERT INTO usuarios (nombre, apellidos, email, password, id_rol, estado) VALUES (?, ?, ?, ?, ?, ?)";
        
        String sqlCliente = "INSERT INTO clientes (id_cliente, puntos_fidelizacion) VALUES (?, ?)";

        try {
            connection = JdbcConnection.getConnection();
            connection.setAutoCommit(false);

            PreparedStatement psUser = connection.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS);
            
            psUser.setString(1, usuario.getNombre());
            psUser.setString(2, usuario.getApellidos()); 
            psUser.setString(3, usuario.getEmail());
            
            String passHasheada = Seguridad.hashearPassword(usuario.getPassword());
            psUser.setString(4, passHasheada); 
            
            psUser.setInt(5, 2); 
            psUser.setBoolean(6, true);
            

            int filasUser = psUser.executeUpdate();

            if (filasUser > 0) {
                ResultSet rsKeys = psUser.getGeneratedKeys();
                if (rsKeys.next()) {
                    int idGenerado = rsKeys.getInt(1);

                   
                    PreparedStatement psClient = connection.prepareStatement(sqlCliente);
                    psClient.setInt(1, idGenerado);
                    psClient.setInt(2, 0); 
                    
                    int filasClient = psClient.executeUpdate();

                    if (filasClient > 0) {
                        connection.commit();
                        registrado = true;
                    } else {
                        connection.rollback();
                    }
                }
            } else {
                connection.rollback();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException ex) { ex.printStackTrace(); }
        } finally {
            try {
                if (connection != null) connection.setAutoCommit(true);
            } catch (SQLException ex) { ex.printStackTrace(); }
        }
        
        return registrado;
    }
    
    public boolean modificarUsuario(Usuario usuario) {
        boolean modificado = false;
        
        String sql = "UPDATE usuarios SET nombre = ?, apellidos = ?, telefono = ? WHERE id_usuario = ?";
        
        try (Connection con = JdbcConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellidos());
            ps.setString(3, usuario.getTelefono());
            ps.setInt(4, usuario.getIdUsuario()); 
            
            int filas = ps.executeUpdate();
            if (filas > 0) {
                modificado = true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return modificado;
    }
    
    public boolean vincularPaypal(int idUsuario, String emailPaypal) {
        boolean exito = false;
        
        try (Connection con = JdbcConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE clientes SET paypal_email = ? WHERE id_cliente = ?")) {
            
            ps.setString(1, emailPaypal);
            ps.setInt(2, idUsuario);
            
            int filas = ps.executeUpdate();
            if (filas > 0) exito = true;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exito;
    }

    public boolean desvincularPaypal(int idUsuario) {
        try (Connection con = JdbcConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE clientes SET paypal_email = NULL WHERE id_cliente = ?")) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================
    //   <--- NUEVO: MÉTODO PARA OBTENER PUNTOS
    // ==========================================
    public int obtenerPuntosActuales(int idUsuario) {
        int puntos = 0;
        String sql = "SELECT puntos_fidelizacion FROM clientes WHERE id_cliente = ?";
        
        try (Connection con = JdbcConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                puntos = rs.getInt("puntos_fidelizacion");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return puntos;
    }

    // ==========================================
    // MÉTODOS AUXILIARES PRIVADOS
    // ==========================================

    private void cargarDatosComunes(Usuario u, ResultSet rs, Rol rol) throws SQLException {
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setRol(rol);
        u.setNombre(rs.getString("nombre"));
        u.setApellidos(rs.getString("apellidos"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password")); 
        u.setTelefono(rs.getString("telefono"));
        u.setEstado(rs.getBoolean("estado"));
        u.setFechaRegistro(rs.getTimestamp("fecha_registro"));
    }

    private void buscarDatosCliente(Connection con, Cliente cliente) throws SQLException {
        String sql = "SELECT puntos_fidelizacion, paypal_email FROM clientes WHERE id_cliente = ?";        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cliente.getIdUsuario());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                cliente.setPuntosFidelizacion(rs.getInt("puntos_fidelizacion"));
                cliente.setPaypalEmail(rs.getString("paypal_email"));
            }
        }
    }

    private void buscarDatosEmpleado(Connection con, Empleado empleado) throws SQLException {
        String sql = "SELECT fecha_alta, observaciones FROM empleados WHERE id_empleado = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, empleado.getIdUsuario());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                empleado.setFechaAlta(rs.getDate("fecha_alta"));
                empleado.setObservaciones(rs.getString("observaciones"));
            }
        }
    }
}
