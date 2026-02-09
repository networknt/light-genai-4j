package com.networknt.agent.service.output;

import com.networknt.agent.Internal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Internal
class StringListOutputParser extends StringCollectionOutputParser<List<String>> {

    @Override
    Supplier<List<String>> emptyCollectionSupplier() {
        return ArrayList::new;
    }

    @Override
    Class<?> collectionType() {
        return List.class;
    }
}
