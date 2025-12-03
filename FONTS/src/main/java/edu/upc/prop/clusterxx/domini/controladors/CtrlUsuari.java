package edu.upc.prop.clusterxx.domini.controladors;

import java.util.HashMap;

import edu.upc.prop.clusterxx.domini.classes.Usuari;
import edu.upc.prop.clusterxx.persistencia.CtrlPersistencia;
import static edu.upc.prop.clusterxx.domini.classes.Exceptions.UsuariJaExisteixException;

/**
 * Controlador d'usuaris que delega totes les operacions de dades a
 * CtrlPersistencia.
 * No manté dades pròpies, només coordina la lògica de negoci amb el domini.
 */
public class CtrlUsuari {
    private CtrlPersistencia persistencia;

    /**
     * Constructor que inicialitza el controlador d'usuaris.
     * Obté la instància de CtrlPersistencia per gestionar les dades.
     * 
     * @param ctrlPresentacio Referència al controlador de presentació (no utilitzat
     *                        actualment)
     */
    public CtrlUsuari(Object ctrlPresentacio) {
        this.persistencia = CtrlPersistencia.getInstance();
    }

    // Mètodes de negoci (delegen a la classe de domini Usuari)
    /**
     * Inicia sessió per a un usuari, establint-lo com a usuari actual del sistema.
     * 
     * @param usuari L'usuari que inicia sessió
     */
    public void login(Usuari usuari) {
        Usuari.login(usuari);
    }

    /**
     * Tanca la sessió de l'usuari actual.
     * Estableix l'usuari actual a null.
     */
    public void logout() {
        Usuari.logout();
    }

    /**
     * Registra un nou usuari al sistema.
     * Crea un nou usuari amb les credencials proporcionades i el guarda a la
     * persistència.
     * 
     * @param username Nom d'usuari únic
     * @param password Contrasenya de l'usuari
     * @throws UsuariJaExisteixException Si ja existeix un usuari amb aquest nom
     */
    public void registrarUsuari(String username, String password) throws UsuariJaExisteixException {
        // Crear el nou usuari directament
        Usuari nouUsuari = new Usuari(username, password);

        // Guardar a persistència
        persistencia.afegirUsuari(nouUsuari);
    }

    /**
     * Verifica si una contrasenya coincideix amb la de l'usuari actual.
     * 
     * @param password Contrasenya a verificar
     * @return true si la contrasenya és correcta, false altrament o si no hi ha
     *         usuari autenticat
     */
    public boolean checkPassword(String password) {
        Usuari usuariActual = Usuari.getUsuariActual();
        if (usuariActual != null) {
            return usuariActual.checkPassword(password);
        }
        return false;
    }

    // Getters
    /**
     * Obté l'usuari actualment autenticat al sistema.
     * 
     * @return L'usuari actual o null si no hi ha cap usuari autenticat
     */
    public Usuari getUsuariActual() {
        return Usuari.getUsuariActual();
    }

    // ===========================================
    // Mètodes per a persistència (delegació)
    // ===========================================

    /**
     * Obté tots els usuaris del sistema per a operacions de persistència.
     * 
     * @return HashMap amb tots els usuaris (clau: username, valor: Usuari)
     */
    public HashMap<String, Usuari> getTotsUsuaris() {
        return persistencia.getAllUsuaris();
    }

    /**
     * Estableix tots els usuaris des de persistència, substituint els existents.
     * 
     * @param usuaris HashMap amb els usuaris a carregar (clau: username, valor:
     *                Usuari)
     */
    public void setTotsUsuaris(HashMap<String, Usuari> usuaris) {
        if (usuaris != null) {
            persistencia.saveUsuaris(usuaris);
        }
    }

    /**
     * Afegeix un usuari existent al sistema de persistència.
     * 
     * @param usuari L'usuari a afegir
     */
    public void afegirUsuari(Usuari usuari) {
        if (usuari != null) {
            persistencia.afegirUsuari(usuari);
        }
    }

    /**
     * Obté un usuari específic pel seu nom d'usuari.
     * 
     * @param username Nom d'usuari a buscar
     * @return L'usuari trobat o null si no existeix
     */
    public Usuari getUsuari(String username) {
        return persistencia.getUsuari(username);
    }

    /**
     * Elimina un usuari del sistema de forma permanent.
     * 
     * @param username Nom d'usuari a eliminar
     */
    public void eliminarUsuari(String username) {
        persistencia.eliminarUsuari(username);
    }

    /**
     * Elimina tots els usuaris del sistema.
     * Útil per a operacions de reinicialització o neteja de dades.
     */
    public void netejarUsuaris() {
        persistencia.saveUsuaris(new HashMap<>());
    }

    /**
     * Obté el nombre total d'usuaris registrats al sistema.
     * 
     * @return Nombre d'usuaris
     */
    public int getNumUsuaris() {
        return persistencia.getNumUsuaris();
    }

    /**
     * Verifica si existeix un usuari amb el nom especificat.
     * 
     * @param username Nom d'usuari a verificar
     * @return true si l'usuari existeix, false altrament
     */
    public boolean existeixUsuari(String username) {
        return persistencia.existeixUsuari(username);
    }
}
