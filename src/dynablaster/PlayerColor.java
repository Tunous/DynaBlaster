package dynablaster;

public enum PlayerColor {
    WHITE,
    GREEN,
    RED,
    BLUE;
    
    public String getPlayerName() {
        switch (this) {
            case WHITE:
                return "Biały";
            case GREEN:
                return "Zielony";
            case RED:
                return "Czerwony";
            case BLUE:
                return "Niebieski";
            default:
                throw new AssertionError("Unknown player " + name());
        }
    }
}
