package edu.upc.prop.clusterxx.presentacio;

import edu.upc.prop.clusterxx.domini.controladors.CtrlDomini;
import java.util.ArrayList;

/**
 * Controlador principal de la capa de presentació.
 * Coordina les vistes i comunica amb el controlador de domini.
 */
public class CtrlPresentacio {
    private CtrlDomini ctrlDomini;
    private VistaPrincipal vistaPrincipal;

    public CtrlPresentacio() {
        ctrlDomini = new CtrlDomini();
        vistaPrincipal = new VistaPrincipal(this);
    }

    public void inicializarPresentacio() {
        // ctrlDomini.inicializarCtrlDomini(); // No existe este método, la
        // inicialización se hace en el constructor
        vistaPrincipal.hacerVisible();
    }

    /**
     * Intenta autenticar a un usuario en el sistema.
     * 
     * @param user Nombre de usuario
     * @param pass Contraseña
     * @return true si el login es correcto, false si falla (o lanza excepción)
     */
    public boolean login(String user, String pass) {
        try {
            ctrlDomini.login(user, pass);
            return true;
        } catch (Exception e) {
            // En una implementación real, propagaríamos la excepción o devolveríamos un
            // mensaje de error
            System.out.println("Error en login: " + e.getMessage());
            return false;
        }
    }

    /**
     * Crea una nueva encuesta.
     * 
     * @param id    Identificador
     * @param titol Título
     * @param desc  Descripció
     * @return Mensaje de éxito o error
     */
    public String crearEnquesta(String id, String titol, String desc) {
        try {
            ctrlDomini.crearEnquesta(id, titol, desc);
            return "Enquesta creada correctament!";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
