package sat;

/*
import static org.junit.Assert.*;

import org.junit.Test;
*/

import sat.env.*;
import sat.formula.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class SATSolverTest {
    Literal a = PosLiteral.make("a");
    Literal b = PosLiteral.make("b");
    Literal c = PosLiteral.make("c");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();

    public static final String outpDir = "C:/Users/Windows 10/Desktop/output/BoolAssignment.txt";
    public static final String inpFile = "C:/Users/Windows 10/Desktop/sampleCNF/sat1.cnf";

    public static void main(String[] args) {
        /*********** Step 1: construct instances af Formula ***********
         */
        List<Clause> clauseList = new LinkedList<>();       //Store all the clauses
        ArrayList<Literal> clause;          //Store the literal parsed from the file temporarily
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
            while((inputLine = reader.readLine()) != null){
                if(inputLine.length() > 0){
                    // get header
                    if(inputLine.charAt(0) == 'p'){
                        startRead = true;
                    }
                    // start parsing
                    else if (startRead){
                        if (inputLine.startsWith(" ")){
                            inputLine = inputLine.substring(1);
                        }
                        String[] inputList = inputLine.split("\\s+");
                        int currentVar;
                        clause = new ArrayList<Literal>();

                        for (int i=0; i<inputList.length; i++){
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
        clauseList.toArray(newClauses);
        Formula f = makeFm(newClauses);
        System.out.println("SAT solver starts!!!");
        long started = System.nanoTime();
        Environment env = SATSolver.solve(f);
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
                String strSet = env.toString().substring(13, env.toString().length() - 1);
                String strList[] = strSet.split(", ");
                for (String str : strList) {
                    String[] oneTuple = str.split("->");
                    writer.write(oneTuple[0] + ":" + oneTuple[1]);
                    writer.newLine();
                }
                writer.close();

                System.out.println("Satisfiable");
                System.out.println(env);
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

    private static Formula makeFm(Clause... e) {
        Formula f = new Formula();
        for (Clause c : e) {
            f = f.addClause(c);
        }
        return f;
    }

    private static Clause makeCl(ArrayList<Literal> e) {
        Clause c = new Clause();
        for (Literal l : e) {
            c = c.add(l);
        }
        return c;
    }
}