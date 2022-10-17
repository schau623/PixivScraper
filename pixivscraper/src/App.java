import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;

public class App {
    public static void main(String[] args) throws Exception {
        String url = "";
        String test_url = "";
        String dir = "";
        String test_dir = "";
        String loginUser = "";
        String loginPass = "";
        int id = -1;
        
        //test();
        //ParseHtmlPixiv pixivURL = new ParseHtmlPixiv(url, dir);
        
        ParseHtmlPixiv pixivID = new ParseHtmlPixiv(id, dir);
        pixivID.downloadPost(test_url, test_dir);
    }

    private static void test(){
    }
    
    
}
