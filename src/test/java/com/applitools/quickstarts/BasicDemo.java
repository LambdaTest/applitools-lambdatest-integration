package com.applitools.quickstarts;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.net.MalformedURLException;
import java.net.URL;

import com.applitools.eyes.*;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Eyes;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Runs Applitools test for the demo app https://demo.applitools.com
 */
@RunWith(JUnit4.class)
public class BasicDemo {

	private EyesRunner runner;
	private Eyes eyes;
	private static BatchInfo batch;
	private RemoteWebDriver driver;
	private static String username;
	private static String accesskey;
	private static String gridURL;
	private static String applitoolsApiKey;

	@BeforeClass
	public static void setBatch() {
		// Must be before ALL tests (at Class-level)
		batch = new BatchInfo("Demo batch");
		username = System.getProperty("LT_USERNAME", "Your LambdaTest Username");
		accesskey = System.getProperty("LT_ACCESS_KEY", "Your Lambdatest Access Key");
		applitoolsApiKey = isNullOrEmpty(System.getenv("APPLITOOLS_API_KEY"))
				? "Your Applitools Api Key"
				: System.getenv("APPLITOOLS_API_KEY");
		gridURL = "http://" + username + ":" + accesskey + "@hub.lambdatest.com/wd/hub";
	}

	@Before
	public void beforeEach() throws MalformedURLException {
		// Initialize the Runner for your test.
		runner = new ClassicRunner();

		// Initialize the eyes SDK
		eyes = new Eyes(runner);

		// Raise an error if no API Key has been found.
		if (isNullOrEmpty(applitoolsApiKey)) {
			throw new RuntimeException("No API Key found; Please set environment variable 'APPLITOOLS_API_KEY'.");
		}

		// Set your personal Applitols API Key from your environment variables.
		eyes.setApiKey(applitoolsApiKey);

		// set batch name
		eyes.setBatch(batch);

		// Use Chrome browser
//		driver = new ChromeDriver();

		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("network", true);
		capabilities.setCapability("visual", true);
		capabilities.setCapability("video", true);
		capabilities.setCapability("console", true);

		capabilities.setCapability("browserName", "Chrome");
		capabilities.setCapability("version", "76");
		capabilities.setCapability("platform", "Windows 10");

		capabilities.setCapability("name", "Applitools Sample Test");

		capabilities.setCapability("build", "Applitools Demo");

		driver = new RemoteWebDriver(new URL(gridURL), capabilities);

	}

	@Test
	public void basicTest() {
		// Set AUT's name, test name and viewport size (width X height)
		// We have set it to 800 x 600 to accommodate various screens. Feel free to
		// change it.
		eyes.open(driver, "Demo App", "Smoke Test", new RectangleSize(800, 600));

		// Navigate the browser to the "ACME" demo app.
		driver.get("https://demo.applitools.com");

		// To see visual bugs after the first run, use the commented line below instead.
		// driver.get("https://demo.applitools.com/index_v2.html");

		// Visual checkpoint #1 - Check the login page.
		eyes.checkWindow("Login Window");

		// This will create a test with two test steps.
		driver.findElement(By.id("log-in")).click();

		// Visual checkpoint #2 - Check the app page.
		eyes.checkWindow("App Window");

		// End the test.
		eyes.closeAsync();
	}

	@After
	public void afterEach() {
		// Close the browser.
		driver.quit();

		// If the test was aborted before eyes.close was called, ends the test as
		// aborted.
		eyes.abortIfNotClosed();

		// Wait and collect all test results
		TestResultsSummary allTestResults = runner.getAllTestResults();

		// Print results
		System.out.println(allTestResults);
	}
}
