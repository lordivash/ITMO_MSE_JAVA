package ru.itmo;

import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.JavaFile;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterSpec;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record Implementor(String outputDirectory) {

	/**
	 * Имплементор по данной папке с class файлами java ищет в ней java класс, которые требуется реализовать.
	 * Класс записывает реализацию в папку `outputDirectory` (учитывая пакеты)
	 * и возвращает полное название нового класса.
	 * Реализация должна находится в том же пакете, что и исходный класс/интерфейс.
	 * <p>
	 * Например, требуется реализовать интерфейс `ru.itmo.AnInterface`.
	 * Тогда в папке directoryPath ожидается файл ru/itmo/AnInterface.class.
	 * Implementor генерирует реализацию этого интерфейса, кладет её в
	 * <outputDirectory>/ru/itmo/AnInterfaceImpl.java
	 * и возвращает полное имя сгенерированного класса ru.itmo.AnInterfaceImpl.
	 *
	 * @param directoryPath путь до директории, которая содержит данный класс/интерфейс
	 * @param className     полное название класса/интерфейса, которое требуется реализовать
	 * @throws ImplementorException в тех ситуациях когда
	 *                              1) Невозможно создать наследника класса.
	 *                              2) Входной класс не найден.
	 *                              3) Невозможно записать сгенерированный класс.
	 */
	public String implementFromDirectory(String directoryPath, String className) throws ImplementorException {
		try {
			URL url = Paths.get(directoryPath).toUri().toURL();
			URLClassLoader classLoader = new URLClassLoader(new URL[]{url});
			Class<?> cls = classLoader.loadClass(className);

			if (!supportedClass(cls)) {
				throw new ImplementorException("Class" + className + " is neither abstract nor interface");
			}
			return generateImplementation(cls, cls.getPackageName());

		} catch (MalformedURLException e) {
			throw new ImplementorException("Invalid directory path: " + directoryPath, e);
		} catch (ClassNotFoundException e) {
			throw new ImplementorException("Class not found: " + className, e);
		}
	}


	/**
	 * Имплементор ищет java класс/интерфейс из стандартной библиотеки, которые требуется реализовать.
	 * Класс записывает реализацию в папку `outputDirectory`.
	 * Реализация должна находится в default пакете.
	 * <p>
	 * Например, требуется реализовать интерфейс `java.lang.Comparable`
	 * Имплементор генерирует реализацию этого интерфейса, кладет её в `outputDirectory/ComparableImpl.java и
	 * возвращает полное имя сгенерированного класса ComparableImpl.
	 *
	 * @param className полное название класса/интерфейса, которое требуется реализовать
	 * @return полное название нового класса, например ComparableImpl
	 * @throws ImplementorException в тех ситуациях когда
	 *                              1) Невозможно создать наследника класса.
	 *                              2) Входной класс не найден.
	 *                              3) Невозможно записать сгенерированный класс.
	 */
	public String implementFromStandardLibrary(String className) throws ImplementorException {
		Class<?> foundClass;
		try {
			foundClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new ImplementorException("Class was not found in standard library: " + className, e);
		}

		if (!supportedClass(foundClass)) {
			throw new ImplementorException("Class is neither abstract nor interface: " + className);
		}

		return generateImplementation(foundClass, "");
	}

	private boolean supportedClass(Class<?> cls) {
		return cls.isInterface() || Modifier.isAbstract(cls.getModifiers());
	}

	private String generateImplementation(Class<?> cls, String packageName) throws ImplementorException {
		String implClassName = cls.getSimpleName() + "Impl";
		String fullClassName = packageName + implClassName;

		TypeSpec.Builder classBuilder = TypeSpec.classBuilder(implClassName)
				.addModifiers(javax.lang.model.element.Modifier.PUBLIC);

		if (cls.isInterface()) {
			classBuilder.addSuperinterface(ClassName.get(cls));
		} else {
			classBuilder.superclass(ClassName.get(cls));
			generateConstructors(cls, classBuilder);
		}

		generateMethods(cls, classBuilder);

		try {
			JavaFile javaFile = JavaFile.builder(packageName, classBuilder.build()).build();
			javaFile.writeTo(Paths.get(outputDirectory));
		} catch (IOException e) {
			throw new ImplementorException("Failed to write generated file to directory", e);
		}

		return fullClassName;
	}

	private void generateConstructors(Class<?> cls, TypeSpec.Builder classBuilder) throws ImplementorException {
		List<Constructor<?>> constructors = Arrays.stream(cls.getDeclaredConstructors())
				.filter(c -> Modifier.isPublic(c.getModifiers()) || Modifier.isProtected(c.getModifiers()))
				.toList();

		if (constructors.isEmpty()) {
			throw new ImplementorException("No accessible constructors in class: " + cls.getName());
		}

		for (Constructor<?> constructor : constructors) {
			MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
					.addModifiers(javax.lang.model.element.Modifier.PUBLIC);

			List<ParameterSpec> params = new ArrayList<>();

			Parameter[] parameters = constructor.getParameters();
			for (Parameter parameter : parameters) {
				ParameterSpec param = ParameterSpec.builder(parameter.getType(), parameter.getName()).build();
				params.add(param);
				constructorBuilder.addParameter(param);
			}

			String superCall = "super(" + params.stream().map(ParameterSpec::name).collect(Collectors.joining(", ")) + ")";
			constructorBuilder.addStatement(superCall);
			classBuilder.addMethod(constructorBuilder.build());
		}
	}

	private void generateMethods(Class<?> cls, TypeSpec.Builder classBuilder) throws ImplementorException {
		Set<Method> methods = collectAbstractMethods(cls);
		for (Method method : methods) {
			MethodSpec methodSpec = buildMethodSpec(method);
			classBuilder.addMethod(methodSpec);
		}
	}

	private Set<Method> collectAbstractMethods(Class<?> cls) {
		Set<Method> methods = new HashSet<>();
		Class<?> current = cls;

		while (current != null && current != Object.class) {
			for (Method method : current.getDeclaredMethods()) {
				if (Modifier.isAbstract(method.getModifiers())) {
					methods.add(method);
				}
			}

			for (Class<?> iface : current.getInterfaces()) {
				methods.addAll(collectInterfaceMethods(iface));
			}

			current = current.getSuperclass();
		}
		return methods;
	}

	private Set<Method> collectInterfaceMethods(Class<?> iface) {
        Set<Method> methods = new HashSet<>(Arrays.asList(iface.getMethods()));
		for (Class<?> superIface : iface.getInterfaces()) {
			methods.addAll(collectInterfaceMethods(superIface));
		}
		return methods;
	}

	private MethodSpec buildMethodSpec(Method method) throws ImplementorException {
		MethodSpec.Builder builder = MethodSpec.methodBuilder(method.getName())
				.addAnnotation(Override.class)
				.returns(TypeName.get(method.getReturnType()));

		int modifiers = method.getModifiers();

		if (Modifier.isPublic(modifiers)) {
			builder.addModifiers(javax.lang.model.element.Modifier.PUBLIC);
		} else if (Modifier.isProtected(modifiers)) {
			builder.addModifiers(javax.lang.model.element.Modifier.PROTECTED);
		} else {
			throw new ImplementorException("Method has non-public/protected access: " + method.getName());
		}

		for (Parameter parameter : method.getParameters()) {
			builder.addParameter(parameter.getType(), parameter.getName());
		}

		if (method.getReturnType() != void.class) {
			builder.addStatement("return $L", getDefaultValue(method.getReturnType()));
		}

		return builder.build();
	}

	private String getDefaultValue(Class<?> type) {
		if (type.isPrimitive()) {
			if (type == boolean.class) return "false";
			if (type == char.class) return "'\\0'";
			if (type == byte.class || type == short.class || type == int.class || type == long.class) return "0";
			if (type == float.class) return "0.0f";
			if (type == double.class) return "0.0d";
		}
		return "null";
	}
}
