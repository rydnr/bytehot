package org.acmsl.javaeda.domain.invariants;

import org.acmsl.javaeda.domain.Invariant;
import com.google.auto.service.AutoService;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
  An invariant for the name of the Kubernetes pod.

  Class name: KubernetesPodNameInvariant.

  Responsibilities:
    - Provide the Pod name information as an invariant.

  Collaborators:
    - Google's auto-service to maintain the META-INF/services/org.acmsl.javaeda.domain.Invariant file.
*/
@SuppressWarnings({"unused","PMD.AtLeastOneConstructor","PMD.NullAssignment"})
@AutoService(Invariant.class)
public class KubernetesPodNameInvariant implements Invariant<String> {

    /**
     * Returns {@code "kubernetes.pod.name"}.
     * @return such literal.
     */
    @Override
    @NonNull
    public String getName() {
        return "kubernetes.pod.name";
    }

    /**
     * Returns the pod name.
     * @return such information.
     */
    @Override
    @NonNull
    public Optional<String> getValue() {
        // 1) Best: Downward API-provided POD_NAME
        String result = System.getenv("POD_NAME");
        if (result == null || result.isBlank()) {
            // 2) Often equals pod name in K8s (and on Docker): HOSTNAME
            result = System.getenv("HOSTNAME");
        }
        if (result == null || result.isBlank()) {
            try {
                result = InetAddress.getLocalHost().getHostName();
            } catch (@NonNull final UnknownHostException ignored) { }
        }
        if (result == null || result.isBlank()) {
            result = null;
        }

        return Optional.ofNullable(result);
    }
}
