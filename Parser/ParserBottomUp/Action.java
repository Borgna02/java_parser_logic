package Implementazione.Parser.ParserBottomUp;

public class Action {
    public enum ActionType {
        SHIFT,
        REDUCE,
        ACCEPT,
        GOTO
    }

    private ActionType actionType;
    private Integer number;

    public Action(ActionType actionType, Integer number) {
        this.actionType = actionType;
        this.number = number;
    }

    public Action() {
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public Integer getNumber() {
        return number;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((actionType == null) ? 0 : actionType.hashCode());
        result = prime * result + ((number == null) ? 0 : number.hashCode());
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
        Action other = (Action) obj;
        if (actionType != other.actionType)
            return false;
        if (number == null) {
            if (other.number != null)
                return false;
        } else if (!number.equals(other.number))
            return false;
        return true;
    }

    @Override
    public String toString() {
        String actionString = "";
        switch (this.actionType) {
            case SHIFT:
                actionString = "S" + this.number;
                break;
            case REDUCE:
                actionString = "R" + this.number;
                break;
            case ACCEPT:
                actionString = "ACC";
                break;
            case GOTO:
                actionString = "G" + this.number;
                break;

            default:
                break;
        }
        return actionString;
    }
}
