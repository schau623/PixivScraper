import java.io.IOException;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;

public class App {
    public static void main(String[] args) throws Exception {
        String url = "-";
        String dir = "-";
        String loginUser = "-";
        String loginPass = "-";
        int id = -1;
        
        ParseHtmlPixiv pixivURL = new ParseHtmlPixiv(url, dir);
        //ParseHtmlPixiv pixivID = new ParseHtmlPixiv(id, dir);
    }

    private static void test(String username, String password, String id) throws IOException {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        driver.get("https://accounts.pixiv.net/login");

        try {
            WebElement wait = new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@autocomplete='username']")));
            System.out.println("HERE");
            WebElement loginUserE = driver.findElement(By.xpath("//input[@autocomplete='username']"));//.sc-bn9ph6-6.degQSE
            loginUserE.sendKeys(username);

            WebElement loginPassE = driver.findElement(By.xpath("//input[@autocomplete='current-password']"));//.sc-bn9ph6-6.hfoSmp
            loginPassE.sendKeys(password);

            WebElement loginBtnE = driver.findElement(By.xpath("//button[text()='Login']")); //.sc-bdnxRM.jvCTkj.sc-dlnjwi.pKCsX.sc-2o1uwj-7.fguACh.sc-2o1uwj-7.fguACh
            loginBtnE.click();
        }
        catch (Exception e) {
            driver.quit();
            System.err.println("ERROR");
            throw(e);
        }
        
    }
    
    
}
