package com.wayqui.transaq.api;

import com.wayqui.transaq.api.model.TransactionRequest;
import com.wayqui.transaq.api.model.TransactionResponse;
import com.wayqui.transaq.api.model.TransactionStatusRequest;
import com.wayqui.transaq.api.model.TransactionStatusResponse;
import com.wayqui.transaq.exception.BusinessException;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/transaction")
@SwaggerDefinition(
        securityDefinition = @SecurityDefinition(
                apiKeyAuthDefinitions = {
                        @ApiKeyAuthDefinition(
                                key = "jwt-token",
                                name = "Authorization",
                                description = "Authorization: Bearer",
                                in = ApiKeyAuthDefinition.ApiKeyLocation.HEADER)
                }
        )
)
@Api("Transactions")
public interface TransactionController {

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @ApiOperation(value = "Add a new transaction",
            notes = "Adds a new transaction",
            authorizations = {
                    @Authorization(value = "jwt-token")
            })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully added transaction", response = TransactionResponse.class),
            @ApiResponse(code = 400, message = "Invalid request for creating a transaction"),
            @ApiResponse(code = 403, message = "You don't have permissions for this operation"),
            @ApiResponse(code = 500, message = "Internal error")
    })
    Response createTransaction(@Valid TransactionRequest transaction) throws BusinessException;

    @GET
    @Produces("application/json")
    @ApiOperation(value = "Find a list of transactions",
            notes = "Finds a list of transactions",
            authorizations = {
                    @Authorization(value = "jwt-token")
            })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Transaction retrieved successfully", response = TransactionResponse.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid request for searching transactions"),
            @ApiResponse(code = 403, message = "You don't have permissions for this operation"),
            @ApiResponse(code = 500, message = "Internal error")
    })
    Response findTransaction(@QueryParam("account_iban") String account_iban,
                             @QueryParam("ascending") Boolean ascending);

    @POST
    @Path("/status")
    @Consumes("application/json")
    @Produces("application/json")
    @ApiOperation(value = "Obtain the status of a transaction",
            notes = "Obtaining the status of a transaction",
            authorizations = {
                    @Authorization(value = "jwt-token")
            })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Transaction status obtained successfully", response = TransactionStatusResponse.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid request for obtaining the status of a transaction"),
            @ApiResponse(code = 403, message = "You don't have permissions for this operation"),
            @ApiResponse(code = 500, message = "Internal error")
    })
    Response obtainTransactionStatus(TransactionStatusRequest transactionStatus);
}
