package main.simulation;

import main.model.Resource;
import main.model.Settler;

import java.util.*;

/**
 * A <i>dispatcher</i> is a metaphor of a centralized perspective of an allocation problem. Here, we can consider
 * the dispatcher like the chief of the colony. The chief knows the preferences and relationships of all the settlers in the colony.
 * He is the only person that can give (affect) a resource to a settler. His goal is to minimize the envy for every settler in the colony.
 */
public class Dispatcher {
    private final Simulation simulation;

    /**
     * Constructs a {@link Dispatcher} for the specified simulation.
     * @param simulation the simulation within the dispatcher is trying to solve the problem
     */
    public Dispatcher(Simulation simulation) {
        this.simulation = simulation;
    }

    /**
     * The linear method consists of assigning each settler his or her preferred resource from among the available resources,
     * as they progress through a linear path.
     * <br><br>
     * This method rarely yields a solution close to the optimum,
     * and the order in which the resources are allocated has a major influence on the result.
     * On the other hand, it gives an acceptable solution
     * (in the sense that each settler receives a distinct resource) in polynomial time.
     */
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

    /**
     * The switch dispatch consists of brute forcing the problem, by making {@code k} random switches and
     * keeping the changes only if it's a better configuration for this simulation.
     * This method is also an approximation algorithm, however, compared to {@link Dispatcher#maxLEFDispatch(int)}
     * it is far less efficient and effective.
     * @param k the number of switches
     */
    public void switchDispatch(int k) {
        int i = 0;
        linearDispatch();
        List<Settler> settlers = simulation.getSettlers();
        while(i < k) {
            Settler p = settlers.get(new Random().nextInt(settlers.size()));
            int r = p.getBadRelations().isEmpty() ? 0 : new Random().nextInt(p.getBadRelations().size());
            Settler q = p.getBadRelations().stream()
                    .skip(r)
                    .findFirst()
                    .orElse(null);
            if(q != null) {
                String pName = p.getName();
                String qName = q.getName();
                int j = simulation.getJealousNumber();
                simulation.switchAffectations(pName, qName);
                if (simulation.getJealousNumber() >= j) {
                    simulation.switchAffectations(pName, qName); //rolling back
                }
            }
            i++;
        }
    }

    /**
     * The MAX-LEF algorithm is a LEF (local envy freeness) maximization algorithm, i.e. it seeks to maximize
     * a settler's local envy freeness.
     * This algorithm is known to be NP-complete
     * (as is the DEC-LEF algorithm, which determines whether there is a solution without any jealousy),
     * so we use an approximation algorithm.
     * For a maximization problem, an algorithm is ρ-approximate, with p ∈ [0, 1], if it outputs a solution
     * whose value is at least p-times the optimal value, for any instance.
     * <br>
     * More precisely, the algorithm implemented here is a {@code |I|/n}-approximate algorithm of MAX-LEF.
     * <br>
     * So, in order to obtain better results and tend towards the optimal, we repeat this algorithm on various instances
     * of the simulation with a different running order each time
     * (each instance can give a different set of independent settlers {@code I}, and thus play on the result).
     * @param inst the number of instances of this algorithm's execution
     */
    public void maxLEFDispatch(int inst) {
        List<Settler> settlers = new ArrayList<>(simulation.getSettlers());

        int min = settlers.size();
        Map<String, Resource> bestAffectation = new HashMap<>();

        for(int i = 0; i < inst; i++) {
            Collections.shuffle(settlers);
            List<Settler> N = new ArrayList<>(settlers);

            Set<Settler> I = maxIndependentSet(N);

            while(!N.isEmpty()) {
                N.removeAll(I);
                I = maxIndependentSet(N);
            }

            int j = simulation.getJealousNumber();
            if(j < min) {
                min = j;
                for(Settler s : settlers) {
                    bestAffectation.put(s.getName(), s.getAffectation());
                }
            }

            simulation.clear();
        }
        simulation.setAffectations(bestAffectation);
    }

    /**
     * Returns the set {@code I} of independents settlers. This method is a step of the MAX-LEF (approximate) algorithm.
     * <br>
     * See {@link Dispatcher#maxLEFDispatch(int)} for more information.
     * @param N the initial set of settlers
     * @return the set {@code I} of independents settlers in the context of MAX-LEF approximate
     */
    private static Set<Settler> maxIndependentSet(List<Settler> N) {
        Queue<Settler> Q = new ArrayDeque<>(N);
        Set<Settler> I = new HashSet<>();

        while(!Q.isEmpty()) {
            Settler independent = Q.poll();
            I.add(independent);
            Set<Settler> neighbors = independent.getBadRelations();
            Q.removeAll(neighbors);

            Resource pi = getBestAffectation(independent);
            assert pi != null;
            independent.setAffectation(pi);
            pi.setAffected(true);
        }

        return I;
    }

    /**
     * Returns the specified settler's preferred resource from the list of available resources.
     * @param s the settler looking for the best resource
     * @return the settler's preferred resource from the list of available resources.
     */
    private static Resource getBestAffectation(Settler s) {
        //gives the best available (O = map(r -> r not affected))
        for(Resource pref : s.getPreferences()) {
            if(!pref.isAffected()) return pref;
        }
        return null;
    }
}
