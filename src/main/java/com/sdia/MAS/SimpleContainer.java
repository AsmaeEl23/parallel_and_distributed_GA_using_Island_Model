package com.sdia.MAS;

import jade.wrapper.AgentContainer;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;

public class SimpleContainer {
    public static void main(String[] args) throws Exception {
        Runtime runtime=Runtime.instance();
        ProfileImpl profile=new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST,"localhost");
        AgentContainer agentContainer=runtime.createAgentContainer(profile);
        for(int i=0;i<GAUtils.ISLAND_NUMBER;i++){
            AgentController islandAgent = agentContainer.createNewAgent("islandAgent" + i, IslandAgent.class.getName(), new Object[]{});
            islandAgent.start();
        }
        AgentController masterAgent = agentContainer.createNewAgent("masterAgent", MasterAgent.class.getName(), new Object[]{});
        masterAgent.start();
    }
}
