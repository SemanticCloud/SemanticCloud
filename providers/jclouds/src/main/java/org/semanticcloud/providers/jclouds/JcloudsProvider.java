package org.semanticcloud.providers.jclouds;

import openllet.jena.PelletReasonerFactory;
import org.apache.jena.ontology.AllValuesFromRestriction;
import org.apache.jena.ontology.HasValueRestriction;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.*;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.semanticcloud.AbstractProvider;
import org.semanticcloud.Cloud;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Generic provider based on Apache jclouds
 */
public class JcloudsProvider extends AbstractProvider {
    protected String providerId;
    protected String identity;
    protected String credential;
    private ComputeServiceContext context;
    private ComputeService client;

    public static void main(String[] args) {
//        JcloudsProvider provider = new JcloudsProvider(
//                "http://trystack.com/#",
//                "openstack-nova",
//                "facebook1832369920122187:facebook1832369920122187",
//                "gueZneMQzJ8Yr2tg",
//                "http://8.43.86.2:5000/v2.0/"
//         );
        JcloudsProvider provider = new JcloudsProvider(
                "http://aws.com/#",
                "aws-ec2",
                "AKIAIEPE7XR7IXKUVK5Q",
                "672hi93g3z5+wjoFdYcKwVLvo8GhbJ29Vg3Ze1aO"
                //"http://8.43.86.2:5000/v2.0/"
        );
        provider.listResources();
        OntModel baseModel = provider.createBaseModel();
        baseModel.createClass(OFFER_CLASS);

        OntModel offer = provider.prepareProposal(baseModel);
        Individual individual = offer.createIndividual("http://aws.com/#test",offer.getOntClass("http://aws.com/#class/c4.8xlarge"));
        individual.listProperties().forEachRemaining( s->{
            System.out.println(s);

        });
        individual = offer.createIndividual("http://aws.com/#test2",offer.getOntClass("http://aws.com/#class/c4.8xlarge"));
        individual.listProperties().forEachRemaining( s->{
            System.out.println(s);

        });

        try {
            FileOutputStream fileOutputStream = new FileOutputStream("/tmp/prop"+provider.providerId+".owl");
            fileOutputStream.write("<?xml version=\"1.0\"?>\n".getBytes(StandardCharsets.UTF_8));
            offer.write(fileOutputStream, "RDF/XML");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        provider.context.close();
    }

    private Individual createCPU(OntModel offer , Processor cpu) {
        Individual individual = offer.createIndividual(Cloud.CPU);

        Literal speed = offer.createTypedLiteral(new Float(cpu.getSpeed()));
        individual.addProperty(Cloud.hasClockSpeed, speed);

        Literal cores = offer.createTypedLiteral((new Float(cpu.getCores())).intValue());
        individual.addProperty(Cloud.hasCores, cores);
        return individual;

    }
    private Individual createVirtualMemory(OntModel offer ,int memory) {
        Individual individual = offer.createIndividual(Cloud.Memory);
        Literal ram = offer.createTypedLiteral((new Float(memory)).intValue());
        individual.addProperty(Cloud.hasMemorySize, ram);
        return individual;

    }

    private Individual createVolume(OntModel offer ,Volume volume) {
        Individual volumeInterface = offer.createIndividual(Cloud.VolumeInterface);
        if(volume.getDevice() != null) {
            Literal deviceID = offer.createTypedLiteral(volume.getDevice());
            volumeInterface.addProperty(Cloud.hasDeviceId, deviceID);
        }

        Individual individual = offer.createIndividual(Cloud.Volume);
        Literal space = offer.createTypedLiteral((volume.getSize()).intValue() * 1024);
        individual.addProperty(Cloud.hasStorageSize, space);
        volumeInterface.addProperty(Cloud.hasVolume, individual);
        return volumeInterface;

    }

    private Individual createCompute(OntModel offer ,Hardware o) {
        Individual individual = offer.createIndividual(namespace + o.getId(),Cloud.Compute);
        o.getProcessors().forEach(cpu -> {
            individual.addProperty(Cloud.hasCPU, createCPU(offer,cpu));
        });
        individual.addProperty(Cloud.hasMemory, createVirtualMemory(offer,o.getRam()));
        o.getVolumes().forEach(d -> {
            individual.addProperty(Cloud.hasVolumeInterface, createVolume(offer,d));
        });
        return individual;

    }

    public JcloudsProvider(String namespace, String providerId, String identity, String credential) {
        super(namespace);
        this.providerId = providerId;
        this.identity = identity;
        this.credential = credential;
        context = ContextBuilder.newBuilder(providerId)
                .credentials(identity, credential)
                .buildView(ComputeServiceContext.class);



        client = context.getComputeService();
    }

    public JcloudsProvider(String namespace, String providerId, String identity, String credential, String endpoint) {
        super(namespace);
        this.providerId = providerId;
        this.identity = identity;
        this.credential = credential;
        context = ContextBuilder.newBuilder(providerId)
                .endpoint(endpoint)
                .credentials(identity, credential)
                .buildView(ComputeServiceContext.class);

        client = context.getComputeService();
    }

    @Override
    public OntModel prepareProposal(OntModel cfp) {
        OntModel offer = createBaseModel();
        OntModel proposal = createBaseModel();
        OntClass service = offer.createClass(namespace + providerId + "ComputeService");
        service.addSuperClass(Cloud.Service);
        List<OntClass> resourcesList = new ArrayList<>();
        Set<? extends Hardware> hardwares = client.listHardwareProfiles();
        hardwares.forEach(o -> {
            if(o.isDeprecated()) return;
            createCompute(offer,o);
            resourcesList.add(createComputeClass(offer,o));
        });
        offer.createAllValuesFromRestriction(null, Cloud.hasResource, offer.createUnionClass(namespace + providerId + "Compute", offer.createList(resourcesList.iterator())))
        .addSubClass(service);


        OntModel model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC, ModelFactory.createUnion(cfp, offer));

        System.out.println(OFFER_CLASS);


        OntClass ontClass = model.getOntClass(OFFER_CLASS);

        List<? extends OntResource> resources4 = ontClass.listInstances().toList();
        System.out.println("Instances:" + resources4.size());
        List<? extends OntResource> resources2 = ontClass.listSubClasses(true).toList();
        if(resources2.size() == 0){
            System.out.println("Subclasses:" + resources2.size());
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




        return offer;
    }

    private OntClass createComputeClass(OntModel offer, Hardware o) {
        OntClass aClass = offer.createClass(namespace + "class/" + o.getId());
        aClass.addSuperClass(Cloud.Compute);

        Individual individual = offer.createIndividual(namespace + o.getId(),Cloud.Compute);
        o.getProcessors().forEach(cpu -> {
            AllValuesFromRestriction hasValueRestriction = offer.createAllValuesFromRestriction(null, Cloud.hasCPU, createCPUClass(offer, cpu));
           hasValueRestriction.addSubClass(aClass);
            //individual.addProperty(Cloud.hasCPU, createCPU(offer,cpu));
        });
//        individual.addProperty(Cloud.hasMemory, createVirtualMemory(offer,o.getRam()));
//        o.getVolumes().forEach(d -> {
//            individual.addProperty(Cloud.hasVolumeInterface, createVolume(offer,d));
//        });
        //return individual;
        return aClass;
    }
    private OntClass createCPUClass(OntModel offer , Processor cpu) {



        Literal speed = offer.createTypedLiteral(new Float(cpu.getSpeed()));

        Literal cores = offer.createTypedLiteral((new Float(cpu.getCores())).intValue());

        OntClass aClass = offer.createClass(namespace + UUID.randomUUID());
        aClass.addSuperClass(Cloud.CPU);
        HasValueRestriction hasValueRestriction = offer.createHasValueRestriction(null, Cloud.hasClockSpeed, speed);
        hasValueRestriction.addSubClass(aClass);
        hasValueRestriction = offer.createHasValueRestriction(null, Cloud.hasCores, cores);
        hasValueRestriction.addSubClass(aClass);
        return aClass;

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
    public void acceptProposal() {

    }

    @Override
    public void refuseProposal() {

    }

    @Override
    public void performAction() {

    }

    public void listResources(){
        OntModel offer = createBaseModel();
        Set<? extends ComputeMetadata> computeMetadata = client.listNodes();
//        computeMetadata.forEach(b ->{
//            client.getNodeMetadata()
//        });
        for (ComputeMetadata nodeData : computeMetadata) {
            System.out.println(">>>>  " + nodeData);
        }
        Set<? extends Hardware> hardwares = client.listHardwareProfiles();
        hardwares.forEach(o -> {
            createCompute(offer,o);
        });

    }
}
