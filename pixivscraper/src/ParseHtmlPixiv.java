import java.io.IOException;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;

public class ParseHtmlPixiv {
    private String dir_ = "";
    private String url_ = "";
    private int id_ = -1;
    static WebDriver driver;

    public ParseHtmlPixiv(int id, String dir) throws IOException {
        this.id_ = id;
        this.dir_ = dir;
        setup();
        if(validateID(id)) {
            CreateDirectoryPixiv cdp = new CreateDirectoryPixiv(id, dir);
        }
        else {
            System.out.println("ERROR: Invalid Id");
        }
    }

    public ParseHtmlPixiv(String url, String dir) throws IOException {
        this.url_ = url;
        this.dir_ = dir;
        setup();
        if(validateURL(url)) {
            id_ = extractId(url);
            CreateDirectoryPixiv cdp = new CreateDirectoryPixiv(id_, dir);
        }
        else {
            System.out.println("ERROR: Invalid URL");
        }
    }

    public String getDir() {
        return this.dir_; 
    }

    public String getUrl() {
        return this.url_;
    }

    public int getId() {
        return this.id_;
    }

    private void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
    }

    private void editURL() {
        if (this.url_.charAt(this.url_.length()-1) == '/') {
            this.url_ = this.url_ + "artworks";
        }
        else {
            this.url_ = this.url_ + "/artworks";
        }
    }

    private void visitURL(String url) {

    }

    private void downloadImg(String url){

    }

    /*
     * Get id from pixiv user url
     */
    private int extractId (String url) {
        StringBuilder id = new StringBuilder();
        char prevChar = url.charAt(0);
        for(int i = 0; i < url.length(); i++) {
            if(url.charAt(i) == '/' && Character.isDigit(prevChar)) { //reached end of id section
                break;
            }
            else if(Character.isDigit(url.charAt(i))) { //iterating the id section of url
                id.append(url.charAt(i));
                prevChar = url.charAt(i);
            }
            
        }
        return Integer.parseInt(id.toString());
    }

    private Boolean validateURL(String url) throws IOException {
        if(url.contains("pixiv") && url.contains("users")) {
            try {
                driver.get(url);
                driver.quit();
                System.out.println("URL successfully validated");
                return true;
            } catch (Exception e) {
                System.err.println("ERROR: URL validate failed");
                return false;
            } 
        }
        else {
            return false;
        }
        
    }

    private Boolean validateID(int id) throws IOException{
        String url = "https://www.pixiv.net/en/users/" + Integer.toString(id);
        System.out.println(url);
        try {
            driver.get(url);
            WebElement username = driver.findElement(By.xpath("//h1[@class='sc-1bcui9t-5 ibhMns']"));
            WebElement wait = new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(username));
            driver.quit();
            System.out.println("ID successfully validated");
            return true;
        }
        catch(Exception e) {
            System.err.println("ERROR: ID validate failed");
            driver.quit();
            return false;
        }
    }
}
