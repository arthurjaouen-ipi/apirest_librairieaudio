package com.myAudioLibrairie.apirest.controller;

import com.myAudioLibrairie.apirest.model.Album;
import com.myAudioLibrairie.apirest.model.Artist;
import com.myAudioLibrairie.apirest.repository.AlbumRepository;
import com.myAudioLibrairie.apirest.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

@RestController
@RequestMapping(value = "/albums")
public class AlbumController {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @RequestMapping( method = RequestMethod.POST )
    public Album createAlbum(@RequestBody Album newAlbum){
        Album testAlbum = albumRepository.findByTitle(newAlbum.getTitle());
        if(testAlbum != null)
            throw new IllegalArgumentException("L'album existe deja.");
        Artist artist = artistRepository.findById(newAlbum.getArtist().getId());
        if(artist == null)
            throw new IllegalArgumentException("L'artiste n'existe pas.");
        return albumRepository.save(newAlbum);
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public Album deleteAlbum(@PathVariable(value = "id") Integer id){
        Album album = albumRepository.findOne(id);
        if(album == null)
            throw new EntityNotFoundException("L'album n'existe pas.");
        albumRepository.delete(album);
        return album;
    }
}