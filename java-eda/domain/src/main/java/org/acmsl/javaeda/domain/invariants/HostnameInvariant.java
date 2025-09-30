package org.acmsl.javaeda.domain.invariants;

import org.acmsl.javaeda.domain.Invariant;
import com.google.auto.service.AutoService;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
  The hostname invariant.

  Class name: HostnameInvariant

  Responsibilities:
    - Provide the hostname information as an invariant.

  Collaborators:
    - Google's auto-service to maintain the META-INF/services/org.acmsl.javaeda.domain.Invariant file.
*/
@SuppressWarnings({"unused","PMD.AtLeastOneConstructor","PMD.NullAssignment"})
@AutoService(Invariant.class)
public class HostnameInvariant implements Invariant<String> {

    /**
     * Returns "hostname".
     * @return such literal.
     */
    @Override
    @NonNull
    public String getName() {
        return "hostname";
    }

    /**
     * Returns the hostname.
     * @return such information.
     */
    @Override
    @NonNull
    public Optional<String> getValue() {
        String result;
        try {
            result = InetAddress.getLocalHost().getHostName();
        } catch (@NonNull final UnknownHostException e) {
            result = null;
        }
        return Optional.ofNullable(result);
    }
}
