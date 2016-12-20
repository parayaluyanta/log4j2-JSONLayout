package org.apache.logging.log4j.core.jackson;

import org.apache.logging.log4j.core.LogEvent;

/**
 * Cribbed from core Log4jJsonModule this merely to override the LogEvent mixin with CustomLogEventMixIn
 *
 * Created by jeremyfranklin-ross on 7/28/15.
 */
public class CustomLog4jJsonModule extends Log4jJsonModule {

    private static final long serialVersionUID = 1L;

    CustomLog4jJsonModule() {
        super();
    }

    @Override
    public void setupModule(final SetupContext context) {
        super.setupModule(context);

        context.setMixInAnnotations(LogEvent.class, CustomLogEventMixIn.class);
    }
}
