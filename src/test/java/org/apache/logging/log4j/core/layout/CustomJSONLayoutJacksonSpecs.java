package org.apache.logging.log4j.core.layout;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.util.KeyValuePair;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.SimpleMessage;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

public class CustomJSONLayoutJacksonSpecs {

    final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
    final DateFormat isoDateFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);

    public static final String LOCATION_INFO = "LocationInfo";
    private static Logger logger = LogManager.getLogger(CustomJSONLayoutJacksonSpecs.class);


    @Test(enabled = false, dataProvider = "dp")
    public void f(Integer n, String s) {
    }

    @DataProvider
    public Object[][] dp() {
        return new Object[][]{
                new Object[]{1, "a"},
                new Object[]{2, "b"},
        };
    }

    @BeforeTest
    public void beforeTest() {
    }

    @AfterTest
    public void afterTest() {
    }


    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void hasTimestampAndVersionInLogMessages() throws Exception {
        Message simpleMessage = new SimpleMessage("Test Message");
        long millis = System.currentTimeMillis();

        Map<String,String>  mdc =     new HashMap<String,String>();
        mdc.put("A","B");//Already some threadcontext

        LogEvent event = new Log4jLogEvent(
                logger.getName(),
                null,
                this.getClass().getCanonicalName(),
                Level.DEBUG,
                simpleMessage,
                null,
                mdc,
                null,
                Thread.currentThread().getName(),
                null,
                millis
                );


        AbstractJacksonLayout layout = CustomJSONLayout.createLayout(
                true, //location
                true, //properties
                true, //complete
                false, //compact
                false, //eventEol
                Charset.defaultCharset(),
                new KeyValuePair[]{new KeyValuePair("Foo", "Bar")}
        );

        String actualJSON = layout.toSerializable(event);
        System.out.println(actualJSON);

        String expectedBasicSimpleTestJSON = "{\"timestamp\":" + "\"" + isoDateFormat.format(new Date(millis)) +"\"," +
                "\"timeMillis\":" + millis + "," +
                "\"thread\":\""+ Thread.currentThread().getName() +"\"," +
                "\"level\":\"DEBUG\"," +
                "\"loggerName\":\"org.apache.logging.log4j.core.layout.CustomJSONLayoutJacksonSpecs\"," +
                "\"message\":\"Test Message\"," +
                "\"endOfBatch\":false," +
                "\"loggerFqcn\":\"org.apache.logging.log4j.core.layout.CustomJSONLayoutJacksonSpecs\","+
                "\"contextMap\":[{\"key\":\"Foo\",\"value\":\"Bar\"},{\"key\":\"A\",\"value\":\"B\"}]}";

        assertThat(actualJSON, sameJSONAs(expectedBasicSimpleTestJSON)
                .allowingExtraUnexpectedFields()
                .allowingAnyArrayOrdering());

    }

    @Test
    public void hasLogMessageAsItIs() throws Exception {
        Message simpleMessage = new SimpleMessage("key1=value1,key2=value2");

        long millis = System.currentTimeMillis();

        Map<String,String>  mdc =     new HashMap<String,String>();
        mdc.put("A","B");//Already some threadcontext

        LogEvent event = new Log4jLogEvent(
                logger.getName(),
                null,
                this.getClass().getCanonicalName(),
                Level.DEBUG,
                simpleMessage,
                null,
                mdc,
                null,
                Thread.currentThread().getName(),
                null,
                System.currentTimeMillis()
        );

        AbstractJacksonLayout layout = CustomJSONLayout.createLayout(
                true, //location
                true, //properties
                true, //complete
                false, //compact
                false, //eventEol
                Charset.defaultCharset(),
                new KeyValuePair[]{new KeyValuePair("Foo", "Bar")}
        );

        String actualJSON = layout.toSerializable(event);
        System.out.println(actualJSON);
        assertThat(actualJSON, sameJSONAs("{\"timestamp\":" + "\"" + isoDateFormat.format(new Date(millis)) +"\"," +
                "\"timeMillis\":" + millis + "," +
                "\"thread\":\""+ Thread.currentThread().getName() +"\"," +
                "\"level\":\"DEBUG\"," +
                "\"loggerName\":\"org.apache.logging.log4j.core.layout.CustomJSONLayoutJacksonSpecs\"," +
                "\"message\":\"key1=value1,key2=value2\"," +
                "\"endOfBatch\":false," +
                "\"loggerFqcn\":\"org.apache.logging.log4j.core.layout.CustomJSONLayoutJacksonSpecs\","+
                "\"contextMap\":[{\"key\":\"Foo\",\"value\":\"Bar\"},{\"key\":\"A\",\"value\":\"B\"}]}")
                .allowingExtraUnexpectedFields()
                .allowingAnyArrayOrdering());
    }
}

