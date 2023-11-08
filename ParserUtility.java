package Implementazione;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;

public final class ParserUtility {

    public final Terminale FINESTRINGA = new Terminale("$");
    private Grammatica grammatica;
    private Terminale epsilon;
    private LinkedHashMap<Simbolo, LinkedHashSet<Terminale>> firsts = new LinkedHashMap<>();
    private LinkedHashMap<NonTerminale, LinkedHashSet<Terminale>> follows;

    public ParserUtility(Grammatica grammatica) {
        this.grammatica = grammatica;
        this.epsilon = grammatica.getTermSeEsiste("eps");
        this.firsts = calculateFirsts();
        this.follows = calculateFollows();
    }

    // ! PARTE DI RESTITUIZIONE DELLE FIRST E FOLLOW GIÀ CALCOLATE
    public LinkedHashMap<Simbolo, LinkedHashSet<Terminale>> getFirsts() {
        return firsts;
    }

    public LinkedHashSet<Terminale> getSymbolFirst(Simbolo simbolo) {
        return this.firsts.get(simbolo);
    }

    public LinkedHashSet<Terminale> getStringFirst(LinkedList<Simbolo> simboli) throws IllegalArgumentException {

        LinkedHashSet<Terminale> first = new LinkedHashSet<>();
        // Controllo che l'argomento sia un insieme di simboli dell'alfabeto
        if (simboli.size() == 0) {
            throw new IllegalArgumentException("Argomento mancante");
        }

        // Per ogni simbolo della stringa prendo la first (che già conosco) e la
        // aggiungo al risultato finché non trovo un simbolo la cui first non contiene
        // epsilon
        Iterator<Simbolo> iterator = simboli.iterator();
        while (iterator.hasNext()) {
            LinkedHashSet<Terminale> firstAttuale = this.getSymbolFirst(iterator.next());
            if (!firstAttuale.contains(this.epsilon) || (!iterator.hasNext())) {
                first.addAll(firstAttuale);
                return first;
            }
            // Se contiene epsilon e non è l'ultimo carattere, rimuovo epsilon e vado avanti
            else {
                firstAttuale.remove(this.epsilon);
                first.addAll(firstAttuale);
            }
        }

        return first;
    }

    public LinkedHashMap<NonTerminale, LinkedHashSet<Terminale>> getFollows() {
        return follows;
    }

    public LinkedHashSet<Terminale> getFollow(NonTerminale nonTerminale) {
        return this.follows.get(nonTerminale);
    }

    // ! PARTE DI CALCOLO DELLE FIRST
    private LinkedHashMap<Simbolo, LinkedHashSet<Terminale>> calculateFirsts() {
        LinkedHashMap<Simbolo, LinkedHashSet<Terminale>> result = new LinkedHashMap<Simbolo, LinkedHashSet<Terminale>>();
        for (NonTerminale nonTerminale : this.grammatica.getNonTerminali()) {
            result.put(nonTerminale, calculateSymbolFirst(nonTerminale));
        }
        for (Terminale terminale : this.grammatica.getTerminali()) {
            result.put(terminale, calculateSymbolFirst(terminale));
        }
        return result;
    }

    /**
     * Passo base per il calcolo della first di un simbolo
     */
    private LinkedHashSet<Terminale> calculateSymbolFirst(Simbolo simbolo) {
        LinkedHashSet<Terminale> first = new LinkedHashSet<Terminale>();
        // Se il simbolo è un non terminale, calcolo le first di tutti i corpi delle
        // produzioni
        if (simbolo instanceof NonTerminale) {
            LinkedList<Simbolo> iniziale = new LinkedList<>();
            iniziale.add(simbolo);

            // Cerco le produzioni che hanno il simbolo in testa e non sono ricorsive
            LinkedHashSet<Produzione> produzioni = this.grammatica.getProduzioniByTestaNonRicorsive(simbolo);

            for (Produzione produzione : produzioni) {
                // rimuovo epsilon dal risultato se ho ancora simboli da valutare perché quelli
                // successivi potrebbero essere non annullabili
                first.addAll(this
                        .calculateFirstPassoSuccessivo(produzione.getCorpo().getSimboli(), iniziale));
            }
        }
        // Se il simbolo è un terminale, allora ho terminato ed esco
        else {
            first.add((Terminale) simbolo);
        }
        return first;
    }

    /**
     * Passo base per il calcolo della first di una stringa
     */
    private LinkedHashSet<Terminale> calculateStringFirst(LinkedList<Simbolo> simboli)
            throws IllegalArgumentException {

        // Controllo che l'argomento sia un insieme di simboli dell'alfabeto
        if (simboli.size() == 0) {
            throw new IllegalArgumentException("Argomento mancante");
        }

        LinkedHashSet<Terminale> first = new LinkedHashSet<>();

        // Per calcolare la first di una stringa devo sempre effettuare l'unione di
        // altre first.
        // L'idea è che nel passo base identifico quali sono tali altre first.
        // Nei passi successivi, se tra le altre first occorre il calcolo della first
        // iniziale la skippo
        Iterator<Simbolo> iterator = simboli.iterator();
        while (iterator.hasNext()) {
            boolean epsilonRemoved = false;
            Simbolo simbolo = iterator.next();
            if (simbolo instanceof NonTerminale) {

                // Cerco le produzioni che hanno il simbolo in testa e non sono ricorsive
                LinkedHashSet<Produzione> produzioni = this.grammatica.getProduzioniByTestaNonRicorsive(simbolo);

                for (Produzione produzione : produzioni) {
                    LinkedHashSet<Terminale> firstAttuale = this
                            .calculateFirstPassoSuccessivo(produzione.getCorpo().getSimboli(), simboli);
                    // rimuovo epsilon dal risultato se ho ancora simboli da valutare perché quelli
                    // successivi potrebbero essere non annullabili
                    if (firstAttuale.contains(this.epsilon) && iterator.hasNext()) {
                        firstAttuale.remove(this.epsilon);
                        epsilonRemoved = true;
                    }
                    first.addAll(firstAttuale);
                }
            }
            // Se il simbolo è un terminale, allora ho terminato ed esco
            else {
                first.add((Terminale) simbolo);
                break;
            }
            // Se il simbolo valutato in precedenza (nel caso di input multi-simbolo) non
            // era annullabile, allora ho terminato ed esco. Se ho rimosso epsilon a mano,
            // allora devo continuare
            if (!first.contains(this.epsilon) && !epsilonRemoved) {
                break;
            }
        }

        return first;
    }

    /**
     * Passi successivi per il calcolo della first che evita cicli infiniti
     */
    private LinkedHashSet<Terminale> calculateFirstPassoSuccessivo(LinkedList<Simbolo> simboli,
            LinkedList<Simbolo> iniziale)
            throws IllegalArgumentException {
        LinkedHashSet<Terminale> first = new LinkedHashSet<Terminale>();

        Iterator<Simbolo> simboliIterator = simboli.iterator();
        Iterator<Simbolo> inizialeIterator = iniziale.iterator();

        if (simboli.size() != 1 && iniziale.size() != 1) {

            // Posiziono l'iteratore sul primo simbolo non annullabile che trovo in simboli
            while (simboliIterator.hasNext()) {
                LinkedHashSet<Terminale> localFirst = getSymbolFirst(simboliIterator.next());
                if (!localFirst.contains(this.epsilon)) {
                    first.addAll(localFirst);
                    break;
                }
            }
            // Posiziono l'iteratore sul primo simbolo non annullabile che trovo in iniziale
            while (inizialeIterator.hasNext()) {
                if (!getSymbolFirst(inizialeIterator.next()).contains(this.epsilon)) {
                    break;
                }
            }
        }
        // Se i due simboli non annullabili sono uguali, allora vuol dire che posso
        // interrompere la ricorsione, altrimenti entrerei in un ciclo infinito
        if (inizialeIterator.hasNext() && simboliIterator.hasNext()
                && inizialeIterator.next().equals(simboliIterator.next())) {
            return first;
        }

        if (simboli.size() == 1) {
            // 1: se l'argomento è un terminale, allora la first è uguale al terminale
            if (simboli.iterator().next() instanceof Terminale)
                first.add((Terminale) simboli.iterator().next());
            // 2: se l'argomento è una variabile, allora la first è uguale all'unione delle
            // first dei corpi di tutte le produzioni con l'argomento in testa
            else {

                // recupero le produzioni che hanno l'argomento come testa
                // se la produzione ha il corpo che inizia con lo stesso simbolo della testa,
                // allora non la considero (per evitare la ricorsione)
                LinkedHashSet<Produzione> produzioni = this.grammatica
                        .getProduzioniByTestaNonRicorsive(simboli.iterator().next());
                for (Produzione produzione : produzioni) {
                    // per ognuna delle produzioni, recupero il corpo
                    Corpo corpo = produzione.getCorpo();
                    // se il corpo è epsilon, aggiungo epsilon alla first
                    if (corpo.getSimboli().size() == 1
                            && corpo.getSimboli().contains(this.epsilon))
                        first.add(this.epsilon);
                    // altrimenti aggiungo alla first totale la first del corpo
                    else {
                        first.addAll(this.calculateFirstPassoSuccessivo(corpo.getSimboli(), simboli));

                    }
                }
            }
            return first;
        }

        // 3: se l'argomento è una stringa, la first è uguale all'unione di tutte le
        // first, private di epsilon, che trovo finché non arrivo ad un simbolo
        // terminale oppure ad un simbolo la cui first non contiene epsilon
        Iterator<Simbolo> iterator = simboli.iterator();
        while (iterator.hasNext()) {
            Simbolo simbolo = iterator.next();
            // se ho trovato un terminale, lo includo nella first e restituisco il risultato
            if (simbolo instanceof Terminale) {
                first.add((Terminale) simbolo);
                return first;
            }
            // se ho trovato un non terminale, calcolo la sua first e la aggiungo al
            // risultato, quindi vedo se contiene epsilon
            LinkedList<Simbolo> newSimbolo = new LinkedList<Simbolo>();
            newSimbolo.add(simbolo);
            LinkedHashSet<Terminale> first_attuale;
            if (this.firsts.containsKey(simbolo)) {
                first_attuale = this.getFirsts().get(simbolo);
            } else {
                // Se non conosco già la first di quel simbolo, la calcolo e la inserisco nelle
                // first migliorando l'efficienza perché la posso facilmente recuperare per i prossimi utilizzi
                first_attuale = this.calculateFirstPassoSuccessivo(newSimbolo, simboli);
                this.firsts.put(simbolo, first_attuale);
            }
            // se non contiene epsilon, allora la variabile non è annullabile ed ho concluso
            if (!first_attuale.contains(this.epsilon)) {
                first.addAll(first_attuale);
                return first;
            }
            // se contiene epsilon, lo rimuovo e passo al simbolo successivo
            else {
                first_attuale.remove(this.epsilon);
                first.addAll(first_attuale);
            }
            // se sono arrivato alla fine della stringa senza trovare un simbolo non
            // annullabile, aggiungo epsilon
            if (!iterator.hasNext()) {
                first.add(this.epsilon);
            }
        }

        return first;
    }

    // ! PARTE DI CALCOLO DELLE FOLLOW
    private LinkedHashMap<NonTerminale, LinkedHashSet<Terminale>> calculateFollows() {
        // resultParziali contiene, per ogni nonTerminale, il parziale attuale e la
        // lista di nonTerminali la cui follow appartiene alla follow del nonTerminale
        LinkedHashMap<NonTerminale, StrutturaFollow> resultParziali = new LinkedHashMap<>();
        LinkedHashMap<NonTerminale, LinkedHashSet<Terminale>> result = new LinkedHashMap<>();

        // Inizializzo la mappa aggiungendo come chiavi i terminali della grammatica
        for (NonTerminale nonTerminale : this.grammatica.getNonTerminali()) {
            // Devo calcolare quali elementi compongono la follow
            // Passo 1: se nonTerminale è starting, contiene $ (fatto nel costruttore)
            StrutturaFollow struttura = new StrutturaFollow(grammatica, nonTerminale, FINESTRINGA);

            // Per ogni produzione il cui corpo contiene nonTerminale:
            for (Produzione produzione : grammatica.getProduzioniIfCorpoContains(nonTerminale)) {
                // Se dopo nonTerminale c'è una stringa calcolo la first della stringa e la
                // aggiungo
                // Se dopo nonTerminale c'è una stringa annullabile, oppure nonTerminale è
                // l'ultimo simbolo del corpo, inserisco Follow(testa)
                Corpo corpo = produzione.getCorpo();
                int nonTerminaleIndex = 0;
                for (Simbolo simbolo : corpo.getSimboli()) {
                    if (simbolo.equals(nonTerminale)) {
                        LinkedList<Simbolo> betaList = new LinkedList<>(
                                corpo.getSimboli().subList(nonTerminaleIndex + 1,
                                        corpo.getSimboli().size()));
                        // Se il simbolo sta alla fine del corpo
                        if (betaList.isEmpty()) {
                            // Aggiungo la testa solo se è diversa dal nonTerm che sto trattando attualmente
                            if (!produzione.getTesta().equals(nonTerminale)) {
                                struttura.addFollow(produzione.getTesta());
                            }
                        } else {
                            LinkedHashSet<Terminale> firstBeta = calculateStringFirst(betaList);
                            // Se la stringa dopo nonTerminale è annullabile
                            if (firstBeta.contains(this.epsilon)) {
                                // Aggiungo la testa solo se è diversa dal nonTerm che sto trattando attualmente
                                if (!produzione.getTesta().equals(nonTerminale)) {
                                    struttura.addFollow(produzione.getTesta());
                                }
                                firstBeta.remove(this.epsilon);
                            }
                            // Aggiungo beta (senza epsilon se c'era)
                            struttura.addToParziale(firstBeta);
                        }
                    }
                    nonTerminaleIndex++;
                }

            }
            resultParziali.put(nonTerminale, struttura);

        }

        boolean cambiato = true;
        while (cambiato) {
            cambiato = false;

            for (Entry<NonTerminale, StrutturaFollow> entry : resultParziali.entrySet()) {
                LinkedHashSet<Terminale> parziale = entry.getValue().getParziale();
                int parzialePrima = parziale.size();
                LinkedHashSet<NonTerminale> follows = entry.getValue().getFollows();
                for (NonTerminale nonTermToAdd : follows) {
                    parziale.addAll(resultParziali.get(nonTermToAdd).getParziale());
                }
                cambiato = !(parzialePrima == parziale.size());
            }
        }

        // Trasferisco i risultati ottenuti nel risultato
        for (NonTerminale nonTerminale : grammatica.getNonTerminali()) {
            result.put(nonTerminale, resultParziali.get(nonTerminale).getParziale());
        }
        return result;
    }

    private class StrutturaFollow {
        LinkedHashSet<Terminale> parziale;
        LinkedHashSet<NonTerminale> follows;

        public StrutturaFollow(Grammatica grammatica, NonTerminale nonTerminale, Terminale FINESTRINGA) {
            this.parziale = new LinkedHashSet<Terminale>();
            this.follows = new LinkedHashSet<NonTerminale>();
            if (grammatica.getPartenza().equals(nonTerminale)) {
                this.parziale.add(FINESTRINGA);
            }
        }

        public void addToParziale(LinkedHashSet<Terminale> terminali) {
            this.parziale.addAll(terminali);
        }

        public void addFollow(NonTerminale nonTerminale) {
            this.follows.add(nonTerminale);
        }

        public LinkedHashSet<Terminale> getParziale() {
            return this.parziale;
        }

        public LinkedHashSet<NonTerminale> getFollows() {
            return this.follows;
        }
    }

    // ! STAMPA DELLA TABELLA FIRST/FOLLOW
    public String firstFollowTable() {

        List<NonTerminale> nonTerminali = new LinkedList<>(this.grammatica.getNonTerminali());
        List<Terminale> terminali = new LinkedList<>(this.grammatica.getTerminali());
        // Non calcolo first e follow di epsilon
        if (terminali.contains(this.epsilon))
            terminali.remove(this.epsilon);
        int numNonTerminali = nonTerminali.size();
        int numTerminali = terminali.size();

        // Costruisci una tabella vuota di altezza 3 (header, first e follow) e
        // larghezza ...
        String[][] table = new String[3][numTerminali +
                numNonTerminali + 1];

        table[0][0] = "simbolo";
        table[1][0] = "first";
        table[2][0] = "follow";

        // Popola la tabella con le intestazioni delle colonne
        int i = 0;
        for (i = 0; i < numNonTerminali; i++) {
            table[0][i + 1] = nonTerminali.get(i).toString();
            NonTerminale nonTerminale = nonTerminali.get(i);
            String first = this.getSymbolFirst(nonTerminale).toString();
            String follow = this.getFollow(nonTerminale).toString();
            table[1][i + 1] = first;
            table[2][i + 1] = follow;
        }

        for (int j = i; j < numNonTerminali + numTerminali; j++) {
            table[0][j + 1] = terminali.get(j - numNonTerminali).toString();
            Terminale terminale = terminali.get(j - numNonTerminali);
            String first = "[" + terminale.toString() + "]";
            table[1][j + 1] = first;
            table[2][j + 1] = "-";
        }

        // Costruisci una stringa rappresentante la tabella
        StringBuilder result = new StringBuilder();
        result.append("\n");
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < numTerminali + numNonTerminali + 1; col++) {
                result.append(table[row][col]);
                // formattazione della tabella: aggiungo un numero di spazi dipendente dalla
                for (int k = table[row][col].length(); k < 20; k++) {
                    if (k == 17)
                        result.append("|");
                    result.append(" ");
                }

            }
            result.append("\n");
        }
        result.append("\n");

        return result.toString();

    }

}
