package com.experitest.auto;

import com.experitest.appium.SeeTestClient;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class Application {
    private AndroidDriver driver;
    private SeeTestClient seetest;
    protected DesiredCapabilities dc = new DesiredCapabilities();
    protected Properties cloudProperties = new Properties();

    public Application(){
    }

    public void init(String deviceQuery, String testName) throws IOException{
        if(driver != null){
            return;
        }
        initCloudProperties();
        String adhocDeviceQuery = System.getenv("deviceQuery");
        if (adhocDeviceQuery != null) {
            System.out.println("[INFO] Redirecting test to the current device.");
            deviceQuery = adhocDeviceQuery;
        }

        dc.setCapability("deviceQuery", deviceQuery);
        dc.setCapability("reportDirectory", "reports");
        dc.setCapability("reportFormat", "xml");
        String accessKey = getProperty("accessKey", cloudProperties, null);
        if (accessKey != null && !accessKey.isEmpty()) {
            dc.setCapability("accessKey", accessKey);
        }
        dc.setCapability(MobileCapabilityType.APP, "cloud:com.experitest.eribank/com.experitest.ExperiBank.LoginActivity");
        dc.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, "com.experitest.eribank");
        dc.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, "com.experitest.ExperiBank.LoginActivity");
        dc.setCapability("appVersion", getProperty("appVersion", null, "1.0"));
        dc.setCapability("testName", testName);
        driver = new AndroidDriver<>(new URL(getProperty("url",cloudProperties, null) + "/wd/hub"), dc);
        seetest = new SeeTestClient(driver);
        seetest.report("Init driver connection to device: " + deviceQuery, true);

    }
    public void login(String user, String password){
        seetest.startStepsGroup("Login");
        driver.findElement(By.xpath("//*[@id='usernameTextField']")).sendKeys(user);
        driver.findElement(By.xpath("//*[@id='passwordTextField']")).sendKeys(password);

        seetest.startPerformanceTransaction("");
        driver.findElement(By.xpath("//*[@id='loginButton']")).click();
        WebDriverWait wait = new WebDriverWait(driver, 20, 100);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@id='pBar']")));
        seetest.endPerformanceTransaction("Login");
        seetest.stopStepsGroup();
    }

    public void logout(){
        seetest.startStepsGroup("Logout");
        driver.findElement(By.xpath("//*[@id='logoutButton']")).click();
        seetest.startPerformanceTransaction("");
        WebDriverWait wait = new WebDriverWait(driver, 20, 100);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='passwordTextField']")));
        seetest.endPerformanceTransaction("Logout");
        seetest.stopStepsGroup();

    }

    public void makePayment(String phone, String name, int amount, String country){
        seetest.startStepsGroup("Make payment");
        driver.findElement(By.xpath("//*[@text='Make Payment']")).click();
        driver.findElement(By.xpath("//*[@id='phoneTextField']")).sendKeys(phone);
        driver.findElement(By.xpath("//*[@id='nameTextField']")).sendKeys(name);
        driver.findElement(By.xpath("//*[@id='amountTextField']")).sendKeys(String.valueOf(amount));
        driver.findElement(By.xpath("//*[@text='Select']")).click();
        driver.findElement(By.xpath("//*[@text='" + country + "']")).click();
        driver.findElement(By.xpath("//*[@text='Send Payment']")).click();
        driver.findElement(By.xpath("//*[@text='Yes']")).click();
        seetest.stopStepsGroup();

    }

    protected String getProperty(String property, Properties props, String defaultValue) {
        if (System.getProperty(property) != null) {
            return System.getProperty(property);
        } else if (System.getenv().containsKey(property)) {
            return System.getenv(property);
        } else if (props != null) {
            return props.getProperty(property);
        }
        return defaultValue;
    }

    private void initCloudProperties() throws FileNotFoundException, IOException {
        FileReader fr = new FileReader("cloud.properties");
        cloudProperties.load(fr);
        fr.close();
    }

    public void close() {
        if(driver != null){
            driver.quit();
        }
    }
}
