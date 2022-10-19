
# Pixiv Scraper

## Description

Image scraper program that automatically downloads images from Pixiv, a popular online community site for artists. Requires a Pixiv account to utilize. <br>

## How to Use
<ul>
<li>
Download PixivScraper.jar
</li>
<li>
Open command line tool and navigate to .jar file directory
</li>
<li>
In command line, type: </li>
</ul>

```
java -jar PixivScraper.jar
```
<br>
You can set a custom download directory in the config.properties file by adding a new property called "folderPath".
<br>
<br>
Example:

```
folderPath=C:/User/[User Name]/[Your Path Here]
```
<ul>
<li>
Note: replace any "\" with "/".
</li>
</ul>

## Demo <br>


https://user-images.githubusercontent.com/61481240/196570053-cddde099-a191-42a4-b32b-1bda568abb1e.mp4



## Features <br>
<ul>
<li>
User login with username and password <br>
</li>
<li>
Saves user login credentials in a properties file on local storage for automatic login <br>
</li>
<li>
Downloads all posts (saved as image files) from a specified Pixiv account using their Pixiv id <br>
</li>
</ul>

## Notes <br>
<ul>
<li>
Sometimes attempts to login will fail. This is likely due to captcha checks. If this occurs, try logging in again or wait a while before attempting another login.
</li>
<li>
Avoid downloading too many images (hundreds - thousands) in one session as making an excessive amount of requests may result in a temporary or even permanent I.P. ban.
</li>
</ul>

## Requirement(s) <br>
<ul>
<li><a href="https://www.java.com/en/")>Java version 8</a></li> <br>
</ul>

## Created Using:
<ul>
<li>
<a href="https://www.java.com/en/")>Java</a> <br>
</li>
<li>
<a href="https://www.selenium.dev/">Selenium</a> (Headless Browser) <br>
</li>
</ul>

