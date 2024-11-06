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
        List<Settler> settlers = new ArrayList<>(simulation.getSettlers());

        int min = settlers.size();
        Map<String, Resource> bestAffectation = new HashMap<>();

        for(int i = 0; i < settlers.size(); i++) {
            Collections.shuffle(settlers);
            Set<String> O = new HashSet<>(simulation.getResources().keySet());
            List<Settler> N = new ArrayList<>(settlers);

            //System.out.println("shuffle : " + settlers.stream().map(Settler::getName).toList()
                    //+ " with O = " + O + "/" + simulation.getResources());
            Set<Settler> I = maxIndependentSet(N, O);

            while(!N.isEmpty()) {
                N.removeAll(I);
                //System.out.println("rest (N\\I) : " + N.stream().map(Settler::getName).toList());
                I = maxIndependentSet(N, O);
            }

            int j = simulation.getJealousNumber();
            //System.out.println("j = " + j + " | m = " + m);
            if(j < min) {
                min = j;
                for(Settler s : settlers) {
                    bestAffectation.put(s.getName(), s.getAffectation());
                }
            }

            simulation.clearAffectations();
        }

        System.out.println("Best affectation found with j = " + min + " : " + bestAffectation);
        simulation.setAffectations(bestAffectation);
    }

    private static Set<Settler> maxIndependentSet(List<Settler> N, Set<String> O) {
        Queue<Settler> Q = new ArrayDeque<>(N);
        Set<Settler> I = new HashSet<>();

        while(!Q.isEmpty()) {
            Settler settler = Q.poll();
            I.add(settler);
            Set<Settler> neighbors = settler.getBadRelations();
            Q.removeAll(neighbors);
        }

        //System.out.println("update independents (I) : " + I.stream().map(Settler::getName).toList());
        for(Settler independent : I) {
            Resource pi = getBestAffectation(independent, O);
            independent.setAffectation(pi);
            //System.out.println("Give " + pi + " to " + independent.getName());
            assert pi != null;
            O.remove(pi.getName());
        }

        return I;
    }

    private static Resource getBestAffectation(Settler s, Set<String> available) {
        //System.out.println(s.getName() + "/" + Arrays.toString(s.getPreferences()) + "/" + available);
        for(Resource pref : s.getPreferences()) {
            if(available.contains(pref.getName())) return pref;
        }
        return null;
    }
}
