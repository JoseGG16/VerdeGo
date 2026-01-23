package modelo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.bean.Categoria;
import modelo.bean.Producto;

public class ProductoDao {

    
    public List<Producto> listarProductos() {
        List<Producto> lista = new ArrayList<>();

        String sql = "SELECT p.*, c.nombre AS nombre_cat, c.descripcion AS desc_cat " +
                     "FROM productos p " +
                     "JOIN categorias c ON p.id_categoria = c.id_categoria " +
                     "WHERE p.activo = 1 " + 
                     "ORDER BY p.id_categoria, p.nombre"; 

        try (Connection con = JdbcConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Producto p = new Producto();
                
                p.setIdProducto(rs.getInt("id_producto"));
                p.setNombre(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setImagenUrl(rs.getString("imagen_url"));
                p.setPrecio(rs.getBigDecimal("precio"));
                p.setStock(rs.getBigDecimal("stock"));
                p.setTipoVenta(rs.getString("tipo_venta"));     
                p.setUnidadMedida(rs.getString("unidad_medida")); 
                p.setActivo(rs.getBoolean("activo"));

                Categoria cat = new Categoria();
                cat.setIdCategoria(rs.getInt("id_categoria")); 
                cat.setNombre(rs.getString("nombre_cat"));     
                cat.setDescripcion(rs.getString("desc_cat"));  
                
                p.setCategoria(cat);

                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error al listar productos: " + e.getMessage());
        }
        
        return lista;
    }
}