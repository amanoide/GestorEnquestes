package edu.upc.prop.clusterxx.domini.controladors;

import java.util.ArrayList;

import edu.upc.prop.clusterxx.domini.classes.Perfil;
import edu.upc.prop.clusterxx.persistencia.CtrlPersistencia;

/**
 * Controlador de perfils que delega totes les operacions de dades a
 * CtrlPersistencia.
 * No manté dades pròpies, només coordina la lògica de negoci.
 */
public class CtrlPerfil {
    private CtrlPersistencia persistencia;

    /**
     * Constructor que inicialitza el controlador de perfils.
     * Obté la instància de CtrlPersistencia per gestionar les dades.
     */
    public CtrlPerfil() {
        this.persistencia = CtrlPersistencia.getInstance();
    }

    /**
     * Crea un nou perfil i l'afegeix al sistema.
     * 
     * @param id         Identificador únic del perfil (numèric en format String)
     * @param descripcio Descripció del perfil
     */
    public void crearPerfil(String id, String descripcio) {
        Perfil nouPerfil = new Perfil(Integer.parseInt(id), descripcio);
        persistencia.afegirPerfil(nouPerfil);
    }

    /**
     * Obté un perfil específic pel seu identificador.
     * 
     * @param id Identificador del perfil
     * @return El perfil trobat o null si no existeix
     */
    public Perfil getPerfil(String id) {
        return persistencia.getPerfil(id);
    }

    /**
     * Modifica la descripció d'un perfil existent.
     * Crea un nou perfil amb la nova descripció i substitueix l'anterior.
     * 
     * @param id             Identificador del perfil a modificar
     * @param novaDescripcio Nova descripció del perfil
     */
    public void modificarPerfil(String id, String novaDescripcio) {
        Perfil p = persistencia.getPerfil(id);
        if (p != null) {
            // Crear un nou perfil amb la nova descripció
            Perfil nouPerfil = new Perfil(Integer.parseInt(id), novaDescripcio);
            persistencia.afegirPerfil(nouPerfil);
        }
    }

    /**
     * Elimina un perfil del sistema.
     * 
     * @param id Identificador del perfil a eliminar
     */
    public void eliminarPerfil(String id) {
        persistencia.eliminarPerfil(id);
    }

    /**
     * Obté la llista de tots els perfils del sistema.
     * 
     * @return ArrayList amb tots els perfils registrats
     */
    public ArrayList<Perfil> llistarPerfils() {
        return new ArrayList<>(persistencia.getAllPerfils().values());
    }
}
