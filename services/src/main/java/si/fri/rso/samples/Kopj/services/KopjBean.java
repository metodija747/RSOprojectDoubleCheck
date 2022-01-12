package si.fri.rso.samples.Kopj.services;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import si.fri.rso.samples.Kopj.models.Kopj;
import si.fri.rso.samples.Kopj.models.Payment;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriInfo;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequestScoped
public class KopjBean {

    @Inject
    private EntityManager em;

    @Inject
    private KopjBean customersBean;

    private Client httpClient;

    @Inject
    @DiscoverService("Payment")
    private Optional<String> baseUrlPayments;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
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



        return customer;
    }

    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    @CircuitBreaker(requestVolumeThreshold = 3)
    @Fallback(fallbackMethod = "getNumberFallback")
    public List<Payment> getNoPayments(String customerId) {

        return httpClient
                .target("http://20.127.141.29/pay/v1/payments/"+customerId)
                //.target(baseUrlPayments.get() + "/v1/payments/"+customerId)
                .request().get(new GenericType<List<Payment>>(){
                });
    }
    public List<Payment> getNumberFallback(String customerId) {
        System.out.println("Fallback called!");

        return new ArrayList<>();
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
