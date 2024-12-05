package com.example.rdf.Model;

import lombok.Data;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleIRI;
import org.eclipse.rdf4j.sparqlbuilder.core.SparqlBuilder;
import org.eclipse.rdf4j.sparqlbuilder.core.Variable;

@Data
public class Painting {

    private String id;
    private String title;
    private String technique;
    private String artistId;

    public Painting() {}
    public Painting(String title, String technique, String artistId) {
        this.title = title;
        this.technique = technique;
        this.artistId = artistId;
    }
    public Painting(String title, String technique, String artistId, String id) {
        this.title = title;
        this.technique = technique;
        this.artistId = artistId;
        this.id = id;
    }

}
