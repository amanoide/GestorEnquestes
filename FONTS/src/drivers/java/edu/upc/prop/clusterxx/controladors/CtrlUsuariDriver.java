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
        System.out.println("--- Mètodes de Sessió (deleguen a Usuari Estàtic) ---");
        System.out.println("(1) Registrar Usuari");
        System.out.println("(2) Login");
        System.out.println("(3) Logout");
        /*
        System.out.println("(4) Canviar Contrasenya");
        System.out.println("(5) Esborrar Usuari");

        
        System.out.println("(6) Get Usuari Actual");
        System.out.println("(7) Check Password");
        */
        
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
                gestioEnquestes();
                break;
            
           /* case "4":
            case "Canviar Contrasenya":
                System.out.print("Introdueix la nova contrasenya: ");
                String novaPassword = in.nextLine();
                Usuari usuariActual = cd.getUsuariActual();
                if (usuariActual == null) {
                    System.out.println("No hi ha cap usuari actual autenticat.");
                    return;
                }
                usuariActual.setPassword(novaPassword);
                System.out.println("Contrasenya canviada correctament per l'usuari '" + usuariActual.getUsername() + "'.");
                break;
            case "5":
            case "Esborrar Usuari":
                Usuari usuariAEliminar = cd.getUsuariActual();
                if (usuariAEliminar == null) {
                    System.out.println("No hi ha cap usuari actual autenticat.");
                    return;
                }
                String usernameAEliminar = usuariAEliminar.getUsername();
                cd.logout(); // Fem logout abans d'eliminar
                cd.eliminarUsuari(usernameAEliminar);
                System.out.println("Usuari '" + usernameAEliminar + "' eliminat correctament.");
                break;
            
                case "3":
            case "Get Usuari Actual":
                testGetUsuariActual();
                break;
            case "4":
            case "Check Password":
                testCheckPassword();
                break;
            */

            case "0":
            case "sortir":
                break;
            default:
                System.out.println("Valor invàlid");
                break;
        }
    }

    private static void gestioEnquestes() {
        try {
            String fullClassName = "edu.upc.prop.clusterxx.controladors.CtrlEnquestaDriver";
            Class<?> clazz = Class.forName(fullClassName);
            java.lang.reflect.Method mainMethod = clazz.getMethod("main", String[].class);
            
            System.out.println("\n═══════════════════════════════════════════");
            System.out.println("   Executant ");
            System.out.println("═══════════════════════════════════════════\n");
            
            String[] args = new String[0];
            mainMethod.invoke(null, (Object) args);
            
            System.out.println("\n═══════════════════════════════════════════");
            System.out.println("   Fi de ");
            System.out.println("═══════════════════════════════════════════");
            
            // Recrear el Scanner después de ejecutar el driver
            // porque muchos drivers hacen in.close() al terminar
            in = new Scanner(System.in);
            
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Error: No s'ha trobat la classe del driver: " + e.getMessage());
        } catch (NoSuchMethodException e) {
            System.out.println("❌ Error: El driver no té un mètode main()");
        } catch (Exception e) {
            System.out.println("❌ Error en executar el driver: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // --- Mètodes de Test (Sessió) ---
    
    private static void testRegistrarUsuari() {
        System.out.println("\n═══ REGISTRAR NOU USUARI ═══");
        System.out.print("Introdueix nom d'usuari: ");
        String username = in.nextLine();
        System.out.print("Introdueix contrasenya: ");
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
        System.out.print("Introdueix nom d'usuari: ");
        String username = in.nextLine();
        System.out.print("Introdueix contrasenya: ");
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
/*
    private static void logout() {
        System.out.println("\n✓ Sessió tancada. Fins aviat, " + usuariActual.getUsername() + "!");
        usuariActual = null;
    }

    private static void canviarcontrasenya() {
        System.out.print("Introdueix la nova contrasenya: ");
        String novaPassword = in.nextLine();
        if (usuariActual == null) {
            System.out.println("No hi ha cap usuari actual autenticat.");
            return;
        }
    // HAURIA D'HAVER UNA OPERACIÓ A CtrlUsuari PER CANVIAR LA CONTRASENYA, NO FER-HO DIRECTAMENT DES DEL DRIVER
        usuariActual.setPassword(novaPassword);
        System.out.println("Contrasenya canviada correctament per l'usuari '" + usuariActual.getUsername() + "'.");
    }

    prvate static void testEsborrarUsuari() {
        if (usuariActual == null) {
            System.out.println("No hi ha cap usuari actual autenticat.");
            return;
        }
        String usernameAEliminar = usuariActual.getUsername();
        logout(); // Fem logout abans d'eliminar
        cd.eliminarUsuari(usernameAEliminar);
        System.out.println("Usuari '" + usernameAEliminar + "' eliminat correctament.");
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
*/
    
}