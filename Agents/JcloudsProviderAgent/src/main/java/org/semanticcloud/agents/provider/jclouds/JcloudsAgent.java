package org.semanticcloud.agents.provider.jclouds;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Module;
import org.apache.commons.io.IOUtils;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.ontology.Ontology;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.shared.Lock;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.semanticcloud.agents.provider.ProviderAgent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JcloudsAgent extends ProviderAgent {
    private OntModel offer;
    private final static String NS = "http://semantic-cloud.org/Cloud#";

    private final static String OFFER_CLASS = "http://semantic-cloud.org/Cloud#Condition";

    private ComputeServiceContext context;
    private ComputeService client;
    private String ns2 = "http://semantic-cloud.org/test#";

    private Set<? extends Hardware> hardwares;

    @Override
    protected void init() {
        Object[] args = getArguments();
        providerId = args[0].toString();
        identity = args[1].toString();
        credential = args[2].toString();

        if (providerId.equals("google-compute-engine")) {
            System.out.println("dupa");
            String fileContents = null;
            try {
                fileContents = Files.toString(new File(identity), Charset.defaultCharset());
            } catch (IOException ex) {
                System.out.println("Error Reading the Json key file. Please check the provided path is correct." + identity);
                System.exit(1);
            }
            final JsonObject json = new JsonParser().parse(fileContents).getAsJsonObject();
            identity = json.get("client_email").toString().replace("\"", "");
            // When reading the file it reads in \n in as
            credential = json.get("private_key").toString().replace("\"", "").replace("\\n", "\n");
        }

        ns2 = "http://semantic-cloud.org/" + providerId + "#";
        if (providerId.equals("openstack-nova")){
            context = ContextBuilder.newBuilder(providerId)
                    .endpoint("http://8.43.86.2:5000/v2.0/")
                    .credentials(identity, credential)
                    .modules(ImmutableSet.<Module>of(new Log4JLoggingModule(),
                            new SshjSshClientModule()))
                    .buildView(ComputeServiceContext.class);

        }
        else {


            context = ContextBuilder.newBuilder(providerId)
                    .credentials(identity, credential)
                    .modules(ImmutableSet.<Module>of(new Log4JLoggingModule(),
                            new SshjSshClientModule()))
                    .buildView(ComputeServiceContext.class);
        }
        System.out.println(providerId + " " + identity + " " + credential);

        client = context.getComputeService();
        createOntology();

    }

    private OntModel createModel() {
        OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
        model.setDynamicImports(true);
        OntDocumentManager dm = model.getDocumentManager();
        dm.addAltEntry("http://semantic-cloud.org/Cloud", "file:/opt/SemanticCloud/cloud.owl");

        Ontology ontology = model.createOntology(ns2.replace("#", ""));
        ontology.addImport(model.createResource("http://semantic-cloud.org/Cloud"));
        model.setNsPrefix("cloud", "http://semantic-cloud.org/Cloud#");
        return model;

    }

    private Individual addCPU(Processor cpu) {
        Individual individual = offer.getOntClass(NS + "CPU").createIndividual();

        Property hasClockSpeed = offer.getProperty(NS + "hasClockSpeed");
        Literal speed = offer.createTypedLiteral(new Float(cpu.getSpeed()));
        individual.addProperty(hasClockSpeed, speed);

        Property hasCores = offer.getProperty(NS + "hasCores");
        Literal cores = offer.createTypedLiteral((new Float(cpu.getCores())).intValue());
        individual.addProperty(hasCores, cores);
        //todo hasArchitecture
        return individual;

    }

    private Individual addVirtualMemory(int memory) {
        Individual individual = offer.getOntClass(NS + "VirtualMemory").createIndividual();
        Property hasAvailableSize = offer.getProperty(NS + "hasAvailableSize");
        Literal ram = offer.createTypedLiteral((new Float(memory)).intValue());
        individual.addProperty(hasAvailableSize, ram);
        return individual;

    }

    private Individual addVolume(Volume volume) {
        Individual volumeInterface = offer.getOntClass(NS + "VolumeInterface").createIndividual();
        Property hasDeviceId = offer.getProperty(NS + "hasDeviceId");
        if(volume.getDevice() != null) {
            Literal deviceID = offer.createTypedLiteral(volume.getDevice());
            volumeInterface.addProperty(hasDeviceId, deviceID);
        }

        Property hasVolume = offer.getProperty(NS + "hasVolume");
        Individual individual = offer.getOntClass(NS + "Volume").createIndividual();
        Property hasAvailableSize = offer.getProperty(NS + "hasAvailableSize");
        Literal space = offer.createTypedLiteral((volume.getSize()).intValue() * 1024);
        individual.addProperty(hasAvailableSize, space);
        volumeInterface.addProperty(hasVolume, individual);
        return volumeInterface;

    }

    private void cloneIndividual(OntModel proposal, Resource resource) {
        if (proposal.getResource(resource.getURI()) != null)
            return;

        if (resource == null)
            return;
        traverse(proposal, resource, new ArrayList<>(), 0);


        resource.listProperties().toList().forEach(statement ->

                System.out.println(statement)
        );

    }

    private void createOntology() {
        offer = createModel();
        offer.enterCriticalSection(Lock.WRITE) ;  // or Lock.WRITE
        try {
            OntClass serviceClass = offer.getOntClass(NS + "Service");
            Individual service = serviceClass.createIndividual(ns2 + "serviceID");
            Property hasResource = offer.getProperty(NS + "hasResource");
            OntClass computeClass = offer.getOntClass(NS + "Compute");
            Property hasCPU = offer.getProperty(NS + "hasCPU");
            Property hasMemory = offer.getProperty(NS + "hasMemory");
            Property hasVolumeInterface = offer.getProperty(NS + "hasVolumeInterface");
            hardwares = client.listHardwareProfiles();
            System.out.println(hardwares);
            hardwares.forEach(o -> {
                Individual individual = computeClass.createIndividual(ns2 + o.getId());
                o.getProcessors().forEach(cpu -> {
                    individual.addProperty(hasCPU, addCPU(cpu));
                });
                individual.addProperty(hasMemory, addVirtualMemory(o.getRam()));
                o.getVolumes().forEach(d -> {
                    individual.addProperty(hasVolumeInterface, addVolume(d));
                });
                service.addProperty(hasResource,individual);

            });
        }finally {
            offer.leaveCriticalSection() ;
        }


        System.out.println(offer.validate().isValid());
        ((PelletInfGraph) offer.getGraph()).classify();
        ((PelletInfGraph) offer.getGraph()).realize();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("/tmp/" + providerId + ".owl");
            fileOutputStream.write("<?xml version=\"1.0\"?>\n".getBytes(StandardCharsets.UTF_8));
            offer.write(fileOutputStream, "RDF/XML");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void traverse(OntModel proposal, Resource oc, List<Resource> occurs, int depth) {
        if (oc == null) return;

        // if end reached abort (Thing == root, Nothing == deadlock)
        //if( oc.getLocalName() == null || oc.getLocalName().equals( "Nothing" ) ) return;

        // print depth times "\t" to retrieve a explorer tree like output
//        for (int i = 0; i < depth; i++) {
//            System.out.print("\t");
//        }
//
//        // print out the OntClass
//        System.out.println(oc.toString());

        // check if we already visited this OntClass (avoid loops in graphs)
        if (oc.canAs(Resource.class) && !occurs.contains(oc)) {
            // for every subClass, traverse down

            for (StmtIterator i = oc.listProperties(); i.hasNext(); ) {
                Statement statement = i.next();
//                for (int j = 0; j < depth; j++) {
//                    System.out.print("\t");
//                }
//
//                // print out the OntClass
//                System.out.println(statement.toString());

                // push this expression on the occurs list before we recurse to avoid loops
                occurs.add(oc);
                // traverse down and increase depth (used for logging tabs)
                //System.out.println(statement.getObject().canAs(Resource.class));
                if (statement.getObject().canAs(Resource.class)) {
                    traverse(proposal, statement.getObject().asResource(), occurs, depth + 1);
                }
                proposal.add(statement);
                // after traversing the path, remove from occurs list
                //occurs.remove( oc );
            }
        }

    }

    @Override
    public String prepareProposal(String content) {
        //OntModel model = createModel();
        OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC, offer.getRawModel());
        model.read(IOUtils.toInputStream(content), "RDF/XML");
        model.setDynamicImports(true);
        OntDocumentManager dm = model.getDocumentManager();
        dm.addAltEntry("http://semantic-cloud.org/Cloud", "file:/opt/SemanticCloud/cloud.owl");
        //todo proposal logic


        try {
            FileOutputStream fileOutputStream = new FileOutputStream("/tmp/test.owl");
            fileOutputStream.write("<?xml version=\"1.0\"?>\n".getBytes(StandardCharsets.UTF_8));
            model.write(fileOutputStream, "RDF/XML");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("/tmp/test.jsonld");
            //fileOutputStream.write("<?xml version=\"1.0\"?>\n".getBytes(StandardCharsets.UTF_8));
            model.write(fileOutputStream, "JSON-LD");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        OntModel proposal = createModel();

        OntClass ontClass = model.getOntClass(OFFER_CLASS);

        //List<? extends OntResource> resources = ontClass.listInstances().toList();
        List<? extends OntResource> resources2 = ontClass.listSubClasses(true).toList();
        if(resources2.size() == 0){
            return null;
        }

        resources2.forEach(r ->{
            OntClass ontClass2 = r.asClass();
            List<? extends OntResource> resources = ontClass2.listInstances().toList();
            System.out.println(r.getURI());
            System.out.println(resources.size());


            resources.forEach(o -> {
                o.getLocalName();
                List<RDFNode> node = new ArrayList<RDFNode>();
                System.out.println("Resource:" + o.getLocalName());
                Resource resource = offer.getBaseModel().getResource(o.getURI());
                traverse(proposal, resource, new ArrayList<>(), 0);


            });
            System.out.println("---info eqivalent");
            ontClass2.listEquivalentClasses().toList().forEach(o -> {
                System.out.println(o);
            });
            System.out.println("---info subclasses");
            ontClass2.listSubClasses().toList().forEach(o -> {
                System.out.println(o);
            });
            System.out.println("---info super");
            ontClass2.listSuperClasses().toList().forEach(o -> {
                System.out.println(o);
            });
        });


        StringWriter out = new StringWriter();
        proposal.write(out, "RDF/XML");
        //System.out.println(out);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("/tmp/prop"+providerId+".owl");
            fileOutputStream.write("<?xml version=\"1.0\"?>\n".getBytes(StandardCharsets.UTF_8));
            proposal.write(fileOutputStream, "RDF/XML");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("/tmp/prop"+providerId+".jsonld");
            //fileOutputStream.write("<?xml version=\"1.0\"?>\n".getBytes(StandardCharsets.UTF_8));

            proposal.write(fileOutputStream, "JSON-LD");
            //proposal.write(fileOutputStream, );

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out.toString();
    }

    @Override
    protected void takeDown() {
        super.takeDown();
        context.close();
    }
}
