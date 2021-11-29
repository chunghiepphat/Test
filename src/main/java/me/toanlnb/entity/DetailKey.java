package me.toanlnb.entity;

import java.io.Serializable;

public class DetailKey implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Bill bill;
	private Ticket ticket;
	
	public DetailKey() {
	}
	
	public DetailKey(Bill bill, Ticket ticket) {
		super();
		this.bill = bill;
		this.ticket = ticket;
	}

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
