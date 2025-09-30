package org.acmsl.javaeda.annotations;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
  Represents each line in a index file

  Class name: EventHandlerDescriptor

  Responsibilities:
    - Represent the format of each line a index file.

  Collaborators:
    - com.dmixcloud.eda.application.EDAApplication
 */
@SuppressWarnings({"unused","PMD.AvoidFieldNameMatchingMethodName"})
public class EventHandlerDescriptor {

    /**
     * The name of the class.
     */
    private final String className;

    /**
     * The name of the method.
     */
    private final String methodName;

    /**
     * The type of the event.
     */
    private final String eventType;

    /**
     * Whetther the method is static.
     */
    private final boolean isStatic;

    /**
     * Creates a new instance.
     * @param className the name of the class.
     * @param methodName the name of the method.
     * @param eventType the type of the event.
     * @param isStatic whether the method is static.
     */
    public EventHandlerDescriptor(
        @NonNull final String className,
        @NonNull final String methodName,
        @NonNull final String eventType,
        final boolean isStatic) {
        this.className = className;
        this.methodName = methodName;
        this.eventType = eventType;
        this.isStatic = isStatic;
    }

    /**
     * Retrieves the name of the class.
     * @return such information.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Retrieves the name of the method.
     * @return such information.
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Retrieves the type of the event.
     * @return such information.
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * Retrieves whether the method is static.
     * @return {@code true} in such case.
     */
    public boolean isStatic() {
        return isStatic;
    }

    /**
     * Parses given line into a {@code EventHandlerDescriptor}.
     * @param line the line to parse.
     * @return a {@code EventHandlerDescriptor} if the line could be parsed.
     * @throws IllegalArgumentException if the line is not parseable as a {@code EventHandlerDescriptor}.
     */
    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public static EventHandlerDescriptor parse(final String line) {
        final String[] parts = line.split(":");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid event handler descriptor: " + line);
        }
        return new EventHandlerDescriptor(parts[0], parts[1], parts[2], "static".equals(parts[3]));
    }
}
