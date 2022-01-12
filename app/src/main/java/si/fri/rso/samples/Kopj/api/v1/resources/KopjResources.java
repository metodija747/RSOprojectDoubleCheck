package si.fri.rso.samples.Kopj.api.v1.resources;


import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import si.fri.rso.samples.Kopj.models.Kopj;
import si.fri.rso.samples.Kopj.models.Payment;
import si.fri.rso.samples.Kopj.services.KopjBean;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.security.KeyPair;
import java.util.List;


@Log
@RequestScoped
@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class KopjResources {

    @Inject
    private KopjBean customersBean;

    @Context
    protected UriInfo uriInfo;



    @Operation(description = "Get for all customers information.", summary = "Get all customer details")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of customers and their details",
                    content = @Content(schema = @Schema(implementation = Kopj.class, type = SchemaType.ARRAY))
            )})
    @GET
    @Metered
    public Response getCustomers() {

        List<Kopj> customers = customersBean.getCustomers();

        return Response.ok(customers).build();
    }

    @Operation(description = "Get number of payments for specific customer.", summary = "Get number of payments")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Number of payments for customer")

            })
    @GET
    @Log
    @Path("no/{customerId}")
    @Metered
    public Response getNumbers(@Parameter(description = "Customer ID!.", required = true)
            @PathParam("customerId") String customerId) {

        List <Payment> pay = customersBean.getNoPayments(customerId);

        return Response.ok(pay.size()+1).build();
    }

    @Operation(description = "Get customer's information.", summary = "Get details for specific customer")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Specific user's details",
                    content = @Content(
                            schema = @Schema(implementation = Kopj.class))
            ),
            @APIResponse(responseCode = "404", description = "Resource not found .")
    })
    @GET
    @Path("/{customerId}")
    @Log
    @Timed
    public Response getCustomer(@Parameter(description = "Customer ID.", required = true)
            @PathParam("customerId") String customerId) {

        Kopj customer = customersBean.getCustomer(customerId);

        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(customer).build();
    }


    @Operation(description = "Add new customer.", summary = "Add customer")
    @APIResponses({
            @APIResponse(responseCode = "201",
                    description = "Customer successfully added."
            ),
            @APIResponse(responseCode = "400", description = "Bas request .")
    })
    @POST
    @Log
    @Timed
    public Response createCustomer(@RequestBody(
            description = "DTO object with customer details.",
            required = true, content = @Content(
            schema = @Schema(implementation = Kopj.class)))Kopj customer) {
        if ((customer.getFirstName() == null || customer.getFirstName().isEmpty()) || (customer.getLastName() == null
                || customer.getLastName().isEmpty())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            customer = customersBean.createCustomer(customer);
        }

        if (customer.getId() != null) {
            return Response.status(Response.Status.CREATED).entity(customer).build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity(customer).build();
        }
    }


    @Operation(description = "Update customer details.", summary = "Update customer's info")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Details successfully updated."
            ),
            @APIResponse(responseCode = "400", description = "Bas request."),
            @APIResponse(responseCode = "304", description = "Resource not modified.")
    })
    @PUT
    @Log
    @Path("{customerId}")
    public Response putZavarovanec(@Parameter(description = "Customer ID.", required = true)
            @PathParam("customerId") String customerId, Kopj customer) {

        customer = customersBean.putCustomer(customerId, customer);

        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            if (customer.getId() != null)
                return Response.status(Response.Status.OK).entity(customer).build();
            else
                return Response.status(Response.Status.NOT_MODIFIED).build();
        }
    }


    @Operation(description = "Delete customer.", summary = "Delete customer")
    @APIResponses({
            @APIResponse(
                    responseCode = "410",
                    description = "Customer successfully deleted."
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Not found."
            )
    })
    @DELETE
    @Path("{customerId}")
    @Log
    public Response deleteCustomer(@Parameter(description = "Customer ID.", required = true)
            @PathParam("customerId") String customerId) {

        boolean deleted = customersBean.deleteCustomer(customerId);

        if (deleted) {
            return Response.status(Response.Status.GONE).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }




}
