package ch.qos.pomapo.agent;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;


public class PomapoTransformer implements ClassFileTransformer {

  static String CLASSNAME = "ch/qos/pomapo/test/Foo";

  @Override
  public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                          ProtectionDomain protectionDomain, byte[] classFileBuffer) throws IllegalClassFormatException {

    //System.out.println("seeing " + className);

    if (CLASSNAME.equals(className)) {
      System.out.println("will transform --- " + CLASSNAME);
      return transformClass(className, classBeingRedefined, classFileBuffer);
    } else {
      return classFileBuffer;
    }

  }

  private byte[] transformClass(String className, Class<?> classBeingRedefined, byte[] originalClassFileBuffer) {
    ClassPool pool = ClassPool.getDefault();
    CtClass ctClass = null;
    byte[] result = null;

    try {
      ctClass = pool.makeClass(new ByteArrayInputStream(originalClassFileBuffer));
      for (CtBehavior ctBehavior : ctClass.getDeclaredBehaviors()) {
        transformMethod(ctBehavior);
      }
      result = ctClass.toBytecode();
    } catch (Exception e) {
      e.printStackTrace();

    } finally {
      if (ctClass != null)
        ctClass.detach();
    }
    if (result != null)
      return result;
    else
      return originalClassFileBuffer;
  }

  private void transformMethod(CtBehavior ctBehavior) throws CannotCompileException {

    String methodName = ctBehavior.getName();
    String className = ctBehavior.getDeclaringClass().getName();
    String methodQualifier = className + '#' + methodName;
    System.out.println("in  transformMethod for" +methodQualifier);
    String profilerClassName = ProfilerGate.class.getName();
    ctBehavior.insertBefore(profilerClassName + ".entry(\"" + methodQualifier + "\")");
    ctBehavior.insertAfter(profilerClassName + ".exit(\"" + methodQualifier + "\")");

  }


}
