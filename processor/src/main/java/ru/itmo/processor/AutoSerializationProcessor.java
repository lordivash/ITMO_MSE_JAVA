package ru.itmo.processor;

import ru.itmo.Letter;
import ru.itmo.Letterize;
import ru.itmo.LetterizeOptional;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.util.Types;

@SupportedSourceVersion(SourceVersion.RELEASE_21)
@SupportedAnnotationTypes({"ru.itmo.Letterize", "ru.itmo.LetterizeOptional"})
public class AutoSerializationProcessor extends AbstractProcessor {

	private static final String PACKAGE_NAME = "ru.itmo";
	private static final String SER_FULL_NAME = "ru.itmo.Serializer";
	private static final String DESER_FULL_NAME = "ru.itmo.Deserializer";
	private static final String SER_PREFIX = "serialize";
	private static final String DESER_PREFIX = "deserialize";

	private Types types;
	private Elements elements;
	private TypeMirror letterInterface;
	private TypeMirror stringClass;
	private Set<TypeElement> serMethodsCreated = new HashSet<>();
	private Set<TypeElement> deserMethodsCreated = new HashSet<>();

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		types = processingEnv.getTypeUtils();
		elements = processingEnv.getElementUtils();
		letterInterface = elements.getTypeElement(Letter.class.getName()).asType();
		stringClass = elements.getTypeElement(String.class.getName()).asType();
		super.init(processingEnv);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver()) {
			return true;
		}

		return false;
	}
}