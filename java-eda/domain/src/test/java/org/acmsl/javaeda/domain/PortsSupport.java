package org.acmsl.javaeda.domain;

/**
 A simple class to be able to access package-protected methods on the {@link Ports} class.

  Class name: PortsSupport

  Responsibilities:
    - Access package-protected methods on Ports.

  Collaborators:
    - org.acmsl.javaeda.domain.Ports
*/
public class PortsSupport {
    /**
     * Defensive approach against the double-locking check.
     */
    protected static final class PortsSupportSingletonContainer {
        /**
         *  The actual singleton.
         */
        private static final PortsSupport SINGLETON = new PortsSupport();
    }

    /**
     * Resets {@link Ports} to use the default singleton.
     */
    public void restoreDefaultProvider() {
        Ports.setProvider(null);
    }
}
