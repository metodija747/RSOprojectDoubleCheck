package si.fri.rso.samples.Kopj.services;

import si.fri.rso.samples.Kopj.models.Kopj;
import si.fri.rso.samples.Kopj.services.config.RestProperties;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import java.util.List;

@RequestScoped
public class KopjBean {



    @Inject
    private RestProperties restProperties;

    @Inject
    private EntityManager em;

    @Inject
    private KopjBean customersBean;


    private Client httpClient;

    public List<Kopj> getCustomers() {

        TypedQuery<Kopj> query = em.createNamedQuery("Customer.getAll", Kopj.class);

        return query.getResultList();
    }

    public Kopj getCustomer(String customerId) {

        Kopj customer = em.find(Kopj.class, customerId);

        if (customer == null) {
            throw new NotFoundException();
        }
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
