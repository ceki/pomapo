package ch.qos.pomapo.agent;

public class ProfilerGate {


  static void entry(String fullyQualifiedMethodName) {
    System.out.println("entry " + fullyQualifiedMethodName);
  }

  static void exit(String fullyQualifiedMethodName) {
    System.out.println("exit " + fullyQualifiedMethodName);
  }
}
