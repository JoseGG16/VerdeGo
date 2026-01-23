package modelo.dao;

import java.util.Scanner;
import modelo.bean.Cliente;
import modelo.bean.Empleado;
import modelo.bean.Usuario;

public class Test {

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        UsuarioDao dao = new UsuarioDao();

        System.out.println("===LOGIN VERDEGO (MODO CONSOLA)===");
        
        // 1. Pedimos datos al usuario por consola
        System.out.print("Introduce tu Email: ");
        String email = teclado.nextLine();

        System.out.print("Introduce tu Password: ");
        String pass = teclado.nextLine();

        System.out.println("----------------------------------------");
        System.out.println("Verificando credenciales...");

        Usuario usuarioLogueado = dao.validarLogin(email, pass);

        if (usuarioLogueado != null) {
            System.out.println("ACCESO CONCEDIDO");
            System.out.println("Bienvenido/a: " + usuarioLogueado.getNombre() + " " + usuarioLogueado.getApellidos());
            
            System.out.println("Tu Rol es: " + usuarioLogueado.getRol().getNombre());
            System.out.println("Descripción: " + usuarioLogueado.getRol().getDescripcion());

            System.out.println("--- Datos Específicos ---");

            if (usuarioLogueado instanceof Cliente) {
                Cliente c = (Cliente) usuarioLogueado;
                System.out.println("Puntos de Fidelización: " + c.getPuntosFidelizacion());
                System.out.println("Acceso a tienda habilitado.");
            
            } else if (usuarioLogueado instanceof Empleado) {
                Empleado e = (Empleado) usuarioLogueado;
                System.out.println(" Fecha de Alta en la empresa: " + e.getFechaAlta());
                System.out.println("Panel de gestión habilitado.");
            
            } else {
                System.out.println("Perfil Administrativo o Genérico.");
            }

        } else {
            System.out.println("ERROR: Usuario o contraseña incorrectos.");
        }

        System.out.println("========================================");
        teclado.close();
    }
}