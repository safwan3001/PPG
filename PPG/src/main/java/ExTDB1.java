/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import java.util.Iterator;

import com.hp.hpl.jena.query.Dataset ;
import com.hp.hpl.jena.query.ReadWrite ;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.tdb.TDBFactory;

/** Example of creating a TDB-backed model.
 *  The preferred way is to create a dataset then get the mode required from the dataset.
 *  The dataset can be used for SPARQL query and update
 *  but the Model (or Graph) can also be used.
 *  
 *  All the Jena APIs work on the model.
 *   
 *  Calling TDBFactory is the only place TDB-specific code is needed.
 */

public class ExTDB1
{
    public static void main(String... argv)
    {
        // Direct way: Make a TDB-back Jena model in the named directory.
        String directory = "D:/TDB_Database" ;
        Dataset dataset = TDBFactory.createDataset(directory) ;
        //dataset.begin(ReadWrite.READ) ;
        // Get model inside the transaction
        Model model = dataset.getDefaultModel() ;
    	System.out.println("before list statement...");
/////
        StmtIterator iterator=model.listStatements();
    	System.out.println("after list statement...");

        while (iterator.hasNext())
        {
        	System.out.println(iterator.nextStatement().getPredicate().toString());
           // model.listSubjectsWithProperty(arg0, arg1, arg2);
        }

       /* System.out.println(model.size());
        StmtIterator r=model.listStatements();
        while (r.hasNext()) {
            System.out.println(""+r.nextStatement().getSubject().toString()+":"+
            		r.nextStatement().getPredicate().toString()+":"+
            		r.nextStatement().getObject().toString());
        }*/
        dataset.end() ;

        
        // ... do work ...
        
        // Close the dataset.
        dataset.close();
        
    }
}
