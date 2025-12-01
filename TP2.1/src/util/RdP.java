package util;
import java.util.*;
import java.util.Vector;

public class RdP {
    private volatile Map<String, Places> places;
    private volatile List<Transitions> transitions;
    private volatile Vector<Integer> senTransitions;

    public RdP() {
        places = new HashMap<>();
        transitions = new ArrayList<>();
        senTransitions = new Vector<>();
        for (int i =0;i<12;i++){
            senTransitions.add(0);
        }

        // Initialize places
        places.put("P0", new Places("P0", 5));
        places.put("P1", new Places("P1", 1));
        places.put("P2", new Places("P2", 0));
        places.put("P3", new Places("P3", 0));
        places.put("P4", new Places("P4", 5));
        places.put("P5", new Places("P5", 0));
        places.put("P6", new Places("P6", 1));
        places.put("P7", new Places("P7", 1));
        places.put("P8", new Places("P8", 0));
        places.put("P9", new Places("P9", 0));
        places.put("P10", new Places("P10", 1));
        places.put("P11", new Places("P11", 0));
        places.put("P12", new Places("P12", 0));
        places.put("P13", new Places("P13", 0));
        places.put("P14", new Places("P14", 0));

        // Initialize transitions
        transitions.add(0,new Transitions("T0", List.of(places.get("P0"),places.get("P1"),places.get("P4")), List.of(places.get("P2"))));
        transitions.add(1,new Transitions("T1", List.of(places.get("P2")), List.of(places.get("P3"),places.get("P1"))));
        transitions.add(2,new Transitions("T2", List.of(places.get("P3"),places.get("P6")), List.of(places.get("P4"), places.get("P5"))));
        transitions.add(3,new Transitions("T3", List.of(places.get("P3"),places.get("P7")), List.of(places.get("P8"), places.get("P4"))));
        transitions.add(4,new Transitions("T4", List.of(places.get("P8")), List.of(places.get("P7"),places.get("P9"))));
        transitions.add(5,new Transitions("T5", List.of(places.get("P5")), List.of(places.get("P6"), places.get("P9"))));
        transitions.add(6,new Transitions("T6", List.of(places.get("P9"),places.get("P10")), List.of(places.get("P11"))));
        transitions.add(7,new Transitions("T7", List.of(places.get("P9"),places.get("P10")), List.of(places.get("P12"))));
        transitions.add(8,new Transitions("T8", List.of(places.get("P12")), List.of(places.get("P10"),places.get("P14"))));
        transitions.add(9,new Transitions("T9", List.of(places.get("P11")), List.of(places.get("P13"))));
        transitions.add(10,new Transitions("T10", List.of(places.get("P13")), List.of(places.get("P10"),places.get("P14"))));
        transitions.add(11,new Transitions("T11", List.of(places.get("P14")), List.of(places.get("P0"))));
    }

    public boolean fireTransition(int transition) {
            if (transitions.get(transition).isEnabled()) {
                transitions.get(transition).fire();
                return true;
            }
            return false;
    }

    public void setSenTransitions() {
        int j = 0;
        for (int i = 0; i < transitions.size(); i++) {
            if (transitions.get(i).isEnabled()) {
                senTransitions.set(j, 1);
            } else {
                senTransitions.set(j, 0);
            }
            j++;
        }
    }

    public Vector<Integer> getSenTransitions() {
        setSenTransitions();
        return senTransitions;
    }

    public void displayState() {
        System.out.println("Current state of the Petri net:");
        for (Places place : places.values()) {
            System.out.println(place.getName() + ": " + place.getTokens() + " tokens");
        }
    }

}
