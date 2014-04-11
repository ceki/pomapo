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

  static String CLASSNAME = "ch.qos.pomapo.test.Foo";

  @Override
  public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                          ProtectionDomain protectionDomain, byte[] classFileBuffer) throws IllegalClassFormatException {

    if (CLASSNAME.equals(className)) {
      return transformClass(className, classBeingRedefined, classFileBuffer);
    } else {
      return classFileBuffer;
    }

  }

  private byte[] transformClass(String className, Class<?> classBeingRedefined, byte[] classFileBuffer) {
    ClassPool pool = ClassPool.getDefault();
    CtClass cl = null;
    byte[] result = null;
    try {
      cl = pool.makeClass(new ByteArrayInputStream(classFileBuffer));
      for (CtBehavior ctBehavior : cl.getDeclaredBehaviors()) {
        transformMethod(ctBehavior);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (cl != null)
        cl.detach();
    }
    if (result != null)
      return result;
    else
      return classFileBuffer;
  }

  private void transformMethod(CtBehavior ctBehavior) throws CannotCompileException {
    String methodName = ctBehavior.getName();
    String className = ctBehavior.getDeclaringClass().getName();
    String methodQualifier = className + '#' + methodName;
    String profilerClassName = ProfilerGate.class.getName();
    ctBehavior.insertBefore(profilerClassName + ".entry(\"" + methodQualifier + "\")");
    ctBehavior.insertAfter(profilerClassName + ".exit(\"" + methodQualifier + "\")");

  }


}
