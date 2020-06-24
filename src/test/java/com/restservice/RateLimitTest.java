package com.restservice;

import static com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.support.RateLimitConstants.HEADER_LIMIT;
import static com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.support.RateLimitConstants.HEADER_QUOTA;
import static com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.support.RateLimitConstants.HEADER_REMAINING;
import static com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.support.RateLimitConstants.HEADER_REMAINING_QUOTA;
import static com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.support.RateLimitConstants.HEADER_RESET;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

import java.util.concurrent.TimeUnit;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RateLimitTest {

    private static final String PATH = "/products";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void requestNotExceedingAndExceedingCapacity() {
        //test not exceeding capacity
        ResponseEntity<String> response = this.restTemplate.withBasicAuth("admin", "admin").getForEntity(PATH, String.class);
        HttpHeaders headers = response.getHeaders();
        String key = "-rate-limit-application_products_127.0.0.1";

        String limit = headers.getFirst(HEADER_LIMIT + key);
        String remaining = headers.getFirst(HEADER_REMAINING + key);
        String reset = headers.getFirst(HEADER_RESET + key);

        assertEquals(limit, "5");
        assertEquals(remaining, "4");
        assertEquals(reset, "60000");

        assertHeaders(headers, key, false, false);
        assertEquals(OK, response.getStatusCode());

        //test exceeding capacity
        for (int i = 0; i < 4; i++) {
            response = this.restTemplate.withBasicAuth("admin", "admin").getForEntity(PATH, String.class);
        }

        headers = response.getHeaders();
        limit = headers.getFirst(HEADER_LIMIT + key);
        remaining = headers.getFirst(HEADER_REMAINING + key);
        reset = headers.getFirst(HEADER_RESET + key);

        assertEquals(limit, "5");
        assertEquals(remaining, "0");
        assertNotEquals(reset, "2000");

        //one more request to exceed limit
        response = this.restTemplate.withBasicAuth("admin", "admin").getForEntity(PATH, String.class);
        assertEquals(TOO_MANY_REQUESTS, response.getStatusCode());

    }

    private void assertHeaders(HttpHeaders headers, String key, boolean nullable, boolean quotaHeaders) {
        String quota = headers.getFirst(HEADER_QUOTA + key);
        String remainingQuota = headers.getFirst(HEADER_REMAINING_QUOTA + key);
        String limit = headers.getFirst(HEADER_LIMIT + key);
        String remaining = headers.getFirst(HEADER_REMAINING + key);
        String reset = headers.getFirst(HEADER_RESET + key);

        if (nullable) {
            if (quotaHeaders) {
                assertNull(quota);
                assertNull(remainingQuota);
            } else {
                assertNull(limit);
                assertNull(remaining);
            }
            assertNull(reset);
        } else {
            if (quotaHeaders) {
                assertNotNull(quota);
                assertNotNull(remainingQuota);
            } else {
                assertNotNull(limit);
                assertNotNull(remaining);
            }
            assertNotNull(reset);
        }
    }


}

