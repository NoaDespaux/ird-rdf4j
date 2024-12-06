package com.example.rdf.Service;

import com.example.rdf.Model.Artist;
import com.example.rdf.Model.ServerInfo;
import lombok.Data;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Data
public class ArtistService {

    public ServerInfo getServerInfo() {
        RemoteRepositoryManager server = RemoteRepositoryManager.getInstance("http://localhost:8080/rdf4j-server/");
        try (RepositoryConnection connection = server.getRepository("Test").getConnection()) {
            ServerInfo serverInfo = new ServerInfo();
            serverInfo.setNamespaces(connection.getNamespaces().stream().toList());
            serverInfo.setStatements(connection.getStatements(null, null, null).stream().toList());
            return serverInfo;
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Artist> getAllArtists() {
        RemoteRepositoryManager server = RemoteRepositoryManager.getInstance("http://localhost:8080/rdf4j-server/");
        try (RepositoryConnection connection = server.getRepository("Test").getConnection()) {
            try {
                String queryString = "PREFIX ex: <http://example.org/> \n"
                        + "PREFIX foaf: <" + FOAF.NAMESPACE + "> \n"
                        + "SELECT ?firstName ?lastName \n"
                        + "WHERE { \n"
                        + "?lastName a ex:Artist; \n"
                        + "foaf:firstName ?firstName \n ."
                        + "}";

                TupleQuery query = connection.prepareTupleQuery(queryString);
                try (TupleQueryResult result = query.evaluate()) {
                    List<Artist> artists = new ArrayList<>();
                    for (BindingSet solution : result) {
                        artists.add(new Artist(
                                solution.getValue("firstName").toString().replace("\"", ""),
                                solution.getValue("lastName").toString()));
                    }
                    return artists;
                }
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Artist getArtistById(String id) {
        RemoteRepositoryManager server = RemoteRepositoryManager.getInstance("http://localhost:8080/rdf4j-server/");
        try (RepositoryConnection connection = server.getRepository("Test").getConnection()) {
            try {
                String queryString = "PREFIX ex: <http://example.org/> \n"
                        + "PREFIX foaf: <" + FOAF.NAMESPACE + "> \n"
                        + "SELECT ?firstName ?lastName \n"
                        + "WHERE { \n"
                        + "ex:" + id + " a ex:Artist; \n"
                        + "foaf:firstName ?firstName \n ."
                        + "}";

                TupleQuery query = connection.prepareTupleQuery(queryString);
                try (TupleQueryResult result = query.evaluate()) {
                    for (BindingSet solution : result) {
                        return new Artist(solution.getValue("firstName").toString().replace("\"", ""), id);
                    }
                }
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Artist addArtist(Artist artist) {
        RemoteRepositoryManager server = RemoteRepositoryManager.getInstance("http://localhost:8080/rdf4j-server/");
        try (RepositoryConnection connection = server.getRepository("Test").getConnection()) {
            connection.begin();
            try {
                ModelBuilder builder = new ModelBuilder();
                Model model = builder.setNamespace("ex", "http://example.org/")
                        .subject("ex:" + artist.getLastName())
                        .add(RDF.TYPE, "ex:Artist")
                        .add(FOAF.FIRST_NAME, artist.getFirstName())
                        .build();
                connection.add(model);
                connection.commit();
                return artist;
            } catch (RepositoryException e) {
                e.printStackTrace();
                connection.rollback();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Artist updateArtist(Artist artist) {
        RemoteRepositoryManager server = RemoteRepositoryManager.getInstance("http://localhost:8080/rdf4j-server/");
        try (RepositoryConnection connection = server.getRepository("Test").getConnection()) {
            connection.begin();
            try {
                String updateString = "PREFIX ex: <http://example.org/>\n"
                        + "PREFIX foaf: <" + FOAF.NAMESPACE + ">\n"
                        + "DELETE {\n"
                        + "ex:" + artist.getLastName() + " foaf:firstName ?firstName.\n"
                        + "}\n"
                        + "INSERT {\n"
                        + "ex:" + artist.getLastName() + " foaf:firstName \"" + artist.getFirstName() + "\".\n"
                        + "}\n"
                        + "WHERE {\n"
                        + "ex:" + artist.getLastName() + " foaf:firstName ?firstName.\n"
                        + "}";
                Update update = connection.prepareUpdate(updateString);
                update.execute();
                connection.commit();
                return artist;
            } catch (RepositoryException e) {
                connection.rollback();
                e.printStackTrace();
            }
        }
        return null;
    }

    public ResponseEntity<String> deleteArtist(String artistId) {
        Artist artist = getArtistById(artistId);
        RemoteRepositoryManager server = RemoteRepositoryManager.getInstance("http://localhost:8080/rdf4j-server/");
        try (RepositoryConnection connection = server.getRepository("Test").getConnection()) {
            connection.begin();
            try {
                String updateString = "PREFIX ex: <http://example.org/> \n"
                        + "PREFIX foaf: <" + FOAF.NAMESPACE + "> \n"
                        + "DELETE DATA { \n"
                        + "ex:" + artist.getLastName() + " a ex:Artist; \n"
                        + "foaf:firstName \"" + artist.getFirstName() + "\". \n"
                        + "}";

                Update update = connection.prepareUpdate(updateString);
                update.execute();
                connection.commit();
                return ResponseEntity.ok("Artist " + artistId + " successfully deleted");
            } catch (RepositoryException e) {
                connection.rollback();
                e.printStackTrace();
            }
        }
        return null;
    }

}
