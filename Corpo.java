package Implementazione;
import java.util.LinkedList;

public class Corpo extends LinkedList<Simbolo> {

    public Corpo(LinkedList<Simbolo> simboli) {
        this.addAll(simboli);
    }

    // Costruttore con i simboli
    public Corpo(Simbolo... simboli) {
        for (Simbolo simbolo : simboli) {
            this.add(simbolo);
        }
    }

    // Costruttore con le stringhe
    /**
     * Costruisce il corpo di una produzione passando come argomenti i singoli
     * simboli (terminali o non) sotto forma di stringhe separate.
     * Se i simboli non fanno parte dell'alfabeto restituisce l'eccezione
     * IllegalArgumentException
     * 
     * @param grammatica
     * @param stringhe
     * @throws IllegalArgumentException
     * 
     */
    public Corpo(Grammatica grammatica, String... stringhe) throws IllegalArgumentException {
        for (String s : stringhe) {
            Simbolo simbolo = grammatica.getTermSeEsiste(s);
            // se s non è un terminale
            if (simbolo == null)
                try {
                    simbolo = grammatica.getNonTermSeEsiste(s);
                } catch (Exception e) {
                    // se s non ha la struttura adatta per essere un non terminale
                    throw new IllegalArgumentException("Almeno uno dei simboli indicati non esiste, " + e.getMessage());
                }
            // se s non è neanche non terminale
            if (simbolo == null)
                throw new IllegalArgumentException("Almeno uno dei simboli indicati non esiste: " + s);
            this.add(simbolo);
        }
    }

    

    

    @Override
    public String toString() {
        String result = "";
        for (Simbolo s : this) {
            result += s;
        }
        return result;
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Corpo other = (Corpo) obj;
        if (!this.equals(other))
            return false;
        return true;
    }

    public String[] toStrings() {
        String[] result = new String[this.size()];
        int i = 0;
        for (Simbolo simbolo : this) {
            result[i] = simbolo.toString();
            i++;   
        }

        return result;
    }
}
