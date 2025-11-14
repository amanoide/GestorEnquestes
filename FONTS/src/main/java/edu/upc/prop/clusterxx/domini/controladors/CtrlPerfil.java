package edu.upc.prop.clusterxx.domini.controladors;

import java.util.ArrayList;

import edu.upc.prop.clusterxx.domini.classes.Perfil;

/**
 * Controlador de perfils que delega totes les operacions de dades a CtrlPersistencia.
 * No manté dades pròpies, només coordina la lògica de negoci.
 */
public class CtrlPerfil {
    private CtrlPersistencia persistencia;

    public CtrlPerfil() {
        this.persistencia = CtrlPersistencia.getInstance();
    }

    public void crearPerfil(String id, String descripcio) {
        Perfil nouPerfil = new Perfil(Integer.parseInt(id), descripcio);
        persistencia.afegirPerfil(id, nouPerfil);
    }

    public Perfil getPerfil(String id) {
        return persistencia.getPerfil(id);
    }

    public void modificarPerfil(String id, String novaDescripcio) {
        Perfil p = persistencia.getPerfil(id);
        if (p != null) {
            // Crear un nou perfil amb la nova descripció
            Perfil nouPerfil = new Perfil(Integer.parseInt(id), novaDescripcio);
            persistencia.afegirPerfil(id, nouPerfil);
        }
    }

    public void eliminarPerfil(String id) {
        persistencia.eliminarPerfil(id);
    }

    public ArrayList<Perfil> llistarPerfils() {
        return new ArrayList<>(persistencia.getAllPerfils().values());
    }
}
