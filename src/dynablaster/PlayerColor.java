package dynablaster;

public enum PlayerColor {
    WHITE("player-white.png"),
    GREEN("player-green.png");
    
    public final String fileName;
    
    PlayerColor(String fileName) {
        this.fileName = fileName;
    }
}
