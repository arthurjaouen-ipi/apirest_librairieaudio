package com.myAudioLibrairie.apirest.controller;

import com.myAudioLibrairie.apirest.model.Album;
import com.myAudioLibrairie.apirest.model.Artist;
import com.myAudioLibrairie.apirest.repository.AlbumRepository;
import com.myAudioLibrairie.apirest.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

@RestController
@RequestMapping(value = "/artists")
public class ArtistController {

    @Autowired
    private ArtistRepository artistRepository;
    
    @Autowired
    private AlbumRepository albumRepository;

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{id}")
    public Artist findById(@PathVariable(value = "id") Integer id){
        Artist artist = artistRepository.findOne(id);
        if(artist == null)
            throw new EntityNotFoundException("L'artiste n'existe pas;");
        return artist;
    }

    @RequestMapping(params = {"name", "page", "size", "sortDirection", "sortProperty"})
    public Page<Artist> findByMatricule(@RequestParam("name") String name,
                                        @RequestParam("page") Integer page,
                                        @RequestParam("size") Integer size,
                                        @RequestParam("sortDirection") Sort.Direction ordre,
                                        @RequestParam("sortProperty") String propertie){
        PageRequest pageRequest = new PageRequest(page, size, ordre, propertie);
        return artistRepository.findByNameStartsWith(name, pageRequest);
    }

    @RequestMapping(params = {"page", "size", "sortDirection", "sortProperty"})
    public Page<Artist> listeArtist(@RequestParam("page") Integer page,
                                      @RequestParam("size") Integer size,
                                      @RequestParam("sortDirection") Sort.Direction ordre,
                                      @RequestParam("sortProperty") String propertie){
        PageRequest pageRequest = new PageRequest(page, size, ordre, propertie);
        return artistRepository.findAll(pageRequest);
    }

    @RequestMapping( method = RequestMethod.POST )
    public Artist createArtiste(@RequestBody Artist newArtist){
        Artist artist = artistRepository.findByName(newArtist.getName());
        if(artist != null)
            throw new IllegalArgumentException("L'artiste existe deja.");
        return artistRepository.save(newArtist);
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void modifArtiste(@RequestBody Artist newArtist){
        Artist artist = artistRepository.findById(newArtist.getId());
        if(artist == null)
            throw new EntityNotFoundException("L'artiste n'existe pas.");
        artist.setName(newArtist.getName());
        artistRepository.save(artist);
    }


    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public Artist deleteArtiste(@PathVariable(value = "id") Integer id){
        Artist artist = artistRepository.findOne(id);
        if(artist == null)
            throw new EntityNotFoundException("L'artiste n'existe pas.");
        for (int i=0; i < artist.getAlbums().size(); i++)
        {
            Album album = albumRepository.findById(artist.getAlbums().get(i).getId());
            albumRepository.delete(album);
        }
        artistRepository.delete(artist);
        return artist;
    }
}
