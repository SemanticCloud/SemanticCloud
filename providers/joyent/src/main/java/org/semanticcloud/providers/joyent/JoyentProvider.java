package org.semanticcloud.providers.joyent;

import com.joyent.triton.CloudApi;
import com.joyent.triton.config.*;
import com.joyent.triton.domain.Package;
import org.apache.commons.io.IOUtils;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.util.FileManager;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.semanticcloud.AbstractProvider;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by malagus on 6/1/17.
 */
public class JoyentProvider extends AbstractProvider {
    private CloudApi cloudApi;

    public JoyentProvider() {
        super("https://www.joyent.com/#");
        HashMap<String, String> objectObjectHashMap = new HashMap<>();


        // objectObjectHashMap.put(MapConfigContext.KEY_ID_KEY,"$HOME/.ssh/test_id_rsa");
        objectObjectHashMap.put(MapConfigContext.KEY_ID_KEY, "a6:b2:eb:ad:d7:be:bb:cb:d8:e5:7d:ef:13:06:e1:fa");
        objectObjectHashMap.put(MapConfigContext.KEY_PATH_KEY, "/home/malagus/.ssh/test_id_rsa");
        objectObjectHashMap.put(MapConfigContext.USER_KEY, "malagus");
        objectObjectHashMap.put(MapConfigContext.URL_KEY, "https://eu-ams-1.api.joyent.com");
        MapConfigContext mapConfigContext = new MapConfigContext(objectObjectHashMap);

        ConfigContext context = new ChainedConfigContext(
                new DefaultsConfigContext(),
                //new EnvVarConfigContext(),
                new SystemSettingsConfigContext(),
                mapConfigContext
        );
        cloudApi = new CloudApi(context);
    }

    public static void main(String[] args) throws IOException {
        /* This configures the Triton SDK using defaults, environment variables
         * and Java system properties. */


        //System.out.println(out);
        try {
//            FileManager fileManager = FileManager.get();
//            fileManager.addLocatorFile("/opt/SemanticCloud/");
            FileInputStream fileInputStream = new FileInputStream("/opt/SemanticCloud/req.owl");

            OntModel cfp = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
            cfp.setDynamicImports(true);
            OntDocumentManager dm = cfp.getDocumentManager();
            dm.addAltEntry("http://semantic-cloud.org/Cloud", "file:/opt/SemanticCloud/cloud.owl");
            cfp.read(fileInputStream, "RDF/XML");




            JoyentProvider joyentProvider = new JoyentProvider();
            joyentProvider.prepareProposal(cfp);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



//        CloudApi cloudApi = new CloudApi(context);
//
//        // Each section of the API has its own class
//        Instances instanceApi = cloudApi.instances();
//
//        Iterator<Package> instancesIterator = cloudApi.packages().list().iterator();
//        cloudApi.images().listLatestVersions();
//
//        while (instancesIterator.hasNext()) {
//            Package instance = instancesIterator.next();
//            System.out.println(instance);
//        }
    }
    private Individual addCPU(OntModel baseModel, Package p) {
        Individual individual = baseModel.getOntClass(NS + "CPU").createIndividual();

//        Property hasClockSpeed = baseModel.getProperty(NS + "hasClockSpeed");
//        Literal speed = baseModel.createTypedLiteral(new Float(cpu.getSpeed()));
//        individual.addProperty(hasClockSpeed, speed);

        Property hasCores = baseModel.getProperty(NS + "hasCores");
        Literal cores = baseModel.createTypedLiteral(p.getVcpus());
        individual.addProperty(hasCores, cores);
        return individual;

    }

    private Individual addVirtualMemory(OntModel baseModel, Package p) {
        Individual individual = baseModel.getOntClass(NS + "VirtualMemory").createIndividual();
        Property hasAvailableSize = baseModel.getProperty(NS + "hasAvailableSize");
        Literal ram = baseModel.createTypedLiteral(p.getMemory());
        individual.addProperty(hasAvailableSize, ram);
        return individual;

    }

    private Individual addVolume(OntModel baseModel, Package p) {
        Individual volumeInterface = baseModel.getOntClass(NS + "VolumeInterface").createIndividual();
//        Property hasDeviceId = baseModel.getProperty(NS + "hasDeviceId");
//        if(volume.getDevice() != null) {
//            Literal deviceID = offer.createTypedLiteral(volume.getDevice());
//            volumeInterface.addProperty(hasDeviceId, deviceID);
//        }

        Property hasVolume = baseModel.getProperty(NS + "hasVolume");
        Individual individual = baseModel.getOntClass(NS + "Volume").createIndividual();
        Property hasAvailableSize = baseModel.getProperty(NS + "hasAvailableSize");
        Literal space = baseModel.createTypedLiteral(p.getDisk());
        individual.addProperty(hasAvailableSize, space);
        volumeInterface.addProperty(hasVolume, individual);
        return volumeInterface;

    }

    @Override
    public OntModel prepareProposal(OntModel cfp) {
        final String OFFER_CLASS = "http://semantic-cloud.org/CloudR#test";
        OntModel baseModel = createBaseModel();
        OntClass serviceClass = baseModel.getOntClass(NS + "Service");
        Individual service = serviceClass.createIndividual(namespace + "TritonCompute");
        Property hasResource = baseModel.getProperty(NS + "hasResource");
        OntClass computeClass = baseModel.getOntClass(NS + "Container");
        Property hasCPU = baseModel.getProperty(NS + "hasCPU");
        Property hasMemory = baseModel.getProperty(NS + "hasMemory");
        Property hasVolumeInterface = baseModel.getProperty(NS + "hasVolumeInterface");
        try {
            Collection<Package> list = cloudApi.packages().list();
            list.forEach( p ->{
                Individual individual = computeClass.createIndividual(namespace + p.getId());

                individual.addProperty(hasCPU, addCPU(baseModel,p));
                individual.addProperty(hasMemory, addVirtualMemory(baseModel,p));
                individual.addProperty(hasVolumeInterface, addVolume(baseModel,p));

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


                service.addProperty(hasResource,individual);

            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        OntModel m = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC, ModelFactory.createUnion(cfp, baseModel));
        OntClass ontClass = m.getOntClass(OFFER_CLASS);
//        if(ontClass != null) {
//            System.out.println("---info eqivalent");
//            System.out.println(ontClass.getEquivalentClass());
//            ontClass.listEquivalentClasses().toList().forEach(o -> {
//                System.out.println(o);
//            });
//            System.out.println("---info instances");
//            ontClass.listInstances().toList().forEach(o -> {
//                System.out.println(o);
//            });
//            System.out.println("---info subclasses");
//            ontClass.listSubClasses().toList().forEach(o -> {
//                System.out.println(o);
//            });
//            System.out.println("---info super");
//            ontClass.listSuperClasses().toList().forEach(o -> {
//                System.out.println(o);
//            });
//        }

//        System.out.println(baseModel.validate().isValid());
//        ((PelletInfGraph) baseModel.getGraph()).classify();
//        ((PelletInfGraph) baseModel.getGraph()).realize();
//        try {
//            FileOutputStream fileOutputStream = new FileOutputStream("/tmp/" + "joyent" + ".owl");
//            fileOutputStream.write("<?xml version=\"1.0\"?>\n".getBytes(StandardCharsets.UTF_8));
//            baseModel.write(fileOutputStream, "RDF/XML");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        if(ontClass != null && ontClass.listInstances().hasNext()) {
            return baseModel;
        }

        return null;
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
