package edu.upc.prop.clusterxx.domini.classes;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe que representa un perfil d'usuari basat en clustering.
 * Conté informació sobre el cluster al qual pertany l'usuari i les seves
 * característiques.
 * 
 * @author ClusterXX
 */
public class Perfil {
    /** Identificador únic del perfil */
    private Integer id;
    /** Descripció del tipus de perfil per al clustering */
    private String descripcion;
    /** ID de l'enquesta sobre la qual es va fer l'anàlisi */
    private String idEnquesta;
    /** Índex del cluster al qual pertany aquest perfil (0, 1, 2, ...) */
    private Integer clusterIndex;
    /**
     * Nom del cluster (per exemple: "Estudiants dedicats", "Usuaris insatisfets")
     */
    private String clusterNom;
    /** Mida del cluster (nombre de membres) */
    private Integer clusterMida;
    /** Coeficient Silhouette del cluster (qualitat del cluster) */
    private Double clusterSilhouette;
    /** Vector característic del perfil (centroide del cluster) */
    private String[] vectorCaracteristic;
    /** Noms de les preguntes (per interpretar el vector característic) */
    private List<String> nomsPreguntes;
    /** Algoritme utilitzat per generar el perfil (KMeans, KMeans++, etc.) */
    private String algoritme;

    /**
     * Constructor bàsic de la classe Perfil (mantingut per compatibilitat)
     * 
     * @param id          Identificador únic del perfil
     * @param descripcion Descripció del perfil
     */
    public Perfil(Integer id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
        this.nomsPreguntes = new ArrayList<>();
    }

    /**
     * Constructor complet per crear un perfil amb informació de clustering
     * 
     * @param id                  Identificador únic del perfil
     * @param descripcion         Descripció del perfil
     * @param idEnquesta          ID de l'enquesta analitzada
     * @param clusterIndex        Índex del cluster
     * @param clusterNom          Nom descriptiu del cluster
     * @param clusterMida         Nombre de membres del cluster
     * @param clusterSilhouette   Coeficient Silhouette del cluster
     * @param vectorCaracteristic Vector característic (centroide)
     * @param nomsPreguntes       Noms de les preguntes
     * @param algoritme           Algoritme utilitzat
     */
    public Perfil(Integer id, String descripcion, String idEnquesta, Integer clusterIndex,
            String clusterNom, Integer clusterMida, Double clusterSilhouette,
            String[] vectorCaracteristic, List<String> nomsPreguntes, String algoritme) {
        this.id = id;
        this.descripcion = descripcion;
        this.idEnquesta = idEnquesta;
        this.clusterIndex = clusterIndex;
        this.clusterNom = clusterNom;
        this.clusterMida = clusterMida;
        this.clusterSilhouette = clusterSilhouette;
        this.vectorCaracteristic = vectorCaracteristic;
        this.nomsPreguntes = new ArrayList<>(nomsPreguntes);
        this.algoritme = algoritme;
    }

    // ========== Getters per a persistència ==========
    
    /**
     * Retorna l'identificador únic del perfil.
     * 
     * @return ID del perfil
     */
    public Integer getId() {
        return id;
    }

    /**
     * Retorna la descripció del perfil.
     * 
     * @return Descripció textual del perfil
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Retorna l'ID de l'enquesta sobre la qual es va fer l'anàlisi.
     * 
     * @return ID de l'enquesta
     */
    public String getIdEnquesta() {
        return idEnquesta;
    }

    /**
     * Retorna l'índex del cluster al qual pertany aquest perfil.
     * 
     * @return Índex del cluster (0, 1, 2, ...)
     */
    public Integer getClusterIndex() {
        return clusterIndex;
    }

    /**
     * Retorna el nom descriptiu del cluster.
     * 
     * @return Nom del cluster
     */
    public String getClusterNom() {
        return clusterNom;
    }

    /**
     * Retorna la mida del cluster (nombre de membres).
     * 
     * @return Nombre de membres del cluster
     */
    public Integer getClusterMida() {
        return clusterMida;
    }

    /**
     * Retorna el coeficient Silhouette del cluster.
     * 
     * @return Valor Silhouette (qualitat del cluster)
     */
    public Double getClusterSilhouette() {
        return clusterSilhouette;
    }

    /**
     * Retorna el vector característic del perfil (centroide del cluster).
     * 
     * @return Array de strings amb els valors característics
     */
    public String[] getVectorCaracteristic() {
        return vectorCaracteristic;
    }

    /**
     * Retorna una còpia de la llista de noms de preguntes.
     * 
     * @return Llista amb els noms de les preguntes
     */
    public List<String> getNomsPreguntes() {
        return new ArrayList<>(nomsPreguntes);
    }

    /**
     * Retorna l'algoritme utilitzat per generar el perfil.
     * 
     * @return Nom de l'algoritme (KMeans, KMeans++, KMedoids)
     */
    public String getAlgoritme() {
        return algoritme;
    }

    // ========== Mètodes Útils ==========

    /**
     * Verifica si aquest perfil té informació de clustering
     * 
     * @return true si el perfil té dades de clustering, false altrament
     */
    public boolean teClustering() {
        return clusterIndex != null && vectorCaracteristic != null;
    }

    /**
     * Obté una representació llegible del perfil característic
     * 
     * @return String amb el format "Pregunta: Resposta" per cada dimensió
     */
    public String getPerfilLlegible() {
        if (!teClustering()) {
            return "Perfil sense dades de clustering";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Perfil del Cluster ").append(clusterIndex + 1).append(": ").append(clusterNom).append("\n");
        sb.append("Enquesta: ").append(idEnquesta).append("\n");
        sb.append("Algoritme: ").append(algoritme).append("\n");
        sb.append("Mida del cluster: ").append(clusterMida).append(" membres\n");
        sb.append("Qualitat (Silhouette): ").append(String.format("%.3f", clusterSilhouette)).append("\n\n");
        sb.append("Característiques:\n");

        for (int i = 0; i < vectorCaracteristic.length && i < nomsPreguntes.size(); i++) {
            sb.append("  - ").append(nomsPreguntes.get(i))
                    .append(": ").append(vectorCaracteristic[i]).append("\n");
        }

        return sb.toString();
    }

    /**
     * Obté la qualitat del cluster en format text
     * 
     * @return Descripció de la qualitat basada en el coeficient Silhouette
     */
    public String getQualitatText() {
        if (clusterSilhouette == null)
            return "Desconeguda";

        if (clusterSilhouette >= 0.7)
            return "Excel·lent";
        else if (clusterSilhouette >= 0.5)
            return "Bona";
        else if (clusterSilhouette >= 0.25)
            return "Acceptable";
        else if (clusterSilhouette >= 0)
            return "Pobra";
        else
            return "Molt pobra";
    }

    @Override
    public String toString() {
        if (teClustering()) {
            return "Perfil{id=" + id + ", cluster=" + clusterNom + ", mida=" + clusterMida +
                    ", silhouette=" + String.format("%.3f", clusterSilhouette) + "}";
        } else {
            return "Perfil{id=" + id + ", descripcion='" + descripcion + "'}";
        }
    }
}
