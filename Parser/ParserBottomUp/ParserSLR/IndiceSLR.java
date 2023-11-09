package Implementazione.Parser.ParserBottomUp.ParserSLR;

public class IndiceSLR {
        private ItemSetSLR itemSet;
        private int itemSetIndex;

        public IndiceSLR(ItemSetSLR itemSet, int itemSetIndex) {
            this.itemSet = itemSet;
            this.itemSetIndex = itemSetIndex;
        }

        public ItemSetSLR getItemSet() {
            return this.itemSet;
        }

        public int getItemSetIndex() {
            return this.itemSetIndex;
        }



        @Override
        public String toString() {
            return "IndiceSLR [itemSet=" + itemSet + ", itemSetIndex=" + itemSetIndex + "]";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((itemSet == null) ? 0 : itemSet.hashCode());
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
            IndiceSLR other = (IndiceSLR) obj;
            if (itemSet == null) {
                if (other.itemSet != null)
                    return false;
            } else if (!itemSet.equals(other.itemSet))
                return false;
            return true;
        }

       

    }
