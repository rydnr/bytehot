package org.acmsl.javaeda.domain;

import org.acmsl.javaeda.annotations.FeatureToggle;
import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
  An aspect to route which method to use on a feature flag-annotated method.

  Class name: FeatureToggleAspect

  Responsibilities:
    - Route to the old method or to the new one, depending on the value of a feature flag.

  Collaborators:
    - FeatureFlag: To check if the feature flag is enabled or not.
 */
@SuppressWarnings({"PMD.LawOfDemeter"})
@Aspect
public class FeatureToggleAspect {

    /**
     * The feature flag.
     */
    private final FeatureFlag featureFlag;

    /**
     * Creates a new instance.
     */
    public FeatureToggleAspect() {
        this.featureFlag = Ports.getInstance().resolveFirst(FeatureFlag.class);
    }

    /**
     * Decides whether to use the new or the old implementation of a feature-flag annotated method.
     * @param pjp the proceeding join point.
     * @param toggle the feature toggle.
     * @return the outcome of the method finally used.
     * @throws Throwable if the method throws it.
     */
    @Around(value = "@annotation(toggle)", argNames = "pjp,toggle")
    public Object aroundToggle(@NonNull final ProceedingJoinPoint pjp, @NonNull final FeatureToggle toggle) throws Throwable {
        final Object result;
        final Object target = pjp.getTarget();
        final Object[] args = pjp.getArgs();
        final MethodSignature sig = (MethodSignature) pjp.getSignature();

        if (this.featureFlag.isEnabled(toggle.feature())) {
            final Method newMethod = target.getClass().getMethod(toggle.replaceWith(), sig.getParameterTypes());
            if (target instanceof BaseObject && ((BaseObject) target).getLogger().isInfoEnabled()) {
                ((BaseObject) target).getLogger().info(toggle.feature() + " is enabled, redirecting to " + toggle.replaceWith());
            }
            result = newMethod.invoke(target, args);
        } else {
            if (target instanceof BaseObject targetObj && targetObj.getLogger().isInfoEnabled()) {
                targetObj.getLogger().info(toggle.feature() + " is disabled, not redirecting to " + toggle.replaceWith());
            }
            result = pjp.proceed();
        }
        return result;
    }
}
