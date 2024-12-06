package com.example.rdf.Service;

import com.example.rdf.Model.EX;
import com.example.rdf.Model.Painting;
import lombok.Data;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Data
public class PaintingService {

    public Painting addPainting(Painting painting) {
        RemoteRepositoryManager server = RemoteRepositoryManager.getInstance("http://localhost:8080/rdf4j-server/");
        try (RepositoryConnection connection = server.getRepository("Test").getConnection()) {
            connection.begin();
            try {
                ModelBuilder builder = new ModelBuilder();
                BNode creator = Values.bnode();
                Model model = builder.setNamespace("ex", "http://example.org/")
                        .subject("ex:" + painting.getArtistId())
                        .add( "ex:creator", creator)
                        .subject(creator)
                        .add("ex:title", painting.getTitle())
                        .add("ex:technique", painting.getTechnique())
                        .build();
                connection.add(model);
                connection.commit();
                return painting;
            } catch (RepositoryException e) {
                e.printStackTrace();
                connection.rollback();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Painting> getPaintingsFromArtist(String artistId) {
        RemoteRepositoryManager server = RemoteRepositoryManager.getInstance("http://localhost:8080/rdf4j-server/");
        try (RepositoryConnection connection = server.getRepository("Test").getConnection()) {
            try {
                String queryString = "PREFIX ex: <http://example.org/> \n"
                        + "SELECT ?title ?technique \n"
                        + "WHERE {\n"
                        + "ex:" + artistId + " ex:creator ?id . \n"
                        + "?id ex:title ?title . \n"
                        + "?id ex:technique ?technique . \n"
                        + "}";

                TupleQuery query = connection.prepareTupleQuery(queryString);
                try (TupleQueryResult result = query.evaluate()) {
                    List<Painting> paintings = new ArrayList<>();
                    for (BindingSet solution : result) {
                        paintings.add(new Painting(
                                solution.getValue("title").toString().replace("\"", ""),
                                solution.getValue("technique").toString().replace("\"", ""),
                                artistId));
                    }
                    return paintings;
                }
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<Painting> getAllPaintings() {
        RemoteRepositoryManager server = RemoteRepositoryManager.getInstance("http://localhost:8080/rdf4j-server/");
        try (RepositoryConnection connection = server.getRepository("Test").getConnection()) {
            try {
                String queryString = "PREFIX ex: <http://example.org/> \n"
                        + "SELECT ?title ?technique ?artist \n"
                        + "WHERE {\n"
                        + "?artist ex:creator ?id . \n"
                        + "?id ex:title ?title . \n"
                        + "?id ex:technique ?technique . \n"
                        + "}";

                TupleQuery query = connection.prepareTupleQuery(queryString);
                try (TupleQueryResult result = query.evaluate()) {
                    List<Painting> paintings = new ArrayList<>();
                    for (BindingSet solution : result) {
                        paintings.add(new Painting(
                                solution.getValue("title").toString().replace("\"", ""),
                                solution.getValue("technique").toString().replace("\"", ""),
                                solution.getValue("artist").toString().replace("\"", "")));
                    }
                    return paintings;
                }
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Painting getPaintingByTitle(String title) {
        RemoteRepositoryManager server = RemoteRepositoryManager.getInstance("http://localhost:8080/rdf4j-server/");
        try (RepositoryConnection connection = server.getRepository("Test").getConnection()) {
            try {
                String queryString = "PREFIX ex: <http://example.org/> \n"
                        + "SELECT ?technique ?artist ?id \n"
                        + "WHERE {\n"
                        + "?artist ex:creator ?id . \n"
                        + "?id ex:title \"" + title + "\" . \n"
                        + "?id ex:technique ?technique . \n"
                        + "}";

                TupleQuery query = connection.prepareTupleQuery(queryString);
                try (TupleQueryResult result = query.evaluate()) {
                    for (BindingSet solution : result) {
                        return new Painting(
                                title,
                                solution.getValue("technique").toString().replace("\"", ""),
                                solution.getValue("artist").toString(),
                                solution.getValue("id").toString());
                    }
                }
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Painting updatePainting(Painting newPainting) {
        RemoteRepositoryManager server = RemoteRepositoryManager.getInstance("http://localhost:8080/rdf4j-server/");
        try (RepositoryConnection connection = server.getRepository("Test").getConnection()) {
            Painting painting = getPaintingByTitle(newPainting.getTitle());
            painting.setArtistId(painting.getArtistId().split("/")[3]);
            connection.begin();
            try {
                String updateString = "PREFIX ex: <http://example.org/>\n"
                        + "DELETE {\n"
                        + "ex:" + painting.getArtistId() + " ex:creator ?id.\n"
                        + "?id ex:technique \"" + painting.getTechnique() + "\".\n"
                        + "}\n"
                        + "INSERT {\n"
                        + "ex:" + newPainting.getArtistId() + " ex:creator ?id.\n"
                        + "?id ex:technique \"" + newPainting.getTechnique() + "\".\n"
                        + "}\n"
                        + "WHERE {\n"
                        + "ex:" + painting.getArtistId() + " ex:creator ?id.\n"
                        + "?id ex:title \"" + painting.getTitle() + "\".\n"
                        + "?id ex:technique \"" + painting.getTechnique() + "\".\n"
                        + "}";
                Update update = connection.prepareUpdate(updateString);
                update.execute();
                connection.commit();
                return painting;
            } catch (RepositoryException e) {
                connection.rollback();
                e.printStackTrace();
            }
        }
        return null;
    }

    public ResponseEntity<String> deletePainting(String paintingId) {
        Painting painting = getPaintingByTitle(paintingId);
        RemoteRepositoryManager server = RemoteRepositoryManager.getInstance("http://localhost:8080/rdf4j-server/");
        try (RepositoryConnection connection = server.getRepository("Test").getConnection()) {
            connection.begin();
            try {
                RepositoryResult<Statement> statementsFromArtist = connection.getStatements(Values.iri(painting.getArtistId()), EX.creator, null);
                for (Statement statementFromArtist: statementsFromArtist) {
                    if (statementFromArtist.getObject().equals(connection.getStatements(null, EX.title, Values.literal(paintingId)).next().getSubject())) {
                        List<Statement> allStatements = connection.getStatements(null, null, null).stream().toList();
                        for (Statement statement: allStatements) {
                            if (statement.getObject().equals(statementFromArtist.getObject()) || statement.getSubject().equals(statementFromArtist.getObject())) {
                                connection.remove(statement);
                            }
                        }
                        connection.commit();
                        return ResponseEntity.ok("Painting " + paintingId + " successfully deleted");
                    }
                }
                connection.rollback();
            } catch (RepositoryException e) {
                connection.rollback();
                e.printStackTrace();
            }
        }
        return null;
    }

}
