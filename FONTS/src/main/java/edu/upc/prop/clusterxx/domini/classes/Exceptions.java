package edu.upc.prop.clusterxx.domini.classes;

/**
 * Contenidor per a totes les excepcions personalitzades de la capa de domini.
 * Aquesta classe agrupa, mitjançant classes internes estàtiques, totes les excepcions
 * específiques de l'aplicació per a una millor organització i gestió d'errors.
 * No es pot instanciar.
 */
public final class Exceptions {
    
    /**
     * Constructor privat per evitar la instanciació d'aquesta classe d'utilitat.
     */
    private Exceptions() {
        throw new AssertionError("No es pot instanciar aquesta classe");
    }

    /**
     * Excepció llançada quan un paràmetre d'un mètode no compleix les condicions
     * requerides (p. ex., és null, buit o fora de rang).
     */
    public static class ParametreInvalidException extends Exception {
        /**
         * Construeix una nova excepció amb un missatge detallat.
         * @param missatge El missatge que descriu l'error.
         */
        public ParametreInvalidException(String missatge) {
            super(missatge);
        }
    }

    /**
     * Excepció llançada en intentar registrar un usuari amb un nom que ja existeix al sistema.
     */
    public static class UsuariJaExisteixException extends Exception {
        /**
         * Construeix una nova excepció per a un usuari existent.
         * @param username El nom de l'usuari que ja existeix.
         */
        public UsuariJaExisteixException(String username) {
            super("L'usuari amb nom '" + username + "' ja existeix.");
        }
    }

    /**
     * Excepció llançada quan les credencials proporcionades (usuari/contrasenya) no són correctes.
     */
    public static class CredencialsIncorrectesException extends Exception {
        /**
         * Construeix una nova excepció amb un missatge genèric d'error de credencials.
         */
        public CredencialsIncorrectesException() {
            super("Nom d'usuari o contrasenya incorrectes.");
        }
    }

    /**
     * Excepció llançada quan es realitza una operació que requereix autenticació prèvia
     * i l'usuari no ha iniciat sessió.
     */
    public static class UsuariNoAutenticatException extends Exception {
        /**
         * Construeix una nova excepció amb un missatge per defecte.
         */
        public UsuariNoAutenticatException() {
            super("Cal estar autenticat per realitzar aquesta acció.");
        }
        
        /**
         * Construeix una nova excepció amb un missatge específic.
         * @param missatge El missatge que descriu l'error.
         */
        public UsuariNoAutenticatException(String missatge) {
            super(missatge);
        }
    }

    /**
     * Excepció llançada en intentar crear una enquesta amb un ID que ja està en ús.
     */
    public static class EnquestaJaExisteixException extends Exception {
        /**
         * Construeix una nova excepció per a una enquesta existent.
         * @param id L'identificador de l'enquesta que ja existeix.
         */
        public EnquestaJaExisteixException(String id) {
            super("Ja existeix una enquesta amb l'ID '" + id + "'.");
        }
    }

    /**
     * Excepció llançada quan s'intenta accedir a una enquesta amb un ID que no existeix.
     */
    public static class EnquestaNoExisteixException extends Exception {
        /**
         * Construeix una nova excepció per a una enquesta no trobada.
         * @param id L'identificador de l'enquesta que no s'ha trobat.
         */
        public EnquestaNoExisteixException(String id) {
            super("No existeix cap enquesta amb l'ID '" + id + "'.");
        }
    }

    /**
     * Excepció llançada quan un usuari intenta contestar una enquesta que ja ha respost prèviament.
     */
    public static class EnquestaJaContestadaException extends Exception {
        /**
         * Construeix una nova excepció per a una enquesta ja contestada.
         * @param idEnquesta L'ID de l'enquesta.
         * @param username El nom de l'usuari que ja l'ha contestada.
         */
        public EnquestaJaContestadaException(String idEnquesta, String username) {
            super("L'usuari '" + username + "' ja ha contestat l'enquesta amb ID " + idEnquesta + ".");
        }
    }

    /**
     * Excepció llançada quan s'intenta accedir a una pregunta que no existeix.
     */
    public static class PreguntaNoExisteixException extends Exception {
        /**
         * Construeix una nova excepció amb un missatge detallat.
         * @param missatge El missatge que descriu per què la pregunta no existeix.
         */
        public PreguntaNoExisteixException(String missatge) {
            super(missatge);
        }
    }

    /**
     * Excepció llançada en intentar afegir una pregunta a una enquesta on ja n'hi ha una amb el mateix ID.
     */
    public static class PreguntaJaExisteixException extends Exception {
        /**
         * Construeix una nova excepció per a una pregunta duplicada.
         * @param idPregunta L'ID de la pregunta que es vol afegir.
         * @param idEnquesta L'ID de l'enquesta on es vol afegir.
         */
        public PreguntaJaExisteixException(String idPregunta, String idEnquesta) {
            super("Ja existeix una pregunta amb l'ID '" + idPregunta + "' a l'enquesta '" + idEnquesta + "'.");
        }
    }

    /**
     * Excepció llançada quan el format o contingut d'una resposta no és vàlid per a la pregunta corresponent.
     */
    public static class RespostaInvalidaException extends Exception {
        /**
         * Construeix una nova excepció amb un missatge que descriu la invalidesa.
         * @param missatge El missatge d'error.
         */
        public RespostaInvalidaException(String missatge) {
            super(missatge);
        }
    }

    /**
     * Excepció llançada quan s'intenta accedir a una resposta que no existeix.
     */
    public static class RespostaNoExisteixException extends Exception {
        /**
         * Construeix una nova excepció amb un missatge detallat.
         * @param missatge El missatge que descriu l'error.
         */
        public RespostaNoExisteixException(String missatge) {
            super(missatge);
        }
    }

    /**
     * Excepció llançada quan ocorre un error durant la importació de dades des d'un fitxer (p. ex., JSON mal format).
     */
    public static class ErrorImportacioException extends Exception {
        /**
         * Construeix una nova excepció amb un missatge sobre l'error d'importació.
         * @param missatge El missatge que detalla el problema.
         */
        public ErrorImportacioException(String missatge) {
            super("Error en la importació: " + missatge);
        }
    }

    /**
     * Excepció llançada quan un usuari intenta realitzar una operació per a la qual no té els permisos necessaris.
     */
    public static class PermisDenegatException extends Exception {
        /**
         * Construeix una nova excepció amb un missatge de denegació de permís.
         * @param missatge El missatge que explica per què s'ha denegat el permís.
         */
        public PermisDenegatException(String missatge) {
            super(missatge);
        }
    }

    /**
     * Excepció llançada quan no es troba cap perfil per a l'usuari.
     */
    public static class PerfilNoTrobatException extends Exception {
        /**
         * Construeix una nova excepció amb un missatge detallat.
         * @param missatge El missatge que descriu per què no s'ha trobat el perfil.
         */
        public PerfilNoTrobatException(String missatge) {
            super(missatge);
        }
    }

    /**
     * Excepció llançada quan es consulta una anàlisi que no existeix.
     */
    public static class AnalisiNoRealitzatException extends Exception {
        /**
         * Construeix una nova excepció per a una anàlisi no realitzada.
         * @param idEnquesta L'ID de l'enquesta per a la qual no s'ha realitzat l'anàlisi.
         */
        public AnalisiNoRealitzatException(String idEnquesta) {
            super("No s'ha realitzat cap anàlisi de clustering per a l'enquesta: " + idEnquesta);
        }
    }
}
