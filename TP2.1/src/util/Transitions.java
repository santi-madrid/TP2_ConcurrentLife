package util;
import java.util.*;

public class Transitions {
    String name;
    List<Places> inputPlaces;
    List<Places> outputPlaces;

    Transitions(String name, List<Places> inputPlaces, List<Places> outputPlaces) {
        this.name = name;
        this.inputPlaces = inputPlaces;
        this.outputPlaces = outputPlaces;
    }

    public String getName() {
        return name;
    }

    public List<Places> getInputPlaces() {
        return inputPlaces;
    }

    public List<Places> getOutputPlaces() {
        return outputPlaces;
    }

    public boolean isEnabled() {
        for (Places place : inputPlaces) {
            if (place.getTokens() == 0) {
                return false;
            }
        }
        return true;
    }

    public void fire() {
        if (isEnabled()) {
            for (Places place : inputPlaces) {
                place.removeTokens(1);
            }
            for (Places place : outputPlaces) {
                place.addTokens(1);
            }
            System.out.println("Transition " + name + " fired.");
        } else {
            System.out.println("Transition " + name + " cannot be fired.");
        }
    }
}
