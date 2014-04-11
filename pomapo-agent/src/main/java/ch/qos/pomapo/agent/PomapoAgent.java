package ch.qos.pomapo.agent;

import java.lang.instrument.Instrumentation;

public class PomapoAgent {

    public  static void premain(String agentArgument, Instrumentation instrumentation) {
      System.out.println("In PomapoAgent.premain");
      instrumentation.addTransformer(new PomapoTransformer());
      Runtime.getRuntime().addShutdownHook(new Hook());
    }

}
