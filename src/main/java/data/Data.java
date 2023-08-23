package data;

public enum Data {

    API_KEY("YOUR_API_KEY"),
    DISCORD_TOKEN("YOUR_DISCORD_TOKEN");

    private final String key;

    Data(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
