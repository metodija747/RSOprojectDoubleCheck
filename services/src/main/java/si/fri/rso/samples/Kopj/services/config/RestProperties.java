package si.fri.rso.samples.Kopj.services.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;


@ApplicationScoped
@ConfigBundle("rest-properties")
public class RestProperties {
    @ConfigValue(value = "external-services.service.enabled", watch = true)
    private boolean ServiceEnabled;

    public boolean isServiceEnabled() {
        return ServiceEnabled;
    }

    public void setServiceEnabled(boolean ServiceEnabled) {
        this.ServiceEnabled = ServiceEnabled;
    }

    //ovoj kod e za citanje od konfiguraciski server
    //localhost:8500
}
