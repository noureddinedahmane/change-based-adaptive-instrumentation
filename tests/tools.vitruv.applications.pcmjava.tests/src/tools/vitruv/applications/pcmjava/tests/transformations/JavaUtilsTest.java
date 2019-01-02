package tools.vitruv.applications.pcmjava.tests.transformations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.members.ClassMethod;
import org.junit.Test;

import tools.vitruv.applications.pcmjava.util.pcm2java.Pcm2JavaUtils;
import tools.vitruv.domains.java.util.JavaModificationUtil;

/**
 * Test for the JaMoPPPCMUtil class - not a utility class. Tests the textual creation of a
 * CompilationUnit using JaMoPP.
 *
 * @author Langhamm
 *
 */
public class JavaUtilsTest {

    @Test
    public void testCreateCompilationUnit() throws IOException {
        final String className = "TestCollectionDataType";
        @SuppressWarnings("rawtypes")
        final Class<? extends Collection> selectedClass = ArrayList.class;
        final String content = "package " + "datatypes;" + "\n\n" + "import " + selectedClass.getPackage().getName()
                + "." + selectedClass.getSimpleName() + ";\n\n" + "public class " + className + " extends "
                + selectedClass.getSimpleName() + "<" + "String" + ">" + " {\n" + "\n\n" + "}";
        final CompilationUnit cu = JavaModificationUtil.createCompilationUnit(className, content);

        assertEquals("CompilationUnit name is wrong", cu.getName(), className + ".java");
        assertTrue("No classifier in compliation unit", cu.getClassifiers().size() == 1);
        assertEquals("ClassifierName name is wrong", cu.getClassifiers().get(0).getName(), className);
    }

    @Test
    public void testCreateMethod() throws Throwable {
        final String content = "public void test() {\n" + "System.out.println(\"Hello world\");" + "\n}";
        final EObject eObject = Pcm2JavaUtils.createJaMoPPMethod(content);

        assertNotNull("eObject is null", eObject);
        assertTrue("eObject is not instance of method", eObject instanceof ClassMethod);
        final ClassMethod cm = (ClassMethod) eObject;
        assertEquals("Method name is not the expected method name", "test", cm.getName());
    }
}
