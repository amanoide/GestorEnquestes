package edu.upc.prop.clusterxx.domini.controladors;

import java.util.ArrayList;

import edu.upc.prop.clusterxx.domini.classes.Enquesta;
import edu.upc.prop.clusterxx.domini.classes.Opcio;
import edu.upc.prop.clusterxx.domini.classes.Pregunta;
import edu.upc.prop.clusterxx.domini.classes.Usuari;
import edu.upc.prop.clusterxx.persistencia.CtrlPersistencia;

/**
 * Controlador d'enquestes que delega totes les operacions de dades a
 * CtrlPersistencia.
 * No manté dades pròpies, només coordina la lògica de negoci.
 */
public class CtrlEnquesta {
    private CtrlPersistencia persistencia;

    public CtrlEnquesta() {
        this.persistencia = CtrlPersistencia.getInstance();
    }

    // Mètodes de gestió d'enquestes
    /**
     * Crea una nova enquesta i la registra al sistema.
     * 
     * L'enquesta es guarda a la capa de persistència i s'afegeix automàticament
     * a la llista d'enquestes creades per l'usuari.
     * 
     * @param id         Identificador únic de l'enquesta
     * @param titol      Títol de l'enquesta
     * @param descripcio Descripció de l'enquesta
     * @param creador    Usuari que crea l'enquesta
     */
    public void crearEnquesta(String id, String titol, String descripcio, Usuari creador) {
        // Crear la nova enquesta
        Enquesta novaEnquesta = new Enquesta(id, titol, descripcio, creador);

        // Guardar l'enquesta a la capa de persistència
        persistencia.afegirEnquesta(novaEnquesta);

        // Registrar l'enquesta al perfil de l'usuari creador
        creador.addEnquestaCreada(novaEnquesta);
    }

    /**
     * Modifica el títol d'una enquesta existent.
     * 
     * @param id       Identificador de l'enquesta
     * @param nouTitol Nou títol a assignar
     */
    public void modificarTitolEnquesta(String id, String nouTitol) {
        Enquesta e = persistencia.getEnquesta(id);
        if (e != null) {
            e.setTitol(nouTitol);
            persistencia.guardarEnquesta(e);
        }
    }

    /**
     * Modifica la descripció d'una enquesta existent.
     * 
     * @param id             Identificador de l'enquesta
     * @param novaDescripcio Nova descripció a assignar
     */
    public void modificarDescripcioEnquesta(String id, String novaDescripcio) {
        Enquesta e = persistencia.getEnquesta(id);
        if (e != null) {
            e.setDescripcio(novaDescripcio);
            persistencia.guardarEnquesta(e);
        }
    }

    /**
     * Elimina una enquesta del sistema de forma permanent.
     * 
     * @param id Identificador de l'enquesta a eliminar
     */
    public void eliminarEnquesta(String id) {
        persistencia.eliminarEnquesta(id);
    }

    // Mètodes de gestió de preguntes
    /**
     * Afegeix una nova pregunta a una enquesta existent.
     * 
     * La pregunta es registra tant a l'enquesta com a la persistència global.
     * 
     * @param idEnquesta Identificador de l'enquesta
     * @param pregunta   Pregunta a afegir
     */
    public void afegirPregunta(String idEnquesta, Pregunta pregunta) {
        Enquesta e = persistencia.getEnquesta(idEnquesta);
        if (e != null) {
            e.afegirPregunta(pregunta);
            // Guardar l'enquesta actualitzada
            persistencia.guardarEnquesta(e);
        }
    }

    /**
     * Elimina una pregunta d'una enquesta.
     * 
     * La pregunta s'elimina tant de l'enquesta com de la persistència global.
     * 
     * @param idEnquesta Identificador de l'enquesta
     * @param idPregunta Identificador de la pregunta a eliminar
     */
    public void eliminarPregunta(String idEnquesta, String idPregunta) {
        Enquesta e = persistencia.getEnquesta(idEnquesta);
        if (e != null) {
            e.eliminarPregunta(idPregunta);
            // També eliminar la pregunta de persistència global
            persistencia.eliminarPregunta(idEnquesta, idPregunta);
        }
    }

    /**
     * Modifica una pregunta existent d'una enquesta.
     * 
     * @param idEnquesta Identificador de l'enquesta
     * @param idPregunta Identificador de la pregunta a modificar
     * @param nova       Nova pregunta amb les dades actualitzades
     */
    public void modificarPregunta(String idEnquesta, String idPregunta, Pregunta nova) {
        Enquesta e = persistencia.getEnquesta(idEnquesta);
        if (e != null) {
            e.modificarPregunta(idPregunta, nova);
            // Guardar els canvis a la persistència
            persistencia.guardarEnquesta(e);
        }
    }

    /**
     * Afegeix una opció a una pregunta qualitativa d'una enquesta.
     * 
     * @param idEnquesta Identificador de l'enquesta
     * @param idPregunta Identificador de la pregunta
     * @param o          Opció a afegir
     */
    public void afegirOpcioAPregunta(String idEnquesta, String idPregunta, Opcio o) {
        Enquesta e = persistencia.getEnquesta(idEnquesta);
        if (e != null) {
            Pregunta p = e.getPregunta(idPregunta);
            if (p != null) {
                p.afegirOpcio(o);
            }
        }
    }

    /**
     * Elimina una opció d'una pregunta qualitativa d'una enquesta.
     * 
     * @param idEnquesta Identificador de l'enquesta
     * @param idPregunta Identificador de la pregunta
     * @param idOpcio    Identificador de l'opció a eliminar
     */
    public void eliminarOpcioDepregunta(String idEnquesta, String idPregunta, int idOpcio) {
        Enquesta e = persistencia.getEnquesta(idEnquesta);
        if (e != null) {
            Pregunta p = e.getPregunta(idPregunta);
            if (p != null) {
                p.eliminarOpcio(idOpcio);
            }
        }
    }

    /**
     * Elimina un usuari de la llista de participants d'una enquesta.
     * 
     * @param enquesta Enquesta de la qual eliminar la participació
     * @param username Nom d'usuari del participant
     */
    public void eliminarParticipacio(Enquesta enquesta, String username) {
        enquesta.eliminarParticipacio(username);
    }

    /**
     * Obté una pregunta específica d'una enquesta.
     * 
     * @param idEnquesta Identificador de l'enquesta
     * @param idPregunta Identificador de la pregunta
     * @return La pregunta si existeix, null altrament
     */
    public Pregunta getPregunta(String idEnquesta, String idPregunta) {
        Enquesta e = persistencia.getEnquesta(idEnquesta);
        if (e != null) {
            return e.getPregunta(idPregunta);
        }
        return null;
    }

    /**
     * Obté l'identificador del creador d'una enquesta.
     * 
     * @param idEnquesta Identificador de l'enquesta
     * @return L'identificador del creador si l'enquesta existeix, null altrament
     */
    public String getIdCreador(String idEnquesta) {
        Enquesta e = persistencia.getEnquesta(idEnquesta);
        if (e != null) {
            return e.getIdCreador();
        }
        return null;
    }

    // Getters
    /**
     * Obté una enquesta per identificador.
     * 
     * @param id Identificador de l'enquesta
     * @return L'enquesta si existeix, null altrament
     */
    public Enquesta getEnquesta(String id) {
        return persistencia.getEnquesta(id);
    }

    /**
     * Obté la llista de totes les enquestes del sistema.
     * 
     * @return ArrayList amb totes les enquestes registrades
     */
    public ArrayList<Enquesta> llistarEnquestes() {
        return persistencia.getAllEnquestes();
    }

    /**
     * Registra la participació d'un usuari en una enquesta.
     * 
     * Afegeix l'usuari a la llista de participants de l'enquesta.
     * 
     * @param idEnquesta Identificador de l'enquesta
     * @param username   Nom d'usuari del participant
     */
    public void registrarParticipacio(String idEnquesta, String username) {
        Enquesta e = persistencia.getEnquesta(idEnquesta);
        if (e != null) {
            e.registrarParticipacio(username);
        }
    }

    /**
     * Verifica si un usuari ha respost una enquesta.
     * 
     * @param idEnquesta Identificador de l'enquesta
     * @param username   Nom d'usuari a verificar
     * @return true si l'usuari ha contestat l'enquesta, false altrament
     */
    public boolean haRespostUsuari(String idEnquesta, String username) {
        Enquesta e = persistencia.getEnquesta(idEnquesta);
        return e != null && e.haRespostUsuari(username);
    }

    /**
     * Obté el nombre total d'enquestes registrades al sistema.
     * 
     * @return Nombre d'enquestes existents
     */
    public int getNumEnquestes() {
        return persistencia.getNumEnquestes();
    }

}
