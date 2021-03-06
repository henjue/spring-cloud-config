/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.config.monitor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * @author henjue
 *
 */
public class GogsPropertyPathNotificationExtractorTests {

	private GogsPropertyPathNotificationExtractor extractor = new GogsPropertyPathNotificationExtractor();

	private HttpHeaders headers = new HttpHeaders();

	@Test
	public void gogsSample() throws Exception {
		// See https://gogs.io/docs/features/webhook
		Map<String, Object> value = new ObjectMapper().readValue(
				new ClassPathResource("gogs.json").getInputStream(),
				new TypeReference<Map<String, Object>>() {
				});
		this.headers.set("X-Gogs-Event", "push");
		PropertyPathNotification extracted = this.extractor.extract(this.headers, value);
		assertNotNull(extracted);
		assertEquals("application-dev.yml", extracted.getPaths()[0]);
	}

	@Test
	public void notAPushNotDetected() throws Exception {
		Map<String, Object> value = new ObjectMapper().readValue(
				new ClassPathResource("gogs.json").getInputStream(),
				new TypeReference<Map<String, Object>>() {
				});
		this.headers.set("X-Gogs-Event", "issues");
		PropertyPathNotification extracted = this.extractor.extract(this.headers, value);
		assertNull(extracted);
	}

}
