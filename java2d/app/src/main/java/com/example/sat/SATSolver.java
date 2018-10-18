package com.example.sat;

import com.example.sat.env.Bool;
import com.example.sat.env.Environment;
import com.example.sat.formula.Clause;
import com.example.sat.formula.Formula;
import com.example.sat.formula.Literal;
import com.example.sat.formula.PosLiteral;

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
    public static Environment solve(Formula formula) {
        List<Clause> clauses = formula.getClauses();
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
        Clause shortest = clauses.get(0); // Arbitrary short
//        outerloop:
        for (Clause cls : clauses){
            int curSize = cls.size();
//            if (curSize == 1){
//                break outerloop;
//            }
            if (curSize < shortest.size()){
                shortest = cls;
            }
            if (curSize == 0) return null; // empty clause
        }

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
            env = env.put(l.getVariable(), Bool.TRUE);
            Environment success = solve(substitute(clauses,l),env);
            if (success.get(l.getVariable()) == null){
                env = env.put(l.getVariable(), Bool.FALSE);
                return solve(substitute(clauses,l.getNegation()),env);
            }
            else{
                return success;
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
        // iterate through every clause and reduce its 'l'
        for (Clause clause : clauses){
            Clause newClause = clause.reduce(l);
            if (newClause != null){ // if whole clause is true, it is empty
                outClauses.add(newClause);
            }
        }
        return outClauses;
    }
}
