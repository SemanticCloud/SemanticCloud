package org.semanticcloud.providers.jclouds;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.semanticcloud.AbstractProvider;
import org.semanticcloud.Cloud;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

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
        JcloudsProvider provider = new JcloudsProvider("http://trystack.com/#","openstack-nova","facebook1832369920122187","gueZneMQzJ8Yr2tg");
        OntModel offer = provider.prepareProposal(null);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream("/tmp/prop"+provider.providerId+".owl");
            fileOutputStream.write("<?xml version=\"1.0\"?>\n".getBytes(StandardCharsets.UTF_8));
            offer.write(fileOutputStream, "RDF/XML");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("/tmp/prop"+provider.providerId+".jsonld");
            //fileOutputStream.write("<?xml version=\"1.0\"?>\n".getBytes(StandardCharsets.UTF_8));

            offer.write(fileOutputStream, "JSON-LD");
            //proposal.write(fileOutputStream, );

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        provider.context.close();
    }

    private Individual createCPU(OntModel offer , Processor cpu) {
        Individual individual = offer.createIndividual(Cloud.CPU);
        //individual = offer.getOntClass(NS + "CPU").createIndividual();
        Property hasClockSpeed = offer.getProperty(NS + "hasClockSpeed");

        Literal speed = offer.createTypedLiteral(new Float(cpu.getSpeed()));
        individual.addProperty(hasClockSpeed, speed);

        Literal cores = offer.createTypedLiteral((new Float(cpu.getCores())).intValue());
        individual.addProperty(Cloud.hasCores, cores);
        //todo hasArchitecture
        return individual;

    }
    private Individual createVirtualMemory(OntModel offer ,int memory) {
        Individual individual = offer.createIndividual(Cloud.Memory);
        Literal ram = offer.createTypedLiteral((new Float(memory)).intValue());
        individual.addProperty(Cloud.hasAvailableSize, ram);
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
        individual.addProperty(Cloud.hasAvailableSize, space);
        volumeInterface.addProperty(Cloud.hasVolume, individual);
        return volumeInterface;

    }

    public JcloudsProvider(String namespace, String providerId, String identity, String credential) {
        super(namespace);
        this.providerId = providerId;
        this.identity = identity;
        this.credential = credential;
        context = ContextBuilder.newBuilder(providerId)
                .endpoint("http://172.16.0.3:5000/v2.0/")
                .credentials("admin:admin", "admin")
                .modules(ImmutableSet.<Module>of(
                        ))
                .buildView(ComputeServiceContext.class);



        client = context.getComputeService();
    }

    @Override
    public OntModel prepareProposal(OntModel cfp) {
        OntModel offer = createBaseModel();
        Set<? extends Hardware> hardwares = client.listHardwareProfiles();
        hardwares.forEach(o -> {
            Individual individual = offer.createIndividual(namespace + o.getId(),Cloud.Compute);
            o.getProcessors().forEach(cpu -> {
                individual.addProperty(Cloud.hasCPU, createCPU(offer,cpu));
            });
            individual.addProperty(Cloud.hasMemory, createVirtualMemory(offer,o.getRam()));
            o.getVolumes().forEach(d -> {
                individual.addProperty(Cloud.hasVolumeInterface, createVolume(offer,d));
            });
            //service.addProperty(hasResource,individual);

        });

        Individual individual = offer.createIndividual(namespace + "test",Cloud.Compute);
        individual.addSameAs(offer.getIndividual(namespace+"RegionOne/2"));
        return offer;
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
