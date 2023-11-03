package Implementazione;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public final class ParserUtility {

    public final Terminale FINESTRINGA = new Terminale("$");
    private Grammatica grammatica;
    private Terminale epsilon;

    public ParserUtility(Grammatica grammatica) {
        this.grammatica = grammatica;
        this.epsilon = grammatica.getTermSeEsiste("eps");
    }

    private LinkedHashSet<Simbolo> stringsToSimboli(String... stringhe) {
        LinkedHashSet<Simbolo> simboli = new LinkedHashSet<Simbolo>();

        for (String s : stringhe) {
            Simbolo simbolo = this.grammatica.getTermSeEsiste(s);
            if (simbolo == null) {
                try {
                    simbolo = this.grammatica.getNonTermSeEsiste(s);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Almeno uno dei simboli indicati non esiste: " + e.getMessage());
                }
            }

            if (simbolo == null) {
                throw new IllegalArgumentException("Almeno uno dei simboli indicati non esiste: " + s);
            }

            simboli.add(simbolo);
        }
        return simboli;
    }

    // Metodo pubblico che restituisce la first di una stringa iniziale
    public LinkedHashSet<Terminale> getFirst(String... stringhe)
            throws IllegalArgumentException {

        // Controllo che l'argomento sia un insieme di simboli dell'alfabeto
        if (stringhe.length == 0) {
            throw new IllegalArgumentException("Argomento mancante");
        }

        // Trasformo le stringhe in simboli controllando se esistono nell'alfabeto
        LinkedHashSet<Simbolo> simboli = stringsToSimboli(stringhe);
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
                            .calculateFirst(new LinkedHashSet<>(produzione.getCorpo().getSimboli()), simboli);
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

    private LinkedHashSet<Terminale> calculateFirst(LinkedHashSet<Simbolo> simboli, LinkedHashSet<Simbolo> iniziale)
            throws IllegalArgumentException {
        LinkedHashSet<Terminale> first = new LinkedHashSet<Terminale>();

        Iterator<Simbolo> simboliIterator = simboli.iterator();
        Iterator<Simbolo> inizialeIterator = iniziale.iterator();

        if (simboli.size() != 1 && iniziale.size() != 1) {

            // Posiziono l'iteratore sul primo simbolo non annullabile che trovo in simboli
            while (simboliIterator.hasNext()) {
                LinkedHashSet<Terminale> localFirst = getFirst(simboliIterator.next().toString());
                if (!localFirst.contains(this.epsilon)) {
                    first.addAll(localFirst);
                    break;
                }
            }
            // Posiziono l'iteratore sul primo simbolo non annullabile che trovo in iniziale
            while (inizialeIterator.hasNext()) {
                if (!getFirst(inizialeIterator.next().toString()).contains(this.epsilon)) {
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
                        first.addAll(this.calculateFirst(new LinkedHashSet<>(corpo.getSimboli()), iniziale));

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
            LinkedHashSet<Simbolo> newSimbolo = new LinkedHashSet<Simbolo>();
            newSimbolo.add(simbolo);
            LinkedHashSet<Terminale> first_attuale = this.calculateFirst(newSimbolo, iniziale);
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

    public LinkedHashSet<Terminale> getFollow(String nonTerminaleString)
            throws IllegalArgumentException {
        NonTerminale nonTerminale;
        try {
            nonTerminale = this.grammatica.getNonTermSeEsiste(nonTerminaleString);
        } catch (IllegalArgumentException e) {
            throw e;
        }
        LinkedHashSet<Terminale> result = new LinkedHashSet<Terminale>();
        // Passo 1: se il non terminale è un simbolo di partenza, inserisco $
        if (this.grammatica.getPartenza().equals(nonTerminale)) {
            result.add(this.FINESTRINGA);
        }

        // Passi 2, 3, 4, trovo le produzioni che contengono nonTerminale nel corpo
        for (Produzione produzione : this.grammatica.getProduzioniIfCorpoContains(nonTerminale)) {
            Corpo corpo = produzione.getCorpo();
            // Salvo l'indice di nonTerminale all'interno del corpo
            int nonTerminaleIndex = corpo.getSimboli().indexOf(nonTerminale);

            // Separo beta dall'intero corpo
            List<Simbolo> betaList = corpo.getSimboli().subList(nonTerminaleIndex + 1, corpo.getSimboli().size());

            // Passo 3: se il terminale è alla fine della stringa ed è diverso dalla testa,
            // inserisco Follow(Testa)
            if (betaList.isEmpty()) {
                if (!nonTerminale.equals(produzione.getTesta()))
                    result.addAll(this.getFollow(produzione.getTesta().toString()));
            } else {
                // Trasformo beta in array di stringhe per chiamare ParserUtility.getFirst
                String[] beta = new String[betaList.size()];
                int i = 0;
                for (Simbolo simbolo : betaList) {
                    beta[i++] = simbolo.toString();
                }

                // calcolo la first di beta
                LinkedHashSet<Terminale> firstBeta = this.getFirst(beta);

                // Passo 4: se la first della stringa dopo il terminale contiene epsilon,
                // devo rimuoverlo per aggiungere gli altri terminali. Inoltre, devo aggiungere
                // Follow(Testa)
                if (firstBeta.contains(epsilon)) {
                    firstBeta.remove(this.epsilon);
                    // TODO qui può capitare che torna a calcolare la follow del simbolo di partenza
                    // TODO implementare metodo di controllo della ricorsione anche nella follow
                    if(!produzione.getTesta().equals(nonTerminale))
                        result.addAll(this.getFollow(produzione.getTesta().toString()));
                }
                // Passo 2: se la first della stringa dopo il terminale non contiene epsilon, la
                // inserisco
                result.addAll(firstBeta);

            }
        }

        return result;
    }

    public String firstFollowTable() {

        List<NonTerminale> nonTerminali = new ArrayList<>(this.grammatica.getNonTerminali());
        List<Terminale> terminali = new ArrayList<>(this.grammatica.getTerminali());
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
            String first = this.getFirst(nonTerminale.toString()).toString();
            String follow = this.getFollow(nonTerminale.toString()).toString();
            table[1][i + 1] = first;
            table[2][i + 1] = follow;
        }

        for (int j = i; j < numNonTerminali + numTerminali; j++) {
            table[0][j + 1] = terminali.get(j - numNonTerminali).toString();
            Terminale terminale = terminali.get(j - numNonTerminali);
            String first = this.getFirst(terminale.toString()).toString();
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
