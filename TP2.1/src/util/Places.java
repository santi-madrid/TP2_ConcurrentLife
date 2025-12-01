package util;

class Places{
    String name;
    int tokens;

    Places(String name, int tokens) {
        this.name = name;
        this.tokens = tokens;
    }

    public String getName() {
        return name;
    }

    public int getTokens() {
        return tokens;
    }

    public void addTokens(int count) {
        this.tokens += count;
    }

    public void removeTokens(int count) {
        this.tokens -= count;
    }
}

