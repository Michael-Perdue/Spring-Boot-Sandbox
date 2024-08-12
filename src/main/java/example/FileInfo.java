package example;

import java.io.File;

public class FileInfo {
    private AuthLevel authLevel;
    private final File file;

    public FileInfo(AuthLevel authLevel,File file){
        this.file = file;
        this.authLevel = authLevel;
    }

    public File getFile(){return file;}
    public int getAuthLevel(){return authLevel.value;}
    public void setAuthLevel(AuthLevel authLevel){
        this.authLevel = authLevel;
    }

    public String getName(){
        return file.getName();
    }

}
