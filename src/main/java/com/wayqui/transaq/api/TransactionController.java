package com.wayqui.transaq.api;

import com.wayqui.transaq.api.model.TransactionRequest;
import com.wayqui.transaq.api.model.TransactionResponse;
import com.wayqui.transaq.api.model.TransactionStatusRequest;
import com.wayqui.transaq.api.model.TransactionStatusResponse;
import com.wayqui.transaq.exception.BusinessException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.ws.rs.core.GenericType;


import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/transaction")
@Api(value = "Transactions API: Service that handles bank transactions from TransaQ system")
public interface TransactionController {

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @ApiOperation(value = "Add a new transaction", notes = "Adds a new transaction")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully added transaction", response = TransactionResponse.class),
            @ApiResponse(code = 400, message = "Invalid request for creating a transaction"),
            @ApiResponse(code = 403, message = "You don't have permissions for this operation"),
            @ApiResponse(code = 500, message = "Internal error")
    })
    public Response createTransaction(TransactionRequest transaction) throws BusinessException;

    @GET
    @Produces("application/json")
    @ApiOperation(value = "Find a list of transactions", notes = "Finds a list of transactions")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Transaction retrieved successfully", response = TransactionResponse.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid request for searching transactions"),
            @ApiResponse(code = 403, message = "You don't have permissions for this operation"),
            @ApiResponse(code = 500, message = "Internal error")
    })
    public Response findTransaction(@QueryParam("account_iban") String account_iban,
                                    @QueryParam("ascending") Boolean ascending);

    @POST
    @Path("/status")
    @Consumes("application/json")
    @Produces("application/json")
    @ApiOperation(value = "Obtain the status of a transaction", notes = "Obtaining the status of a transaction")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Transaction status obtained successfully", response = TransactionStatusResponse.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid request for obtaining the status of a transaction"),
            @ApiResponse(code = 403, message = "You don't have permissions for this operation"),
            @ApiResponse(code = 500, message = "Internal error")
    })
    public Response obtainTransactionStatus(TransactionStatusRequest transactionStatus);
}
