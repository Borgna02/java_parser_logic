package Implementazione;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class Grammatica {
    private LinkedHashSet<NonTerminale> nonTerminali;
    private LinkedHashSet<Terminale> terminali;
    private LinkedHashSet<Produzione> produzioni;
    private NonTerminale partenza;

    public NonTerminale getPartenza() {
        return this.partenza;
    }

    public void setPartenza(NonTerminale nonTerminale) {
        this.partenza = nonTerminale;

    }

    public Grammatica() {
        this.produzioni = new LinkedHashSet<Produzione>();
    }

    public LinkedHashSet<NonTerminale> getNonTerminali() {
        return nonTerminali;
    }

    public NonTerminale getNonTermSeEsiste(String nonTerminale) {
        for (NonTerminale obj : this.nonTerminali) {
            if (obj.equals(new NonTerminale(nonTerminale)))
                return obj;
        }
        return null;
    }

    public void addNonTerminale(String stringa) {
        NonTerminale newNonTerminale = new NonTerminale(stringa);
        this.nonTerminali.add(newNonTerminale);
    }

    public void addNonTerminale(NonTerminale newNonTerminale) {
        this.nonTerminali.add(newNonTerminale);
    }

    public void setNonTerminali(LinkedHashSet<NonTerminale> nonTerminali) {
        this.nonTerminali = nonTerminali;
    }

    public void setNonTerminali(NonTerminale... nonTerminali) {
        this.nonTerminali = new LinkedHashSet<NonTerminale>();
        for (NonTerminale nonTerminale : nonTerminali) {
            this.nonTerminali.add(nonTerminale);
        }
    }

    public void setNonTerminali(String... strings) throws IllegalArgumentException {
        this.nonTerminali = new LinkedHashSet<NonTerminale>();
        for (String string : strings) {
            try {
                this.nonTerminali.add(new NonTerminale(string));
            } catch (IllegalArgumentException e) {
                throw e;
            }
        }
    }

    public LinkedHashSet<Terminale> getTerminali() {
        return terminali;
    }

    public Terminale getTermSeEsiste(String terminale) {
        for (Terminale obj : this.terminali) {
            if (obj.equals(new Terminale(terminale))) {
                return obj;
            }
        }

        return null;
    }

    public void setTerminali(LinkedHashSet<Terminale> terminali) {
        this.terminali = terminali;
    }

    public void setTerminali(Terminale... terminali) {
        this.terminali = new LinkedHashSet<Terminale>();
        for (Terminale terminale : terminali) {
            this.terminali.add(terminale);
        }
    }

    public void setTerminali(String... strings) {
        this.terminali = new LinkedHashSet<Terminale>();
        for (String string : strings) {
            this.terminali.add(new Terminale(string));
        }
    }

    public LinkedHashSet<Produzione> getProduzioni() {
        return produzioni;
    }

    public LinkedHashSet<Produzione> getProduzioniByTesta(String string) throws IllegalArgumentException {
        LinkedHashSet<Produzione> result = new LinkedHashSet<Produzione>();
        try {
            NonTerminale testa = new NonTerminale(string);
            for (Produzione produzione : this.produzioni) {
                if (produzione.getTesta().equals(testa)) {
                    result.add(produzione);
                }
            }
        } catch (IllegalArgumentException e) {
            throw e;
        }
        ;

        return result;
    }

    public LinkedHashSet<Produzione> getProduzioniByTesta(Simbolo simbolo) {

        LinkedHashSet<Produzione> result = new LinkedHashSet<Produzione>();
        if (simbolo instanceof NonTerminale) {
            for (Produzione produzione : this.produzioni) {
                if (produzione.getTesta().equals(simbolo)) {
                    result.add(produzione);
                }
            }
        }
        return result;
    }

    public LinkedHashSet<Produzione> getProduzioniByTestaNonRicorsive(Simbolo simbolo) {
        LinkedHashSet<Produzione> result = new LinkedHashSet<Produzione>();
        if (simbolo instanceof NonTerminale) {
            for (Produzione produzione : this.produzioni) {
                if (produzione.getTesta().equals(simbolo)
                        && !produzione.getTesta().equals(produzione.getCorpo().getSimboli().get(0))) {
                    result.add(produzione);
                }
            }
        }
        return result;
    }

    public LinkedHashSet<Produzione> getProduzioniIfCorpoContains(Simbolo simbolo) {
        LinkedHashSet<Produzione> result = new LinkedHashSet<Produzione>();
        for (Produzione produzione : this.produzioni) {
            if (produzione.getCorpo().getSimboli().contains(simbolo)) {
                result.add(produzione);
            }
        }
        return result;
    }

    public void setProduzioni(LinkedHashSet<Produzione> produzioni) {
        this.produzioni = produzioni;
    }

    public void addProduzione(String testa, Corpo corpo) {
        this.produzioni.add(new Produzione(this.getNonTermSeEsiste(testa), corpo));
    }

    public String toStringProduzioni() {
        String result = "";
        int i = 0;
        for (Produzione produzione : this.produzioni) {
            result += i + ": " + produzione.toString() + "\n";
            i++;
        }
        return result;
    }

    @Override
    public String toString() {
        return "\nTerminali: " + this.terminali + "\nNon terminali: " + this.nonTerminali + "\nSimboli di partenza: "
                + this.partenza + "\nProduzioni: \n" + this.toStringProduzioni();
    }

    public boolean makeNonRecursive() {
        boolean isRecursive = false;
        boolean modified = false;
        ArrayList<NonTerminale> terminaliAttuali = new ArrayList<>(this.nonTerminali);
        for (NonTerminale nonTerminale : terminaliAttuali) {
            LinkedHashSet<Produzione> produzioniByNonTerminale = new LinkedHashSet<>(
                    getProduzioniByTesta(nonTerminale));
            for (Produzione produzione : produzioniByNonTerminale) {
                // Se il nonTerminale causa una ricorsione immediata
                if (produzione.getTesta().equals(produzione.getCorpo().getSimboli().get(0))) {
                    isRecursive = true;
                    break;
                }
            }
            if (isRecursive) {
                isRecursive = false;
                modified = true;
                // Rimuovo le ricorsioni immediate per quel non terminale
                this.produzioni.removeAll(produzioniByNonTerminale);
                NonTerminale testa = nonTerminale;
                // Se non l'ho già creato, creo un nuovo non terminale che aggiunge un apice
                String newNonTerminaleString = testa.toString() + '\'';
                NonTerminale newNonTerminale = this.getNonTermSeEsiste(newNonTerminaleString);
                if(newNonTerminale == null) {
                    newNonTerminale = new NonTerminale(newNonTerminaleString);
                    this.addNonTerminale(newNonTerminale);
                }
                this.produzioni.add(new Produzione(newNonTerminale, new Corpo(this.getTermSeEsiste("eps"))));
                
                for (Produzione produzione : produzioniByNonTerminale) {
                    Corpo corpo = produzione.getCorpo();

                    // Caso 1: se A -> Aalpha
                    if (testa.equals(corpo.getSimboli().get(0))) {
                        // Creo il nuovo corpo della forma A' -> alphaA'
                        ArrayList<Simbolo> newCorpo = new ArrayList<Simbolo>(corpo.getSimboli());
                        newCorpo.remove(0);
                        newCorpo.add(newNonTerminale);
                        Produzione newProduzione = new Produzione(newNonTerminale, new Corpo(newCorpo));
                        this.produzioni.add(newProduzione);
                    }
                    // Caso 2: se A -> beta
                    else {
                        // Se la prodizione ha il corpo uguale a epsilon la ignoro
                        if(!corpo.getSimboli().contains(this.getTermSeEsiste("eps"))) {

                            // Creo il nuovo corpo della forma A -> betaA'
                            ArrayList<Simbolo> newCorpo = new ArrayList<Simbolo>(corpo.getSimboli());
                            newCorpo.add(newNonTerminale);
                            Produzione newProduzione = new Produzione(testa, new Corpo(newCorpo));
                            this.produzioni.add(newProduzione);
                        }
                    }
                }

            }

        }

        if (modified) {
            System.out.println("La grammatica è ricorsiva, modifico...");
        }
        return modified;
    }
}
