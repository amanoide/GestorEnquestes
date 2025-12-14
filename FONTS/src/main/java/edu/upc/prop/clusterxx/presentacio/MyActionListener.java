package edu.upc.prop.clusterxx.presentacio;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Frame;
import java.util.ArrayList;
import edu.upc.prop.clusterxx.domini.classes.Pregunta;
import edu.upc.prop.clusterxx.domini.classes.Opcio;
import edu.upc.prop.clusterxx.domini.classes.TipusPregunta;

/**
 * Listener centralitzat per gestionar totes les accions dels botons de la
 * interfície.
 * 
 * Aquest ActionListener centralitza la lògica d'esdeveniments de tots els
 * botons de l'aplicació, reduint la duplicació de codi i facilitant el
 * manteniment. Utilitza un patró basat en enums per garantir type-safety i
 * evitar errors de typo en les accions.
 * 
 * Funcionalitats clau:
 * 
 * Gestió centralitzada d'accions mitjançant enums.
 * Context dinàmic per accedir a dades específiques de cada vista/diàleg.
 * Separació de responsabilitats per categories d'accions.
 * Gestió d'errors amb fallback segur.
 * 
 * 
 */
public class MyActionListener implements ActionListener {

    /**
     * Enum amb totes les accions possibles de la interfície.
     * 
     * Agrupa les accions per categories: autenticació, navegació, enquestes,
     * preguntes, respostes, anàlisi i usuaris.
     */
    public enum Action {
        // Autenticació
        LOGIN, REGISTER, LOGOUT,

        // Navegació
        TORNAR_MENU, MOSTRAR_LOGIN, MOSTRAR_REGISTER,

        // Enquestes
        CREAR_ENQUESTA, MODIFICAR_ENQUESTA, ELIMINAR_ENQUESTA,
        IMPORTAR_ENQUESTA, GESTIONAR_ENQUESTES, VEURE_PARTICIPANTS,

        // Preguntes
        GESTIONAR_PREGUNTES, CREAR_PREGUNTA, MODIFICAR_PREGUNTA, ELIMINAR_PREGUNTA, VEURE_RESPOSTES_PREGUNTA,

        // Respostes
        RESPONDRE_ENQUESTA, GESTIONAR_RESPOSTES, MODIFICAR_RESPOSTA, MODIFICAR_RESPOSTA_INDIVIDUAL, ELIMINAR_RESPOSTA, IMPORTAR_RESPOSTA, VEURE_RESPOSTES_ENQUESTA,

        // Anàlisi
        ANALITZAR_ENQUESTA, VEURE_PERFIL,

        // Usuaris
        ELIMINAR_COMPTE
    }

    /** Controlador de presentació per executar les operacions. */
    private CtrlPresentacio ctrlPresentacio;
    /** Referència a la vista principal per a la navegació. */
    private VistaPrincipal vistaPrincipal;
    /**
     * Context adicional: pot ser una vista, diàleg o objecte amb dades
     * necessàries.
     */
    private Object context;

    /**
     * Constructor simplificat sense context.
     *
     * @param ctrl  Controlador de presentació.
     * @param vista Vista principal.
     */
    public MyActionListener(CtrlPresentacio ctrl, VistaPrincipal vista) {
        this(ctrl, vista, null);
    }

    /**
     * Constructor complet amb context.
     *
     * @param ctrl    Controlador de presentació.
     * @param vista   Vista principal.
     * @param context Vista, diàleg o dades addicionals necessàries per executar
     *                l'acció.
     */
    public MyActionListener(CtrlPresentacio ctrl, VistaPrincipal vista, Object context) {
        this.ctrlPresentacio = ctrl;
        this.vistaPrincipal = vista;
        this.context = context;
    }

    /**
     * Processa l'esdeveniment d'acció del botó.
     * 
     * Extreu l'acció del ActionCommand i delega al mètode handleAction.
     * Si l'acció no és vàlida, mostra un error per consola.
     *
     * @param e Esdeveniment d'acció generat pel botó.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            Action action = Action.valueOf(e.getActionCommand());
            handleAction(action, e);
        } catch (IllegalArgumentException ex) {
            System.err.println("Acció desconeguda: " + e.getActionCommand());
        }
    }

    /**
     * Gestiona l'execució de l'acció especificada.
     * 
     * Delega cada acció al mètode corresponent segons la categoria.
     *
     * @param action Acció a executar (del enum Action).
     * @param e      Esdeveniment original (per si es necessita informació
     *               addicional).
     */
    private void handleAction(Action action, ActionEvent e) {
        switch (action) {
            // Autenticació
            case LOGIN:
                handleLogin();
                break;
            case REGISTER:
                handleRegister();
                break;
            case LOGOUT:
                handleLogout();
                break;

            // Navegació
            case TORNAR_MENU:
                vistaPrincipal.mostrarVista("MENU");
                break;
            case MOSTRAR_LOGIN:
                vistaPrincipal.mostrarVista("LOGIN");
                break;
            case MOSTRAR_REGISTER:
                vistaPrincipal.mostrarVista("REGISTER");
                break;

            // Enquestes
            case CREAR_ENQUESTA:
                handleCrearEnquesta();
                break;
            case MODIFICAR_ENQUESTA:
                handleModificarEnquesta();
                break;
            case ELIMINAR_ENQUESTA:
                handleEliminarEnquesta();
                break;
            case IMPORTAR_ENQUESTA:
                handleImportarEnquesta();
                break;
            case GESTIONAR_ENQUESTES:
                vistaPrincipal.mostrarVista("GESTION");
                break;
            case VEURE_PARTICIPANTS:
                handleVeureParticipants();
                break;

            // Preguntes
            case GESTIONAR_PREGUNTES:
                handleGestionarPreguntes();
                break;
            case CREAR_PREGUNTA:
                handleCrearPregunta();
                break;
            case MODIFICAR_PREGUNTA:
                handleModificarPregunta();
                break;
            case ELIMINAR_PREGUNTA:
                handleEliminarPregunta();
                break;
            case VEURE_RESPOSTES_PREGUNTA:
                handleVeureRespostesPregunta();
                break;

            // Respostes
            case RESPONDRE_ENQUESTA:
                handleRespondrEnquesta();
                break;
            case GESTIONAR_RESPOSTES:
                vistaPrincipal.mostrarVista("GESTION_RESPOSTES");
                break;
            case MODIFICAR_RESPOSTA:
                handleModificarResposta();
                break;
            case MODIFICAR_RESPOSTA_INDIVIDUAL:
                handleModificarRespostaIndividual();
                break;
            case ELIMINAR_RESPOSTA:
                handleEliminarResposta();
                break;
            case IMPORTAR_RESPOSTA:
                handleImportarResposta();
                break;
            case VEURE_RESPOSTES_ENQUESTA:
                handleVeureRespostesEnquesta();
                break;

            // Anàlisi
            case ANALITZAR_ENQUESTA:
                handleAnalitzarEnquesta();
                break;
            case VEURE_PERFIL:
                handleVeurePerfil();
                break;

            // Usuaris
            case ELIMINAR_COMPTE:
                handleEliminarCompte();
                break;

            default:
                System.err.println("Acció no implementada: " + action);
        }
    }

    // ============ AUTENTICACIÓ ============

    /**
     * Gestiona l'acció de login.
     * 
     * Extreu les credencials de la vista de login i intenta autenticar
     * l'usuari. Si té èxit, navega al menú principal.
     */
    private void handleLogin() {
        if (context instanceof VistaLogin) {
            VistaLogin vista = (VistaLogin) context;
            String user = vista.getUsername();
            String pass = vista.getPassword();

            if (user.isEmpty() || pass.isEmpty()) {
                vista.mostrarError("⚠ Camps obligatoris");
                return;
            }

            if (ctrlPresentacio.login(user, pass)) {
                vista.netejarError();
                vistaPrincipal.mostrarVista("MENU");
            } else {
                vista.mostrarError("⚠ Credencials incorrectes");
            }
        }
    }

    /**
     * Gestiona l'acció de registre.
     * 
     * Extreu les dades del formulari de registre i intenta crear un nou
     * usuari. Mostra el resultat i redirigeix al login si té èxit.
     */
    private void handleRegister() {
        if (context instanceof VistaRegistro) {
            VistaRegistro vista = (VistaRegistro) context;
            String user = vista.getUsername();
            String pass = vista.getPassword();
            String passConfirm = vista.getPasswordConfirm();

            if (user.isEmpty() || pass.isEmpty() || passConfirm.isEmpty()) {
                vista.mostrarError("⚠ Camps obligatoris");
                return;
            }

            if (!pass.equals(passConfirm)) {
                vista.mostrarError("⚠ Les contrasenyes no coincideixen");
                return;
            }

            String resultado = ctrlPresentacio.registrarUsuari(user, pass);
            if (resultado.contains("correctament")) {
                JOptionPane.showMessageDialog(getParentFrame(), "✓ " + resultado, "Registre Completat",
                        JOptionPane.INFORMATION_MESSAGE);
                vistaPrincipal.mostrarVista("LOGIN");
            } else {
                vista.mostrarError("⚠ " + resultado.replace("Error: ", ""));
            }
        }
    }

    /**
     * Gestiona l'acció de logout.
     * 
     * Tanca la sessió de l'usuari actual i torna a la pantalla de login.
     */
    private void handleLogout() {
        ctrlPresentacio.logout();
        vistaPrincipal.mostrarVista("LOGIN");
    }

    // ============ ENQUESTES ============

    /**
     * Gestiona la creació d'una nova enquesta.
     * 
     * Obre el diàleg de creació i, si es confirma, envia les dades al
     * controlador.
     */
    private void handleCrearEnquesta() {
        Frame parent = getParentFrame();
        DialogoEnquesta dialogo = new DialogoEnquesta(parent, "Nova enquesta", true);
        dialogo.setVisible(true);

        if (dialogo.isConfirmado()) {
            String resultado = ctrlPresentacio.crearEnquesta(
                    dialogo.getId(), dialogo.getTitol(), dialogo.getDesc());
            JOptionPane.showMessageDialog(parent, resultado);
        }
    }

    /**
     * Gestiona la modificació d'una enquesta existent.
     * 
     * Obté l'enquesta seleccionada del context (VistaGestionEnquestes), obre
     * el diàleg d'edició amb les dades actuals i actualitza si es confirma.
     */
    private void handleModificarEnquesta() {
        if (context instanceof VistaGestionEnquestes) {
            VistaGestionEnquestes vista = (VistaGestionEnquestes) context;
            String selected = vista.listEnquestes.getSelectedValue();
            if (selected == null)
                return;

            String id = selected.split(":")[0].trim();
            String currentTitle = "", currentDesc = "";

            for (edu.upc.prop.clusterxx.domini.classes.Enquesta e : ctrlPresentacio.getEnquestesUsuari()) {
                if (e.getId().equals(id)) {
                    currentTitle = e.getTitol();
                    currentDesc = e.getDescripcio();
                    break;
                }
            }

            Frame parent = getParentFrame();
            DialogoEnquesta dialogo = new DialogoEnquesta(parent, "Modificar Enquesta", false);
            dialogo.setDatos(id, currentTitle, currentDesc);
            dialogo.setVisible(true);

            if (dialogo.isConfirmado()) {
                String result = ctrlPresentacio.modificarEnquesta(id, dialogo.getTitol(), dialogo.getDesc());
                JOptionPane.showMessageDialog(parent, result);
                vista.actualizarLista();
            }
        }
    }

    /**
     * Gestiona l'eliminació d'una enquesta.
     * 
     * Demana confirmació i elimina l'enquesta seleccionada del context.
     */
    private void handleEliminarEnquesta() {
        if (context instanceof VistaGestionEnquestes) {
            VistaGestionEnquestes vista = (VistaGestionEnquestes) context;
            String selected = vista.listEnquestes.getSelectedValue();
            if (selected == null)
                return;

            String id = selected.split(":")[0].trim();
            Frame parent = getParentFrame();

            int confirm = JOptionPane.showConfirmDialog(parent,
                    "Estàs segur de que vols eliminar l'enquesta '" + id + "'?",
                    "Confirmar eliminació", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                String result = ctrlPresentacio.esborrarEnquesta(id);
                JOptionPane.showMessageDialog(parent, result);
                vista.actualizarLista();
            }
        }
    }

    /**
     * Gestiona la importació d'una enquesta des d'un fitxer.
     * 
     * Obre un selector de fitxers i importa l'enquesta seleccionada.
     */
    private void handleImportarEnquesta() {
        Frame parent = getParentFrame();
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(parent);

        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            String resultado = ctrlPresentacio.importarEnquesta(selectedFile.getAbsolutePath());
            JOptionPane.showMessageDialog(parent, resultado);
        }
    }

    /**
     * Gestiona la visualització de participants d'una enquesta.
     * 
     * Obre el diàleg DialogoParticipants amb els usuaris que han respost.
     */
    private void handleVeureParticipants() {
        if (context instanceof VistaGestionEnquestes) {
            VistaGestionEnquestes vista = (VistaGestionEnquestes) context;
            String selected = vista.listEnquestes.getSelectedValue();
            if (selected == null)
                return;

            String id = selected.split(":")[0].trim();
            
            // Buscar l'enquesta per obtenir participants
            edu.upc.prop.clusterxx.domini.classes.Enquesta enquesta = null;
            for (edu.upc.prop.clusterxx.domini.classes.Enquesta e : ctrlPresentacio.getEnquestesUsuari()) {
                if (e.getId().equals(id)) {
                    enquesta = e;
                    break;
                }
            }
            
            if (enquesta != null) {
                Frame parent = getParentFrame();
                new DialogoParticipants(parent, enquesta.getTitol(), enquesta.getParticipants()).setVisible(true);
            }
        }
    }

    // ============ PREGUNTES ============

    /**
     * Gestiona l'obertura del diàleg de gestió de preguntes.
     * 
     * Obre DialogoGestionPreguntes per gestionar les preguntes de l'enquesta.
     */
    private void handleGestionarPreguntes() {
        if (context instanceof VistaGestionEnquestes) {
            VistaGestionEnquestes vista = (VistaGestionEnquestes) context;
            String selected = vista.listEnquestes.getSelectedValue();
            if (selected == null)
                return;

            String id = selected.split(":")[0].trim();
            Frame parent = getParentFrame();
            new DialogoGestionPreguntes(parent, ctrlPresentacio, id).setVisible(true);
        }
    }

    /**
     * Gestiona la creació d'una nova pregunta.
     */
    private void handleCrearPregunta() {
        if (context instanceof DialogoGestionPreguntes) {
            DialogoGestionPreguntes dialogo = (DialogoGestionPreguntes) context;
            Frame parent = getParentFrame();
            DialogoCrearPregunta dialogoCrear = new DialogoCrearPregunta(parent);
            dialogoCrear.setVisible(true);

            if (dialogoCrear.isConfirmado()) {
                String resultado = ctrlPresentacio.afegirPregunta(
                        dialogo.getIdEnquesta(),
                        dialogoCrear.getId(),
                        dialogoCrear.getPreguntaText(),
                        dialogoCrear.getTipus(),
                        dialogoCrear.getMin(),
                        dialogoCrear.getMax(),
                        dialogoCrear.getOpcions(),
                        dialogoCrear.getMaxSeleccions());
                JOptionPane.showMessageDialog(parent, resultado);
                dialogo.cargarPreguntes();
            }
        }
    }

    /**
     * Gestiona la modificació d'una pregunta existent.
     */
    private void handleModificarPregunta() {
        if (context instanceof DialogoGestionPreguntes) {
            DialogoGestionPreguntes dialogo = (DialogoGestionPreguntes) context;
            String idPregunta = dialogo.getSelectedPreguntaId();
            Frame parent = getParentFrame();

            if (idPregunta == null) {
                JOptionPane.showMessageDialog(parent, "Selecciona una pregunta per modificar.");
                return;
            }

            edu.upc.prop.clusterxx.domini.classes.Pregunta p = ctrlPresentacio.getDadesPregunta(dialogo.getIdEnquesta(), idPregunta);

            if (p == null) {
                JOptionPane.showMessageDialog(parent, "Error al recuperar dades de la pregunta.");
                return;
            }

            DialogoCrearPregunta dialogoCrear = new DialogoCrearPregunta(parent);
            dialogoCrear.setTitle("Modificar Pregunta");
            dialogoCrear.setDades(p.getId(), p.getText(), p.getTipus().toString(),
                    p.getValorMinim(), p.getValorMaxim(), p.getOpcions(), p.getMaxSeleccions());
            dialogoCrear.setVisible(true);

            if (dialogoCrear.isConfirmado()) {
                String resultado = ctrlPresentacio.modificarPregunta(
                        dialogo.getIdEnquesta(),
                        dialogoCrear.getId(),
                        dialogoCrear.getPreguntaText(),
                        dialogoCrear.getTipus(),
                        dialogoCrear.getMin(),
                        dialogoCrear.getMax(),
                        dialogoCrear.getOpcions(),
                        dialogoCrear.getMaxSeleccions());
                JOptionPane.showMessageDialog(parent, resultado);
                dialogo.cargarPreguntes();
            }
        }
    }

    /**
     * Gestiona l'eliminació d'una pregunta.
     */
    private void handleEliminarPregunta() {
        if (context instanceof DialogoGestionPreguntes) {
            DialogoGestionPreguntes dialogo = (DialogoGestionPreguntes) context;
            String idPregunta = dialogo.getSelectedPreguntaId();
            Frame parent = getParentFrame();

            if (idPregunta == null) {
                JOptionPane.showMessageDialog(parent, "Selecciona una pregunta per eliminar.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(parent,
                    "Estàs segur que vols eliminar la pregunta " + idPregunta + "?",
                    "Confirmar eliminació",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                String resultado = ctrlPresentacio.eliminarPregunta(dialogo.getIdEnquesta(), idPregunta);
                JOptionPane.showMessageDialog(parent, resultado);
                dialogo.cargarPreguntes();
            }
        }
    }

    /**
     * Gestiona la visualització de totes les respostes d'una pregunta.
     */
    private void handleVeureRespostesPregunta() {
        if (context instanceof DialogoGestionPreguntes) {
            DialogoGestionPreguntes dialogo = (DialogoGestionPreguntes) context;
            String idPregunta = dialogo.getSelectedPreguntaId();

            if (idPregunta == null) {
                JOptionPane.showMessageDialog(dialogo, "Selecciona una pregunta per veure les respostes.");
                return;
            }

            // Obrir el diàleg amb les respostes de la pregunta
            DialogoRespostesPregunta dialogoRespostes = new DialogoRespostesPregunta(dialogo, ctrlPresentacio, idPregunta);
            dialogoRespostes.setVisible(true);
        }
    }

    // ============ RESPOSTES ============

    /**
     * Gestiona l'acció de respondre una enquesta.
     * 
     * Obre un diàleg per seleccionar l'enquesta i després el diàleg de
     * respostes.
     */
    private void handleRespondrEnquesta() {
        Frame parent = getParentFrame();
        DialogoSeleccionarEnquesta dialogoSel = new DialogoSeleccionarEnquesta(parent, ctrlPresentacio);
        dialogoSel.setVisible(true);

        if (dialogoSel.isConfirmado()) {
            String idEnquesta = dialogoSel.getSelectedId();
            
            // Verificar si l'enquesta té preguntes abans d'obrir el diàleg
            ArrayList<Pregunta> preguntes = 
                ctrlPresentacio.getPreguntesEnquestaObjects(idEnquesta);
            
            if (preguntes == null || preguntes.isEmpty()) {
                JOptionPane.showMessageDialog(parent,
                    "Actualment no hi ha preguntes a respondre",
                    "Enquesta sense preguntes",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            DialogoResponderEnquesta dialogoResp = new DialogoResponderEnquesta(
                    parent, ctrlPresentacio, idEnquesta);
            dialogoResp.setVisible(true);
        }
    }

    /**
     * Gestiona l'obertura del diàleg per modificar respostes.
     * 
     * Obre DialogoGestionarRespostes amb l'enquesta seleccionada.
     */
    private void handleModificarResposta() {
        if (context instanceof VistaGestionarRespostes) {
            VistaGestionarRespostes vista = (VistaGestionarRespostes) context;
            String selected = vista.listEnquestes.getSelectedValue();
            if (selected == null)
                return;

            String id = selected.split(":")[0].trim();
            Frame parent = getParentFrame();
            new DialogoGestionarRespostes(parent, ctrlPresentacio, id).setVisible(true);
            vista.actualizarLista();
        }
    }

    /**
     * Gestiona la modificació d'una resposta individual.
     */
    private void handleModificarRespostaIndividual() {
        if (context instanceof Object[]) {
            Object[] ctx = (Object[]) context;
            if (ctx.length == 2 && ctx[0] instanceof DialogoGestionarRespostes && ctx[1] instanceof Pregunta) {
                DialogoGestionarRespostes dialogo = (DialogoGestionarRespostes) ctx[0];
                Pregunta p = (Pregunta) ctx[1];

                String currentAnswer = dialogo.getResposta(p.getId());
                String novaResposta = null;

                if (p.tipusAdmetOpcions() && p.getTipus() != TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE) {
                    // Mostrar ComboBox para opciones simples/ordenadas
                    java.util.ArrayList<Opcio> opcions = p.getOpcions();
                    String[] opcionsText = new String[opcions.size()];
                    for (int i = 0; i < opcions.size(); i++) {
                        opcionsText[i] = opcions.get(i).getText();
                    }
                    
                    Object selected = JOptionPane.showInputDialog(dialogo,
                            "Selecciona la nova resposta per a:\n\n" + p.getText(),
                            "Modificar Resposta",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            opcionsText,
                            currentAnswer);
                            
                    if (selected != null) {
                        novaResposta = selected.toString();
                    }
                } else {
                    // Input de texto normal para otros tipos
                    String message = "Introdueix la nova resposta per a:\n\n" + p.getText() + "\n\n" +
                                   "Format esperat: " + p.getTipus();
                                   
                    if (p.getTipus() == TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE) {
                        message += "\n(Opcions vàlides: ";
                        for (Opcio o : p.getOpcions()) {
                            message += o.getText() + ", ";
                        }
                        if (!p.getOpcions().isEmpty()) {
                            message = message.substring(0, message.length() - 2);
                        }
                        message += ")";
                    }
                    
                    novaResposta = JOptionPane.showInputDialog(dialogo, message, currentAnswer);
                }

                if (novaResposta != null && !novaResposta.trim().isEmpty()) {
                    String resultat = ctrlPresentacio.modificarResposta(dialogo.getIdEnquesta(), p.getId(), novaResposta);
                    JOptionPane.showMessageDialog(dialogo, resultat);
                    if (resultat.contains("correctament")) {
                        dialogo.actualizarVista();
                    }
                }
            }
        }
    }

    /**
     * Gestiona l'eliminació de totes les respostes d'una enquesta.
     * 
     * Demana confirmació i esborra les respostes de l'usuari actual.
     */
    private void handleEliminarResposta() {
        String idEnquesta = null;
        Runnable onSuccess = null;

        if (context instanceof VistaGestionarRespostes) {
            VistaGestionarRespostes vista = (VistaGestionarRespostes) context;
            String selected = vista.listEnquestes.getSelectedValue();
            if (selected == null)
                return;

            idEnquesta = selected.split(":")[0].trim();
            onSuccess = () -> vista.actualizarLista();
        } else if (context instanceof DialogoGestionarRespostes) {
            DialogoGestionarRespostes dialogo = (DialogoGestionarRespostes) context;
            idEnquesta = dialogo.getIdEnquesta();
            onSuccess = () -> dialogo.setVisible(false);
        }

        if (idEnquesta != null) {
            Frame parent = getParentFrame();

            int confirm = JOptionPane.showConfirmDialog(parent,
                    "Estàs segur de que vols esborrar totes les teves respostes d'aquesta enquesta?",
                    "Confirmar eliminació", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                String result = ctrlPresentacio.esborrarRespostesEnquesta(idEnquesta);
                JOptionPane.showMessageDialog(parent, result);
                if (onSuccess != null)
                    onSuccess.run();
            }
        }
    }

    /**
     * Gestiona la importació d'una resposta des d'un fitxer JSON.
     * 
     * Obre un diàleg de selecció de fitxer i crida al controlador per importar la resposta.
     */
    private void handleImportarResposta() {
        Frame parent = getParentFrame();
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(parent);

        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File selectedFile = fileChooser.getSelectedFile();
            String resultado = ctrlPresentacio.importarResposta(selectedFile.getAbsolutePath());
            JOptionPane.showMessageDialog(parent, resultado);
            
            // Actualitzar la llista si estem en la vista de gestió de respostes
            if (context instanceof VistaGestionarRespostes) {
                VistaGestionarRespostes vista = (VistaGestionarRespostes) context;
                vista.actualizarLista();
            }
        }
    }

    /**
     * Gestiona la visualització de totes les respostes d'una enquesta.
     * 
     * Mostra un diàleg amb totes les respostes de tots els usuaris a l'enquesta seleccionada.
     */
    private void handleVeureRespostesEnquesta() {
        if (context instanceof VistaGestionEnquestes) {
            VistaGestionEnquestes vista = (VistaGestionEnquestes) context;
            String selected = vista.listEnquestes.getSelectedValue();
            if (selected == null) return;

            String idEnquesta = selected.split(":")[0].trim();
            
            Frame parent = getParentFrame();
            DialogoRespostesEnquesta dialogo = new DialogoRespostesEnquesta(parent, ctrlPresentacio, idEnquesta);
            dialogo.setVisible(true);
        }
    }

    // ============ ANÀLISI ============

    /**
     * Gestiona l'execució de l'anàlisi de clustering sobre una enquesta.
     * 
     * Obre el DialogoAnalisi per configurar i executar l'algorisme.
     */
    private void handleAnalitzarEnquesta() {
        if (context instanceof VistaAnalisi) {
            VistaAnalisi vista = (VistaAnalisi) context;
            String selected = vista.listEnquestes.getSelectedValue();
            if (selected == null)
                return;

            String id = selected.split(":")[0].trim();
            Frame parent = getParentFrame();
            new DialogoAnalisi(parent, ctrlPresentacio, id).setVisible(true);
        }
    }

    /**
     * Gestiona la visualització del perfil de clustering de l'usuari.
     * 
     * Consulta els perfils i els mostra en un diàleg.
     */
    private void handleVeurePerfil() {
        String perfil = ctrlPresentacio.consultarMeuPerfil();
        Frame parent = getParentFrame();
        new DialogoPerfil(parent, perfil).setVisible(true);
    }

    // ============ USUARIS ============

    /**
     * Gestiona l'eliminació del compte de l'usuari actual.
     * 
     * Mostra un diàleg de confirmació i, si s'accepta, elimina el compte i
     * torna al login.
     */
    private void handleEliminarCompte() {
        Frame parent = getParentFrame();
        int confirm = JOptionPane.showConfirmDialog(parent,
                "Estàs segur de que vols esborrar el teu compte? Aquesta acció és irreversible.",
                "Eliminar compte", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String resultado = ctrlPresentacio.esborrarUsuariActual();
            JOptionPane.showMessageDialog(parent, resultado);

            if (resultado.contains("correctament")) {
                vistaPrincipal.mostrarVista("LOGIN");
            }
        }
    }

    // ============ UTILS ============

    /**
     * Obté la finestra pare per mostrar diàlegs modals.
     * 
     * Si el context és un Frame, el retorna directament. Si és un Component,
     * busca el Frame propietari.
     *
     * @return Frame propietari o null si no es pot determinar.
     */
    private Frame getParentFrame() {
        if (context instanceof Frame) {
            return (Frame) context;
        }
        if (context instanceof java.awt.Component) {
            return (Frame) SwingUtilities.getWindowAncestor((java.awt.Component) context);
        }
        return null;
    }
}
