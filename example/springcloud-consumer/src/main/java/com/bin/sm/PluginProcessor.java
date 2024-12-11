package com.bin.sm;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Objects.isNull;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.NestingKind.TOP_LEVEL;


public class PluginProcessor extends AbstractProcessor {

    TreeSet<String> processAnnotations = new TreeSet<>();

    public PluginProcessor() {
        super();
        processAnnotations.add(SuppressWarnings.class.getCanonicalName());

    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return processAnnotations;
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }
        final BeanUtils utils = BeanUtils.of(processingEnv);
        Elements elements = processingEnv.getElementUtils();
        LinkedHashMap<String, TypeElement> plugins = searchPluginClass(roundEnv.getRootElements(), utils);
        if (plugins == null || plugins.isEmpty()) {
            processingEnv.getMessager().printMessage(Kind.WARNING, "Can't find AgentPlugin class!");
            return false;
        }
        Set<Class<? extends Annotation>> classes = new HashSet<>();
      //  classes.add(AdvicesTo.class);

        Set<TypeElement> interceptors = process(classes, elements, roundEnv);
        // generate providerBean
        generateProviderBeans(plugins, interceptors, utils);

        return false;
    }



    private Set<TypeElement> process(Set<Class<? extends Annotation>> annotationClasses,
                                     Elements elements,
                                     RoundEnvironment roundEnv) {
        TreeSet<String> services = new TreeSet<>();
        Set<TypeElement> types = new HashSet<>();
        Class<?> dstClass = Object.class; // Interceptor

        Set<Element> roundElements = new HashSet<>();
        for (Class<? extends Annotation> annotationClass : annotationClasses) {
            Set<? extends Element> es = roundEnv.getElementsAnnotatedWith(annotationClass);
            roundElements.addAll(es);
        }

        for (Element e : roundElements) {
            if (!e.getKind().isClass() || e.getModifiers().contains(ABSTRACT)) {
                continue;
            }
            TypeElement type = (TypeElement)e;
            types.add(type);
            services.add(elements.getBinaryName(type).toString());
        }
        if (services.isEmpty()) {
            return types;
        }
        writeToMetaInf(dstClass, services);

        return types;
    }

    private void writeToMetaInf(Class<?> dstClass, Collection<String> services) {
        String fileName = "META-INF/services/" + dstClass.getCanonicalName();

        if (services.isEmpty()) {
            return;
        }

        Filer filer = processingEnv.getFiler();
        PrintWriter pw = null;
        try {
            processingEnv.getMessager().printMessage(Kind.NOTE,"Writing " + fileName);
            FileObject f = filer.createResource(StandardLocation.CLASS_OUTPUT, "", fileName);
            pw = new PrintWriter(new OutputStreamWriter(f.openOutputStream(), StandardCharsets.UTF_8));
            services.forEach(pw::println);
        } catch (IOException x) {
            processingEnv.getMessager().printMessage(Kind.ERROR, "Failed to write generated files: " + x);
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    LinkedHashMap<String, TypeElement> searchPluginClass(Set<? extends Element> elements, BeanUtils utils) {
        TypeElement findInterface = utils.getTypeElement(Object.class.getCanonicalName()); // AgentPlugin
        TypeElement found;

        ArrayList<TypeElement> plugins = new ArrayList<>();
        ElementVisitor8 visitor = new ElementVisitor8(utils);
        for (Element e : elements) {
            found = e.accept(visitor, findInterface);
            if (found != null) {
                plugins.add(found);
            }
        }
        LinkedHashMap<String, TypeElement> pluginNames = new LinkedHashMap<>();
        for (TypeElement p : plugins) {

//            ClassName className = utils.classNameOf(p);
//            pluginNames.put(className.canonicalName(), p);
        }
        writeToMetaInf(Object.class, pluginNames.keySet()); // AgentPlugin

        return pluginNames;
    }

    private void generateProviderBeans(LinkedHashMap<String, TypeElement> plugins,
                                       Set<TypeElement> interceptors, BeanUtils utils) {
        TreeSet<String> providers = new TreeSet<>();
        TreeSet<String> points = new TreeSet<>();
        for (TypeElement type : interceptors) {
            if(isNull(type.getAnnotation(SuppressWarnings.class)) // AdviceTo
                    && isNull(type.getAnnotation(SuppressWarnings.class))) { // AdvicesTo
                continue;
            }
            List<? extends AnnotationMirror> annotations = type.getAnnotationMirrors();
            Set<AnnotationMirror> adviceToAnnotations = new HashSet<>();
            for (AnnotationMirror annotation : annotations) {
                if (utils.isSameType(annotation.getAnnotationType(), SuppressWarnings.class.getCanonicalName())) { // AdviceTo
                    adviceToAnnotations.add(annotation);
                    continue;
                }
                if (!utils.isSameType(annotation.getAnnotationType(), SuppressWarnings.class.getCanonicalName())) { // AdvicesTo
                    continue;
                }
                Map<? extends ExecutableElement, ? extends AnnotationValue> values = annotation.getElementValues();
                RepeatedAnnotationVisitor visitor = new RepeatedAnnotationVisitor();
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> e : values.entrySet()) {
                    String key = e.getKey().getSimpleName().toString();
                    if (key.equals("value")) {
                        AnnotationValue av = e.getValue();
                        Set<AnnotationMirror> as = av.accept(visitor, SuppressWarnings.class); // AdvicesTo
                        adviceToAnnotations.addAll(as);
                        break;
                    }
                }
            }

            int seq = 0;
            TypeElement plugin = plugins.values().toArray(new TypeElement[0])[0];
            for (AnnotationMirror annotation : adviceToAnnotations) {
                Map<? extends ExecutableElement, ? extends AnnotationValue> values = annotation.getElementValues();
                Map<String, String> to = new HashMap<>();
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> e : values.entrySet()) {
                    String key = e.getKey().getSimpleName().toString();
                    AnnotationValue av = e.getValue();
                    String value;
                    if (av.getValue() == null) {
                        value = "default";
                    } else {
                        value = av.getValue().toString();
                    }
                    to.put(key, value);
                    if (key.equals("value")) {
                        points.add(value);
                    } else if (key.equals("plugin") && plugins.get(value) != null) {
                        plugin = plugins.get(value);
                    }
                }
                to.put("seq", Integer.toString(seq));
//                GenerateProviderBean gb = new GenerateProviderBean(plugin, type, to, utils);
//                JavaFile file = gb.apply();
//                try {
//                    file.toBuilder().indent("    ")
//                            .addFileComment("This ia a generated file.")
//                            .build().writeTo(processingEnv.getFiler());
//                    providers.add(gb.getProviderClass());
//                    seq += 1;
//                } catch (IOException e) {
//                    processingEnv.getMessager().printMessage(Kind.ERROR, e.getLocalizedMessage());
//                }
            }
        }
        writeToMetaInf(Object.class, points); // Points
        writeToMetaInf(Object.class, providers); // InterceptorProvider
    }


    public static List<String> get(TypeElement element) {

        List<String> names = new ArrayList<>();
        for (Element e = element; isClassOrInterface(e); e = e.getEnclosingElement()) {
//            checkArgument(element.getNestingKind() == TOP_LEVEL || element.getNestingKind() == MEMBER,
//                    "unexpected type testing");
            names.add(e.getSimpleName().toString());
        }
        names.add(getPackage(element).getQualifiedName().toString());
        Collections.reverse(names);
        return names;
    }

    private static boolean isClassOrInterface(Element e) {
        return e.getKind().isClass() || e.getKind().isInterface();
    }

    private static PackageElement getPackage(Element type) {
        while (type.getKind() != ElementKind.PACKAGE) {
            type = type.getEnclosingElement();
        }
        return (PackageElement) type;
    }
}
