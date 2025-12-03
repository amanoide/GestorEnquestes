package edu.upc.prop.clusterxx;

import edu.upc.prop.clusterxx.presentacio.CtrlPresentacio;

/**
 * Clase principal de la aplicación.
 */
public class Main {

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
