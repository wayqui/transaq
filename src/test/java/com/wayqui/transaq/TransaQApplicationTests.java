package com.wayqui.transaq;

import com.wayqui.transaq.api.model.TransactionRequest;
import com.wayqui.transaq.api.model.TransactionResponse;
import com.wayqui.transaq.api.model.TransactionStatusRequest;
import com.wayqui.transaq.api.model.TransactionStatusResponse;
import com.wayqui.transaq.dao.UserRepository;
import com.wayqui.transaq.dto.TransactionStatus;
import com.wayqui.transaq.entity.AppUser;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransaQApplicationTests {

	private final Logger log = LoggerFactory.getLogger(TransaQApplicationTests.class);

	@Autowired
	private TestRestTemplate restTemplate;

	@Value( "${spring.security.user.name}" )
	private String username;
	@Value( "${spring.security.user.password}" )
	private String password;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder encoder;

	@Before
	public void init() {
		// FIXME not integration tests here but unit tests... i have to mock the response of the services
	}

	@Test
	void testing_POST_Create_Transaction() {
		TransactionRequest request = new TransactionRequest();
		request.setDate(Instant.now());
		request.setAccount_iban("ES9820385778983000760236");
		request.setAmount(2343.4);
		request.setDescription("Lorem ipsun...");
		request.setFee(23.2);
		ResponseEntity<TransactionResponse> response = restTemplate
				.withBasicAuth(username, password)
				.postForEntity("/rest/transaction/", request, TransactionResponse.class);

		log.info("testing_POST_Create_Transaction => Transaction created: "+response.getBody().toString());

		assertEquals(response.getStatusCode(), HttpStatus.CREATED);
		assertNotNull(response.getBody().getReference());
	}

	@Test
	void testing_GET_Find_Transactions() {
		ResponseEntity<TransactionResponse[]> response = restTemplate
				.withBasicAuth(username, password)
				.getForEntity("/rest/transaction?account_iban=ES9820385778983000760236&ascending=false", TransactionResponse[].class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	void testing_POST_Obtain_Transaction_Status() {
		TransactionStatusRequest request = TransactionStatusRequest.builder().reference("dfsdf").channel("INTERNAL").build();

		ResponseEntity<TransactionStatusResponse> response = restTemplate
				.withBasicAuth(username, password)
				.postForEntity("/rest/transaction/status", request, TransactionStatusResponse.class);

		log.info("testing_POST_Obtain_Transaction_Status => Transaction status: "+response.getBody().toString());

		assertEquals(response.getStatusCode(), HttpStatus.OK);
		assertNotNull(response.getBody().getStatus());
		assertEquals(TransactionStatus.INVALID.toString(), response.getBody().getStatus());
	}

	@Test
	void testing_Authenticate_With_a_Created_User() {
		AppUser unregisteredAppUser = new AppUser();
		unregisteredAppUser.setUsername("joselobm");
		unregisteredAppUser.setPassword(encoder.encode("testingpwd"));
		AppUser registeredAppUser = userRepository.save(unregisteredAppUser);
		assertNotNull(registeredAppUser.getId());
		log.info("User registered is: "+registeredAppUser.toString());
		log.info("User registered is: "+registeredAppUser.toString());

		ResponseEntity<TransactionResponse[]> responseGet = restTemplate
				.withBasicAuth("joselobm", "testingpwd")
				.getForEntity("/rest/transaction?account_iban=ES9820385778983000760236&ascending=false", TransactionResponse[].class);

		assertEquals(HttpStatus.OK, responseGet.getStatusCode());

		TransactionStatusRequest request = TransactionStatusRequest.builder().reference("dfsdf").channel("INTERNAL").build();

		ResponseEntity<TransactionStatusResponse> response = restTemplate
				.withBasicAuth("joselobm", "testingpwd")
				.postForEntity("/rest/transaction/status", request, TransactionStatusResponse.class);

		log.info("testing_POST_Obtain_Transaction_Status => Transaction status: "+response.getBody().toString());

		assertEquals(response.getStatusCode(), HttpStatus.OK);
		assertNotNull(response.getBody().getStatus());
		assertEquals(TransactionStatus.INVALID.toString(), response.getBody().getStatus());
	}
}
