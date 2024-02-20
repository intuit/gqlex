package com.intuit.library.jsonConverter.interfaces;

public interface Jsonable {
    Object generateAsJsonObject();

    Object generateAsJsonObject(Object parentJsonObject);
}
