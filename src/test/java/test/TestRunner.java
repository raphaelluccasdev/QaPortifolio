package test;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.testng.annotations.Test;

@RunWith(Cucumber.class)

@CucumberOptions(
		features = "src/test/java/Feature",
		plugin = "pretty",
		snippets = CucumberOptions.SnippetType.CAMELCASE,
		glue = {"Steps", "Setups"},
		monochrome = true,
		dryRun = false
		)

@Test(priority = 11)
public class TestRunner{

}
