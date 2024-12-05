package com.example.rdf.Model;

import lombok.Data;
import org.eclipse.rdf4j.model.Statement;
import org. eclipse. rdf4j. model. Namespace;
import java.util.List;

@Data
public class ServerInfo {
    private List<Namespace> namespaces;
    private List<Statement> statements;
}
