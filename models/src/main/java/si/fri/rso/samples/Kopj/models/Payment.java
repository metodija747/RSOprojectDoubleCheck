package si.fri.rso.samples.Kopj.models;
import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;
@Entity(name = "payment")

@UuidGenerator(name = "idGenerator")
public class Payment {
    @Id
    @GeneratedValue(generator = "idGenerator")
    private String id;

    @Column(name = "User_id")
    private String UserId;

    @Column(name = "amount")
    private String amount;

    @Column(name = "date")
    private String date;

    @Column(name = "Charge_station")
    private String ChargeStation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getElStationName() {
        return ChargeStation;
    }

    public void setElStationName(String ElStationName) {
        this.ChargeStation = ElStationName;
    }


}
