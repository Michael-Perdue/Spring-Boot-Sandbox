package example;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    @GetMapping(path="/file")
    public ResponseEntity<?> getFile(@RequestParam(value="name",defaultValue ="GBP.csv") String fileName){
        ArrayList<HashMap<String,String>> values = fileLoaderService.getFile(fileName);
        if(values == null)
            return new ResponseEntity<>("File corrupted or no file found with the name: " + fileName,HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(fileLoaderService.getFile(fileName), HttpStatus.OK);
    }
}