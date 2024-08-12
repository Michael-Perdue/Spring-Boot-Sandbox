package example;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
public class DemoController {
    private final FileLoaderService fileLoaderService;

    @GetMapping(Route.HELLO)
    public ResponseEntity<String> hello() {
        return new ResponseEntity<>("Hello, World!",HttpStatus.OK);
    }

    @Autowired
    public DemoController(FileLoaderService fileLoaderService){
        this.fileLoaderService = fileLoaderService;
    }

    private AuthLevel getUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        GrantedAuthority authority = authentication.getAuthorities().iterator().next();
        return AuthLevel.fromString(authority.getAuthority());
    }

    @GetMapping(Route.GET_FILE)
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public ResponseEntity<?> getFile(@RequestParam(value="name",defaultValue ="GBP.csv") String fileName){
        try {
            SecurityContextHolder.getContext().getAuthentication().getName();
            ArrayList<HashMap<String, String>> values = fileLoaderService.getFile(fileName,getUserRole());
            return new ResponseEntity<>(values, HttpStatus.OK);
        }catch (ResponseStatusException e){
            if(e.getStatusCode() == HttpStatus.FORBIDDEN)
                return new ResponseEntity<>("Forbidden: You do not have access to this resource", e.getStatusCode());
            if (e.getStatusCode() == HttpStatus.NOT_FOUND)
                return new ResponseEntity<>("No file found with the name: " + fileName, HttpStatus.NOT_FOUND);
            return new ResponseEntity<>("An error occurred whilst reading the file ", e.getStatusCode());
        }
    }

    @PutMapping(Route.UPLOAD_FILE)
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> uploadFile(@RequestParam(value="file") MultipartFile file){
        Result upload = fileLoaderService.uploadFile(file,getUserRole());
        if(upload == Result.FILE_UPLOADED)
            return new ResponseEntity<>("File uploaded", HttpStatus.OK);
        if(upload == Result.FILE_FAILED_WRITING)
            return new ResponseEntity<>("File failed to upload while writing",HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>("File already exists with that name",HttpStatus.CONFLICT);
    }

    @GetMapping(Route.GET_FILE_NAMES)
    @Secured({"ROLE_USER","ROLE_ADMIN"})
    public ResponseEntity<HashMap<String,ArrayList<String>>> getFiles(){
        HashMap<String,ArrayList<String>> names = new HashMap<>();
        names.put("files",fileLoaderService.getFileNames(getUserRole()));
        return new ResponseEntity<>(names, HttpStatus.OK);
    }

    @GetMapping(Route.CHECK_ADMIN)
    @Secured("ROLE_ADMIN")
    public ResponseEntity<String> checkAdmin(){
        return new ResponseEntity<>("You are an admin", HttpStatus.OK);
    }
}