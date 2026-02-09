package com.networknt.agent.data.document.loader;

import static com.networknt.agent.spi.ServiceHelper.loadFactories;

import com.networknt.agent.Internal;
import com.networknt.agent.data.document.DocumentParser;
import com.networknt.agent.spi.data.document.parser.DocumentParserFactory;

@Internal
class DocumentParserLoader {
    static DocumentParser loadDocumentParser() {
        var factories = loadFactories(DocumentParserFactory.class);

        if (factories.size() > 1) {
            throw new RuntimeException("Conflict: multiple document parsers have been found in the classpath. "
                    + "Please explicitly specify the one you wish to use.");
        }

        for (DocumentParserFactory factory : factories) {
            return factory.create();
        }

        return null;
    }
}
