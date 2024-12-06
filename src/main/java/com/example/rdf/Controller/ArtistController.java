package com.example.rdf.Controller;

import com.example.rdf.Model.Artist;
import com.example.rdf.Model.ServerInfo;
import com.example.rdf.Service.ArtistService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Data
@RequestMapping("artist")
public class ArtistController {

    private ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping("getServerInfo")
    public ServerInfo getServerInfo() {
        return artistService.getServerInfo();
    }

    @GetMapping("all")
    public List<Artist> getAllArtists() {
        return artistService.getAllArtists();
    }

    /**
     * @param artist :
     *               {
     *                  "firstName": "string",
     *                  "lastName": "string"
     *               }
     */
    @PostMapping("add")
    public Artist addArtist(@RequestBody Artist artist) {
        return artistService.addArtist(artist);
    }

    @PutMapping("update")
    public Artist updateArtist(@RequestBody Artist artist) {
        if (artistService.deleteArtist(artist.getLastName()) != null) {
            return artistService.addArtist(artist);
        }
        return null;
    }

    @PutMapping("updateSPARQL")
    public Artist updateArtistSPARQL(@RequestBody Artist artist) {
        return artistService.updateArtist(artist);
    }

    @DeleteMapping("delete")
    public ResponseEntity<String> deleteArtist(@RequestParam("artistId") String artistId) {
        return artistService.deleteArtist(artistId);
    }

}
