package edu.upc.prop.clusterxx.domini.controladors;

import java.util.HashMap;

import edu.upc.prop.clusterxx.domini.classes.Pregunta;
import edu.upc.prop.clusterxx.domini.classes.Resposta;
import edu.upc.prop.clusterxx.domini.classes.Usuari;
import edu.upc.prop.clusterxx.persistencia.CtrlPersistencia;

/**
 * Controlador de respostes que delega totes les operacions de dades a
 * CtrlPersistencia.
 * No manté dades pròpies, només coordina la lògica de negoci.
 */
public class CtrlResposta {
    private CtrlPersistencia persistencia;

    public CtrlResposta() {
        this.persistencia = CtrlPersistencia.getInstance();
    }

    /**
     * Registra una resposta individual.
     * Genera automàticament l'ID de la resposta com: idPregunta + "_" + username
     * 
     * @param idEnquesta   ID de l'enquesta
     * @param idPregunta   ID de la pregunta
     * @param textResposta Text de la resposta
     * @param usuari       Usuari que respon
     */
    public void registrarResposta(String idEnquesta, String idPregunta, String textResposta, Usuari usuari) {
        // Generar ID únic per la resposta
        String idResposta = idPregunta + "_" + usuari.getUsername();

        Resposta resposta = new Resposta(idResposta, idPregunta, textResposta, usuari);
        // Utilitzar el mètode que accepta l'enquesta explícita per evitar ambigüitats
        persistencia.afegirResposta(resposta, idEnquesta);
    }

    /**
     * Modifica una resposta específica d'un usuari.
     * 
     * @param usuari       L'usuari que modifica.
     * @param pregunta     La pregunta on està la resposta.
     * @param novaResposta El nou text de la resposta.
     * @return 0 si s'ha modificat correctament, 1 si no existia la resposta, 2 si
     *         no té permisos.
     */

    public void modificarResposta(Resposta resposta, String novaResposta) {
        resposta.modificarResposta(novaResposta);
        persistencia.actualitzarResposta(resposta);
    }

    /**
     * Esborra una resposta específica.
     * Delega les operacions d'eliminació de persistència i de la pregunta.
     * 
     * @param resposta La resposta a esborrar.
     * @param pregunta La pregunta on està la resposta.
     */

    public void esborrarResposta(Resposta resposta, Pregunta pregunta) {
        // Eliminar de persistència
        persistencia.eliminarResposta(resposta.getId());

        // Eliminar de la pregunta
        pregunta.eliminarResposta(resposta.getUsernameUsuari());
    }

    /**
     * Obté la resposta d'un usuari a una pregunta específica.
     * 
     * @param pregunta La pregunta.
     * @param username El nom d'usuari.
     * @return La resposta de l'usuari o null si no existeix.
     */
    public Resposta getRespostaUsuari(Pregunta pregunta, String username) {
        return pregunta.getResposta(username);
    }

    // ===========================================
    // Mètodes per a persistència (delegació)
    // ===========================================

    /**
     * Obté tota l'estructura de respostes per a persistència.
     * 
     * @return HashMap complet amb totes les respostes del sistema (idResposta ->
     *         Resposta)
     */

    public HashMap<String, Resposta> getTotesRespostes() {
        return persistencia.getAllRespostes();
    }

    /**
     * Obté el nombre total de respostes.
     * 
     * @return Nombre total de respostes al sistema
     */

    public int getNumRespostes() {
        return persistencia.getAllRespostes().size();
    }
}
