package example;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

    public ArrayList<HashMap<String,String>> getFile(String name){
        try {
            name = name.replaceAll("\\.csv","");
            for (File file : files) {
                String foundFile = file.getName().replaceAll("\\.csv","");
                if (foundFile.equals(name)) {
                    return getFileContent(file);
                }
            }
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

    private ArrayList<HashMap<String,String>> getFileContent(File file){
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
            HashMap<String,String> values = new HashMap<>();
            values.put("ERROR","ERROR READING FILE");
            entries.add(values);
        }
        return entries;
    }

    public boolean uploadFile(MultipartFile file){
        try {
            Files.write(Path.of("src/main/resources/data/" + file.getOriginalFilename()), file.getBytes());
            loadFiles();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
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
