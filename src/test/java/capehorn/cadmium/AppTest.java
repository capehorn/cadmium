package capehorn.cadmium;


import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.classfile.*;


class AppTest {

    @Test
    void testApp() throws IOException {
        ClassModel cm = ClassFile.of().parse(readFile("capehorn/cadmium/App.class"));
        for (ClassElement ce : cm) {
            switch (ce) {
                case MethodModel mm -> System.out.printf("Method %s%n", mm.methodName().stringValue());
                case FieldModel fm -> System.out.printf("Field %s%n", fm.fieldName().stringValue());
                default -> { }
            }
        }
    }

    private byte[] readFile(String fileName) throws IOException, IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(fileName);) {
            return is.readAllBytes();
        }
    }
}
