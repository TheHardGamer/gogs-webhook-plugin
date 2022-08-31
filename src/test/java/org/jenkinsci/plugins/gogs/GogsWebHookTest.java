package org.jenkinsci.plugins.gogs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.kohsuke.stapler.RequestImpl;
import org.kohsuke.stapler.ResponseImpl;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GogsWebHookTest {
    final Logger log = LoggerFactory.getLogger(GogsWebHookTest.class);

    @Rule
    public TestRule watcher = new TestWatcher() {
        protected void starting(Description description) {
            GogsWebHookTest.this.log.info("\t **************  Start Test ({})  *******************", description
                    .getMethodName());
        }
    };

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    //
    // Helper methods
    //


    private void performDoIndexTest(StaplerRequest staplerRequest, StaplerResponse staplerResponse, File file) throws IOException {
        PrintWriter printWriter = new PrintWriter(file.getAbsoluteFile(), "UTF-8");
        when(staplerResponse.getWriter()).thenReturn(printWriter);

        GogsWebHook gogsWebHook = new GogsWebHook();

        //Call the method under test
        gogsWebHook.doIndex(staplerRequest, staplerResponse);

        //Save the Jason log file so we can check it
        printWriter.flush();
        printWriter.close();
    }

    private void isExpectedOutput(File uniqueFile, String expectedOutput) throws IOException {
        String output = FileUtils.readFileToString(uniqueFile, "utf-8");
        uniqueFile.delete();
        String completeExpectedOutput = "{\"result\":\"ERROR\",\"message\":\"" + expectedOutput + "\"}";
        assertEquals("Not the expected output file content", completeExpectedOutput, output);
    }


    class MockServletInputStream extends ServletInputStream {
        InputStream inputStream;

        MockServletInputStream(String string) {
            this.inputStream = IOUtils.toInputStream(string);
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }
    }
}
