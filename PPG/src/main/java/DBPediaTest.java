import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.tdb.TDBFactory;


public class DBPediaTest {

	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException 
	{
		DBPedia dbPediaObject=new DBPedia("d:\\predicateList","d:\\predicateScope","D:/TDB_Database");

		long startTime = System.currentTimeMillis();	

		//Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
		// Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();

		//To enale derivation     reasoner.setDerivationLogging(true);
		//InfModel infModel=ModelFactory.createInfModel(reasoner, dbPediaObject.getModel());


		//getting the deduced statements using  getDeductionsModel()
		//Model dedModel=infModel.getDeductionsModel();

		//System.out.println("The model contains "+dbPediaObject.getModel().size()+" statement");   

		//dbPediaObject.writePredicate();

		//System.out.println("Number Of predicates in the model is "+dbPediaObject.readPredicate().size());
		//dbPediaObject.writePredicateScope("predicateScop1");
		//dbPediaObject.getPredicateScope();
		//HashSet<HashMap<String,HashSet<String>>> predicateScope=dbPediaObject.readPredicateScope();
		
		
		System.out.println("Number of predicates with scope is "+dbPediaObject.readPredicateScope().size());

		dbPediaObject.closeDataSet();
		
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("The total time for system to run is "+totalTime/1000+" Second");     
	}
}

