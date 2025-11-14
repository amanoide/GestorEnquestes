package edu.upc.prop.clusterxx.domini.classes;

/**
 * Fitxer que conté totes les excepcions personalitzades del domini.
 * Aquesta classe no és instanciable.
 */
public final class Exceptions {
    
    // Constructor privat per evitar instanciació
    private Exceptions() {
        throw new AssertionError("No es pot instanciar aquesta classe");
    }

    /**
     * Excepció llançada quan un paràmetre és invàlid (null, buit, etc.).
     */
    public static class ParametreInvalidException extends Exception {
        public ParametreInvalidException(String missatge) {
            super(missatge);
        }
    }

    /**
     * Excepció llançada quan un usuari ja existeix al sistema.
     */
    public static class UsuariJaExisteixException extends Exception {
        public UsuariJaExisteixException(String username) {
            super("L'usuari amb nom '" + username + "' ja existeix.");
        }
    }

    /**
     * Excepció llançada quan les credencials d'un usuari són incorrectes.
     */
    public static class CredencialsIncorrectesException extends Exception {
        public CredencialsIncorrectesException() {
            super("Nom d'usuari o contrasenya incorrectes.");
        }
    }

    /**
     * Excepció llançada quan un usuari no està autenticat.
     */
    public static class UsuariNoAutenticatException extends Exception {
        public UsuariNoAutenticatException() {
            super("Cal estar autenticat per realitzar aquesta acció.");
        }
        
        public UsuariNoAutenticatException(String missatge) {
            super(missatge);
        }
    }

    /**
     * Excepció llançada quan es vol crear una enquesta amb un ID que ja existeix.
     */
    public static class EnquestaJaExisteixException extends Exception {
        public EnquestaJaExisteixException(String id) {
            super("Ja existeix una enquesta amb l'ID '" + id + "'.");
        }
    }

    /**
     * Excepció llançada quan no s'ha trobat una enquesta amb l'ID especificat.
     */
    public static class EnquestaNoExisteixException extends Exception {
        public EnquestaNoExisteixException(String id) {
            super("No existeix cap enquesta amb l'ID '" + id + "'.");
        }
    }

    /**
     * Excepció llançada quan un usuari intenta contestar una enquesta que ja ha contestat.
     */
    public static class EnquestaJaContestadaException extends Exception {
        public EnquestaJaContestadaException(String idEnquesta, String username) {
            super("L'usuari '" + username + "' ja ha contestat l'enquesta amb ID " + idEnquesta + ".");
        }
    }

    /**
     * Excepció llançada quan no s'ha trobat una pregunta.
     */
    public static class PreguntaNoExisteixException extends Exception {
        public PreguntaNoExisteixException(String missatge) {
            super(missatge);
        }
    }

    /**
     * Excepció llançada quan es vol afegir una pregunta que ja existeix.
     */
    public static class PreguntaJaExisteixException extends Exception {
        public PreguntaJaExisteixException(String idPregunta, String idEnquesta) {
            super("Ja existeix una pregunta amb l'ID '" + idPregunta + "' a l'enquesta '" + idEnquesta + "'.");
        }
    }

    /**
     * Excepció llançada quan una resposta no és vàlida.
     */
    public static class RespostaInvalidaException extends Exception {
        public RespostaInvalidaException(String missatge) {
            super(missatge);
        }
    }

    /**
     * Excepció llançada quan no s'ha trobat una resposta.
     */
    public static class RespostaNoExisteixException extends Exception {
        public RespostaNoExisteixException(String missatge) {
            super(missatge);
        }
    }

    /**
     * Excepció llançada quan hi ha errors en la importació de fitxers.
     */
    public static class ErrorImportacioException extends Exception {
        public ErrorImportacioException(String missatge) {
            super("Error en la importació: " + missatge);
        }
    }

    /**
     * Excepció llançada quan un usuari no té permisos per realitzar una operació.
     */
    public static class PermisDenegatException extends Exception {
        public PermisDenegatException(String missatge) {
            super(missatge);
        }
    }
}
