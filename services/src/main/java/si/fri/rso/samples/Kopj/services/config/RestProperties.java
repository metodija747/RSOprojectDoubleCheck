package si.fri.rso.samples.Kopj.services.config;

import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

public class RestProperties {
    @ConfigValue(value = "external-services.order-service.enabled", watch = true)
    private boolean orderServiceEnabled;

    public boolean isOrderServiceEnabled() {
        return orderServiceEnabled;
    }

    public void setOrderServiceEnabled(boolean orderServiceEnabled) {
        this.orderServiceEnabled = orderServiceEnabled;
    }

}
