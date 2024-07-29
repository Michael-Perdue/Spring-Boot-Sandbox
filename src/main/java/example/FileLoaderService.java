package example;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
public class FileLoaderService {
    private static ArrayList<File> files = new ArrayList<>();

    @PostConstruct
    private void init(){
        loadFiles();
    }

    public ArrayList<HashMap<String,String>> getFile(String name) throws Exception {
        name = name.replaceAll("\\.csv","");
        for (File file : files) {
            String foundFile = file.getName().replaceAll("\\.csv","");
            if (foundFile.equals(name)) {
                return getFileContent(file);
            }
        }
        return null;
    }

    private ArrayList<HashMap<String,String>> getFileContent(File file) throws Exception {
        ArrayList<HashMap<String,String>> entries = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(file);
            String[] headers = scanner.nextLine().split(",");
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split(",");
                HashMap<String,String> values = new HashMap<>();
                for (int x =0;x<line.length;x++) {
                    values.put(headers[x],line[x]);
                }
                entries.add(values);
            }
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
        return entries;
    }

    public Result uploadFile(MultipartFile file){
        try {
            if(!fileExists(file.getOriginalFilename())){
                Files.write(Path.of("src/main/resources/data/" + file.getOriginalFilename()), file.getBytes());
                loadFiles();
                return Result.FILE_UPLOADED;
            }
            return Result.FILE_ALREADY_EXISTS;
        }catch (Exception e){
            e.printStackTrace();
            return Result.FILE_FAILED_WRITING;
        }
    }

    private boolean fileExists(String fileName){
        for (File file : files) {
            if (file.getName().equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    private void loadFiles(){
        try {
            File[] fileList = new File("src/main/resources/data").listFiles();
            for (File file : fileList) {
                if (file.getName().endsWith(".csv")) {
                    files.add(file);
                }
            }
        }catch (Exception e){e.printStackTrace();}
    }
}
