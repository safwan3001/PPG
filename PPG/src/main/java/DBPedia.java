import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.Timer;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Dataset ;
import com.hp.hpl.jena.query.ReadWrite ;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.tdb.TDBFactory;


public class DBPedia
{
	private int numberOfPredicates=0;
	private HashSet<String> predicateStringHashSet;
	private HashSet<Property> predicatePropertyHashSet;
	private String predicateHashSetFileDirectory;
	private String DBDirectory;
	private Model model;
	private Dataset dataset;
	private File file;

	public DBPedia(String predicateListFilePath ,String DBDirectory){
		this.predicateHashSetFileDirectory=predicateListFilePath;
		this.DBDirectory=DBDirectory;
		dataset = TDBFactory.createDataset(DBDirectory);
		this.model = dataset.getDefaultModel() ;
		this.predicateStringHashSet=new HashSet<String>();
		this.predicatePropertyHashSet=new HashSet<Property>();
		this.setFile();
	}

	
	public void closeDataSet(){
		//Finish the transaction - if a write transaction and commit() has not been called, then abort
		dataset.end() ;
		// Close the dataset, potentially releasing any associated resources.
		dataset.close();
	}
	public HashSet<String> readPredicateHashSet() throws ClassNotFoundException, IOException{

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.predicateHashSetFileDirectory));
		this.predicateStringHashSet=(HashSet<String>) ois.readObject();
		return this.predicateStringHashSet;

	}
	public void writePredicateHashSet() throws FileNotFoundException, IOException{

		predicatePropertyHashSet=this.getModelPredicates(this.getModel());

		if (file.exists()){
			System.out.println("File already exists");
		}
		else{
			System.out.println("Creating the file and writing the hashSet on it");
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.predicateHashSetFileDirectory));

			for(Property predicate: predicatePropertyHashSet )
			{
				this.predicateStringHashSet.add(predicate.toString());
			}
			oos.writeObject(this.predicateStringHashSet);
		}

	}
	
	public HashSet<Property> getModelPredicates(Model model){

		if(this.getFile().exists()){
			System.out.println("File already exists no need to check all predicates");
			return predicatePropertyHashSet;
		}
		else{
			StmtIterator r=model.listStatements();
			while (r.hasNext()) {
				predicatePropertyHashSet.add(r.nextStatement().getPredicate());
			}

			System.out.println("Number of predicates in the model is "+this.getNumberOfPredicates());
			return predicatePropertyHashSet;
		}
	}
	
	public void getPredicateScope(){
		
	}

	public String getPredicateListFilePath() {
		return predicateHashSetFileDirectory;
	}

	public void setPredicateListFilePath(String predicateListFilePath) {
		this.predicateHashSetFileDirectory = predicateListFilePath;
	}

	public int getNumberOfPredicates() {
		return numberOfPredicates;
	}

	public void setNumberOfPredicates() {
		this.numberOfPredicates = predicateStringHashSet.size();
	}

	public Model getModel() {
		return model;
	}

	public void setModel() {
		this.model = dataset.getDefaultModel() ;
	}

	public File getFile() {
		return file;
	}

	public void setFile() {
		this.file = new File(predicateHashSetFileDirectory);
	}

}