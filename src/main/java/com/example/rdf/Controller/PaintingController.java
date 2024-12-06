package com.example.rdf.Controller;

import com.example.rdf.Model.Painting;
import com.example.rdf.Service.PaintingService;
import lombok.Data;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Data
@RequestMapping("painting")
public class PaintingController {

    private PaintingService paintingService;

    public PaintingController(PaintingService paintingService) {
        this.paintingService = paintingService;
    }

    @GetMapping("all")
    public List<Painting> getAllPaintings() {
        return paintingService.getAllPaintings();
    }

    @GetMapping("getFromArtist")
    public ResponseEntity<List<Painting>> getPaintingsFromArtist(@RequestParam("artistId") String artistId) {
        return paintingService.getPaintingsFromArtist(artistId);
    }

    /**
     * @param painting :
     *               {
     *                  "title": "string",
     *                  "technique": "string",
     *                  "artistId": "name"
     *               }
     */
    @PostMapping("add")
    public ResponseEntity<Painting> addPainting(@RequestBody Painting painting) {
        return paintingService.addPainting(painting);
    }

    @PutMapping("update")
    public ResponseEntity<Painting> updatePainting(@RequestBody Painting painting) {
        if (paintingService.deletePainting(painting.getTitle()) != null) {
            return paintingService.addPainting(painting);
        }
        return null;
    }

    @PutMapping("updateSPARQL")
    public ResponseEntity<Painting> updateSPARQL(@RequestBody Painting painting) {
        return paintingService.updatePainting(painting);
    }

    @DeleteMapping("delete")
    public ResponseEntity<String> deletePainting(@RequestParam("paintingId") String paintingId) {
        return paintingService.deletePainting(paintingId);
    }
}
