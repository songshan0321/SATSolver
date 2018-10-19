package com.example.sat;

/*
import static org.junit.Assert.*;

import org.junit.Test;
*/

import com.example.sat.env.Bool;
import com.example.sat.env.Variable;
import com.example.sat.formula.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class SATSolverTest {
    Literal a = PosLiteral.make("a");
    Literal b = PosLiteral.make("b");
    Literal c = PosLiteral.make("c");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();

    public static final String outpDir = "C:/Users/Windows 10/Desktop/output/BoolAssignment.txt";
    public static final String inpFile = "C:/Users/Windows 10/Desktop/sampleCNF/largeSat.cnf";

    public static void main(String[] args) {
        /*********** Step 1: construct instances af Formula ***********
         */
        List<Clause> clauseList = new LinkedList<>();       //Store all the clauses
        List<Literal> clause;          //Store the literal parsed from the file temporarily
        Boolean startRead = false;

        /*********** Step 2: read XXX.cnf file *********************
        */
        System.out.println("Start Parsing");
        File file = new File(inpFile);
        BufferedReader reader;
        try {
            // open file for reading
            reader = new BufferedReader(new FileReader(file));
            // Read all contents of the file.
            String inputLine = null;
            // read first line
            inputLine = reader.readLine();
            while((inputLine = reader.readLine()) != null){
                if(inputLine.length() > 0){
                    // get header
                    if(inputLine.charAt(0) == 'p'){
                        startRead = true;
                        String[] header = inputLine.split(" ");
                        int numOfVar = Integer.parseInt(header[2]);
                    }
                    // start parsing
                    else if (startRead){
                        String[] inputList = inputLine.split("\\s+");
                        int currentVar;
                        clause = new ArrayList<Literal>();
                        for (int i=0; i<inputList.length; i++){ // include "0" at the end
                            currentVar = Integer.parseInt(inputList[i]);
                            if (currentVar > 0){
                                clause.add(PosLiteral.make(inputList[i]));
                            }
                            else if (currentVar < 0){
                                clause.add(NegLiteral.make(inputList[i].substring(1)));
                            }
                            else {  // 0
                                clauseList.add(makeCl(clause));
                                clause = null;
                            }
                        }
                    }
                }
            }
            reader.close();
        }
        catch (IOException ex){
            System.err.println("An IOException was caught!");
            ex.printStackTrace();
        }

        /*********** Step 3: call SATSolver.solve *******************
         */
        Clause[] newClauses = new Clause[clauseList.size()];
        System.out.println("SAT solver starts!!!");
        long started = System.nanoTime();
        Map env = SATSolver.solve(clauseList);
        long time = System.nanoTime();
        long timeTaken= time - started;
        System.out.println("Time:" + timeTaken/1000000.0 + "ms");

        /*********** Step 4: write BoolAssignment.txt file **********
         */
        File output = new File(outpDir);
        try {
            FileWriter fstream = new FileWriter(output);
            BufferedWriter writer = new BufferedWriter(fstream);
            if (env != null) {
                // Sort env
                ArrayList<Map.Entry<Variable, Bool>> listOfEntry = new ArrayList<Map.Entry<Variable,Bool>>(env.entrySet());
                Map<Integer,Bool> sortedEnv = new TreeMap<Integer,Bool>();
                for (Map.Entry<Variable, Bool> entry : listOfEntry)
                {
                    sortedEnv.put(Integer.parseInt(entry.getKey().getName()),entry.getValue());
                }

                int l = sortedEnv.toString().length();
                String[] strList = sortedEnv.toString().substring(1,l-1).split(", ");
                for (String str : strList) {
                    String[] oneTuple = str.split("=");
                    writer.write(oneTuple[0] + ":" + oneTuple[1]);
                    writer.newLine();
                }
                writer.close();

                System.out.println("Satisfiable");
                System.out.println("Environment: " + sortedEnv );
            }
            else {
                System.out.println("Not Satisfiable");
            }
            writer.close();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }

//    public void testSATSolver1(){
//    	// (a v b)
//    	Environment e = SATSolver.solve(makeFm(makeCl(a,b))	);
///*
//    	assertTrue( "one of the literals should be set to true",
//    			Bool.TRUE == e.get(a.getVariable())
//    			|| Bool.TRUE == e.get(b.getVariable())	);
//
//*/
//    }


//    public void testSATSolver2(){
//    	// (~a)
//    	Environment e = SATSolver.solve(makeFm(makeCl(na)));
///*
//    	assertEquals( Bool.FALSE, e.get(na.getVariable()));
//*/
//    }

//    private static Formula makeFm(Clause... e) {
//        Formula f = new Formula();
//        for (Clause c : e) {
//            f = f.addClause(c);
//        }
//        return f;
//    }

    private static Clause makeCl(List<Literal> e) {
        Clause c = new Clause();
        for (Literal l : e) {
            c = c.add(l);
        }
        return c;
    }
}