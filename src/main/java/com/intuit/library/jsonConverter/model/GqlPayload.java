package com.intuit.library.jsonConverter.model;

public class GqlPayload {
    private GqlMetaData metaData;
    private GqlDocument document;

    public GqlPayload(GqlDocument document, GqlMetaData metaData) {
        this.document = document;

        this.metaData = metaData;
    }

    public GqlMetaData getMetaData() {
        return metaData;
    }

    /*public void setMetaData(GqlMetaData metaData) {
        this.metaData = metaData;
    }*/

    public GqlDocument getDocument() {
        return document;
    }

   /* public void setDocument(GqlDocument document) {
        this.document = document;
    }*/

    /*@Override
    public String toString() {
        return "GqlPayload{" +
                "metaData=" + metaData +
                ", document=" + document +
                '}';
    }*/
}
