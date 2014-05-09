import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.tdb.TDBFactory;


public class DbpediaTest {

	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException 
	{
		DBPedia dbPediaObject=new DBPedia("d:\\test.txt","D:/TDB_Database");

		long startTime = System.currentTimeMillis();	

		Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
		// Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();

		//To enale derivation     reasoner.setDerivationLogging(true);
		//InfModel infModel=ModelFactory.createInfModel(reasoner, dbPediaObject.getModel());


		//getting the deduced statements using  getDeductionsModel()
		//Model dedModel=infModel.getDeductionsModel();

		System.out.println("The model contains "+dbPediaObject.getModel().size()+" statement");   

		dbPediaObject.writePredicateHashSet();
		/// to find the intersection between two sets we sue retainAll(collection) and we pass a collection object so we will get back 
		//Retains only the elements in this set that are contained in the specified collection (optional operation). In other words, removes from this
		//set all of its elements that are not contained in the specified collection.If the specified collection is also a set, this operation effectively
		//modifies this set so that its value is the intersection of the two sets.


		
		int counter=0;
		for (String predicate : dbPediaObject.readPredicateHashSet()){
			System.out.println("inside for statement");
			HashMap attHashMap=new HashMap();

			if (counter<1){
				System.out.println("inside if statement");
				ResIterator subjectsList=dbPediaObject.getModel().listSubjectsWithProperty(dbPediaObject.getModel().getProperty(predicate));
				//System.out.println("The scop of the predicate "+predicate+" contains "+subjectsList.toSet().size()+" element");
				attHashMap.put(predicate,subjectsList.toSet());
				System.out.println(attHashMap.toString());
				counter++;
			}
			else{
				System.out.println("inside else statement");

				break;
			}
		}

		dbPediaObject.closeDataSet();
		
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("The total time for system to run is "+totalTime/1000);     
	}
}

