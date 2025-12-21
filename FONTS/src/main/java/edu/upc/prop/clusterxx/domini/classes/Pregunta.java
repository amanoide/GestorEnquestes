package edu.upc.prop.clusterxx.domini.classes;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Representa una pregunta dins d'una enquesta.
 * <p>
 * Aquesta classe encapsula tota la informació d'una pregunta, incloent-hi el
 * seu identificador,
 * el text, el tipus de pregunta, i les restriccions associades (com opcions per
 * a preguntes
 * qualitatives o rangs per a numèriques). També gestiona la col·lecció de
 * respostes
 * donades pels usuaris, actuant com a agregat per a les entitats
 * {@link Resposta}.
 * </p>
 *
 * @see Enquesta
 * @see TipusPregunta
 * @see Opcio
 * @see Resposta
 */
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
     *
     * @param id   L'identificador únic de la pregunta.
     * @param text El text de la pregunta.
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
     *
     * @param id   L'identificador únic de la pregunta.
     * @param text El text de la pregunta.
     * @param min  El valor mínim permès per a la resposta (inclusiu).
     * @param max  El valor màxim permès per a la resposta (inclusiu).
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
     *
     * @param id            L'identificador únic de la pregunta.
     * @param text          El text de la pregunta.
     * @param tipus         El tipus de pregunta qualitativa (ordenada, simple o
     *                      múltiple).
     * @param maxSeleccions El nombre màxim de seleccions per a preguntes de
     *                      resposta múltiple.
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
     * Constructor genèric per a compatibilitat amb la importació des de fitxers.
     * Converteix un tipus de pregunta en format String al seu corresponent valor
     * d'enum {@link TipusPregunta}.
     *
     * @param id       L'identificador únic de la pregunta.
     * @param text     El text de la pregunta.
     * @param tipusStr La representació en String del tipus de pregunta.
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
            case "qualitativa_no_ordenada_simple":
                this.tipus = TipusPregunta.QUALITATIVA_NO_ORDENADA_SIMPLE;
                break;
            case "qualitativa_multiple":
            case "multiple":
            case "qualitativa_no_ordenada_multiple":
                this.tipus = TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE;
                this.maxSeleccions = 3; // Per defecte
                break;
            case "text":
            case "text_lliure":
            default:
                this.tipus = TipusPregunta.TEXT_LLIURE;
                break;
        }
    }

    // Getters
    /**
     * Retorna l'identificador únic de la pregunta.
     * 
     * @return L'ID de la pregunta.
     */
    public String getId() {
        return id;
    }

    /**
     * Retorna el text de la pregunta.
     * 
     * @return El text de la pregunta.
     */
    public String getText() {
        return text;
    }

    /**
     * Retorna el tipus de la pregunta.
     * 
     * @return L'enum {@link TipusPregunta} que defineix el tipus.
     */
    public TipusPregunta getTipus() {
        return tipus;
    }

    /**
     * Retorna una còpia de la llista d'opcions per a preguntes qualitatives.
     * 
     * @return Un {@code ArrayList} amb les opcions.
     */
    public ArrayList<Opcio> getOpcions() {
        return new ArrayList<>(opcions);
    }

    /**
     * Retorna el nombre màxim de seleccions permeses per a preguntes de resposta
     * múltiple.
     * 
     * @return El nombre màxim de seleccions.
     */
    public int getMaxSeleccions() {
        return maxSeleccions;
    }

    /**
     * Retorna el valor mínim del rang per a preguntes numèriques.
     * 
     * @return El valor mínim permès.
     */
    public Double getValorMinim() {
        return valorMinim;
    }

    /**
     * Retorna el valor màxim del rang per a preguntes numèriques.
     * 
     * @return El valor màxim permès.
     */
    public Double getValorMaxim() {
        return valorMaxim;
    }

    /**
     * Retorna el valor més alt registrat entre totes les respostes numèriques a
     * aquesta pregunta.
     * 
     * @return El valor més alt, o null si no hi ha respostes numèriques.
     */
    public Double getValorMesAlt() {
        return valorMesAlt;
    }

    /**
     * Retorna el valor més baix registrat entre totes les respostes numèriques a
     * aquesta pregunta.
     * 
     * @return El valor més baix, o null si no hi ha respostes numèriques.
     */
    public Double getValorMesBaix() {
        return valorMesBaix;
    }

    // Setters
    /**
     * Estableix un nou text per a la pregunta.
     * 
     * @param text El nou text.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Estableix el nombre màxim de seleccions per a preguntes de resposta múltiple.
     * 
     * @param max El nou límit de seleccions.
     */
    public void setMaxSeleccions(int max) {
        this.maxSeleccions = max;
    }

    /**
     * Estableix el rang de valors per a una pregunta numèrica.
     * Aquest mètode només té efecte si la pregunta és de tipus
     * {@code TipusPregunta.NUMERICA}.
     * 
     * @param min El nou valor mínim.
     * @param max El nou valor màxim.
     */
    public void setRangNumeric(Double min, Double max) {
        if (this.tipus == TipusPregunta.NUMERICA) {
            this.valorMinim = min;
            this.valorMaxim = max;
        }
    }

    // Gestió d'opcions
    /**
     * Afegeix una opció a la pregunta, si el seu tipus ho permet.
     * Per a preguntes ordenades, llança una excepció si ja existeix una opció amb
     * el mateix ordre.
     * 
     * @param opcio L'objecte {@link Opcio} a afegir.
     * @throws IllegalArgumentException si es duplica l'ordre en una pregunta
     *                                  ordenada.
     */
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

    /**
     * Elimina una opció de la pregunta a partir del seu ID.
     * 
     * @param idOpcio L'ID de l'opció a eliminar.
     */
    public void eliminarOpcio(int idOpcio) {
        opcions.removeIf(o -> o.getId() == idOpcio);
    }

    /**
     * Obté una opció específica a partir del seu ID.
     * 
     * @param idOpcio L'ID de l'opció a buscar.
     * @return L'objecte {@link Opcio} si es troba, altrament {@code null}.
     */
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
     * 
     * @return {@code true} si és una pregunta qualitativa, {@code false} en cas
     *         contrari.
     */
    public boolean tipusAdmetOpcions() {
        return tipus == TipusPregunta.QUALITATIVA_ORDENADA ||
                tipus == TipusPregunta.QUALITATIVA_NO_ORDENADA_SIMPLE ||
                tipus == TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE;
    }

    /**
     * Valida si una resposta donada és consistent amb el tipus i les restriccions
     * de la pregunta.
     * <p>
     * <b>Lògica de validació:</b>
     * </p>
     * <ul>
     * <li><b>NUMERICA:</b> Comprova si la resposta és un número i està dins del
     * rang [min, max].</li>
     * <li><b>QUALITATIVA_SIMPLE/ORDENADA:</b> Comprova si la resposta coincideix
     * amb el text d'una de les opcions.</li>
     * <li><b>QUALITATIVA_MULTIPLE:</b> Comprova si la resposta (separada per comes)
     * conté un nombre vàlid de seleccions i si cada selecció correspon a una opció
     * (per ID o text).</li>
     * <li><b>TEXT_LLIURE:</b> Comprova que la resposta no sigui nul·la ni
     * buida.</li>
     * </ul>
     * 
     * @param resposta La resposta en format String a validar.
     * @return {@code true} si la resposta és vàlida, {@code false} en cas contrari.
     */
    public boolean validarResposta(String resposta) {
        switch (tipus) {
            case NUMERICA:
                try {
                    Double valor = Double.parseDouble(resposta);
                    if (valorMinim != null && valor < valorMinim)
                        return false;
                    if (valorMaxim != null && valor > valorMaxim)
                        return false;
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
                if (seleccions.length > maxSeleccions)
                    return false;

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

                    if (!trobada)
                        return false;
                }
                return true;

            case TEXT_LLIURE:
                return resposta != null && !resposta.trim().isEmpty();

            default:
                return false;
        }
    }

    /**
     * Retorna una representació en format String de l'objecte Pregunta.
     * 
     * @return Una cadena de text que representa la pregunta.
     */
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
     * Afegeix o actualitza la resposta d'un usuari a aquesta pregunta.
     * Si la pregunta és numèrica, actualitza els valors extrems (més alt i més
     * baix) registrats.
     * 
     * @param username El nom de l'usuari que respon.
     * @param resposta L'objecte {@link Resposta} a afegir.
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
     * Si la pregunta és numèrica, recalcula els valors extrems després de
     * l'eliminació.
     * 
     * @param username El nom de l'usuari la resposta del qual s'eliminarà.
     * @return La resposta eliminada, o {@code null} si l'usuari no havia respost.
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
     * Aquest mètode privat s'invoca quan s'elimina una resposta numèrica per
     * mantenir
     * la consistència dels valors extrems.
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
     * Obté la resposta d'un usuari específic a aquesta pregunta.
     * 
     * @param username El nom de l'usuari.
     * @return L'objecte {@link Resposta}, o {@code null} si no ha respost.
     */
    public Resposta getResposta(String username) {
        return respostes.get(username);
    }

    /**
     * Obté totes les respostes a aquesta pregunta.
     * 
     * @return Un {@code HashMap} que mapeja noms d'usuari a les seves respectives
     *         respostes.
     */
    public HashMap<String, Resposta> getRespostes() {
        return new HashMap<>(respostes);
    }

    /**
     * Obté el nombre total de respostes a aquesta pregunta.
     * 
     * @return El nombre d'usuaris que han respost.
     */
    public int getNumRespostes() {
        return respostes.size();
    }

    /**
     * Comprova si un usuari ha respost aquesta pregunta.
     * 
     * @param username El nom de l'usuari a comprovar.
     * @return {@code true} si l'usuari ha respost, {@code false} en cas contrari.
     */
    public boolean teResposta(String username) {
        return respostes.containsKey(username);
    }
}
