/**
 * Author: dnj, Hank Huang, 6.005 staff
 * Date: March 7, 2009
 * 6.005 Elements of Software Construction
 * (c) 2007-2009, MIT 6.005 Staff
 */
package com.example.sat.formula;

import com.example.immutable.EmptyImList;
import com.example.immutable.ImList;
import com.example.immutable.ImListIterator;
import com.example.immutable.NonEmptyImList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.example.sat.env.Variable;

/**
 * Formula represents an immutable boolean formula in conjunctive normal form,
 * intended to be solved by a SAT solver.
 */
public class Formula {
    private final List<Clause> clauses;

    // Rep invariant:
    // clauses != null
    // clauses contains no null elements (ensured by spec of ImList)
    //
    // Note: although a formula is intended to be a set,
    // the list may include duplicate clauses without any problems.
    // The cost of ensuring that the list has no duplicates is not worth paying.
    //
    //
    // Abstraction function:
    // The list of clauses c1,c2,...,cn represents
    // the boolean formula (c1 and c2 and ... and cn)
    //
    // For example, if the list contains the two clauses (a,b) and (!c,d), then
    // the
    // corresponding formula is (a or b) and (!c or d).

    void checkRep() {
        assert this.clauses != null : "SATProblem, Rep invariant: clauses non-null";
    }

    /**
     * Create a new problem for solving that contains no clauses (that is the
     * vacuously true problem)
     * 
     * @return the true problem
     */
    public Formula() {
        this(new ArrayList<Clause>());
        checkRep();
    }

    /**
     * Create a new problem for solving that contains a single clause with a
     * single literal
     * 
     * @return the problem with a single clause containing the literal l
     */
    public Formula(Variable l) {
        this(new Clause(PosLiteral.make(l.getName())));
    }

    /**
     * Create a new problem for solving that contains a single clause
     * 
     * @return the problem with a single clause c
     */
    public Formula(Clause c) {
        this(new ArrayList<Clause>(Arrays.asList(c)));
    }

    private Formula(List<Clause> clauses) {
        this.clauses = clauses;
    }

    /**
     * Add a clause to this problem
     *
     * @return a new problem with the clauses of this, but c added
     */
    public Formula addClause(Clause c) {
        clauses.add(c);
        return new Formula(clauses);
    }

    /**
     * Get the clauses of the formula.
     * 
     * @return list of clauses
     */
    public List<Clause> getClauses() {
        List<Clause> outClause = new ArrayList<>();
        Iterator<Clause> iterator = clauses.iterator();
        while(iterator.hasNext()) {
            Clause next = iterator.next();
            outClause.add(next);
        }
        return outClause;
    }

//    /**
//     * Iterator over clauses
//     *
//     * @return an iterator that yields each clause of this in some arbitrary
//     *         order
//     */
//    public Iterator<Clause> iterator() {
//        return new ImListIterator<Clause>(clauses);
//    }

    /**
     * @return a new problem corresponding to the conjunction of this and p
     */
    public Formula and(Formula p) {
        List<Clause> pclauses = p.getClauses();
        for (Clause c : clauses) {
            pclauses.add(c);
        }
        return new Formula(pclauses);
    }

    /**
     * @return a new problem corresponding to the disjunction of this and p
     */
    public Formula or(Formula p) {
        List<Clause> result = new ArrayList<Clause>();
        for (Clause c1 : p.clauses) {
            for (Clause c2 : clauses) {
                Clause c = c1.merge(c2);
                if (c != null)
                    result.add(c);
            }
        }
        return new Formula(result);
    }

    /**
     * @return a new problem corresponding to the negation of this
     */
    public Formula not() {
        Formula result = new Formula(new Clause());
        for (Clause c : clauses) {
            result = result.or(negate(c));
        }
        return result;
    }

    /*
     * Make a problem corresponding to the negation of a clause containing one
     * clause with a single negated literal for each literal of the original
     * clause.
     */
    private static Formula negate(Clause c) {
        // explode: make list of unit clauses
        List<Clause> result = new ArrayList<Clause>();
        for (Literal l : c) {
            result.add(new Clause(l.getNegation()));
        }
        return new Formula(result);
    }

    /**
     * 
     * @return number of clauses in this
     */
    public int getSize() {
        return clauses.size();
    }

    public String toString() {
        String result = "Problem[";
        for (Clause c : clauses)
            result += "\n" + c;
        return result + "]";
    }
}
