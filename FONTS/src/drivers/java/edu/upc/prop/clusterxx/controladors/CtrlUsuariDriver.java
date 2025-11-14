/**
 * Driver per a la classe CtrlUsuari.
 * Permet provar els mètodes relacionats amb la gestió d'usuaris,
 * tant a nivell de sessió estàtica com a nivell de persistència.
 * 
 * @autor Joan Garvin Cardona
 * 
 * IMPORTANT:
 * ESTA A MITJES D'IMPLEMENTACIÓ I PROVA.
 */

package edu.upc.prop.clusterxx.controladors;

// Importacions dels controladors
import edu.upc.prop.clusterxx.domini.controladors.CtrlUsuari;
import edu.upc.prop.clusterxx.domini.controladors.CtrlDomini;

// Importacions del paquet de domini
import edu.upc.prop.clusterxx.domini.classes.Usuari;
import static edu.upc.prop.clusterxx.domini.classes.Exceptions.*;

// Importacions de Java
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Driver per provar la classe CtrlUsuari.
 * Permet provar tant la gestió de la sessió estàtica (login/registre)
 * com la gestió del HashMap intern d'usuaris per a persistència.
 * 
 * HE AÑADIDO COMENTARIOS EN LOS TESTS DONDE HE VIST QUE FALTABA ALGUNA COMPROBACIÓN EN LOS CONTROLADORES O CLASES INTERNAS
 * PUEDE QUE FALTEN ALGUNOS TESTS MÁS, PERO HE INTENTADO CUBRIR LAS FUNCIONALIDADES PRINCIPALES.
 */
public class CtrlUsuariDriver {

    private static Scanner in;
    private static CtrlUsuari cu;

    /**
     * Mètode principal que executa el driver.
     */
    public static void main(String[] args) {
        init();
        System.out.println("Driver de testeig de CtrlUsuari");

        String input = "";
        while (!input.equals("0") && !input.equals("sortir")) {
            mostra_metodes();
            input = in.nextLine();

            // Gestionem l'entrada i capturem excepcions (de login/registre)
            try {
                gestionarEntrada(input);
            } catch (Exception e) {
                System.err.println("S'HA PRODUÏT UNA EXCEPCIÓ: " + e.getMessage());
                // e.printStackTrace(); // Descomentar per a més detalls
            }
        }
        in.close();
        System.out.println("Tancant el driver. Adéu!");
    }

    /**
     * Inicialitza l'Scanner i el CtrlUsuari.
     * Passem 'null' al constructor, ja que el driver no té CtrlPresentacio.
     */
    private static void init() {
        in = new Scanner(System.in);
        // Passem null, ja que el camp ctrlPresentacio no s'utilitza 
        // en cap dels mètodes proveïts.
        cu = new CtrlUsuari(null);
    }

    /**
     * Mostra el menú d'opcions al driver.
     */
    private static void mostra_metodes() {
        System.out.println("\n--- Menú CtrlUsuari ---");
        System.out.println("--- Mètodes de Sessió (deleguen a Usuari Estàtic) ---");
        System.out.println("(1) Registrar Usuari");
        System.out.println("(2) Login");
        System.out.println("(3) Logout");
        System.out.println("(4) Get Usuari Actual");
        System.out.println("(5) Check Password");
        System.out.println("--- Mètodes de Gestió (HashMap intern per Persistència) ---");
        System.out.println("(10) Afegir Usuari (al HashMap intern)");
        System.out.println("(11) Get Usuari (del HashMap intern)");
        System.out.println("(12) Eliminar Usuari (del HashMap intern)");
        System.out.println("(13) Llistar Tots els Usuaris (del HashMap intern)");
        System.out.println("(14) Get Num Usuaris (del HashMap intern)");
        System.out.println("(15) Netejar Tots els Usuaris (del HashMap intern)");
        System.out.println("(16) Existeix Usuari (al HashMap intern)");
        System.out.println("------------------------");
        System.out.println("(0|sortir) - Tancar driver");
        System.out.print("Escull una opció: ");
    }

    /**
     * Gestiona l'entrada de l'usuari amb un switch.
     * Llança Excepció si els mètodes de negoci fallen.
     */
    private static void gestionarEntrada(String input) throws Exception {
        switch (input) {
            // --- Sessió (Estàtic) ---
            case "1":
            case "Registrar Usuari":
                testRegistrarUsuari();
                break;
            case "2":
            case "Login":
                testLogin();
                break;
            case "3":
            case "Logout":
                testLogout();
                break;
            case "4":
            case "Get Usuari Actual":
                testGetUsuariActual();
                break;
            case "5":
            case "Check Password":
                testCheckPassword();
                break;
            
            // --- Gestió (HashMap) ---
            case "10":
            case "Afegir Usuari":
                testAfegirUsuari();
                break;
            case "11":
            case "Get Usuari":
                testGetUsuari();
                break;
            case "12":
            case "Eliminar Usuari":
                testEliminarUsuari();
                break;
            case "13":
            case "Llistar Tots els Usuaris":
                testLlistarTotsUsuaris();
                break;
            case "14":
            case "Get Num Usuaris":
                testGetNumUsuaris();
                break;
            case "15":
            case "Netejar Tots els Usuaris":
                testNetejarUsuaris();
                break;
            case "16":
            case "Existeix Usuari":
                testexsisteixUsuari();
                break;

            case "0":
            case "sortir":
                break;
            default:
                System.out.println("Valor invàlid");
                break;
        }
    }

    // --- Mètodes de Test (Sessió) ---
    
    private static void testRegistrarUsuari() throws Exception {
        System.out.print("Introdueix nom d'usuari: ");
        String username = in.nextLine();
        System.out.print("Introdueix contrasenya: ");
        String password = in.nextLine();
        
        cu.registrarUsuari(username, password);
        
        System.out.println("Usuari '" + username + "' registrat (estàticament).");
    }
    
    private static void testLogin() throws Exception {
        System.out.print("Introdueix nom d'usuari: ");
        String username = in.nextLine();
        System.out.print("Introdueix contrasenya: ");
        String password = in.nextLine();
        
        // Necessitem utilitzar CtrlDomini perquè faci les validacions
        CtrlDomini cd = new CtrlDomini();
        try {
            cd.login(username, password);
            System.out.println("Login correcte. L'usuari actual (estàtic) és '" + username + "'.");
        } catch (Exception e) {
            System.out.println("Error al fer login: " + e.getMessage());
        }
    }

    private static void testLogout() {
        Usuari actual = cu.getUsuariActual();
        if (actual == null) {
            System.out.println("No hi ha cap usuari autenticat.");
            return;
        }
        String username = actual.getUsername();
        cu.logout();
        System.out.println("Logout correcte. Sessió tancada per l'usuari '" + username + "'.");
    }

    private static void testGetUsuariActual() {
        Usuari usuari = cu.getUsuariActual();
        if (usuari == null) {
            System.out.println("No hi ha cap usuari actual (estàtic) autenticat.");
        } else {
            System.out.println("L'usuari actual (estàtic) és: " + usuari.getUsername());
        }
    }

    private static void testCheckPassword() {
        System.out.print("Introdueix la contrasenya a comprovar (per l'usuari actual): ");
        String password = in.nextLine();
        boolean correcte = cu.checkPassword(password);
        if(!correcte){
            System.out.println("Contrasenya incorrecta.");
            return;
        }
        else System.out.println("Contrasenya correcta.");
    }

    // --- Mètodes de Test (Gestió/HashMap) ---

    private static void testAfegirUsuari() {
        System.out.print("Introdueix nom d'usuari: ");
        String username = in.nextLine();
        System.out.print("Introdueix contrasenya: ");
        String password = in.nextLine();
        
        // Creem l'objecte Usuari
        Usuari nouUsuari = new Usuari(username, password);
        
        // L'afegim al HashMap intern del controlador
        cu.afegirUsuari(nouUsuari);
        
        System.out.println("Usuari '" + username + "' afegit al HashMap intern (llest per persistir).");
    }

    private static void testGetUsuari() {
        System.out.print("Introdueix nom d'usuari a buscar al HashMap: ");
        String username = in.nextLine();
        
        Usuari usuari = cu.getUsuari(username);
        
        if (usuari == null) {
            System.out.println("Usuari '" + username + "' no trobat al HashMap intern.");
        } else {
            System.out.println("Usuari trobat al HashMap: " + usuari.getUsername());
        }
    }

    private static void testEliminarUsuari() {
        System.out.print("Introdueix nom d'usuari a eliminar del HashMap: ");
        String username = in.nextLine();
        cu.eliminarUsuari(username);
        System.out.println("Usuari '" + username + "' eliminat del HashMap intern.");
    }

    private static void testLlistarTotsUsuaris() {
        HashMap<String, Usuari> tots = cu.getTotsUsuaris();
        if (tots.isEmpty()) {
            System.out.println("El HashMap intern d'usuaris és buit.");
            return;
        }
        
        System.out.println("Usuaris al HashMap intern (" + tots.size() + "):");
        for (Map.Entry<String, Usuari> entry : tots.entrySet()) {
            System.out.println("  - " + entry.getKey());
        }
    }

    private static void testGetNumUsuaris() {
        int num = cu.getNumUsuaris();
        System.out.println("Hi ha " + num + " usuaris al HashMap intern.");
    }

    private static void testNetejarUsuaris() {
        cu.netejarUsuaris();
        System.out.println("El HashMap intern d'usuaris ha estat netejat.");
    }

    private static void testexsisteixUsuari(){
        System.out.print("Introdueix nom d'usuari a buscar al HashMap: ");
        String username = in.nextLine();
        
        boolean existeix = cu.existeixUsuari(username);
        
        if (existeix) {
            System.out.println("L'usuari '" + username + "' existeix al HashMap intern.");
        } else {
            System.out.println("L'usuari '" + username + "' NO existeix al HashMap intern.");
        }
    }
}