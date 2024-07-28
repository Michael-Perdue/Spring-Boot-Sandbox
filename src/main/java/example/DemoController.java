package example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
public class DemoController {
    private final FileLoaderService fileLoaderService;

    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }

    @Autowired
    public DemoController(FileLoaderService fileLoaderService){
        this.fileLoaderService = fileLoaderService;
    }

    @GetMapping("/file")
    public ArrayList<HashMap<String,String>> getFile(){
        return fileLoaderService.getFile("GBP");
    }
}