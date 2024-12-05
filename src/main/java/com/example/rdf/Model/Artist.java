package com.example.rdf.Model;

import lombok.Data;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.sparqlbuilder.core.SparqlBuilder;
import org.eclipse.rdf4j.sparqlbuilder.core.Variable;

@Data
public class Artist {

    private String lastName;
    private String firstName;

    public Artist(String firstName, String lastName) {
        this.lastName = lastName;
        this.firstName = firstName;
    }
}
