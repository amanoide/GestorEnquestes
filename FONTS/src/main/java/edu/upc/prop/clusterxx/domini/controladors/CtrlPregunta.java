package edu.upc.prop.clusterxx.domini.controladors;


import edu.upc.prop.clusterxx.domini.classes.Opcio;
import edu.upc.prop.clusterxx.domini.classes.Pregunta;

/**
 * Controlador de preguntes que delega totes les operacions de dades a CtrlPersistencia.
 * No manté dades pròpies, només coordina la lògica de negoci.
 */
public class CtrlPregunta {
    private CtrlPersistencia persistencia;

    public CtrlPregunta() {
        this.persistencia = CtrlPersistencia.getInstance();
    }

    public void modificarPregunta(String idPregunta, Pregunta nova) {
        if (persistencia.getPregunta(idPregunta) != null) {
            persistencia.afegirPregunta(idPregunta, nova);
        }
    }

    public void afegirOpcio(String idPregunta, Opcio o) {
        Pregunta p = persistencia.getPregunta(idPregunta);
        if (p != null) {
            p.afegirOpcio(o);
        }
    }

    public void eliminarOpcio(String idPregunta, int idOpcio) {
        Pregunta p = persistencia.getPregunta(idPregunta);
        if (p != null) {
            p.eliminarOpcio(idOpcio);
        }
    }
}
