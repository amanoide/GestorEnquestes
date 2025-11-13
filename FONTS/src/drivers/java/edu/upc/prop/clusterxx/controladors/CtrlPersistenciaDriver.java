package edu.upc.prop.clusterxx.controladors;

// Importacions dels controladors
import edu.upc.prop.clusterxx.domini.controladors.CtrlPersistencia;

// Importacions dels stubs de domini
import edu.upc.prop.clusterxx.domini.classes.Enquesta;
import edu.upc.prop.clusterxx.domini.classes.Perfil;
import edu.upc.prop.clusterxx.domini.classes.Pregunta;
import edu.upc.prop.clusterxx.domini.classes.Resposta;
import edu.upc.prop.clusterxx.domini.classes.Usuari;
import static edu.upc.prop.clusterxx.domini.classes.Exceptions.*;

// Importacions de Java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Driver per provar la classe CtrlPersistencia (Singleton).
 * Prova els mètodes de repositori (CRUD) per a cada tipus de dada.
 * Requereix classes STUB del paquet domini per funcionar.
 */
public class CtrlPersistenciaDriver {

    private static Scanner in;
    private static CtrlPersistencia cp;

    public static void main(String[] args) {
        init();
        System.out.println("Driver de testeig de CtrlPersistencia (Singleton)");
        System.out.println("Nota: Aquest driver utilitza STUBS del paquet domini.");

        String input = "";
        while (!input.equals("0") && !input.equals("sortir")) {
            mostra_metodes();
            input = in.nextLine();

            try {
                gestionarEntrada(input);
            } catch (Exception e) {
                System.err.println("S'HA PRODUÏT UN ERROR: " + e.getMessage());
                // e.printStackTrace(); // Descomentar per a més detalls
            }
        }
        in.close();
        System.out.println("Tancant el driver. Adéu!");
    }

    private static void init() {
        in = new Scanner(System.in);
        // Obtenim la instància Singleton
        cp = CtrlPersistencia.getInstance();
    }

    private static void mostra_metodes() {
        System.out.println("\n--- Menú CtrlPersistencia ---");
        System.out.println("--- Enquestes ---");
        System.out.println("(10) Afegir Enquesta");
        System.out.println("(11) Get Enquesta (per ID)");
        System.out.println("(12) Eliminar Enquesta");
        System.out.println("(13) Llistar Totes les Enquestes");
        System.out.println("(14) Get Num Enquestes");
        System.out.println("(15) Save Enquestes (Sobreescriu amb dades noves)");
        System.out.println("(16) Load Enquestes (Carrega i mostra totes les enquestes)");
        System.out.println("--- Usuaris ---");
        System.out.println("(20) Save Usuaris (Sobreescriu amb dades noves)");
        System.out.println("(21) Load Usuaris (Carrega i mostra tots els usuaris)");
        System.out.println("(22) Afegir Usuari");
        System.out.println("(23) Get Usuari (per Username)");
        System.out.println("(24) Eliminar Usuari");
        System.out.println("(25) Llistar Tots els Usuaris");
        System.out.println("(26) Get Num Usuaris");
        System.out.println("(27) Existeix Usuari (per Username)");
        System.out.println("--- Perfils ---");
        System.out.println("(30) Save Perfils (Sobreescriu amb dades noves)");
        System.out.println("(31) Load Perfils (Carrega i mostra tots els perfils)");
        System.out.println("(32) Afegir Perfil");
        System.out.println("(33) Get Perfil (per ID)");
        System.out.println("(34) Eliminar Perfil");
        System.out.println("(35) Llistar Tots els Perfils");
        System.out.println("--- Preguntes Plantilla ---");
        System.out.println("(40) Save Preguntes Plantilla (Sobreescriu amb dades noves)");
        System.out.println("(41) Load Preguntes Plantilla (Carrega i mostra totes les preguntes plantilla)");
        System.out.println("(42) Afegir Pregunta Plantilla");
        System.out.println("(43) Get Pregunta Plantilla (per ID)");
        System.out.println("(44) Eliminar Pregunta Plantilla");
        System.out.println("(45) Llistar Totes les Preguntes Plantilla");
        System.out.println("--- Respostes ---");
        System.out.println("(50) Save Respostes (Sobreescriu amb dades noves)");
        System.out.println("(41) Load Respostes (Carrega i mostra totes les respostes)");
        System.out.println("(52) Afegir Resposta");
        System.out.println("(53) Get Respostes d'un Usuari");
        System.out.println("(54) Get Respostes d'una Enquesta");
        System.out.println("------------------------");
        System.out.println("(0|sortir) - Tancar driver");
        System.out.print("Escull una opció: ");
    }

    private static void gestionarEntrada(String input) throws Exception {
        switch (input) {
            // Enquestes
            case "10" :
            case "Afegir Enquesta" :
                testAfegirEnquesta();
                break;
            case "11" :
            case "Get Enquesta" :
                testGetEnquesta();
                break;

            case "12" :
            case "Eliminar Enquesta" :
                testEliminarEnquesta();
                break;
            case "13" :
            case "Llistar Totes les Enquestes" :
                testGetAllEnquestes();
                break;
            case "14" :
            case "Get Num Enquestes" :
                testGetNumEnquestes();
                break;
            case "15" :
            case "Save Enquestes" :
                testSaveEnquestes();
                break;
            case "16" :
            case "Load Enquestes" :
                testloadEnquestes();
                break;
            // Usuaris
            case "20":
            case "Save Usuaris":
                testsaveUsuaris();
                break;
            case "21":
            case "Load Usuaris":
                testloadUsuaris();
                break;
            case "22":
            case "Afegir Usuari":
                testAfegirUsuari();
                break;
            case "23":
            case "Get Usuari":
                testGetUsuari();
                break;
            case "24":
            case "Eliminar Usuari":
                testEliminarUsuari();
                break;
            case "25":
            case "Llistar Tots els Usuaris":
                testGetAllUsuaris();
                break;
            case "26":
            case "Get Num Usuaris":
                testGetNumUsuaris();
                break;
            case "27":
            case "Existeix Usuari":
                testexsisteixUsuari();
                break;
            
            // Perfils
            case "30":
            case "Save Perfils":
                testSavePerfils();
                break;
            case "31":
            case "Load Perfils":
                testloadPerfils();
                break;
            case "32":
            case "Afegir Perfil":
                testAfegirPerfil();
                break;
            case "33":
            case "Get Perfil":
                testGetPerfil();
                break;
            case "34":
            case "Eliminar Perfil":
                testEliminarPerfil();
                break;
            case "35":
            case "Llistar Tots els Perfils":
                testGetAllPerfils();
                break;
            
            /* 
            // Preguntes
            case "40":
            case "Save Preguntes Plantilla":
                testSavePreguntesPlantilla();
                break;
            case "41":
            case "Load Preguntes Plantilla":
                testloadPreguntesPlantilla();
                break;
            case "42":
            case "Afegir Pregunta Plantilla":
                testAfegirPreguntaPlantilla();
                break;
            case "43":
            case "Get Pregunta Plantilla":
                testGetPreguntaPlantilla();
                break;
            case "44":
            case "Eliminar Pregunta Plantilla":
                testEliminarPreguntaPlantilla();
                break;
            case "45":
            case "Llistar Totes les Preguntes Plantilla":
                testGetAllPreguntesPlantilla();
                break;
            
            // Respostes
            case "50":
            case "Save Respostes":
                testsaveRespostes();
                break;
            case "51":
            case "Load Respostes":
                testloadRespostes();
                break;
            case "52":
            case "Afegir Resposta":
                testAfegirResposta();
                break;
            case "53":
            case "Get Respostes d'un Usuari":
                testGetRespostesUsuari();
                break;
            case "54":
            case "Get Respostes d'una Enquesta":
                testGetRespostesEnquesta();
                break;
                */
            
            case "0": case "sortir": break;
            default: System.out.println("Valor invàlid"); break;
        }
    }

    // --- Mètodes de Test (Enquestes) ---

    private static void testAfegirEnquesta() {
        System.out.print("Introdueix ID enquesta: ");
        String id = in.nextLine();
        System.out.print("Introdueix Títol: ");
        String titol = in.nextLine();
        System.out.print("Introdueix Username creador: ");
        String username = in.nextLine();
        System.out.print("Introdueix la Descripció: ");
        String desc = in.nextLine();
        
        Usuari creadorStub = new Usuari(username, "pass");
        Enquesta enquestaStub = new Enquesta(id, titol, desc, creadorStub);
        
        cp.afegirEnquesta(enquestaStub);
        System.out.println("Enquesta afegida.");
    }

    private static void testGetEnquesta() {
        System.out.print("Introdueix ID enquesta: ");
        String id = in.nextLine();
        Enquesta e = cp.getEnquesta(id);
        if (e != null) {
            System.out.println("Trobat: " + e.toString());
        } else {
            System.out.println("Enquesta no trobada.");
        }
    }

    private static void testEliminarEnquesta() {
        System.out.print("Introdueix ID enquesta a eliminar: ");
        String id = in.nextLine();
        boolean eliminada = cp.eliminarEnquesta(id);
        System.out.println("Enquesta eliminada: " + eliminada);
    }

    private static void testGetAllEnquestes() {
        ArrayList<Enquesta> llista = cp.getAllEnquestes();
        System.out.println("Llistat d'enquestes (" + llista.size() + "):");
        for (Enquesta e : llista) {
            System.out.println("  - " + e.toString());
        }
    }
    
    private static void testSaveEnquestes() {
        System.out.println("Creant una nova llista (simulada) de 2 enquestes (e_save1, e_save2)...");
        ArrayList<Enquesta> llistaNova = new ArrayList<>();
        Usuari uStub = new Usuari("user_save", "pass");
        llistaNova.add(new Enquesta("e_save1", "Titol Save 1", "Descripcio Save1", uStub));
        llistaNova.add(new Enquesta("e_save2", "Titol Save 2", "Descripcio Save2", uStub));
        
        cp.saveEnquestes(llistaNova);
        System.out.println("Save completat. Totes les enquestes anteriors han estat sobreescrites.");
    }

    private static void testloadEnquestes() {
        ArrayList<Enquesta> llistaCarregada = cp.loadEnquestes();
        System.out.println("Llistat d'enquestes carregades (" + llistaCarregada.size() + "):");
        for (Enquesta e : llistaCarregada) {
            System.out.println("  - " + e.toString());
        }
    }

    private static void testGetNumEnquestes() {
        int num = cp.getNumEnquestes();
        System.out.println("Nombre d'enquestes: " + num);
    }

    // --- Mètodes de Test (Usuaris) ---

    private static void testsaveUsuaris() {
        System.out.println("Creant una nova llista (simulada) de 2 usuaris (u_save1, u_save2)...");
        HashMap<String, Usuari> mapNova = new HashMap<>();
        mapNova.put("u_save1", new Usuari("u_save1", "pass1"));
        mapNova.put("u_save2", new Usuari("u_save2", "pass2"));
        
        cp.saveUsuaris(mapNova);
        System.out.println("Save completat. Tots els usuaris anteriors han estat sobreescrits.");
    }

    private static void testloadUsuaris() {
        HashMap<String, Usuari> mapCarregat = cp.loadUsuaris();
        System.out.println("Llistat d'usuaris carregats (" + mapCarregat.size() + "):");
        for (Map.Entry<String, Usuari> entry : mapCarregat.entrySet()) {
            System.out.println("  - Key: " + entry.getKey() + ", Value: " + entry.getValue().toString());
        }
    }

    private static void testAfegirUsuari() {
        System.out.print("Introdueix Username: ");
        String username = in.nextLine();
        System.out.print("Introdueix Password: ");
        String pass = in.nextLine();
        
        Usuari usuariStub = new Usuari(username, pass);
        cp.afegirUsuari(username, usuariStub);
        System.out.println("Usuari afegit.");
    }

    private static void testGetUsuari() {
        System.out.print("Introdueix Username: ");
        String username = in.nextLine();
        Usuari u = cp.getUsuari(username);
        if (u != null) {
            System.out.println("Trobat: " + u.toString());
        } else {
            System.out.println("Usuari no trobat.");
        }
    }

    private static void testEliminarUsuari() {
        System.out.print("Introdueix Username a eliminar: ");
        String username = in.nextLine();
        Usuari u = cp.eliminarUsuari(username);
        System.out.println("Usuari eliminat: " + (u != null ? u.toString() : "cap (no existia)"));
    }

    private static void testGetAllUsuaris() {
        HashMap<String, Usuari> map = cp.getAllUsuaris();
        System.out.println("Llistat d'usuaris (" + map.size() + "):");
        for (Map.Entry<String, Usuari> entry : map.entrySet()) {
            System.out.println("  - Key: " + entry.getKey() + ", Value: " + entry.getValue().toString());
        }
    }

    private static void testexsisteixUsuari() {
        System.out.print("Introdueix Username a comprovar: ");
        String username = in.nextLine();
        boolean existeix = cp.existeixUsuari(username);
        if(existeix) {
            System.out.println("L'usuari '" + username + "' existeix.");
        } else {
            System.out.println("L'usuari '" + username + "' NO existeix.");
        }
    }

    private static void testGetNumUsuaris() {
        int num = cp.getNumUsuaris();
        System.out.println("Nombre d'usuaris: " + num);
    }

    // --- Mètodes de Test (Perfils) ---

    private static void testSavePerfils() {
        System.out.println("Creant una nova llista (simulada) de 2 perfils (p_save1, p_save2)...");
        HashMap<String, Perfil> mapNova = new HashMap<>();
        mapNova.put("p_save1", new Perfil(1, "Descripció Save 1"));
        mapNova.put("p_save2", new Perfil(2, "Descripció Save 2"));
        
        cp.savePerfils(mapNova);
        System.out.println("Save completat. Tots els perfils anteriors han estat sobreescrits.");
    }

    private static void testloadPerfils() {
        HashMap<String, Perfil> mapCarregat = cp.loadPerfils();
        System.out.println("Llistat de perfils carregats (" + mapCarregat.size() + "):");
        for (Map.Entry<String, Perfil> entry : mapCarregat.entrySet()) {
            System.out.println("  - Key: " + entry.getKey() + ", Value: " + entry.getValue().toString());
        }
    }

    private static void testAfegirPerfil() {
        System.out.print("Introdueix ID perfil (numèric): ");
        int idInt = Integer.parseInt(in.nextLine());
        String idStr = String.valueOf(idInt);
        System.out.print("Introdueix Descripció: ");
        String desc = in.nextLine();
        
        Perfil perfilStub = new Perfil(idInt, desc);
        cp.afegirPerfil(idStr, perfilStub);
        System.out.println("Perfil afegit.");
    }

    private static void testGetPerfil() {
        System.out.print("Introdueix ID perfil: ");
        String id = in.nextLine();
        Perfil p = cp.getPerfil(id);
        if (p != null) {
            System.out.println("Trobat: " + p.toString());
        } else {
            System.out.println("Perfil no trobat.");
        }
    }
    
    private static void testEliminarPerfil() {
        System.out.print("Introdueix ID perfil a eliminar: ");
        String id = in.nextLine();
        Perfil p = cp.eliminarPerfil(id);
        System.out.println("Perfil eliminat: " + (p != null ? p.toString() : "cap (no existia)"));
    }

    private static void testGetAllPerfils() {
        HashMap<String, Perfil> map = cp.getAllPerfils();
        System.out.println("Llistat de perfils (" + map.size() + "):");
        for (Map.Entry<String, Perfil> entry : map.entrySet()) {
            System.out.println("  - Key: " + entry.getKey() + ", Value: " + entry.getValue().toString());
        }
    }

    // --- Mètodes de Test (Preguntes Plantilla) ---
    /* 
    private static void testSavePreguntesPlantilla() {
        System.out.println("Creant una nova llista (simulada) de 2 preguntes plantilla (q_save1, q_save2)...");
        HashMap<String, Pregunta> mapNova = new HashMap<>();
        mapNova.put("q_save1", new Pregunta("q_save1", "Text Save 1"));
        mapNova.put("q_save2", new Pregunta("q_save2", "Text Save 2"));
        
        cp.savePreguntes(mapNova);
        System.out.println("Save completat. Totes les preguntes plantilla anteriors han estat sobreescrites.");
    }

    private static void testloadPreguntesPlantilla() {
        HashMap<String, Pregunta> mapCarregat = cp.loadPreguntes();
        System.out.println("Llistat de preguntes plantilla carregades (" + mapCarregat.size() + "):");
        for (Map.Entry<String, Pregunta> entry : mapCarregat.entrySet()) {
            System.out.println("  - Key: " + entry.getKey() + ", Value: " + entry.getValue().toString());
        }
    }

    private static void testAfegirPreguntaPlantilla() {
        System.out.print("Introdueix ID pregunta: ");
        String id = in.nextLine();
        System.out.print("Introdueix Text: ");
        String text = in.nextLine();
        
        Pregunta preguntaStub = new Pregunta(id, text);
        cp.afegirPreguntaPlantilla(id, preguntaStub);
        System.out.println("Pregunta plantilla afegida.");
    }

    private static void testGetPreguntaPlantilla() {
        System.out.print("Introdueix ID pregunta: ");
        String id = in.nextLine();
        Pregunta p = cp.getPreguntaPlantilla(id);
        if (p != null) {
            System.out.println("Trobada: " + p.toString());
        } else {
            System.out.println("Pregunta plantilla no trobada.");
        }
    }
    
    private static void testEliminarPreguntaPlantilla() {
        System.out.print("Introdueix ID pregunta a eliminar: ");
        String id = in.nextLine();
        Pregunta p = cp.eliminarPreguntaPlantilla(id);
        System.out.println("Pregunta eliminada: " + (p != null ? p.toString() : "cap (no existia)"));
    }

    private static void testGetAllPreguntesPlantilla() {
        HashMap<String, Pregunta> map = cp.getAllPreguntesPlantilla();
        System.out.println("Llistat de preguntes plantilla (" + map.size() + "):");
        for (Map.Entry<String, Pregunta> entry : map.entrySet()) {
            System.out.println("  - Key: " + entry.getKey() + ", Value: " + entry.getValue().toString());
        }
    }

    // --- Mètodes de Test (Respostes) ---

    private static void testsaveRespostes() {
        System.out.println("Creant una nova llista (simulada) de respostes per a l'enquesta 'e_save_respostes'...");
        String idEnquestaPerGuardar = "e_save_respostes";
        HashMap<String, HashMap<String, Resposta>> mapaUsuaris = new HashMap<>();
        
        // Respostes de l'usuari u1
        HashMap<String, Resposta> respostesU1 = new HashMap<>();
        Usuari u1Stub = new Usuari("u1", "pass1");
        respostesU1.put("q1", new Resposta("q1", "Resposta 1", u1Stub));
        respostesU1.put("q2", new Resposta("q2", "Resposta 2", u1Stub));
        mapaUsuaris.put("u1", respostesU1);
        
        // Respostes de l'usuari u2
        HashMap<String, Resposta> respostesU2 = new HashMap<>();
        Usuari u2Stub = new Usuari("u2", "pass2");
        respostesU2.put("q1", new Resposta("q1", "Resposta A", u2Stub));
        respostesU2.put("q2", new Resposta("q2", "Resposta B", u2Stub));
        mapaUsuaris.put("u2", respostesU2);
        
        HashMap<String, HashMap<String, HashMap<String, Resposta>>> mapNova = new HashMap<>();
        mapNova.put(idEnquestaPerGuardar, mapaUsuaris);
        cp.saveRespostes(mapNova);
        System.out.println("Save completat. Totes les respostes anteriors per a aquesta enquesta han estat sobreescrites.");
    }

    private static void testloadRespostes() {
    // 1. Aquest mètode carrega TOTES les respostes de TOTES les enquestes.
    // El tipus és: Map<EnquestaID, Map<UsuariID, Map<PreguntaID, Resposta>>>
    HashMap<String, HashMap<String, HashMap<String, Resposta>>> mapCarregat = cp.loadRespostes();
    
    for (Map.Entry<String, HashMap<String, HashMap<String, Resposta>>> entryEnquesta : mapCarregat.entrySet()) {
        String idEnquesta = entryEnquesta.getKey();
        HashMap<String, HashMap<String, Resposta>> mapaUsuaris = entryEnquesta.getValue();
        
        System.out.println("  > Enquesta: " + idEnquesta + " (" + mapaUsuaris.size() + " usuaris)");

        
        for (Map.Entry<String, HashMap<String, Resposta>> entryUsuari : mapaUsuaris.entrySet()) {
            String idUsuari = entryUsuari.getKey();
            HashMap<String, Resposta> mapaRespostes = entryUsuari.getValue();

            System.out.println("    >> Usuari: " + idUsuari + " (" + mapaRespostes.size() + " respostes)");

           
            for (Map.Entry<String, Resposta> entryResposta : mapaRespostes.entrySet()) {
                String idPregunta = entryResposta.getKey();
                Resposta resposta = entryResposta.getValue();
                
                System.out.println("      - Preg: " + idPregunta + ", Resp: " + resposta.toString());
            }
        }
    }
    System.out.println("--- Fi del llistat ---");
}

    private static void testAfegirResposta() {
        System.out.print("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        System.out.print("Introdueix Username: ");
        String username = in.nextLine();
        System.out.print("Introdueix ID pregunta: ");
        String idPregunta = in.nextLine();
        System.out.print("Introdueix Valor de la resposta: ");
        String valor = in.nextLine();

        // Necessitem un usuari stub per crear la resposta
        Usuari usuariStub = new Usuari(username, "pass");
        Resposta respostaStub = new Resposta(idPregunta, valor, usuariStub);
        
        cp.afegirResposta(idEnquesta, username, idPregunta, respostaStub);
        System.out.println("Resposta afegida.");
    }

    private static void testGetRespostesUsuari() {
        System.out.print("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        System.out.print("Introdueix Username: ");
        String username = in.nextLine();
        
        HashMap<String, Resposta> map = cp.getRespostesUsuari(idEnquesta, username);
        System.out.println("Respostes de '" + username + "' a '" + idEnquesta + "' (" + map.size() + "):");
        for (Map.Entry<String, Resposta> entry : map.entrySet()) {
            System.out.println("  - Preg: " + entry.getKey() + ", Resp: " + entry.getValue().toString());
        }
    }

    private static void testGetRespostesEnquesta() {
        System.out.print("Introdueix ID enquesta: ");
        String idEnquesta = in.nextLine();
        
        HashMap<String, HashMap<String, Resposta>> map = cp.getRespostesEnquesta(idEnquesta);
        System.out.println("Respostes de l'enquesta '" + idEnquesta + "' (" + map.size() + " usuaris):");
        
        for (Map.Entry<String, HashMap<String, Resposta>> entryUsuari : map.entrySet()) {
            System.out.println("  > Usuari: " + entryUsuari.getKey());
            for (Map.Entry<String, Resposta> entryResposta : entryUsuari.getValue().entrySet()) {
                System.out.println("    - Preg: " + entryResposta.getKey() + ", Resp: " + entryResposta.getValue().toString());
            }
        }
    }
        */
    
}