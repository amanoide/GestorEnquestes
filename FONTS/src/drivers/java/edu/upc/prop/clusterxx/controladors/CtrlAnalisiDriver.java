package edu.upc.prop.clusterxx.controladors;

// Importacions de Java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// Importacions dels stubs de domini
import edu.upc.prop.clusterxx.domini.classes.Enquesta;
import edu.upc.prop.clusterxx.domini.classes.Exceptions.EnquestaNoExisteixException;
import edu.upc.prop.clusterxx.domini.classes.Exceptions.ErrorImportacioException;
import edu.upc.prop.clusterxx.domini.classes.Exceptions.ParametreInvalidException;
import edu.upc.prop.clusterxx.domini.classes.Exceptions.UsuariNoAutenticatException;
import edu.upc.prop.clusterxx.domini.classes.Kluster;
import edu.upc.prop.clusterxx.domini.classes.Perfil;
import edu.upc.prop.clusterxx.domini.classes.Pregunta;
import edu.upc.prop.clusterxx.domini.classes.Resposta;
import edu.upc.prop.clusterxx.domini.classes.Usuari;
// Importacions dels controladors
import edu.upc.prop.clusterxx.domini.controladors.CtrlDomini;
import edu.upc.prop.clusterxx.domini.controladors.CtrlAnalisi;

/**
 * Driver per provar la classe CtrlAnalisi.
 * Aquest driver depèn dels STUBS de KMeans i ClusteringAlgorithm.
 */
public class CtrlAnalisiDriver {

    private static Scanner in;
    private static CtrlDomini cd;
    private static Usuari admin;


    public static void main(String[] args) {
        init();
        System.out.println("Driver de testeig de CtrlAnalisi");

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
        cd = new CtrlDomini();
        admin = new Usuari("USER_MOCK", "1234");
        Usuari.login(admin);
    }

    private static void mostra_metodes() {
        System.out.println("\n--- Menú CtrlAnalisi ---");
        System.out.println("------------------------");
        System.out.println("(1) Importar enquesta");
        System.out.println("(2) Importar Respostes");
        System.out.println("(3) Analitzar enquesta");
        System.out.println("(4) Veure el meu perfil");
        System.out.println("(0|sortir) - Tancar driver");
        System.out.println("Escull una opció: ");
    }

    private static void gestionarEntrada(String input) throws Exception {
        switch (input) {
            case "1":
                testImportarEnquesta();
                break;
            case "2":
                importarRespostes();
                break;
            case "3":
                analitzarEnquesta();
                break;
            case "4":
                veureMeuPerfil();
                break;

            case "0":
            case "sortir":
                break;
            default:
                System.out.println("Valor invàlid");
                break;
        }
    }


    // --- Mètodes Auxiliars ---

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

    private static void analitzarEnquesta() {
        System.out.println("\n═══ ANALITZAR ENQUESTA AMB CLUSTERING ═══");
        
        try {
            ArrayList<Enquesta> totes = cd.consultarEnquestes();
        if (totes.isEmpty()) {
            System.out.println("No hi ha enquestes al sistema.");
            return;
        }
        
        System.out.println("Enquestes disponibles:");
        for (int i = 0; i < totes.size(); i++) {
            Enquesta e = totes.get(i);
            System.out.println((i + 1) + ". " + e.getTitol() + " (ID: " + e.getId() + ") - Participants: " + e.getParticipants().size());
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
        
        Enquesta enquesta = totes.get(num);
        
        try {
            // Verificar si la enquesta tiene respuestas
            HashMap<String, ArrayList<Resposta>> respostesPerUsuari = cd.consultarRespostesEnquesta(enquesta.getId());
            
            if (respostesPerUsuari.isEmpty()) {
                System.out.println("\n⚠ Aquesta enquesta no té respostes. Necessites almenys 2 participants per fer clustering.");
                return;
            }
            
            if (respostesPerUsuari.size() < 2) {
                System.out.println("\n⚠ Necessites almenys 2 participants per fer clustering. Aquesta enquesta només té " + respostesPerUsuari.size() + " participant.");
                return;
            }
            
            // Preguntar cómo escoger k
            System.out.println("\n¿Com vols escollir el nombre de clusters (k)?");
            System.out.println("  1. Manual (tu esculls k)");
            System.out.println("  2. Aleatori (k entre 2 i √n)");
            System.out.println("  3. Automàtic (millor k segons Silhouette)");
            System.out.print("Escull (1-3): ");
            String kOpcio = in.nextLine();
            
            int k = -1;
            int nParticipants = respostesPerUsuari.size();
            
            switch (kOpcio) {
                case "1": // Manual
                    while (k < 2 || k > nParticipants) {
                        try {
                            System.out.print("\nQuants grups (clusters) vols crear? (2-" + nParticipants + "): ");
                            k = Integer.parseInt(in.nextLine());
                            if (k < 2) {
                                System.out.println("❌ Necessites almenys 2 clusters");
                            } else if (k > nParticipants) {
                                System.out.println("❌ No pots tenir més clusters que participants (" + nParticipants + ")");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("❌ Si us plau, introdueix un número vàlid");
                        }
                    }
                    break;
                    
                case "2": // Aleatorio - delegar a CtrlDomini
                    k = cd.escollirKAleatori(enquesta.getId());
                    System.out.println("✓ k escollit aleatòriament: " + k);
                    break;
                    
                case "3": // Automático - delegar a CtrlDomini
                    System.out.println("✓ S'avaluaran diferents valors de k per trobar l'òptim...");
                    break;
                    
                default:
                    System.out.println("⚠️ Opció no vàlida, s'usarà k=3 per defecte");
                    k = 3;
            }
            
            // Preguntar qué algoritmo usar
            System.out.println("\nAlgoritme de clustering:");
            System.out.println("  1. KMeans (inicialització aleatòria)");
            System.out.println("  2. KMeans++ (inicialització intel·ligent - recomanat)");
            System.out.println("  3. KMedoids (medoides reals - robust a outliers)");
            System.out.print("Escull (1-3): ");
            String algOpcio = in.nextLine();
            
            boolean usePlusPlus = algOpcio.equals("2");
            String algoritmeNom;
            switch (algOpcio) {
                case "2":
                    algoritmeNom = "KMeans++";
                    break;
                case "3":
                    algoritmeNom = "KMedoids";
                    break;
                case "1":
                default:
                    algoritmeNom = "KMeans";
                    break;
            }
            
            // Si es automático, buscar el mejor k
            if (kOpcio.equals("3")) {
                System.out.println("\n⏳ Avaluant diferents valors de k...");
                
                // Obtener rango sugerido usando CtrlDomini
                int[] range = cd.suggestKRange(nParticipants);
                int kMin = range[0];
                int kMax = range[1];
                
                // Buscar k óptimo usando CtrlDomini
                CtrlAnalisi.OptimalKResult result = cd.trobarMillorK(
                    enquesta.getId(), 
                    kMin, 
                    kMax, 
                    algoritmeNom, 
                    100
                );
                
                // Mostrar resultados de la evaluación
                for (int kTest = kMin; kTest <= kMax; kTest++) {
                    System.out.printf("  k=%d → Silhouette=%.3f%n", kTest, result.getSilhouetteForK(kTest));
                }
                
                k = result.bestK;
                System.out.println("\n✓ Millor k trobat: " + k + " (Silhouette=" + String.format("%.3f", result.bestSilhouette) + ")");
            }
            
            System.out.println("\n⏳ Analitzant respostes amb k=" + k + "...");
            
            // Delegar todo el clustering al CtrlDomini
            CtrlDomini.ResultatClustering resultat = cd.analitzarEnquesta(
                enquesta.getId(), 
                k, 
                usePlusPlus, 
                100, 
                algoritmeNom
            );
            
            // Mostrar resultados
            System.out.println("\n╔══════════════════════════════════════════════╗");
            System.out.println("║      RESULTATS DEL CLUSTERING               ║");
            System.out.println("╚══════════════════════════════════════════════╝");
            System.out.println("\n📊 Algoritme: " + algoritmeNom);
            System.out.println("📊 Nombre de clusters: " + k);
            System.out.println("📊 Participants analitzats: " + resultat.usernames.size());
            System.out.printf("📊 Coeficient Silhouette global: %.3f%n", resultat.silhouetteGlobal);
            
            // Interpretación del Silhouette
            System.out.print("   Qualitat: ");
            double silhouette = resultat.silhouetteGlobal;
            if (silhouette >= 0.7) {
                System.out.println("Excel·lent ✓✓✓ (clusters ben separats i compactes)");
            } else if (silhouette >= 0.5) {
                System.out.println("Bona ✓✓ (estructura de clusters clara)");
            } else if (silhouette >= 0.25) {
                System.out.println("Acceptable ✓ (estructura present però amb superposició)");
            } else if (silhouette >= 0) {
                System.out.println("Pobra (clusters poc definits)");
            } else {
                System.out.println("Molt pobra (molts punts mal assignats)");
            }
            
            // Mostrar cada cluster
            List<Pregunta> preguntes = enquesta.getPreguntes();
            for (int i = 0; i < resultat.clusters.size(); i++) {
                Kluster cluster = resultat.clusters.get(i);
                List<String[]> members = cluster.getMembers();
                String[] centroid = cluster.getCentroid();
                
                System.out.println("\n┌─ CLUSTER " + (i + 1) + " ─┐");
                System.out.println("│ Mida: " + members.size() + " participants");
                System.out.printf("│ Silhouette: %.3f%n", resultat.silhouettePerCluster[i]);
                
                // Mostrar centroide (perfil característic del cluster)
                System.out.println("│ Perfil característic:");
                for (int j = 0; j < preguntes.size(); j++) {
                    Pregunta p = preguntes.get(j);
                    System.out.println("│   " + p.getText() + ": " + centroid[j]);
                }
                
                // Mostrar miembros del cluster
                System.out.println("│ Membres:");
                for (String[] memberVector : members) {
                    // Buscar el índice usando el contenido del vector
                    String vectorKey = String.join("|", memberVector);
                    Integer memberIdx = resultat.vectorToIndex.get(vectorKey);
                    
                    if (memberIdx != null && memberIdx < resultat.usernames.size()) {
                        System.out.println("│   - " + resultat.usernames.get(memberIdx));
                    }
                }
                System.out.println("└" + "─".repeat(50) + "┘");
            }
            
            System.out.println("\n✓ Anàlisi completada i perfils assignats!");
            System.out.println("\n💡 Interpretació:");
            System.out.println("   - Cada cluster representa un grup d'usuaris amb respostes similars");
            System.out.println("   - El 'Perfil característic' mostra la resposta típica del grup");
            System.out.println("   - Els perfils s'han guardat per a cada usuari");
            System.out.println("   - Usa l'opció 12 per veure el teu perfil");
            
        } catch (ParametreInvalidException | EnquestaNoExisteixException | UsuariNoAutenticatException e) {
            System.out.println("❌ Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error durant l'anàlisi: " + e.getMessage());
            e.printStackTrace();
        }
        } catch (UsuariNoAutenticatException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private static void veureMeuPerfil() {
        System.out.println("\n═══ EL MEU PERFIL ═══");
        
        HashMap<String, Perfil> perfils = admin.getPerfils();
        
        if (perfils.isEmpty()) {
            System.out.println("⚠ Encara no tens cap perfil assignat.");
            System.out.println("  Els perfils es generen quan s'analitza una enquesta que has respost.");
            System.out.println("  Usa l'opció 11 per analitzar una enquesta.");
            return;
        }
        
        System.out.println("Tens " + perfils.size() + " perfil(s) assignat(s):\n");
        
        int i = 1;
        for (Map.Entry<String, Perfil> entry : perfils.entrySet()) {
            Perfil perfil = entry.getValue();
            
            System.out.println("═══ PERFIL " + i + " ═══");
            System.out.println(perfil.getPerfilLlegible());
            System.out.println();
            i++;
        }
    }
}