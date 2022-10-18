import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
 * Creates a new folder at the specified directory
 * Folder name is based off the Pixiv id found in the url
 * Ex: https://www.pixiv.net/en/users/18385405/artworks
 */

public class CreateDirectoryPixiv {
    private String newFolderDir = "";

    public CreateDirectoryPixiv(int id, String dir) throws IOException {
        createFolder(dir, id); 
    }

    public String getNewFolderDir() {
        return this.newFolderDir;
    }

    private void createFolder (String dir, int id) {
        if(dir.charAt(dir.length()-1) != '/') { //add "/" to end of input directory to correctly create new folder
                dir = dir + "/PixivScraper/";
            }
            dir = dir + Integer.toString(id);
            this.newFolderDir = dir; 
        try {
            Path path = Paths.get(dir);

            Files.createDirectories(path);

            System.out.println("Directory successfully created");

        } catch (IOException e) {
            System.err.println("Failed to create directory!" + e.getMessage());
            System.exit(0);
        }
    }
}
