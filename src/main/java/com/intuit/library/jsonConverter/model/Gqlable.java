package com.intuit.library.jsonConverter.model;

import com.intuit.library.jsonConverter.interfaces.Jsonable;

import java.util.List;

public interface Gqlable extends Jsonable {
    boolean hasChilds();

    FieldNameDescriptor getFieldNameDescriptor();

    List<Gqlable> getChildNodes();
}
