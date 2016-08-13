package org.semanticcloud.agents.provider.jclouds;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.apache.commons.io.IOUtils;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.Ontology;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.semanticcloud.agents.provider.ProviderAgent;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class JcloudsAgent extends ProviderAgent {
    private OntModel offer;
    private final static String NS = "http://semantic-cloud.org/Cloud#";
    private final static String NS2 = "http://semantic-cloud.org/aws#";

    private ComputeServiceContext context;
    private ComputeService client;

    @Override
    protected void init() {
        Object[] args = getArguments();
        providerId = args[0].toString();
        identity = args[1].toString();
        credential = args[2].toString();
        context = ContextBuilder.newBuilder(providerId)
                .credentials(identity, credential)
                .modules(ImmutableSet.<Module> of(new Log4JLoggingModule(),
                        new SshjSshClientModule()))
                .buildView(ComputeServiceContext.class);
        System.out.println(providerId+identity+credential);

        client = context.getComputeService();
        createOntology();

    }

    private OntModel createModel(){
        OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
        model.setDynamicImports(true);
        OntDocumentManager dm = model.getDocumentManager();
        dm.addAltEntry("http://semantic-cloud.org/Cloud", "file:/opt/SemanticCloud/cloud.owl");

        Ontology ontology = model.createOntology("http://semantic-cloud.org/aws");
        ontology.addImport(model.createResource( "http://semantic-cloud.org/Cloud" ));
        return model;

    }

    private Individual addCPU(Processor cpu){
        Individual individual = offer.getOntClass(NS + "CPU").createIndividual();

        Property hasClockSpeed = offer.getProperty(NS + "hasClockSpeed");
        Literal speed = offer.createTypedLiteral(new Float(cpu.getSpeed()));
        individual.addProperty(hasClockSpeed,speed);

        Property hasCores = offer.getProperty(NS + "hasCores");
        Literal cores = offer.createTypedLiteral((new Float(cpu.getCores())).intValue());
        individual.addProperty(hasCores,cores);
        //todo hasArchitecture
        return individual;

    }

    private Individual addVirtualMemory(int memory){
        Individual individual = offer.getOntClass(NS + "VirtualMemory").createIndividual();
        Property hasAvailableSize = offer.getProperty(NS + "hasAvailableSize");
        Literal ram = offer.createTypedLiteral((new Float(memory)).intValue());
        individual.addProperty(hasAvailableSize,ram);
        return individual;

    }

    private Individual addVolume(Volume volume){
        Individual volumeInterface = offer.getOntClass(NS + "VolumeInterface").createIndividual();
        Property hasDeviceId = offer.getProperty(NS + "hasDeviceId");
        Literal deviceID = offer.createTypedLiteral(volume.getDevice());
        volumeInterface.addProperty(hasDeviceId,deviceID);

        Property hasVolume = offer.getProperty(NS + "hasVolume");
        Individual individual = offer.getOntClass(NS + "Volume").createIndividual();
        Property hasAvailableSize = offer.getProperty(NS + "hasAvailableSize");
        Literal space = offer.createTypedLiteral((volume.getSize()).intValue()*1024);
        individual.addProperty(hasAvailableSize,space);
        volumeInterface.addProperty(hasVolume, individual);
        return volumeInterface;

    }

    private void createOntology(){
        offer = createModel();

        //Add Component/Resources
        //Classes
        OntClass computeClass = offer.getOntClass(NS + "Compute");
//        OntClass cpuClass = offer.getOntClass(NS + "CPU");
//        OntClass virtualMemory = offer.getOntClass(NS + "VirtualMemory");
        //Properties
        Property hasCPU = offer.getProperty(NS + "hasCPU");
//        Property hasClockSpeed = offer.getProperty(NS + "hasClockSpeed");
//        Property hasCores = offer.getProperty(NS + "hasCores");
        //Property hasAvailableSize = offer.getProperty(NS + "hasAvailableSize");
        Property hasMemory = offer.getProperty(NS + "hasMemory");
        offer.setNsPrefix( "cloud", "http://semantic-cloud.org/Cloud#" );
        Property hasVolumeInterface = offer.getProperty(NS + "hasVolumeInterface");
        Set<? extends Hardware> hardwares = client.listHardwareProfiles();
        hardwares.forEach(o -> {
            Individual individual = computeClass.createIndividual(NS2 + o.getId());
            o.getProcessors().forEach(cpu ->{
                individual.addProperty(hasCPU,addCPU(cpu));
            });
            individual.addProperty(hasMemory,addVirtualMemory(o.getRam()));
            o.getVolumes().forEach(d->{
                individual.addProperty(hasVolumeInterface, addVolume(d));
            });

        });



//        Set<? extends Location> locations = client.listAssignableLocations();
//        System.out.println(locations);
//        RDFList rdfList2 =  offer.createList(new RDFNode[] {
//                offer.createHasValueRestriction(null,hasCores,offer.createTypedLiteral(16))
//        });
//        //rdfList.add(computeClass);
//        OntClass a = offer.createIntersectionClass(null,rdfList2);
//
//        SomeValuesFromRestriction someValuesFromRestriction = offer.createSomeValuesFromRestriction(null, hasCPU, a);
//        RDFList rdfList =  offer.createList(new RDFNode[] {
//                computeClass,someValuesFromRestriction
//        });
//        //rdfList.add(computeClass);
//        OntClass myCompute = offer.createIntersectionClass(NS2+"myCompute",rdfList);


        try {
            FileOutputStream fileOutputStream = new FileOutputStream("/tmp/test.owl");
            fileOutputStream.write("<?xml version=\"1.0\"?>\n".getBytes(StandardCharsets.UTF_8));
            offer.write(fileOutputStream, "RDF/XML");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        offer.validate();

//        System.out.println(myCompute.getEquivalentClass());
//        System.out.println(computeClass.listInstances().toList().size());
//        System.out.println(myCompute.listInstances().toList().size());
//        myCompute.listInstances().toList().forEach(o -> {
//            System.out.println(o);
//        });
//        System.out.println("---");
//        myCompute.listEquivalentClasses().toList().forEach(o -> {
//            System.out.println(o);
//        });
//        myCompute.listSubClasses().toList().forEach(o -> {
//            System.out.println(o);
//        });

    }

    @Override
    public String prepareProposal(String content) {
        OntModel model = createModel();
        model.read(IOUtils.toInputStream(content),"RDF/XML");

        //model =(OntModel) createModel().add(model).add(offer);
        //todo proposal logic
        StringWriter out = new StringWriter();
        model.write(out,"RDF/XML");
        System.out.println(out);
        return out.toString();
    }

    @Override
    protected void takeDown() {
        super.takeDown();
        context.close();
    }
}
