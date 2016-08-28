/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */

package org.apache.logging.log4j.core.appender;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.junit.LoggerContextRule;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import com.google.common.io.Files;

public class CsvJsonParameterLayoutFileAppenderTest {

    private static final String FILE_PATH = "target/CsvJsonParameterLayoutFileAppenderTest.log";

    private static final LoggerContextRule loggerContextRule = new LoggerContextRule("log4j-cvs-json-parameter.xml");

    @Rule
    public RuleChain rule = loggerContextRule.withCleanFilesRule(FILE_PATH);

    @Test
    @Ignore("https://issues.apache.org/jira/browse/LOG4J2-1502")
    public void testNoNulCharacters() throws IOException {
        @SuppressWarnings("resource")
        final LoggerContext loggerContext = loggerContextRule.getLoggerContext();
        final Logger logger = loggerContext.getLogger("com.example");
        final String json = "{\"id\":10,\"name\":\"Alice\"}";
        logger.error("log:", json);
        loggerContext.stop();
        final File file = new File(FILE_PATH);
        final byte[] contents = Files.toByteArray(file);
        int count0s = 0;
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < contents.length; i++) {
            final byte b = contents[i];
            if (b == 0) {
                sb.append(i);
                sb.append(", ");
                count0s++;
            }
        }
        Assert.assertEquals("File contains " + count0s + " 0x00 byte at indices " + sb, 0, count0s);
        final List<String> readLines = Files.readLines(file, Charset.defaultCharset());
        final String actual = readLines.get(0);
        Assert.assertTrue(actual, actual.contains(json));
    }
}
