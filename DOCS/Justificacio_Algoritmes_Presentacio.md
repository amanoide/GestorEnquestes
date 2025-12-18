# Justificació d'Algoritmes i Estructures - Capa de Presentació

Aquest document descriu les decisions de disseny i implementació preses per a la capa de presentació projecte. Seguint la mateixa estructura que la documentació de domini, analitzem l'estructura triada, la seva justificació, alternatives descartades i complexitat.

---

## 1. Gestió d'Esdeveniments (Patró Observer)

### Estructura triada
Hem implementat un **Listener Centralitzat (`MyActionListener`)** que actua com a únic observador per a tots los botons i elements interactius de la interfície. 
- Utilitza un ``Enum`` (`Action`) per identificar inequívocament l'origen de l'esdeveniment.
- Implementa el patró **Command** simplificat a través d'un `switch` massiu que delega a mètodes privats (`handleLogin`, `handleCrearEnquesta`, etc.).

### Justificació
Aquesta estructura desacobla completament la definició visual de la interfície (Vistes) de la lògica de control (Controlador/Listener). 
1. **Mantenibilitat:** Tota la lògica de "què passa quan clico" està en un sol fitxer, no escampada en classes anònimes dins de cada vista.
2. **Reutilització:** Un mateix mètode de gestió (ex: `TORNAR_MENU`) pot ser assignat a múltiples botons en diferents pantalles sense duplicar codi.

### Alternatives descartades
- **Listeners Anònims (Inner Classes):** És l'enfocament clàssic de Swing (`button.addActionListener(new ActionListener() {...})`). Va ser descartat perquè genera "Spaghetti Code", fa que les classes de vista siguin molt llargues i dificulta la lectura del flux de l'aplicació.
- **Un Listener per Vista:** Tenir `LoginListener`, `MenuListener`, etc. Hauria estat una opció vàlida, però vam preferir un únic punt d'entrada per simplificar la comunicació amb el `CtrlPresentacio` principal.

### Complexitat
- **Temporal:** $O(1)$ per a la dispatx de l'esdeveniment (accés directe via `switch` sobre Enum).
- **Espacial:** $O(1)$. Només necessitem una instància del Listener compartida per tota l'aplicació, estalviant la creació de centenars de petits objectes `ActionListener` anònims en memòria.

---

## 2. Sistema de Navegació (CardLayout)

### Estructura triada
Utilitzem un **`CardLayout`** gestionat per la classe `VistaPrincipal`. 
- Totes les vistes principals (`VistaLogin`, `VistaMenu`, etc.) s'inicialitzen a l'inici i s'afegeixen a un panell contenidor.
- La navegació es redueix a mostrar/amagar capes identificades per un string ("LOGIN", "MENU").

### Justificació
El `CardLayout` gestiona automàticament la visibilitat i la substitució de components. 
- **Experiència d'Usuari:** El canvi entre pantalles és instantani (sense parpelleig de finestres tancant-se i obrint-se).
- **Estat:** Permet mantenir l'estat de les vistes "en segon pla" (ex: no perdre el text escrit si canvies de pantalla momentàniament), tot i que nosaltres forcem una actualització (`actualizarLista()`) en entrar per garantir consistència.

### Alternatives descartades
- **Múltiples JFrames:** Obrir una finestra nova (`new JFrame()`) i fer `dispose()` de l'anterior cada cop que l'usuari canvia de pantalla. Descartat perquè és lent, visualment "tosca" i complicat de gestionar a nivell de cicle de vida de l'aplicació (tancar l'app accidentalment és fàcil).
- **JTabbedPane:** Pestanyes. Descartat per raons estètiques i de flux; volíem una aplicació seqüencial, no un panell de control amb tot accessible alhora.

### Complexitat
- **Temporal:** $O(1)$ per canviar de vista. El cost de pintar (`repaint()`) depèn de la complexitat de la vista destí, però l'algoritme de canvi és constant.
- **Espacial:** $O(V)$ on $V$ és el nombre de vistes. Totes les vistes estan carregades en memòria RAM. Donat que la nostra aplicació no és massiva gràficament, això és acceptable i preferible a la latència de càrrega sota demanda.

---

## 3. Estructures de Dades Visuals (ArrayList vs LinkedList)

### Estructura triada
Utilitzem **`ArrayList`** per alimentar els models de dades de `JList` i `JComboBox`.

### Justificació
El component Swing `JList` i el seu model `DefaultListModel` o `AbstractListModel` accedeixen als elements per índex (`getElementAt(int index)`).
- **`ArrayList`** ofereix accés aleatori constant $O(1)$.
- Això és crític per al rendiment de renderitzat (el "pintat") de llistes amb molts elements, ja que el component de la UI pot demanar l'element 50 sense haver de recórrer els 49 anteriors.

### Alternatives descartades
- **LinkedList:** Descartada immediatament. L'accés posicional és $O(N)$. Si tinguéssim una llista de 1000 enquestes, fer scroll seria notablement lent perquè Swing demanaria els elements un a un i `LinkedList` hauria de recórrer la llista des de l'inici per a cada un.
- **Arrays estàtics (`String[]`):** Utilitzats puntualment quan la mida és fixa (ex: opcions d'un desplegable immutables), però descartats per a dades de domini (llista d'enquestes) per la manca de flexibilitat per afegir/esborrar items dinàmicament.

### Complexitat
- **Temporal (Visualització):** $O(1)$ accés per element. 
- **Temporal (Càrrega):** $O(N)$ per copiar les dades del domini a la llista de presentació.
- **Espacial:** $O(N)$ elements.

---

## 4. Mapes de Respostes (HashMap)

### Estructura triada
Utilitzem **`HashMap<String, ...>`** per gestionar l'intercanvi de respostes i preguntes entre els diàlegs i el controlador.

### Justificació
En la capa de presentació, sovint necessitem relacionar un ID (String) amb un objecte (Pregunta o Resposta) ràpidament, per exemple, quan l'usuari clica en un element d'una llista i volem mostrar-ne els detalls.
- Permet cerques $O(1)$ sense haver de recórrer llistes cada vegada que l'usuari interactua amb un element.

### Alternatives descartades
- **Llistes de Parells (Pairs):** Cerca lineal $O(N)$. Ineficient.
- **TreeMap:** Cerca logarítmica $O(\log N)$ i ordenat. Descartat perquè a la UI l'ordre visual ja el gestionem nosaltres (per exemple, l'ordre de les preguntes a l'enquesta), i no necessitem l'ordre natural de les claus (IDs).

### Complexitat
- **Temporal:** $O(1)$ (promig) per recuperar dades associades a un ID d'interfície.
- **Espacial:** $O(N)$ per emmagatzemar les referències.
