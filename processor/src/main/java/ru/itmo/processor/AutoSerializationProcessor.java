package ru.itmo.processor;

import ru.itmo.Letter;
import ru.itmo.Letterize;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.tools.JavaFileObject;

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
	private Messager messager;
	private TypeMirror letterInterface;
	private TypeMirror stringClass;
	private TypeMirror objectClass;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		types = processingEnv.getTypeUtils();
		elements = processingEnv.getElementUtils();
		messager = processingEnv.getMessager();
		letterInterface = elements.getTypeElement(Letter.class.getName()).asType();
		stringClass = elements.getTypeElement(String.class.getName()).asType();
		objectClass = elements.getTypeElement(Object.class.getName()).asType();
		super.init(processingEnv);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver()) {
			return true;
		}

		for (TypeElement annotation : annotations) {
			if (annotation.getSimpleName().toString().equals("LetterizeOptional")) {
				messager.printWarning("LetterizeOptional annotations is unsupported");
				continue;
			}

			Map<Boolean, List<Element>> annotatedEntities = roundEnv.getElementsAnnotatedWith(annotation)
					.stream()
					.collect(Collectors.partitioningBy(cls -> cls.getKind() == ElementKind.CLASS));

			annotatedEntities.get(false).forEach(entity -> {
				messager.printWarning("Unsupported entity", entity);
			});

			List<Element> annotatedClasses = annotatedEntities.get(true);

			Map<TypeElement, List<VariableElement>> classToFields = new HashMap<>();

			for (Element annotatedClass : annotatedClasses) {
				TypeElement cls = (TypeElement) annotatedClass;
				classToFields.put(cls, extractFields(cls));
			}

			try {
				writeJavaFiles(classToFields);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		return false;
	}

	List<VariableElement> extractFields(TypeElement annotatedClass) {
		assert annotatedClass.getKind() == ElementKind.CLASS;
		assert !types.isSameType(annotatedClass.asType(), objectClass);
		assert annotatedClass.getAnnotation(Letterize.class) != null;

		if (!types.isAssignable(annotatedClass.asType(), letterInterface)) {
			messager.printError(String.format(
					"Base class %s doesn't implement the interface %s",
					annotatedClass,
					letterInterface
			));
		}

		checkRecursion(annotatedClass);

		return flattenInheritance(annotatedClass);
	}

	List<VariableElement> flattenInheritance(TypeElement cls) {
		return ElementFilter.fieldsIn(elements.getAllMembers(cls))
				.stream()
				.filter(element -> isSerializable((TypeElement) element.getEnclosingElement()))
				.toList();
	}

	void checkRecursionImpl(TypeElement rootClass, Set<TypeElement> typeElements) {
		assert !isPrimitiveOrString(rootClass);
		assert isSerializable(rootClass);
		assert rootClass.getKind() == ElementKind.CLASS;

		if (typeElements.contains(rootClass)) {
			messager.printError("Class hierarchy has a possible recursion which is unsupported");
			return;
		}
		typeElements.add(rootClass);

		List<VariableElement> fields = ElementFilter.fieldsIn(rootClass.getEnclosedElements());

		for (VariableElement field : fields) {
			if (isPrimitiveOrString(field)) {
				continue;
			}

			if (field.asType().getKind() != TypeKind.DECLARED) {
				messager.printError("Only classes supported as reference type fields");
			}

			TypeElement classField = (TypeElement) types.asElement(field.asType());

			if (!isSerializable(classField)) {
				messager.printError(String.format("Non serializable field in %s", rootClass));
			}

			checkRecursionImpl(classField, typeElements);
		}
	}

	void checkRecursion(TypeElement root) {
		Set<TypeElement> typeElements = new HashSet<>();
		checkRecursionImpl(root, typeElements);
	}

	boolean isSerializable(TypeElement cls) {
		return cls.getAnnotation(Letterize.class) != null &&
				types.isAssignable(cls.asType(), letterInterface) ||
				types.isSameType(cls.asType(), stringClass);
	}

	boolean isPrimitiveOrString(Element element) {
		return element.asType().getKind().isPrimitive() ||
				types.isSameType(element.asType(), stringClass);
	}

	String getTypeString(TypeKind kind) {
		return switch(kind) {
			case BOOLEAN -> "Boolean";
			case SHORT, INT, BYTE, CHAR -> "Int";
			case FLOAT -> "Float";
			case DOUBLE -> "Double";
			case DECLARED -> "UTF";
			default -> throw new IllegalStateException("Unexpected value: " + kind);
		};
	}

	void writeJavaFiles(Map<TypeElement, List<VariableElement>> classToFields) throws IOException {
		writeSerializationFile(classToFields, true);
		writeSerializationFile(classToFields, false);
	}

	void writeSerializationFile(Map<TypeElement, List<VariableElement>> classToFields, boolean serializer) throws IOException {
		Filer filer = processingEnv.getFiler();

		String className = serializer ? ".Serializer" : ".Deserializer";
		String streamType = serializer ? "DataOutputStream" : "DataInputStream";

		JavaFileObject javaFile = filer.createSourceFile(PACKAGE_NAME + className);
		JavaWriter javaWriter = new JavaWriter(javaFile.openWriter());

		javaWriter.emitPackage(PACKAGE_NAME);
		javaWriter.emitImports(
				String.format("java.io.%s", streamType),
				"java.io.IOException"
		);

		if (!serializer) {
			javaWriter.emitImports("ru.itmo.processor.IllegalLetterFormatException");
		}

		writeSerializationClass(javaWriter, classToFields, serializer);

		javaWriter.close();
	}

	void writeSerializationClass(JavaWriter writer, Map<TypeElement, List<VariableElement>> classToFields, boolean serializer) throws IOException {
		String classFullName = serializer ? SER_FULL_NAME : DESER_FULL_NAME;
		String className = serializer ? "Serializer" : "Deserializer";
		String streamType = serializer ? "DataOutputStream" : "DataInputStream";
		String streamName = "iostream";

		writer
				.beginType(classFullName, "class", EnumSet.of(Modifier.PUBLIC))
				.emitField(streamType, streamName, EnumSet.of(Modifier.PRIVATE, Modifier.FINAL))
				// Constructor
				.beginMethod(
						null,
						className,
						EnumSet.of(Modifier.PUBLIC),
						streamType,
						streamName
				)
				.emitStatement("this.%s = %s", streamName, streamName)
				.endMethod();

		for (Map.Entry<TypeElement, List<VariableElement>> entry : classToFields.entrySet()) {
			writeSerializationMethod(writer, entry.getKey(), entry.getValue(), serializer);
		}

		writer.endType();
	}

	void writeSerializationMethod(JavaWriter writer, TypeElement cls, List<VariableElement> fields, boolean serializer) throws IOException {
		String className = cls.getSimpleName().toString();
		String returnType = (serializer ? "void" : className);
		String methodName = (serializer ? SER_PREFIX : DESER_PREFIX) + className;
		String objectName = Character.toLowerCase(className.charAt(0)) + className.substring(1);
		List<String> parameters = (serializer ? List.of(className, objectName) : List.of());

		writer.beginMethod(
				returnType,
				methodName,
				EnumSet.of(Modifier.PUBLIC),
				parameters,
				List.of("IOException")
		);

		if (!serializer) {
			writer
					.emitStatement("int discriminator = 0")
					.emitStatement("%s %s = new %s()", className, objectName, className);
		}

		for (VariableElement field : fields) {
			if (serializer) {
				writeSerializeField(writer, field, objectName);
			} else {
				writeDeserializeField(writer, field, objectName);
			}
		}

		if (!serializer) {
			writer.emitStatement("return %s", objectName);
		}

		writer.endMethod();
	}

	void writeSerializeField(JavaWriter writer, VariableElement field, String objectName) throws IOException {
		String fieldName = field.getSimpleName().toString();
		// Doing this to avoid name shadowing
		String ownerCast = field.getEnclosingElement().getSimpleName().toString();
		String fieldLocation = String.format("((%s)%s).%s", ownerCast, objectName, fieldName);

		if (!isPrimitiveOrString(field)) {
			Element classElement = types.asElement(field.asType());
			String className = classElement.getSimpleName().toString();

			writer
					.beginControlFlow(String.format("if (%s == null)", fieldLocation))
					.emitStatement("iostream.writeInt(0)")
					.nextControlFlow("else")
					.emitStatement("iostream.writeInt(1)")
					.emitStatement("%s%s(%s)", SER_PREFIX, className, fieldLocation)
					.endControlFlow();
		} else {
			String typeName = getTypeString(field.asType().getKind());
			writer.emitStatement("iostream.write%s(%s)", typeName, fieldLocation);
		}
	}

	void writeDeserializeField(JavaWriter writer, VariableElement field, String objectName) throws IOException {
		String fieldName = field.getSimpleName().toString();
		// Doing this to avoid name shadowing
		String ownerCast = field.getEnclosingElement().getSimpleName().toString();
		String fieldLocation = String.format("((%s)%s).%s", ownerCast, objectName, fieldName);

		if (!isPrimitiveOrString(field)) {
			Element classElement = types.asElement(field.asType());
			String className = classElement.getSimpleName().toString();

			writer
					.emitStatement("discriminator = iostream.readInt()")
					.beginControlFlow("if (discriminator == 0)")
					.emitStatement("%s = null", fieldLocation)
					.nextControlFlow("else if (discriminator == 1)")
					.emitStatement("%s = %s%s()", fieldLocation, DESER_PREFIX, className)
					.nextControlFlow("else")
					.emitStatement("throw new IllegalLetterFormatException()")
					.endControlFlow();
		} else {
			String typeName = getTypeString(field.asType().getKind());
			writer.emitStatement("%s = iostream.read%s()", fieldLocation, typeName);
		}
	}
}