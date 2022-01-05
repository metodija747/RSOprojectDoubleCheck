package si.fri.rso.samples.Kopj.api.v1;

import com.kumuluz.ee.discovery.annotations.RegisterService;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@OpenAPIDefinition(info = @Info(title = "Customer details API1", version = "v1", contact = @Contact(email = "mb8696@student.uni-lj.si"), license = @License(name="dev"), description = "API for managing customers."), servers = @Server(url ="http://20.127.141.29/user"))
@RegisterService
@ApplicationPath("/v1")
public class KopjApplication extends Application{

}
