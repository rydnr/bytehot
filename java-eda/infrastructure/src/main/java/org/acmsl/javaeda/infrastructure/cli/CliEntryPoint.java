package org.acmsl.javaeda.infrastructure.cli;

import org.acmsl.javaeda.domain.Application;
import org.acmsl.javaeda.domain.BaseObject;
import org.acmsl.javaeda.domain.PrimaryPort;
import org.acmsl.javaeda.infrastructure.DomainContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.spring.PicocliSpringFactory;

/**
  Entry point for CLI parsing.

  Class name: CliEntryPoint

  Responsibilities:
    - Manage the parsing of the CLI arguments.

  Collaborators:
    - Picocli
 */
@SuppressWarnings({"PMD.LawOfDemeter","PMD.UseArraysAsList","PMD.InefficientEmptyStringCheck","PMD.LongVariable","PMD.SystemPrintln"})
@Component
public class CliEntryPoint extends BaseObject implements PrimaryPort {

    /**
     * The application context.
     */
    private final ApplicationContext applicationContext;

    /**
     * The domain context.
     */
    private final DomainContext domainContext;

    /**
     * Creates a new CliEntryPoint instance.
     * @param applicationContext the {@code ApplicationContext}.
     */
    @Autowired
    public CliEntryPoint(@NonNull final ApplicationContext applicationContext, @NonNull final DomainContext domainContext) {
        super();
        this.applicationContext = applicationContext;
        this.domainContext = domainContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {}

    /**
     * Whether this port is enabled or not.
     * @return {@code true} in that case.
     */
    @Override
    public boolean enabled() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(@NonNull final Application app) {
        this.domainContext.setApplication(app);
        final PicocliSpringFactory factory = new PicocliSpringFactory(this.applicationContext);
        final Object rootCommand = this.applicationContext.getBean(CliRootCommand.class);
        final CommandLine root = new CommandLine(rootCommand, factory);

        // Discover subcommands
        final Map<String, Object> discovered = this.applicationContext.getBeansWithAnnotation(CommandLine.Command.class);
        discovered.values().stream()
            .filter(b -> !root.getCommand().equals(b))
            .forEach(bean -> {
                final CommandLine.Command meta = AnnotationUtils.findAnnotation(bean.getClass(), CommandLine.Command.class);
                if (meta != null && !meta.name().trim().isEmpty()) {
                    root.addSubcommand(meta.name(), bean);
                } else {
                    root.addMixin(bean.getClass().getSimpleName(), bean);
                }
            });

        // Manually parse args and run after full domain bootstrap
        final String[] args = this.applicationContext.getBean(ApplicationArguments.class).getSourceArgs();

        // Step 1: extract `-e <event>`
        final String event = extractEvent(args);
        if (event == null || event.isBlank()) {
            if (getLogger("cli").isErrorEnabled()) {
                getLogger("cli").error("❌ You must provide an event using -e <event>");
            } else {
                System.err.println("❌ You must provide an event using -e <event>");
            }

            //System.exit(1);
        }

        // Step 2: Rewrite args
        final List<String> rewritten = rewriteArgs(event, args);

        // Step 3: Build and run the real CLI
        root.execute(rewritten.toArray(String[]::new));
        //final int exitCode = root.execute(rewritten.toArray(String[]::new));
        //System.exit(exitCode);
    }

    /**
     * Extracts the event from given args.
     * @param args the command-line arguments.
     * @return the event, if found.
     */
    protected String extractEvent(@NonNull final String... args) {
        String result = null;
        for (int i = 0; i < args.length - 1; i++) {
            if ("-e".equals(args[i]) || "--event".equals(args[i])) {
                result = args[i + 1];
                break;
            }
        }
        return result;
    }

    /**
     * Rewrites the args to remove the event argument.
     * @param args the command-line arguments.
     * @param event the event.
     * @return the args without the event.
     */
    @NonNull
    protected List<String> rewriteArgs(@NonNull final String event, @NonNull final String... args) {
        final List<String> result = new ArrayList<>();
        boolean skipNext = false;
        for (final String arg : args) {
            if (skipNext) {
                skipNext = false;
                continue;
            }
            if ("-e".equals(arg) || "--event".equals(arg)) {
                skipNext = true;
                continue;
            }
            result.add(arg);
        }
        result.addFirst(event); // inject subcommand
        return result;
    }
}
