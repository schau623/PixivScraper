import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.time.Duration;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;

public class ParseHtmlPixiv {
    private String dir_ = "";
    private String url_ = "";
    private int id_ = -1;
    static WebDriver driver;
    static CreateDirectoryPixiv directory;
    private String referer = "https://pixiv.net";

    public ParseHtmlPixiv(int id, String dir) throws IOException {
        this.id_ = id;
        this.dir_ = dir;
        setup();
        if(validateID(id)) {
            directory = new CreateDirectoryPixiv(id, dir);
            login();
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
            login();
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
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--headless"); //disable browser window
        driver = new ChromeDriver(options);
        
        //WebDriverManager.firefoxdriver().setup();
        //driver = new FirefoxDriver();

    }
    //login using username and password, then saves cookie info for easy auth in future logins
    private Boolean login() throws IOException {
        driver.get("https://accounts.pixiv.net/login");
        /* BufferedReader usernameReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader passwordReader = new BufferedReader(new InputStreamReader(System.in)); */

        String username = "";
        String password = "";

        try {
            WebElement wait = new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@autocomplete='username']")));
            
            WebElement loginUserE = driver.findElement(By.xpath("//input[@autocomplete='username']"));//.sc-bn9ph6-6.degQSE
            loginUserE.sendKeys(username);

            WebElement loginPassE = driver.findElement(By.xpath("//input[@autocomplete='current-password']"));//.sc-bn9ph6-6.hfoSmp
            loginPassE.sendKeys(password);

            WebElement loginBtnE = driver.findElement(By.xpath("//button[text()='Login']")); //.sc-bdnxRM.jvCTkj.sc-dlnjwi.pKCsX.sc-2o1uwj-7.fguACh.sc-2o1uwj-7.fguACh
            loginBtnE.click();

            WebElement root = new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(By.id("root"))); //verify login
            System.out.println("Login Successful");
        }
        catch (Exception e) {
            driver.quit();
            System.err.println("ERROR: Login Failed");
            throw(e);
        }
        return false;
    }

    private void editURL() {
        if (this.url_.charAt(this.url_.length()-1) == '/') {
            this.url_ = this.url_ + "illustrations";
        }
        else {
            this.url_ = this.url_ + "/illustrations";
        }
    }

    private void visitURL(String url) {

    }

    public void downloadPost(String url, String dest) {
        driver.get(url);
        try {
            //WebElement wait = new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".sc-1qpw8k9-3.eFhoug.gtm-expand-full-size-illust")));
            WebElement wait = new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(By.id("root")));
            List<WebElement> findBtn = driver.findElements(By.cssSelector(".sc-emr523-0.guczbC"));
            if(findBtn.size() == 0) {
                System.out.println("Only one image in post");
                WebElement img = driver.findElement(By.cssSelector(".sc-1qpw8k9-3.eFhoug.gtm-expand-full-size-illust"));
                String imgSrc = img.getAttribute("href");
                downloadImg(imgSrc, dest);
            }
            else {
                System.out.println("Multiple images in post");
                WebElement seeAll = driver.findElement((By.cssSelector(".sc-emr523-0.guczbC")));
                seeAll.click();
                wait = new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".sc-1qpw8k9-0.eXiEBZ")));
                List<WebElement> imgs = driver.findElements(By.xpath("//*[@class='sc-1qpw8k9-3 eFhoug gtm-expand-full-size-illust']"));
                //System.out.println(imgs.size());
                for(WebElement img : imgs) {
                    //System.out.println(img.getTagName());
                    String imgSrc = img.getAttribute("href");
                    downloadImg(imgSrc, dest);
                    //System.out.println(imgSrc);
                } 
            }
            
            //String imgSrc = img.getAttribute("href");
            //downloadImg(imgSrc, dest);
            /* WebElement postName = driver.findElement(By.cssSelector(".sc-1u8nu73-3.lfwBiP"));
            String name = postName.getText();
            String fileName = Integer.toString(id_) + "-" + name;
            //String userAgent = "";
            if (driver instanceof JavascriptExecutor) {
                userAgent=(String) ((JavascriptExecutor)driver).executeScript("return navigator.userAgent");
            } else {
                throw new IllegalStateException("This driver does not support JavaScript!");
            } */
            //System.out.println("GET");
        }
        catch (Exception e) {
            driver.quit();
            System.err.println(e);
        }
    }

    private void downloadImg(String imgSrc, String dest) throws IOException{
        //file name structure: "artist id-post name".png
        String fileName = "";
        Boolean foundSlash = false;
        int i = imgSrc.length()-1;
        while(!foundSlash) {
            if(imgSrc.charAt(i) == '/') {
                foundSlash = true;
                break;
            }
            else {
                fileName = imgSrc.charAt(i) + fileName;
                i--;
            }
        }
        HttpClient client = HttpClientBuilder.create().build();
        //System.out.println(client);
        HttpGet get = new HttpGet(imgSrc);
        get.setHeader("Referer",referer);
        ClassicHttpResponse  responseGet = (ClassicHttpResponse) client.execute(get);
        //System.out.println(responseGet);
        if(responseGet.getCode() == 200) {
            HttpEntity entity = responseGet.getEntity();
            if(entity != null) {
                System.out.println("Downloading Image");
                InputStream is = entity.getContent();
                FileOutputStream fos = new FileOutputStream(new File(dest + "/" + fileName));
                int inByte;
                while((inByte = is.read()) != -1)
                    fos.write(inByte);
                is.close();
                fos.close();
                return;
            }
            else {
                System.out.println("Response failed");
            }
        }
        else {
            System.out.println("Download Auth Failed");
        }
        //System.out.println(responseGet.getCode());
        //System.out.println(userAgent);
        //driver.quit();
        
        //BufferedImage saveImg = ImageIO.read(imgURL.openStream());
        //ImageIO.write(saveImg, "png", new File(dest + "/" + fileName + ".png"));       
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
                //driver.quit();
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
            //driver.quit();
            System.out.println("ID successfully validated");
            return true;
        }
        catch(Exception e) {
            System.err.println("ERROR: ID validate failed");
            driver.quit();
            return false;
        }
    }
    //Stores cookies in a file for future login use
    private void saveCookies() throws IOException{
        Set<Cookie> allcookies = driver.manage().getCookies();
        //allcookies.stream().filter(c -> c.isHttpOnly()).forEach(System.out::println);
        //System.out.println(allcookies);
    }
}
