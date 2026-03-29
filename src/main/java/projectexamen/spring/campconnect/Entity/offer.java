package projectexamen.spring.campconnect.Entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long offerId;

    private String title;
    private String description;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    private Double price;

    @Enumerated(EnumType.STRING)
    private OfferStatus status;

    @OneToMany(mappedBy = "offer")
    private List<Contrat> contracts;

    public offer() {}

    public offer(Long offerId, String title, String description, Date startDate, Date endDate, Double price, OfferStatus status, List<Contrat> contracts) {
        this.offerId = offerId; this.title = title; this.description = description;
        this.startDate = startDate; this.endDate = endDate; this.price = price;
        this.status = status; this.contracts = contracts;
    }

    public Long getOfferId() { return offerId; }
    public void setOfferId(Long offerId) { this.offerId = offerId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public OfferStatus getStatus() { return status; }
    public void setStatus(OfferStatus status) { this.status = status; }
    public List<Contrat> getContracts() { return contracts; }
    public void setContracts(List<Contrat> contracts) { this.contracts = contracts; }
}