package edu.upc.prop.clusterxx.domini.classes;

/**
 * Excepció llançada quan un usuari intenta contestar una enquesta que ja ha contestat.
 */
public class EnquestaJaContestadaException extends Exception {
    public EnquestaJaContestadaException(String idEnquesta, String username) {
        super("L'usuari '" + username + "' ja ha contestat l'enquesta amb ID " + idEnquesta + ".");
    }
}
