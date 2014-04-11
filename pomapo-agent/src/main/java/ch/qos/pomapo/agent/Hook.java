package ch.qos.pomapo.agent;

public class Hook extends Thread {
  public void run() {
    System.out.println("Pomapo Agent stopped");
  }
}
