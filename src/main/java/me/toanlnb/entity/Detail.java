package me.toanlnb.entity;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Detail")
@IdClass(DetailKey.class)
public class Detail {

	@Id
	@ManyToOne
	@JoinColumn(name = "bill_id", foreignKey = @ForeignKey(name = "fk_bill"))
	private Bill bill;

	@Id
	@OneToOne
	@JoinColumn(name = "ticket_id", foreignKey = @ForeignKey(name = "fk_ticket"))
	private Ticket ticket;

	public Bill getBill() {
		return bill;
	}

	public void setBill(Bill bill) {
		this.bill = bill;
	}

	public Ticket getTicket() {
		return ticket;
	}

	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}
	
	

}
