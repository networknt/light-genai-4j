package com.networknt.agent.service.output;

import com.networknt.agent.Internal;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

@Internal
class StringSetOutputParser extends StringCollectionOutputParser<Set<String>> {

    @Override
    Supplier<Set<String>> emptyCollectionSupplier() {
        return LinkedHashSet::new;
    }

    @Override
    Class<?> collectionType() {
        return Set.class;
    }
}
