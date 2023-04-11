package com.bynder.lottery;

import com.bynder.lottery.controller.dto.CreateLotteryResponseDto;
import com.bynder.lottery.controller.dto.LotteryResponseDto;
import com.bynder.lottery.controller.dto.ParticipantBallotsResponseDto;
import com.bynder.lottery.controller.dto.RegisterParticipantResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class LotteryApiIntegrationTest extends AbstractIntegrationTest {
    @Value(value = "${local.server.port}")
    private Integer port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void endToEndTest() throws JSONException, JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<LotteryResponseDto[]> initialLotteriesResponse = testRestTemplate.exchange(
                String.format("http://localhost:%s/lottery", port),
                HttpMethod.GET,
                entity,
                LotteryResponseDto[].class
        );
        assertEquals(HttpStatus.OK, initialLotteriesResponse.getStatusCode());
        assertTrue(initialLotteriesResponse.hasBody());
        assertEquals(0, initialLotteriesResponse.getBody().length);

        Long lottery1 = createLottery();
        Long lottery2 = createLottery();
        Long lottery3 = createLottery();

        ResponseEntity<LotteryResponseDto[]> lotteriesResponse = testRestTemplate.exchange(
                String.format("http://localhost:%s/lottery", port),
                HttpMethod.GET,
                entity,
                LotteryResponseDto[].class
        );
        assertEquals(HttpStatus.OK, lotteriesResponse.getStatusCode());
        assertTrue(lotteriesResponse.hasBody());
        assertEquals(3, lotteriesResponse.getBody().length);

        String johnsSsn = "John's SSN";
        String katesSsn = "Kates's SSN";

        Long participantJohnLottery1 = registerParticipant("John", johnsSsn, lottery1);
        Long participantJohnLottery3 = registerParticipant("John", johnsSsn, lottery3);
        Long participantKateLottery2 = registerParticipant("Kate", katesSsn, lottery2);

        ResponseEntity<LotteryResponseDto[]> johnsLotteries = testRestTemplate.exchange(
                String.format("http://localhost:%s/participant/%s/lotteries", port, johnsSsn),
                HttpMethod.GET,
                entity,
                LotteryResponseDto[].class
        );
        assertEquals(HttpStatus.OK, johnsLotteries.getStatusCode());
        assertTrue(johnsLotteries.hasBody());
        assertEquals(2, johnsLotteries.getBody().length);

        ResponseEntity<LotteryResponseDto[]> katesLotteries = testRestTemplate.exchange(
                String.format("http://localhost:%s/participant/%s/lotteries", port, katesSsn),
                HttpMethod.GET,
                entity,
                LotteryResponseDto[].class
        );
        assertEquals(HttpStatus.OK, katesLotteries.getStatusCode());
        assertTrue(katesLotteries.hasBody());
        assertEquals(1, katesLotteries.getBody().length);

        List<String> katesBallotsPost = submit(lottery2, katesSsn, 3);
        assertEquals(3, katesBallotsPost.size());

        ResponseEntity<ParticipantBallotsResponseDto> katesBallotsGet = testRestTemplate.exchange(
                String.format("http://localhost:%s/participant/%s/lottery/%s/ballots", port, katesSsn, lottery2),
                HttpMethod.GET,
                entity,
                ParticipantBallotsResponseDto.class
        );
        assertEquals(HttpStatus.OK, katesBallotsGet.getStatusCode());
        assertTrue(katesBallotsGet.hasBody());
        assertEquals(3, katesBallotsGet.getBody().getBallots().size());

        finishLottery(lottery2);

        ResponseEntity<LotteryResponseDto[]> katesLotteriesAfterFinish = testRestTemplate.exchange(
                String.format("http://localhost:%s/participant/%s/lotteries", port, katesSsn),
                HttpMethod.GET,
                entity,
                LotteryResponseDto[].class
        );
        assertEquals(HttpStatus.OK, katesLotteriesAfterFinish.getStatusCode());
        assertTrue(katesLotteriesAfterFinish.hasBody());
        assertEquals(0, katesLotteriesAfterFinish.getBody().length);
    }

    private Long createLottery() throws JSONException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject createLotteryRequestJsonObject = new JSONObject();
        createLotteryRequestJsonObject.put("name", "win-win lottery");
        createLotteryRequestJsonObject.put("award", "applause");
        createLotteryRequestJsonObject.put("ballotPrice", 1);
        createLotteryRequestJsonObject.put("ballotUnit", "DOLLAR");
        HttpEntity<String> request = new HttpEntity<>(createLotteryRequestJsonObject.toString(), headers);

        ResponseEntity<CreateLotteryResponseDto> response = testRestTemplate.exchange(
                String.format("http://localhost:%s/lottery", port),
                HttpMethod.POST,
                request,
                CreateLotteryResponseDto.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.hasBody());

        return response.getBody().getId();
    }

    private Long registerParticipant(String name, String ssn, Long lotteryId) throws JSONException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject createLotteryRequestJsonObject = new JSONObject();
        createLotteryRequestJsonObject.put("name", name);
        createLotteryRequestJsonObject.put("ssn", ssn);
        createLotteryRequestJsonObject.put("lotteryId", lotteryId);
        HttpEntity<String> request = new HttpEntity<>(createLotteryRequestJsonObject.toString(), headers);

        ResponseEntity<RegisterParticipantResponseDto> response = testRestTemplate.exchange(
                String.format("http://localhost:%s/participant/register", port),
                HttpMethod.POST,
                request,
                RegisterParticipantResponseDto.class
        );

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertTrue(response.hasBody());

        return response.getBody().getId();
    }

    private List<String> submit(Long lotteryId, String ssn, Integer numberOfBallots) throws JsonProcessingException, JSONException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject createLotteryRequestJsonObject = new JSONObject();
        createLotteryRequestJsonObject.put("lotteryId", lotteryId);
        createLotteryRequestJsonObject.put("numberOfBallots", numberOfBallots);
        HttpEntity<String> request = new HttpEntity<>(createLotteryRequestJsonObject.toString(), headers);

        ResponseEntity<ParticipantBallotsResponseDto> response = testRestTemplate.exchange(
                String.format("http://localhost:%s/participant/%s/submit", port, ssn),
                HttpMethod.POST,
                request,
                ParticipantBallotsResponseDto.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.hasBody());

        return response.getBody().getBallots();
    }

    private void finishLottery(Long lotteryId) throws JsonProcessingException {
        RestTemplate patchRestTemplate = testRestTemplate.getRestTemplate();
        patchRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject createLotteryRequestJsonObject = new JSONObject();
        HttpEntity<String> request = new HttpEntity<>(createLotteryRequestJsonObject.toString(), headers);

        ResponseEntity<String> response = patchRestTemplate.exchange(
                String.format("http://localhost:%s/lottery/%s/finish?_method=patch", port, lotteryId),
                HttpMethod.PATCH,
                request,
                String.class
        );

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertTrue(response.hasBody());
    }
}
