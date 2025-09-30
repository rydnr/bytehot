package org.acmsl.javaeda.domain;

import java.util.Map;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
   Defines the primary key of an entity.

   Class name: PrimaryKey

   Responsibilities:
   - Define the values identifying an entity uniquely.

   Collaborators:
   - None
*/
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface PrimaryKey {
    /**
     * Retrieves the map representing the primary key.
     * @return such map.
     */
    @NonNull
    Map<String, Object> getAttributes();

    /**
       Inner class to encapsulate equality logic.

       Class name: PrimaryKey.PrimaryKeyEquals

       Responsibilities:
       - Provide a reliable way to check if two primary keys are equal.

       Collaborators:
       - java.util.Map
       - java.util.Objects
    */
    @SuppressWarnings("unused")
    class PrimaryKeyEquals {
        /**
         * The actual PrimaryKey instance.
         */
        private final PrimaryKey self;

        /**
         * The other object to check.
         */
        private final Object other;

        /**
         * Creates a new instance to compare the primary key and other object.
         * @param self the {@code PrimaryKey}.
         * @param other the other object to check.
         */
        public PrimaryKeyEquals(@NonNull final PrimaryKey self, @Nullable final Object other) {
            this.self = self;
            this.other = other;
        }

        /**
         * Performs the comparison.
         * @return {@code true} if both objects represent the same primary key; {@code false} otherwise.
         */
        @SuppressWarnings("PMD.CompareObjectsWithEquals")
        public boolean value() {
            final boolean result;
            if (this.self == this.other) {
                result = true;
            } else if (this.other == null || !(this.other instanceof PrimaryKey otherPk)) {
                result = false;
            } else {
                result = Objects.equals(this.self.getAttributes(), otherPk.getAttributes());
            }
            return result;
        }
    }

    /**
       Inner class to encapsulate hash code logic.

       Class name: PrimaryKey.PrimaryKeyHashCode

       Responsibilities:
       - Provide a reliable way to calculate the hashCode of a primary key.

       Collaborators:
       - java.util.Objects
    */
    @SuppressWarnings("unused")
    class PrimaryKeyHashCode {
        /**
         * The primary key.
         */
        private final PrimaryKey self;

        /**
         * Creates a new instance.
         * @param self the {@code PrimaryKey}.
         */
        public PrimaryKeyHashCode(@NonNull final PrimaryKey self) {
            this.self = self;
        }

        /**
         * Default hash code computation based on the attributes and the entity class.
         * Implementors should avoid overriding hashCode unless they delegate here.
         * @return the hash code.
         */
        public int value() {
            return Objects.hash(this.self.getAttributes());
        }
    }
}
