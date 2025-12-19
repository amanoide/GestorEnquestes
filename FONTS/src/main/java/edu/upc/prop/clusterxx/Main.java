package edu.upc.prop.clusterxx;

import edu.upc.prop.clusterxx.presentacio.CtrlPresentacio;

/**
 * Classe principal de l'aplicació que inicia el sistema de clustering.
 */
public class Main {

    /**
     * Punt d'entrada principal de l'aplicació.
     * 
     * @param args Arguments de línia de comandes (no utilitzats)
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        CtrlPresentacio ctrlPresentacio = new CtrlPresentacio();
                        ctrlPresentacio.inicializarPresentacio();
                    }
                });
    }
}
