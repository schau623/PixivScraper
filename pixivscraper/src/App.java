import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;


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
        String userURL = "https://www.pixiv.net/en/users/18385405";
        String test_url = "https://www.pixiv.net/en/artworks/98582396";
        String dir = "C:/Users/Steven/Google Drive/Pixiv";
        String test_dir = "C:/Users/Steven/Google Drive/Pixiv/18385405";
        int id = 18385405;
        ParseHtmlPixiv pixivURL = new ParseHtmlPixiv();
        //test();
        //ParseHtmlPixiv pixivURL = new ParseHtmlPixiv(url, dir);
        
        //ParseHtmlPixiv pixivID = new ParseHtmlPixiv(id, dir);
        //pixivID.downloadPost(test_url, test_dir);
    }

    private static void test(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter username");

        String userName = sc.nextLine();  // Read user input
        System.out.println("Username is: " + userName);  // Output user input
        
        String dir = System.getProperty("user.dir");

        // directory from where the program was launched
        // e.g /home/mkyong/projects/core-java/java-io
        System.out.println(dir);
    }
    
    
}
