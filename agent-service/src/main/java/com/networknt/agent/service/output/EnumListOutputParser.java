package com.networknt.agent.service.output;

import com.networknt.agent.Internal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Internal
class EnumListOutputParser<E extends Enum<E>> extends EnumCollectionOutputParser<E, List<E>> {

    EnumListOutputParser(Class<E> enumClass) {
        super(enumClass);
    }

    @Override
    Supplier<List<E>> emptyCollectionSupplier() {
        return ArrayList::new;
    }

    @Override
    Class<?> collectionType() {
        return List.class;
    }
}
