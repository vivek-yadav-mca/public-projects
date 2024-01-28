package dummydata.android;

public enum Game {

    NORMAL_SPIN("normal_spin"),
    NORMAL_SCRATCH("normal_scratch"),
    NORMAL_FLIP("normal_flip"),
    TOTAL_CASH_COINS("total_coins");

    String id;

    Game(String id) {
        this.id = id;
    }

    public static Game getInstance(String name) {
        for (Game game : Game.values()) {
            if (game.getId().equalsIgnoreCase(name)) {
                return game;
            }
        }
        return null;
    }

    public String getId() {
        return id;
    }
}
