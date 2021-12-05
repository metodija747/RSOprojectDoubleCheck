package si.fri.rso.samples.Kopj.services;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;

import si.fri.rso.samples.Kopj.models.Kopj;
import si.fri.rso.samples.Kopj.models.Order;
import si.fri.rso.samples.Kopj.services.config.RestProperties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequestScoped
public class KopjBean {



    private Optional<String> baseUrl;

    @Inject
    private RestProperties restProperties;

    @Inject
    private EntityManager em;

    @Inject
    private KopjBean customersBean;


    private Client httpClient;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
        baseUrl = Optional.of("http://20.84.34.152:8080");
    }
    public List<Kopj> getCustomers() {

        TypedQuery<Kopj> query = em.createNamedQuery("Customer.getAll", Kopj.class);

        return query.getResultList();

    }
    public List<Kopj> getCustomersFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, Kopj.class, queryParameters);
    }

    public Kopj getCustomer(String customerId) {

        Kopj customer = em.find(Kopj.class, customerId);

        if (customer == null) {
            throw new NotFoundException();
        }

        //if (restProperties.isOrderServiceEnabled()) {
            //List<Order> orders = customersBean.getOrders(customerId);
           // customer.setOrders(orders);
       // }

        return customer;
    }

    public Kopj createCustomer(Kopj customer) {

        try {
            beginTx();
            em.persist(customer);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return customer;
    }
    public Kopj putCustomer(String customerId, Kopj customer) {

        Kopj c = em.find(Kopj.class, customerId);

        if (c == null) {
            return null;
        }

        try {
            beginTx();
            customer.setId(c.getId());
            customer = em.merge(customer);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return customer;
    }

    public boolean deleteCustomer(String customerId) {

        Kopj customer = em.find(Kopj.class, customerId);

        if (customer != null) {
            try {
                beginTx();
                em.remove(customer);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else
            return false;

        return true;

    }

    public List<Order> getOrders(String customerId) {
        if (baseUrl.isPresent()) {

            try {
                return httpClient
                        .target(baseUrl.get() + "/v1/orders?where=customerId:EQ:" + customerId)
                        .request().get(new GenericType<List<Order>>() {
                        });
            } catch (WebApplicationException | ProcessingException e) {

                throw new InternalServerErrorException(e);
            }
        }

        return new ArrayList<>();



    }




    private void beginTx() {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
    }

    private void commitTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().commit();
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().rollback();
    }

}
