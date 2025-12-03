package edu.upc.prop.clusterxx.domini.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Representa un usuari del sistema.
 * <p>
 * Aquesta classe emmagatzema les dades d'un usuari, incloent-hi les seves
 * credencials,
 * les enquestes que ha creat, les respostes que ha donat i els perfils de
 * personalitat
 * que se li han assignat després d'analitzar les seves respostes a una
 * enquesta.
 * </p>
 * <p>
 * També gestiona la sessió de l'usuari actual mitjançant mètodes estàtics,
 * actuant
 * com un punt d'accés global a l'usuari autenticat.
 * </p>
 *
 * @see Enquesta
 * @see Resposta
 * @see Perfil
 */
public class Usuari {
    /**
     * Emmagatzema l'usuari que ha iniciat sessió actualment. És {@code null} si no
     * hi ha cap sessió activa.
     */
    private static Usuari usuariActual = null;

    // Atributs d'instància
    private String username;
    private String password;
    private List<Enquesta> enquestesCreades;
    private HashMap<String, Resposta> respostesUsuari; // idResposta -> Resposta
    private HashMap<String, Perfil> perfils; // idEnquesta -> Perfil (un perfil per enquesta analitzada)

    /**
     * Constructor de la classe Usuari.
     *
     * @param username El nom d'usuari, que ha de ser únic.
     * @param password La contrasenya de l'usuari.
     */
    public Usuari(String username, String password) {
        this.username = username;
        this.password = password;
        this.enquestesCreades = new ArrayList<>();
        this.respostesUsuari = new HashMap<>();
        this.perfils = new HashMap<>();
    }

    // Mètodes públics de negoci
    /**
     * Comprova si la contrasenya proporcionada coincideix amb la de l'usuari.
     *
     * @param password La contrasenya a verificar.
     * @return {@code true} si la contrasenya és correcta, {@code false} en cas
     *         contrari.
     */
    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    /**
     * Afegeix una enquesta a la llista d'enquestes creades per aquest usuari.
     *
     * @param enquesta L'enquesta que ha creat l'usuari.
     */
    public void addEnquestaCreada(Enquesta enquesta) {
        this.enquestesCreades.add(enquesta);
    }

    /**
     * Elimina una enquesta de la llista d'enquestes creades per aquest usuari.
     *
     * @param enquesta L'enquesta a eliminar.
     */
    public void removeEnquestaCreada(Enquesta enquesta) {
        this.enquestesCreades.remove(enquesta);
    }

    /**
     * Afegeix una resposta a la col·lecció de respostes de l'usuari.
     *
     * @param idResposta L'identificador de la resposta.
     * @param resposta   L'objecte {@link Resposta}.
     */
    public void afegirResposta(String idResposta, Resposta resposta) {
        this.respostesUsuari.put(idResposta, resposta);
    }

    /**
     * Elimina una resposta de la col·lecció de respostes de l'usuari.
     *
     * @param idResposta L'identificador de la resposta a eliminar.
     */
    public void eliminarResposta(String idResposta) {
        this.respostesUsuari.remove(idResposta);
    }

    /**
     * Obté una resposta específica de l'usuari a partir del seu ID.
     *
     * @param idResposta L'identificador de la resposta.
     * @return L'objecte {@link Resposta}, o {@code null} si no existeix.
     */
    public Resposta getResposta(String idResposta) {
        return this.respostesUsuari.get(idResposta);
    }

    /**
     * Retorna una còpia del mapa de totes les respostes donades per l'usuari.
     *
     * @return Un {@code HashMap} que mapeja ID de resposta a objectes
     *         {@link Resposta}.
     */
    public HashMap<String, Resposta> getRespostesUsuari() {
        return new HashMap<>(respostesUsuari);
    }

    // Mètodes per a gestió de perfils
    /**
     * Assigna un perfil de personalitat a l'usuari per a una enquesta específica.
     *
     * @param idEnquesta L'ID de l'enquesta analitzada.
     * @param perfil     El {@link Perfil} resultant de l'anàlisi.
     */
    public void assignarPerfil(String idEnquesta, Perfil perfil) {
        this.perfils.put(idEnquesta, perfil);
    }

    /**
     * Obté el perfil de l'usuari per a una enquesta específica.
     *
     * @param idEnquesta L'ID de l'enquesta.
     * @return El {@link Perfil} associat, o {@code null} si no en té.
     */
    public Perfil getPerfil(String idEnquesta) {
        return this.perfils.get(idEnquesta);
    }

    /**
     * Retorna una còpia del mapa de tots els perfils de l'usuari.
     *
     * @return Un {@code HashMap} que mapeja ID d'enquesta a objectes
     *         {@link Perfil}.
     */
    public HashMap<String, Perfil> getPerfils() {
        return new HashMap<>(perfils);
    }

    /**
     * Comprova si l'usuari té un perfil assignat per a una enquesta específica.
     *
     * @param idEnquesta L'ID de l'enquesta a comprovar.
     * @return {@code true} si té un perfil, {@code false} en cas contrari.
     */
    public boolean tePerfil(String idEnquesta) {
        return this.perfils.containsKey(idEnquesta);
    }

    /**
     * Elimina el perfil de l'usuari per a una enquesta específica.
     *
     * @param idEnquesta L'ID de l'enquesta el perfil de la qual s'eliminarà.
     */
    public void eliminarPerfil(String idEnquesta) {
        this.perfils.remove(idEnquesta);
    }

    // Getters
    /**
     * Retorna el nom d'usuari.
     * 
     * @return El nom d'usuari.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Retorna la contrasenya de l'usuari.
     * Necessari per a la persistència.
     * 
     * @return La contrasenya.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Retorna una còpia de la llista d'enquestes creades per l'usuari.
     * 
     * @return Una {@code List} d'objectes {@link Enquesta}.
     */
    public List<Enquesta> getEnquestesCreades() {
        return new ArrayList<>(enquestesCreades);
    }

    // Mètodes estàtics per a gestió de sessió
    /**
     * Inicia la sessió d'un usuari, establint-lo com l'usuari actual del sistema.
     *
     * @param usuari L'usuari que inicia sessió.
     */
    public static void login(Usuari usuari) {
        usuariActual = usuari;
    }

    /**
     * Retorna l'usuari que té la sessió iniciada actualment.
     *
     * @return L'objecte {@link Usuari} actual, o {@code null} si no hi ha cap
     *         sessió activa.
     */
    public static Usuari getUsuariActual() {
        return usuariActual;
    }

    /**
     * Tanca la sessió de l'usuari actual, establint l'usuari actual a {@code null}.
     */
    public static void logout() {
        usuariActual = null;
    }
}
