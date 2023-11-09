package Implementazione.Parser.ParserTopDown;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import Implementazione.Domain.Grammatica;
import Implementazione.Domain.NonTerminale;
import Implementazione.Domain.Produzione;
import Implementazione.Domain.Simbolo;
import Implementazione.Domain.Terminale;
import Implementazione.Parser.First;
import Implementazione.Parser.Follow;
import Implementazione.Parser.ParserUtility;

public class ParserTopDown {

    private Grammatica grammatica;
    private ParserUtility parserUtility;
    private Terminale epsilon;
    private boolean isLL;
    private ParsingTable parsingTable;

    public ParserTopDown(Grammatica grammatica) {
        this.grammatica = grammatica;
        this.parserUtility = new ParserUtility(grammatica);
        this.epsilon = grammatica.getTermSeEsiste("eps");
        this.parsingTable = calculateParsingTable();
    }

    public ParsingTable getParsingTable() {
        return this.parsingTable;
    }

    public List<Produzione> parsing(String... strings) throws Exception {
        LinkedList<Terminale> input = new LinkedList<Terminale>();
        LinkedList<Produzione> result = new LinkedList<Produzione>();
        final Terminale FINESTRINGA = ParserUtility.FINESTRINGA;

        // Controllo che l'input sia insieme di terminali
        for (String str : strings) {
            Terminale term = this.grammatica.getTermSeEsiste(str);
            if (term != null) {
                input.add(term);
            } else {
                throw new IllegalArgumentException(
                        "L'input deve essere un insieme di terminali esistenti, " + str + " non valido");
            }
        }

        // Aggiungo il FINESTRINGA per definire la fine dell'input
        input.add(FINESTRINGA);

        // Definisco lo stack e inserisco il simbolo di partenza e FINESTRINGA
        Stack<Simbolo> stack = new Stack<Simbolo>();
        stack.push(FINESTRINGA);
        stack.push(this.grammatica.getPartenza());

        // Definisco matched per vedere la parte di stringa finora matchata
        LinkedList<Terminale> matched = new LinkedList<Terminale>();

        // Finché il top dello stack è diverso da FINESTRINGA
        System.out.println("Matched: " + matched + ", Stack: " + stack + ",Input: " + input + "\n");
        while (!stack.peek().equals(FINESTRINGA)) {
            // 1: se peek è uguale al primo elemento della stringa
            if (stack.peek().equals(input.get(0))) {
                // input contiene solo terminali, quindi se sono uguali vuol dire che l'elemento
                // che rimuovo dallo stack è un terminale
                matched.add((Terminale) stack.pop());
                List<Terminale> newInput = input.subList(1, input.size() - 1);
                input = new LinkedList<>();
                for (Terminale terminale : newInput) {
                    input.add(terminale);
                }
                input.add(FINESTRINGA);
            }
            // 2: se peek è un terminale ma non ha fatto matching nell'if precedente, allora
            // errore
            else if (stack.peek() instanceof Terminale) {
                throw new Exception("Errore: il parsing non può essere eseguito");
            }
            // 3: se peek è un terminale, se non c'è una produzione in M[peek, input[0]],
            // allora errore
            else if (this.parsingTable.get((NonTerminale) stack.peek()).get(input.get(0)) == null) {
                throw new Exception("Errore: il parsing non può essere eseguito");
            }
            // 4: se c'è una produzione la aggiungo al risultato ed aggiungo il corpo allo
            // stack
            else {
                // ! IMPORTANTE: nel caso di conflitti sceglie sempre la produzione inserita per
                // prima
                Produzione produzione = this.parsingTable
                        .get((NonTerminale) stack.peek()).get(input.get(0)).get(0);
                result.add(produzione);
                stack.pop();
                LinkedList<Simbolo> simboliCorpo = produzione.getCorpo();

                for (int i = simboliCorpo.size() - 1; i >= 0; i--) {
                    if (!simboliCorpo.get(i).equals(this.epsilon))
                        stack.push(simboliCorpo.get(i));
                }
            }

            System.out.println("Matched: " + matched + ", Stack: " + stack + ",Input: " + input + "\n");
        }

        return result;

    }

    private ParsingTable calculateParsingTable() {
        ParsingTable parsingTable = new ParsingTable();

        for (NonTerminale nonTerminale : grammatica.getNonTerminali()) {
            LinkedHashMap<Terminale, LinkedList<Produzione>> entry = new LinkedHashMap<>();

            for (Terminale terminale : grammatica.getTerminali()) {
                if (!terminale.equals(this.epsilon)) {
                    entry.put(terminale, new LinkedList<>());
                }
            }
            entry.put(ParserUtility.FINESTRINGA, new LinkedList<>());
            parsingTable.put(nonTerminale, entry);
        }

        // Per ogni produzione A -> alpha in G
        for (Produzione produzione : grammatica.getProduzioni()) {
            // Per ogni terminale a in First(alpha) diverso da epsilon inserisco A -> alpha
            // in M[A, a]
            First firstAlpha = new First();
            NonTerminale testa = produzione.getTesta();
            // Se il corpo è epsilon, posso saltare completamente questo passaggio perché
            // First(alpha) non esiste
            if (!produzione.getCorpo().get(0).equals(this.epsilon)) {
                firstAlpha = this.parserUtility
                        .calculateStringFirst(produzione.getCorpo());
                for (Terminale terminale : firstAlpha) {
                    if (!terminale.equals(this.epsilon)) {
                        if (parsingTable.get(testa).get(terminale).size() >= 1
                                && !parsingTable.get(testa).get(terminale).contains(produzione)) {
                            isLL = false;
                        }
                        parsingTable.get(testa).get(terminale).add(produzione);
                    }
                }
            }
            // Se epsilon in First(alpha), per ogni terminale b in Follow(A)
            // diverso da FINESTRINGA inserisco A -> alpha in M[A, b]
            if (firstAlpha.contains(this.epsilon) || produzione.getCorpo().get(0).equals(this.epsilon)) {
                Follow followTesta = this.parserUtility.getFollow(testa);
                for (Terminale terminale : followTesta) {
                    if (!terminale.equals(ParserUtility.FINESTRINGA)) {

                        if (parsingTable.get(testa).get(terminale).size() >= 1
                                && !parsingTable.get(testa).get(terminale).contains(produzione)) {
                            isLL = false;
                        }
                        parsingTable.get(testa).get(terminale).add(produzione);
                    }
                }
                // Se epsilon in First(alpha) e FINESTRINGA in Follow(A), inserisco A -> alpha
                // in M[A,FINESTRINGA]
                if (followTesta.contains(ParserUtility.FINESTRINGA)) {
                    if (parsingTable.get(testa).get(ParserUtility.FINESTRINGA).size() >= 1
                            && !parsingTable.get(testa).get(ParserUtility.FINESTRINGA)
                                    .contains(produzione)) {
                        isLL = false;
                    }
                    parsingTable.get(testa).get(ParserUtility.FINESTRINGA).add(produzione);
                }
            }

        }
        if (!isLL) {
            System.out.println("La grammatica non è LL, il parsing potrebbe essere ambiguo...\n");
        }
        return parsingTable;
    }
}
