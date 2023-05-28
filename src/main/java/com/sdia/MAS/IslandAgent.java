package com.sdia.MAS;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.Arrays;
import java.util.Random;

public class IslandAgent extends Agent {
    Population population=new Population();
    Random rnd=new Random();

    @Override
    protected void setup() {
        DFAgentDescription dfAgentDescription = new DFAgentDescription();
        dfAgentDescription.setName(getAID());
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType("ga");
        serviceDescription.setName("island");
        dfAgentDescription.addServices(serviceDescription);
        try {
            DFService.register(this, dfAgentDescription);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
        SequentialBehaviour sequentialBehaviour=new SequentialBehaviour();
        sequentialBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                population.initializePopulation();
                population.calculateIndividualFitness();
                population.sortPopulation();
            }
        });

        sequentialBehaviour.addSubBehaviour(new Behaviour() {
            int it=0;
            @Override
            public void action() {
                population.selection();
                population.crossover();
                population.mutation();
                population.calculateIndividualFitness();
                population.sortPopulation();
                it++;
            }

            @Override
            public boolean done() {
                return it>=GAUtils.MAX_IT || population.getBestFitnessInd().getFitness()==GAUtils.CHROMOSOME_SIZE;
            }
        });

        sequentialBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription dfAgentDescription1=new DFAgentDescription();
                ServiceDescription serviceDescription1=new ServiceDescription();
                serviceDescription1.setType("ga");
                serviceDescription1.setName("master");
                DFAgentDescription[] agentDescriptions;
                try {
                    agentDescriptions = DFService.search(getAgent(), dfAgentDescription1);
                } catch (FIPAException e) {
                    throw new RuntimeException(e);
                }
                ACLMessage aclMessage=new ACLMessage(ACLMessage.INFORM);
                aclMessage.addReceiver(agentDescriptions[0].getName());
                aclMessage.setContent(Arrays.toString(population.getBestFitnessInd().getGenes())+"-"+population.getBestFitnessInd().getFitness());
                send(aclMessage);
            }
        });
        addBehaviour(sequentialBehaviour);
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
    }
}