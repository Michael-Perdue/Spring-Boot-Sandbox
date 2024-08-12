package example;

import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
public class FileLoaderService {
    private static ArrayList<FileInfo> files = new ArrayList<>();

    @PostConstruct
    private void init(){
        loadFiles();
    }

    public ArrayList<HashMap<String,String>> getFile(String name,AuthLevel authLevel) throws ResponseStatusException {
        name = name.replaceAll("\\.csv","");
        for (FileInfo file : files) {
            String foundFile = file.getName().replaceAll("\\.csv","");
            if (foundFile.equals(name)) {
                if(file.getAuthLevel() <= authLevel.value)
                    return getFileContent(file.getFile());
                else throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    private ArrayList<HashMap<String,String>> getFileContent(File file) throws ResponseStatusException {
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
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return entries;
    }

    public Result uploadFile(MultipartFile file,AuthLevel authLevel){
        try {
            if(!fileExists(file.getOriginalFilename())){
                if(authLevel == AuthLevel.USER)
                    Files.write(Path.of(Route.USER_UPLOAD + file.getOriginalFilename()), file.getBytes());
                if(authLevel == AuthLevel.ADMIN)
                    Files.write(Path.of(Route.ADMIN_UPLOAD + file.getOriginalFilename()), file.getBytes());
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
        for (FileInfo file : files) {
            if (file.getName().equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    private void loadFiles(){
        try {
            File[] fileList = new File(Route.USER_UPLOAD).listFiles();
            for (File file : fileList) {
                if (file.getName().endsWith(".csv")) {
                    files.add(new FileInfo(AuthLevel.USER,file));
                }
            }
            fileList = new File(Route.ADMIN_UPLOAD ).listFiles();
            for (File file : fileList) {
                if (file.getName().endsWith(".csv")) {
                    files.add(new FileInfo(AuthLevel.ADMIN,file));
                }
            }
        }catch (Exception e){e.printStackTrace();}
    }

    public ArrayList<String> getFileNames(AuthLevel authLevel){
        ArrayList<String> names = new ArrayList<>();
        for (FileInfo file : files) {
            if(file.getAuthLevel() <= authLevel.value)
                names.add(file.getName());
        }
        return names;
    }
}
