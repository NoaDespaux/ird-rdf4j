package com.example.rdf.Model;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.impl.SimpleNamespace;
import org.eclipse.rdf4j.model.util.Values;

import static org.eclipse.rdf4j.model.util.Values.iri;

public class EX {
    private static final Namespace base = new SimpleNamespace("ex", "http://example.org/");
    public static final IRI Artist = Values.iri(base, "Artist");
    public static final IRI city = Values.iri(base,"city");
    public static final IRI country = Values.iri(base, "country");
    public static final IRI creator = Values.iri(base,"creator");
    public static final IRI title = Values.iri(base,"title");
    public static final IRI technique = Values.iri(base,"technique");
    public static final IRI Gallery = Values.iri(base,"Gallery");
    public static final IRI guernica = Values.iri(base,"guernica");
    public static final IRI Painting = Values.iri(base,"Painting");
    public static final IRI Picasso = Values.iri(base,"Picasso");
    public static final IRI potatoEaters = Values.iri(base,"potatoEaters");
    public static final IRI Rembrandt = Values.iri(base,"Rembrandt");
    public static final IRI starryNight = Values.iri(base,"starryNight");
    public static final IRI street = Values.iri(base,"street");
    public static final IRI sunflowers = Values.iri(base,"sunflowers");
    public static final IRI VanGogh = Values.iri(base,"VanGogh");

    public EX() {}

    public static IRI of(String localName) {
        return Values.iri(base, localName);
    }
}
