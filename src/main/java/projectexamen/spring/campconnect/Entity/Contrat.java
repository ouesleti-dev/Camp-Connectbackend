package projectexamen.spring.campconnect.Entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Contrat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contractId;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    private Double commission;

    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    @ManyToOne
    @JoinColumn(name = "offer_id")
    private offer offer;

    public Contrat() {}

    public Contrat(Long contractId, Date startDate, Date endDate, Double commission, ContractStatus status, offer offer) {
        this.contractId = contractId; this.startDate = startDate; this.endDate = endDate;
        this.commission = commission; this.status = status; this.offer = offer;
    }

    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public Double getCommission() { return commission; }
    public void setCommission(Double commission) { this.commission = commission; }
    public ContractStatus getStatus() { return status; }
    public void setStatus(ContractStatus status) { this.status = status; }
    public offer getOffer() { return offer; }
    public void setOffer(offer offer) { this.offer = offer; }
}