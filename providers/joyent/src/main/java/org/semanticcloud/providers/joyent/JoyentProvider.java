package org.semanticcloud.providers.joyent;

import com.joyent.triton.CloudApi;
import com.joyent.triton.config.ChainedConfigContext;
import com.joyent.triton.config.ConfigContext;
import com.joyent.triton.config.DefaultsConfigContext;
import com.joyent.triton.config.MapConfigContext;
import com.joyent.triton.config.SystemSettingsConfigContext;
import com.joyent.triton.domain.Instance;
import org.semanticcloud.providers.joyent.triton.domain.Package;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.semanticcloud.AbstractProvider;
import org.semanticcloud.Cloud;
import org.semanticcloud.providers.joyent.triton.TritonService;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by malagus on 6/1/17.
 */
public class JoyentProvider extends AbstractProvider {
    private CloudApi cloudApi;
    private TritonService tritonService;

    public JoyentProvider() {
        super("https://www.joyent.com/#");
        HashMap<String, String> objectObjectHashMap = new HashMap<>();


        // objectObjectHashMap.put(MapConfigContext.KEY_ID_KEY,"$HOME/.ssh/test_id_rsa");
        objectObjectHashMap.put(MapConfigContext.KEY_ID_KEY, "93:3f:61:e4:b6:f0:71:4c:e1:8d:e8:4b:d1:26:c2:67");
        objectObjectHashMap.put(MapConfigContext.KEY_PATH_KEY, "/home/l.smolaga/.ssh/test2_id_rsa");
        objectObjectHashMap.put(MapConfigContext.USER_KEY, "malagus");
        objectObjectHashMap.put(MapConfigContext.URL_KEY, "https://eu-ams-1.api.joyent.com");
        MapConfigContext mapConfigContext = new MapConfigContext(objectObjectHashMap);

        ConfigContext context = new ChainedConfigContext(
                new DefaultsConfigContext(),
                //new EnvVarConfigContext(),
                new SystemSettingsConfigContext(),
                mapConfigContext
        );
        try {
            tritonService = new TritonService("https://eu-ams-1.api.joyent.com","malagus","/home/l.smolaga/.ssh/test2_id_rsa");
        } catch (IOException e) {
            e.printStackTrace();
        }
        cloudApi = new CloudApi(context);
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
//            FileManager fileManager = FileManager.get();
//            fileManager.addLocatorFile("/opt/SemanticCloud/");
//            FileInputStream fileInputStream = new FileInputStream("/opt/SemanticCloud/req.owl");
//
//            OntModel cfp = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
//            cfp.setDynamicImports(true);
//            OntDocumentManager dm = cfp.getDocumentManager();
//            dm.addAltEntry("http://semantic-cloud.org/Cloud", "file:/opt/SemanticCloud/cloud.owl");
//            cfp.read(fileInputStream, "RDF/XML");




            JoyentProvider provider = new JoyentProvider();
            //joyentProvider.prepareProposal(cfp);
            OntModel baseModel = provider.createBaseModel();
            baseModel.createClass(OFFER_CLASS);

            OntModel offer = provider.prepareProposal(baseModel);
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
        provider.listResources();

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
        try {
            Iterator<Instance> list = cloudApi.instances().list();
            list.forEachRemaining(i ->{
                System.out.println(i);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Individual addCPU(OntModel baseModel, Package p) {
        Individual individual = baseModel.createIndividual(Cloud.CPU);

//        Property hasClockSpeed = baseModel.getProperty(NS + "hasClockSpeed");
//        Literal speed = baseModel.createTypedLiteral(new Float(cpu.getSpeed()));
//        individual.addProperty(hasClockSpeed, speed);

        Literal cores = baseModel.createTypedLiteral(p.getVcpus());
        individual.addProperty(Cloud.hasCores, cores);
        return individual;

    }

    private Individual addVirtualMemory(OntModel baseModel, Package p) {
        Individual individual = baseModel.createIndividual(Cloud.Memory);
        Literal ram = baseModel.createTypedLiteral(p.getMemory());
        individual.addProperty(Cloud.hasAvailableSize, ram);
        return individual;

    }

    private Individual addVolume(OntModel baseModel, Package p) {
        Individual volumeInterface = baseModel.createIndividual(Cloud.VolumeInterface);
//        Property hasDeviceId = baseModel.getProperty(NS + "hasDeviceId");
//        if(volume.getDevice() != null) {
//            Literal deviceID = offer.createTypedLiteral(volume.getDevice());
//            volumeInterface.addProperty(hasDeviceId, deviceID);
//        }

        Individual individual = baseModel.createIndividual(Cloud.Volume);
        Literal space = baseModel.createTypedLiteral(p.getDisk());
        individual.addProperty(Cloud.hasAvailableSize, space);
        volumeInterface.addProperty(Cloud.hasVolume, individual);
        return volumeInterface;

    }

    @Override
    public OntModel prepareProposal(OntModel cfp) {
        final String OFFER_CLASS = "http://semantic-cloud.org/CloudR#test";
        OntModel baseModel = createBaseModel();
        Individual service = baseModel.createIndividual(namespace + "TritonCompute",Cloud.Service);
        List<Package> packages = tritonService.listPakages();
        //Collection<Package> list = cloudApi.packages().list();
        packages.forEach( p ->{
            System.out.println(p);
            Individual individual = baseModel.createIndividual(namespace + p.getName(), Cloud.Contanier);

            individual.addProperty(Cloud.hasCPU, addCPU(baseModel,p));
            individual.addProperty(Cloud.hasMemory, addVirtualMemory(baseModel,p));
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

        });

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
