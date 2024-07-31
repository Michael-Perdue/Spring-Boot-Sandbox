package example;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
public class DemoController {
    private final FileLoaderService fileLoaderService;

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return new ResponseEntity<>("Hello, World!",HttpStatus.OK);
    }

    @Autowired
    public DemoController(FileLoaderService fileLoaderService){
        this.fileLoaderService = fileLoaderService;
    }


    @GetMapping(path="/file/get")
    public ResponseEntity<?> getFile(@RequestParam(value="name",defaultValue ="GBP.csv") String fileName){
        try {
            ArrayList<HashMap<String, String>> values = fileLoaderService.getFile(fileName);
            if (values == null)
                return new ResponseEntity<>("No file found with the name: " + fileName, HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(values, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("An error occurred whilst reading the file ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/file/upload")
    public ResponseEntity<?> uploadFile(@RequestParam(value="file") MultipartFile file){
        Result upload = fileLoaderService.uploadFile(file);
        if(upload == Result.FILE_UPLOADED)
            return new ResponseEntity<>("File uploaded", HttpStatus.OK);
        if(upload == Result.FILE_FAILED_WRITING)
            return new ResponseEntity<>("File failed to upload while writing",HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>("File already exists with that name",HttpStatus.CONFLICT);
    }

    @GetMapping(path="/file/names")
    public ResponseEntity<HashMap<String,ArrayList<String>>> getFiles(){
        HashMap<String,ArrayList<String>> names = new HashMap<>();
        names.put("files",fileLoaderService.getFileNames());
        return new ResponseEntity<>(names, HttpStatus.OK);
    }
}