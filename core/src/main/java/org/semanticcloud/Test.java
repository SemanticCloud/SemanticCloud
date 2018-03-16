package org.semanticcloud;

import openllet.owlapi.OWL;
import openllet.owlapi.OWLGenericTools;
import openllet.owlapi.OWLHelper;
import openllet.owlapi.OpenlletReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import owl.owlapi.MicroReasoner;
import owl.owlapi.MicroReasonerFactory;
import owl.sod.hlds.Composition;
import owl.sod.hlds.Item;
import owl.sod.hlds.Match;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;


public class Test {
    private static final String ONTO_DIR = "/tmp/propjoyent.owl";
    private static final String REQUEST_DIR = "/home/l.smolaga/workspace/SemanticCloud/samples/request.owl";

    public static void main(String[] args) throws IOException {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        File file = new File(ONTO_DIR);
        File request = new File(REQUEST_DIR);
        //OWL.
        try{
            OWLOntology onto = manager.loadOntologyFromOntologyDocument(file);
            final OWLHelper h = OWLHelper.createLightHelper(OpenlletReasonerFactory.getInstance().createReasoner(onto));

            //Create the factory
            MicroReasonerFactory reasonerFactory = new MicroReasonerFactory();
            //Return an instance of OWLReasoner class that represents our Mini-ME reasoner
            MicroReasoner reasoner = reasonerFactory.createMicroReasoner(onto);
            OWLOntology onto_request = manager.loadOntologyFromOntologyDocument(request);
            Set<String> requests_name = reasoner.loadDemand(onto_request);
            String requestInd = null;
            Iterator<String> iter_requests = requests_name.iterator();
            while(iter_requests.hasNext()){
                requestInd = (String) iter_requests.next();
            }


            System.out.println("Compute covering...");
            System.out.println(requestInd);
            //Match match = reasoner.evaluateMatch("https://www.joyent.com/#g4-griffin-master-8G", requestInd);
            Composition result = reasoner.composition(requestInd);

            System.out.print("Set: ");
            Vector<Item> set = (Vector<Item>)result.Rc;
            for(int i=0; i<set.size(); i++){
                System.out.print(((Item)set.get(i)).name+", ");
            }
            System.out.println("");
            System.out.println("Uncovered-part: "+((Item)result.Du).description.toString());

        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
