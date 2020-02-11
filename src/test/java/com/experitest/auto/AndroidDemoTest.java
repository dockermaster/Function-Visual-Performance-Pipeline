package com.experitest.auto;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;

import com.experitest.appium.SeeTestClient;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.BatchInfo;




public class AndroidDemoTest extends BaseTest {
	private Eyes eyes = null;
	protected AndroidDriver<AndroidElement> driver = null;
	protected final String DEFAULT_QUERY = "@os='android'";
	Application app;
	@BeforeMethod
	public void setUp1() throws Exception {
		app = new Application();
		
		eyes = new Eyes();
		eyes.setApiKey("");
	}
	
	static BatchInfo LT = new BatchInfo("Login Test");
	static BatchInfo MP = new BatchInfo("Make Payment");
	
	@Test
	@Parameters({ "deviceQuery" })
	public void login(@Optional(DEFAULT_QUERY) String deviceQuery) throws Exception {
		
		eyes.setBatch(LT);
		
		app.init(deviceQuery, "Login");
		eyes.open(app.getDriver(), "Login Test", "Login Test");
		app.login("company", "company");
		eyes.checkWindow();
		app.logout();
		eyes.close(false);
	}
	@Test
	@Parameters({ "deviceQuery" })
	public void makePayment(@Optional(DEFAULT_QUERY) String deviceQuery) throws Exception {
						
		eyes.setBatch(MP);

		app.init(deviceQuery, "Make Payment");
		
		eyes.open(app.getDriver(), "Demo Test", "Make Payment");

		eyes.checkWindow();
		app.login("company", "company");
		eyes.checkWindow();
		app.makePayment("123456", "guy", 10, "USA");
		eyes.checkWindow();
		app.logout();
		
		eyes.close(false);
	}


	@AfterMethod
	public void tearDown(){
		app.close();
	}
	
}
