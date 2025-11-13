package edu.upc.prop.clusterxx.domini.controladors;

import java.util.ArrayList;

import edu.upc.prop.clusterxx.domini.classes.Enquesta;
import edu.upc.prop.clusterxx.domini.classes.Opcio;
import edu.upc.prop.clusterxx.domini.classes.Pregunta;
import edu.upc.prop.clusterxx.domini.classes.Usuari;

/**
 * Controlador d'enquestes que delega totes les operacions de dades a CtrlPersistencia.
 * No manté dades pròpies, només coordina la lògica de negoci.
 */
public class CtrlEnquesta {
    private CtrlPersistencia persistencia;

    public CtrlEnquesta() {
        this.persistencia = CtrlPersistencia.getInstance();
    }

    // Mètodes de gestió d'enquestes
    /**
     * Crea una nova enquesta i la registra tant a la persistència com al perfil del creador.
     * @param id L'identificador de l'enquesta
     * @param titol El títol de l'enquesta
     * @param descripcio La descripció de l'enquesta
     * @param creador L'usuari creador de l'enquesta
     */
    public void crearEnquesta(String id, String titol, String descripcio, Usuari creador) {
        // Crear la nova enquesta
        Enquesta novaEnquesta = new Enquesta(id, titol, descripcio, creador);
        
        // Guardar l'enquesta a la capa de persistència
        persistencia.afegirEnquesta(novaEnquesta);
        
        // Registrar l'enquesta al perfil de l'usuari creador
        creador.addEnquestaCreada(novaEnquesta);
    }

    public void modificarTitolEnquesta(String id, String nouTitol) {
        Enquesta e = persistencia.getEnquesta(id);
        if (e != null) {
            e.modificarTitol(nouTitol);
        }
    }

    public void modificarDescripcioEnquesta(String id, String novaDescripcio) {
        Enquesta e = persistencia.getEnquesta(id);
        if (e != null) {
            e.modificarDescripcio(novaDescripcio);
        }
    }

    public void eliminarEnquesta(String id) {
        persistencia.eliminarEnquesta(id);
    }

    // Mètodes de gestió de preguntes
    public void afegirPregunta(String idEnquesta, Pregunta pregunta) {
        Enquesta e = persistencia.getEnquesta(idEnquesta);
        if (e != null) {
            e.afegirPregunta(pregunta);
            // També afegir la pregunta a persistència global
            persistencia.afegirPregunta(pregunta.getId(), pregunta);
        }
    }

    public void eliminarPregunta(String idEnquesta, String idPregunta) {
        Enquesta e = persistencia.getEnquesta(idEnquesta);
        if (e != null) {
            e.eliminarPregunta(idPregunta);
            // També eliminar la pregunta de persistència global
            persistencia.eliminarPregunta(idPregunta);
        }
    }

    public void modificarPregunta(String idEnquesta, String idPregunta, Pregunta nova) {
        Enquesta e = persistencia.getEnquesta(idEnquesta);
        if (e != null) {
            e.modificarPregunta(idPregunta, nova);
        }
    }

    public void afegirOpcioAPregunta(String idEnquesta, String idPregunta, Opcio o) {
        Enquesta e = persistencia.getEnquesta(idEnquesta);
        if (e != null) {
            Pregunta p = e.getPregunta(idPregunta);
            if (p != null) {
                p.afegirOpcio(o);
            }
        }
    }

    public void eliminarOpcioDepregunta(String idEnquesta, String idPregunta, int idOpcio) {
        Enquesta e = persistencia.getEnquesta(idEnquesta);
        if (e != null) {
            Pregunta p = e.getPregunta(idPregunta);
            if (p != null) {
                p.eliminarOpcio(idOpcio);
            }
        }
    }

    public Pregunta getPregunta(String idEnquesta, String idPregunta) {
        Enquesta e = persistencia.getEnquesta(idEnquesta);
        if (e != null) {
            return e.getPregunta(idPregunta);
        }
        return null;
    }

    public String getIdCreador(String idEnquesta) {
        Enquesta e = persistencia.getEnquesta(idEnquesta);
        if (e != null) {
            return e.getIdCreador();
        }
        return null;
    }

    // Getters
    public Enquesta getEnquesta(String id) {
        return persistencia.getEnquesta(id);
    }

    public ArrayList<Enquesta> llistarEnquestes() {
        return persistencia.getAllEnquestes();
    }

    public void registrarParticipacio(String idEnquesta, String username) {
        Enquesta e = persistencia.getEnquesta(idEnquesta);
        if (e != null) {
            e.registrarParticipacio(username);
        }
    }

    public boolean haRespostUsuari(String idEnquesta, String username) {
        Enquesta e = persistencia.getEnquesta(idEnquesta);
        return e != null && e.haRespostUsuari(username);
    }

    // ===========================================
    // Mètodes per a persistència (delegació)
    // ===========================================

    /**
     * Obté totes les enquestes per a persistència.
     * @return ArrayList amb totes les enquestes
     */
    public ArrayList<Enquesta> getTotesEnquestes() {
        return persistencia.getAllEnquestes();
    }

    /**
     * Estableix totes les enquestes des de persistència.
     * @param enquestes ArrayList amb les enquestes a carregar
     */
    public void setTotesEnquestes(ArrayList<Enquesta> enquestes) {
        if (enquestes != null) {
            persistencia.saveEnquestes(enquestes);
        }
    }

    /**
     * Neteja totes les enquestes (útil per a reinicialització).
     */
    public void netejarEnquestes() {
        // Neteja via persistencia creant una llista buida
        persistencia.saveEnquestes(new ArrayList<>());
    }

    /**
     * Obté el nombre total d'enquestes.
     * @return Nombre d'enquestes en el sistema
     */
    public int getNumEnquestes() {
        return persistencia.getNumEnquestes();
    }

    /**
     * Obté les enquestes d'un creador específic.
     * @param username El nom d'usuari del creador
     * @return ArrayList amb les enquestes creades per l'usuari
     */
    public ArrayList<Enquesta> getEnquestesPerCreador(String username) {
        ArrayList<Enquesta> resultat = new ArrayList<>();
        for (Enquesta e : persistencia.getAllEnquestes()) {
            if (e.getIdCreador().equals(username)) {
                resultat.add(e);
            }
        }
        return resultat;
    }
}
