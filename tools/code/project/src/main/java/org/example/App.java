package org.example;

import java.util.*;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setTemplateMode(TemplateMode.TEXT);
        resolver.setPrefix("templates/");

        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);

        Context c = new Context();
        Unit unit = new Unit("Software Tools", Arrays.asList("Linux", "Git", "Maven"));
        c.setVariable("unit", unit);
        String greeting = engine.process("unit", c);

        System.out.println(greeting);
    }
}
