package si.fri.rso.samples.Kopj.services;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import si.fri.rso.samples.Kopj.models.Kopj;
import si.fri.rso.samples.Kopj.models.Payments;

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

@RequestScoped
public class KopjBean {

    @Inject
    private EntityManager em;

    @Inject
    private KopjBean customersBean;


    private Client httpClient;
    private String baseUrl;



    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
        baseUrl = "http://20.127.141.29"; // only for demonstration
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

    @Timeout(value = 2)
    @CircuitBreaker(requestVolumeThreshold = 3)
    @Fallback(fallbackMethod = "getPaymentsFallback")
    public List <Payments> getPayments(String Userid) {

            return httpClient
                    .target(baseUrl + "/pay/v1/payments?where=UserId:EQ:" + Userid).
                    request().get(new GenericType <List < Payments>>() {
                    });

        }
    public Integer getPaymentsFallback(String Userid) {
        return null;
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
