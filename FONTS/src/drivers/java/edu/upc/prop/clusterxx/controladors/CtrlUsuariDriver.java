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
import edu.upc.prop.clusterxx.domini.controladors.CtrlDomini;

// Importacions del paquet de domini
import edu.upc.prop.clusterxx.domini.classes.Usuari;
import static edu.upc.prop.clusterxx.domini.classes.Exceptions.*;

// Importacions de Java
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
    private static CtrlDomini cd;

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
        cd = new CtrlDomini();
        
    }

    /**
     * Mostra el menú d'opcions al driver.
     */
    private static void mostra_metodes() {
        System.out.println("\n--- Menú CtrlUsuari ---");
        System.out.println("--- Mètodes de Sessió ---");
        System.out.println("(1) Registrar Usuari");
        System.out.println("(2) Login");
        System.out.println("(3) Logout");
        System.out.println("(4) Esborrar Usuari");
        
        System.out.println("(5) Consulta Usuari Actual");
        
        System.out.println("------------------------");
        System.out.println("(0|sortir) - Tancar driver");
        System.out.println("Escull una opció: ");
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
                cd.logout();
                System.out.println("✓ Logout correcte!");
                break;
            
            case "4":
            case "Esborrar Usuari":
                testEsborrarUsuari();
                break;

            case "5":
            case "Consulta Usuari actual":
                testGetUsuariActual();
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
    
    private static void testRegistrarUsuari() {
        System.out.println("\n═══ REGISTRAR NOU USUARI ═══");
    System.out.println("Introdueix nom d'usuari: ");
        String username = in.nextLine();
    System.out.println("Introdueix contrasenya: ");
        String password = in.nextLine();
        try {
            cd.registrarUsuari(username, password);
            System.out.println("✓ Usuari registrat correctament!");
            System.out.println("  Ara pots fer login amb aquest usuari.");
        } catch (UsuariJaExisteixException e) {
            System.out.println("❌ Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    
    private static void testLogin() {
        System.out.println("\n═══ INICIAR SESSIÓ ═══");
    System.out.println("Introdueix nom d'usuari: ");
        String username = in.nextLine();
    System.out.println("Introdueix contrasenya: ");
        String password = in.nextLine();
        
        try {
            cd.login(username, password);            
            System.out.println("✓ Login correcte!");
            System.out.println("  Benvingut/da, " + username + "!");
        } catch (CredencialsIncorrectesException e) {
            System.out.println("❌ Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private static void testEsborrarUsuari() {
        System.out.println("\n═══ ESBORRAR USUARI ═══");
                System.out.println("Introdueix nom d'usuari a eliminar: ");
                String usernameAEliminar = in.nextLine();

                try {
                    cd.eliminarUsuari(usernameAEliminar);
                    System.out.println("Usuari '" + usernameAEliminar + "' eliminat correctament.");
                } catch (ParametreInvalidException e) {
                    System.out.println("❌ Error: " + e.getMessage());
                }
    }



    private static void testGetUsuariActual() {
        Usuari usuari = cd.getUsuariActual();
        if (usuari == null) {
            System.out.println("No hi ha cap usuari actual autenticat.");
        } else {
            System.out.println("L'usuari actual és: " + usuari.getUsername());
        }
    }
    
}