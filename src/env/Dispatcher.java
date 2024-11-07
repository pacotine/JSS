package env;

import model.Resource;
import model.Settler;

import java.util.*;

public class Dispatcher {
    private final Simulation simulation;

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

    public void maxLEFDispatch(int inst) {
        List<Settler> settlers = new ArrayList<>(simulation.getSettlers());

        int min = settlers.size();
        Map<String, Resource> bestAffectation = new HashMap<>();

        for(int i = 0; i < inst; i++) {
            Collections.shuffle(settlers);
            List<Settler> N = new ArrayList<>(settlers);

            //System.out.println("shuffle : " + settlers.stream().map(Settler::getName).toList()
                    //+ " with O = " + O + "/" + simulation.getResources());
            Set<Settler> I = maxIndependentSet(N);

            while(!N.isEmpty()) {
                N.removeAll(I);
                //System.out.println("rest (N\\I) : " + N.stream().map(Settler::getName).toList());
                I = maxIndependentSet(N);
            }

            int j = simulation.getJealousNumber();
            System.out.println("j = " + j + " | m = " + min);
            if(j < min) {
                min = j;
                for(Settler s : settlers) {
                    bestAffectation.put(s.getName(), s.getAffectation());
                }
            }

            simulation.clear();
        }

        System.out.println("Best affectation found with j = " + min + " : " + bestAffectation);
        simulation.setAffectations(bestAffectation);
    }

    private static Set<Settler> maxIndependentSet(List<Settler> N) {
        Queue<Settler> Q = new ArrayDeque<>(N);
        Set<Settler> I = new HashSet<>();

        while(!Q.isEmpty()) {
            Settler independent = Q.poll();
            I.add(independent);
            Set<Settler> neighbors = independent.getBadRelations();
            Q.removeAll(neighbors);

            Resource pi = getBestAffectation(independent);
            //System.out.println("Give " + pi + " to " + independent.getName());
            assert pi != null;
            independent.setAffectation(pi);
            pi.setAffected(true);
        }

        return I;
    }

    //gives the best available (O = map(r -> r not affected))
    private static Resource getBestAffectation(Settler s) {
        //System.out.println(s.getName() + "/" + Arrays.toString(s.getPreferences()) + "/" + available);
        for(Resource pref : s.getPreferences()) {
            if(!pref.isAffected()) return pref;
        }
        return null;
    }
}
