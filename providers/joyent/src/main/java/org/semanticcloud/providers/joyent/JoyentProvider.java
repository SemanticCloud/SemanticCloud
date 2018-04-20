package org.semanticcloud.providers.joyent;

import openllet.jena.PelletReasonerFactory;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

import org.semanticcloud.AbstractProvider;
import org.semanticcloud.Cloud;
import org.semanticcloud.jena.ConditionParser;
import org.semanticcloud.providers.joyent.triton.TritonService;
import org.semanticcloud.providers.joyent.triton.domain.Package;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

/**
 * Created by malagus on 6/1/17.
 */
public class JoyentProvider extends AbstractProvider {
    private TritonService tritonService;

    public JoyentProvider() {
        super("https://www.joyent.com/#");
        try {
            tritonService = new TritonService("https://eu-central-1a.api.samsungcloud.io","griffin_srpol","/home/l.smolaga/.ssh/joyent");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
//        InstanceAPI target = Feign.builder()
//                .contract(new JAXRSContract())
//                .decoder(new JacksonDecoder())
//                .requestInterceptor(new MyRequestInterceptor("malagus","/home/l.smolaga/.ssh/test2_id_rsa"))
//                .target(InstanceAPI.class, "https://eu-ams-1.api.joyent.com");
//        List<org.semanticcloud.providers.joyent.triton.domain.Instance> list = target.list("malagus");
//        System.out.println(list);
        //System.out.println(target.get("malagus","60a3b1fa-0674-11e2-abf5-cb82934a8e24"));


        //System.out.println(out);
      //  try {
            FileManager fileManager = FileManager.get();
            fileManager.addLocatorFile("/opt/SemanticCloud/");
            FileInputStream fileInputStream = new FileInputStream("/home/l.smolaga/workspace/SemanticCloud/samples/request.owl");
        OntModelSpec ontModelSpec = new OntModelSpec(OntModelSpec.OWL2_MEM);
        ontModelSpec.setReasonerFactory(PelletReasonerFactory.theInstance());
            OntModel cfp = ModelFactory.createOntologyModel(ontModelSpec);
            cfp.setDynamicImports(true);
            OntDocumentManager dm = cfp.getDocumentManager();
            //dm.addAltEntry("http://semantic-cloud.org/Cloud", "file:/opt/SemanticCloud/cloud.owl");
            cfp.read(fileInputStream, "RDF/XML");




            JoyentProvider provider = new JoyentProvider();
            //joyentProvider.prepareProposal(cfp);
            OntModel baseModel = provider.createBaseModel();
            baseModel.createClass(OFFER_CLASS);

            OntModel offer = provider.prepareProposal(cfp);
//            Individual individual = offer.createIndividual("http://aws.com/#test",offer.getOntClass("http://aws.com/#class/c4.8xlarge"));
//            individual.listProperties().forEachRemaining( s->{
//                System.out.println(s);
//
//            });
//            individual = offer.createIndividual("http://aws.com/#test2",offer.getOntClass("http://aws.com/#class/c4.8xlarge"));
//            individual.listProperties().forEachRemaining( s->{
//                System.out.println(s);
//
//            });

//        List<Package> packages = provider.tritonService.listPakages();
//        System.out.println(packages);
        //provider.tritonService.listImages().forEach( i -> System.out.println(i));


        //System.out.println(provider.tritonService.createInstance().body());
        //provider.listResources();

            try {
                FileOutputStream fileOutputStream = new FileOutputStream("/tmp/prop"+"joyent"+".owl");
                fileOutputStream.write("<?xml version=\"1.0\"?>\n".getBytes(StandardCharsets.UTF_8));
                offer.write(fileOutputStream, "RDF/XML");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }



    }

    private void listResources() {
        OntModel baseModel = createBaseModel();
        Individual service = baseModel.createIndividual(namespace + "TritonCompute",Cloud.Service);

//        try {
//
//            Iterator<Instance> list = cloudApi.instances().list();
//            list.forEachRemaining(i ->{
//                System.out.println(i);
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    private Individual addCPU(OntModel baseModel, Package p) {
        Individual individual = baseModel.createIndividual(namespace+ UUID.randomUUID(),Cloud.CPU);

//        Property hasClockSpeed = baseModel.getProperty(NS + "hasClockSpeed");
//        Literal speed = baseModel.createTypedLiteral(new Float(cpu.getSpeed()));
//        individual.addProperty(hasClockSpeed, speed);

        Literal cores = baseModel.createTypedLiteral(p.getVcpus());
        baseModel.createDatatypeProperty(Cloud.hasCores.getURI());
        individual.addProperty(Cloud.hasCores, cores);
        return individual;

    }
    private OntClass addCPURestriction(OntModel baseModel, Package p) {
        OntClass individual = baseModel.createClass(namespace+ UUID.randomUUID());
        individual.addSuperClass(Cloud.CPU);

//        Property hasClockSpeed = baseModel.getProperty(NS + "hasClockSpeed");
//        Literal speed = baseModel.createTypedLiteral(new Float(cpu.getSpeed()));
//        individual.addProperty(hasClockSpeed, speed);

        Literal cores = baseModel.createTypedLiteral(p.getVcpus());
        baseModel.createDatatypeProperty(Cloud.hasCores.getURI());
        //individual.addProperty(Cloud.hasCores, cores);
        individual.addSuperClass(baseModel.createHasValueRestriction(null, Cloud.hasCores, cores));
        return individual;

    }

    private Individual addVirtualMemory(OntModel baseModel, Package p) {
        Individual individual = baseModel.createIndividual(namespace+ UUID.randomUUID(),Cloud.Memory);
        Literal ram = baseModel.createTypedLiteral(p.getMemory());
        baseModel.createDatatypeProperty(Cloud.hasMemorySize.getURI());
        individual.addProperty(Cloud.hasMemorySize, ram);
        return individual;

    }

    private Individual addVolume(OntModel baseModel, Package p) {
        Individual volumeInterface = baseModel.createIndividual(namespace+ UUID.randomUUID(),Cloud.VolumeInterface);
//        Property hasDeviceId = baseModel.getProperty(NS + "hasDeviceId");
//        if(volume.getDevice() != null) {
//            Literal deviceID = offer.createTypedLiteral(volume.getDevice());
//            volumeInterface.addProperty(hasDeviceId, deviceID);
//        }

        Individual individual = baseModel.createIndividual(namespace+ UUID.randomUUID(),Cloud.Volume);
        Literal space = baseModel.createTypedLiteral(p.getDisk());
        baseModel.createDatatypeProperty(Cloud.hasStorageSize.getURI());

        individual.addProperty(Cloud.hasStorageSize, space);
        baseModel.createObjectProperty(Cloud.hasVolume.getURI());

        volumeInterface.addProperty(Cloud.hasVolume, individual);
        return volumeInterface;

    }
    private Individual addVolumeRestriction(OntModel baseModel, Package p) {
        Individual volumeInterface = baseModel.createIndividual(namespace+ UUID.randomUUID(),Cloud.VolumeInterface);
//        Property hasDeviceId = baseModel.getProperty(NS + "hasDeviceId");
//        if(volume.getDevice() != null) {
//            Literal deviceID = offer.createTypedLiteral(volume.getDevice());
//            volumeInterface.addProperty(hasDeviceId, deviceID);
//        }

        Individual individual = baseModel.createIndividual(namespace+ UUID.randomUUID(),Cloud.Volume);
        Literal space = baseModel.createTypedLiteral(p.getDisk());
        baseModel.createDatatypeProperty(Cloud.hasStorageSize.getURI());

        individual.addProperty(Cloud.hasStorageSize, space);
        baseModel.createObjectProperty(Cloud.hasVolume.getURI());

        volumeInterface.addProperty(Cloud.hasVolume, individual);
        return volumeInterface;

    }

    @Override
    public OntModel prepareProposal(OntModel cfp) {
        final String OFFER_CLASS = "https://semanticcloud.github.io/Ontology/cloud.owl#Condition";
        OntModel baseModel = createBaseModel();
        Individual service = baseModel.createIndividual(namespace + "Triton",Cloud.Service);
        List<Package> packages = tritonService.listPakages();
        //Collection<Package> list = cloudApi.packages().list();
        OntClass packageClass = baseModel.createClass(namespace + "Package");
        packageClass.addSuperClass(Cloud.Contanier);
        packageClass.addSuperClass(
                baseModel
                        .createCardinalityRestriction(null, Cloud.hasCPU,1)
        );

//        packageClass.addSuperClass(
//                baseModel.createAllValuesFromRestriction(null, Cloud.hasName, XSD.xstring)
//        );
        packages.forEach( p ->{
            //System.out.println(p);

            OntClass packageLocalClass = baseModel.createClass(namespace + p.getName() + "Package");
            packageLocalClass.addSuperClass(packageClass);

            Individual individual = baseModel.createIndividual(namespace + p.getName(), packageLocalClass);

            baseModel.createObjectProperty(Cloud.hasCPU.getURI());
//            packageClass.addSuperClass(
//                    baseModel
//                            .createCardinalityRestriction(null, Cloud.hasCPU,1)
//            );
            individual.addProperty(Cloud.hasCPU, addCPU(baseModel,p));

            packageLocalClass.addSuperClass(
                    baseModel.createAllValuesFromRestriction(null, Cloud.hasCPU, addCPURestriction(baseModel,p))
            );

            baseModel.createObjectProperty(Cloud.hasMemory.getURI());
            individual.addProperty(Cloud.hasMemory, addVirtualMemory(baseModel,p));

            baseModel.createObjectProperty(Cloud.hasVolumeInterface.getURI());
            individual.addProperty(Cloud.hasVolumeInterface, addVolume(baseModel,p));

//                OntClass aClass = baseModel.createClass(namespace + p.getName());
//                aClass.addSuperClass(computeClass);
//                //Restriction restriction = baseModel.createRestriction(hasCPU);
//                OntClass cpu = baseModel.getOntClass(NS + "CPU");
//                OntClass c = baseModel.createClass(NS + "CPU"+p.getName());
//                //c.addSuperClass(cpu);
//                cpu.addSubClass(c);
//
////        Property hasClockSpeed = baseModel.getProperty(NS + "hasClockSpeed");
////        Literal speed = baseModel.createTypedLiteral(new Float(cpu.getSpeed()));
////        individual.addProperty(hasClockSpeed, speed);
//
//                Property hasCores = baseModel.getProperty(NS + "hasCores");
//                Literal cores = baseModel.createTypedLiteral(p.getVcpus());
//                HasValueRestriction hasValueRestriction = baseModel.createHasValueRestriction(null, hasCores, cores);
//                c.addSuperClass(hasValueRestriction);
//                //individual.addProperty(hasCores, cores);
//                AllValuesFromRestriction hasValueRestriction1 = baseModel.createAllValuesFromRestriction(null, hasCPU, c);
//                aClass.addSuperClass(hasValueRestriction1);


            service.addProperty(Cloud.hasResource,individual);
            baseModel.createObjectProperty(Cloud.hasResource.getURI());

        });

        OntModelSpec ontModelSpec = new OntModelSpec(OntModelSpec.OWL2_MEM);
        ontModelSpec.setReasonerFactory(PelletReasonerFactory.theInstance());
        OntModel m = ModelFactory.createOntologyModel(ontModelSpec, ModelFactory.createUnion(cfp, baseModel));
        OntClass ontClass = m.getOntClass(OFFER_CLASS);

//        cfp.listRestrictions().forEachRemaining(
//                r -> {
//                    //r.is
//                    if(r.onProperty(Cloud.hasCores) && r.isSomeValuesFromRestriction()){
//                        SomeValuesFromRestriction someValuesFromRestriction = r.asSomeValuesFromRestriction();
//                        Resource someValuesFrom = someValuesFromRestriction.getSomeValuesFrom();
//                        //RDFS
//                        Resource propertyResourceValue = someValuesFrom.getPropertyResourceValue(OWL2.withRestrictions);
//
//                        if(propertyResourceValue.canAs(RDFList.class)){
//                            propertyResourceValue.as(RDFList.class).iterator().forEachRemaining(
//                                    n -> System.out.println(n.asResource().getProperty(XSD.maxExclusive))
//                            );
//
//
//                        }
////                        if(someValuesFrom.getProperty(RDF.type)) {
////                            someValuesFrom.listProperties().forEachRemaining(
////
////                                    p -> System.out.print(p)
////                            );
////                        }
//
//
//
//                        System.out.println(r.getOnProperty());
//                    }
//                    //System.out.println(r.get);
//                }
//        );
        if(ontClass != null) {

//            System.out.println("---info disjoint");
//            ontClass.listDisjointWith().toList().forEach(o -> {
//                System.out.println(o);
//            });
//            System.out.println("---info properties");
//            ontClass.listSubClasses().toList().forEach(o -> {
//                System.out.println(o);
//            });
//            System.out.println("---info eqivalent");
            //System.out.println(ontClass.getEquivalentClass());

            ConditionParser restrictionParser = new ConditionParser();
            restrictionParser.parseEqivalentClass(ontClass);


//            ontClass.listEquivalentClasses().toList().forEach(o -> {
//                if(o.isAnon() && o.isIntersectionClass()) {
//                    IntersectionClass intersectionClass = o.asIntersectionClass();
//                    System.out.println(intersectionClass);
//                    System.out.println(intersectionClass.getProperty(intersectionClass.operator()).getObject());
//                    //System.out.println(intersectionClass.);
//                }
//            });
//            System.out.println("---info instances");
//            ontClass.listInstances().toList().forEach(o -> {
//                //System.out.println(o);
//                PelletReasoner reasoner =(PelletReasoner) ((InfModel) m).getReasoner();
//
//
//
//                PelletInfGraph graph = (PelletInfGraph) m.getGraph();
//
//
//                //OntResource equipeInstance = o;
//                    //System.out.println( "Equipe instance: " + equipeInstance.getProperty( Cloud.hasCPU ).getString() );
//
//                    // find out the resources that link to the instance
//                    //for (StmtIterator stmts = m.listStatements( null, null, o ); stmts.hasNext(); ) {
//                        Individual ind = o.as( Individual.class );
//
//
//
//                         //show the properties of this individual
//                        System.out.println( "  " + ind.getURI() );
//                        for (StmtIterator j = ind.listProperties(); j.hasNext(); ) {
//                            Statement s = j.next();
//                            if( !s.getPredicate().getLocalName().equals("type")) continue;
//                            if( !s.getObject().toString().equals("https://semanticcloud.github.io/Ontology/cloud.owl#Condition")) continue;
//                            System.out.print( "    " + s.getPredicate().getLocalName() + " -> " );
//
//
//                            if (s.getObject().isLiteral()) {
//                                System.out.println( s.getLiteral().getLexicalForm() );
//                            }
//                            else {
//                                System.out.println( s.getObject() );
//                            }
//                            //System.out.println(graph.explain(s));
////                            System.out.println("---");
////                            Model explain = graph.explain(s);
////                            //System.out.println(m.getDerivation(s));
////                            explain.write(System.out);
////                            System.out.println("---");
//                        }
//                    }
//            );
//            System.out.println("---info subclasses");
//            ontClass.listSubClasses().toList().forEach(o -> {
//                System.out.println(o);
//            });
//            System.out.println("---info super");
//            ontClass.listSuperClasses().toList().forEach(o -> {
//                System.out.println(o);
//            });
        }

//        System.out.println(baseModel.validate().isValid());
//        ((PelletInfGraph) baseModel.getGraph()).classify();
//        ((PelletInfGraph) baseModel.getGraph()).realize();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("/tmp/" + "model" + ".owl");
            fileOutputStream.write("<?xml version=\"1.0\"?>\n".getBytes(StandardCharsets.UTF_8));
            cfp.write(fileOutputStream, "RDF/XML");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(ontClass != null && ontClass.listInstances().hasNext()) {
            return baseModel;
        }

        return baseModel;
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

}
