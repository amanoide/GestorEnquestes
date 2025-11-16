/**
 * Driver per a la classe CtrlResposta.
 * Permet provar les funcionalitats de registre, modificació, esborrat i consulta de respostes.7
 * S'utilitza per a la validació i testeig de la classe CtrlResposta.
 * 
 * @autor Joan Garvin Cardona
 * 
 * IMPORTANT:
 * ESTA A MITJES DE DESENVOLUPAMENT
 * 
 */

package edu.upc.prop.clusterxx.controladors;

// Importacions dels controladors
import edu.upc.prop.clusterxx.domini.controladors.CtrlDomini;

// Importacions dels stubs de domini
import edu.upc.prop.clusterxx.domini.classes.Enquesta;

import edu.upc.prop.clusterxx.domini.classes.Pregunta;
import edu.upc.prop.clusterxx.domini.classes.Opcio;
import edu.upc.prop.clusterxx.domini.classes.Resposta;
import edu.upc.prop.clusterxx.domini.classes.TipusPregunta;
import edu.upc.prop.clusterxx.domini.classes.Usuari;
import edu.upc.prop.clusterxx.domini.classes.Exceptions.*;

// Importacions de Java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.List;

/**
 * Driver per provar la classe CtrlResposta.
 * Permet provar el registre, modificació, esborrat i consulta de respostes.
 * 
 * HE AÑADIDO COMENTARIOS EN LOS TESTS DONDE HE VIST QUE FALTABA ALGUNA COMPROBACIÓN EN LOS CONTROLADORES O CLASES INTERNAS
 * PUEDE QUE FALTEN ALGUNOS TESTS MÁS, PERO HE INTENTADO CUBRIR LAS FUNCIONALIDADES PRINCIPALES.
 */
public class CtrlRespostaDriver {

    private static Scanner in;
    private static CtrlDomini cd;
    private static Usuari admin;
    private static Enquesta enquesta;
    /**
     * Mètode principal que executa el driver.
     */
    public static void main(String[] args) {
        init();
        System.out.println("Driver de testeig de CtrlResposta");

        String input = "";
        while (!input.equals("0") && !input.equals("sortir")) {
            mostra_metodes();
            input = in.nextLine();

            // Gestionem l'entrada i capturem errors
            try {
                gestionarEntrada(input);
            } catch (Exception e) {
                System.err.println("S'HA PRODUÏT UN ERROR D'ENTRADA: " + e.getMessage());
            }
        }
        in.close();
        System.out.println("Tancant el driver. Adéu!");
    }

    /**
     * Inicialitza l'Scanner i el CtrlResposta.
     */
    private static void init() {
        in = new Scanner(System.in);
        cd = new CtrlDomini();
        admin = new Usuari("USER_MOCK", "1234");
        Usuari.login(admin);

        //crear opcions mock per les preguntes que ho requereixin
        Opcio opcio1 = new Opcio(1, "OPCIO1");
        Opcio opcioOrdenada = new Opcio(2, "OPCIO2", 2);
        
        //crear preguntes mock hi ha d'haver una de cada tipus amb una opcio les d'opció
        Pregunta preguntaText = new Pregunta("1", "PREGUNTA DE TEXT LLIURE", "text");
        Pregunta preguntaNum = new Pregunta("2", "PREGUNTA NUMERICA", 0.0, 10.0);
        Pregunta preguntaOrd = new Pregunta("3", "PREGUNTA QUALITATIVA", "qualitativa_ordenada");
        preguntaOrd.afegirOpcio(opcioOrdenada);
        Pregunta preguntaQS = new Pregunta("4", "PREGUNTA QUALITATIVA SIMPLE", "qualitativa_simple");
        preguntaQS.afegirOpcio(opcio1);
        Pregunta preguntaQM = new Pregunta("5", "PREGUNTA QUALITATIVA MULTIPLE", "qualitativa_multiple");
        preguntaQM.afegirOpcio(opcio1);
        preguntaQM.afegirOpcio(opcio1);

        //crear enquesta mock
        enquesta = new Enquesta("1", "ENQUESTA_MOCK", "AQUESTA ENQUESTA ÉS UNA ENQUESTA DE PROVA", admin);
         
        // ✓ REGISTRAR L'ENQUESTA EN EL CONTROLADOR
        try {
            cd.crearEnquesta("1", "ENQUESTA_MOCK", "AQUESTA ENQUESTA ÉS UNA ENQUESTA DE PROVA");
            
            // Afegir les preguntes a través del controlador
            cd.afegirPregunta("1", preguntaText);
            cd.afegirPregunta("1", preguntaNum);
            cd.afegirPregunta("1", preguntaOrd);
            cd.afegirPregunta("1", preguntaQS);
            cd.afegirPregunta("1", preguntaQM);

            
            System.out.println("✓ Enquesta mock registrada correctament en CtrlDomini");
        } catch (Exception e) {
            System.out.println("⚠️ Error registrant enquesta mock: " + e.getMessage());
            // Continuar igualment amb la enquesta local si falla
        }
    }

    /**
     * Mostra el menú d'opcions al driver.
     */
    private static void mostra_metodes() {
        System.out.println("\n--- Menú CtrlResposta ---");
        System.out.println("(1) Contestar enquesta");
        System.out.println("(2) Modificar Resposta");
        System.out.println("(3) Esborrar Resposta");
        System.out.println("(4) Importar Respostes des de Fitxer");
        System.out.println("(5) Consultar Respostes");

        System.out.println("CREACIÓ DE MOCKS:");
        System.out.println("(500) Importar Enquesta");
        System.out.println("------------------------");
        System.out.println("(0|sortir) - Tancar driver");
        System.out.println("Escull una opció: ");
    }

    /**
     * Gestiona l'entrada de l'usuari amb un switch.
     */
    private static void gestionarEntrada(String input) throws Exception {
        switch (input) {
            case "500" :
            case "Importar Enquesta" :
                testImportarEnquesta();
                break;  
            case "1":
            case "Contestar enquesta":
                contestarEnquesta();
                break;
            case "2":
            case "Modificar Resposta":
                testModificarResposta();
                break;
            case "3":
            case "Esborrar Resposta":
                testEsborrarResposta();
                break;
            case "4":
            case "Importar Respostes des de Fitxer":
                importarRespostes();
                break;
            case "5":
            case "Consultar Respostes":
                testConsultarRespostesEnquesta();
                break;
            case "0":
            case "sortir":
                break;
            default:
                System.out.println("Valor invàlid");
                break;
        }
    }


    // --- Mètodes de Test ---
    
   private static void testImportarEnquesta() {
        
        System.out.println("\n═══ IMPORTAR ENQUESTA DES DE JSON ═══");
        System.out.println("Fitxer d'exemple: exemple_enquesta.json");
    System.out.println("Ruta del fitxer JSON (o només el nom si està en el directori actual): ");
        String path = in.nextLine().trim();
        
        // Si solo es un nombre de archivo, añadir la ruta completa
        if (!path.contains("\\") && !path.contains("/")) {
            path = System.getProperty("user.dir") + "\\" + path;
        }
        
        try {
            cd.importarEnquesta(path);
            System.out.println("✓ Enquesta importada correctament!");
        } catch (ErrorImportacioException e) {
            System.out.println("❌ Error important l'enquesta: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error inesperat: " + e.getMessage());
        }
    

    }

    private static void contestarEnquesta() {
        System.out.println("\n═══ RESPONDRE ENQUESTA ═══");
        ArrayList<Enquesta> totes = new ArrayList<>();
        try{
         totes = cd.consultarEnquestes();
        } catch (UsuariNoAutenticatException e){
            System.out.println("❌ Error: " + e.getMessage());
        }
        if (totes.isEmpty()) {
            System.out.println("No hi ha enquestes al sistema.");
            return;
        }
        
        System.out.println("Enquestes disponibles:");
        for (int i = 0; i < totes.size(); i++) {
            Enquesta e = totes.get(i);
            System.out.println((i + 1) + ". " + e.getTitol() + " (ID: " + e.getId() + ")");
        }
        
        int num = -1;
        boolean numValid = false;
        
        while (!numValid) {
            try {
                System.out.print("\nEscull enquesta (número): ");
                num = Integer.parseInt(in.nextLine()) - 1;
                
                if (num < 0 || num >= totes.size()) {
                    System.out.println("❌ Número no vàlid. Tria un número entre 1 i " + totes.size());
                } else {
                    numValid = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Si us plau, introdueix un número vàlid");
            }
        }
        
        try {
            Enquesta enquesta = totes.get(num);
            List<Pregunta> preguntes = enquesta.getPreguntes();
            
            //DEBEIA PARAR LA EJECUCION SI NO HAY PREGUNTAS
            
            HashMap<String, String> respostes = new HashMap<>();
            HashMap<String, String> idPreguntaPerResposta = new HashMap<>();
            System.out.println("\n─── " + enquesta.getTitol() + " ───");
            System.out.println(enquesta.getDescripcio() + "\n");
            
            for (Pregunta p : preguntes) {
                System.out.println("➤ " + p.getText());
                
                switch (p.getTipus()) {
                    case TEXT_LLIURE:
                        System.out.print("  Resposta: ");
                        String respText = in.nextLine();
                        respostes.put(p.getId(), respText);
                        idPreguntaPerResposta.put(p.getId(), p.getId());
                        break;
                        
                    case NUMERICA:
                        System.out.print("  Valor (" + p.getValorMinim() + "-" + p.getValorMaxim() + "): ");
                        boolean valid = false;
                        while(!valid){
                            String respNum = in.nextLine();
                            try{
                                double valor = Double.parseDouble(respNum);
                                if(valor < p.getValorMinim() || valor > p.getValorMaxim()){
                                    System.out.print("  ❌ Valor fora de rang. Torna a intentar: ");
                                } else {
                                    respostes.put(p.getId(), respNum);
                                    idPreguntaPerResposta.put(p.getId(), p.getId());
                                    valid = true;
                                }
                            } catch (NumberFormatException e){
                                System.out.print("  ❌ Si us plau, introdueix un número vàlid: ");
                            }
                        }
                        break;
                        
                    case QUALITATIVA_ORDENADA:
                    case QUALITATIVA_NO_ORDENADA_SIMPLE:
                        List<Opcio> opcions = p.getOpcions();
                        for (int j = 0; j < opcions.size(); j++) {
                            System.out.println("  " + (j + 1) + ". " + opcions.get(j).getText());
                        }
                        
                        int opcioIdx = -1;
                        boolean opcioValida = false;
                        while (!opcioValida) {
                            try {
                                System.out.print("  Escull opció (1-" + opcions.size() + "): ");
                                opcioIdx = Integer.parseInt(in.nextLine()) - 1;
                                if (opcioIdx < 0 || opcioIdx >= opcions.size()) {
                                    System.out.println("  ❌ Opció no vàlida. Tria entre 1 i " + opcions.size());
                                } else {
                                    opcioValida = true;
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("  ❌ Si us plau, introdueix un número vàlid");
                            }
                        }
                        respostes.put(p.getId(), opcions.get(opcioIdx).getText());
                        idPreguntaPerResposta.put(p.getId(), p.getId());
                        
                        break;
                        
                    case QUALITATIVA_NO_ORDENADA_MULTIPLE:
                        List<Opcio> opcionsM = p.getOpcions();
                        for (Opcio o : opcionsM) {
                            System.out.println("  " + o.getId() + ". " + o.getText());
                        }
                        System.out.print("  Escull opcions separades per comes (IDs ex: 0,1,2): ");
                        String opcionsEsc = in.nextLine();
                        
                        respostes.put(p.getId(), opcionsEsc);
                        idPreguntaPerResposta.put(p.getId(), p.getId());
                        break;
                }
            }
            
            cd.contestarEnquesta(enquesta.getId(), respostes, idPreguntaPerResposta);//esto ya lo hace contestarEnquesta 
            System.out.println("\n✓ Enquesta completada! Gràcies per participar.");
            
        } catch (UsuariNoAutenticatException | EnquestaNoExisteixException | EnquestaJaContestadaException | PreguntaNoExisteixException | RespostaInvalidaException | ParametreInvalidException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    
    private static void testModificarResposta() {
        System.out.println("----MODIFICAR RESPOSTA----");
        
        ArrayList<Enquesta> totes = new ArrayList<>();
        try{
         totes = cd.consultarEnquestes();
        } catch (UsuariNoAutenticatException e){
            System.out.println("❌ Error: " + e.getMessage());
        }        if (totes.isEmpty()) {
            System.out.println("No hi ha enquestes al sistema.");
            return;
        }
        
        System.out.println("Enquestes disponibles:");
        for (int i = 0; i < totes.size(); i++) {
            Enquesta e = totes.get(i);
            System.out.println((i + 1) + ". " + e.getTitol() + " (ID: " + e.getId() + ")");
        }
        
        int num = -1;
        boolean numValid = false;
        
        while (!numValid) {
            try {
                System.out.print("\nEscull enquesta (número): ");
                num = Integer.parseInt(in.nextLine()) - 1;
                
                if (num < 0 || num >= totes.size()) {
                    System.out.println("❌ Número no vàlid. Tria un número entre 1 i " + totes.size());
                } else {
                    numValid = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Si us plau, introdueix un número vàlid");
            }
        }
        try {
            Enquesta enquesta = totes.get(num);

            List<Pregunta> preguntes = enquesta.getPreguntes();
            
            if (preguntes.isEmpty()) {
                System.out.println("❌ L'enquesta no té preguntes.");
                return;
            }
            
            System.out.println("\n─── " + enquesta.getTitol() + " ───");
            System.out.println(enquesta.getDescripcio() + "\n");
            
            // 1) Mostrar totes les preguntes amb opcions
            System.out.println("SELECCIONA UNA PREGUNTA PER MODIFICAR LA SEVA RESPOSTA:\n");
            for (int i = 0; i < preguntes.size(); i++) {
                Pregunta p = preguntes.get(i);
                System.out.println((i + 1) + ". " + p.getText() + " (" + p.getTipus() + ")");
                
                // Mostrar opcions si les té
                if (p.getOpcions() != null && !p.getOpcions().isEmpty()) {
                    for (int j = 0; j < p.getOpcions().size(); j++) {
                        System.out.println("   " + (j + 1) + ". " + p.getOpcions().get(j).getText());
                    }
                }
            }
            
            // 2) Seleccionar pregunta
            System.out.print("\nEscull número de pregunta (1-" + preguntes.size() + "): ");
            int seleccio = -1;
            boolean seleccioValida = false;
            
            while (!seleccioValida) {
                try {
                    seleccio = Integer.parseInt(in.nextLine()) - 1;
                    if (seleccio < 0 || seleccio >= preguntes.size()) {
                        System.out.println("❌ Selecció no vàlida. Tria entre 1 i " + preguntes.size());
                        System.out.print("Torna a intentar: ");
                    } else {
                        seleccioValida = true;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("❌ Si us plau, introdueix un número vàlid.");
                    System.out.print("Torna a intentar: ");
                }
            }
            
            Pregunta preguntaSeleccionada = preguntes.get(seleccio);
            String novaResposta = "";
            String idP = preguntaSeleccionada.getId();
            
            // 3) Demanar nova resposta segons el tipus de pregunta
            System.out.println("\n─── NOVA RESPOSTA PARA: " + preguntaSeleccionada.getText() + " ───\n");
            
            switch (preguntaSeleccionada.getTipus()) {
                case TEXT_LLIURE:
                    System.out.print("Introdueix la nova resposta: ");
                    novaResposta = in.nextLine();
                    break;
                    
                case NUMERICA:
                    System.out.print("Introdueix nova resposta numèrica (" + preguntaSeleccionada.getValorMinim() + "-" + preguntaSeleccionada.getValorMaxim() + "): ");
                    novaResposta = in.nextLine();
                    break;
                    
                case QUALITATIVA_ORDENADA:
                case QUALITATIVA_NO_ORDENADA_SIMPLE:
                    List<Opcio> opcions = preguntaSeleccionada.getOpcions();
                    System.out.println("Selecciona una opció:");
                    for (int j = 0; j < opcions.size(); j++) {
                        System.out.println((j + 1) + ". " + opcions.get(j).getText());
                    }
                    
                    int opcioIdx = -1;
                    boolean opcioValida = false;
                    while (!opcioValida) {
                        try {
                            System.out.print("Escull opció (1-" + opcions.size() + "): ");
                            opcioIdx = Integer.parseInt(in.nextLine()) - 1;
                            if (opcioIdx < 0 || opcioIdx >= opcions.size()) {
                                System.out.println("❌ Opció no vàlida. Tria entre 1 i " + opcions.size());
                            } else {
                                opcioValida = true;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("❌ Si us plau, introdueix un número vàlid");
                        }
                    }
                    novaResposta = opcions.get(opcioIdx).getText();
                    break;
                    
                case QUALITATIVA_NO_ORDENADA_MULTIPLE:
                    List<Opcio> opcionsM = preguntaSeleccionada.getOpcions();
                    System.out.println("Selecciona una o més opcions (separades per comes):");
                    for (Opcio o : opcionsM) {
                        System.out.println(o.getId() + ". " + o.getText());
                    }
                    System.out.print("Escull opcions (IDs ex: 1,2,3): ");
                    novaResposta = in.nextLine();
                    break;
            }
             cd.modificarResposta(enquesta.getId(), idP, novaResposta);
            System.out.println("✓ Resposta modificada correctament!");
            
        
        
        } catch (EnquestaNoExisteixException | PreguntaNoExisteixException | RespostaNoExisteixException | RespostaInvalidaException | UsuariNoAutenticatException | ParametreInvalidException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

  
    private static void testEsborrarResposta() {
        System.out.println("----ESBORRAR RESPOSTA----");
        ArrayList<Enquesta> totes = new ArrayList<>();
        try{
         totes = cd.consultarEnquestes();
        } catch (UsuariNoAutenticatException e){
            System.out.println("❌ Error: " + e.getMessage());
        }
        if (totes.isEmpty()) {
            System.out.println("No hi ha enquestes al sistema.");
            return;
        }
        
        System.out.println("Enquestes disponibles:");
        for (int i = 0; i < totes.size(); i++) {
            Enquesta e = totes.get(i);
            System.out.println((i + 1) + ". " + e.getTitol() + " (ID: " + e.getId() + ")");
        }
        
        int num = -1;
        boolean numValid = false;
        
        while (!numValid) {
            try {
                System.out.print("\nEscull enquesta (número): ");
                num = Integer.parseInt(in.nextLine()) - 1;
                
                if (num < 0 || num >= totes.size()) {
                    System.out.println("❌ Número no vàlid. Tria un número entre 1 i " + totes.size());
                } else {
                    numValid = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Si us plau, introdueix un número vàlid");
            }
        }
        try {
            Enquesta enquesta = totes.get(num);
            List<Pregunta> preguntes = enquesta.getPreguntes();
            
            if (preguntes.isEmpty()) {
                System.out.println("❌ L'enquesta no té preguntes.");
                return;
            }
            
            System.out.println("\n─── " + enquesta.getTitol() + " ───");
            System.out.println(enquesta.getDescripcio() + "\n");
            
            // 1) Mostrar totes les preguntes amb opcions
            for (int i = 0; i < preguntes.size(); i++) {
                Pregunta p = preguntes.get(i);
                System.out.println((i + 1) + ". " + p.getText() + " (" + p.getTipus() + ")");
                
                // Mostrar opcions si les té
                if (p.getOpcions() != null && !p.getOpcions().isEmpty()) {
                    for (int j = 0; j < p.getOpcions().size(); j++) {
                        System.out.println("   " + (j + 1) + ". " + p.getOpcions().get(j).getText());
                    }
                }
            }
          
            
            // Aquí podrías mostrar la resposta actual si el controlador ho permet
            // Per ara, mostrem un missatge informatiu
            System.out.println("\n⚠️ Estàs a punt d'esborrar la resposta d'aquesta pregunta.");
            
            // 4) Confirmació final (doble confirmació per esborrats)
            System.out.print("\n¿Estàs segur que vols esborrar aquesta resposta? (S/N): ");
            String confirma = in.nextLine();
            
            if (!confirma.equalsIgnoreCase("S")) {
                System.out.println("ℹ️ Acció cancel·lada.");
                return;
            }
            
            // 5) Esborrar resposta
            cd.esborrarResposta(enquesta.getId());
            System.out.println("✓ Resposta esborrada correctament!");
            
        }  catch (EnquestaNoExisteixException | UsuariNoAutenticatException | RespostaNoExisteixException | ParametreInvalidException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // TODAS LAS RESPUESTAS A ENQUESTAS DEBE EXISTIR TANTO LA ENCUESTA COMO LOS USUARIOS QUE RESPONDEN Y PREGUNTAS
    //SI NO NO IRÁ CORRECTAMENTE
    private static void importarRespostes() {
        System.out.println("----IMPORTAR RESPOSTES DES DE FITXER----");
        // Implementació pendent segons l'especificació del fitxer
        System.out.println("Fitxer d'exemple: exemple_resposta.json");
        System.out.println("Ruta del fitxer JSON (o només el nom si està en el directori actual): ");
        String path = in.nextLine().trim();
        
        // Si solo es un nombre de archivo, añadir la ruta completa
        if (!path.contains("\\") && !path.contains("/")) {
            path = System.getProperty("user.dir") + "\\" + path;
        }
        
        try {
            cd.importarRespostes(path);
            System.out.println("✓ Resposta importada correctament!");
        } catch (ErrorImportacioException e) {
            System.out.println("❌ Error important la resposta: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error inesperat: " + e.getMessage());
        }
        
    }
     /**
     * Consulta todas las respostas de una enquesta agrupadas por usuario.
     */
    private static void testConsultarRespostesEnquesta() {
        System.out.println("\n════ CONSULTAR RESPOSTES D'UNA ENQUESTA ════");
        
        ArrayList<Enquesta> totes = new ArrayList<>();
        try{
         totes = cd.consultarEnquestes();
        } catch (UsuariNoAutenticatException e){
            System.out.println("❌ Error: " + e.getMessage());
        }        if (totes.isEmpty()) {
            System.out.println("❌ No hi ha enquestes al sistema.");
            return;
        }
        
        // 1) Seleccionar enquesta
        System.out.println("\nEnquestes disponibles:");
        for (int i = 0; i < totes.size(); i++) {
            Enquesta e = totes.get(i);
            System.out.println((i + 1) + ". " + e.getTitol() + " (ID: " + e.getId() + ")");
        }
        
        int numEnq = -1;
        boolean numValid = false;
        
        while (!numValid) {
            try {
                System.out.print("\nEscull enquesta (número): ");
                numEnq = Integer.parseInt(in.nextLine()) - 1;
                
                if (numEnq < 0 || numEnq >= totes.size()) {
                    System.out.println("❌ Número no vàlid. Tria un número entre 1 i " + totes.size());
                } else {
                    numValid = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Si us plau, introdueix un número vàlid");
            }
        }
        
        Enquesta enquesta = totes.get(numEnq);
        
        try {
            // 2) Obtenir respostes de l'enquesta (agrupades per usuari)
            HashMap<String, ArrayList<Resposta>> respostesPerPregunta = 
                cd.consultarRespostesEnquesta(enquesta.getId());
            
            if (respostesPerPregunta == null || respostesPerPregunta.isEmpty()) {
                System.out.println("\n❌ No hi ha respostes per aquesta enquesta.");
                return;
            }
            
            // 3) Mostrar respostes agrupades per pregunta
            System.out.println("\n═══════════════════════════════════════");
            System.out.println("RESPOSTES DE L'ENQUESTA: " + enquesta.getTitol());
            System.out.println("ID: " + enquesta.getId());
            System.out.println("═══════════════════════════════════════\n");
            

            for (String idPregunta : respostesPerPregunta.keySet()) {
                System.out.println("── Pregunta ID: " + idPregunta + " ──");
                ArrayList<Resposta> respostes = respostesPerPregunta.get(idPregunta);
                
                for (Resposta r : respostes) {
                    System.out.println("Usuari: " + r.getUsernameUsuari() + " | Resposta: " + r.getTextResposta());
                }
                System.out.println();
            }


                

            
        } catch (Exception e) {
            System.out.println("❌ Error consultant respostes: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
