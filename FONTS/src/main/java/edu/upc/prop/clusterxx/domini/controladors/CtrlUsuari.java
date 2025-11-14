package edu.upc.prop.clusterxx.domini.controladors;

import java.util.HashMap;

import edu.upc.prop.clusterxx.domini.classes.Usuari;
import static edu.upc.prop.clusterxx.domini.classes.Exceptions.UsuariJaExisteixException;

/**
 * Controlador d'usuaris que delega totes les operacions de dades a CtrlPersistencia.
 * No manté dades pròpies, només coordina la lògica de negoci amb el domini.
 */
public class CtrlUsuari {
    private CtrlPersistencia persistencia;

    public CtrlUsuari(Object ctrlPresentacio) {
        this.persistencia = CtrlPersistencia.getInstance();
    }

    // Mètodes de negoci (delegen a la classe de domini Usuari)
    public void login(Usuari usuari) {
        Usuari.login(usuari);
    }

    public void logout() {
        Usuari.logout();
    }

    public void registrarUsuari(String username, String password) throws UsuariJaExisteixException {
        // Crear el nou usuari directament
        Usuari nouUsuari = new Usuari(username, password);
        
        // Guardar a persistència
        persistencia.afegirUsuari(username, nouUsuari);
    }

    public boolean checkPassword(String password) {
        Usuari usuariActual = Usuari.getUsuariActual();
        if (usuariActual != null) {
            return usuariActual.checkPassword(password);
        }
        return false;
    }

    // Getters
    public Usuari getUsuariActual() {
        return Usuari.getUsuariActual();
    }

    // ===========================================
    // Mètodes per a persistència (delegació)
    // ===========================================

    /**
     * Obté tots els usuaris per a persistència.
     * @return HashMap amb tots els usuaris (username -> Usuari)
     */
    public HashMap<String, Usuari> getTotsUsuaris() {
        return persistencia.getAllUsuaris();
    }

    /**
     * Estableix tots els usuaris des de persistència.
     * @param usuaris HashMap amb els usuaris a carregar
     */
    public void setTotsUsuaris(HashMap<String, Usuari> usuaris) {
        if (usuaris != null) {
            persistencia.saveUsuaris(usuaris);
        }
    }

    /**
     * Afegeix un usuari al sistema.
     * @param usuari L'usuari a afegir
     */
    public void afegirUsuari(Usuari usuari) {
        if (usuari != null) {
            persistencia.afegirUsuari(usuari.getUsername(), usuari);
        }
    }

    /**
     * Obté un usuari pel seu username.
     * @param username El nom d'usuari
     * @return L'usuari o null si no existeix
     */
    public Usuari getUsuari(String username) {
        return persistencia.getUsuari(username);
    }

    /**
     * Elimina un usuari del sistema.
     * @param username El nom d'usuari a eliminar
     */
    public void eliminarUsuari(String username) {
        persistencia.eliminarUsuari(username);
    }

    /**
     * Neteja tots els usuaris (útil per a reinicialització).
     */
    public void netejarUsuaris() {
        persistencia.saveUsuaris(new HashMap<>());
    }

    /**
     * Obté el nombre total d'usuaris registrats.
     * @return Nombre d'usuaris
     */
    public int getNumUsuaris() {
        return persistencia.getNumUsuaris();
    }

    /**
     * Comprova si un usuari existeix.
     * @param username El nom d'usuari a comprovar
     * @return true si existeix, false altrament
     */
    public boolean existeixUsuari(String username) {
        return persistencia.existeixUsuari(username);
    }
}
