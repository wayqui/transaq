package com.wayqui.transaq.api;

import com.wayqui.transaq.api.model.TransactionRequest;
import com.wayqui.transaq.api.model.TransactionStatusRequest;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/transaction")
public interface TransactionController {

    @POST
    @Consumes("application/json")
    public Response createTransaction(TransactionRequest transaction);

    @GET
    @Produces("application/json")
    public Response findTransaction(@QueryParam("account_iban") String account_iban,
                                    @QueryParam("ascending") Boolean ascending);

    @POST
    @Path("/status")
    @Consumes("application/json")
    public Response obtainTransactionStatus(TransactionStatusRequest transactionStatus);
}
