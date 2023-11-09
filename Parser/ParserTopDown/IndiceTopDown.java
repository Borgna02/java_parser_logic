package Implementazione.Parser.ParserTopDown;

import Implementazione.Domain.NonTerminale;
import Implementazione.Domain.Terminale;

public class IndiceTopDown {
        private Terminale terminale;
        private NonTerminale nonTerminale;

        public IndiceTopDown(Terminale terminale, NonTerminale nonTerminale) {
            this.terminale = terminale;
            this.nonTerminale = nonTerminale;
        }

        public Terminale getTerminale() {
            return terminale;
        }

        public void setTerminale(Terminale terminale) {
            this.terminale = terminale;
        }

        public NonTerminale getNonTerminale() {
            return nonTerminale;
        }

        public void setNonTerminale(NonTerminale nonTerminale) {
            this.nonTerminale = nonTerminale;
        }

        @Override
        public String toString() {
            return "[" + terminale + ", " + nonTerminale + "]";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((terminale == null) ? 0 : terminale.hashCode());
            result = prime * result + ((nonTerminale == null) ? 0 : nonTerminale.hashCode());
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
            IndiceTopDown other = (IndiceTopDown) obj;
            if (terminale == null) {
                if (other.terminale != null)
                    return false;
            } else if (!terminale.equals(other.terminale))
                return false;
            if (nonTerminale == null) {
                if (other.nonTerminale != null)
                    return false;
            } else if (!nonTerminale.equals(other.nonTerminale))
                return false;
            return true;
        }

   

    }
