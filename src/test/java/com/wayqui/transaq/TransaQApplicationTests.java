package com.wayqui.transaq;

import com.wayqui.transaq.api.model.TransactionRequest;
import com.wayqui.transaq.api.model.TransactionResponse;
import com.wayqui.transaq.api.model.TransactionStatusRequest;
import com.wayqui.transaq.api.model.TransactionStatusResponse;
import com.wayqui.transaq.dao.UserRepository;
import com.wayqui.transaq.dto.TransactionStatus;
import com.wayqui.transaq.entity.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransaQApplicationTests {

	private final Logger log = LoggerFactory.getLogger(TransaQApplicationTests.class);

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private UserRepository userRepository;

	private String username;
	private String password;

	@BeforeEach
	public void init() {
		// FIXME not integration tests here but unit tests... i have to mock the response of the services
		username = UUID.randomUUID().toString();
		password = UUID.randomUUID().toString();
		AppUser unregisteredAppUser = new AppUser();
		unregisteredAppUser.setUsername(username);
		unregisteredAppUser.setPassword(new BCryptPasswordEncoder().encode(password));

		AppUser registeredAppUser = userRepository.save(unregisteredAppUser);
		assertNotNull(registeredAppUser.getId());
	}

	@Test
	void testing_POST_Create_Transaction() {
		TransactionRequest transactionRequest = new TransactionRequest();
		transactionRequest.setDate(Instant.now());
		transactionRequest.setAccount_iban("ES9820385778983000760236");
		transactionRequest.setAmount(2343.4);
		transactionRequest.setDescription("Lorem ipsun...");
		transactionRequest.setFee(23.2);

		ResponseEntity<TransactionResponse> response = restTemplate
				.withBasicAuth(username, password)
				.postForEntity("/rest/transaction/", transactionRequest, TransactionResponse.class);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNotNull(response.getBody().getReference());

		log.info("testing_POST_Create_Transaction => Transaction created: "+response.getBody().toString());
	}

	@Test
	void testing_GET_Find_Transactions() {
		ResponseEntity<TransactionResponse[]> response = restTemplate
				.withBasicAuth(username, password)
				.getForEntity("/rest/transaction?account_iban=ES9820385778983000760236&ascending=false", TransactionResponse[].class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());

		for(TransactionResponse transacResp : response.getBody()){
			log.info("testing_GET_Find_Transactions => Transaction : "+transacResp);
		}
	}

	@Test
	void testing_POST_Obtain_Transaction_Status() {
		TransactionStatusRequest request = TransactionStatusRequest.builder().reference("dfsdf").channel("INTERNAL").build();

		ResponseEntity<TransactionStatusResponse> response = restTemplate
				.withBasicAuth(username, password)
				.postForEntity("/rest/transaction/status", request, TransactionStatusResponse.class);

		assertEquals(response.getStatusCode(), HttpStatus.OK);
		assertNotNull(response.getBody().getStatus());
		assertEquals(TransactionStatus.INVALID.toString(), response.getBody().getStatus());

		log.info("testing_POST_Obtain_Transaction_Status => Transaction status: "+response.getBody().toString());
	}

	@Test
	void testing_Authenticate_With_a_Created_User() {
		ResponseEntity<TransactionResponse[]> responseGet = restTemplate
				.withBasicAuth(username, password)
				.getForEntity("/rest/transaction?account_iban=ES9820385778983000760236&ascending=false", TransactionResponse[].class);

		log.info("testing_Authenticate_With_a_Created_User => GET Response status: "+responseGet.getStatusCode().toString());

		assertEquals(HttpStatus.OK, responseGet.getStatusCode());

		TransactionStatusRequest request = TransactionStatusRequest.builder().reference("dfsdf").channel("INTERNAL").build();

		ResponseEntity<TransactionStatusResponse> responsePost = restTemplate
				.withBasicAuth(username, password)
				.postForEntity("/rest/transaction/status", request, TransactionStatusResponse.class);

		log.info("testing_Authenticate_With_a_Created_User => POST Response status: "+responsePost.getStatusCode().toString());

		assertEquals(responsePost.getStatusCode(), HttpStatus.OK);
		assertNotNull(responsePost.getBody().getStatus());
		assertEquals(TransactionStatus.INVALID.toString(), responsePost.getBody().getStatus());
	}
}
