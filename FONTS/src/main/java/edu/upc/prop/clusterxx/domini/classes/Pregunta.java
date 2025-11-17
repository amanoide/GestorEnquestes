package edu.upc.prop.clusterxx.domini.classes;

import java.util.ArrayList;
import java.util.HashMap;


public class Pregunta {
    private String id;
    private String text;
    private TipusPregunta tipus; // Tipus de pregunta
    private ArrayList<Opcio> opcions; // Per a preguntes qualitatives
    private int maxSeleccions; // Per a preguntes qualitatives múltiples (q)
    private Double valorMinim; // Per a preguntes numèriques (rang permès)
    private Double valorMaxim; // Per a preguntes numèriques (rang permès)
    private Double valorMesAlt; // Per a preguntes numèriques (valor més alt entre les respostes)
    private Double valorMesBaix; // Per a preguntes numèriques (valor més baix entre les respostes)
    private HashMap<String, Resposta> respostes; // username -> Resposta (COMPOSICIÓ)

    /**
     * Constructor per a preguntes de text lliure.
     */
    public Pregunta(String id, String text) {
        this.id = id;
        this.text = text;
        this.tipus = TipusPregunta.TEXT_LLIURE;
        this.opcions = new ArrayList<>();
        this.respostes = new HashMap<>();
    }

    /**
     * Constructor per a preguntes numèriques amb rang.
     */
    public Pregunta(String id, String text, Double min, Double max) {
        this.id = id;
        this.text = text;
        this.tipus = TipusPregunta.NUMERICA;
        this.valorMinim = min;
        this.valorMaxim = max;
        this.opcions = new ArrayList<>();
        this.respostes = new HashMap<>();
    }

    /**
     * Constructor per a preguntes qualitatives.
     */
    public Pregunta(String id, String text, TipusPregunta tipus, int maxSeleccions) {
        this.id = id;
        this.text = text;
        this.tipus = tipus;
        this.maxSeleccions = maxSeleccions;
        this.opcions = new ArrayList<>();
        this.respostes = new HashMap<>();
    }

    /**
     * Constructor genèric (per compatibilitat amb codi existent).
     */
    public Pregunta(String id, String text, String tipusStr) {
        this.id = id;
        this.text = text;
        this.opcions = new ArrayList<>();
        this.respostes = new HashMap<>();
        
        // Mapear string a TipusPregunta
        switch (tipusStr.toLowerCase()) {
            case "numerica":
                this.tipus = TipusPregunta.NUMERICA;
                break;
            case "qualitativa_ordenada":
            case "ordenada":
                this.tipus = TipusPregunta.QUALITATIVA_ORDENADA;
                break;
            case "qualitativa_simple":
                this.tipus = TipusPregunta.QUALITATIVA_NO_ORDENADA_SIMPLE;
                break;
            case "qualitativa_multiple":
            case "multiple":
                this.tipus = TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE;
                this.maxSeleccions = 3; // Per defecte
                break;
            case "text":
            default:
                this.tipus = TipusPregunta.TEXT_LLIURE;
                break;
        }
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public TipusPregunta getTipus() {
        return tipus;
    }

    public ArrayList<Opcio> getOpcions() {
        return new ArrayList<>(opcions);
    }

    public int getMaxSeleccions() {
        return maxSeleccions;
    }

    public Double getValorMinim() {
        return valorMinim;
    }

    public Double getValorMaxim() {
        return valorMaxim;
    }
    
    public Double getValorMesAlt() {
        return valorMesAlt;
    }
    
    public Double getValorMesBaix() {
        return valorMesBaix;
    }

    // Setters
    public void setText(String text) {
        this.text = text;
    }

    public void setMaxSeleccions(int max) {
        this.maxSeleccions = max;
    }

    public void setRangNumeric(Double min, Double max) {
        if (this.tipus == TipusPregunta.NUMERICA) {
            this.valorMinim = min;
            this.valorMaxim = max;
        }
    }

    // Gestió d'opcions
    public void afegirOpcio(Opcio opcio) {
        if (tipusAdmetOpcions()) {
            // Per preguntes ordenades, verificar que no hi hagi ordre duplicat
            if (tipus == TipusPregunta.QUALITATIVA_ORDENADA) {
                for (Opcio o : opcions) {
                    if (o.getOrdre() == opcio.getOrdre()) {
                        throw new IllegalArgumentException("Ja existeix una opció amb l'ordre " + opcio.getOrdre());
                    }
                }
            }
            opcions.add(opcio);
        }
    }

    public void eliminarOpcio(int idOpcio) {
        opcions.removeIf(o -> o.getId() == idOpcio);
    }

    public Opcio getOpcio(int idOpcio) {
        for (Opcio o : opcions) {
            if (o.getId() == idOpcio) {
                return o;
            }
        }
        return null;
    }

    /**
     * Comprova si aquest tipus de pregunta admet opcions predefinides.
     */
    public boolean tipusAdmetOpcions() {
        return tipus == TipusPregunta.QUALITATIVA_ORDENADA ||
               tipus == TipusPregunta.QUALITATIVA_NO_ORDENADA_SIMPLE ||
               tipus == TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE;
    }

    /**
     * Valida una resposta segons el tipus de pregunta.
     * @param resposta La resposta a validar
     * @return true si és vàlida, false altrament
     */
    public boolean validarResposta(String resposta) {
        switch (tipus) {
            case NUMERICA:
                try {
                    Double valor = Double.parseDouble(resposta);
                    if (valorMinim != null && valor < valorMinim) return false;
                    if (valorMaxim != null && valor > valorMaxim) return false;
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
                
            case QUALITATIVA_ORDENADA:
            case QUALITATIVA_NO_ORDENADA_SIMPLE:
                // Comprovar que la resposta és una de les opcions
                for (Opcio o : opcions) {
                    if (o.getText().equals(resposta)) {
                        return true;
                    }
                }
                return false;
                
            case QUALITATIVA_NO_ORDENADA_MULTIPLE:
                // Format esperat: "id1,id2,id3" o "text1,text2,text3"
                String[] seleccions = resposta.split(",");
                if (seleccions.length > maxSeleccions) return false;
                
                for (String sel : seleccions) {
                    String selTrimmed = sel.trim();
                    boolean trobada = false;
                    
                    // Intentar primer com a ID numèric
                    try {
                        int id = Integer.parseInt(selTrimmed);
                        for (Opcio o : opcions) {
                            if (o.getId() == id) {
                                trobada = true;
                                break;
                            }
                        }
                    } catch (NumberFormatException e) {
                        // No és un número, buscar per text
                        for (Opcio o : opcions) {
                            if (o.getText().equals(selTrimmed)) {
                                trobada = true;
                                break;
                            }
                        }
                    }
                    
                    if (!trobada) return false;
                }
                return true;
                
            case TEXT_LLIURE:
                return resposta != null && !resposta.trim().isEmpty();
                
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pregunta{id='").append(id)
          .append("', text='").append(text)
          .append("', tipus=").append(tipus);
        
        if (tipus == TipusPregunta.NUMERICA && valorMinim != null && valorMaxim != null) {
            sb.append(", rang=[").append(valorMinim).append("-").append(valorMaxim).append("]");
        }
        
        if (tipusAdmetOpcions() && !opcions.isEmpty()) {
            sb.append(", opcions=").append(opcions.size());
            if (tipus == TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE) {
                sb.append(", max_seleccions=").append(maxSeleccions);
            }
        }
        
        sb.append("}");
        return sb.toString();
    }

    // ===========================================
    // GESTIÓ DE RESPOSTES
    // ===========================================

    /**
     * Afegeix o actualitza una resposta d'un usuari a aquesta pregunta.
     * @param username Username de l'usuari
     * @param resposta La resposta
     */
    public void afegirResposta(String username, Resposta resposta) {
        respostes.put(username, resposta);
        
        // Si és una pregunta numèrica, actualitzar els valors màxim i mínim
        if (this.tipus == TipusPregunta.NUMERICA && resposta != null) {
            try {
                Double valor = Double.parseDouble(resposta.getTextResposta());
                
                if (valorMesAlt == null || valor > valorMesAlt) {
                    valorMesAlt = valor;
                }
                
                if (valorMesBaix == null || valor < valorMesBaix) {
                    valorMesBaix = valor;
                }
            } catch (NumberFormatException e) {
                // Si la resposta no és un número vàlid, ignorem l'actualització
            }
        }
    }

    /**
     * Elimina la resposta d'un usuari a aquesta pregunta.
     * @param username Username de l'usuari
     * @return La resposta eliminada o null si no existia
     */
    public Resposta eliminarResposta(String username) {
        Resposta eliminada = respostes.remove(username);
        
        // Si és una pregunta numèrica, recalcular els valors màxim i mínim
        if (this.tipus == TipusPregunta.NUMERICA && eliminada != null) {
            recalcularValorsNumerics();
        }
        
        return eliminada;
    }
    
    /**
     * Recalcula els valors màxim i mínim de les respostes numèriques.
     */
    private void recalcularValorsNumerics() {
        valorMesAlt = null;
        valorMesBaix = null;
        
        for (Resposta r : respostes.values()) {
            try {
                Double valor = Double.parseDouble(r.getTextResposta());
                
                if (valorMesAlt == null || valor > valorMesAlt) {
                    valorMesAlt = valor;
                }
                
                if (valorMesBaix == null || valor < valorMesBaix) {
                    valorMesBaix = valor;
                }
            } catch (NumberFormatException e) {
                // Ignorar respostes no numèriques
            }
        }
    }

    /**
     * Obté la resposta d'un usuari a aquesta pregunta.
     * @param username Username de l'usuari
     * @return La resposta o null si no existeix
     */
    public Resposta getResposta(String username) {
        return respostes.get(username);
    }

    /**
     * Obté totes les respostes a aquesta pregunta.
     * @return HashMap de respostes (username -> Resposta)
     */
    public HashMap<String, Resposta> getRespostes() {
        return new HashMap<>(respostes);
    }

    /**
     * Obté el nombre de respostes a aquesta pregunta.
     * @return Nombre d'usuaris que han respost aquesta pregunta
     */
    public int getNumRespostes() {
        return respostes.size();
    }

    /**
     * Comprova si un usuari ha respost aquesta pregunta.
     * @param username Username de l'usuari
     * @return true si l'usuari ha respost, false altrament
     */
    public boolean teResposta(String username) {
        return respostes.containsKey(username);
    }
}
