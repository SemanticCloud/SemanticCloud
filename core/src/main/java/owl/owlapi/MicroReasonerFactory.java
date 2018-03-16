 package owl.owlapi;

 import org.semanticweb.owlapi.model.OWLOntology;
 import org.semanticweb.owlapi.reasoner.IllegalConfigurationException;
 import org.semanticweb.owlapi.reasoner.OWLReasoner;
 import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;

 public class MicroReasonerFactory implements org.semanticweb.owlapi.reasoner.OWLReasonerFactory
 {
   public MicroReasoner createMicroReasoner(OWLOntology ontology)
   {
     if (ontology == null) {
       throw new NullPointerException();
     }
     return new MicroReasoner(ontology);
   }
   
   public OWLReasoner createNonBufferingReasoner(OWLOntology ontology)
   {
     return createMicroReasoner(ontology);
   }
   
 
 
 
 
   public OWLReasoner createNonBufferingReasoner(OWLOntology ontology, OWLReasonerConfiguration config)
     throws IllegalConfigurationException
   {
     return createNonBufferingReasoner(ontology);
   }
   
 
   public OWLReasoner createReasoner(OWLOntology ontology)
   {
     throw new UnsupportedOperationException("Buffering mode is not yet implemented.");
   }
   
 
 
   public OWLReasoner createReasoner(OWLOntology ontology, OWLReasonerConfiguration config)
     throws IllegalConfigurationException
   {
     throw new UnsupportedOperationException("Buffering mode is not yet implemented.");
   }
   
   public String getReasonerName()
   {
     return "Mini-ME OWLReasoner - Developed by SisInfLab (http://sisinflab.poliba.it)";
   }
 }


