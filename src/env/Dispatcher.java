package env;

import model.Resource;
import model.Settler;

import java.util.*;

public class Dispatcher {
    private Simulation simulation;

    public Dispatcher(Simulation simulation) {
        this.simulation = simulation;
    }

    public void linearDispatch() {
        List<Settler> settlers = simulation.getSettlers();
        for(Settler settler : settlers) {
            Resource[] preferences = settler.getPreferences();
            int i = 0;
            Resource preference = preferences[i];
            while(preference.isAffected()) {
                preference = preferences[++i];
            }
            simulation.affect(settler.getName(), preference.getName());
        }
    }

    public void maxLEFDispatch() {
        Set<Settler> I = new HashSet<>();
        Queue<Settler> N = new ArrayDeque<>(simulation.getSettlers());
        Set<Settler> NI = new HashSet<>();
        Set<String> O = simulation.getResources().keySet();

        while(!N.isEmpty()) {
            Settler settler = N.poll();
            I.add(settler);
            Set<Settler> neighbors = settler.getBadRelations();
            N.removeAll(neighbors);

            NI.addAll(neighbors);
        }

        for(Settler independent : I) {
            Resource pi = getBestAffectation(independent, O);
            independent.setAffectation(pi);
            assert pi != null;
            O.remove(pi.getName());
        }

        for(Settler rest : NI) {
            Resource pi = getBestAffectation(rest, O);
            rest.setAffectation(pi);
            assert pi != null;
            O.remove(pi.getName());
        }
    }

    private static Resource getBestAffectation(Settler s, Set<String> available) {
        //System.out.println(s.getName() + "/" + Arrays.toString(s.getPreferences()) + "/" + available);
        for(Resource pref : s.getPreferences()) {
            if(available.contains(pref.getName())) return pref;
        }
        return null;
    }
}
