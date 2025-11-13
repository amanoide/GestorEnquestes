package edu.upc.prop.clusterxx.domini.controladors;

import java.util.HashMap;

import edu.upc.prop.clusterxx.domini.classes.Pregunta;
import edu.upc.prop.clusterxx.domini.classes.Resposta;
import edu.upc.prop.clusterxx.domini.classes.Usuari;

/**
 * Controlador de respostes que delega totes les operacions de dades a CtrlPersistencia.
 * No manté dades pròpies, només coordina la lògica de negoci.
 */
public class CtrlResposta {
    private CtrlPersistencia persistencia;

    public CtrlResposta() {
        this.persistencia = CtrlPersistencia.getInstance();
    }
    
    /**
     * Registra una resposta individual.
     * @param idResposta ID de la resposta
     * @param idPregunta ID de la pregunta
     * @param textResposta Text de la resposta
     * @param usuari Usuari que respon
     * @param pregunta Pregunta a la qual es respon
     */
    public void registrarResposta(String idResposta, String idPregunta, String textResposta, Usuari usuari, Pregunta pregunta) {
        Resposta resposta = new Resposta(idResposta, idPregunta, textResposta, usuari);
        persistencia.afegirResposta(idResposta, resposta, pregunta);
    }

    /**
     * Modifica una resposta específica d'un usuari.
     * @param usuari L'usuari que modifica.
     * @param pregunta La pregunta on està la resposta.
     * @param novaResposta El nou text de la resposta.
     * @return 0 si s'ha modificat correctament, 1 si no existia la resposta, 2 si no té permisos.
     */
    //(jairo)
    public int modificarResposta(Usuari usuari, Pregunta pregunta, String novaResposta) {
        String username = usuari.getUsername();
        
        // Buscar la resposta en la pregunta
        Resposta resposta = pregunta.getResposta(username);
        
        if (resposta != null) {
            // Verificar que el usuario que intenta modificar es el propietario
            if (!resposta.getUsernameUsuari().equals(username)) {
                return 2; // No tiene permisos
            }
            
            resposta.modificarResposta(novaResposta);
            return 0; // Modificada correctamente
        }
        
        return 1; // No existe la resposta
    }

    /**
     * Esborra una resposta específica d'un usuari.
     * @param usuari L'usuari que esborra.
     * @param pregunta La pregunta on està la resposta.
     * @return 0 si s'ha esborrat correctament, 1 si no existia la resposta, 2 si no té permisos.
     */
    //(jairo)
    public int esborrarResposta(Usuari usuari, Pregunta pregunta) {
        String username = usuari.getUsername();
        
        // Buscar la resposta en la pregunta
        Resposta resposta = pregunta.getResposta(username);
        
        if (resposta != null) {
            // Verificar que el usuario que intenta esborrar es el propietario
            if (!resposta.getUsernameUsuari().equals(username)) {
                return 2; // No tiene permisos
            }
            
            // Eliminar de persistència i de la pregunta
            persistencia.eliminarResposta(resposta.getId());
            pregunta.eliminarResposta(username);
            return 0; // Esborrada correctamente
        }
        
        return 1; // No existe la resposta
    }

    /**
     * Obté la resposta d'un usuari a una pregunta específica.
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
     * @return HashMap complet amb totes les respostes del sistema (idResposta -> Resposta)
     */
    //(jairo)
    public HashMap<String, Resposta> getTotesRespostes() {
        return persistencia.getAllRespostes();
    }

    /**
     * Estableix tota l'estructura de respostes des de persistència.
     * @param respostes HashMap complet amb totes les respostes a carregar (idResposta -> Resposta)
     */
    //(jairo)
    public void setTotesRespostes(HashMap<String, Resposta> respostes) {
        if (respostes != null) {
            persistencia.saveRespostes(respostes);
        }
    }

    /**
     * Neteja totes les respostes (útil per a reinicialització).
     */
    //(jairo)
    public void netejarRespostes() {
        persistencia.saveRespostes(new HashMap<>());
    }

    /**
     * Obté el nombre total de respostes.
     * @return Nombre total de respostes al sistema
     */
    //(jairo)
    public int getNumRespostes() {
        return persistencia.getAllRespostes().size();
    }
}
