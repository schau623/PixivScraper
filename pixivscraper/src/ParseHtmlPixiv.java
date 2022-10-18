import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Scanner;

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
    static WebDriver driver;
    static CreateDirectoryPixiv directory;
    private Boolean loginSuccess = false;
    private String referer = "https://pixiv.net";
    Properties prop = new Properties();
    Scanner sc = new Scanner(System.in);

    public ParseHtmlPixiv() throws IOException {
        setup();
        loginWithConfig();
        if(loginSuccess == false) {
            System.out.println("Press any key to exit.");
            sc.nextLine();
            sc.close();
            driver.quit();
            System.exit(0);
        }
        mainLoop();
        sc.close();
        driver.quit();
        //directory = new CreateDirectoryPixiv(id, dir);
    }

    private void mainLoop() throws IOException {
        Boolean input_quit = false;
        String userInput = "";
        while(input_quit != true) {
            System.out.println("Press 1 to download all posts from a Pixiv artist");
            System.out.println("Press 0 to quit the program");
            userInput = sc.nextLine();

            if(Integer.parseInt(userInput) == 1) {
                System.out.println("Enter Pixiv ID:");
                userInput = sc.nextLine();

                String url = "https://www.pixiv.net/users/" + userInput;
                System.out.println(url);
                if(!validateURL(url)) {
                    System.out.println("ERROR: Invalid Pixiv ID");
                }
                else {
                    String defaultDir = System.getProperty("user.home") + "/Downloads";
                    //18385405
                    CreateDirectoryPixiv cdp = new CreateDirectoryPixiv(Integer.parseInt(userInput), defaultDir);
                    //downloadAllPosts(url);
                }

            }
            else if(Integer.parseInt(userInput) == 0) {
                input_quit = true;
            }
            else {
                System.out.println("ERROR: Invalid Command");
            }
        }
        return;
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
    private Boolean login(String username, String password) throws IOException {
        driver.get("https://accounts.pixiv.net/login");

        try {
            System.out.println("Logging in...");
            WebElement wait = new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(By.cssSelector(".sc-bn9ph6-6.degQSE")));
            
            WebElement loginUserE = driver.findElement(By.cssSelector(".sc-bn9ph6-6.degQSE"));// //input[@autocomplete='username']
            loginUserE.sendKeys(username);

            WebElement loginPassE = driver.findElement(By.cssSelector(".sc-bn9ph6-6.hfoSmp"));// //input[@autocomplete='current-password']
            loginPassE.sendKeys(password);

            WebElement loginBtnE = driver.findElement(By.cssSelector(".sc-bdnxRM.jvCTkj.sc-dlnjwi.pKCsX.sc-2o1uwj-7.fguACh.sc-2o1uwj-7.fguACh")); //"//button[text()='Login']"
            loginBtnE.click();

            WebElement root = new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOfElementLocated(By.id("root"))); //verify login
            System.out.println("Login Successful");
            return true;
        }
        catch (Exception e) {
            //driver.quit();
            System.err.println(e);
            System.err.println("ERROR: Login Failed");
            return false;
        }
        
    }

    private void loginWithConfig() throws IOException{
        File configProp = new File(System.getProperty("user.dir") + "/config.properties");
        String username = "";
        String password = "";
        //read from config properties file if exists, otherwise create one and populate it with user creds
        if(!configProp.exists()) {
            OutputStream output = null;
            while(loginSuccess == false) {
                System.out.println("Enter 0 to exit program");
                System.out.println("Enter Username:");
                username = sc.nextLine();

                if(Integer.parseInt(username) == 0) {
                    sc.close();
                    driver.quit();
                    System.exit(0);
                }
                System.out.println("Enter Password:");
                password = sc.nextLine();

                if(Integer.parseInt(password) == 0) {
                    sc.close();
                    driver.quit();
                    System.exit(0);
                }
                loginSuccess = login(username, password);
            }
            try {
                output = new FileOutputStream("config.properties");
                prop.setProperty("username", username);
                prop.setProperty("password", password);
                prop.store(output, null);
        
                } catch (IOException io) {
                    io.printStackTrace();
                } finally {
                    if (output != null) {
                        try {
                            System.out.println("Config File Successfully Created");
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        }
        else {
            InputStream input = null;
            try {
               input = new FileInputStream("config.properties");
                prop.load(input);
                username = prop.getProperty("username");
                password = prop.getProperty("password"); 
                loginSuccess = login(username, password);
                if(loginSuccess == false) {
                    System.out.println("Likely ran into captcha request. If so, try again later");
                    return;
                }
                
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            
        }
        
    }

    private String editURL(String url) {
        if (url.charAt(url.length()-1) == '/') {
            url = url + "illustrations";
        }
        else {
            url = url+ "/illustrations";
        }
        return url;
    }

    private void getArtist() {
        String url = "";
        String dest = "";
        
    }
    
    private void downloadAllPosts(String url, String dest) {

    }

    private void visitPost(String url, String dest) {

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
            driver.quit();
            
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
        String url = "https://www.pixiv.net/users/" + Integer.toString(id);
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
