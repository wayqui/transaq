package com.wayqui.transaq.unit.api;

public class TransactionControllerTest {

    /*@Mock
    private TransactionService serviceMock;
    
    @Mock
    private KafkaTransactionProducer producerMock;

    @InjectMocks
    private TransactionControllerImpl controller;*/

    /*@Test
    public void testCreateValidTransaction() throws BusinessException {
        // Given
        TransactionRequest request = TransactionRequest.builder().build();

        // When

        TransactionDto dto = TransactionMapper.INSTANCE.requestToDto(request);
        when(serviceMock.createTransaction(dto)).thenReturn(dto);

        Response response = controller.createTransaction(request);

        // Then
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(TransactionMapper.INSTANCE.dtoToResponse(dto), response.getEntity());
        verify(producerMock, times(1)).sendAsyncDefaultTopic(any(TransactionEvent.class));
    }

    @Test
    public void testErrorInvalidTransaction() throws BusinessException {
        // Given
        TransactionRequest request = TransactionRequest.builder().build();


        Response response = controller.createTransaction(request);

        // Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        verify(producerMock, times(0)).sendAsyncDefaultTopic(any(TransactionEvent.class));

    }*/



}