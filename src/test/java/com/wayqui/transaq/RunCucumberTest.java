package com.wayqui.transaq;


import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty"},
        features = "src/test/resources/com/wayqui/transaq/features",
        glue = "com.wayqui.transaq.steps")
public class RunCucumberTest {
}