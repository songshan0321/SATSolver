package sat;

import immutable.ImList;
import sat.env.Bool;
import sat.env.Environment;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.PosLiteral;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple DPLL SAT solver. See http://en.wikipedia.org/wiki/DPLL_algorithm
 */
public class SATSolver {
    /**
     * Solve the problem using a simple version of DPLL with backtracking and
     * unit propagation. The returned environment binds literals of class
     * bool.Variable rather than the special literals used in clausification of
     * class clausal.Literal, so that clients can more readily use it.
     *
     * @return an environment for which the problem evaluates to Bool.TRUE, or
     *         null if no such environment exists.
     */

    static int minPossibleSize = 1;

    public static Environment solve(Formula formula) {
        ImList<Clause> inClauses = formula.getClauses();
        List<Clause> clauses = new ArrayList<Clause>();
        for ( Clause c : inClauses){
            clauses.add(c);
        }
        Environment truth = new Environment();
        return solve(clauses,truth);
    }

    /**
     * Takes a partial assignment of variables to values, and recursively
     * searches for a complete satisfying assignment.
     *
     * @param clauses
     *            formula in conjunctive normal form
     * @param env
     *            assignment of some or all variables in clauses to true or
     *            false values.
     * @return an environment for which all the clauses evaluate to Bool.TRUE,
     *         or null if no such environment exists.
     */
    private static Environment solve(List<Clause> clauses, Environment env) {
        // no clauses
        if (clauses.size() == 0){
            return env;
        }
        // Find shortest clause,
        int minSize = Integer.MAX_VALUE;
        Clause shortest = clauses.get(0); // Arbitrary short
        for (Clause cls : clauses){
            int curSize = cls.size();
            if (curSize < minSize){
                shortest = cls;
                minSize = curSize;
            }
            if (curSize == 0) return null; // empty clause
            if (cls.size() == minPossibleSize) {
                break;
            }
        }
        minPossibleSize = minSize;

        // One-literal clause, substitute it for every clause
        if (shortest.size() == 1){
            Literal l = shortest.chooseLiteral();
            if (l instanceof PosLiteral){
                env = env.put(l.getVariable(), Bool.TRUE);
            }
            else {
                env = env.put(l.getVariable(), Bool.FALSE);
            }
            return solve(substitute(clauses,l),env);
        }

        else {
            Literal l = shortest.chooseLiteral();
            Environment success;
            if (l instanceof PosLiteral) {
                env = env.put(l.getVariable(), Bool.TRUE);
                success = solve(substitute(clauses,l),env);
                if (success == null){
                    env = env.put(l.getVariable(), Bool.FALSE);
                    return solve(substitute(clauses,l.getNegation()),env);
                }
                else{
                    return success;
                }
            }
            else{
                env = env.put(l.getVariable(), Bool.FALSE);
                success = solve(substitute(clauses,l),env);
                if (success == null){
                    env = env.put(l.getVariable(), Bool.TRUE);
                    return solve(substitute(clauses,l.getNegation()),env);
                }
                else{
                    return success;
                }
            }
        }
    }

    /**
     * given a clause list and literal, produce a new list resulting from
     * setting that literal to true
     *
     * @param clauses
     *            , a list of clause
     * @param l
     *            , a literal to set to true
     * @return a new list of clauses resulting from setting l to true
     */
    private static List<Clause> substitute(List<Clause> clauses,
                                           Literal l) {
        List<Clause> outClauses = new ArrayList<>();
        int smallestSize = Integer.MAX_VALUE;
        // iterate through every clause and reduce its 'l'
        for (Clause clause : clauses){
            Clause newClause = clause.reduce(l);
            if (newClause != null){ // if whole clause is true, it is empty
                if (newClause.size()<smallestSize && !newClause.isEmpty()){
                    smallestSize = newClause.size();
                }
                outClauses.add(newClause);
            }
        }
        minPossibleSize = smallestSize;
        return outClauses;
    }
}
